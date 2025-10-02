package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.requests.EvaluationRequestDTO;
import com.vtit.intern.dtos.responses.EvaluationResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.dtos.searches.EvaluationSearchDTO;
import com.vtit.intern.exceptions.ResourceNotFoundException;
import com.vtit.intern.models.Employee;
import com.vtit.intern.models.Evaluation;
import com.vtit.intern.models.EvaluationCycle;
import com.vtit.intern.enums.EvaluationCycleStatus;
import com.vtit.intern.models.Project;
import com.vtit.intern.repositories.*;
import com.vtit.intern.services.EvaluationService;
import com.vtit.intern.utils.ResponseUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private final ProjectRepository projectRepository;
    @Autowired
    private final ModelMapper modelMapper;

    public EvaluationServiceImpl(EvaluationRepository evaluationRepository, EmployeeRepository employeeRepository,
                                 CriterionRepository criterionRepository, EvaluationCycleRepository evaluationCycleRepository, ModelMapper modelMapper, ProjectRepository projectRepository) {
        this.evaluationRepository = evaluationRepository;
        this.employeeRepository = employeeRepository;
        this.criterionRepository = criterionRepository;
        this.modelMapper = modelMapper;
        this.evaluationCycleRepository = evaluationCycleRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public ResponseEntity<ResponseDTO<EvaluationResponseDTO>> evaluate(EvaluationRequestDTO dto) {
        // find the evaluation cycle by ID
        Long evaluationCycleId = dto.getEvaluationCycleId();
        EvaluationCycle evaluationCycle = evaluationCycleRepository.findById(evaluationCycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation Cycle not found with id: " + evaluationCycleId));

        // check if the evaluation cycle is still active
        if (evaluationCycle.getStatus() == EvaluationCycleStatus.COMPLETED ||
        evaluationCycle.getStatus() == EvaluationCycleStatus.CANCELLED) {
            throw new IllegalStateException("Cannot add evaluation to a completed or closed evaluation cycle.");
        }

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + dto.getProjectId()));

        Evaluation e = new Evaluation();
        e.setEmployee(employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + dto.getEmployeeId())));
        e.setEvaluationCycle(evaluationCycle);
        e.setProject(project);
        e.setTotalScore(0.0);
        e.setCompletionLevel(null);
        e.setKiRanking(null);
        e.setManagerFeedback(dto.getManagerFeedback() == null ? "" : dto.getManagerFeedback());
        e.setCustomerFeedback(dto.getCustomerFeedback() == null ? "" : dto.getCustomerFeedback());
        e.setNote(dto.getNote() == null ? "" : dto.getNote());

        Evaluation savedEvaluation = evaluationRepository.save(e);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.success(modelMapper.map(savedEvaluation, EvaluationResponseDTO.class)));
    }

    @Override
    public ResponseEntity<ResponseDTO<EvaluationResponseDTO>> update(Long evaluationId, EvaluationRequestDTO dto) {

        Evaluation existingEvaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation not found with id: " + evaluationId));

        // validate that the evaluation cycle is still active
        EvaluationCycle evaluationCycle = existingEvaluation.getEvaluationCycle();
        if (evaluationCycle.getStatus() == EvaluationCycleStatus.COMPLETED ||
            evaluationCycle.getStatus() == EvaluationCycleStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update evaluation in a completed or closed evaluation cycle.");
        }

//        existingEvaluation.setScore(dto.getScore());
//        existingEvaluation.setComment(dto.getComment());
//        existingEvaluation.setEvaluationDate(dto.getEvaluationDate());

        Evaluation updatedEvaluation = evaluationRepository.save(existingEvaluation);
        return ResponseEntity.ok(ResponseUtil.success(modelMapper.map(updatedEvaluation, EvaluationResponseDTO.class)));
    }

    @Override
    public void delete(Long evaluationId) {
        Evaluation existingEvaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation not found with id: " + evaluationId));

        // validate that the evaluation cycle is still active
        if (existingEvaluation.getEvaluationCycle().getStatus() == EvaluationCycleStatus.COMPLETED ||
            existingEvaluation.getEvaluationCycle().getStatus() == EvaluationCycleStatus.CANCELLED) {
            throw new IllegalStateException("Cannot delete evaluation in a completed or closed evaluation cycle.");
        }

        existingEvaluation.setDeleted(true);
        evaluationRepository.save(existingEvaluation);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseDTO<EvaluationResponseDTO>> moveEvaluationToCycle(Long evaluationId, Long newCycleId) {
        Evaluation existingEvaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation not found with id: " + evaluationId));

        EvaluationCycle newCycle = evaluationCycleRepository.findById(newCycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation Cycle not found with id: " + newCycleId));

        // validate that the old cycle is still active
        EvaluationCycle oldCycle = existingEvaluation.getEvaluationCycle();
        if (oldCycle.getStatus() == EvaluationCycleStatus.COMPLETED ||
            oldCycle.getStatus() == EvaluationCycleStatus.CANCELLED) {
            throw new IllegalStateException("Cannot move evaluation from a completed or closed evaluation cycle.");
        }

        // validate that the new cycle is active
        if (newCycle.getStatus() == EvaluationCycleStatus.COMPLETED ||
            newCycle.getStatus() == EvaluationCycleStatus.CANCELLED) {
            throw new IllegalStateException("Cannot move evaluation to a completed or closed evaluation cycle.");
        }

        // remove from old cycle
//        oldCycle.removeEvaluation(existingEvaluation);

        // add to new cycle
//        newCycle.addEvaluation(existingEvaluation);
        existingEvaluation.setEvaluationCycle(newCycle);

        Evaluation updatedEvaluation = evaluationRepository.save(existingEvaluation);
        evaluationCycleRepository.save(oldCycle);
        evaluationCycleRepository.save(newCycle);

        return ResponseEntity.ok(ResponseUtil.success(modelMapper.map(updatedEvaluation, EvaluationResponseDTO.class)));
    }

    @Override
    public ResponseEntity<ResponseDTO<EvaluationResponseDTO>> patch(Long evaluationId, EvaluationRequestDTO dto) {
        Evaluation existingEvaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation not found with id: " + evaluationId));

        // validate that the evaluation cycle is still active
        if (existingEvaluation.getEvaluationCycle().getStatus() == EvaluationCycleStatus.COMPLETED ||
            existingEvaluation.getEvaluationCycle().getStatus() == EvaluationCycleStatus.CANCELLED) {
            throw new IllegalStateException("Cannot patch evaluation in a completed or closed evaluation cycle.");
        }

//        if (dto.getScore() != null) {
//            existingEvaluation.setScore(dto.getScore());
//        }
//        if (dto.getComment() != null) {
//            existingEvaluation.setComment(dto.getComment());
//        }
//        if (dto.getEvaluationDate() != null) {
//            existingEvaluation.setEvaluationDate(dto.getEvaluationDate());
//        }

        Evaluation updatedEvaluation = evaluationRepository.save(existingEvaluation);
        return ResponseEntity.ok(ResponseUtil.success(modelMapper.map(updatedEvaluation, EvaluationResponseDTO.class)));
    }

    @Override
    public ResponseEntity<ResponseDTO<PageResponse<EvaluationResponseDTO>>> getEvaluations(
            EvaluationSearchDTO dto, Pageable pageable, Authentication auth) {

        // Validate score range
        if (dto != null) {
            if (dto.getMinScore() != null && dto.getMaxScore() != null &&
                    dto.getMinScore() > dto.getMaxScore()) {
                throw new IllegalArgumentException("Minimum score cannot be greater than maximum score");
            }
            if (dto.getStartDate() != null && dto.getEndDate() != null &&
                    dto.getStartDate().isAfter(dto.getEndDate())) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
        }

        // Determine who is allowed to see what
        Long employeeId = null;

        if (isEmployee(auth)) {
            // Employees can only see their own evaluations
            String username = auth.getName();
            Employee employee = employeeRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Employee not found with username: " + username));
            employeeId = employee.getId();

            // Force dto.employeeId to match the authenticated employee (or ignore it)
            if (dto != null) {
                dto.setEmployeeId(employeeId);
            }

        } else if (isManagerOrAdmin(auth)) {
            // Managers/Admins can see any employee’s evaluations
            if (dto != null) {
                employeeId = dto.getEmployeeId();
            }

        } else {
            throw new AccessDeniedException("You are not authorized to view evaluations");
        }

        // Perform the search
        if (dto == null) {
            return ResponseEntity.ok(ResponseUtil.success(searchAndMap(employeeId, null, null, null, null, null, null, pageable)));
        }

        return ResponseEntity.ok(ResponseUtil.success(searchAndMap(
                employeeId,
                dto.getCriterionId(),
                dto.getMinScore(),
                dto.getMaxScore(),
                trimOrNull(dto.getComment()),
                dto.getStartDate(),
                dto.getEndDate(),
                pageable
        )));
    }

    private boolean isEmployee(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
    }

    private boolean isManagerOrAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER") || a.getAuthority().equals("ROLE_ADMIN"));
    }

    private String trimOrNull(String str) {
        return (str != null && !str.isBlank()) ? str.trim() : null;
    }

    private PageResponse<EvaluationResponseDTO> searchAndMap(
            Long employeeId, Long criterionId, Double minScore, Double maxScore,
            String comment, LocalDate startDate, LocalDate endDate, Pageable pageable) {

        Page<Evaluation> evaluationPage = evaluationRepository.searchEvaluations(
                employeeId, pageable);

        List<EvaluationResponseDTO> content = evaluationPage.stream()
                .map(evaluation -> modelMapper.map(evaluation, EvaluationResponseDTO.class))
                .toList();

        return new PageResponse<>(
                content,
                evaluationPage.getNumber(),
                evaluationPage.getSize(),
                evaluationPage.getTotalElements(),
                evaluationPage.getTotalPages(),
                evaluationPage.isLast()
        );
    }

}
