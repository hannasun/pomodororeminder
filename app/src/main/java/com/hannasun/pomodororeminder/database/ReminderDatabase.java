package com.hannasun.pomodororeminder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hannasun.pomodororeminder.models.Reminder;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ReminderDatabase extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    //Database Name
    private static final String DATABASE_NAME = "ReminderDatabase";
    //Table name
    private static final String TABLE_REMINDERS = "ReminderTable";

    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_REPEAT = "repeat";
    private static final String KEY_REPEAT_NO = "repeat_no";
    private static final String KEY_REPEAT_TYPE = "repeat_type";
    private static final String KEY_ACTIVE = "active";


    public ReminderDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_REMINDERS_TABLE = "CREATE TABLE " + TABLE_REMINDERS +
                "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME + " INTEGER,"
                + KEY_REPEAT + " BOOLEAN,"
                + KEY_REPEAT_NO + " INTEGER,"
                + KEY_REPEAT_TYPE + " TEXT,"
                + KEY_ACTIVE + " BOOLEAN" +
                ")";
        db.execSQL(CREATE_REMINDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
           if(oldVersion >= newVersion)
               return;
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
        onCreate(db);
    }

    public int addReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TITLE, reminder.getmTitle());
        values.put(KEY_DATE, reminder.getmDate());
        values.put(KEY_TIME, reminder.getmTime());
        values.put(KEY_REPEAT, reminder.getmRepeat());
        values.put(KEY_REPEAT_NO, reminder.getmRepeatNo());
        values.put(KEY_REPEAT_TYPE, reminder.getmRepeatType());
        values.put(KEY_ACTIVE, reminder.getmActive());
        long ID = db.insert(TABLE_REMINDERS, null, values);
        db.close();
        return (int)ID;
    }

    public Reminder getReminder(int id) {
        SQLiteDatabase db =this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_REMINDERS, new String[]
                {
                        KEY_ID,
                        KEY_TITLE,
                        KEY_DATE,
                        KEY_TIME,
                        KEY_REPEAT,
                        KEY_REPEAT_NO,
                        KEY_REPEAT_TYPE,
                        KEY_ACTIVE
                }, KEY_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);
        if(cursor != null)
            cursor.moveToFirst();

        Reminder reminder = new Reminder(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))),
                cursor.getString(cursor.getColumnIndex(KEY_TITLE)), cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                cursor.getString(cursor.getColumnIndex(KEY_TIME)), cursor.getString(cursor.getColumnIndex(KEY_REPEAT)),
                cursor.getString(cursor.getColumnIndex(KEY_REPEAT_NO)), cursor.getString(cursor.getColumnIndex(KEY_REPEAT_TYPE)),
                cursor.getString(cursor.getColumnIndex(KEY_ACTIVE)));
        cursor.close();
        return reminder;
    }

    public List<Reminder> getAllReminders() {
        List<Reminder> reminderList = new ArrayList<>();


        String selectQuery = "SELECT * FROM " + TABLE_REMINDERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor  = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do{
                Reminder reminder = new Reminder();
                reminder.setmID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
                reminder.setmTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                reminder.setmDate(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
                reminder.setmTime(cursor.getString(cursor.getColumnIndex(KEY_TIME)));
                reminder.setmRepeat(cursor.getString(cursor.getColumnIndex(KEY_REPEAT)));
                reminder.setmRepeatNo(cursor.getString(cursor.getColumnIndex(KEY_REPEAT_NO)));
                reminder.setmRepeatType(cursor.getString(cursor.getColumnIndex(KEY_REPEAT_TYPE)));
                reminder.setmActive(cursor.getString(cursor.getColumnIndex(KEY_ACTIVE)));

                reminderList.add(reminder);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return reminderList;
    }

    public int getRemindersCount() {
        String countQuery = "SELECT * FROM " + TABLE_REMINDERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    public int updateReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, reminder.getmTitle());
        values.put(KEY_DATE, reminder.getmDate());
        values.put(KEY_TIME, reminder.getmTime());
        values.put(KEY_REPEAT, reminder.getmRepeat());
        values.put(KEY_REPEAT_NO, reminder.getmRepeatNo());
        values.put(KEY_REPEAT_TYPE, reminder.getmRepeatType());
        values.put(KEY_ACTIVE, reminder.getmActive());

        return db.update(TABLE_REMINDERS, values, KEY_ID + "=?",
                new String[]{String.valueOf(reminder.getmID())});
    }

    public void deleteReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REMINDERS, KEY_ID + "=?",
                new String[]{String.valueOf(reminder.getmID())});
        db.close();
    }
}
