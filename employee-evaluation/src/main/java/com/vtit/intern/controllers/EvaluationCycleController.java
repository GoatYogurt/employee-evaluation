package com.vtit.intern.controllers;

import com.vtit.intern.dtos.EvaluationCycleDTO;
import com.vtit.intern.services.impl.EvaluationCycleServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluation-cycles")
public class EvaluationCycleController {
    private final EvaluationCycleServiceImpl evaluationCycleServiceImpl;

    public EvaluationCycleController(EvaluationCycleServiceImpl evaluationCycleServiceImpl) {
        this.evaluationCycleServiceImpl = evaluationCycleServiceImpl;
    }

    @GetMapping
    public List<EvaluationCycleDTO> getAllEvaluationCycles() {
        return evaluationCycleServiceImpl.getAllEvaluationCycles();
    }

    @GetMapping("/{id}")
    public EvaluationCycleDTO getEvaluationCycleById(@PathVariable Long id) {
        return evaluationCycleServiceImpl.get(id);
    }

    @GetMapping("/active")
    public List<EvaluationCycleDTO> getActiveEvaluationCycles() {
        return evaluationCycleServiceImpl.getActiveEvaluationCycles();
    }

    @PostMapping
    public EvaluationCycleDTO createEvaluationCycle(@RequestBody EvaluationCycleDTO evaluationCycleDTO) {
        return evaluationCycleServiceImpl.create(evaluationCycleDTO);
    }

    @PutMapping("/{id}")
    public EvaluationCycleDTO updateEvaluationCycle(@PathVariable Long id, @RequestBody EvaluationCycleDTO evaluationCycleDTO) {
        return evaluationCycleServiceImpl.update(id, evaluationCycleDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteEvaluationCycle(@PathVariable Long id) {
        evaluationCycleServiceImpl.delete(id);
    }
}
