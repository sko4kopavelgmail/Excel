package com.vorstu.excel.dto;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterProperties {

    @NotNull
    private String direction;

    @NotNull
    private String group;

    @NotNull
    private Long from;

    @NotNull
    private Long to;

}
