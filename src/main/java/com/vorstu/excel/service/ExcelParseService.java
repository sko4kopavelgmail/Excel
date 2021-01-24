package com.vorstu.excel.service;

import com.vorstu.excel.model.GroupEntity;
import com.vorstu.excel.model.TimeSlotEntity;
import com.vorstu.excel.model.WorkDayEntity;
import com.vorstu.excel.repository.WorkRepository;
import com.vorstu.excel.utils.LessonRange;
import com.vorstu.excel.utils.WeekDay;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.vorstu.excel.utils.ExcelUtils.*;

@Service
public class ExcelParseService {

    private static final String REBASE = "ПЕРЕЕЗД";
    private static final String EVEN = "знаменатель";
    private static final String ODD = "числитель";

    private final WorkRepository workRepository;

    public ExcelParseService(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    public boolean parse(MultipartFile file) {
        List<WorkDayEntity> workDayEntities = processWorkBook(getWorkBookByInputStream(file));
        workRepository.saveAll(workDayEntities);
        return true;
    }

    private List<WorkDayEntity> processWorkBook(XSSFWorkbook workbook) {
        List<WorkDayEntity> schedule = new ArrayList<>();
        for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
            schedule.addAll(processSheet(workbook.getSheetAt(sheetIndex)));
        }
        return schedule;
    }

    private List<WorkDayEntity> processSheet(XSSFSheet sheet) {
        List<GroupEntity> groups = getGroups(sheet);
        List<WorkDayEntity> workDays = new ArrayList<>();
        if (sheet.getPhysicalNumberOfRows() == 0) {
            return workDays;
        }
        for (WeekDay weekDay : WeekDay.values()) {
            workDays.addAll(processWeekDay(sheet, weekDay, groups));
        }
        return workDays;
    }

    private List<WorkDayEntity> processWeekDay(XSSFSheet sheet, WeekDay weekDay, List<GroupEntity> groups) {
        Optional<Integer> weekDayRowIndex = getWeekDayRowIndex(sheet, weekDay.getValue());
        if (!weekDayRowIndex.isPresent()) {
            return new ArrayList<>();
        }
        XSSFCell cell = sheet.getRow(weekDayRowIndex.get()).getCell(0);
        Pair<Integer, Integer> weekDayRange = getFirstAndLastMergedRow(sheet, cell);
        List<TimeSlotEntity> timeSlots = new ArrayList<>();
        for (int rowIndex = weekDayRange.getFirst(); rowIndex < weekDayRange.getSecond(); rowIndex++) {
            XSSFCell timeCell = sheet.getRow(rowIndex).getCell(1);
            if (checkDate(sheet, weekDay, timeCell)) {
                timeSlots.addAll(processTimeSlots(sheet, timeCell, groups));
            }
        }
        return groupTimeSlots(timeSlots, weekDay.getValue());
    }

    private boolean checkDate(XSSFSheet sheet, WeekDay weekDay, XSSFCell timeCell) {
        try {
            return Objects.nonNull(timeCell) && Objects.nonNull(timeCell.getDateCellValue());
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to parse date: wrong format. Found at: sheet: %s, weekDay: %s, time: %s", sheet.getSheetName(), weekDay.getValue(), timeCell.getStringCellValue()));
        }
    }

    private List<TimeSlotEntity> processTimeSlots(XSSFSheet sheet, XSSFCell timeCell, List<GroupEntity> groups) {
        List<TimeSlotEntity> timeSlots = new ArrayList<>();
        LocalTime time = getLocalTime(timeCell);
        Pair<Integer, Integer> timeCellRowRange = getFirstAndLastMergedRow(sheet, timeCell);

        XSSFRow row = sheet.getRow(timeCell.getRowIndex());

        for (int cellIndex = timeCell.getColumnIndex() + 1; cellIndex < row.getLastCellNum(); cellIndex++) {
            XSSFCell cell = row.getCell(cellIndex);
            if (Objects.isNull(cell) || CellType.FORMULA.equals(cell.getCellType())) {
                continue;
            }
            Pair<Integer, Integer> lessonRowRange = getFirstAndLastMergedRow(sheet, cell);
            if (lessonRowRange.getSecond().equals(timeCellRowRange.getSecond())) {
                if (StringUtils.isBlank(cell.getStringCellValue()) || REBASE.equalsIgnoreCase(cell.getStringCellValue())) {
                    continue;
                }
                timeSlots.addAll(processLesson(sheet, cell, groups, time, null));
            }
            if (lessonRowRange.getSecond() < timeCellRowRange.getSecond()) {
                XSSFRow oddRow = sheet.getRow(timeCell.getRowIndex() + 1);
                XSSFCell oddCell = oddRow.getCell(cellIndex);
                if (Objects.nonNull(oddCell) && !StringUtils.isBlank(oddCell.getStringCellValue())) {
                    timeSlots.addAll(processLesson(sheet, oddCell, groups, time, Boolean.FALSE));
                }
                if (!StringUtils.isBlank(cell.getStringCellValue())) {
                    timeSlots.addAll(processLesson(sheet, cell, groups, time, Boolean.TRUE));
                }
            }
            if (lessonRowRange.getSecond() > timeCellRowRange.getSecond()) {
                if (StringUtils.isBlank(cell.getStringCellValue())) {
                    continue;
                }
                timeSlots.addAll(processLesson(sheet, cell, groups, time, null));
            }
        }

        return timeSlots;
    }

    private LocalTime getLocalTime(XSSFCell timeCell) {
        return LocalTime.of(timeCell.getDateCellValue().getHours(), timeCell.getDateCellValue().getMinutes());
    }

    private List<TimeSlotEntity> processLesson(XSSFSheet sheet, XSSFCell cell, List<GroupEntity> groups, LocalTime time, Boolean odd) {
        List<TimeSlotEntity> result = new ArrayList<>();
        if (Objects.isNull(cell)) {
            return result;
        }
        Pair<Integer, Integer> lessonColRange = getFirstAndLastMergedCol(sheet, cell);
        LessonRange range = LessonRange.getRangeByTime(time);
        for (GroupEntity group : groups) {
            if (group.getStartColumnIndex() >= lessonColRange.getFirst() && group.getEndColumnIndex() <= lessonColRange.getSecond()) {
                result.add(new TimeSlotEntity(
                        range.getRange().getFirst(),
                        range.getRange().getSecond(),
                        getStringValueWithoutAdditionalSpaces(cell.getStringCellValue()), odd, group)
                );
                continue;
            }
            if (group.getStartColumnIndex() <= lessonColRange.getFirst() && group.getEndColumnIndex() >= lessonColRange.getSecond()) {
                odd = group.getStartColumnIndex() == lessonColRange.getFirst();
                if (cell.getStringCellValue().contains(ODD)) {
                    odd = true;
                }
                if (cell.getStringCellValue().contains(EVEN)) {
                    odd = false;
                }
                result.add(new TimeSlotEntity(
                        range.getRange().getFirst(),
                        LessonRange.getNextLessonRange(range.getIndex()).getRange().getSecond(),
                        getStringValueWithoutAdditionalSpaces(cell.getStringCellValue()), odd, group)
                );
            }
        }
        return result;
    }

    private List<WorkDayEntity> groupTimeSlots(List<TimeSlotEntity> timeSlots, String weekDay) {
        Map<GroupEntity, List<TimeSlotEntity>> collect = timeSlots.stream().collect(Collectors.groupingBy(TimeSlotEntity::getGroup));
        List<WorkDayEntity> schedule = new ArrayList<>();

        for (Map.Entry<GroupEntity, List<TimeSlotEntity>> item : collect.entrySet()) {
            WorkDayEntity workDayEntity = new WorkDayEntity(weekDay, item.getValue(), item.getKey());
            item.getValue().forEach(timeSlotEntity -> timeSlotEntity.setWorkDay(workDayEntity));
            schedule.add(workDayEntity);
        }

        return schedule;
    }

    private List<GroupEntity> getGroups(XSSFSheet sheet) {
        List<GroupEntity> result = new ArrayList<>();
        if (sheet.getPhysicalNumberOfRows() == 0) {
            return result;
        }
        XSSFRow row = sheet.getRow(getGroupRowIndex(sheet));
        for (int cellIndex = 2; cellIndex < row.getLastCellNum(); cellIndex++) {
            XSSFCell cell = row.getCell(cellIndex);
            if (!cell.getStringCellValue().isEmpty()) {
                Pair<Integer, Integer> colRange = getFirstAndLastMergedCol(sheet, cell);
                result.add(new GroupEntity(cell.getStringCellValue(), colRange.getFirst(), colRange.getSecond()));
            }
        }
        return result;
    }

    private XSSFWorkbook getWorkBookByInputStream(MultipartFile file) {
        try {
            return new XSSFWorkbook(getInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException("Unable to transform multipart file to workbook");
        }
    }

    private InputStream getInputStream(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read the provided file");
        }
    }

}
