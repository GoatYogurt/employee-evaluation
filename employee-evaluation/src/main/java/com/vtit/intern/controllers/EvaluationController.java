package com.vtit.intern.controllers;

import com.vtit.intern.dtos.EvaluationDTO;
import com.vtit.intern.services.EvaluationService;
import com.vtit.intern.services.impl.EvaluationServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {
    private final EvaluationServiceImpl evaluationServiceImpl;

    public EvaluationController(EvaluationServiceImpl evaluationServiceImpl) {
        this.evaluationServiceImpl = evaluationServiceImpl;
    }

    @PostMapping
    public ResponseEntity<EvaluationDTO> evaluate(@RequestBody EvaluationDTO evaluationDTO) {
        return ResponseEntity.status(201).body(evaluationServiceImpl.evaluate(evaluationDTO));
    }

    @GetMapping("/employee/{employeeId}")
    public List<EvaluationDTO> getEvaluations(@PathVariable Long employeeId) {
        return evaluationServiceImpl.getEvaluationsByEmployeeId(employeeId);
    }
}
