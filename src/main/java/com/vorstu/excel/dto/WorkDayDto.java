package com.vorstu.excel.dto;

import java.util.List;

public class WorkDayDto {

    private Long id;

    private String name;

    private List<TimeSlotDto> lessons;

    private GroupDto group;

    public WorkDayDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TimeSlotDto> getLessons() {
        return lessons;
    }

    public void setLessons(List<TimeSlotDto> lessons) {
        this.lessons = lessons;
    }

    public GroupDto getGroup() {
        return group;
    }

    public void setGroup(GroupDto group) {
        this.group = group;
    }
}
