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
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import com.vtit.intern.exceptions.ResourceNotFoundException;

import java.util.List;

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

        return ResponseEntity.ok(ResponseUtil.success(new PageResponse<>(
                content,
                criterionPage.getNumber(),
                criterionPage.getSize(),
                criterionPage.getTotalElements(),
                criterionPage.getTotalPages(),
                criterionPage.isLast()
        )));
    }

    @Override
    public ResponseEntity<ResponseDTO<CriterionResponseDTO>> getById(Long id) {
        return ResponseEntity.ok(ResponseUtil.success(criterionRepository.findById(id)
                .map(criterion -> modelMapper.map(criterion, CriterionResponseDTO.class))
                .orElseThrow(() -> new ResourceNotFoundException("Criterion not found with id: " + id))));
    }

    @Override
    public ResponseEntity<ResponseDTO<CriterionResponseDTO>> create(CriterionRequestDTO dto) {
        Criterion criterion = modelMapper.map(dto, Criterion.class);
        criterion.setCreatedAt(java.time.LocalDateTime.now());
        Criterion savedCriterion = criterionRepository.save(criterion);
        return ResponseEntity.ok(ResponseUtil.created(modelMapper.map(savedCriterion, CriterionResponseDTO.class)));
    }

//    @Override
//    public CriterionResponseDTO update(Long id, CriterionRequestDTO dto) {
//        if (!criterionRepository.existsById(id)) {
//            throw new ResourceNotFoundException("Cannot update. Criterion not found with id: " + id);
//        }
//
//        Criterion criterion = modelMapper.map(dto, Criterion.class);
//        criterion.setId(id); // Ensure the ID is set for the update
//        Criterion updatedCriterion = criterionRepository.save(criterion);
//        return modelMapper.map(updatedCriterion, CriterionResponseDTO.class);
//    }

    @Override
    public void delete(Long id) {
        if (!criterionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Criterion not found with id: " + id);
        }
        criterionRepository.deleteById(id);
    }

    @Override
    public ResponseEntity<ResponseDTO<CriterionResponseDTO>> patch(Long id, CriterionRequestDTO dto) {
        if (!criterionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot patch. Criterion not found with id: " + id);
        }

        Criterion existingCriterion = criterionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Criterion not found with id: " + id));

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
        return ResponseEntity.ok(ResponseUtil.success(modelMapper.map(updatedCriterion, CriterionResponseDTO.class)));
    }
}
