package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.requests.CriterionGroupRequestDTO;
import com.vtit.intern.dtos.responses.CriterionGroupResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.models.CriterionGroup;
import com.vtit.intern.repositories.CriterionGroupRepository;
import com.vtit.intern.services.CriterionGroupService;
import com.vtit.intern.utils.ResponseUtil;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CriterionGroupServiceImpl implements CriterionGroupService {
    private CriterionGroupRepository groupRepository;
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseDTO<PageResponse<CriterionGroupResponseDTO>>> getAllGroups(String name, String description, Pageable pageable) {
        String searchName = name != null ? name.trim() : null;
        String searchDesc = description != null ? description.trim() : null;

        Page<CriterionGroup> pageGroups = groupRepository.searchGroups(searchName, searchDesc, pageable);

        List<CriterionGroupResponseDTO> content = pageGroups.getContent()
                .stream()
                .map(group -> modelMapper.map(group, CriterionGroupResponseDTO.class))
                .toList();

        return ResponseUtil.success(new PageResponse<>(
                content,
                pageGroups.getNumber(),
                pageGroups.getSize(),
                pageGroups.getTotalElements(),
                pageGroups.getTotalPages(),
                pageGroups.isLast()
        ));
    }

    @Override
    public ResponseEntity<ResponseDTO<CriterionGroupResponseDTO>> getById(Long id) {
        return groupRepository.findByIdAndIsDeletedFalse(id)
                .map(criterionGroup -> ResponseUtil.success(modelMapper.map(criterionGroup, CriterionGroupResponseDTO.class)))
                .orElseGet(() -> ResponseUtil.notFound("CriterionGroup not found with id: " + id));
    }

    @Override
    public ResponseEntity<ResponseDTO<CriterionGroupResponseDTO>> create(CriterionGroupRequestDTO dto) {
        CriterionGroup group = modelMapper.map(dto, CriterionGroup.class);
        CriterionGroup saved = groupRepository.save(group);
        return ResponseUtil.created(modelMapper.map(saved, CriterionGroupResponseDTO.class));
    }

    @Override
    public ResponseEntity<ResponseDTO<CriterionGroupResponseDTO>> patch(Long id, CriterionGroupRequestDTO dto) {
        Optional<CriterionGroup> optGroup = groupRepository.findByIdAndIsDeletedFalse(id);
        if (optGroup.isEmpty()) {
            return ResponseUtil.notFound("CriterionGroup not found with id: " + id);
        }
        CriterionGroup existing = optGroup.get();

        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }

        CriterionGroup updated = groupRepository.save(existing);
        return ResponseUtil.success(modelMapper.map(updated, CriterionGroupResponseDTO.class));
    }

    @Override
    public ResponseEntity<ResponseDTO<Void>> delete(Long id) {
        Optional<CriterionGroup> optGroup = groupRepository.findByIdAndIsDeletedFalse(id);
        if (optGroup.isEmpty()) {
            return ResponseUtil.notFound("CriterionGroup not found with id: " + id);
        }
        CriterionGroup existing = optGroup.get();
        existing.setDeleted(true);
        groupRepository.save(existing);
        return ResponseUtil.deleted("Criterion group " + existing.getName() + " deleted successfully");
    }
}
