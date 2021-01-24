package com.vorstu.excel.service;

import com.vorstu.excel.dto.FilterProperties;
import com.vorstu.excel.dto.TimeSlotDto;
import com.vorstu.excel.dto.WorkDayDto;
import com.vorstu.excel.mapper.TimeSlotMapper;
import com.vorstu.excel.mapper.WorkDayMapper;
import com.vorstu.excel.model.GroupEntity;
import com.vorstu.excel.model.TimeSlotEntity;
import com.vorstu.excel.model.WorkDayEntity;
import com.vorstu.excel.repository.GroupRepo;
import com.vorstu.excel.repository.WorkRepository;
import com.vorstu.excel.utils.WeekDay;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExcelFilterService {

    private final WorkRepository workRepository;
    private final GroupRepo groupRepo;

    public ExcelFilterService(WorkRepository workRepository, GroupRepo groupRepo) {
        this.workRepository = workRepository;
        this.groupRepo = groupRepo;
    }

    public List<WorkDayDto> findFilteredWorkDays(FilterProperties filterProperties) {
        List<WorkDayDto> result = new ArrayList<>();
        GroupEntity groupEntity = groupRepo.findByName(filterProperties.getGroup());
        LocalDate from = new Date(filterProperties.getFrom()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate to = new Date(filterProperties.getTo()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        while (!from.isAfter(to)) {
            buildWorkDay(from, groupEntity).ifPresent(result::add);
            from = from.plusDays(1);
        }

        return result;
    }

    private Optional<WorkDayDto> buildWorkDay(LocalDate date, GroupEntity groupEntity) {
        String weekDay = WeekDay.getWeekDayByLocalDateValue(date.getDayOfWeek().toString()).getValue();
        Optional<WorkDayEntity> workDay = workRepository.findByGroupAndName(groupEntity, weekDay);
        if (workDay.isPresent()) {
            WorkDayDto dto = WorkDayMapper.MAPPER.toDto(workDay.get());
            dto.setLessons(getFilteredLessons(workDay.get(), date));
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    private List<TimeSlotDto> getFilteredLessons(WorkDayEntity workDay, LocalDate date) {
        List<TimeSlotDto> collect = getTimeSlots(workDay, date);
        for (TimeSlotDto timeSlot : collect) {
            timeSlot.setStartLongTime(getLongFromLocalDateAndLocalTime(date, timeSlot.getStartTime()));
            timeSlot.setEndLongTime(getLongFromLocalDateAndLocalTime(date, timeSlot.getEndTime()));
        }
        return collect;
    }

    private List<TimeSlotDto> getTimeSlots(WorkDayEntity workDay, LocalDate date) {
        List<TimeSlotEntity> collect = workDay.getLessons().stream()
                .filter(timeSlotEntity -> Objects.isNull(timeSlotEntity.getOdd()) || isDateOdd(date) == timeSlotEntity.getOdd())
                .collect(Collectors.toList());
        return TimeSlotMapper.MAPPER.toDtoList(collect);
    }

    private Long getLongFromLocalDateAndLocalTime(LocalDate localDate, LocalTime localTime) {
        return LocalDateTime.of(localDate, localTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private boolean isDateOdd(LocalDate localDate) {
        LocalDate from = LocalDate.of(getYear(), 9, 1);
        int weeks = from.getDayOfWeek().equals(DayOfWeek.MONDAY) ? 0 : 1;

        while (from.isBefore(localDate) || from.isEqual(localDate)) {
            if (from.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
                weeks++;
            }
            from = from.plusDays(1);
        }
        System.out.println(weeks);
        return weeks % 2 == 1;
    }

    private int getYear() {
        return LocalDate.now().getMonth().getValue() > 10 ? LocalDate.now().getYear() : LocalDate.now().getYear() - 1;
    }


}
