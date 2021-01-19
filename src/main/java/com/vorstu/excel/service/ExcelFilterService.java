package com.vorstu.excel.service;

import com.vorstu.excel.dto.FilterProperties;
import com.vorstu.excel.model.GroupEntity;
import com.vorstu.excel.model.TimeSlotEntity;
import com.vorstu.excel.model.WorkDayEntity;
import com.vorstu.excel.repository.GroupRepo;
import com.vorstu.excel.repository.WorkRepository;
import com.vorstu.excel.utils.WeekDay;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExcelFilterService {

    private final WorkRepository workRepository;
    private final GroupRepo groupRepo;

    public List<WorkDayEntity> findFilteredWorkDays(FilterProperties filterProperties) {
        List<WorkDayEntity> result = new ArrayList<>();
        GroupEntity groupEntity = groupRepo.findByName(filterProperties.getGroup());
        LocalDate from = new Date(filterProperties.getFrom()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate to = new Date(filterProperties.getTo()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        while (!from.isAfter(to)) {
            buildWorkDay(from, groupEntity).ifPresent(result::add);
            from = from.plusDays(1);
        }

        return result;
    }

    private Optional<WorkDayEntity> buildWorkDay(LocalDate date, GroupEntity groupEntity) {
        String weekDay = WeekDay.getWeekDayByLocalDateValue(date.getDayOfWeek().toString()).getValue();
        Optional<WorkDayEntity> workDay = workRepository.findByGroupAndName(groupEntity, weekDay);
        if (workDay.isPresent()) {
            workDay.get().setDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());
            workDay.get().setLessons(getFilteredLessons(workDay.get(), date));
            return workDay;
        }
        return Optional.empty();
    }

    private List<TimeSlotEntity> getFilteredLessons(WorkDayEntity workDay, LocalDate date) {
        return workDay.getLessons().stream()
                .filter(timeSlotEntity -> Objects.isNull(timeSlotEntity.getEven()) || isDateEven(date) == timeSlotEntity.getEven())
                .collect(Collectors.toList());
    }

    private boolean isDateEven(LocalDate localDate) {
        return (ChronoUnit.WEEKS.between(LocalDate.of(LocalDate.now().getYear() - 1, 9, 1), localDate)) / 2 == 0;
    }


}
