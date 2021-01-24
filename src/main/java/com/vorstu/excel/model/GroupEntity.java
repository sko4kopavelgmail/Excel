package com.vorstu.excel.model;

import javax.persistence.*;

@Entity
@Table(name = "group_entity")
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int startColumnIndex;

    private int endColumnIndex;

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

    public int getStartColumnIndex() {
        return startColumnIndex;
    }

    public void setStartColumnIndex(int startColumnIndex) {
        this.startColumnIndex = startColumnIndex;
    }

    public int getEndColumnIndex() {
        return endColumnIndex;
    }

    public void setEndColumnIndex(int endColumnIndex) {
        this.endColumnIndex = endColumnIndex;
    }

    public GroupEntity() {
    }

    public GroupEntity(String name, int startColumnIndex, int endColumnIndex) {
        this.name = name;
        this.startColumnIndex = startColumnIndex;
        this.endColumnIndex = endColumnIndex;
    }
}
