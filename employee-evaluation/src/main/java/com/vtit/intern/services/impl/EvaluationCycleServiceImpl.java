package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.EvaluationCycleDTO;
import com.vtit.intern.dtos.PageResponse;
import com.vtit.intern.exceptions.ResourceNotFoundException;
import com.vtit.intern.mappers.EvaluationCycleMapper;
import com.vtit.intern.models.EvaluationCycle;
import com.vtit.intern.repositories.EvaluationCycleRepository;
import com.vtit.intern.services.EvaluationCycleService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.vtit.intern.models.EvaluationCycleStatus;

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
    public EvaluationCycleDTO create(EvaluationCycleDTO evaluationCycleDTO) {
        EvaluationCycle evaluationCycle = EvaluationCycleMapper.toEntity(evaluationCycleDTO);
        EvaluationCycle savedEvaluationCycle = evaluationCycleRepository.save(evaluationCycle);
        return EvaluationCycleMapper.toDTO(savedEvaluationCycle);
    }

    @Override
    public EvaluationCycleDTO get(Long id) {
        EvaluationCycle evaluationCycle = evaluationCycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation Cycle not found with id: " + id));
        return EvaluationCycleMapper.toDTO(evaluationCycle);
    }

    @Override
    public EvaluationCycleDTO update(Long id, EvaluationCycleDTO evaluationCycleDTO) {
        EvaluationCycle existingEvaluationCycle = evaluationCycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation Cycle not found with id: " + id));

        EvaluationCycle updatedEvaluationCycle = evaluationCycleRepository.save(existingEvaluationCycle);
        return EvaluationCycleMapper.toDTO(updatedEvaluationCycle);
    }

    @Override
    public void delete(Long id) {
        EvaluationCycle evaluationCycle = evaluationCycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation Cycle not found with id: " + id));
        evaluationCycleRepository.delete(evaluationCycle);
    }

    @Override
    public PageResponse<EvaluationCycleDTO> getAllEvaluationCycles(
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

        List<EvaluationCycleDTO> content = evaluationCyclePage.getContent().stream()
                .map(EvaluationCycleMapper::toDTO)
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
    public PageResponse<EvaluationCycleDTO> getActiveEvaluationCycles(Pageable pageable) {
        Page<EvaluationCycle> evaluationCyclePage = evaluationCycleRepository.searchEvaluationCycles(
                null, // name
                null, // description
                EvaluationCycleStatus.ACTIVE, // status
                null, // startDate
                null, // endDate
                pageable
        );

        List<EvaluationCycleDTO> content = evaluationCyclePage.getContent().stream()
                .map(EvaluationCycleMapper::toDTO)
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
}
