package com.example.son.democalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;

import java.util.List;

public class MainActivity extends BaseActivity {
    private CalendarLayout calendarLayout;
    private CalendarView calendarView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        calendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);
        calendarView = (CalendarView) findViewById(R.id.calendarView);


    }

    @Override
    protected void initData() {

    }

}
