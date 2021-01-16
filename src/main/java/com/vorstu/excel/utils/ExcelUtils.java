package com.vorstu.excel.utils;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.List;
import java.util.Objects;

public class ExcelUtils {

    private static final String DAY = "день";

    public static String getStringValueWithoutAdditionalSpaces(String value) {
        return value.replaceAll("[\\s]{2,}", " ");
    }

    public static Pair<Integer, Integer> getFirstAndLastMergedCol(XSSFSheet sheet, XSSFCell cell) {
        CellRangeAddress range = getRangeByCell(sheet, cell);
        if (Objects.isNull(range)) {
            return Pair.create(cell.getColumnIndex(), cell.getColumnIndex());
        }
        return Pair.create(range.getFirstColumn(), range.getLastColumn());
    }

    public static Pair<Integer, Integer> getFirstAndLastMergedRow(XSSFSheet sheet, XSSFCell cell) {
        CellRangeAddress range = getRangeByCell(sheet, cell);
        if (Objects.isNull(range)) {
            return Pair.create(cell.getRowIndex(), cell.getRowIndex());
        }
        return Pair.create(range.getFirstRow(), range.getLastRow());
    }

    public static int getGroupRowIndex(XSSFSheet sheet) {
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = sheet.getRow(i);
            XSSFCell cell = row.getCell(0);
            if (Objects.nonNull(cell) && Objects.nonNull(cell.getStringCellValue()) && DAY.equalsIgnoreCase(cell.getStringCellValue())) {
                return i;
            }

        }
        throw new RuntimeException("Unable to find table start (table starts with 'День')");
    }

    public static int getWeekDayRowIndex(XSSFSheet sheet, String weekDay) {
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = sheet.getRow(i);
            XSSFCell cell = row.getCell(0);
            if (Objects.nonNull(cell) && Objects.nonNull(cell.getStringCellValue()) && weekDay.equalsIgnoreCase(cell.getStringCellValue())) {
                return i;
            }
        }
        throw new RuntimeException("Unable to find weekday in the schedule table");
    }

    private static CellRangeAddress getRangeByCell(XSSFSheet sheet, XSSFCell cell) {
        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
        for (CellRangeAddress mergedRegion : mergedRegions) {
            if (cell.getRowIndex() == mergedRegion.getFirstRow() && cell.getColumnIndex() == mergedRegion.getFirstColumn()) {
                return mergedRegion;
            }
        }
        return null;
    }


}
