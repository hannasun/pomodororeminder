package com.hannasun.pomodororeminder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.hannasun.pomodororeminder.pomodoro.AlarmHelper;
import com.hannasun.pomodororeminder.pomodoro.Pomodoro;
import com.hannasun.pomodororeminder.pomodoro.PomodoroAlarm;

/**
 * Created by Administrator on 2016/4/27.
 */
public class PomodoroReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        long now = System.currentTimeMillis();
        int alarmType = intent.getIntExtra(Pomodoro.EXTRA_ALARM_TYPE, 0);
        long duration = intent.getLongExtra(Pomodoro.EXTRA_ALARM_DURATION, 0);
        long start = intent.getLongExtra(Pomodoro.EXTRA_ALARM_TYPE, 0);

        //Load setting
   SharedPreferences prefs = context.getSharedPreferences(Pomodoro.PREFERENCES, 0);
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Uri uri = null;
        int timeout = 1;
        boolean vibrate = false;
        String tone;
        int volume = 100;
        int delay = 0;

        if(alarmType == Pomodoro.ALARM_TYPE_TOMATO) {
            vibrate = prefs.getBoolean(Pomodoro.PREF_TOMATO_VIBRATE, Pomodoro.PREF_TOMATO_VIBRATE_DEFAULT);
            tone = prefs.getString(Pomodoro.PREF_TOMATO_RINGTONE, Pomodoro.PREF_TOMATO_RINGTONE_DEFAULT);
            if(tone != null)
                uri = Uri.parse(tone);
            volume = prefs.getInt(Pomodoro.PREF_TOMATO_VOLUME, Pomodoro.PREF_TOMATO_VOLUME_DEFAULT);
        } else if(alarmType == Pomodoro.ALARM_TYPE_REST) {
            vibrate = prefs.getBoolean(Pomodoro.PREF_REST_VIBRATE, Pomodoro.PREF_REST_VIBRATE_DEFAULT);
            tone = prefs.getString(Pomodoro.PREF_REST_RINGTONE, Pomodoro.PREF_REST_RINGTONE_DEFAULT);
            if(tone != null)
                uri = Uri.parse(tone);
            volume = prefs.getInt(Pomodoro.PREF_REST_VOLUME, Pomodoro.PREF_REST_VOLUME_DEFAULT);

        }

        if(uri == null)
            uri = Settings.System.DEFAULT_RINGTONE_URI;

        //Wake device
        Pomodoro.WakeLock.acquire(context);

        //Start alarm
        AlarmHelper alarmHelper = AlarmHelper.instance(context);
        alarmHelper.play(uri, vibrate, timeout* 60000, volume, delay);

        //Launch ui
        Intent fireAlarm = new Intent(context, PomodoroAlarm.class);
        fireAlarm.putExtra(Pomodoro.EXTRA_ALARM_TYPE, alarmType);
        fireAlarm.putExtra(Pomodoro.EXTRA_ALARM_DURATION, duration);
        fireAlarm.putExtra(Pomodoro.EXTRA_ALARM_START, start);

        fireAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(fireAlarm);

    }
}
