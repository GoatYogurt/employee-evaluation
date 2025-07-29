package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.EvaluationCycleDTO;
import com.vtit.intern.dtos.EvaluationDTO;
import com.vtit.intern.dtos.PageResponse;
import com.vtit.intern.exceptions.ResourceNotFoundException;
import com.vtit.intern.models.Evaluation;
import com.vtit.intern.models.EvaluationCycle;
import com.vtit.intern.repositories.CriterionRepository;
import com.vtit.intern.repositories.EmployeeRepository;
import com.vtit.intern.repositories.EvaluationCycleRepository;
import com.vtit.intern.repositories.EvaluationRepository;
import com.vtit.intern.services.EvaluationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvaluationServiceImpl implements EvaluationService {
    @Autowired
    private final EvaluationRepository evaluationRepository;
    @Autowired
    private final EmployeeRepository employeeRepository;
    @Autowired
    private final CriterionRepository criterionRepository;
    @Autowired
    private final EvaluationCycleRepository evaluationCycleRepository;
    @Autowired
    private final ModelMapper modelMapper;

    public EvaluationServiceImpl(EvaluationRepository evaluationRepository, EmployeeRepository employeeRepository,
                                 CriterionRepository criterionRepository, EvaluationCycleRepository evaluationCycleRepository, ModelMapper modelMapper) {
        this.evaluationRepository = evaluationRepository;
        this.employeeRepository = employeeRepository;
        this.criterionRepository = criterionRepository;
        this.modelMapper = modelMapper;
        this.evaluationCycleRepository = evaluationCycleRepository;
    }

    @Override
    public EvaluationDTO evaluate(EvaluationDTO evaluationDTO) {
        Evaluation e = new Evaluation();

        e.setEmployee(employeeRepository.findById(evaluationDTO.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + evaluationDTO.getEmployeeId())));
        e.setCriterion(criterionRepository.findById(evaluationDTO.getCriterionId())
                .orElseThrow(() -> new ResourceNotFoundException("Criterion not found with id: " + evaluationDTO.getCriterionId())));
        e.setScore(evaluationDTO.getScore());
        e.setComment(evaluationDTO.getComment());
        e.setEvaluationDate(evaluationDTO.getEvaluationDate());

        Long evaluationCycleId = evaluationDTO.getEvaluationCycleId();
        EvaluationCycle evaluationCycle = evaluationCycleRepository.findById(evaluationCycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation Cycle not found with id: " + evaluationCycleId));

        evaluationCycle.addEvaluation(e);
        evaluationCycleRepository.save(evaluationCycle);

//        Evaluation savedEvaluation = evaluationRepository.save(e);
        return modelMapper.map(e, EvaluationDTO.class);
    }

    @Override
    public PageResponse<EvaluationDTO> getEvaluations(Long employeeId, Long criterionId, Double minScore, Double maxScore, String comment, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Evaluation> evaluationPage = evaluationRepository.searchEvaluations(
                employeeId,
                criterionId,
                minScore,
                maxScore,
                comment != null ? comment.trim() : null,
                startDate,
                endDate,
                pageable
        );

        List<EvaluationDTO> content = evaluationPage.stream()
                .map(evaluation -> modelMapper.map(evaluation, EvaluationDTO.class))
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                evaluationPage.getNumber(),
                evaluationPage.getSize(),
                evaluationPage.getTotalElements(),
                evaluationPage.getTotalPages(),
                evaluationPage.isLast()
        );
    }

    @Override
    public EvaluationDTO update(Long evaluationId, EvaluationDTO evaluationDTO) {
        Evaluation existingEvaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation not found with id: " + evaluationId));

        existingEvaluation.setScore(evaluationDTO.getScore());
        existingEvaluation.setComment(evaluationDTO.getComment());
        existingEvaluation.setEvaluationDate(evaluationDTO.getEvaluationDate());

        Evaluation updatedEvaluation = evaluationRepository.save(existingEvaluation);
        return modelMapper.map(updatedEvaluation, EvaluationDTO.class);
    }

    @Override
    public void delete(Long evaluationId) {
        Evaluation existingEvaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation not found with id: " + evaluationId));

        EvaluationCycle evaluationCycle = existingEvaluation.getEvaluationCycle();
        evaluationCycle.removeEvaluation(existingEvaluation);

        evaluationCycleRepository.save(evaluationCycle);
    }
}
