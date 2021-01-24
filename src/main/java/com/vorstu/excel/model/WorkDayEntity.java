package com.vorstu.excel.model;

import javax.persistence.*;
import java.util.List;

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

    public List<TimeSlotEntity> getLessons() {
        return lessons;
    }

    public void setLessons(List<TimeSlotEntity> lessons) {
        this.lessons = lessons;
    }

    public GroupEntity getGroup() {
        return group;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }

    public WorkDayEntity() {
    }

    public WorkDayEntity(String name, List<TimeSlotEntity> lessons, GroupEntity group) {
        this.name = name;
        this.lessons = lessons;
        this.group = group;
    }
}
