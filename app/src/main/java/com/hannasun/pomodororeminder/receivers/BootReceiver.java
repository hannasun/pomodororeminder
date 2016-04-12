package com.hannasun.pomodororeminder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hannasun.pomodororeminder.database.ReminderDatabase;
import com.hannasun.pomodororeminder.models.Reminder;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2016/4/11.
 */
public class BootReceiver extends BroadcastReceiver {

    private String mTitle;
    private String mTime;
    private String mDate;
    private String mRepeat;
    private String mRepeatNo;
    private String mRepeatType;
    private String mActive;
    private String[] mDateSplit;
    private String[] mTimeSplit;
    private int mYear, mMonth, mDay, mHour, mMinute, mReceivedId;
    private long mRepeatTime;


    private Calendar mCalendar;
    private AlarmReceiver mAlarmReceiver;

    //Constant values in milliseconds
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;


    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("android.intent.android.BOOT_COMPLETED")) {
            ReminderDatabase db = new ReminderDatabase(context);
            mCalendar = Calendar.getInstance();
            mAlarmReceiver = new AlarmReceiver();

            List<Reminder> reminders = db.getAllReminders();

            for(Reminder reminder : reminders) {
                mReceivedId = reminder.getmID();
                mRepeat = reminder.getmRepeat();
                mRepeatNo = reminder.getmRepeatNo();
                mRepeatType = reminder.getmRepeatType();
                mActive  = reminder.getmActive();
                mDate = reminder.getmDate();
                mTime = reminder.getmTime();

                mDateSplit = mDate.split("/");
                mTimeSplit = mTime.split(":");

                mDay = Integer.parseInt(mDateSplit[0]);
                mMonth = Integer.parseInt(mDateSplit[1]);
                mYear = Integer.parseInt(mDateSplit[2]);

                mHour = Integer.parseInt(mTimeSplit[0]);
                mMinute = Integer.parseInt(mTimeSplit[1]);

                mCalendar.set(Calendar.MONTH, --mMonth);
                mCalendar.set(Calendar.YEAR, mYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
                mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
                mCalendar.set(Calendar.MINUTE, mMinute);
                mCalendar.set(Calendar.SECOND, 0);

                //Cancel existing notification of the reminder by using its id
                //mAlarmReceiver.cancelAlarm(context, mReceivedId);

                //Ckeck repeat type
                if(mRepeatType.equals("Minute")) {
                    mRepeatTime = Integer.parseInt(mRepeatNo) * milMinute;
                } else if(mRepeatType.equals("Hour")) {
                    mRepeatTime = Integer.parseInt(mRepeatNo) * milHour;
                } else if(mRepeatType.equals("Day")) {
                    mRepeatTime = Integer.parseInt(mRepeatNo) * milDay;
                } else if(mRepeatType.equals("Week")) {
                    mRepeatTime = Integer.parseInt(mRepeatNo) * milMonth;
                }

                //Create a new notification
                if(mActive.equals("true")) {
                    if(mRepeat.equals("true")) {
                        mAlarmReceiver.setRepeatAlarm(context, mCalendar,mReceivedId, mRepeatTime);
                    } else if(mRepeat.equals("false")) {
                        mAlarmReceiver.setAlarm(context, mCalendar, mReceivedId);
                    }
                }
            }
        }
    }
}
