package com.vorstu.excel.service;

import com.vorstu.excel.dto.FilterProperties;
import com.vorstu.excel.model.GroupEntity;
import com.vorstu.excel.model.TimeSlotEntity;
import com.vorstu.excel.model.WorkDayEntity;
import com.vorstu.excel.repository.WorkRepository;
import com.vorstu.excel.utils.WeekDays;
import lombok.RequiredArgsConstructor;
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
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.vorstu.excel.utils.ExcelUtils.*;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private static final String REBASE = "ПЕРЕЕЗД";
    private final WorkRepository workRepository;

    public boolean parse(MultipartFile file) {
        List<WorkDayEntity> workDayEntities = processWorkBook(getWorkBookByInputStream(file));
        workRepository.saveAll(workDayEntities);
        return true;
    }

    public List<WorkDayEntity> findFilteredWorkDays(FilterProperties filterProperties) {
        return workRepository.findAll().stream()
                .filter(workDayEntity -> Objects.isNull(filterProperties.getGroup()) || workDayEntity.getGroup().getName().equals(filterProperties.getGroup()))
                .collect(Collectors.toList());
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
        for (WeekDays weekDay : WeekDays.values()) {
            workDays.addAll(processWeekDay(sheet, weekDay, groups));
        }
        return workDays;
    }

    private List<WorkDayEntity> processWeekDay(XSSFSheet sheet, WeekDays weekDay, List<GroupEntity> groups) {
        int weekDayRowIndex = getWeekDayRowIndex(sheet, weekDay.getValue());
        XSSFCell cell = sheet.getRow(weekDayRowIndex).getCell(0);
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

    private boolean checkDate(XSSFSheet sheet, WeekDays weekDay, XSSFCell timeCell) {
        try {
            return Objects.nonNull(timeCell) && Objects.nonNull(timeCell.getDateCellValue());
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to parse date: wrong format. Found at: sheet: %s, weekDay: %s, time: %s", sheet.getSheetName(), weekDay.getValue(), timeCell.getStringCellValue()));
        }
    }

    private List<TimeSlotEntity> processTimeSlots(XSSFSheet sheet, XSSFCell timeCell, List<GroupEntity> groups) {
        List<TimeSlotEntity> timeSlots = new ArrayList<>();
        LocalTime time = Instant.ofEpochMilli(timeCell.getDateCellValue().getTime()).atZone(ZoneId.systemDefault()).toLocalTime();
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
                if (Objects.isNull(oddCell) || (StringUtils.isBlank(cell.getStringCellValue()) && StringUtils.isBlank(oddCell.getStringCellValue()))) {
                    continue;
                }
                timeSlots.addAll(processLesson(sheet, cell, groups, time, Boolean.TRUE));
                timeSlots.addAll(processLesson(sheet, oddCell, groups, time, Boolean.FALSE));
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

    private LocalTime getRaisedTime(LocalTime time) {
        if (time.equals(LocalTime.of(11, 30))) {
            return time.plus(Duration.ofMinutes(120));
        }
        return time.plus(Duration.ofMinutes(105));
    }

    private List<TimeSlotEntity> processLesson(XSSFSheet sheet, XSSFCell cell, List<GroupEntity> groups, LocalTime time, Boolean even) {
        List<TimeSlotEntity> result = new ArrayList<>();
        if (Objects.isNull(cell)) {
            return result;
        }
        Pair<Integer, Integer> lessonColRange = getFirstAndLastMergedCol(sheet, cell);
        for (GroupEntity group : groups) {
            if (group.getStartColumnIndex() >= lessonColRange.getFirst() && group.getEndColumnIndex() <= lessonColRange.getSecond()) {
                result.add(new TimeSlotEntity(time, time, getStringValueWithoutAdditionalSpaces(cell.getStringCellValue()), even, group));
                continue;
            }
            if (group.getStartColumnIndex() <= lessonColRange.getFirst() && group.getEndColumnIndex() >= lessonColRange.getSecond()) {
                even = group.getStartColumnIndex() == lessonColRange.getFirst();
                result.add(new TimeSlotEntity(time, getRaisedTime(time), getStringValueWithoutAdditionalSpaces(cell.getStringCellValue()), even, group));
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