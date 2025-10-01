package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.requests.CriterionGroupRequestDTO;
import com.vtit.intern.dtos.responses.CriterionGroupResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.exceptions.ResourceNotFoundException;
import com.vtit.intern.models.CriterionGroup;
import com.vtit.intern.repositories.CriterionGroupRepository;
import com.vtit.intern.services.CriterionGroupService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class CriterionGroupServiceImpl implements CriterionGroupService {

    @Autowired
    private CriterionGroupRepository groupRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PageResponse<CriterionGroupResponseDTO> getAllGroups(String name, String description, Pageable pageable) {
        String searchName = name != null ? name.trim() : null;
        String searchDesc = description != null ? description.trim() : null;

        Page<CriterionGroup> pageGroups = groupRepository.searchGroups(searchName, searchDesc, pageable);

        List<CriterionGroupResponseDTO> content = pageGroups.getContent()
                .stream()
                .map(group -> modelMapper.map(group, CriterionGroupResponseDTO.class))
                .toList();

        return new PageResponse<>(
                content,
                pageGroups.getNumber(),
                pageGroups.getSize(),
                pageGroups.getTotalElements(),
                pageGroups.getTotalPages(),
                pageGroups.isLast()
        );
    }

    @Override
    public CriterionGroupResponseDTO getById(Long id) {
        CriterionGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CriterionGroup not found with id: " + id));
        return modelMapper.map(group, CriterionGroupResponseDTO.class);
    }

    @Override
    public CriterionGroupResponseDTO create(CriterionGroupRequestDTO dto) {
        CriterionGroup group = modelMapper.map(dto, CriterionGroup.class);
        CriterionGroup saved = groupRepository.save(group);
        return modelMapper.map(saved, CriterionGroupResponseDTO.class);
    }

    @Override
    public CriterionGroupResponseDTO patch(Long id, CriterionGroupRequestDTO dto) {
        CriterionGroup existing = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CriterionGroup not found with id: " + id));

        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }

        CriterionGroup updated = groupRepository.save(existing);
        return modelMapper.map(updated, CriterionGroupResponseDTO.class);
    }

    @Override
    public void delete(Long id) {
        CriterionGroup existing = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CriterionGroup not found with id: " + id));
        existing.setDeleted(true);        // xóa mềm
        groupRepository.save(existing);
    }
}
