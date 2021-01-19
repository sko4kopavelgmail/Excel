package com.vorstu.excel.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "work_day")
public class WorkDayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeSlotEntity> lessons;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

    public WorkDayEntity(String name, List<TimeSlotEntity> lessons, GroupEntity group) {
        this.name = name;
        this.lessons = lessons;
        this.group = group;
    }
}
