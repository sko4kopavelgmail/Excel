package com.vorstu.excel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "group_entity")
@JsonIgnoreProperties({"startColumnIndex", "endColumnIndex"})
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int startColumnIndex;

    private int endColumnIndex;

    public GroupEntity(String name, int startColumnIndex, int endColumnIndex) {
        this.name = name;
        this.startColumnIndex = startColumnIndex;
        this.endColumnIndex = endColumnIndex;
    }
}
