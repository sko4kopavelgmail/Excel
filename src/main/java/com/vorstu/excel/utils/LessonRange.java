package com.vorstu.excel.utils;

import org.apache.commons.math3.util.Pair;

import java.time.LocalTime;

public enum LessonRange {

    L_8(LocalTime.of(8, 0), Pair.create(LocalTime.of(8,0), LocalTime.of(9, 45)), 0),
    L_9(LocalTime.of(9, 45), Pair.create(LocalTime.of(9,45), LocalTime.of(11, 30)), 1),
    L_11(LocalTime.of(11, 30), Pair.create(LocalTime.of(11,30), LocalTime.of(13, 30)), 2),
    L_13(LocalTime.of(13, 30), Pair.create(LocalTime.of(13,30), LocalTime.of(15, 15)), 3),
    L_15(LocalTime.of(15, 15), Pair.create(LocalTime.of(15,15), LocalTime.of(17, 0)), 4),
    L_17(LocalTime.of(17, 0), Pair.create(LocalTime.of(17,0), LocalTime.of(18, 45)), 5),
    L_18(LocalTime.of(18, 45), Pair.create(LocalTime.of(18,45), LocalTime.of(19, 30)), 6);

    private LocalTime time;

    private Pair<LocalTime, LocalTime> range;

    private int index;

    LessonRange(LocalTime time, Pair<LocalTime, LocalTime> range, int index) {
        this.time = time;
        this.range = range;
        this.index = index;
    }

    public static LessonRange getRangeByTime(LocalTime localTime) {
        for (LessonRange value : LessonRange.values()) {
            if (localTime.equals(value.getTime())) {
                return value;
            }
        }
        throw new RuntimeException("Unable to find provided time");
    }

    public static LessonRange getNextLessonRange(int index) {
        for (LessonRange value : LessonRange.values()) {
            if (index+1 == value.getIndex()) {
                return value;
            }
        }
        throw new RuntimeException("Unable to find provided index");
    }

    public LocalTime getTime() {
        return time;
    }

    public Pair<LocalTime, LocalTime> getRange() {
        return range;
    }

    public int getIndex() {
        return index;
    }
}
