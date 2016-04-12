package com.hannasun.pomodororeminder.utils;

import com.hannasun.pomodororeminder.models.DateTimeSorter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by Administrator on 2016/4/12.
 */
public class DateTimeComparator implements Comparator{

    DateFormat f = new SimpleDateFormat("dd/mm/yyyy hh:mm");


    @Override
    public int compare(Object lhs, Object rhs) {
        String o1 = ((DateTimeSorter) lhs).getDateAndTime();
        String o2 = ((DateTimeSorter) rhs).getDateAndTime();
        try {
            return f.parse(o1).compareTo(f.parse(o2));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
