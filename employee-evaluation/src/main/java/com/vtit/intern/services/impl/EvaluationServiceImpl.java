package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.EvaluationDTO;
import com.vtit.intern.exceptions.ResourceNotFoundException;
import com.vtit.intern.models.Evaluation;
import com.vtit.intern.repositories.CriterionRepository;
import com.vtit.intern.repositories.EmployeeRepository;
import com.vtit.intern.repositories.EvaluationRepository;
import com.vtit.intern.services.EvaluationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private final ModelMapper modelMapper;

    public EvaluationServiceImpl(EvaluationRepository evaluationRepository, EmployeeRepository employeeRepository,
                                 CriterionRepository criterionRepository, ModelMapper modelMapper) {
        this.evaluationRepository = evaluationRepository;
        this.employeeRepository = employeeRepository;
        this.criterionRepository = criterionRepository;
        this.modelMapper = modelMapper;
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
        Evaluation savedEvaluation = evaluationRepository.save(e);
        return modelMapper.map(savedEvaluation, EvaluationDTO.class);
    }

    @Override
    public List<EvaluationDTO> getEvaluationsByEmployeeId(Long employeeId) {
        return evaluationRepository.findByEmployeeId(employeeId).stream()
                .map(evaluation -> modelMapper.map(evaluation, EvaluationDTO.class))
                .collect(Collectors.toList());
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
        evaluationRepository.delete(existingEvaluation);
    }
}
