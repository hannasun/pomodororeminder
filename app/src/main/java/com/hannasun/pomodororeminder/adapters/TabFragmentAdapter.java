package com.hannasun.pomodororeminder.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hannasun.pomodororeminder.fragments.PomodoroFragment;
import com.hannasun.pomodororeminder.fragments.ReminderListFragment;
import com.hannasun.pomodororeminder.fragments.TomatoCountFragment;

/**
 *
 */
public class TabFragmentAdapter extends FragmentPagerAdapter{

    String[] titles = new String[]{ "番茄时钟", "番茄数", "提醒列表"};

    public TabFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch(position) {
            case 0:
                //fragment = new PomodoroClockFragment();
                fragment = new PomodoroFragment();
                break;
            case 1:
                fragment = new TomatoCountFragment();
                break;
            case 2:
                fragment = new ReminderListFragment();
                break;


            default:
                fragment = null;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
