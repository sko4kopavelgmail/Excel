package com.vorstu.excel.utils;

public enum WeekDays {

    MONDAY("ПОНЕДЕЛЬНИК"),
    TUESDAY("ВТОРНИК"),
    WEDNESDAY("СРЕДА"),
    THURSDAY("ЧЕТВЕРГ"),
    FRIDAY("ПЯТНИЦА"),
    SATURDAY("СУББОТА");

    private String value;

    WeekDays(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
