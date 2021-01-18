package com.vorstu.excel.utils;

public enum WeekDay {

    MONDAY("ПОНЕДЕЛЬНИК"),
    TUESDAY("ВТОРНИК"),
    WEDNESDAY("СРЕДА"),
    THURSDAY("ЧЕТВЕРГ"),
    FRIDAY("ПЯТНИЦА"),
    SATURDAY("СУББОТА"),
    SUNDAY("ВОСКРЕСЕНЬЕ");

    private String value;

    public String getValue() {
        return value;
    }

    WeekDay(String value) {
        this.value = value;
    }

    public static WeekDay getWeekDayByLocalDateValue(String localDateValue) {
        for (WeekDay weekDay : WeekDay.values()) {
            if (weekDay.toString().equalsIgnoreCase(localDateValue)) {
                return weekDay;
            }
        }
        throw new RuntimeException("Unable to find weekDay with provided string " + localDateValue);
    }

}
