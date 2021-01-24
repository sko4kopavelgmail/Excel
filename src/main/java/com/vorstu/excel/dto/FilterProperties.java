package com.vorstu.excel.dto;

import com.sun.istack.NotNull;


public class FilterProperties {

    @NotNull
    private String direction;

    @NotNull
    private String group;

    @NotNull
    private Long from;

    @NotNull
    private Long to;

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }
}
