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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Một số công cụ tính toán hỗ trợ ngày
 */
final class CalendarUtil {

    private static final long ONE_DAY = 1000 * 3600 * 24;

    @SuppressLint("SimpleDateFormat")
    static int getDate(String formatStr, Date date) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return Integer.parseInt(format.format(date));
    }

    /**
     * Xác định xem một ngày là một ngày cuối tuần, tức là thứ bảy
     *
     * @param calendar calendar
     * @return Xác định xem một ngày là một ngày cuối tuần, tức là thứ bảy
     */
    static boolean isWeekend(Calendar calendar) {
        int week = getWeekFormCalendar(calendar);
        return week == 0 || week == 6;
    }

    /**
     * Lấy số ngày trong một tháng
     *
     * @param year  年
     * @param month 月
     * @return Số ngày trong một tháng
     */
    static int getMonthDaysCount(int year, int month) {
        int count = 0;
        //判断大月份
        if (month == 1 || month == 3 || month == 5 || month == 7
                || month == 8 || month == 10 || month == 12) {
            count = 31;
        }

        //判断小月
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            count = 30;
        }

        //Đánh giá năm trung bình và năm nhuận
        if (month == 2) {
            if (isLeapYear(year)) {
                count = 29;
            } else {
                count = 28;
            }
        }
        return count;
    }


    /**
     * Có phải là một năm nhuận?
     *
     * @param year year
     * @return Có phải là một năm nhuận?
     */
    static boolean isLeapYear(int year) {
        return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
    }


    static int getMonthViewLineCount(int year, int month, int weekStartWith, int mode) {
        if (mode == CalendarViewDelegate.MODE_ALL_MONTH) {
            return 6;
        }
        int nextDiff = CalendarUtil.getMonthEndDiff(year, month, weekStartWith);
        int preDiff = CalendarUtil.getMonthViewStartDiff(year, month, weekStartWith);
        int monthDayCount = CalendarUtil.getMonthDaysCount(year, month);
        return (preDiff + monthDayCount + nextDiff) / 7;
    }

    /**
     * Lấy chiều cao chính xác của chế độ xem theo tháng
     *       * Đạt bài kiểm tra
     *
     * @param year       年
     * @param month      月
     * @param itemHeight Chiều cao của mỗi món đồ
     * @return Không cần thêm hàng chiều cao
     */
    static int getMonthViewHeight(int year, int month, int itemHeight, int weekStartWith) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(year, month - 1, 1);
        int preDiff = getMonthViewStartDiff(year, month, weekStartWith);
        int monthDaysCount = getMonthDaysCount(year, month);
        int nextDiff = getMonthEndDiff(year, month, monthDaysCount, weekStartWith);
        return (preDiff + monthDaysCount + nextDiff) / 7 * itemHeight;
    }

    /**
     * Lấy chiều cao chính xác của chế độ xem theo tháng
     * Test pass
     *
     * @param year       年
     * @param month      月
     * @param itemHeight Chiều cao của mỗi món đồ
     * @return Không cần thêm hàng chiều cao
     */
    static int getMonthViewHeight(int year, int month, int itemHeight, int weekStartWith, int mode) {
        if (mode == CalendarViewDelegate.MODE_ALL_MONTH) {
            return itemHeight * 6;
        }
        return getMonthViewHeight(year, month, itemHeight, weekStartWith);
    }

    /**
     * Nói cách khác, lấy ngày trong tuần trong tháng, nói cách khác,
     * hãy lấy ngày trong một vài hàng đầu tiên của chế độ xem tháng, vài tuần đầu tiên, lấy năng động dựa trên bắt đầu tuần
     * Test pass，Bài kiểm tra đơn vị đã qua
     *
     * @param calendar  calendar
     * @param weekStart Trong thực tế, ngày nào trong tuần?
     * @return Nhận một ngày nhất định trong vài tuần đầu tiên của tháng the week line in MonthView
     */
    static int getWeekFromDayInMonth(Calendar calendar, int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(calendar.getYear(), calendar.getMonth() - 1, 1);
        //Ngày đầu tiên của tháng là ngày trong tuần, Chủ nhật == 0
        int diff = getMonthViewStartDiff(calendar, weekStart);
        return (calendar.getDay() + diff - 1) / 7 + 1;
    }

    /**
     * 获取上一个日子
     *
     * @param calendar calendar
     * @return Nhận ngày cuối cùng
     */
    static Calendar getPreCalendar(Calendar calendar) {
        java.util.Calendar date = java.util.Calendar.getInstance();

        date.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());//

        long timeMills = date.getTimeInMillis();//Bắt đầu dấu thời gian

        date.setTimeInMillis(timeMills - ONE_DAY);

        Calendar preCalendar = new Calendar();
        preCalendar.setYear(date.get(java.util.Calendar.YEAR));
        preCalendar.setMonth(date.get(java.util.Calendar.MONTH) + 1);
        preCalendar.setDay(date.get(java.util.Calendar.DAY_OF_MONTH));

        return preCalendar;
    }

    static Calendar getNextCalendar(Calendar calendar) {
        java.util.Calendar date = java.util.Calendar.getInstance();

        date.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());//

        long timeMills = date.getTimeInMillis();//Bắt đầu dấu thời gian

        date.setTimeInMillis(timeMills + ONE_DAY);

        Calendar nextCalendar = new Calendar();
        nextCalendar.setYear(date.get(java.util.Calendar.YEAR));
        nextCalendar.setMonth(date.get(java.util.Calendar.MONTH) + 1);
        nextCalendar.setDay(date.get(java.util.Calendar.DAY_OF_MONTH));

        return nextCalendar;
    }

    /**
     * DAY_OF_WEEK return  1  2  3 	4  5  6	 7，Bù đắp một chút
     * Lấy phần bù bắt đầu của chế độ xem tháng trong đó ngày được đặt
     * Test pass
     *
     * @param calendar  calendar
     * @param weekStart weekStart 星期的起始
     * @return Lấy phần bù bắt đầu của chế độ xem tháng trong đó ngày được đặt the start diff with MonthView
     */
    static int getMonthViewStartDiff(Calendar calendar, int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(calendar.getYear(), calendar.getMonth() - 1, 1);
        int week = date.get(java.util.Calendar.DAY_OF_WEEK);
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_SUN) {
            return week - 1;
        }
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_MON) {
            return week == 1 ? 6 : week - weekStart;
        }
        return week == CalendarViewDelegate.WEEK_START_WITH_SAT ? 0 : week;
    }


    /**
     * DAY_OF_WEEK return  1  2  3 	4  5  6	 7，偏移了一位
     * Giá trị bù ban đầu tương ứng với chế độ xem hàng tháng của ngày
     * Test pass
     *
     * @param year      年
     * @param month     月
     * @param weekStart 周起始
     * @return Lấy phần bù bắt đầu của chế độ xem tháng trong đó ngày được đặt   the start diff with MonthView
     */
    static int getMonthViewStartDiff(int year, int month, int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(year, month - 1, 1);
        int week = date.get(java.util.Calendar.DAY_OF_WEEK);
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_SUN) {
            return week - 1;
        }
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_MON) {
            return week == 1 ? 6 : week - weekStart;
        }
        return week == CalendarViewDelegate.WEEK_START_WITH_SAT ? 0 : week;
    }


    /**
     * DAY_OF_WEEK return  1  2  3 	4  5  6	 7，偏移了一位
     * Lấy phần bù cuối tương ứng với tháng ngày, được sử dụng để tính tổng số tuần giữa hai năm, không được sử dụng MonthView
     * Test pass
     *
     * @param year      年
     * @param month     月
     * @param weekStart 周起始
     * @return Nhận phần bù kết thúc cho tháng ngày the end diff in Month not MonthView
     */
    static int getMonthEndDiff(int year, int month, int weekStart) {
        return getMonthEndDiff(year, month, getMonthDaysCount(year, month), weekStart);
    }


    /**
     * DAY_OF_WEEK return  1  2  3 	4  5  6	 7，偏移了一位
     * Lấy phần bù cuối tương ứng với tháng ngày, được sử dụng để tính tổng số tuần giữa hai năm, không được sử dụngMonthView
     * Test pass
     *
     * @param year      年
     * @param month     月
     * @param weekStart 周起始
     * @return Nhận phần bù kết thúc cho tháng ngày the end diff in Month not MonthView
     */
    private static int getMonthEndDiff(int year, int month, int day, int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(year, month - 1, day);
        int week = date.get(java.util.Calendar.DAY_OF_WEEK);
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_SUN) {
            return 7 - week;
        }
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_MON) {
            return week == 1 ? 0 : 7 - week + 1;
        }
        return week == 7 ? 6 : 7 - week - 1;
    }

    /**
     * Nhận một ngày là ngày trong tuần
     * 测试通过
     *
     * @param calendar 某个日期
     * @return Trả lại một ngày là ngày trong tuần
     */
    static int getWeekFormCalendar(Calendar calendar) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
        return date.get(java.util.Calendar.DAY_OF_WEEK) - 1;
    }


    /**
     * Nhận chuyển đổi đến vị trí tùy chọn mặc định cho chế độ xem tuần WeekView index
     * 测试通过 test pass
     *
     * @param calendar  calendar
     * @param weekStart weekStart
     * @return Nhận chuyển đổi đến vị trí tùy chọn mặc định cho chế độ xem tuần
     */
    static int getWeekViewIndexFromCalendar(Calendar calendar, int weekStart) {
        return getWeekViewStartDiff(calendar.getYear(), calendar.getMonth(), calendar.getDay(), weekStart);
    }

    /**
     * C trả về thời gian là trong  ngày
     * <p>
     * 测试通过 test pass
     *
     * @param calendar     calendar
     * @param minYear      minYear
     * @param minYearDay   Năm tối thiểu
     * @param minYearMonth minYearMonth
     * @param maxYear      maxYear
     * @param maxYearMonth maxYearMonth
     * @param maxYearDay   Năm tối đa
     * @return trả về thời gian là trong  ngày
     */
    static boolean isCalendarInRange(Calendar calendar,
                                     int minYear, int minYearMonth, int minYearDay,
                                     int maxYear, int maxYearMonth, int maxYearDay) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.set(minYear, minYearMonth - 1, minYearDay);
        long minTime = c.getTimeInMillis();
        c.set(maxYear, maxYearMonth - 1, maxYearDay);
        long maxTime = c.getTimeInMillis();
        c.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
        long curTime = c.getTimeInMillis();
        return curTime >= minTime && curTime <= maxTime;
    }

    /**
     * Lấy tổng số tuần giữa hai ngày,
     *       * Lưu ý rằng tuần bắt đầu từ Thứ Hai, Chủ Nhật, Thứ Bảy
     *       * Đã qua kiểm tra test pass
     *
     * @param minYear      minYear 最小年份
     * @param minYearMonth maxYear Năm tối thiểu của tháng
     * @param minYearDay   最小年份天
     * @param maxYear      maxYear Năm tối đa
     * @param maxYearMonth maxYear Năm tối đa của tháng
     * @param maxYearDay   最大年份天
     * @param weekStart    周起始
     * @return Số tuần được sử dụng cho WeekViewPager itemCount
     */
    static int getWeekCountBetweenBothCalendar(int minYear, int minYearMonth, int minYearDay,
                                               int maxYear, int maxYearMonth, int maxYearDay,
                                               int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(minYear, minYearMonth - 1, minYearDay);
        long minTimeMills = date.getTimeInMillis();//Đưa ra dấu thời gian
        int preDiff = getWeekViewStartDiff(minYear, minYearMonth, minYearDay, weekStart);

        date.set(maxYear, maxYearMonth - 1, maxYearDay);

        long maxTimeMills = date.getTimeInMillis();//Đưa ra dấu thời gian

        int nextDiff = getWeekViewEndDiff(maxYear, maxYearMonth, maxYearDay, weekStart);

        int count = preDiff + nextDiff;

        int c = (int) ((maxTimeMills - minTimeMills) / ONE_DAY) + 1;
        count += c;
        return count / 7;
    }


    /**
     * Theo ngày, khoảng cách tối thiểu có được trong vài tuần đầu tiên.
     *       * được sử dụng để đặt WeekView currentItem
     *       * Kiểm tra bằng cách vượt qua kiểm tra
     *
     * @param calendar     calendar
     * @param minYear      minYear 最小年份
     * @param minYearMonth maxYear 最小年份月份
     * @param minYearDay   最小年份天
     * @param weekStart    周起始
     * @return Trả lại vài tuần đầu tiên của hai năm  the WeekView currentItem
     */
    static int getWeekFromCalendarStartWithMinCalendar(Calendar calendar,
                                                       int minYear, int minYearMonth, int minYearDay,
                                                       int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(minYear, minYearMonth - 1, minYearDay);//起始日期
        long firstTimeMill = date.getTimeInMillis();//获得范围起始时间戳

        int preDiff = getWeekViewStartDiff(minYear, minYearMonth, minYearDay, weekStart);//范围起始的周偏移量

        int weekStartDiff = getWeekViewStartDiff(calendar.getYear(),
                calendar.getMonth(),
                calendar.getDay(),
                weekStart);//获取点击的日子在周视图的起始，为了兼容全球时区，最大日差为一天，如果周起始偏差weekStartDiff=0，则日期加1

        date.set(calendar.getYear(),
                calendar.getMonth() - 1,
                weekStartDiff == 0 ? calendar.getDay() + 1 : calendar.getDay());

        long curTimeMills = date.getTimeInMillis();//给定时间戳

        int c = (int) ((curTimeMills - firstTimeMill) / ONE_DAY);

        int count = preDiff + c;

        return count / 7 + 1;
    }

    /**
     * Tính ngày đầu tuần dựa trên số tuần và ngày tối thiểu
     * //测试通过 Test pass
     *
     * @param minYear      Năm tối thiểu 2017
     * @param minYearMonth maxYear Năm tối thiểu của tháng，like : 2017-07
     * @param minYearDay   Năm tối thiểu
     * @param week         Từ năm nhỏ nhất月minYearMonth 日1 开始的第几周 week > 0
     * @return Ngày đầu tuần
     */
    static Calendar getFirstCalendarStartWithMinCalendar(int minYear, int minYearMonth, int minYearDay, int week, int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();

        date.set(minYear, minYearMonth - 1, minYearDay);//

        long firstTimeMills = date.getTimeInMillis();//Bắt đầu dấu thời gian


        long weekTimeMills = (week - 1) * 7 * ONE_DAY;

        long timeCountMills = weekTimeMills + firstTimeMills;

        date.setTimeInMillis(timeCountMills);

        int startDiff = getWeekViewStartDiff(date.get(java.util.Calendar.YEAR),
                date.get(java.util.Calendar.MONTH) + 1,
                date.get(java.util.Calendar.DAY_OF_MONTH), weekStart);

        timeCountMills -= startDiff * ONE_DAY;
        date.setTimeInMillis(timeCountMills);

        Calendar calendar = new Calendar();
        calendar.setYear(date.get(java.util.Calendar.YEAR));
        calendar.setMonth(date.get(java.util.Calendar.MONTH) + 1);
        calendar.setDay(date.get(java.util.Calendar.DAY_OF_MONTH));

        return calendar;
    }


    /**
     * Cho dù đó là trong phạm vi ngày
     *
     * @param calendar calendar
     * @param delegate delegate
     * @return thời gian trong ngày
     */
    static boolean isCalendarInRange(Calendar calendar, CalendarViewDelegate delegate) {
        return isCalendarInRange(calendar,
                delegate.getMinYear(), delegate.getMinYearMonth(), delegate.getMinYearDay(),
                delegate.getMaxYear(), delegate.getMaxYearMonth(), delegate.getMaxYearDay());
    }

    /**
     * thời gian trong ngày
     *
     * @param year         year
     * @param month        month
     * @param minYear      minYear
     * @param minYearMonth minYearMonth
     * @param maxYear      maxYear
     * @param maxYearMonth maxYearMonth
     * @return thời gian trong ngày
     */
    static boolean isMonthInRange(int year, int month, int minYear, int minYearMonth, int maxYear, int maxYearMonth) {
        return !(year < minYear || year > maxYear) &&
                !(year == minYear && month < minYearMonth) &&
                !(year == maxYear && month > maxYearMonth);
    }

    /**
     * Hoạt động calendar1 - calendar2
     * test Pass
     *
     * @param calendar1 calendar1
     * @param calendar2 calendar2
     * @return calendar1 - calendar2
     */
    static int differ(Calendar calendar1, Calendar calendar2) {
        if (calendar1 == null) {
            return Integer.MIN_VALUE;
        }
        if (calendar2 == null) {
            return Integer.MAX_VALUE;
        }
        java.util.Calendar date = java.util.Calendar.getInstance();

        date.set(calendar1.getYear(), calendar1.getMonth() - 1, calendar1.getDay());//

        long startTimeMills = date.getTimeInMillis();//Bắt đầu dấu thời gian

        date.set(calendar2.getYear(), calendar2.getMonth() - 1, calendar2.getDay());//

        long endTimeMills = date.getTimeInMillis();//获得结束时间戳

        return (int) ((startTimeMills - endTimeMills) / ONE_DAY);
    }

    /**
     * 比较日期大小
     *
     * @param minYear      minYear
     * @param minYearMonth minYearMonth
     * @param minYearDay   minYearDay
     * @param maxYear      maxYear
     * @param maxYearMonth maxYearMonth
     * @param maxYearDay   maxYearDay
     * @return -1 0 1
     */
    static int compareTo(int minYear, int minYearMonth, int minYearDay,
                         int maxYear, int maxYearMonth, int maxYearDay) {
        Calendar first = new Calendar();
        first.setYear(minYear);
        first.setMonth(minYearMonth);
        first.setDay(minYearDay);

        Calendar second = new Calendar();
        second.setYear(maxYear);
        second.setMonth(maxYearMonth);
        second.setDay(maxYearDay);
        return first.compareTo(second);
    }

    /**
     * Khởi tạo lịch để xem tháng
     *
     * @param year        year
     * @param month       month
     * @param currentDate currentDate
     * @param weekStar    weekStar
     * @return Khởi tạo các mục lịch để xem tháng
     */
    static List<Calendar> initCalendarForMonthView(int year, int month, Calendar currentDate, int weekStar) {
        java.util.Calendar date = java.util.Calendar.getInstance();

        date.set(year, month - 1, 1);

        int mPreDiff = getMonthViewStartDiff(year, month, weekStar);//Có được lượt xem thực tế hàng tháng

        int monthDayCount = getMonthDaysCount(year, month);//Lấy số ngày thực trong tháng

        int preYear, preMonth;
        int nextYear, nextMonth;

        int size = 42;

        List<Calendar> mItems = new ArrayList<>();

        int preMonthDaysCount;
        if (month == 1) {//如果是1月
            preYear = year - 1;
            preMonth = 12;
            nextYear = year;
            nextMonth = month + 1;
            preMonthDaysCount = mPreDiff == 0 ? 0 : CalendarUtil.getMonthDaysCount(preYear, preMonth);
        } else if (month == 12) {//如果是12月
            preYear = year;
            preMonth = month - 1;
            nextYear = year + 1;
            nextMonth = 1;
            preMonthDaysCount = mPreDiff == 0 ? 0 : CalendarUtil.getMonthDaysCount(preYear, preMonth);
        } else {//Thông thường
            preYear = year;
            preMonth = month - 1;
            nextYear = year;
            nextMonth = month + 1;
            preMonthDaysCount = mPreDiff == 0 ? 0 : CalendarUtil.getMonthDaysCount(preYear, preMonth);
        }
        int nextDay = 1;
        for (int i = 0; i < size; i++) {
            Calendar calendarDate = new Calendar();
            if (i < mPreDiff) {
                calendarDate.setYear(preYear);
                calendarDate.setMonth(preMonth);
                calendarDate.setDay(preMonthDaysCount - mPreDiff + i + 1);
            } else if (i >= monthDayCount + mPreDiff) {
                calendarDate.setYear(nextYear);
                calendarDate.setMonth(nextMonth);
                calendarDate.setDay(nextDay);
                ++nextDay;
            } else {
                calendarDate.setYear(year);
                calendarDate.setMonth(month);
                calendarDate.setCurrentMonth(true);
                calendarDate.setDay(i - mPreDiff + 1);
            }
            if (calendarDate.equals(currentDate)) {
                calendarDate.setCurrentDay(true);
            }
            LunarCalendar.setupLunarCalendar(calendarDate);
            mItems.add(calendarDate);
        }
        return mItems;
    }

    static List<Calendar> getWeekCalendars(Calendar calendar, CalendarViewDelegate mDelegate) {
        long curTime = calendar.getTimeInMillis();

        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(calendar.getYear(),
                calendar.getMonth() - 1,
                calendar.getDay());//
        int week = date.get(java.util.Calendar.DAY_OF_WEEK);
        int startDiff;
        if (mDelegate.getWeekStart() == 1) {
            startDiff = week - 1;
        } else if (mDelegate.getWeekStart() == 2) {
            startDiff = week == 1 ? 6 : week - mDelegate.getWeekStart();
        } else {
            startDiff = week == 7 ? 0 : week;
        }

        curTime -= startDiff * ONE_DAY;
        java.util.Calendar minCalendar = java.util.Calendar.getInstance();
        minCalendar.setTimeInMillis(curTime);
        Calendar startCalendar = new Calendar();
        startCalendar.setYear(minCalendar.get(java.util.Calendar.YEAR));
        startCalendar.setMonth(minCalendar.get(java.util.Calendar.MONTH) + 1);
        startCalendar.setDay(minCalendar.get(java.util.Calendar.DAY_OF_MONTH));
        return initCalendarForWeekView(startCalendar, mDelegate, mDelegate.getWeekStart());
    }

    /**
     * Tạo 7 lượt xem của tuần item
     *
     * @param calendar  calendar
     * @param mDelegate mDelegate
     * @param weekStart weekStart
     * @return Tạo 7 lượt xem của tuần item
     */
    static List<Calendar> initCalendarForWeekView(Calendar calendar, CalendarViewDelegate mDelegate, int weekStart) {

        java.util.Calendar date = java.util.Calendar.getInstance();//当天时间
        date.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
        long curDateMills = date.getTimeInMillis();//生成选择的日期时间戳

        int weekEndDiff = getWeekViewEndDiff(calendar.getYear(), calendar.getMonth(), calendar.getDay(), weekStart);
        List<Calendar> mItems = new ArrayList<>();

        date.setTimeInMillis(curDateMills);
        Calendar selectCalendar = new Calendar();
        selectCalendar.setYear(date.get(java.util.Calendar.YEAR));
        selectCalendar.setMonth(date.get(java.util.Calendar.MONTH) + 1);
        selectCalendar.setDay(date.get(java.util.Calendar.DAY_OF_MONTH));
        if (selectCalendar.equals(mDelegate.getCurrentDay())) {
            selectCalendar.setCurrentDay(true);
        }
        LunarCalendar.setupLunarCalendar(selectCalendar);
        selectCalendar.setCurrentMonth(true);
        mItems.add(selectCalendar);


        for (int i = 1; i <= weekEndDiff; i++) {
            date.setTimeInMillis(curDateMills + i * ONE_DAY);
            Calendar calendarDate = new Calendar();
            calendarDate.setYear(date.get(java.util.Calendar.YEAR));
            calendarDate.setMonth(date.get(java.util.Calendar.MONTH) + 1);
            calendarDate.setDay(date.get(java.util.Calendar.DAY_OF_MONTH));
            if (calendarDate.equals(mDelegate.getCurrentDay())) {
                calendarDate.setCurrentDay(true);
            }
            LunarCalendar.setupLunarCalendar(calendarDate);
            calendarDate.setCurrentMonth(true);
            mItems.add(calendarDate);
        }
        return mItems;
    }

    /**
     * Bài kiểm tra đơn vị đã qua
     *       * Nhận phần bù bắt đầu của chế độ xem theo tuần từ ngày đã chọn để tạo bố cục chế độ xem hàng tuần
     *
     * @param year      year
     * @param month     month
     * @param day       day
     * @param weekStart 周起始，1，2，7 日 一 六
     * @return Lấy phần bù bắt đầu của chế độ xem theo tuần để tạo bố cục chế độ xem hàng tuần
     */
    private static int getWeekViewStartDiff(int year, int month, int day, int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(year, month - 1, day);//
        int week = date.get(java.util.Calendar.DAY_OF_WEEK);
        if (weekStart == 1) {
            return week - 1;
        }
        if (weekStart == 2) {
            return week == 1 ? 6 : week - weekStart;
        }
        return week == 7 ? 0 : week;
    }


    /**
     * Bài kiểm tra đơn vị đã qua
     *       * Nhận phần bù cuối của chế độ xem tuần từ ngày đã chọn để tạo bố cục chế độ xem hàng tuần
     *
     * @param year      year
     * @param month     month
     * @param day       day
     * @param weekStart 周起始，1，2，7 日 一 六
     * @return Lấy phần bù cuối của chế độ xem theo tuần được sử dụng để tạo bố cục chế độ xem hàng tuần
     */
    private static int getWeekViewEndDiff(int year, int month, int day, int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(year, month - 1, day);
        int week = date.get(java.util.Calendar.DAY_OF_WEEK);
        if (weekStart == 1) {
            return 7 - week;
        }
        if (weekStart == 2) {
            return week == 1 ? 0 : 7 - week + 1;
        }
        return week == 7 ? 6 : 7 - week - 1;
    }


    /**
     * Chuyển từ chế độ xem theo tháng để lấy ngày của ngày đầu tiên
     *
     * @param position position
     * @param delegate position
     * @return Chuyển từ chế độ xem theo tháng để lấy ngày của ngày đầu tiên
     */
    static Calendar getFirstCalendarFromMonthViewPager(int position, CalendarViewDelegate delegate) {
        Calendar calendar = new Calendar();
        calendar.setYear((position + delegate.getMinYearMonth() - 1) / 12 + delegate.getMinYear());
        calendar.setMonth((position + delegate.getMinYearMonth() - 1) % 12 + 1);
        if (delegate.getDefaultCalendarSelectDay() != CalendarViewDelegate.FIRST_DAY_OF_MONTH) {
            int monthDays = getMonthDaysCount(calendar.getYear(), calendar.getMonth());
            Calendar indexCalendar = delegate.mIndexCalendar;
            calendar.setDay(indexCalendar == null || indexCalendar.getDay() == 0 ? 1 :
                    monthDays < indexCalendar.getDay() ? monthDays : indexCalendar.getDay());
        } else {
            calendar.setDay(1);
        }
        if (!isCalendarInRange(calendar, delegate)) {
            if (isMinRangeEdge(calendar, delegate)) {
                calendar = delegate.getMinRangeCalendar();
            } else {
                calendar = delegate.getMaxRangeCalendar();
            }
        }
        calendar.setCurrentMonth(calendar.getYear() == delegate.getCurrentDay().getYear() &&
                calendar.getMonth() == delegate.getCurrentDay().getMonth());
        calendar.setCurrentDay(calendar.equals(delegate.getCurrentDay()));
        LunarCalendar.setupLunarCalendar(calendar);
        return calendar;
    }


    /**
     * Nhận ngày truy cập biên giới dựa trên ngày được thông qua, lớn nhất hoặc nhỏ nhất
     *
     * @param calendar calendar
     * @param delegate delegate
     * @return trả về ngày truy cập biên giới
     */
    static Calendar getRangeEdgeCalendar(Calendar calendar, CalendarViewDelegate delegate) {
        if (CalendarUtil.isCalendarInRange(delegate.getCurrentDay(), delegate)
                && delegate.getDefaultCalendarSelectDay() != CalendarViewDelegate.LAST_MONTH_VIEW_SELECT_DAY_IGNORE_CURRENT) {
            return delegate.createCurrentDate();
        }
        if (isCalendarInRange(calendar, delegate)) {
            return calendar;
        }
        Calendar minRangeCalendar = delegate.getMinRangeCalendar();
        if (minRangeCalendar.isSameMonth(calendar)) {
            return delegate.getMinRangeCalendar();
        }
        return delegate.getMaxRangeCalendar();
    }

    /**
     * Có phải là ranh giới truy cập tối thiểu?
     *
     * @param calendar calendar
     * @return ó phải là ranh giới truy cập tối thiểu?
     */
    private static boolean isMinRangeEdge(Calendar calendar, CalendarViewDelegate delegate) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.set(delegate.getMinYear(), delegate.getMinYearMonth() - 1, delegate.getMinYearDay());
        long minTime = c.getTimeInMillis();
        c.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
        long curTime = c.getTimeInMillis();
        return curTime < minTime;
    }

    /**
     * dp đến px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
