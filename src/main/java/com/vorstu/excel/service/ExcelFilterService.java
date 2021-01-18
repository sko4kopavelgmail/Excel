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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExcelFilterService {

    private final WorkRepository workRepository;
    private final GroupRepo groupRepo;

    public List<WorkDayEntity> findFilteredWorkDays(FilterProperties filterProperties) {
        List<WorkDayEntity> result = new ArrayList<>();
        GroupEntity groupEntity = groupRepo.findByName(filterProperties.getGroup());

        while (!filterProperties.getFrom().isAfter(filterProperties.getTo())) {
            buildWorkDay(filterProperties.getFrom(), groupEntity).ifPresent(result::add);
            filterProperties.setFrom(filterProperties.getFrom().plusDays(1));
        }

        return result;
    }

    private Optional<WorkDayEntity> buildWorkDay(LocalDate date, GroupEntity groupEntity) {
        String weekDay = WeekDay.getWeekDayByLocalDateValue(date.getDayOfWeek().toString()).getValue();
        Optional<WorkDayEntity> workDay = workRepository.findByGroupAndName(groupEntity, weekDay);
        if (workDay.isPresent()) {
            workDay.get().setDate(date);
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
