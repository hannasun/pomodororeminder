package com.hannasun.pomodororeminder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.hannasun.pomodororeminder.adapters.TabFragmentAdapter;
import com.hannasun.pomodororeminder.preference.PomodoroPreference;

public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private TabLayout mTabs;
    private ViewPager mPagers;

    private ViewGroup mSetting ;
    private ViewGroup mSetTime;
    private ViewGroup mNotify;
    private ViewGroup mAbout;

    private static final int REQUEST_PREFERENCES=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up toolbar
        mToolbar = (Toolbar)findViewById(R.id.id_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //Initialize tab in pager
        mPagers = (ViewPager)findViewById(R.id.id_viewPager);
        mTabs = (TabLayout)findViewById(R.id.id_tabs);
        mDrawer = (DrawerLayout)findViewById(R.id.id_drawer);
        mPagers.setAdapter(new TabFragmentAdapter(getSupportFragmentManager()));

        //set up tabs
        mTabs.setTabTextColors(getResources().getColor(R.color.red),
                getResources().getColor(R.color.darkGreen));
        mTabs.setFillViewport(true);
        mTabs.setupWithViewPager(mPagers);
        mTabs.setTabGravity(TabLayout.GRAVITY_FILL);

        mToggle = new ActionBarDrawerToggle(this,
                mDrawer,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mToggle.syncState();
        mDrawer.setDrawerListener(mToggle);

        mSetting = (ViewGroup) findViewById(R.id.rl_settings);
//        mSetTime = (ViewGroup) findViewById(R.id.rl_timer);
//        mNotify = (ViewGroup) findViewById(R.id.rl_notify);
        mAbout = (ViewGroup) findViewById(R.id.rl_about);
    }


    public void onSetting(View v) {
        /*Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);*/

        Intent intent = new Intent(MainActivity.this, PomodoroPreference.class);
        startActivityForResult(intent, REQUEST_PREFERENCES);
    }



    public void onAbout(View view) {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }
}
