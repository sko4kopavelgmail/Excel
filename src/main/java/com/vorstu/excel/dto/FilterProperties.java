package com.vorstu.excel.dto;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FilterProperties {

    @NotNull
    private String direction;

    @NotNull
    private String group;

    @NotNull
    private LocalDate from;

    @NotNull
    private LocalDate to;

}
