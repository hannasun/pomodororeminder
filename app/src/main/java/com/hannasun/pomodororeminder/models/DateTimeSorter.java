package com.hannasun.pomodororeminder.models;

/**
 * Created by Administrator on 2016/4/12.
 */
public class DateTimeSorter {
    private int index;
    private String dateAndTime;

    public DateTimeSorter() {
    }

    public DateTimeSorter(int index, String dateAndTime) {
        this.index = index;
        this.dateAndTime = dateAndTime;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }
}
