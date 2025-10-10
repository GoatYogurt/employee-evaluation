package com.vtit.intern.services;

import com.vtit.intern.dtos.requests.CriterionRequestDTO;
import com.vtit.intern.dtos.responses.CriterionResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.utils.ResponseUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface CriterionService {
    ResponseEntity<ResponseDTO<PageResponse<CriterionResponseDTO>>> getAllCriteria(String name, String description, Double minWeight, Double maxWeight, Pageable pageable);
    ResponseEntity<ResponseDTO<CriterionResponseDTO>> getById(Long id);
    ResponseEntity<ResponseDTO<CriterionResponseDTO>> create(CriterionRequestDTO dto);
//    CriterionResponseDTO update(Long id, CriterionRequestDTO dto);
    ResponseEntity<ResponseDTO<Void>> delete(Long id);
    ResponseEntity<ResponseDTO<CriterionResponseDTO>> patch(Long id, CriterionRequestDTO dto);
}
