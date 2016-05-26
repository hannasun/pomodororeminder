package com.hannasun.pomodororeminder.widgets;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.TextView;

import com.hannasun.pomodororeminder.pomodoro.Pomodoro;

/**
 * Created by Administrator on 2016/4/26.
 */
public class CountDownView extends TextView {

    private long mUntil;
    private Handler mHandler;
    private Runnable mCallback;
   private int  mTomatoDuration;
    private boolean mPaused, mRunning;
   private  SharedPreferences prefs;
    public CountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHandler = new Handler();
        mCallback = new Runnable() {
            @Override
            public void run() {
                if(mRunning && !mPaused) {
                    CountDownView.this.tick();
                    mHandler.postDelayed(mCallback,1000);
                }
            }
        };
//  prefs = context.getSharedPreferences(Pomodoro.PREFERENCES, 0);
prefs = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void start(long durationMillis) {
        start(durationMillis, 0);
    }
    public void start(long durationMillis, long startTime) {
        startTime = (startTime == 0) ? System.currentTimeMillis() : startTime;
        mUntil = startTime + durationMillis;

        mPaused = false;
        mRunning = true;
        mHandler.postDelayed(mCallback, 0);
    }

    public void stop() {
        mTomatoDuration = prefs.getInt(Pomodoro.PREF_TOMATO_DURATION, Pomodoro.PREF_TOMATO_DURATION_DEFAULT);
        if(mTomatoDuration < 10)
            setText(String.valueOf(0)  + mTomatoDuration + ":00");
        else
        setText(mTomatoDuration + ":00");
        mRunning = false;
    }


    public void pause() {
        mPaused = true;
    }

    public void unpause() {
        mPaused = false;
        mHandler.postDelayed(mCallback, 0);
    }
    protected void tick() {
        long left = mUntil - System.currentTimeMillis();
        long min = Math.abs((long)(left / 60000));
        long sec = Math.abs((long)((left / 1000 - min * 60 )  % 60));
        if(min < 10) {
            if(sec < 10) {
                setText((left < 0 ? "-" : "") + "0" + min + ":0" + sec);
            } else
            setText((left < 0 ? "-" : "") + "0" + min +  ":" + sec);
        } else {
            if(sec < 10) {
                setText((left < 0 ? "-" : "")  + min + ":0" + sec);
            } else
            setText((left < 0 ? "-" : "")  + min + ":" + sec);
        }
    }
}
