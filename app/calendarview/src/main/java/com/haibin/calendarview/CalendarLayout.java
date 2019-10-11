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


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Bố cục lịch
 */
@SuppressWarnings("unused")
public class CalendarLayout extends LinearLayout {

    /**
     * Hỗ trợ đa chạm
     */
    private int mActivePointerId;

    private static final int ACTIVE_POINTER = 1;

    private static final int INVALID_POINTER = -1;

    /**
     * Xem tuần
     */
    private static final int CALENDAR_SHOW_MODE_BOTH_MONTH_WEEK_VIEW = 0;


    /**
     * Chỉ xem tuần
     */
    private static final int CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW = 1;

    /**
     * Chỉ xem tháng
     */
    private static final int CALENDAR_SHOW_MODE_ONLY_MONTH_VIEW = 2;

    /**
     * Mở rộng mặc định
     */
    private static final int STATUS_EXPAND = 0;

    /**
     * Thu nhỏ mặc định
     */
    private static final int STATUS_SHRINK = 1;

    /**
     * Trạng thái mặc định
     */
    private int mDefaultStatus;

    private boolean isWeekView;

    /**
     * Cột tuần
     */
    WeekBar mWeekBar;

    /**
     * ViewPager tùy chỉnh, xem tháng
     */
    MonthViewPager mMonthView;

    /**
     * Lịch
     */
    CalendarView mCalendarView;

    /**
     * Chế độ xem tuần tùy chỉnh
     */
    WeekViewPager mWeekPager;

    /**
     * Chế độ xem năm
     */
    YearViewPager mYearView;

    /**
     * ContentView
     */
    ViewGroup mContentView;

    /**
     *Cử chỉ mặc định
     */
    private static final int GESTURE_MODE_DEFAULT = 0;

//       /**
    //     *Lịch chỉ hợp lệ
    //     */
//    private static final int GESTURE_MODE_ONLY_CALENDAR = 1;

    /**
     * Vô hiệu hóa cử chỉ
     */
    private static final int GESTURE_MODE_DISABLED = 2;

    /**
     *Chế độ cử chỉ
     */
    private int mGestureMode;


    private int mCalendarShowMode;

    private int mTouchSlop;
    private int mContentViewTranslateY; //ContentView  Khoảng cách tối đa có thể trượt, cố định
    private int mViewPagerTranslateY = 0;// Khoảng cách mà ViewPager có thể dịch, không biểu thị khoảng cách dịch của mMonthView

    private float downY;
    private float mLastY;
    private float mLastX;
    private boolean isAnimating = false;

    /**
     *Bố cục nội dung
     */
    private int mContentViewId;

    /**
     * Đánh giá tốc độ tay
     */
    private VelocityTracker mVelocityTracker;
    private int mMaximumVelocity;

    private int mItemHeight;

    private CalendarViewDelegate mDelegate;

    public CalendarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CalendarLayout);
        mContentViewId = array.getResourceId(R.styleable.CalendarLayout_calendar_content_view_id, 0);
        mDefaultStatus = array.getInt(R.styleable.CalendarLayout_default_status, STATUS_EXPAND);
        mCalendarShowMode = array.getInt(R.styleable.CalendarLayout_calendar_show_mode, CALENDAR_SHOW_MODE_BOTH_MONTH_WEEK_VIEW);
        mGestureMode = array.getInt(R.styleable.CalendarLayout_gesture_mode, GESTURE_MODE_DEFAULT);
        array.recycle();
        mVelocityTracker = VelocityTracker.obtain();
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    /**
     * Khởi tạo
     *
     * @param delegate delegate
     */
    final void setup(CalendarViewDelegate delegate) {
        this.mDelegate = delegate;
        mItemHeight = mDelegate.getCalendarItemHeight();
        initCalendarPosition(delegate.mSelectedCalendar.isAvailable() ?
                delegate.mSelectedCalendar :
                delegate.createCurrentDate());
        updateContentViewTranslateY();
    }

    /**
     * Khởi tạo vị trí của thời điểm hiện tại
     *
     * @param cur Ngày giờ hiện tại
     */
    private void initCalendarPosition(Calendar cur) {
        int diff = CalendarUtil.getMonthViewStartDiff(cur, mDelegate.getWeekStart());
        int size = diff + cur.getDay() - 1;
        updateSelectPosition(size);
    }

    /**
     * Số hiện tại được chọn, cập nhật số lượng bản dịch
     *
     * @param selectPosition Chế độ xem tháng được cập nhật vị trí
     */
    final void updateSelectPosition(int selectPosition) {
        int line = (selectPosition + 7) / 7;
        mViewPagerTranslateY = (line - 1) * mItemHeight;
    }

    /**
     * Đặt tuần đã chọn để cập nhật vị trí
     *
     * @param week week
     */
    final void updateSelectWeek(int week) {
        mViewPagerTranslateY = (week - 1) * mItemHeight;
    }


    /**
     * Cập nhật khoảng cách tối đa mà ContentView có thể xoay
     */
    void updateContentViewTranslateY() {
        Calendar calendar = mDelegate.mIndexCalendar;
        if (mDelegate.getMonthViewShowMode() == CalendarViewDelegate.MODE_ALL_MONTH) {
            mContentViewTranslateY = 5 * mItemHeight;
        } else {
            mContentViewTranslateY = CalendarUtil.getMonthViewHeight(calendar.getYear(),
                    calendar.getMonth(), mItemHeight, mDelegate.getWeekStart())
                    - mItemHeight;
        }
    //    Chế độ xem theo tuần đã được hiển thị, bạn cần tự động xoay chiều cao của nội dung
        if (mWeekPager.getVisibility() == VISIBLE) {
            if (mContentView == null)
                return;
            mContentView.setTranslationY(-mContentViewTranslateY);
        }
    }

    /**
     * Cập nhật chiều cao mục lịch
     */
    final void updateCalendarItemHeight() {
        mItemHeight = mDelegate.getCalendarItemHeight();
        if (mContentView == null)
            return;
        Calendar calendar = mDelegate.mIndexCalendar;
        updateSelectWeek(CalendarUtil.getWeekFromDayInMonth(calendar, mDelegate.getWeekStart()));
        if (mDelegate.getMonthViewShowMode() == CalendarViewDelegate.MODE_ALL_MONTH) {
            mContentViewTranslateY = 5 * mItemHeight;
        } else {
            mContentViewTranslateY = CalendarUtil.getMonthViewHeight(calendar.getYear(), calendar.getMonth(),
                    mItemHeight, mDelegate.getWeekStart()) - mItemHeight;
        }
        translationViewPager();
        if (mWeekPager.getVisibility() == VISIBLE) {
            mContentView.setTranslationY(-mContentViewTranslateY);
        }
    }

    /**
     * Lịch ẩn
     */
    public void hideCalendarView() {
        if (mCalendarView == null) {
            return;
        }
        mCalendarView.setVisibility(GONE);
        if (!isExpand()) {
            expand(0);
        }
        requestLayout();
    }

    /**
     * Lịch hiển thị
     */
    public void showCalendarView() {

        mCalendarView.setVisibility(VISIBLE);
        requestLayout();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureMode == GESTURE_MODE_DISABLED ||
                mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_MONTH_VIEW ||
                mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW) {//Vô hiệu hóa cử chỉ hoặc chỉ hiển thị chế độ xem
            return false;
        }
        if (mDelegate == null) {
            return false;
        }
        if (mDelegate.isShowYearSelectedLayout) {
            return false;
        }

        if (mContentView == null || mCalendarView == null || mCalendarView.getVisibility() == GONE) {
            return false;
        }

        int action = event.getAction();
        float y = event.getY();
        mVelocityTracker.addMovement(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int index = event.getActionIndex();
                mActivePointerId = event.getPointerId(index);
                mLastY = downY = y;
                return true;
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int indexx = event.getActionIndex();
                mActivePointerId = event.getPointerId(indexx);
                if (mActivePointerId == 0) {
                    //Mã cốt lõi là để cho những điều sau đây dy = y- mLastY == 0，Tránh lắc
                    mLastY = event.getY(mActivePointerId);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:

                getPointerIndex(event, mActivePointerId);
                if (mActivePointerId == INVALID_POINTER) {
                    //Nếu bạn chuyển ngón tay, sau đó thay đổi mLastY sang tọa độ y của ngón tay mới nhất.
                    // Cốt lõi là để cho dy sau == 0, tTránh lắc
                    mLastY = y;
                    mActivePointerId = ACTIVE_POINTER;
                }
                float dy = y - mLastY;

                //Vuốt lên và  xoay nội dung Xem chảo đến khoảng cách tối đa, hiển thị chế độ xem theo tuần
                if (dy < 0 && mContentView.getTranslationY() == -mContentViewTranslateY) {
                    mLastY = y;
                    event.setAction(MotionEvent.ACTION_DOWN);
                    dispatchTouchEvent(event);
                    mWeekPager.setVisibility(VISIBLE);
                    mMonthView.setVisibility(INVISIBLE);
                    if (!isWeekView && mDelegate.mViewChangeListener != null) {
                        mDelegate.mViewChangeListener.onViewChange(false);
                    }
                    isWeekView = true;
                    return false;
                }
                hideWeek(false);

                //Vuốt xuống và contentView đã được quét hoàn toàn xuống phía dưới
                if (dy > 0 && mContentView.getTranslationY() + dy >= 0) {
                    mContentView.setTranslationY(0);
                    translationViewPager();
                    mLastY = y;
                    return super.onTouchEvent(event);
                }

                //Trượt lên và contentView đã được quét tới khoảng cách tối đa, sau đó contentView sẽ chuyển sang khoảng cách tối đa
                if (dy < 0 && mContentView.getTranslationY() + dy <= -mContentViewTranslateY) {
                    mContentView.setTranslationY(-mContentViewTranslateY);
                    translationViewPager();
                    mLastY = y;
                    return super.onTouchEvent(event);
                }
                //Mặt khác theo tỷ lệ
                mContentView.setTranslationY(mContentView.getTranslationY() + dy);
                translationViewPager();
                mLastY = y;
                break;
            case MotionEvent.ACTION_CANCEL:

            case MotionEvent.ACTION_POINTER_UP:
                int pointerIndex = getPointerIndex(event, mActivePointerId);
                if (mActivePointerId == INVALID_POINTER)
                    break;
                mLastY = event.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_UP:

                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                float mYVelocity = velocityTracker.getYVelocity();
                if (mContentView.getTranslationY() == 0
                        || mContentView.getTranslationY() == mContentViewTranslateY) {
                    expand();
                    break;
                }
                if (Math.abs(mYVelocity) >= 800) {
                    if (mYVelocity < 0) {
                        shrink();
                    } else {
                        expand();
                    }
                    return super.onTouchEvent(event);
                }
                if (event.getY() - downY > 0) {
                    expand();
                } else {
                    shrink();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isAnimating) {
            return super.dispatchTouchEvent(ev);
        }
        if (mGestureMode == GESTURE_MODE_DISABLED) {
            return super.dispatchTouchEvent(ev);
        }
        if (mYearView == null ||
                mCalendarView == null || mCalendarView.getVisibility() == GONE ||
                mContentView == null ||
                mContentView.getVisibility() != VISIBLE) {
            return super.dispatchTouchEvent(ev);
        }

        if (mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_MONTH_VIEW ||
                mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW) {
            return super.dispatchTouchEvent(ev);
        }

        if (mYearView.getVisibility() == VISIBLE || mDelegate.isShowYearSelectedLayout) {
            return super.dispatchTouchEvent(ev);
        }
        final int action = ev.getAction();
        float y = ev.getY();
        if (action == MotionEvent.ACTION_MOVE) {
            float dy = y - mLastY;
            /*
             * Nếu bạn cuộn xuống, có 2 trường hợp và y nằm dưới ViewPager
              * 1, RecyclerView hoặc các chế độ xem cuộn khác, chặn sự kiện khi mContentView cuộn lên trên cùng
              * 2, điều khiển không cuộn, các sự kiện chặn trực tiếp
             */
            if (dy > 0 && mContentView.getTranslationY() == -mContentViewTranslateY) {
                if (isScrollTop()) {
                    requestDisallowInterceptTouchEvent(false);//父View向子View拦截分发事件
                    return super.dispatchTouchEvent(ev);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isAnimating) {
            return true;
        }
        if (mGestureMode == GESTURE_MODE_DISABLED) {
            return false;
        }
        if (mYearView == null ||
                mCalendarView == null || mCalendarView.getVisibility() == GONE ||
                mContentView == null ||
                mContentView.getVisibility() != VISIBLE) {
            return super.onInterceptTouchEvent(ev);
        }

        if (mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_MONTH_VIEW ||
                mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW) {
            return false;
        }

        if (mYearView.getVisibility() == VISIBLE || mDelegate.isShowYearSelectedLayout) {
            return super.onInterceptTouchEvent(ev);
        }
        final int action = ev.getAction();
        float y = ev.getY();
        float x = ev.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int index = ev.getActionIndex();
                mActivePointerId = ev.getPointerId(index);
                mLastY = downY = y;
                mLastX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                float dx = x - mLastX;
                 /*
                  Nếu cuộn lên và ViewPager bị thu hẹp, không có sự kiện nào bị chặn
                 */
                if (dy < 0 && mContentView.getTranslationY() == -mContentViewTranslateY) {
                    return false;
                }
                /*
                 * Nếu bạn cuộn xuống, có 2 trường hợp và y nằm dưới ViewPager
                  * 1, RecyclerView hoặc các chế độ xem cuộn khác, chặn sự kiện khi mContentView cuộn lên trên cùng
                  * 2, điều khiển không cuộn, các sự kiện chặn trực tiếp
                 */
                if (dy > 0 && mContentView.getTranslationY() == -mContentViewTranslateY
                        && y >= mDelegate.getCalendarItemHeight() + mDelegate.getWeekBarHeight()) {
                    if (!isScrollTop()) {
                        return false;
                    }
                }

                if (dy > 0 && mContentView.getTranslationY() == 0 && y >= CalendarUtil.dipToPx(getContext(), 98)) {
                    return false;
                }

                if (Math.abs(dy) > Math.abs(dx) ) { //Khoảng cách trượt dọc lớn hơn khoảng cách trượt bên, chặn các sự kiện trượt
                    if ((dy > 0 && mContentView.getTranslationY() <= 0)
                            || (dy < 0 && mContentView.getTranslationY() >= -mContentViewTranslateY)) {
                        mLastY = y;
                        return true;
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


    private int getPointerIndex(MotionEvent ev, int id) {
        int activePointerIndex = ev.findPointerIndex(id);
        if (activePointerIndex == -1) {
            mActivePointerId = INVALID_POINTER;
        }
        return activePointerIndex;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mContentView == null || mCalendarView == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int year = mDelegate.mIndexCalendar.getYear();
        int month = mDelegate.mIndexCalendar.getMonth();
        int weekBarHeight = CalendarUtil.dipToPx(getContext(), 1)
                + mDelegate.getWeekBarHeight();

        int monthHeight = CalendarUtil.getMonthViewHeight(year, month,
                mDelegate.getCalendarItemHeight(),
                mDelegate.getWeekStart(),
                mDelegate.getMonthViewShowMode())
                + weekBarHeight;

        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (mDelegate.isFullScreenCalendar()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int heightSpec = MeasureSpec.makeMeasureSpec(height - weekBarHeight - mDelegate.getCalendarItemHeight(),
                    MeasureSpec.EXACTLY);
            mContentView.measure(widthMeasureSpec, heightSpec);
            mContentView.layout(mContentView.getLeft(), mContentView.getTop(), mContentView.getRight(), mContentView.getBottom());
            return;
        }

        if (monthHeight >= height && mMonthView.getHeight() > 0) {
            height = monthHeight;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(monthHeight +
                    weekBarHeight +
                    mDelegate.getWeekBarHeight(), MeasureSpec.EXACTLY);
        } else if (monthHeight < height && mMonthView.getHeight() > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }

        int h;
        if (mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_MONTH_VIEW ||
                mCalendarView.getVisibility() == GONE) {
            h = height - (mCalendarView.getVisibility() == GONE ? 0 : mCalendarView.getHeight());
        } else if (mGestureMode == GESTURE_MODE_DISABLED && !isAnimating) {
            if (isExpand()) {
                h = height - monthHeight;
            } else {
                h = height - weekBarHeight - mItemHeight;
            }
        } else {
            h = height - weekBarHeight - mItemHeight;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSpec = MeasureSpec.makeMeasureSpec(h,
                MeasureSpec.EXACTLY);
        mContentView.measure(widthMeasureSpec, heightSpec);
        mContentView.layout(mContentView.getLeft(), mContentView.getTop(), mContentView.getRight(), mContentView.getBottom());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMonthView = findViewById(R.id.vp_month);
        mWeekPager = findViewById(R.id.vp_week);
        if (getChildCount() > 0) {
            mCalendarView = (CalendarView) getChildAt(0);
        }
        mContentView = findViewById(mContentViewId);
        mYearView = findViewById(R.id.selectLayout);
        if (mContentView != null) {
            mContentView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }


    /**
     * chuyển đổi ViewPager Xem tháng
     */
    private void translationViewPager() {
        float percent = mContentView.getTranslationY() * 1.0f / mContentViewTranslateY;
        mMonthView.setTranslationY(mViewPagerTranslateY * percent);
    }


    public void setModeBothMonthWeekView() {
        mCalendarShowMode = CALENDAR_SHOW_MODE_BOTH_MONTH_WEEK_VIEW;
        requestLayout();
    }

    public void setModeOnlyWeekView() {
        mCalendarShowMode = CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW;
        requestLayout();
    }

    public void setModeOnlyMonthView() {
        mCalendarShowMode = CALENDAR_SHOW_MODE_ONLY_MONTH_VIEW;
        requestLayout();
    }


    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        Parcelable parcelable = super.onSaveInstanceState();
        bundle.putParcelable("super", parcelable);
        bundle.putBoolean("isExpand", isExpand());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        Parcelable superData = bundle.getParcelable("super");
        boolean isExpand = bundle.getBoolean("isExpand");
        if (isExpand) {
            post(new Runnable() {
                @Override
                public void run() {
                    expand(0);
                }
            });
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    shrink(0);
                }
            });

        }
        super.onRestoreInstanceState(superData);
    }

    /**
     * Cho dù nó bắt đầu
     *
     * @return isExpand
     */
    public final boolean isExpand() {
        return mMonthView.getVisibility() == VISIBLE;
    }


    public boolean expand() {
        return expand(240);
    }


    /**
     * 展开
     *
     * @param duration Thời lượng
     * @return Việc mở rộng có thành công hay không
     */
    public boolean expand(int duration) {
        if (isAnimating ||
                mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW ||
                mContentView == null)
            return false;
        if (mMonthView.getVisibility() != VISIBLE) {
            mWeekPager.setVisibility(GONE);
            onShowMonthView();
            isWeekView = false;
            mMonthView.setVisibility(VISIBLE);
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mContentView,
                "translationY", mContentView.getTranslationY(), 0f);
        objectAnimator.setDuration(duration);
        objectAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (Float) animation.getAnimatedValue();
                float percent = currentValue * 1.0f / mContentViewTranslateY;
                mMonthView.setTranslationY(mViewPagerTranslateY * percent);
                isAnimating = true;
            }
        });
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;
                if (mGestureMode == GESTURE_MODE_DISABLED) {
                    requestLayout();
                }
                hideWeek(true);
                if (mDelegate.mViewChangeListener != null && isWeekView) {
                    mDelegate.mViewChangeListener.onViewChange(true);
                }
                isWeekView = false;

            }
        });
        objectAnimator.start();
        return true;
    }

    public boolean shrink() {
        return shrink(240);
    }

    /**
     * 收缩
     *
     * @param duration Thời lượng
     * @return Thành công hay thất bại
     */
    public boolean shrink(int duration) {
        if (mGestureMode == GESTURE_MODE_DISABLED) {
            requestLayout();
        }
        if (isAnimating || mContentView == null) {
            return false;
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mContentView,
                "translationY", mContentView.getTranslationY(), -mContentViewTranslateY);
        objectAnimator.setDuration(duration);
        objectAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (Float) animation.getAnimatedValue();
                float percent = currentValue * 1.0f / mContentViewTranslateY;
                mMonthView.setTranslationY(mViewPagerTranslateY * percent);
                isAnimating = true;
            }
        });
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;
                showWeek();
                isWeekView = true;

            }
        });
        objectAnimator.start();
        return true;
    }

    /**
     * Trạng thái khởi tạo
     */
    final void initStatus() {

        if ((mDefaultStatus == STATUS_SHRINK ||
                mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW) &&
                mCalendarShowMode != CALENDAR_SHOW_MODE_ONLY_MONTH_VIEW) {
            if (mContentView == null) {
                mWeekPager.setVisibility(VISIBLE);
                mMonthView.setVisibility(GONE);
                return;
            }
            post(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mContentView,
                            "translationY", mContentView.getTranslationY(), -mContentViewTranslateY);
                    objectAnimator.setDuration(0);
                    objectAnimator.addUpdateListener(new AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float currentValue = (Float) animation.getAnimatedValue();
                            float percent = currentValue * 1.0f / mContentViewTranslateY;
                            mMonthView.setTranslationY(mViewPagerTranslateY * percent);
                            isAnimating = true;
                        }
                    });
                    objectAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            isAnimating = false;
                            isWeekView = true;
                            showWeek();
                            if (mDelegate == null || mDelegate.mViewChangeListener == null) {
                                return;
                            }
                            mDelegate.mViewChangeListener.onViewChange(false);

                        }
                    });
                    objectAnimator.start();
                }
            });
        } else {
            if (mDelegate.mViewChangeListener == null) {
                return;
            }
            post(new Runnable() {
                @Override
                public void run() {
                    mDelegate.mViewChangeListener.onViewChange(true);
                }
            });
        }
    }

    /**
     * Chế độ xem tuần ẩn
     */
    private void hideWeek(boolean isNotify) {
        if (isNotify) {
            onShowMonthView();
        }
        mWeekPager.setVisibility(GONE);
        mMonthView.setVisibility(VISIBLE);
    }

    /**
     * Hiển thị xem tuần
     */
    private void showWeek() {
        onShowWeekView();
        if (mWeekPager != null && mWeekPager.getAdapter() != null) {
            mWeekPager.getAdapter().notifyDataSetChanged();
            mWeekPager.setVisibility(VISIBLE);
        }
        mMonthView.setVisibility(INVISIBLE);
    }

    /**
     * Chế độ xem tuần hiển thị sự kiện
     */
    private void onShowWeekView() {
        if (mWeekPager.getVisibility() == VISIBLE) {
            return;
        }
        if (mDelegate != null && mDelegate.mViewChangeListener != null && !isWeekView) {
            mDelegate.mViewChangeListener.onViewChange(false);
        }
    }


    /**
     * Chế độ xem tuần hiển thị sự kiện
     */
    private void onShowMonthView() {
        if (mMonthView.getVisibility() == VISIBLE) {
            return;
        }
        if (mDelegate != null && mDelegate.mViewChangeListener != null && isWeekView) {
            mDelegate.mViewChangeListener.onViewChange(true);
        }
    }

    /**
     * ContentView Có nên cuộn lên đầu Nếu bạn không phù hợp chút nào, hãy viết lại phương pháp này
     *
     * @return Có nên di chuyển lên trên cùng không
     */
    protected boolean isScrollTop() {
        if (mContentView instanceof CalendarScrollView) {
            return ((CalendarScrollView) mContentView).isScrollToTop();
        }
        if (mContentView instanceof RecyclerView)
            return ((RecyclerView) mContentView).computeVerticalScrollOffset() == 0;
        if (mContentView instanceof AbsListView) {
            boolean result = false;
            AbsListView listView = (AbsListView) mContentView;
            if (listView.getFirstVisiblePosition() == 0) {
                final View topChildView = listView.getChildAt(0);
                result = topChildView.getTop() == 0;
            }
            return result;
        }
        return mContentView.getScrollY() == 0;
    }


    /**
     * Bố cục nội dung ẩn
     */
    @SuppressLint("NewApi")
    final void hideContentView() {
        if (mContentView == null)
            return;
        mContentView.animate()
                .translationY(getHeight() - mMonthView.getHeight())
                .setDuration(220)
                .setInterpolator(new LinearInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mContentView.setVisibility(INVISIBLE);
                        mContentView.clearAnimation();
                    }
                });
    }

    /**
     *Hiển thị bố cục nội dung
     */
    @SuppressLint("NewApi")
    final void showContentView() {
        if (mContentView == null)
            return;
        mContentView.setTranslationY(getHeight() - mMonthView.getHeight());
        mContentView.setVisibility(VISIBLE);
        mContentView.animate()
                .translationY(0)
                .setDuration(180)
                .setInterpolator(new LinearInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                });
    }


    @SuppressWarnings("unused")
    private int getCalendarViewHeight() {
        return mMonthView.getVisibility() == VISIBLE ? mDelegate.getWeekBarHeight() + mMonthView.getHeight() :
                mDelegate.getWeekBarHeight() + mDelegate.getCalendarItemHeight();
    }

    /**
     * Nếu bạn có ContentView rất đặc biệt, bạn có thể tùy chỉnh giao diện này.
     */
    public interface CalendarScrollView {
        /**
         *Có nên di chuyển lên trên cùng không
         *
         * @return Có nên di chuyển lên trên cùng không
         */
        boolean isScrollToTop();
    }
}
