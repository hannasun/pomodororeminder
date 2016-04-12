package com.hannasun.pomodororeminder.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hannasun.pomodororeminder.fragments.ReminderListFragment;

/**
 *
 */
public class TabFragmentAdapter extends FragmentPagerAdapter{

    String[] titles = new String[]{ "Reminder"};

    public TabFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch(position) {
            case 0:
                //fragment = new PomodoroClockFragment();
                fragment = new ReminderListFragment();
                break;
          /*  case 1:
                fragment = new ReminderListFragment();
                break;*/
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
