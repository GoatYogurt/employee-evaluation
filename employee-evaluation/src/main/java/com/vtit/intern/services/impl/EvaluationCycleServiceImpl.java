package com.vtit.intern.services.impl;

import com.vtit.intern.dtos.requests.EvaluationCycleRequestDTO;
import com.vtit.intern.dtos.responses.EvaluationCycleResponseDTO;
import com.vtit.intern.dtos.responses.PageResponse;
import com.vtit.intern.dtos.responses.ProjectResponseDTO;
import com.vtit.intern.dtos.responses.ResponseDTO;
import com.vtit.intern.models.*;
import com.vtit.intern.repositories.EvaluationCycleRepository;
import com.vtit.intern.repositories.EvaluationRepository;
import com.vtit.intern.repositories.EvaluationScoreRepository;
import com.vtit.intern.repositories.ProjectRepository;
import com.vtit.intern.services.EvaluationCycleService;
import com.vtit.intern.utils.ExcelUtil;
import com.vtit.intern.utils.ResponseUtil;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.vtit.intern.enums.EvaluationCycleStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EvaluationCycleServiceImpl implements EvaluationCycleService {
    private final EvaluationCycleRepository evaluationCycleRepository;
    private final ProjectRepository projectRepository;
    private final EvaluationRepository evaluationRepository;
    private final EvaluationScoreRepository evaluationScoreRepository;

    @Override
    public ResponseEntity<ResponseDTO<EvaluationCycleResponseDTO>> create(EvaluationCycleRequestDTO dto) {
        EvaluationCycle evaluationCycle = requestToEntity(dto);
        EvaluationCycle savedEvaluationCycle = evaluationCycleRepository.save(evaluationCycle);
        return ResponseUtil.created(entityToResponse(savedEvaluationCycle));
    }

    @Override
    public ResponseEntity<ResponseDTO<EvaluationCycleResponseDTO>> get(Long id) {
        return evaluationCycleRepository.findByIdAndIsDeletedFalse(id)
                .map(ec -> ResponseUtil.success(entityToResponse(ec)))
                .orElseGet(() -> ResponseUtil.notFound("Evaluation Cycle not found with id: " + id));
    }

    @Override
    public ResponseEntity<ResponseDTO<Void>> delete(Long id) {
        Optional<EvaluationCycle> optionalEvaluationCycle = evaluationCycleRepository.findByIdAndIsDeletedFalse(id);;
        if (optionalEvaluationCycle.isEmpty()) {
            return ResponseUtil.notFound("Evaluation Cycle not found with id: " + id);
        }
        EvaluationCycle evaluationCycle = optionalEvaluationCycle.get();

        evaluationCycle.setDeleted(true);
        evaluationCycle.getProjects().clear();
        evaluationCycleRepository.save(evaluationCycle);
        return ResponseUtil.deleted("Evaluation Cycle " + evaluationCycle.getName() + " deleted successfully.");
    }

    @Override
    public ResponseEntity<ResponseDTO<PageResponse<EvaluationCycleResponseDTO>>> getAllEvaluationCycles(
            String name,
            String description,
            EvaluationCycleStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        Page<EvaluationCycle> evaluationCyclePage = evaluationCycleRepository.searchEvaluationCycles(
                name != null ? name.trim() : null,
                description != null ? description.trim() : null,
                status,
                startDate,
                endDate,
                pageable
        );

        List<EvaluationCycleResponseDTO> content = evaluationCyclePage.getContent().stream()
                .map(this::entityToResponse)
                .collect(Collectors.toList());

        return ResponseUtil.success(new PageResponse<>(
                content,
                evaluationCyclePage.getNumber(),
                evaluationCyclePage.getSize(),
                evaluationCyclePage.getTotalElements(),
                evaluationCyclePage.getTotalPages(),
                evaluationCyclePage.isLast()
        ));
    }

    @Override
    public ResponseEntity<ResponseDTO<PageResponse<EvaluationCycleResponseDTO>>> getActiveEvaluationCycles(Pageable pageable) {
        Page<EvaluationCycle> evaluationCyclePage = evaluationCycleRepository.searchEvaluationCycles(
                null, // name
                null, // description
                EvaluationCycleStatus.ACTIVE, // status
                null, // startDate
                null, // endDate
                pageable
        );

        List<EvaluationCycleResponseDTO> content = evaluationCyclePage.getContent().stream()
                .map(this::entityToResponse)
                .collect(Collectors.toList());

        return ResponseUtil.success(new PageResponse<>(
                content,
                evaluationCyclePage.getNumber(),
                evaluationCyclePage.getSize(),
                evaluationCyclePage.getTotalElements(),
                evaluationCyclePage.getTotalPages(),
                evaluationCyclePage.isLast()
        ));
    }

    @Override
    public ResponseEntity<ResponseDTO<PageResponse<ProjectResponseDTO>>> getProjectsByEvaluationCycleId(Long evaluationCycleId, Pageable pageable) {
        Optional<EvaluationCycle> evaluationCycle = evaluationCycleRepository.findByIdAndIsDeletedFalse(evaluationCycleId);

        if (evaluationCycle.isEmpty()) {
            return ResponseUtil.notFound("Evaluation Cycle not found with id: " + evaluationCycleId);
        } else {
            Page<Project> projectPage = projectRepository.findByIdInAndIsDeletedFalse(
                    evaluationCycle.get().getProjects().stream().map(Project::getId).collect(Collectors.toSet()),
                    pageable
            );

            List<ProjectResponseDTO> content = projectPage.getContent().stream()
                    .map(project -> {
                        ProjectResponseDTO dto = new ProjectResponseDTO();
                        dto.setId(project.getId());
                        dto.setCode(project.getCode());
                        dto.setOdc(project.isOdc());
                        dto.setManagerName(project.getManager() != null ? project.getManager().getFullName() : null);
                        Set<EvaluationCycle> evaluationCycles = project.getEvaluationCycles();
                        if (evaluationCycles != null) {
                            dto.setEvaluationCycleIds(evaluationCycles.stream().map(EvaluationCycle::getId).collect(Collectors.toSet()));
                        } else {
                            dto.setEvaluationCycleIds(Set.of());
                        }
                        dto.setCreatedAt(project.getCreatedAt());
                        dto.setUpdatedAt(project.getUpdatedAt());
                        dto.setCreatedBy(project.getCreatedBy());
                        dto.setUpdatedBy(project.getUpdatedBy());
                        return dto;
                    })
                    .toList();

            return ResponseUtil.success(new PageResponse<>(
                    content,
                    projectPage.getNumber(),
                    projectPage.getSize(),
                    projectPage.getTotalElements(),
                    projectPage.getTotalPages(),
                    projectPage.isLast()
            ));
        }
    }

    @Override
    public ResponseEntity<ResponseDTO<EvaluationCycleResponseDTO>> patch(Long id, EvaluationCycleRequestDTO dto) {
        Optional<EvaluationCycle> optionalEvaluationCycle = evaluationCycleRepository.findByIdAndIsDeletedFalse(id);
        if (optionalEvaluationCycle.isEmpty()) {
            return ResponseUtil.notFound("Evaluation Cycle not found with id: " + id);
        }
        EvaluationCycle existingEvaluationCycle = optionalEvaluationCycle.get();

        // Update fields selectively based on non-null values in evaluationCycleDTO
        if (dto.getName() != null) {
            existingEvaluationCycle.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            existingEvaluationCycle.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            existingEvaluationCycle.setStatus(dto.getStatus());
        }
        if (dto.getStartDate() != null) {
            existingEvaluationCycle.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            existingEvaluationCycle.setEndDate(dto.getEndDate());
        }

        EvaluationCycle updatedEvaluationCycle = evaluationCycleRepository.save(existingEvaluationCycle);
        return ResponseUtil.success(entityToResponse(updatedEvaluationCycle));
    }

    @Override
    public ResponseEntity<InputStreamResource> exportEvaluationCycleReport(Long evaluationCycleId) throws IOException {
        Optional<EvaluationCycle> optionalEvaluationCycle = evaluationCycleRepository.findByIdAndIsDeletedFalse(evaluationCycleId);
        if (optionalEvaluationCycle.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // TODO: handle properly
        }
        EvaluationCycle evaluationCycle = optionalEvaluationCycle.get();
        Set<Project> projects = evaluationCycle.getProjects();

        ClassPathResource resource = new ClassPathResource("templates/evaluation_cycle_export_template.xlsx");

        try (InputStream templateStream = resource.getInputStream();
                XSSFWorkbook workbook = new XSSFWorkbook(templateStream)) {

            CellStyle textStyle = ExcelUtil.fullBorderLeftText(workbook, workbook.createFont());
            CellStyle numberStyle = ExcelUtil.fullBorderRightNumber(workbook, workbook.createFont());

            Sheet sheet  = workbook.getSheetAt(0);
            int rowIndex = 7;

            for (Project project : projects) {
                Set<Evaluation> evaluations = evaluationRepository
                        .findByProject_IdAndEvaluationCycle_IdAndIsDeletedFalse(project.getId(), evaluationCycleId);

                for (Evaluation evaluation : evaluations) {
                    List<EvaluationScore> evaluationScores =
                            evaluationScoreRepository.findByEvaluationIdAndIsDeletedFalse(evaluation.getId());
                    Row row = sheet.createRow(rowIndex++);
                    Employee currentEmployee = evaluation.getEmployee();

                    // Basic employee and project info
                    ExcelUtil.setCell(row, 0, rowIndex - 7, numberStyle);
                    ExcelUtil.setCell(row, 1, currentEmployee.getStaffCode(), textStyle);
                    ExcelUtil.setCell(row, 2, currentEmployee.getFullName(), textStyle);
                    ExcelUtil.setCell(row, 3, currentEmployee.getEmail(), textStyle);
                    ExcelUtil.setCell(row, 4, currentEmployee.getDepartment(), textStyle);
                    ExcelUtil.setCell(row, 5, currentEmployee.getRole().name(), textStyle);
                    ExcelUtil.setCell(row, 6, currentEmployee.getLevel().toDisplayString(), textStyle);
                    ExcelUtil.setCell(row, 7, project.isOdc() ? "ODC" : "NOT ODC", textStyle);
                    ExcelUtil.setCell(row, 8, project.getCode(), textStyle);
                    ExcelUtil.setCell(row, 9, 5, numberStyle);

                    // Criterion scores
                    for (int i = 10; i <= 17; ++i) {
                        if (i == 10) {
                            double score = evaluationScores.getFirst().getScore();
                            ExcelUtil.setCell(row, 10, score, numberStyle);
                            continue;
                        }
                        double score = evaluationScores.get(i - 11).getScore();
                        ExcelUtil.setCell(row, i, score, numberStyle);
                    }

                    // Totals and feedback
                    ExcelUtil.setCell(row, 18, evaluation.getTotalScore(), numberStyle);
                    ExcelUtil.setCell(row, 19, evaluation.getCompletionLevel(), textStyle);
                    ExcelUtil.setCell(row, 20, evaluation.getKiRanking(), textStyle);
                    ExcelUtil.setCell(row, 21, evaluation.getManagerFeedback(), textStyle);
                    ExcelUtil.setCell(row, 22, evaluation.getCustomerFeedback(), textStyle);
                    ExcelUtil.setCell(row, 23, evaluation.getNote(), textStyle);
                }
            }


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            InputStreamResource inputStreamResource = new InputStreamResource(new java.io.ByteArrayInputStream(out.toByteArray()));
            String fileName = "Evaluation_Cycle_Report_" + evaluationCycle.getId() + ".xlsx";
            return ResponseUtil.downloadFile(fileName, inputStreamResource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // TODO: handle properly
        }
    }

    private EvaluationCycleResponseDTO entityToResponse(EvaluationCycle evaluationCycle) {
        EvaluationCycleResponseDTO evaluationCycleResponseDTO = new EvaluationCycleResponseDTO();
        evaluationCycleResponseDTO.setId(evaluationCycle.getId());
        evaluationCycleResponseDTO.setName(evaluationCycle.getName());
        evaluationCycleResponseDTO.setDescription(evaluationCycle.getDescription());
        evaluationCycleResponseDTO.setStartDate(evaluationCycle.getStartDate());
        evaluationCycleResponseDTO.setEndDate(evaluationCycle.getEndDate());
        evaluationCycleResponseDTO.setStatus(evaluationCycle.getStatus());
        evaluationCycleResponseDTO.setProjectIds(evaluationCycle.getProjects().stream().map(Project::getId).collect(Collectors.toSet()));
        evaluationCycleResponseDTO.setCreatedAt(evaluationCycle.getCreatedAt());
        evaluationCycleResponseDTO.setUpdatedAt(evaluationCycle.getUpdatedAt());
        evaluationCycleResponseDTO.setCreatedBy(evaluationCycle.getCreatedBy());
        evaluationCycleResponseDTO.setUpdatedBy(evaluationCycle.getUpdatedBy());

        return evaluationCycleResponseDTO;
    }

    private EvaluationCycle requestToEntity(EvaluationCycleRequestDTO dto) {
        EvaluationCycle evaluationCycle = new EvaluationCycle();
        evaluationCycle.setId(dto.getId());
        evaluationCycle.setName(dto.getName());
        evaluationCycle.setDescription(dto.getDescription());
        evaluationCycle.setStartDate(dto.getStartDate());
        evaluationCycle.setEndDate(dto.getEndDate());
        evaluationCycle.setStatus(dto.getStatus());

        return evaluationCycle;
    }
}

