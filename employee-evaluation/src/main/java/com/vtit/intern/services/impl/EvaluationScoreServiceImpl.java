package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.requests.CriterionScoreRequestDTO;
import com.vtit.intern.dtos.requests.EvaluationScoreRequestDTO;
import com.vtit.intern.dtos.requests.MultipleEvaluationScoreRequestDTO;
import com.vtit.intern.dtos.responses.EvaluationScoreResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.exceptions.ResourceNotFoundException;
import com.vtit.intern.models.Criterion;
import com.vtit.intern.models.Evaluation;
import com.vtit.intern.models.EvaluationCycle;
import com.vtit.intern.models.EvaluationScore;
import com.vtit.intern.repositories.CriterionRepository;
import com.vtit.intern.repositories.EvaluationRepository;
import com.vtit.intern.repositories.EvaluationScoreRepository;
import com.vtit.intern.services.EvaluationScoreService;
import com.vtit.intern.utils.ResponseUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EvaluationScoreServiceImpl implements EvaluationScoreService {

    private final EvaluationScoreRepository evaluationScoreRepository;
    private final EvaluationRepository evaluationRepository;
    private final CriterionRepository criterionRepository;

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

        return ResponseUtil.success(new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        ));
    }

    @Override
    public ResponseEntity<ResponseDTO<EvaluationScoreResponseDTO>> getById(Long id) {
        Optional<EvaluationScore> optionalEvaluationScore = evaluationScoreRepository.findByIdAndIsDeletedFalse(id);
        if (optionalEvaluationScore.isEmpty()) {
            return ResponseUtil.notFound("EvaluationScore not found with id: " + id);
        }
        EvaluationScore score = optionalEvaluationScore.get();

        EvaluationScoreResponseDTO dto = new EvaluationScoreResponseDTO();
        dto.setId(score.getId());
        dto.setCriterionId(score.getCriterion().getId());
        dto.setCriterionName(score.getCriterion().getName());
        dto.setEvaluationId(score.getEvaluation().getId());
        dto.setScore(score.getScore());

        return ResponseUtil.success(dto);
    }

    @Override
    public ResponseEntity<ResponseDTO<EvaluationScoreResponseDTO>> create(EvaluationScoreRequestDTO requestDTO) {
        EvaluationScore evaluationScore = new EvaluationScore();

        Optional<Criterion> optionalCriterion = criterionRepository.findByIdAndIsDeletedFalse(requestDTO.getCriterionId());
        if (optionalCriterion.isEmpty()) {
            return ResponseUtil.notFound("Criterion not found with id: " + requestDTO.getCriterionId());
        }
        Criterion criterion = optionalCriterion.get();

        Optional<Evaluation> optionalEvaluation = evaluationRepository.findByIdAndIsDeletedFalse(requestDTO.getEvaluationId());
        if (optionalEvaluation.isEmpty()) {
            return ResponseUtil.notFound("Evaluation not found with id: " + requestDTO.getEvaluationId());
        }
        Evaluation evaluation = optionalEvaluation.get();

        evaluationScore.setScore(requestDTO.getScore());
        evaluationScore.setCriterion(criterion);
        evaluationScore.setEvaluation(evaluation);

        double newTotalScore = evaluation.getTotalScore() + requestDTO.getScore() * criterion.getWeight();;
        try {
            evaluation.setTotalScore(newTotalScore);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        EvaluationScore saved = evaluationScoreRepository.save(evaluationScore);
        evaluationRepository.save(evaluation);

        EvaluationScoreResponseDTO dto = new EvaluationScoreResponseDTO();
        dto.setCriterionId(saved.getCriterion().getId());
        dto.setCriterionName(saved.getCriterion().getName());
        dto.setEvaluationId(saved.getEvaluation().getId());
        dto.setScore(saved.getScore());

        return ResponseUtil.created(dto);
    }

    @Override
    public ResponseEntity<ResponseDTO<Void>> createMultiple(MultipleEvaluationScoreRequestDTO dto) {
        Optional<Evaluation> optionalEvaluation = evaluationRepository.findByIdAndIsDeletedFalse(dto.getEvaluationId());
        if (optionalEvaluation.isEmpty()) {
            return ResponseUtil.notFound("Evaluation not found with id: " + dto.getEvaluationId());
        }
        Evaluation evaluation = optionalEvaluation.get();

        double totalScore = 0.0;

        for (CriterionScoreRequestDTO scoreDTO : dto.getScores()) {
            EvaluationScore evaluationScore = new EvaluationScore();
            evaluationScore.setScore(scoreDTO.getScore());
            Optional<Criterion> optionalCriterion = criterionRepository.findByIdAndIsDeletedFalse(scoreDTO.getCriterionId());
            if (optionalCriterion.isEmpty()) {
                return ResponseUtil.notFound("Criterion not found with id: " + scoreDTO.getCriterionId());
            }
            evaluationScore.setCriterion(optionalCriterion.get());
            evaluationScore.setEvaluation(evaluation);

            evaluationScoreRepository.save(evaluationScore);

            double weight = evaluationScore.getCriterion().getWeight();
            totalScore += scoreDTO.getScore() * weight;
        }

        evaluation.setTotalScore(totalScore);
        evaluationRepository.save(evaluation);

        return ResponseUtil.created("Multiple evaluation scores for " + evaluation.getId() + " created successfully");
    }

    @Override
    public ResponseEntity<ResponseDTO<EvaluationScoreResponseDTO>> update(Long id, EvaluationScoreRequestDTO dto) {
        Optional<EvaluationScore> optionalEvaluationScore = evaluationScoreRepository.findByIdAndIsDeletedFalse(id);
        if (optionalEvaluationScore.isEmpty()) {
            return ResponseUtil.notFound("EvaluationScore not found with id: " + id);
        }
        EvaluationScore evaluationScore = optionalEvaluationScore.get();

        evaluationScore.setScore(dto.getScore());

        Optional<Criterion> optionalCriterion = criterionRepository.findByIdAndIsDeletedFalse(dto.getCriterionId());
        if (optionalCriterion.isEmpty()) {
            return ResponseUtil.notFound("Criterion not found with id: " + dto.getCriterionId());
        }
        evaluationScore.setCriterion(optionalCriterion.get());

        Optional<Evaluation> optionalEvaluation = evaluationRepository.findByIdAndIsDeletedFalse(dto.getEvaluationId());
        if (optionalEvaluation.isEmpty()) {
            return ResponseUtil.notFound("Evaluation not found with id: " + dto.getEvaluationId());
        }
        evaluationScore.setEvaluation(optionalEvaluation.get());

        EvaluationScore updated = evaluationScoreRepository.save(evaluationScore);

        EvaluationScoreResponseDTO responseDTO = new EvaluationScoreResponseDTO();
        responseDTO.setCriterionId(updated.getCriterion().getId());
        responseDTO.setCriterionName(updated.getCriterion().getName());
        responseDTO.setEvaluationId(updated.getEvaluation().getId());
        responseDTO.setScore(updated.getScore());

        return ResponseUtil.success(responseDTO);
    }

    @Override
    public ResponseEntity<ResponseDTO<Void>> delete(Long id) {
        Optional<EvaluationScore> optionalEvaluationScore = evaluationScoreRepository.findByIdAndIsDeletedFalse(id);
        if (optionalEvaluationScore.isEmpty()) {
            return ResponseUtil.notFound("EvaluationScore not found with id: " + id);
        }
        EvaluationScore evaluationScore = optionalEvaluationScore.get();
        Evaluation evaluation = evaluationScore.getEvaluation();
        evaluation.setTotalScore(evaluation.getTotalScore() - evaluationScore.getScore() * evaluationScore.getCriterion().getWeight());
        evaluationScore.setDeleted(true);
        evaluationScoreRepository.save(evaluationScore);

        return ResponseUtil.deleted("Evaluation Score with id " + evaluationScore.getId() + " deleted successfully");
    }
}
