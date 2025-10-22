package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.requests.CriterionRequestDTO;
import com.vtit.intern.dtos.responses.CriterionResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.models.Criterion;
import com.vtit.intern.repositories.CriterionRepository;
import com.vtit.intern.services.CriterionService;
import com.vtit.intern.utils.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.vtit.intern.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class CriterionServiceImpl implements CriterionService {
    @Autowired
    private final CriterionRepository criterionRepository;
    @Autowired
    private final ModelMapper modelMapper;

    public CriterionServiceImpl(CriterionRepository criterionRepository, ModelMapper modelMapper) {
        this.criterionRepository = criterionRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseEntity<ResponseDTO<PageResponse<CriterionResponseDTO>>> getAllCriteria(String name, String description, Double minWeight, Double maxWeight, Pageable pageable) {
        String searchName = name != null ? name.trim() : null;
        String searchDescription = description != null ? description.trim() : null;
        Double searchMinWeight = minWeight != null ? minWeight : 0.0;
        Double searchMaxWeight = maxWeight != null ? maxWeight : Double.MAX_VALUE;

        Page<Criterion> criterionPage = criterionRepository.searchCriteria(
                searchName,
                searchDescription,
                searchMinWeight,
                searchMaxWeight,
                pageable
        );
        List<CriterionResponseDTO> content = criterionPage.getContent().stream()
                .map(criterion -> modelMapper.map(criterion, CriterionResponseDTO.class))
                .toList();

        return ResponseUtil.success(new PageResponse<>(
                content,
                criterionPage.getNumber(),
                criterionPage.getSize(),
                criterionPage.getTotalElements(),
                criterionPage.getTotalPages(),
                criterionPage.isLast()
        ));
    }

    @Override
    public ResponseEntity<ResponseDTO<CriterionResponseDTO>> getById(Long id) {
        return criterionRepository.findByIdAndIsDeletedFalse(id)
                .map(criterion -> ResponseUtil.success(modelMapper.map(criterion, CriterionResponseDTO.class)))
                .orElseGet(() -> ResponseUtil.notFound("Criterion not found or deleted with id: " + id));
    }

    @Override
    public ResponseEntity<ResponseDTO<CriterionResponseDTO>> create(CriterionRequestDTO dto) {
        Criterion criterion = modelMapper.map(dto, Criterion.class);
        criterion.setCreatedAt(java.time.LocalDateTime.now());
        Criterion savedCriterion = criterionRepository.save(criterion);
        return ResponseUtil.created(modelMapper.map(savedCriterion, CriterionResponseDTO.class));
    }

    @Override
    public ResponseEntity<ResponseDTO<Void>> delete(Long id) {
        Optional<Criterion> optionalCriterion = criterionRepository.findByIdAndIsDeletedFalse(id);
        if (optionalCriterion.isEmpty()) {
            return ResponseUtil.notFound("Criterion not found with id: " + id);
        }
        Criterion existing = optionalCriterion.get();
        existing.setDeleted(true);
        criterionRepository.save(existing);
        return ResponseUtil.deleted("Criterion " + existing.getName() + " deleted successfully");
    }

    @Override
    public ResponseEntity<ResponseDTO<CriterionResponseDTO>> patch(Long id, CriterionRequestDTO dto) {
        Optional<Criterion> optCriterion = criterionRepository.findByIdAndIsDeletedFalse(id);
        if (optCriterion.isEmpty()) {
            return ResponseUtil.notFound("Criterion not found with id: " + id);
        }
        Criterion existingCriterion = optCriterion.get();

        // update only the fields that are present in the DTO
        if (dto.getName() != null) {
            existingCriterion.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            existingCriterion.setDescription(dto.getDescription());
        }
        if (dto.getWeight() != null) {
            existingCriterion.setWeight(dto.getWeight());
        }

        Criterion updatedCriterion = criterionRepository.save(existingCriterion);
        return ResponseUtil.success(modelMapper.map(updatedCriterion, CriterionResponseDTO.class));
    }
}
