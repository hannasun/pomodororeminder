package com.hannasun.pomodororeminder.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hannasun.pomodororeminder.R;
import com.hannasun.pomodororeminder.pomodoro.Pomodoro;

/**
 * Created by Administrator on 2016/4/27.
 */
public class TomatoCountFragment extends Fragment {

    private String TAG = TomatoCountFragment.class.getSimpleName();
    private static  SharedPreferences mPrefs;
    private static  int  mTomatoCount;
    private static int mAlarmType;
    private static ViewGroup rootView;
    private static Context mContext;
    private static TextView mCountStatus;
    private static Button mReset;
    private static LinearLayout ll_count;
    public TomatoCountFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tomato_count, container, false);
        mCountStatus = (TextView) rootView.findViewById(R.id.tv_status);
        mReset = (Button) rootView.findViewById(R.id.id_reset);
        ll_count = (LinearLayout)rootView.findViewById(R.id.ll_count);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mTomatoCount = mPrefs.getInt(Pomodoro.PREF_TOMATO_COUNT, 0);
        mAlarmType = mPrefs.getInt(Pomodoro.PREF_ALARM_TYPE, 0);

        if(mTomatoCount <= 0) {
            mReset.setVisibility(View.GONE);
        } else {
            mReset.setVisibility(View.VISIBLE);
            mReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTomatoCount = Pomodoro.setTomatoCount(mContext, 0);
                    update_tomato_count();
                    mReset.setVisibility(View.GONE);
                }
            });
        }
        update_tomato_count();
        return rootView;
    }

    public static void update_tomato_count(){
           rootView.removeAllViews();
        int i ;
        int c = 0;
        int tomatoCount = mPrefs.getInt(Pomodoro.PREF_TOMATO_COUNT, 0);

         rootView.addView(ll_count);
        LinearLayout holder =  null;
        for( i = 0; i < tomatoCount; i++) {
            if(c++ % 4 == 0) {
                holder = new LinearLayout(mContext);
                holder.setGravity(Gravity.CENTER);
                rootView.addView(holder);
            }
            ImageView iv = new ImageView(mContext);
            iv.setPadding(10, 0, 10, 0);
            iv.setImageResource(R.mipmap.tomato48px);
            holder.addView(iv);
        }
        if(c++ % 4 == 0) {
            holder = new LinearLayout(mContext);
            holder.setGravity(Gravity.CENTER);
            rootView.addView(holder);
        }
         if(mAlarmType == Pomodoro.ALARM_TYPE_REST) {
             ImageView iv = new ImageView(mContext);
             iv.setPadding(10, 0, 10, 0);
             iv.setImageResource(R.mipmap.tomato48px);
             holder.addView(iv);
         } else {
             c -=1;
         }
        if(c == 0) {
            mCountStatus.setText(R.string.no_count_text);
        } else {
            mCountStatus.setText(mContext.getText(R.string.count_text) + String.valueOf(c) +  "个番茄时间");
        }

        if(c <= 0) {
            mReset.setVisibility(View.GONE);
        } else {
            mReset.setVisibility(View.VISIBLE);
            mReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Pomodoro.stopTomato(mContext);
                    mTomatoCount= Pomodoro.setTomatoCount(mContext, 0);
                    update_tomato_count();
                    PomodoroFragment.updateTextView();
                    PomodoroFragment.updateButtonStatus();
                    mReset.setVisibility(View.GONE);
                }
            });
        }


    }



    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach");
    }
}
