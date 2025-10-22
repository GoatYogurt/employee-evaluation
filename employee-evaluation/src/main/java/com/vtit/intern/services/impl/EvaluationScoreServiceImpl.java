package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.requests.CriterionScoreRequestDTO;
import com.vtit.intern.dtos.requests.EvaluationScoreRequestDTO;
import com.vtit.intern.dtos.requests.MultipleEvaluationScoreRequestDTO;
import com.vtit.intern.dtos.responses.EvaluationResponseDTO;
import com.vtit.intern.dtos.responses.EvaluationScoreResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.models.*;
import com.vtit.intern.repositories.*;
import com.vtit.intern.services.EvaluationScoreService;
import com.vtit.intern.utils.ResponseUtil;
import lombok.AllArgsConstructor;
import org.mariadb.jdbc.util.options.OptionAliases;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EvaluationScoreServiceImpl implements EvaluationScoreService {

    private final EvaluationScoreRepository evaluationScoreRepository;
    private final EvaluationRepository evaluationRepository;
    private final CriterionRepository criterionRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final EvaluationCycleRepository evaluationCycleRepository;
    private final ModelMapper modelMapper;

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
    public ResponseEntity<ResponseDTO<EvaluationResponseDTO>> createMultiple(MultipleEvaluationScoreRequestDTO dto) {
        Long employeeId = dto.getEmployeeId();
        Long projectId = dto.getProjectId();
        Long evaluationCycleId = dto.getEvaluationCycleId();

        Optional<Employee> optionalEmployee = employeeRepository.findByIdAndIsDeletedFalse(employeeId);
        if (optionalEmployee.isEmpty()) {
            return ResponseUtil.notFound("Employee not found with id: " + employeeId);
        }

        Optional<Project> optionalProject = projectRepository.findByIdAndIsDeletedFalse(projectId);
        if (optionalProject.isEmpty()) {
            return ResponseUtil.notFound("Project not found with id: " + projectId);
        }

        Optional<EvaluationCycle> optionalEvaluationCycle = evaluationCycleRepository.findByIdAndIsDeletedFalse(evaluationCycleId);
        if (optionalEvaluationCycle.isEmpty()) {
            return ResponseUtil.notFound("Evaluation Cycle not found with id: " + evaluationCycleId);
        }

        int numCriteria = criterionRepository.findAllByIsDeletedFalse().size();
        if (dto.getScores().size() != numCriteria) {
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, "Number of scores provided (" + dto.getScores().size() + ") does not match number of criteria (" + numCriteria + ")");
        }

        Optional<Evaluation> optionalEvaluation = evaluationRepository.findByEmployee_IdAndProject_IdAndEvaluationCycle_IdAndIsDeletedFalse(employeeId, projectId, evaluationCycleId);
        if (optionalEvaluation.isEmpty()) {
            Evaluation evaluation = new Evaluation();
            evaluation.setEmployee(optionalEmployee.get());
            evaluation.setProject(optionalProject.get());
            evaluation.setEvaluationCycle(optionalEvaluationCycle.get());
            evaluationRepository.save(evaluation);

            double totalScore = 0.0;
            for (CriterionScoreRequestDTO scoreDTO: dto.getScores()) {
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
            return ResponseUtil.created(modelMapper.map(evaluation, EvaluationResponseDTO.class));
        }
        Evaluation evaluation = optionalEvaluation.get();
        Long evaluationId = evaluation.getId();

        double totalScore = 0.0;

        for (CriterionScoreRequestDTO scoreDTO : dto.getScores()) {
            // TODO: optimize to avoid unnecessary DB calls
            Optional<EvaluationScore> optionalEvaluationScore = evaluationScoreRepository.findByEvaluationIdAndCriterionIdAndIsDeletedFalse(evaluationId, scoreDTO.getCriterionId());
            if (optionalEvaluationScore.isEmpty()) {
                return ResponseUtil.error(HttpStatus.BAD_REQUEST, "EvaluationScore not found for evaluation id: " + evaluationId + " and criterion id: " + scoreDTO.getCriterionId());
            }
            EvaluationScore evaluationScore = optionalEvaluationScore.get();

            evaluationScore.setScore(scoreDTO.getScore());
            Optional<Criterion> optionalCriterion = criterionRepository.findByIdAndIsDeletedFalse(scoreDTO.getCriterionId());
            if (optionalCriterion.isEmpty()) {
                return ResponseUtil.notFound("Criterion not found with id: " + scoreDTO.getCriterionId());
            }
            evaluationScoreRepository.save(evaluationScore);

            double weight = evaluationScore.getCriterion().getWeight();
            totalScore += scoreDTO.getScore() * weight;
        }

        evaluation.setTotalScore(totalScore);
        evaluationRepository.save(evaluation);

        return ResponseUtil.created(modelMapper.map(evaluation, EvaluationResponseDTO.class));
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
