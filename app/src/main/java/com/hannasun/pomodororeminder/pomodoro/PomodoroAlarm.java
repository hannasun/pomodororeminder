package com.hannasun.pomodororeminder.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hannasun.pomodororeminder.R;
import com.hannasun.pomodororeminder.fragments.PomodoroFragment;
import com.hannasun.pomodororeminder.fragments.TomatoCountFragment;
import com.hannasun.pomodororeminder.widgets.CountDownView;

/**
 * Created by Administrator on 2016/4/27.
 */
public class PomodoroAlarm extends AppCompatActivity{
    private CountDownView mTimerView;
    private AlarmHelper mAlarmHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alarm);

        Intent intent = getIntent();
        final int alarmType = intent.getIntExtra(Pomodoro.EXTRA_ALARM_TYPE, Pomodoro.ALARM_TYPE_TOMATO);
        long start = intent.getLongExtra(Pomodoro.EXTRA_ALARM_START, 0L);
        long duration = intent.getLongExtra(Pomodoro.EXTRA_ALARM_DURATION, 0L);

        TextView tv = (TextView) findViewById(R.id.alarm_message);
        Button btnContinue = (Button) findViewById(R.id.btn_continue);
        Button btnCancel = (Button)findViewById(R.id.btn_cancel);

        mTimerView = (CountDownView)findViewById(R.id.tomato_clock);

        if(mTimerView != null) {
            mTimerView.start(0, 0);
        }

        if(alarmType == Pomodoro.ALARM_TYPE_TOMATO) {
            tv.setText(R.string.alarm_message_tomato);
            btnContinue.setText(R.string.start_rest);
            btnCancel.setText(R.string.stop_working);
        } else if(alarmType == Pomodoro.ALARM_TYPE_REST) {
            tv.setText(R.string.alarm_message_rest);
            btnCancel.setText(R.string.stop_working);
            btnContinue.setText(R.string.start_tomato);
        }

        mAlarmHelper = AlarmHelper.instance(PomodoroAlarm.this);
         btnContinue.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 mAlarmHelper.stop();
                 if(alarmType == Pomodoro.ALARM_TYPE_TOMATO)
                     Pomodoro.startRest(PomodoroAlarm.this);
                 else {
                     Pomodoro.startTomato(PomodoroAlarm.this);
                     Pomodoro.setTomatoCount(PomodoroAlarm.this, -1);
                 }

                 finish();
             }
         });
     btnCancel.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             mAlarmHelper.stop();
             mTimerView.pause();
            Pomodoro.stopTomato(PomodoroAlarm.this);
             Pomodoro.setTomatoCount(PomodoroAlarm.this, -1);
             TomatoCountFragment.update_tomato_count();
             finish();
         }
     });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            mAlarmHelper.stop();
            Pomodoro.stopTomato(PomodoroAlarm.this);
            Pomodoro.setTomatoCount(PomodoroAlarm.this, -1);
           finish();
        }

        return super.onKeyDown(keyCode, event);
    }
}
