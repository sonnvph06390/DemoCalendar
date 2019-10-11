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

/**
 * Chế độ xem tuần cơ bản nhất, vì giao diện người dùng lịch được triển khai bằng cách cắm nóng,
 * do đó, nó phải được kế thừa ở đây và giao diện người dùng có thể nhất quán.
 *   * Điều này có thể được sử dụng để mở rộng các chế độ xem khác nhau, chẳng hạn như: WeekView, RangeWeekView
 */

public abstract class BaseWeekView extends BaseView {

    public BaseWeekView(Context context) {
        super(context);
    }

    /**
     * Khởi tạo điều khiển xem tuần
     *
     * @param calendar calendar
     */
    final void setup(Calendar calendar) {
        mItems = CalendarUtil.initCalendarForWeekView(calendar, mDelegate, mDelegate.getWeekStart());
        addSchemesFromMap();
        invalidate();
    }


    /**
     * Ghi lại ngày bạn đã chọn
     *
     * @param calendar calendar
     */
    final void setSelectedCalendar(Calendar calendar) {
        if (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_SINGLE &&
                !calendar.equals(mDelegate.mSelectedCalendar)) {
            return;
        }
        mCurrentItem = mItems.indexOf(calendar);
    }


    /**
     * Công tắc xem tuần nhấp vào vị trí mặc định
     *
     * @param calendar calendar
     * @param isNotice isNotice
     */
    final void performClickCalendar(Calendar calendar, boolean isNotice) {

        if (mParentLayout == null ||
                mDelegate.mInnerListener == null ||
                mItems == null || mItems.size() == 0) {
            return;
        }

        int week = CalendarUtil.getWeekViewIndexFromCalendar(calendar, mDelegate.getWeekStart());
        if (mItems.contains(mDelegate.getCurrentDay())) {
            week = CalendarUtil.getWeekViewIndexFromCalendar(mDelegate.getCurrentDay(), mDelegate.getWeekStart());
        }

        int curIndex = week;

        Calendar currentCalendar = mItems.get(week);
        if (mDelegate.getSelectMode() != CalendarViewDelegate.SELECT_MODE_DEFAULT) {
            if (mItems.contains(mDelegate.mSelectedCalendar)) {
                currentCalendar = mDelegate.mSelectedCalendar;
            } else {
                mCurrentItem = -1;
            }
        }

        if (!isInRange(currentCalendar)) {
            curIndex = getEdgeIndex(isMinRangeEdge(currentCalendar));
            currentCalendar = mItems.get(curIndex);
        }


        currentCalendar.setCurrentDay(currentCalendar.equals(mDelegate.getCurrentDay()));
        mDelegate.mInnerListener.onWeekDateSelected(currentCalendar, false);
        int i = CalendarUtil.getWeekFromDayInMonth(currentCalendar, mDelegate.getWeekStart());
        mParentLayout.updateSelectWeek(i);


        if (mDelegate.mCalendarSelectListener != null
                && isNotice
                && mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT) {
            mDelegate.mCalendarSelectListener.onCalendarSelect(currentCalendar, false);
        }

        mParentLayout.updateContentViewTranslateY();
        if (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT) {
            mCurrentItem = curIndex;
        }

        if (!mDelegate.isShowYearSelectedLayout &&
                mDelegate.mIndexCalendar != null &&
                calendar.getYear() != mDelegate.mIndexCalendar.getYear() &&
                mDelegate.mYearChangeListener != null) {
            mDelegate.mYearChangeListener.onYearChange(mDelegate.mIndexCalendar.getYear());
        }

        mDelegate.mIndexCalendar = currentCalendar;
        invalidate();
    }

    /**
     * Có phải là ranh giới truy cập tối thiểu?
     *
     * @param calendar calendar
     * @return Có phải là ranh giới truy cập tối thiểu?
     */
    final boolean isMinRangeEdge(Calendar calendar) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.set(mDelegate.getMinYear(), mDelegate.getMinYearMonth() - 1, mDelegate.getMinYearDay());
        long minTime = c.getTimeInMillis();
        c.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
        long curTime = c.getTimeInMillis();
        return curTime < minTime;
    }

    /**
     * Lấy chỉ mục trong phạm vi ranh giới
     *
     * @param isMinEdge isMinEdge
     * @return Lấy chỉ mục trong phạm vi ranh giới
     */
    final int getEdgeIndex(boolean isMinEdge) {
        for (int i = 0; i < mItems.size(); i++) {
            Calendar item = mItems.get(i);
            boolean isInRange = isInRange(item);
            if (isMinEdge && isInRange) {
                return i;
            } else if (!isMinEdge && !isInRange) {
                return i - 1;
            }
        }
        return isMinEdge ? 6 : 0;
    }


    /**
     * Lấy lịch đã nhấp
     *
     * @return Lấy lịch đã nhấp
     */
    protected Calendar getIndex() {

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
     *Cập nhật chế độ hiển thị
     */
    final void updateShowMode() {
        invalidate();
    }

    /**
     * Cập nhật tuần bắt đầu
     */
    final void updateWeekStart() {

        int position = (int) getTag();
        Calendar calendar = CalendarUtil.getFirstCalendarStartWithMinCalendar(mDelegate.getMinYear(),
                mDelegate.getMinYearMonth(),
                mDelegate.getMinYearDay(),
                position + 1,
                mDelegate.getWeekStart());
        setSelectedCalendar(mDelegate.mSelectedCalendar);
        setup(calendar);
    }

    /**
     * Cập nhật chế độ chọn
     */
    final void updateSingleSelect() {
        if (!mItems.contains(mDelegate.mSelectedCalendar)) {
            mCurrentItem = -1;
            invalidate();
        }
    }

    @Override
    void updateCurrentDate() {
        if (mItems == null)
            return;
        if (mItems.contains(mDelegate.getCurrentDay())) {
            for (Calendar a : mItems) {//添加操作
                a.setCurrentDay(false);
            }
            int index = mItems.indexOf(mDelegate.getCurrentDay());
            mItems.get(index).setCurrentDay(true);
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mItemHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
     * Gọi lại bắt đầu vẽ vòng lặp, không cần bỏ qua
     *       * Vẽ một vòng lặp cho mỗi mục lịch, được sử dụng để tính toán cơ sở, tọa độ trung tâm, v.v.
     *
     * @param x Thẻ lịch x tọa độ điểm bắt đầu
     */
    @SuppressWarnings("unused")
    protected void onLoopStart(int x) {
        // TODO: 2017/11/16
    }

    @Override
    protected void onDestroy() {

    }
}
