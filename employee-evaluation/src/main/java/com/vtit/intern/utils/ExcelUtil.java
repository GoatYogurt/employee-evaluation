package com.vtit.intern.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
    public static CellStyle fullBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    public static CellStyle fullBorderRightNumber(XSSFWorkbook workbook, Font defaultFont) {
        CellStyle style = workbook.createCellStyle();

        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());

        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.RIGHT);

        style.setWrapText(true);

        defaultFont.setBold(false);
        defaultFont.setFontName("Times New Roman");
        defaultFont.setFontHeightInPoints((short) 12);

        style.setFont(defaultFont);
        return style;
    }

    public static CellStyle fullBorderCenterText(XSSFWorkbook workbook, Font defaultFont) {
        CellStyle style = workbook.createCellStyle();

        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());

        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.CENTER);

        style.setWrapText(true);

        defaultFont.setBold(false);
        defaultFont.setFontName("Times New Roman");
        defaultFont.setFontHeightInPoints((short) 12);

        style.setFont(defaultFont);
        return style;
    }

    public static void setCell(Row row, int colIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value != null) {
            cell.setCellValue(value.toString());
        }
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
}