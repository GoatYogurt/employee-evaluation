package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.requests.EvaluationScoreRequestDTO;
import com.vtit.intern.dtos.responses.EvaluationScoreResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.exceptions.ResourceNotFoundException;
import com.vtit.intern.models.CriterionGroup;
import com.vtit.intern.models.EvaluationScore;
import com.vtit.intern.repositories.CriterionRepository;
import com.vtit.intern.repositories.EvaluationRepository;
import com.vtit.intern.repositories.EvaluationScoreRepository;
import com.vtit.intern.services.EvaluationScoreService;
import com.vtit.intern.utils.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluationScoreServiceImpl implements EvaluationScoreService {

    private final EvaluationScoreRepository evaluationScoreRepository;
    private final ModelMapper modelMapper;
    private final EvaluationRepository evaluationRepository;
    private final CriterionRepository criterionRepository;

    @Autowired
    public EvaluationScoreServiceImpl(
            EvaluationScoreRepository evaluationScoreRepository,
            ModelMapper modelMapper,
            CriterionRepository criterionRepository,
            EvaluationRepository evaluationRepository
    ) {
        this.evaluationScoreRepository = evaluationScoreRepository;
        this.modelMapper = modelMapper;
        this.evaluationRepository = evaluationRepository;
        this.criterionRepository = criterionRepository;
    }

    @Override
    public ResponseEntity<ResponseDTO<PageResponse<EvaluationScoreResponseDTO>>> getAll(
            Long criterionId, Long evaluationId, Double minScore, Double maxScore, Pageable pageable) {

        Page<EvaluationScore> page = evaluationScoreRepository.searchEvaluationScore(
                criterionId, evaluationId, minScore, maxScore, pageable);

        List<EvaluationScoreResponseDTO> content = page.getContent().stream().map(es -> {
            EvaluationScoreResponseDTO dto = new EvaluationScoreResponseDTO();
            dto.setCriterionId(es.getCriterion().getId());
            dto.setCriterionName(es.getCriterion().getName());
            dto.setEvaluationId(es.getEvaluation().getId());
            dto.setScore(es.getScore());
            return dto;
        }).toList();

        return ResponseEntity.ok(ResponseUtil.success(new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        )));
    }

    @Override
    public ResponseEntity<ResponseDTO<EvaluationScoreResponseDTO>> getById(Long id) {
        EvaluationScore score = evaluationScoreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EvaluationScore not found"));

        EvaluationScoreResponseDTO dto = new EvaluationScoreResponseDTO();
        dto.setCriterionId(score.getCriterion().getId());
        dto.setCriterionName(score.getCriterion().getName());
        dto.setEvaluationId(score.getEvaluation().getId());
        dto.setScore(score.getScore());

        return ResponseEntity.ok(ResponseUtil.success(dto));
    }

    @Override
    public ResponseEntity<ResponseDTO<EvaluationScoreResponseDTO>> create(EvaluationScoreRequestDTO requestDTO) {
        EvaluationScore evaluationScore = new EvaluationScore();
        evaluationScore.setScore(requestDTO.getScore());
        evaluationScore.setCriterion(
                criterionRepository.findById(requestDTO.getCriterionId())
                        .orElseThrow(() -> new RuntimeException("Criterion not found"))
        );
        evaluationScore.setEvaluation(
                evaluationRepository.findById(requestDTO.getEvaluationId())
                        .orElseThrow(() -> new RuntimeException("Evaluation not found"))
        );

        EvaluationScore saved = evaluationScoreRepository.save(evaluationScore);

        EvaluationScoreResponseDTO dto = new EvaluationScoreResponseDTO();
        dto.setCriterionId(saved.getCriterion().getId());
        dto.setCriterionName(saved.getCriterion().getName());
        dto.setEvaluationId(saved.getEvaluation().getId());
        dto.setScore(saved.getScore());

        return ResponseEntity.ok(ResponseUtil.success(dto));
    }

    @Override
    public ResponseEntity<ResponseDTO<EvaluationScoreResponseDTO>> update(Long id, EvaluationScoreRequestDTO dto) {
        EvaluationScore evaluationScore = evaluationScoreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EvaluationScore not found"));

        evaluationScore.setScore(dto.getScore());
        evaluationScore.setCriterion(
                criterionRepository.findById(dto.getCriterionId())
                        .orElseThrow(() -> new RuntimeException("Criterion not found"))
        );
        evaluationScore.setEvaluation(
                evaluationRepository.findById(dto.getEvaluationId())
                        .orElseThrow(() -> new RuntimeException("Evaluation not found"))
        );

        EvaluationScore updated = evaluationScoreRepository.save(evaluationScore);

        EvaluationScoreResponseDTO responseDTO = new EvaluationScoreResponseDTO();
        responseDTO.setCriterionId(updated.getCriterion().getId());
        responseDTO.setCriterionName(updated.getCriterion().getName());
        responseDTO.setEvaluationId(updated.getEvaluation().getId());
        responseDTO.setScore(updated.getScore());

        return ResponseEntity.ok(ResponseUtil.success(responseDTO));
    }


    @Override
    public void delete(Long id) {
        EvaluationScore existing = evaluationScoreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CriterionGroup not found with id: " + id));
        existing.setDeleted(true);        // xóa mềm
        evaluationScoreRepository.save(existing);
    }
}
