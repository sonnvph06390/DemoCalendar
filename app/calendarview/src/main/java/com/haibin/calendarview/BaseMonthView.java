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

import android.annotation.SuppressLint;
import android.content.Context;


//          * Kiểm soát cơ sở xem tháng, kế thừa tự do
//         Điều này có thể được sử dụng để mở rộng các chế độ xem khác nhau, chẳng hạn như: MonthView, RangeMonthView, MultiMonthView
//
public abstract class BaseMonthView extends BaseView {

    MonthViewPager mMonthViewPager;

    /**
     * năm hiện tại
     */
    protected int mYear;

    /**
     * Tháng hiện tại
     */
    protected int mMonth;


    /**
     * Số lượng hàng trong lịch
     */
    protected int mLineCount;

    /**
     * Chiều cao của lịch
     */
    protected int mHeight;
    /**
     * Số lần bù vào tháng tiếp theo
     */
    protected int mNextDiff;

    public BaseMonthView(Context context) {
        super(context);
    }

    /**
     * Ngày khởi tạo
     *
     * @param year  year
     * @param month month
     */
    final void initMonthWithDate(int year, int month) {
        mYear = year;
        mMonth = month;
        initCalendar();
        mHeight = CalendarUtil.getMonthViewHeight(year, month, mItemHeight, mDelegate.getWeekStart(),
                mDelegate.getMonthViewShowMode());

    }

    /**
     * Lịch khởi tạo
     */
    @SuppressLint("WrongConstant")
    private void initCalendar() {

        mNextDiff = CalendarUtil.getMonthEndDiff(mYear, mMonth, mDelegate.getWeekStart());
        int preDiff = CalendarUtil.getMonthViewStartDiff(mYear, mMonth, mDelegate.getWeekStart());
        int monthDayCount = CalendarUtil.getMonthDaysCount(mYear, mMonth);

        mItems = CalendarUtil.initCalendarForMonthView(mYear, mMonth, mDelegate.getCurrentDay(), mDelegate.getWeekStart());

        if (mItems.contains(mDelegate.getCurrentDay())) {
            mCurrentItem = mItems.indexOf(mDelegate.getCurrentDay());
        } else {
            mCurrentItem = mItems.indexOf(mDelegate.mSelectedCalendar);
        }

        if (mCurrentItem > 0 &&
                mDelegate.mCalendarInterceptListener != null &&
                mDelegate.mCalendarInterceptListener.onCalendarIntercept(mDelegate.mSelectedCalendar)) {
            mCurrentItem = -1;
        }

        if (mDelegate.getMonthViewShowMode() == CalendarViewDelegate.MODE_ALL_MONTH) {
            mLineCount = 6;
        } else {
            mLineCount = (preDiff + monthDayCount + mNextDiff) / 7;
        }
        addSchemesFromMap();
        invalidate();
    }

    /**
     * Bấm vào ngày đã chọn
     *
     * @return return
     */
    protected Calendar getIndex() {
        if (mItemWidth == 0 || mItemHeight == 0) {
            return null;
        }
        int indexX = (int) (mX - mDelegate.getCalendarPadding()) / mItemWidth;
        if (indexX >= 7) {
            indexX = 6;
        }
        int indexY = (int) mY / mItemHeight;
        int position = indexY * 7 + indexX;// 选择项
        if (position >= 0 && position < mItems.size())
            return mItems.get(position);
        return null;
    }

    /**
     * Ghi lại ngày bạn đã chọn
     *
     * @param calendar calendar
     */
    final void setSelectedCalendar(Calendar calendar) {
        mCurrentItem = mItems.indexOf(calendar);
    }


    /**
     * Cập nhật chế độ hiển thị
     */
    final void updateShowMode() {
        mLineCount = CalendarUtil.getMonthViewLineCount(mYear, mMonth,
                mDelegate.getWeekStart(),mDelegate.getMonthViewShowMode());
        mHeight = CalendarUtil.getMonthViewHeight(mYear, mMonth, mItemHeight, mDelegate.getWeekStart(),
                mDelegate.getMonthViewShowMode());
        invalidate();
    }

    /**
     * Cập nhật tuần bắt đầu
     */
    final void updateWeekStart() {
        initCalendar();
        mHeight = CalendarUtil.getMonthViewHeight(mYear, mMonth, mItemHeight, mDelegate.getWeekStart(),
                mDelegate.getMonthViewShowMode());
    }

    @Override
    void updateItemHeight() {
        super.updateItemHeight();
        mHeight = CalendarUtil.getMonthViewHeight(mYear, mMonth, mItemHeight, mDelegate.getWeekStart(),
                mDelegate.getMonthViewShowMode());
    }


    @Override
    void updateCurrentDate() {
        if (mItems == null)
            return;
        if (mItems.contains(mDelegate.getCurrentDay())) {
            for (Calendar a : mItems) {// Thêm hoạt động
                a.setCurrentDay(false);
            }
            int index = mItems.indexOf(mDelegate.getCurrentDay());
            mItems.get(index).setCurrentDay(true);
        }
        invalidate();
    }


    /**
     * Lấy chỉ mục đã chọn
     *
     * @param calendar calendar
     * @return Lấy chỉ mục đã chọn
     */
    protected final int getSelectedIndex(Calendar calendar) {
        return mItems.indexOf(calendar);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mLineCount != 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Bắt đầu vẽ hook trước, thực hiện một số thao tác khởi tạo ở đây, chỉ một lần cho mỗi lần vẽ, hiệu suất là hiệu quả
     * Không cần bỏ qua, không thực hiện
     * Ví dụ
     * 1、Cần vẽ nền sự kiện đánh dấu tròn, nơi bạn có thể tính bán kính
     * 2、Vẽ một hiệu ứng lựa chọn hình chữ nhật, bạn cũng có thể tính chiều rộng và chiều cao hình chữ nhật ở đây.
     */
    protected void onPreviewHook() {
        // TODO: 2017/11/16
    }


    /**
     * Gọi lại bắt đầu vẽ vòng lặp, không cần bỏ qua
     * Vẽ một vòng lặp của từng mục lịch, có thể được sử dụng để tính toán cơ sở, tọa độ trung tâm, v.v.
     *
         * @param x Lịch Card x  Tọa độ điểm bắt đầu
     * @param y Lịch Card y Tọa độ điểm bắt đầu
     */
    protected void onLoopStart(int x, int y) {
        // TODO: 2017/11/16  
    }

    @Override
    protected void onDestroy() {

    }
}
