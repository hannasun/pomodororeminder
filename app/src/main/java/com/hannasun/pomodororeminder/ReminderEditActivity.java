package com.hannasun.pomodororeminder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hannasun.pomodororeminder.database.ReminderDatabase;
import com.hannasun.pomodororeminder.models.Reminder;
import com.hannasun.pomodororeminder.receivers.AlarmReceiver;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

/**
 *
 */
public class ReminderEditActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener{

    private Toolbar mToolbar;
    private EditText mTitleText;
    private TextView mDateText,mTimeText, mRepeatText, mRepeatNoText, mRepeatTypeText, mActiveText;
    private Switch mRepeatSwitch;
    private Switch mActiveSwitch;
    private String mTitle;
    private String mTime;
    private String mDate;
    private String mRepeat;
    private String mRepeatNo;
    private String mRepeatType;
    private String mActive;
    private String[] mDateSplit;
    private String[] mTimeSplit;
    private int mReceivedId;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mRepeatTime;
    private Calendar mCalendar;
    private Reminder mReceivedReminder;
    private ReminderDatabase mReminderDatabase;
    private AlarmReceiver mAlarmReceiver;

    //Constant Intent String
    public static final String EXTRA_REMINDER_ID = "Reminder_ID";

    //Values for orientation change
    private static final String KEY_TITLE = "title_key";
    private static final String KEY_TIME = "time_key";
    private static final String KEY_DATE = "date_key";
    private static final String KEY_REPEAT = "repeat_key";
    private static final String KEY_REPEAT_NO = "repeat_no_key";
    private static final String KEY_REPEAT_TYPE = "repeat_type_key";
    private static final String KEY_ACTIVE = "active_key";


    //Constant values in milliseconds
    private static final long milMinute = 60000L;
    private static final long milHour  = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder);

        //Initialize Views
        mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        mTitleText = (EditText) findViewById(R.id.reminder_title);
        mDateText = (TextView) findViewById(R.id.set_date_text);
        mTimeText = (TextView) findViewById(R.id.set_time_text);
        mRepeatText = (TextView) findViewById(R.id.set_repeat);
        mRepeatNoText = (TextView) findViewById(R.id.set_repeat_no);
        mRepeatTypeText = (TextView) findViewById(R.id.set_repeat_type);
        mActiveText = (TextView) findViewById(R.id.set_active_text);
        mRepeatSwitch = (Switch) findViewById(R.id.repeat_switch);
        mActiveSwitch = (Switch) findViewById(R.id.active_switch);

        //Setup Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.titleActivityEditReminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Setup Reminder Title EditText
        mTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle = s.toString().trim();
                mTitleText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Get reminder id from intent
        mReceivedId = Integer.parseInt(getIntent().getStringExtra(EXTRA_REMINDER_ID));

        //Get reminder using reminder id
        mReminderDatabase = new ReminderDatabase(this);
        mReceivedReminder = mReminderDatabase.getReminder(mReceivedId);

        //Get values from reminder
        mTitle = mReceivedReminder.getmTitle();
        mDate = mReceivedReminder.getmDate();
        mTime = mReceivedReminder.getmTime();
        mRepeat = mReceivedReminder.getmRepeat();
        mRepeatNo = mReceivedReminder.getmRepeatNo();
        mRepeatType = mReceivedReminder.getmRepeatType();
        mActive = mReceivedReminder.getmActive();

        //Setup TextViews using reminder values
        mTitleText.setText(mTitle);
        mDateText.setText(mDate);
        mTimeText.setText(mTime);
        mRepeatNoText.setText(mRepeatNo);
        mRepeatTypeText.setText(mRepeatType);
        mRepeatText.setText("每 " + mRepeatNo + " " + mRepeatType);
        mActiveText.setText("已开启提醒");
        //To save state on device rotation
        if (savedInstanceState != null) {
            String savedTitle = savedInstanceState.getString(KEY_TITLE);
            mTitleText.setText(savedTitle);
            mTitle = savedTitle;

            String savedTime = savedInstanceState.getString(KEY_TIME);
            mTimeText.setText(savedTime);
            mTime = savedTime;

            String savedDate = savedInstanceState.getString(KEY_DATE);
            mDateText.setText(savedDate);
            mDate = savedDate;

            String savedRepeat = savedInstanceState.getString(KEY_REPEAT);
            mRepeatText.setText(savedRepeat);
            mRepeat = savedRepeat;

            String savedRepeatNo = savedInstanceState.getString(KEY_REPEAT_NO);
            mRepeatNoText.setText(savedRepeatNo);
            mRepeatNo = savedRepeatNo;

            String savedRepeatType = savedInstanceState.getString(KEY_REPEAT_TYPE);
            mRepeatTypeText.setText(savedRepeatType);
            mRepeatType = savedRepeatType;

            String savedActive = savedInstanceState.getString(KEY_ACTIVE);
            mActiveText.setText(savedActive);
            mActive = savedActive;
        }


        //Setup repeate switch
        if (mRepeat.equals("false")) {
            mRepeatSwitch.setChecked(false);
            mRepeatText.setText("不重复");
        } else if (mRepeat.equals("true")) {
            mRepeatSwitch.setChecked(true);
        }

        if (mActive.equals("false")) {
            mActiveSwitch.setChecked(false);
            mActiveText.setText("已关闭提醒");
        } else if (mActive.equals("true")) {
            mActiveSwitch.setChecked(true);

        }

        //Obtain Date and Time details
        mCalendar = Calendar.getInstance();
        mAlarmReceiver = new AlarmReceiver();

        mDateSplit = mDate.split("/");
        mTimeSplit = mTime.split(":");

        mDay = Integer.parseInt(mDateSplit[0]);
        mMonth = Integer.parseInt(mDateSplit[1]);
        mYear = Integer.parseInt(mDateSplit[2]);

        mHour = Integer.parseInt(mTimeSplit[0]);
        mMinute = Integer.parseInt(mTimeSplit[1]);
    }

    //To save state on device rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(KEY_TITLE, mTitleText.getText());
        outState.putCharSequence(KEY_TIME, mTimeText.getText());
        outState.putCharSequence(KEY_DATE, mDateText.getText());
        outState.putCharSequence(KEY_REPEAT, mRepeatText.getText());
        outState.putCharSequence(KEY_REPEAT_NO, mRepeatNoText.getText());
        outState.putCharSequence(KEY_REPEAT_TYPE, mRepeatTypeText.getText());
        outState.putCharSequence(KEY_ACTIVE, mActiveText.getText());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear ++;
        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;
        mDate = dayOfMonth + "/" + monthOfYear + "/"+ year;
        mDateText.setText(mDate);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        mHour = hourOfDay;
        mMinute = minute;
        if(minute < 10) {
            mTime = hourOfDay + ":" + "0" + minute;
        } else {
            mTime = hourOfDay + ":" + minute;
        }
        mTimeText.setText(mTime);
    }

    //On Clicking Time picker
    public void setTime(View v) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false);

        tpd.setThemeDark(false);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    //On clicking Date picker
    public void setDate(View v) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    public void onSwitchRepeat(View view) {
        boolean on = ((Switch)view).isChecked();
        if(on) {
            mRepeat = "true";
            mRepeatText.setText("每 " + mRepeatNo +" " +  mRepeatType);
        } else {
            mRepeat ="false";
            mRepeatText.setText("不重复");
        }
    }

    public void onSwitchActive(View view) {

        boolean on = ((Switch)view).isChecked();
        if(on) {
            mActive = "true";
            mActiveText.setText("已开启提醒");
        } else {
            mActive = "false";
            mActiveText.setText("已关闭提醒");
        }

    }
    //On clicking repeat type button
    public void selectRepeatType(View v) {
        final String[] items = new String[5];

        items[0] = "分钟";
        items[1] = "小时";
        items[2] = "天";
        items[3] = "周";
        items[4] = "月";

        //Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择类型");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mRepeatType = items[which];
                mRepeatTypeText.setText(mRepeatType);
                mRepeatText.setText("每 " + mRepeatNo  + " " + mRepeatType);

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    //On clicking repeat interval button
    public void setRepeatNo(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("输入数字");

        //Create EditText box to input repeat number
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().length() == 0) {
                            mRepeatNo = Integer.toString(1);
                            mRepeatNoText.setText(mRepeatNo);
                            mRepeatText.setText("每 " + mRepeatNo  +" "+ mRepeatType);

                        } else {
                            mRepeatNo = input.getText().toString().trim();
                            mRepeatNoText.setText(mRepeatNo);
                            mRepeatText.setText("每 " + mRepeatNo + " " + mRepeatType + "(s)");
                        }
                    }
                });

        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing
            }
        });
        alert.show();
    }

    //On clicking the update button
    public void updateReminder() {
        //Set new values in the reminder
        mReceivedReminder.setmTitle(mTitle);
        mReceivedReminder.setmDate(mDate);
        mReceivedReminder.setmTime(mTime);
        mReceivedReminder.setmRepeat(mRepeat);
        mReceivedReminder.setmRepeatNo(mRepeatNo);
        mReceivedReminder.setmRepeatType(mRepeatType);
        mReceivedReminder.setmActive(mActive);

        //Update reminder
        mReminderDatabase.updateReminder(mReceivedReminder);

        //Setup calendar for creating the notification
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        //Canceling existing notification of the reminder by using its ID
        mAlarmReceiver.cancelAlarm(getApplicationContext(), mReceivedId);

        //Check repeat type
        if(mRepeatType.equals("分钟")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milMinute;
        } else if(mRepeatType.equals("小时")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milHour;
        } else if(mRepeatType.equals("天")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milDay;
        } else if(mRepeatType.equals("周")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milWeek;
        } else if(mRepeatType.equals("月")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milMonth;
        }

        //Create a new notification
        if(mActive.equals("true")) {
            if(mRepeat.equals("true")) {
                mAlarmReceiver.setRepeatAlarm(getApplicationContext(), mCalendar, mReceivedId, mRepeatTime);
            } else if(mRepeat.equals("false")) {
                mAlarmReceiver.setAlarm(getApplication(), mCalendar, mReceivedId);
            }
        }

        //Create toast to confirm updates
        Toast.makeText(getApplicationContext(), "已修改", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    //On pressing the back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //Creating the menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    //On clicking menu buttons

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //On clicking the back arrow
            //Discard any changes
            case android.R.id.home:
                onBackPressed();
                return true;

            //On clicking save button
            //Update reminder
            case R.id.save_reminder:
                mTitleText.setText(mTitle);

                if(mTitleText.getText().toString().length() == 0) {
                    CharSequence errorText = Html.fromHtml("<font color='white'>提醒内容不能为空！</font>");
                    mTitleText.setError(errorText);
                } else {
                    updateReminder();
                }
                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
