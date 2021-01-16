package com.vorstu.excel.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FilterProperties {

    private String direction;

    private String group;

    private LocalDate from;

    private LocalDate to;

}
