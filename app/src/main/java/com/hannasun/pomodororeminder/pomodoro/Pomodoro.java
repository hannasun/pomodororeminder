package com.hannasun.pomodororeminder.pomodoro;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;

import com.hannasun.pomodororeminder.MainActivity;
import com.hannasun.pomodororeminder.R;
import com.hannasun.pomodororeminder.fragments.PomodoroFragment;

import javax.net.ssl.HandshakeCompletedListener;

/**
 * Created by Administrator on 2016/4/26.
 */
public class Pomodoro {

    public final static String ACTION_TOMATO_ALERT = "com.hannasun.pomodororeminder.pomodoro.action.TOMATO_ALARM";

    public final static String EXTRA_ALARM_START = "com.hannasun.pomodororeminder.pomodoro.extra.ALARM_START";
    public final static String EXTRA_ALARM_DURATION = "com.hannasun.pomodororeminder.pomodoro.extra.ALARM_DURATION";
    public final static String EXTRA_ALARM_TYPE = "com.hannasun.pomodororeminder.pomodoro.extra.ALARM_TYPE";

    public final static int ALARM_TYPE_NONE = 0;
    public final static int ALARM_TYPE_TOMATO = 1;
    public final static int ALARM_TYPE_REST = 2;


    public final static String PREFERENCES = "com.hannasun.pomodororeminder.pomodoro.pomodoro_preference";
    public final static String PREF_ALARM_TYPE = "alarm_type";
    public final static String PREF_ALARM_START = "alarm_start";
    public final static String PREF_ALARM_DURATION = "alarm_duration";
    public final static String PREF_TOMATO_COUNT = "tomato_count";

    public final static String PREF_NOTIFICATION_TIMER = "notification_timer";
    public final static boolean PREF_NOTIFICATION_TIMER_DEFAULT = true;

    public final static String PREF_TOMATO_DURATION = "tomato_duration";
    public final static int PREF_TOMATO_DURATION_DEFAULT = 25;

    public final static String PREF_TOMATO_RINGTONE = "tomato_ringtone";
    public final static String PREF_TOMATO_RINGTONE_DEFAULT = null;

    public final static String PREF_TOMATO_VIBRATE = "tomato_vibrate";
    public final static boolean PREF_TOMATO_VIBRATE_DEFAULT =false;

    public final static String PREF_TOMATO_VOLUME = "tomato_volume";
    public final static int PREF_TOMATO_VOLUME_DEFAULT = 100;

    public final static String PREF_TOMATO_DELAY = "tomato_delay";
    public final static int PREF_TOMATO_DELAY_DEFAULT = 0;


    public final static String PREF_REST_DURATION = "rest_duration";
    public final static int PREF_REST_DURATION_DEFAULT = 5;

    public final static String PREF_REST_RINGTONE = "rest_ringtone";
    public final static String PREF_REST_RINGTONE_DEFAULT = null;

    public final static String PREF_REST_VIBRATE = "rest_vibrate";
    public final static boolean PREF_REST_VIBRATE_DEFAULT = false;

    public final static String PREF_REST_VOLUME  = "rest_volume";
    public final static int PREF_REST_VOLUME_DEFAULT = 20;

    public final static String PREF_REST_DELAY = "rest_delay";
    public final static int PREF_REST_DELAY_DEFAULT = 0;


    public static void startTomato(Context context) {
        Pomodoro.pendingAlert(context, Pomodoro.ALARM_TYPE_TOMATO, 0, 0);
    }

    public static void startRest(Context context) {
        Pomodoro.pendingAlert(context, Pomodoro.ALARM_TYPE_REST, 0, 0);
    }

    public static void stopTomato(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);//context.getSharedPreferences(Pomodoro.PREFERENCES, 0);

        prefs.edit().putInt(Pomodoro.PREF_ALARM_TYPE, 0)
                    .putLong(Pomodoro.PREF_ALARM_DURATION, 0)
                    .putLong(Pomodoro.PREF_ALARM_START, 0).commit();

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ACTION_TOMATO_ALERT);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(sender);

        CountDownNotification notification = CountDownNotification.instance(context);

        notification.cancel();
    }

    static void pendingAlert(Context context, int type, long durationMillis, long startTime) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
       // SharedPreferences pref = context.getSharedPreferences(Pomodoro.PREFERENCES, 0);
       SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(durationMillis == 0) {
            if (type == Pomodoro.ALARM_TYPE_REST) {
                durationMillis = pref.getInt(Pomodoro.PREF_REST_DURATION, Pomodoro.PREF_REST_DURATION_DEFAULT) * 60000;
            } else {
                durationMillis = pref.getInt(Pomodoro.PREF_TOMATO_DURATION, Pomodoro.PREF_TOMATO_DURATION_DEFAULT) * 60000;
            }
        }
            if(startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            Intent intent = new Intent(ACTION_TOMATO_ALERT);
            intent.putExtra(EXTRA_ALARM_TYPE, type);
            intent.putExtra(EXTRA_ALARM_START, startTime);
            intent.putExtra(EXTRA_ALARM_DURATION, durationMillis);

            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            long when = startTime + durationMillis;
            am.set(AlarmManager.RTC_WAKEUP, when, sender);

            pref.edit().putInt(Pomodoro.PREF_ALARM_TYPE, type)
                    .putLong(Pomodoro.PREF_ALARM_START, startTime)
                    .putLong(Pomodoro.PREF_ALARM_DURATION, durationMillis).commit();

            if(pref.getBoolean(Pomodoro.PREF_NOTIFICATION_TIMER, Pomodoro.PREF_NOTIFICATION_TIMER_DEFAULT)) {
                CountDownNotification notification = CountDownNotification.instance(context);
                notification.start(type, when);
            }
        }

    public static int getTomatoCount(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Pomodoro.PREFERENCES, 0);
        return pref.getInt(Pomodoro.PREF_TOMATO_COUNT, 0);
    }
  public   static int setTomatoCount(Context context, int value) {
//        SharedPreferences pref = context.getSharedPreferences(Pomodoro.PREFERENCES, 0);
      SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int tomatoCount = pref.getInt(Pomodoro.PREF_TOMATO_COUNT, 0);
        tomatoCount = value < 0 ? Math.abs(value) + tomatoCount : value;
        pref.edit().putInt(Pomodoro.PREF_TOMATO_COUNT, tomatoCount).commit();
        return tomatoCount;
    }

    public static String formatTime(long when) {
        long left = when - System.currentTimeMillis();
        long min = Math.abs((long)(left / 60000));

        long sec = Math.abs((long)((left / 1000 - min * 60 )  % 60));

        if(min < 10) {
            if(sec < 10) {
                return ((left < 0 ? "-" : "") + "0" + min + ":0" + sec);
            } else
            return ((left < 0 ? "-" : "") + "0" + min +  ":" + sec);
        } else {
            if(sec < 10) {
                return ((left < 0 ? "-" : "")  + min + ":0" + sec);
            } else
            return ((left < 0 ? "-" : "")  + min + ":" + sec);
        }
    }

    public static class WakeLock {
        private static PowerManager.WakeLock sWakeLock;

        public static void acquire(Context context) {
            if(sWakeLock != null) {
                sWakeLock.release();
            }
            PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            sWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
            |PowerManager.ON_AFTER_RELEASE, "Pomodoro");
            sWakeLock.acquire();
        }

        static void release() {
            if(sWakeLock != null) {
                sWakeLock.release();
                sWakeLock = null;
            }
        }

    }

    public static class CountDownNotification extends Notification {
        private static final int NOTIFICATION_ID = 11235;

        private static CountDownNotification sInstance = null;

        private long mTickDuration = 1000;
        private boolean mTicking = false;
        private Handler mHandler;
        private Runnable mCallback;

        private Context mContext;
        private NotificationManager mNotifyManager;
        private PendingIntent mIntent;
        private long mWhen;
        private int mType;


        public static CountDownNotification instance(Context context) {
            if(sInstance == null) {
                sInstance = new CountDownNotification(context);
            }
            return sInstance;
        }

        private CountDownNotification(Context context) {
            super();

            mNotifyManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            mContext = context;
            mIntent = PendingIntent.getActivity(context, 0, new Intent(context,MainActivity.class), 0);
            this.flags = Notification.FLAG_ONGOING_EVENT;

            mHandler = new Handler();
            mCallback  = new Runnable() {
                @Override
                public void run() {
                      if(when < System.currentTimeMillis())
                          CountDownNotification.this.cancel();
                    if(mTicking) {
                        CountDownNotification.this.tick();
                        mHandler.postDelayed(mCallback, mTickDuration);
                    }
                }
            };
        }

        public void start(int type, long when) {
            mWhen = this.when = when;
            mType = type;
            mTicking = true;
            mHandler.postDelayed(mCallback, 0);
        }

        public void tick() {
            CountDownNotification.Builder builder = new CountDownNotification.Builder(mContext);

            String msg;

            if(mType == Pomodoro.ALARM_TYPE_TOMATO)
            msg = mContext.getString(R.string.status_tomato);
            else
                msg = mContext.getString(R.string.status_rest);

            msg  += " (" + Pomodoro.formatTime(mWhen) + ")";
            builder.setSmallIcon(R.mipmap.access_alarm_32px)
            .setContentTitle(mContext.getString(R.string.app_name))
            .setContentText(msg)
            .setContentIntent(mIntent);
            mNotifyManager.notify(NOTIFICATION_ID, builder.build());

        }

        public void cancel() {
            mTicking = false;
            mNotifyManager.cancel(NOTIFICATION_ID);
        }
    }

}
