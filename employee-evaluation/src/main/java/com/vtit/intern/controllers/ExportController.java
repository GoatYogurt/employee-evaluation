package com.vtit.intern.controllers;

import com.vtit.intern.models.Employee;
import com.vtit.intern.repositories.EmployeeRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/exports")
@AllArgsConstructor
public class ExportController {
    @Autowired
    private final EmployeeRepository employeeRepository;

    private Workbook getEmployeeWorkbook() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employees");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Full Name");
        header.createCell(2).setCellValue("Department");
        header.createCell(4).setCellValue("Role");
        header.createCell(6).setCellValue("Username");
        header.createCell(7).setCellValue("Email");

        List<Employee> employees = employeeRepository.findAll();

        int rowNum = 1;
        for (Employee employee: employees) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(employee.getId());
            row.createCell(1).setCellValue(employee.getFullName());
            row.createCell(2).setCellValue(employee.getDepartment());
            row.createCell(4).setCellValue(employee.getRole().name());
            row.createCell(6).setCellValue(employee.getUsername());
            row.createCell(7).setCellValue(employee.getEmail());
        }

        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("ID");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Name");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Department");
        headerCell.setCellStyle(headerStyle);

        return workbook;
    }

    @GetMapping("/employees")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=employees.xlsx";
        response.setHeader(headerKey, headerValue);

        Workbook workbook = getEmployeeWorkbook();
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
