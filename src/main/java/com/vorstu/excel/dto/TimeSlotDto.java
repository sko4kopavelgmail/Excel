package com.vorstu.excel.dto;

import java.time.LocalTime;


public class TimeSlotDto {

    private Long id;

    private LocalTime startTime;

    private Long startLongTime;

    private LocalTime endTime;

    private Long endLongTime;

    private String value;

    private Boolean odd;

    public TimeSlotDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Long getStartLongTime() {
        return startLongTime;
    }

    public void setStartLongTime(Long startLongTime) {
        this.startLongTime = startLongTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Long getEndLongTime() {
        return endLongTime;
    }

    public void setEndLongTime(Long endLongTime) {
        this.endLongTime = endLongTime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getOdd() {
        return odd;
    }

    public void setOdd(Boolean odd) {
        this.odd = odd;
    }
}
