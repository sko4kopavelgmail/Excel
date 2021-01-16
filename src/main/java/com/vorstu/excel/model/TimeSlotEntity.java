package com.vorstu.excel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "time_slot")
@JsonIgnoreProperties({"startRowIndex", "endRowIndex", "group", "workDay"})
public class TimeSlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime startTime;

    private LocalTime endTime;

    private String value;

    private Boolean even;

    @ManyToOne
    @JoinColumn(name = "work_day_id", nullable = false)
    private WorkDayEntity workDay;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

    public TimeSlotEntity(LocalTime startTime, LocalTime endTime, String value, Boolean even, GroupEntity group) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.value = value;
        this.even = even;
        this.group = group;
    }
}
