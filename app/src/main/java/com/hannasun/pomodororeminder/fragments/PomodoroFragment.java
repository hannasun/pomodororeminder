package com.hannasun.pomodororeminder.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hannasun.pomodororeminder.R;
import com.hannasun.pomodororeminder.pomodoro.Pomodoro;
import com.hannasun.pomodororeminder.widgets.CountDownView;

import org.w3c.dom.Text;

/**
 * Created by Administrator on 2016/4/26.
 */
public class PomodoroFragment extends Fragment {


    private CountDownView mTimerView;
    private static ImageButton  mStopButton, mStartButton;

    private Context mContext;
    private SharedPreferences mPrefs;
   private static int mAlarmType, mTomatoCount;
    private static TextView mStatus;
    private static final int REQUEST_PREFERENCE = 1;

    public PomodoroFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pomodoro, container, false);
        mTimerView = (CountDownView) rootView.findViewById(R.id.tomato_clock);
        mStatus = (TextView) rootView.findViewById(R.id.working_status);
        mStartButton = (ImageButton)rootView.findViewById(R.id.tomato_start);
        mStopButton = (ImageButton) rootView.findViewById(R.id.tomato_stop);


        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mPrefs = mContext.getSharedPreferences(Pomodoro.PREFERENCES, 0);
                mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                int duration = mPrefs.getInt(Pomodoro.PREF_TOMATO_DURATION, Pomodoro.PREF_TOMATO_DURATION_DEFAULT);

                Pomodoro.startTomato(mContext);
                mTimerView.start(duration * 60000);
                mAlarmType = Pomodoro.ALARM_TYPE_TOMATO;
                updateButtonStatus();
                updateTextView();
//                TomatoCountFragment.update_tomato_count(mTomatoCount);
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Pomodoro.stopTomato(mContext);
                mTimerView.stop();

                if(mAlarmType == Pomodoro.ALARM_TYPE_REST)
                    mTomatoCount = Pomodoro.setTomatoCount(mContext, -1);

                mAlarmType = Pomodoro.ALARM_TYPE_NONE;
                updateButtonStatus();
                updateTextView();
            }
        });
            return rootView;
        }


        @Override
        public void onResume () {
            super.onResume();
            //mPrefs = mContext.getSharedPreferences(Pomodoro.PREFERENCES, 0);
            mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            long start = mPrefs.getLong(Pomodoro.PREF_ALARM_START, 0);
            long duration = mPrefs.getLong(Pomodoro.PREF_ALARM_DURATION, 0);
            mAlarmType = mPrefs.getInt(Pomodoro.PREF_ALARM_TYPE, 0);
            mTomatoCount = mPrefs.getInt(Pomodoro.PREF_TOMATO_COUNT, 0);

            mTimerView.stop();
            if (mAlarmType == Pomodoro.ALARM_TYPE_TOMATO) {
                mTimerView.start(duration, start);
            } else if (mAlarmType == Pomodoro.ALARM_TYPE_REST) {
                mTimerView.start(duration, start);
            }

            updateButtonStatus();
            updateTextView();
//        TomatoCountFragment.update_tomato_count(mTomatoCount);
        }

        @Override
        public void onDetach () {
            super.onDetach();
        }

    public static void updateTextView() {
        if (mAlarmType == Pomodoro.ALARM_TYPE_TOMATO) {
            mStatus.setText(R.string.status_tomato);
        } else if (mAlarmType == Pomodoro.ALARM_TYPE_REST) {
            mStatus.setText(R.string.status_rest);
        } else {
            mStatus.setText(R.string.start_tomato);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mTimerView.pause();
    }

    public  static void updateButtonStatus() {
        if (mAlarmType == Pomodoro.ALARM_TYPE_TOMATO | mAlarmType == Pomodoro.ALARM_TYPE_REST) {
            mStartButton.setVisibility(View.GONE);
            mStopButton.setVisibility(View.VISIBLE);
        } else {
            mStartButton.setVisibility(View.VISIBLE);
            mStopButton.setVisibility(View.GONE);
        }
    }




}
