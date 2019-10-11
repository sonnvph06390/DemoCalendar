package com.example.son.democalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;

import java.util.List;

public class MainActivity extends BaseActivity implements CalendarView.OnCalendarSelectListener,
        View.OnClickListener {
    private CalendarLayout calendarLayout;
    private CalendarView calendarView;
    private TextView tvToday;
    private TextView tvLunarToday;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        calendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        tvToday = (TextView) findViewById(R.id.tvToday);
        tvLunarToday = findViewById(R.id.tvLunarToday);
        tvToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        tvToday.setText(" Dương lịch " + calendarView.getCurDay() + " tháng " + calendarView.getCurMonth() + " năm " + calendarView.getCurYear());
        calendarView.setOnCalendarSelectListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        tvToday.setText("Dương lịch " + calendar.getDay() + " tháng " + calendar.getMonth() + " năm " + calendar.getYear());
        tvLunarToday.setText("Âm lịch " + calendar.getLunarCalendar().getDay() + " tháng " + calendar.getLunarCalendar().getMonth() + " năm " +
                calendar.getLunarCalendar().getYear());
        if (isClick) {
            Toast.makeText(this, getCalendarText(calendar), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {

    }

    private static String getCalendarText(Calendar calendar) {
        return String.format("Lịch:%s \n Âm lịch %s \n Dương lịch：%s \n Tết âm lịch：%s \n Tiết khí：%s \n 是否闰月：%s",
                calendar.getMonth() + "ngày " + calendar.getDay() + " tháng",
                 "ngày " + calendar.getLunarCalendar().getDay() + " tháng "+calendar.getLunarCalendar().getMonth(),
                TextUtils.isEmpty(calendar.getGregorianFestival()) ? "Không" : calendar.getGregorianFestival(),
                TextUtils.isEmpty(calendar.getTraditionFestival()) ? "Không" : calendar.getTraditionFestival(),
                TextUtils.isEmpty(calendar.getSolarTerm()) ? "Không" : calendar.getSolarTerm(),
                calendar.getLeapMonth() == 0 ? "Không" : String.format("Nhuận %s tháng", calendar.getLeapMonth()));
    }
}
