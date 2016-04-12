package com.hannasun.pomodororeminder.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hannasun.pomodororeminder.R;
import com.hannasun.pomodororeminder.ReminderAddActivity;
import com.hannasun.pomodororeminder.ReminderEditActivity;
import com.hannasun.pomodororeminder.database.ReminderDatabase;
import com.hannasun.pomodororeminder.interfaces.ItemTouchHelperViewHolder;
import com.hannasun.pomodororeminder.interfaces.ItemTouchHolderAdapter;
import com.hannasun.pomodororeminder.models.DateTimeSorter;
import com.hannasun.pomodororeminder.models.Reminder;
import com.hannasun.pomodororeminder.receivers.AlarmReceiver;
import com.hannasun.pomodororeminder.utils.DateTimeComparator;
import com.hannasun.pomodororeminder.utils.ItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/4/10.
 */
public class ReminderListFragment extends Fragment {

    private ItemTouchHelper mItemTouchHelper;

    private RecyclerView mList;
    private TextView mNoReminderView;
    private FloatingActionButton mAddButton;
    private ReminderListAdapter mAdapter;
    private ReminderDatabase db;

    private int tempPos;
    //Store the id of reminders , the key is the position in recyclerView.
    private LinkedHashMap<Integer, Integer> idMap = new LinkedHashMap<>();
    private AlarmReceiver mAlarmReceiver;

    public ReminderListFragment(){

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        db = new ReminderDatabase(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.layout_reminder_list, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void initView(View view){
        mList = (RecyclerView) view.findViewById(R.id.reminder_list);
        mNoReminderView = (TextView) view.findViewById(R.id.no_reminder_text);
        mAddButton = (FloatingActionButton) view.findViewById(R.id.fab);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Reminder> mReminders = db.getAllReminders();

        if(mReminders.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
            mList.setVisibility(View.GONE);
        } else {
            mList.setVisibility(View.VISIBLE);
            mNoReminderView.setVisibility(View.GONE);
        }

        mList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter  =new ReminderListAdapter();
        mList.setAdapter(mAdapter);
        mAlarmReceiver = new AlarmReceiver();

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mList);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReminderAddActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAdapter.setItem();
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Reminder> mTest = db.getAllReminders();
        if(mTest.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);

        } else {
            mNoReminderView.setVisibility(View.GONE);
        }
        mAdapter.setItem();


    }
    class ReminderListAdapter extends RecyclerView.Adapter<ReminderListAdapter.ReminderViewHolder> implements ItemTouchHolderAdapter {

        private ArrayList<ReminderItem> mItems;
        private List<Integer> idList;

       public  ReminderListAdapter() {
          mItems = new ArrayList<>();
        }
        @Override
        public ReminderListAdapter.ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View root = inflater.inflate(R.layout.layout_recyclerview_item, parent, false);

            return new ReminderViewHolder(root);
        }

        @Override
        public void onBindViewHolder(ReminderListAdapter.ReminderViewHolder holder, int position) {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tempPos = mList.getChildAdapterPosition(v);
                    int mClickId = idMap.get(tempPos);
                    selectReminder(mClickId, v);
                }


            });


            ReminderItem item = mItems.get(position);
            holder.setTitleText(item.mTitle);
            holder.setDateAndTimeText(item.mDateTime);
            holder.setRepeatInfoText(item.mRepeat, item.mRepeatNo, item.mRepeatType);
            holder.setActiveImage(item.mActive);

        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public void onItemDismiss(int position) {

            if(mItems.isEmpty())
                return;

            int id = idMap.get(position);
            Reminder temp = db.getReminder(id);
            db.deleteReminder(temp);
            mItems.remove(position);
            notifyItemRemoved(position);
            mAlarmReceiver.cancelAlarm(getContext(), id);

            mAdapter.onDeleteItem();

            Toast.makeText(getContext(), "已删除！", Toast.LENGTH_SHORT).show();

            List<Reminder> list = db.getAllReminders();
            if(list.isEmpty()) {
                mNoReminderView.setVisibility(View.VISIBLE);
            } else {
                mNoReminderView.setVisibility(View.GONE);
            }

        }
        public void setItem () {
            mItems.clear();
            List<ReminderItem> items = generateData();

            mItems.addAll(items);
            mAdapter.notifyDataSetChanged();

        }

        private List<ReminderItem> generateData() {
            ArrayList<ReminderItem> items = new ArrayList<>();

            List<Reminder> reminders = db.getAllReminders();


            List<String> titles = new ArrayList<>();
            List<String> repeats = new ArrayList<>();
            List<String> repeatNos = new ArrayList<>();
            List<String> repeatTypes = new ArrayList<>();
            List<String> actives = new ArrayList<>();
            List<String> dateAndTime = new ArrayList<>();
            idList = new ArrayList<>();
            List<DateTimeSorter> dateTimeSortList = new ArrayList<>();

            for(Reminder r: reminders) {
                titles.add(r.getmTitle());
                repeats.add(r.getmRepeat());
                repeatNos.add(r.getmRepeatNo());
                repeatTypes.add(r.getmRepeatType());
                dateAndTime.add(r.getmDate() + " " + r.getmTime());
                actives.add(r.getmActive());
                idList.add(r.getmID());
            }


            for(int k= 0; k < titles.size(); k++) {
                dateTimeSortList.add(new DateTimeSorter(k, dateAndTime.get(k)));
            }

            //按照日期做升序排序
            Collections.sort(dateTimeSortList, new DateTimeComparator());
            int key = 0;
            //往recyclerView中的item添加数据
            for(DateTimeSorter sorter : dateTimeSortList) {
                int i = sorter.getIndex();

                items.add(new ReminderItem(titles.get(i),
                        dateAndTime.get(i),
                        repeats.get(i),
                        repeatNos.get(i),
                        repeatTypes.get(i),
                        actives.get(i)));
                idMap.put(key, idList.get(i));
                key ++;
            }

            return  items;
        }

        public void onDeleteItem() {
            mItems.clear();
            mItems.addAll(generateData());
        }

        class ReminderViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

            private TextView mTitleText, mDateAndTimeText, mRepeatInfoText;
            private ImageView mActiveImage;

            public ReminderViewHolder(View itemView) {
                super(itemView);
                mTitleText = (TextView) itemView.findViewById(R.id.recycler_title);
                mDateAndTimeText = (TextView) itemView.findViewById(R.id.recycler_date_time);
                mRepeatInfoText =(TextView)itemView.findViewById(R.id.recycler_repeat_info);
                mActiveImage = (ImageView)itemView.findViewById(R.id.active_image);

            }



            public void setTitleText(String text) {
                mTitleText.setText(text);
            }


            public void setDateAndTimeText(String dateTime) {
                mDateAndTimeText.setText(dateTime);
            }



            public void setRepeatInfoText(String repeat , String repeatNo, String  repeatType) {
                if(repeat.equals("true")) {
                    mRepeatInfoText.setText("每 " + repeatNo + " " + repeatType );

                } else if(repeat.equals("false")) {
                    mRepeatInfoText.setText("不重复");
                }
            }


            public void setActiveImage(String active) {
                if(active.equals("true")) {
                    mActiveImage.setImageResource(R.mipmap.audio_on_32px);
                } else if(active.equals("false")) {
                    mActiveImage.setImageResource(R.mipmap.audio_off_32px);
                }
            }

            @Override
            public void onItemSelected() {
                itemView.setBackgroundColor(Color.LTGRAY);
                Toast.makeText(getContext(), "往左滑动删除提醒！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClear() {
                itemView.setBackgroundColor(0);
            }
        }


        public class ReminderItem {
            public String mTitle;
            public String mDateTime;
            public String mRepeat;
            public String mRepeatNo;
            public String mRepeatType;
            public String mActive;

            public ReminderItem(String mTitle, String mDateTime, String mRepeat, String mRepeatNo, String mRepeatType, String mActive) {
                this.mTitle = mTitle;
                this.mDateTime = mDateTime;
                this.mRepeat = mRepeat;
                this.mRepeatNo = mRepeatNo;
                this.mRepeatType = mRepeatType;
                this.mActive = mActive;
            }


        }

    }



    private void selectReminder(int mClickId, View view) {
        String idString = Integer.toString(mClickId);

        Intent intent = new Intent(view.getContext(), ReminderEditActivity.class);
        intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, idString);
        startActivityForResult(intent, 1);
    }




}

