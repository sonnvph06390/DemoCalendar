/*
 * Copyright (C) 2016 huanghaibin_dev <huanghaibin_dev@163.com>
 * WebSite https://github.com/MiracleTimes-Dev
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.haibin.calendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * Chế độ xem năm
 * Created by huanghaibin on 2018/10/9.
 */
@SuppressWarnings("unused")
public abstract class YearView extends View {

    CalendarViewDelegate mDelegate;

    /**
     * Bút ngày tháng hiện tại
     */
    protected Paint mCurMonthTextPaint = new Paint();

    /**
     * Màu tháng khác
     */
    protected Paint mOtherMonthTextPaint = new Paint();

    /**
     * Màu văn bản tháng hiện tại
     */
    protected Paint mCurMonthLunarTextPaint = new Paint();

    /**
     * Màu văn bản tháng hiện tại
     */
    protected Paint mSelectedLunarTextPaint = new Paint();

    /**
     * Màu văn bản tháng khác
     */
    protected Paint mOtherMonthLunarTextPaint = new Paint();

    /**
     * Màu văn bản tháng khác
     */
    protected Paint mSchemeLunarTextPaint = new Paint();

    /**
     * Đánh dấu ngày màu nền bàn chải
     */
    protected Paint mSchemePaint = new Paint();

    /**
     * Màu nền được chọn
     */
    protected Paint mSelectedPaint = new Paint();

    /**
     * Đánh dấu văn bản
     */
    protected Paint mSchemeTextPaint = new Paint();

    /**
     * Đánh dấu văn bản được chọn
     */
    protected Paint mSelectTextPaint = new Paint();

    /**
     * Đánh dấu màu văn bản ngày hiện tại
     */
    protected Paint mCurDayTextPaint = new Paint();

    /**
     * Đánh dấu màu văn bản ngày âm hiện tại
     */
    protected Paint mCurDayLunarTextPaint = new Paint();

    /**
     * Đánh dấu tháng
     */
    protected Paint mMonthTextPaint = new Paint();

    /**
     * Đánh dấu hàng tuần
     */
    protected Paint mWeekTextPaint = new Paint();

    /**
     * Mục lịch
     */
    List<Calendar> mItems;

    /**
     * Chiều cao của mỗi món đồ
     */
    protected int mItemHeight;

    /**
     * Chiều rộng của mỗi mục
     */
    protected int mItemWidth;

    /**
     * Đường cơ sở của văn bản
     */
    protected float mTextBaseLine;

    /**
     * Đường cơ sở của văn bản
     */
    protected float mMonthTextBaseLine;

    /**
     * Đường cơ sở của văn bản
     */
    protected float mWeekTextBaseLine;

    /**
     * Năm hiện tại thẻ lịch
     */
    protected int mYear;

    /**
     * Tháng hiện tại thẻ lịch
     */
    protected int mMonth;

    /**
     * Số lần bù vào tháng tới
     */
    protected int mNextDiff;

    /**
     * Tuần bắt đầu
     */
    protected int mWeekStart;

    /**
     * Số lượng hàng trong lịch
     */
    protected int mLineCount;

    public YearView(Context context) {
        this(context, null);
    }

    public YearView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }


    /**
     * Cấu hình ban đầu
     */
    private void initPaint() {
        mCurMonthTextPaint.setAntiAlias(true);
        mCurMonthTextPaint.setTextAlign(Paint.Align.CENTER);
        mCurMonthTextPaint.setColor(0xFF111111);
        mCurMonthTextPaint.setFakeBoldText(true);

        mOtherMonthTextPaint.setAntiAlias(true);
        mOtherMonthTextPaint.setTextAlign(Paint.Align.CENTER);
        mOtherMonthTextPaint.setColor(0xFFe1e1e1);
        mOtherMonthTextPaint.setFakeBoldText(true);

        mCurMonthLunarTextPaint.setAntiAlias(true);
        mCurMonthLunarTextPaint.setTextAlign(Paint.Align.CENTER);

        mSelectedLunarTextPaint.setAntiAlias(true);
        mSelectedLunarTextPaint.setTextAlign(Paint.Align.CENTER);

        mOtherMonthLunarTextPaint.setAntiAlias(true);
        mOtherMonthLunarTextPaint.setTextAlign(Paint.Align.CENTER);

        mMonthTextPaint.setAntiAlias(true);
        mMonthTextPaint.setFakeBoldText(true);

        mWeekTextPaint.setAntiAlias(true);
        mWeekTextPaint.setFakeBoldText(true);
        mWeekTextPaint.setTextAlign(Paint.Align.CENTER);

        mSchemeLunarTextPaint.setAntiAlias(true);
        mSchemeLunarTextPaint.setTextAlign(Paint.Align.CENTER);

        mSchemeTextPaint.setAntiAlias(true);
        mSchemeTextPaint.setStyle(Paint.Style.FILL);
        mSchemeTextPaint.setTextAlign(Paint.Align.CENTER);
        mSchemeTextPaint.setColor(0xffed5353);
        mSchemeTextPaint.setFakeBoldText(true);

        mSelectTextPaint.setAntiAlias(true);
        mSelectTextPaint.setStyle(Paint.Style.FILL);
        mSelectTextPaint.setTextAlign(Paint.Align.CENTER);
        mSelectTextPaint.setColor(0xffed5353);
        mSelectTextPaint.setFakeBoldText(true);

        mSchemePaint.setAntiAlias(true);
        mSchemePaint.setStyle(Paint.Style.FILL);
        mSchemePaint.setStrokeWidth(2);
        mSchemePaint.setColor(0xffefefef);

        mCurDayTextPaint.setAntiAlias(true);
        mCurDayTextPaint.setTextAlign(Paint.Align.CENTER);
        mCurDayTextPaint.setColor(Color.RED);
        mCurDayTextPaint.setFakeBoldText(true);

        mCurDayLunarTextPaint.setAntiAlias(true);
        mCurDayLunarTextPaint.setTextAlign(Paint.Align.CENTER);
        mCurDayLunarTextPaint.setColor(Color.RED);
        mCurDayLunarTextPaint.setFakeBoldText(true);

        mSelectedPaint.setAntiAlias(true);
        mSelectedPaint.setStyle(Paint.Style.FILL);
        mSelectedPaint.setStrokeWidth(2);
    }

    /**
     * Cài đặt
     *
     * @param delegate delegate
     */
    final void setup(CalendarViewDelegate delegate) {
        this.mDelegate = delegate;
        updateStyle();
    }

    final void updateStyle() {
        if (mDelegate == null) {
            return;
        }
        this.mCurMonthTextPaint.setTextSize(mDelegate.getYearViewDayTextSize());
        this.mSchemeTextPaint.setTextSize(mDelegate.getYearViewDayTextSize());
        this.mOtherMonthTextPaint.setTextSize(mDelegate.getYearViewDayTextSize());
        this.mCurDayTextPaint.setTextSize(mDelegate.getYearViewDayTextSize());
        this.mSelectTextPaint.setTextSize(mDelegate.getYearViewDayTextSize());

        this.mSchemeTextPaint.setColor(mDelegate.getYearViewSchemeTextColor());
        this.mCurMonthTextPaint.setColor(mDelegate.getYearViewDayTextColor());
        this.mOtherMonthTextPaint.setColor(mDelegate.getYearViewDayTextColor());
        this.mCurDayTextPaint.setColor(mDelegate.getYearViewCurDayTextColor());
        this.mSelectTextPaint.setColor(mDelegate.getYearViewSelectTextColor());
        this.mMonthTextPaint.setTextSize(mDelegate.getYearViewMonthTextSize());
        this.mMonthTextPaint.setColor(mDelegate.getYearViewMonthTextColor());
        this.mWeekTextPaint.setColor(mDelegate.getYearViewWeekTextColor());
        this.mWeekTextPaint.setTextSize(mDelegate.getYearViewWeekTextSize());
    }

    /**
     * Xem năm khởi tạo
     *
     * @param year  year
     * @param month month
     */
    final void init(int year, int month) {
        mYear = year;
        mMonth = month;
        mNextDiff = CalendarUtil.getMonthEndDiff(mYear, mMonth, mDelegate.getWeekStart());
        int preDiff = CalendarUtil.getMonthViewStartDiff(mYear, mMonth, mDelegate.getWeekStart());

        mItems = CalendarUtil.initCalendarForMonthView(mYear, mMonth, mDelegate.getCurrentDay(), mDelegate.getWeekStart());

        mLineCount = 6;
        addSchemesFromMap();

    }

    /**
     * Đo kích thước
     *
     * @param width  width
     * @param height height
     */
    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    final void measureSize(int width, int height) {

        Rect rect = new Rect();
        mCurMonthTextPaint.getTextBounds("1", 0, 1, rect);
        int textHeight = rect.height();
        int mMinHeight = 12 * textHeight + getMonthViewTop();

        int h = height >= mMinHeight ? height : mMinHeight;

        getLayoutParams().width = width;
        getLayoutParams().height = h;
        mItemHeight = (h - getMonthViewTop()) / 6;

        Paint.FontMetrics metrics = mCurMonthTextPaint.getFontMetrics();
        mTextBaseLine = mItemHeight / 2 - metrics.descent + (metrics.bottom - metrics.top) / 2;

        Paint.FontMetrics monthMetrics = mMonthTextPaint.getFontMetrics();
        mMonthTextBaseLine = mDelegate.getYearViewMonthHeight() / 2 - monthMetrics.descent +
                (monthMetrics.bottom - monthMetrics.top) / 2;

        Paint.FontMetrics weekMetrics = mWeekTextPaint.getFontMetrics();
        mWeekTextBaseLine = mDelegate.getYearViewWeekHeight() / 2 - weekMetrics.descent +
                (weekMetrics.bottom - weekMetrics.top) / 2;

        invalidate();
    }

    /**
     * Thêm thẻ sự kiện từ Bản đồ
     */
    private void addSchemesFromMap() {
        if (mDelegate.mSchemeDatesMap == null || mDelegate.mSchemeDatesMap.size() == 0) {
            return;
        }
        for (Calendar a : mItems) {
            if (mDelegate.mSchemeDatesMap.containsKey(a.toString())) {
                Calendar d = mDelegate.mSchemeDatesMap.get(a.toString());
                if (d == null) {
                    continue;
                }
                a.setScheme(TextUtils.isEmpty(d.getScheme()) ? mDelegate.getSchemeText() : d.getScheme());
                a.setSchemeColor(d.getSchemeColor());
                a.setSchemes(d.getSchemes());
            } else {
                a.setScheme("");
                a.setSchemeColor(0);
                a.setSchemes(null);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mItemWidth = (getWidth() - 2 * mDelegate.getYearViewPadding()) / 7;
        onPreviewHook();
        onDrawMonth(canvas);
        onDrawWeek(canvas);
        onDrawMonthView(canvas);
    }

    /**
     * Vẽ
     *
     * @param canvas canvas
     */
    private void onDrawMonth(Canvas canvas) {
        onDrawMonth(canvas,
                mYear, mMonth,
                mDelegate.getYearViewPadding(),
                mDelegate.getYearViewMonthMarginTop(),
                getWidth() - 2 * mDelegate.getYearViewPadding(),
                mDelegate.getYearViewMonthHeight() +
                        mDelegate.getYearViewMonthMarginTop());
    }

    private int getMonthViewTop() {
        return mDelegate.getYearViewMonthMarginTop() +
                mDelegate.getYearViewMonthHeight() +
                mDelegate.getYearViewMonthMarginBottom() +
                mDelegate.getYearViewWeekHeight();
    }

    /**
     * Vẽ
     *
     * @param canvas canvas
     */
    private void onDrawWeek(Canvas canvas) {
        if (mDelegate.getYearViewWeekHeight() <= 0) {
            return;
        }
        int week = mDelegate.getWeekStart();
        if (week > 0) {
            week -= 1;
        }
        int width = (getWidth() - 2 * mDelegate.getYearViewPadding()) / 7;
        for (int i = 0; i < 7; i++) {
            onDrawWeek(canvas,
                    week,
                    mDelegate.getYearViewPadding() + i * width,
                    mDelegate.getYearViewMonthHeight() +
                            mDelegate.getYearViewMonthMarginTop() +
                            mDelegate.getYearViewMonthMarginBottom(),
                    width,
                    mDelegate.getYearViewWeekHeight());
            week += 1;
            if (week >= 7) {
                week = 0;
            }

        }
    }

    /**
     * Vẽ dữ liệu tháng
     *
     * @param canvas canvas
     */
    private void onDrawMonthView(Canvas canvas) {

        int count = mLineCount * 7;
        int d = 0;
        for (int i = 0; i < mLineCount; i++) {
            for (int j = 0; j < 7; j++) {
                Calendar calendar = mItems.get(d);
                if (d > mItems.size() - mNextDiff) {
                    return;
                }
                if (!calendar.isCurrentMonth()) {
                    ++d;
                    continue;
                }
                draw(canvas, calendar, i, j, d);
                ++d;
            }
        }
    }


    /**
     * Bắt đầu vẽ
     *
     * @param canvas   canvas
     * @param calendar Lịch tương ứng
     * @param i        i
     * @param j        j
     * @param d        d
     */
    private void draw(Canvas canvas, Calendar calendar, int i, int j, int d) {
        int x = j * mItemWidth + mDelegate.getYearViewPadding();
        int y = i * mItemHeight + getMonthViewTop();

        boolean isSelected = calendar.equals(mDelegate.mSelectedCalendar);
        boolean hasScheme = calendar.hasScheme();

        if (hasScheme) {
            //Ngày đánh dấu
            boolean isDrawSelected = false;//Có tiếp tục vẽ bản vẽ đã chọn trênDrawScheme không
            if (isSelected) {
                isDrawSelected = onDrawSelected(canvas, calendar, x, y, true);
            }
            if (isDrawSelected || !isSelected) {
                //Đặt cọ thành màu đánh dấu
                mSchemePaint.setColor(calendar.getSchemeColor() != 0 ? calendar.getSchemeColor() : mDelegate.getSchemeThemeColor());
                onDrawScheme(canvas, calendar, x, y);
            }
        } else {
            if (isSelected) {
                onDrawSelected(canvas, calendar, x, y, false);
            }
        }
        onDrawText(canvas, calendar, x, y, hasScheme, isSelected);
    }

    /**
     * Bắt đầu vẽ hook trước, thực hiện một số thao tác khởi tạo ở đây, chỉ một lần cho mỗi lần vẽ, hiệu suất là hiệu quả
     *       * Không cần bỏ qua, không thực hiện
     *       * Ví dụ:
     *       * 1, bạn cần vẽ nền sự kiện đánh dấu tròn, bạn có thể tính bán kính ở đây
     *       * 2, vẽ hiệu ứng chọn hình chữ nhật, bạn cũng có thể tính chiều rộng và chiều cao hình chữ nhật ở đây
     */
    protected void onPreviewHook() {
        // TODO: 2017/11/16
    }


    /**
     * Tháng vẽ
     *
     * @param canvas canvas
     * @param year   year
     * @param month  month
     * @param x      x
     * @param y      y
     * @param width  width
     * @param height height
     */
    protected abstract void onDrawMonth(Canvas canvas, int year, int month, int x, int y, int width, int height);


    /**
     * Vẽ chế độ xem tuần của chế độ xem năm
     *
     * @param canvas canvas
     * @param week   week
     * @param x      x
     * @param y      y
     * @param width  width
     * @param height height
     */
    protected abstract void onDrawWeek(Canvas canvas, int week, int x, int y, int width, int height);


    /**
     * Vẽ ngày đã chọn
     *
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param y         日历Card y起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return 是否绘制onDrawScheme，true or false
     */
    protected abstract boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme);

    /**
     * Vẽ ngày của nhãn hiệu, ở đây có thể là màu nền, đánh dấu màu hoặc một cái gì đó
     *
     * @param canvas   canvas
     * @param calendar 日历calendar
     * @param x        日历Card x起点坐标
     * @param y        日历Card y起点坐标
     */
    protected abstract void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y);


    /**
     * Vẽ văn bản lịch
     *
     * @param canvas     canvas
     * @param calendar   日Lịch
     * @param x          日Thẻ lịch x tọa độ điểm bắt đầu
     * @param y          日Thẻ lịch y tọa độ điểm bắt đầu
     * @param hasScheme  Có phải là ngày của thẻ?
     * @param isSelected Có nên chọn
     */
    protected abstract void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected);
}
