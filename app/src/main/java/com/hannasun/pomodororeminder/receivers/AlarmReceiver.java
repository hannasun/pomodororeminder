package com.hannasun.pomodororeminder.receivers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.hannasun.pomodororeminder.R;
import com.hannasun.pomodororeminder.ReminderEditActivity;
import com.hannasun.pomodororeminder.database.ReminderDatabase;
import com.hannasun.pomodororeminder.models.Reminder;

import java.util.Calendar;

/**
 *
 */
public class AlarmReceiver extends WakefulBroadcastReceiver{

    AlarmManager mAlarmManager;
    PendingIntent mPendingIntent;
    NotificationManager mManager;
    NotificationCompat.Builder mBuilder;

    @Override
    public void onReceive(Context context, Intent intent) {

        int mReceivedId = Integer.parseInt(intent.getStringExtra(ReminderEditActivity.EXTRA_REMINDER_ID));

        //Get notification title from Reminder Database
        ReminderDatabase mReminderDatabase = new ReminderDatabase(context);
        Reminder reminder = mReminderDatabase.getReminder(mReceivedId);
        String mTitle = reminder.getmTitle();

        //Create intent to open ReminderEditActivity on notification click
        Intent editIntent =  new Intent(context, ReminderEditActivity.class);
        editIntent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(mReceivedId));
        editIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent mClick = PendingIntent.getActivity(context, mReceivedId, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Create a notification
        mBuilder = new NotificationCompat.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.access_alarm_48px))
                .setSmallIcon(R.mipmap.access_alarm_32px)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setTicker(mTitle)
                .setContentText(mTitle)
                .setContentIntent(mClick)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_VIBRATE);
        mManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.notify(mReceivedId, mBuilder.build());

    }

    public void setAlarm(Context context, Calendar calendar,int id) {
        mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        //Put reminder id in intent extra
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(id));
        mPendingIntent = PendingIntent.getBroadcast(context, id, intent,PendingIntent.FLAG_CANCEL_CURRENT );

        //Caculate notification time
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;

        //Start alarm using notification time
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime,
                mPendingIntent);

        //Restart alarm if device is rebooted
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }

    public void setRepeatAlarm(Context context, Calendar calendar, int id, long repeatTime) {
        mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        //Put reminder id in intent extra
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(id));
        mPendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //Caculate notification time
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;

        //Start alarm using initial notification time and repeat interval time
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime,
                repeatTime,
                mPendingIntent);

        //Restart alarm if device is rebooted
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }


    public void cancelAlarm(Context context, int id) {
        mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Cancel alarm using reminder id
        mPendingIntent = PendingIntent.getBroadcast(context, id,
                new Intent(context, AlarmReceiver.class),
                0);
        mAlarmManager.cancel(mPendingIntent);

        //Disable alarm
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        mManager.cancel(id);
    }
}
