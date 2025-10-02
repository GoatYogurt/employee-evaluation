package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.requests.EvaluationCycleRequestDTO;
import com.vtit.intern.dtos.responses.EvaluationCycleResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.exceptions.ResourceNotFoundException;
import com.vtit.intern.mappers.EvaluationCycleMapper;
import com.vtit.intern.models.CriterionGroup;
import com.vtit.intern.models.EvaluationCycle;
import com.vtit.intern.repositories.EvaluationCycleRepository;
import com.vtit.intern.services.EvaluationCycleService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.vtit.intern.enums.EvaluationCycleStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvaluationCycleServiceImpl implements EvaluationCycleService {
    @Autowired
    private final EvaluationCycleRepository evaluationCycleRepository;

    private EvaluationCycleServiceImpl(EvaluationCycleRepository evaluationCycleRepository) {
        this.evaluationCycleRepository = evaluationCycleRepository;
    }

    @Override
    public EvaluationCycleResponseDTO create(EvaluationCycleRequestDTO dto) {
        EvaluationCycle evaluationCycle = EvaluationCycleMapper.requestToEntity(dto);
        EvaluationCycle savedEvaluationCycle = evaluationCycleRepository.save(evaluationCycle);
        return EvaluationCycleMapper.entityToResponse(savedEvaluationCycle);
    }

    @Override
    public EvaluationCycleResponseDTO get(Long id) {
        EvaluationCycle evaluationCycle = evaluationCycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation Cycle not found with id: " + id));
        return EvaluationCycleMapper.entityToResponse(evaluationCycle);
    }

//    @Override
//    public EvaluationCycleResponseDTO update(Long id, EvaluationCycleRequestDTO dto) {
//        EvaluationCycle existingEvaluationCycle = evaluationCycleRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Evaluation Cycle not found with id: " + id));
//
//        EvaluationCycle updatedEvaluationCycle = evaluationCycleRepository.save(existingEvaluationCycle);
//        return EvaluationCycleMapper.entityToResponse(updatedEvaluationCycle);
//    }

    @Override
    public void delete(Long id) {
        EvaluationCycle existing = evaluationCycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation Cycle not found with id: " + id));
        existing.setDeleted(true);        // xóa mềm
        evaluationCycleRepository.save(existing);
    }

    @Override
    public PageResponse<EvaluationCycleResponseDTO> getAllEvaluationCycles(
            String name,
            String description,
            EvaluationCycleStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        Page<EvaluationCycle> evaluationCyclePage = evaluationCycleRepository.searchEvaluationCycles(
                name != null ? name.trim() : null,
                description != null ? description.trim() : null,
                status,
                startDate,
                endDate,
                pageable
        );

        List<EvaluationCycleResponseDTO> content = evaluationCyclePage.getContent().stream()
                .map(EvaluationCycleMapper::entityToResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                evaluationCyclePage.getNumber(),
                evaluationCyclePage.getSize(),
                evaluationCyclePage.getTotalElements(),
                evaluationCyclePage.getTotalPages(),
                evaluationCyclePage.isLast()
        );
    }

    @Override
    public PageResponse<EvaluationCycleResponseDTO> getActiveEvaluationCycles(Pageable pageable) {
        Page<EvaluationCycle> evaluationCyclePage = evaluationCycleRepository.searchEvaluationCycles(
                null, // name
                null, // description
                EvaluationCycleStatus.ACTIVE, // status
                null, // startDate
                null, // endDate
                pageable
        );

        List<EvaluationCycleResponseDTO> content = evaluationCyclePage.getContent().stream()
                .map(EvaluationCycleMapper::entityToResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                evaluationCyclePage.getNumber(),
                evaluationCyclePage.getSize(),
                evaluationCyclePage.getTotalElements(),
                evaluationCyclePage.getTotalPages(),
                evaluationCyclePage.isLast()
        );
    }
    @Override
    public EvaluationCycleResponseDTO patch(Long id, EvaluationCycleRequestDTO dto) {
        EvaluationCycle existingEvaluationCycle = evaluationCycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation Cycle not found with id: " + id));

        // Update fields selectively based on non-null values in evaluationCycleDTO
        if (dto.getName() != null) {
            existingEvaluationCycle.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            existingEvaluationCycle.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            existingEvaluationCycle.setStatus(dto.getStatus());
        }
        if (dto.getStartDate() != null) {
            existingEvaluationCycle.setStartDate(dto.getStartDate().atStartOfDay());
        }
        if (dto.getEndDate() != null) {
            existingEvaluationCycle.setEndDate(dto.getEndDate().atStartOfDay());
        }

        EvaluationCycle updatedEvaluationCycle = evaluationCycleRepository.save(existingEvaluationCycle);
        return EvaluationCycleMapper.entityToResponse(updatedEvaluationCycle);
    }
}
