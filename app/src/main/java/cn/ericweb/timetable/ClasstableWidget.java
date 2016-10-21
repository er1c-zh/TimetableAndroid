package cn.ericweb.timetable;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.ericweb.timetable.domain.ClassTable;
import cn.ericweb.timetable.domain.ClassTableAppAdditionalInfo;
import cn.ericweb.timetable.domain.CourseAppAdditionalInfo;
import cn.ericweb.timetable.domain.CourseInClassTable;
import cn.ericweb.timetable.ericandroid.EricRoundedCornerTextview;
import cn.ericweb.timetable.util.AppConstant;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static cn.ericweb.timetable.ClasstableFragment.CLASSTABLE_ADDITIONAL_JSON;
import static cn.ericweb.timetable.ClasstableFragment.CLASSTABLE_JSON;
import static cn.ericweb.timetable.ClasstableFragment.CONTAINER_HEIGHT;
import static cn.ericweb.timetable.ClasstableFragment.CONTAINER_WIDTH;
import static cn.ericweb.timetable.ClasstableFragment.FIRST_WEEK_DATE_STRING;
import static cn.ericweb.timetable.ClasstableFragment.IF_SHOW_WEEKENDS;
import static cn.ericweb.timetable.ClasstableFragment.WEEK_TO_SHOW;

/**
 * Implementation of App Widget functionality.
 */
public class ClasstableWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.classtable_widget);

        SharedPreferences widgetConfig = context.getSharedPreferences(AppConstant.SHARED_PREF_WIDGET, MODE_PRIVATE);
        int widgetWidth = widgetConfig.getInt(AppConstant.WIDGET_KEY_WIDTH, 720);
        int widgetHeight = widgetConfig.getInt(AppConstant.WIDGET_KEY_HEIGHT, 1280);
        // TODO: 2016/10/22 加一个监控情况的IF
        try {
            SharedPreferences config = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences classtableSharedPref = context.getSharedPreferences(AppConstant.SHARED_PREF_CLASSTABLE, MODE_PRIVATE);

            // 防止第一次或清空了数据
            String jsonClasstable = classtableSharedPref.getString(AppConstant.CLASSTABLE_KEY_MAIN, "");
            String jsonClasstableAdditionalInfo = classtableSharedPref.getString(AppConstant.CLASSTABLE_KEY_ADDITIONAL_INFO, "");
            if (jsonClasstable.equals("") || jsonClasstableAdditionalInfo.equals("")) {
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt(ClasstableFragment.WEEK_TO_SHOW, 1);
            bundle.putBoolean(ClasstableFragment.IF_SHOW_WEEKENDS, config.getBoolean(context.getString(R.string.setting_classtable_show_weekends_key), true));
            bundle.putString(ClasstableFragment.CLASSTABLE_JSON, classtableSharedPref.getString(AppConstant.CLASSTABLE_KEY_MAIN, ""));
            bundle.putString(ClasstableFragment.CLASSTABLE_ADDITIONAL_JSON, classtableSharedPref.getString(AppConstant.CLASSTABLE_KEY_ADDITIONAL_INFO, ""));
            // 获得尺寸数据
            // 宽度
            int containerWidth = widgetWidth;
            // 高度
            int containerHeight = widgetHeight;
            bundle.putInt(ClasstableFragment.CONTAINER_WIDTH, containerWidth);
            bundle.putInt(ClasstableFragment.CONTAINER_HEIGHT, containerHeight);
            bundle.putString(ClasstableFragment.FIRST_WEEK_DATE_STRING, config.getString(context.getString(R.string.setting_classtable_now_week_first_week_start_date_key), ""));
            int week2show = bundle.getInt(WEEK_TO_SHOW);
            // get课程表和附加信息
            Gson gson = new Gson();
            ClassTable classTable;
            ClassTableAppAdditionalInfo classTableAppAdditionalInfo;
            classTable = gson.fromJson(bundle.getString(CLASSTABLE_JSON), ClassTable.class);
            classTableAppAdditionalInfo = gson.fromJson(bundle.getString(CLASSTABLE_ADDITIONAL_JSON), ClassTableAppAdditionalInfo.class);

            // 获得课程表容器并清空
            LinearLayout classTableContainer = new LinearLayout(context);
            classTableContainer.setOrientation(LinearLayout.VERTICAL);

            // 获得显示几天一周
            int dayToShow = bundle.getBoolean(IF_SHOW_WEEKENDS) ? 7 : 5;
            // 宽度
            int perClassWidth = containerWidth / (1 + dayToShow);
            // 高度
            int perClassHeight = containerHeight / (classTable.getCourseNumberPerDay() + 1);
            // font size
            int fontSize = perClassHeight * perClassWidth / 350;

            // 添加周几
            LinearLayout weekdayBar = new LinearLayout(context);
            weekdayBar.setOrientation(LinearLayout.HORIZONTAL);
            // 添加周几前的周数
            EricRoundedCornerTextview indexOfWeek = new EricRoundedCornerTextview(context);
            indexOfWeek.setGravity(Gravity.CENTER);
            indexOfWeek.setWidth(perClassWidth);
            indexOfWeek.setHeight(perClassHeight);
            indexOfWeek.setBorderWidth(1);
            indexOfWeek.setTextSize(fontSize);
            TextPaint paint = indexOfWeek.getPaint();
            paint.setFakeBoldText(true);

            // 计算时间
            Date now = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat yyyymmdd = new SimpleDateFormat("yyyyMMdd");
            Date startDate;
            try {
                startDate = yyyymmdd.parse(bundle.getString(FIRST_WEEK_DATE_STRING));
            } catch (ParseException e) {
                e.printStackTrace();
                startDate = now;
            }

            Calendar tempCalendar = Calendar.getInstance();
            tempCalendar.setTime(startDate);
            // 这里-1是因为保存的日期是第一周的
            tempCalendar.add(Calendar.DATE, (week2show - 1) * 7);
            int month = tempCalendar.get(Calendar.MONTH) + 1;
            indexOfWeek.setText("W" + week2show + "\nM" + month);

            indexOfWeek.measure(perClassWidth, perClassHeight);
            indexOfWeek.layout(0, 0, perClassWidth, perClassHeight);
            indexOfWeek.setDrawingCacheEnabled(true);

            weekdayBar.addView(indexOfWeek);

            for (int i = 0; i < dayToShow; i++) {
                EricRoundedCornerTextview weekDayTextView = new EricRoundedCornerTextview(context);
                weekDayTextView.setGravity(Gravity.CENTER);
                weekDayTextView.setWidth(perClassWidth);
                weekDayTextView.setHeight(perClassHeight);
                weekDayTextView.setBorderWidth(1);

                paint = weekDayTextView.getPaint();
                paint.setFakeBoldText(true);

                weekDayTextView.setTextSize(fontSize);

                int temp = i + 1;
                Calendar tempDateCalendar = Calendar.getInstance();
                tempDateCalendar.setTime(tempCalendar.getTime());
                tempDateCalendar.add(Calendar.DATE, i);

                weekDayTextView.setText(temp + "" + "\nD" + tempDateCalendar.get(Calendar.DAY_OF_MONTH));
                Calendar nowCalendar = Calendar.getInstance();
                if (nowCalendar.get(Calendar.DAY_OF_YEAR) == tempDateCalendar.get(Calendar.DAY_OF_YEAR)) {
                    weekDayTextView.setTextColor(context.getResources().getColor(R.color.colorAccent));
                }

                weekDayTextView.measure(perClassWidth, perClassHeight);
                weekDayTextView.layout(0, 0, perClassWidth, perClassHeight);
                weekDayTextView.setDrawingCacheEnabled(true);

                weekdayBar.addView(weekDayTextView);
            }

            weekdayBar.measure(containerWidth, perClassHeight);
            weekdayBar.layout(0, 0, containerWidth, perClassHeight);
            weekdayBar.setDrawingCacheEnabled(true);

            classTableContainer.addView(weekdayBar);

            // 添加课程表
            LinearLayout classTableRow = new LinearLayout(context);
            classTableRow.setOrientation(LinearLayout.HORIZONTAL);

            // 添加课程index
            LinearLayout classIndexContainer = new LinearLayout(context);
            classIndexContainer.setOrientation(LinearLayout.VERTICAL);

            // 时间的计时器
            int hour = classTable.getStartHour();
            int minute = classTable.getStartMinute();
            ArrayList<Integer> classIntervalArrayList = classTable.getIntervalPerCourse();
            for (int classIndex = 1; classIndex <= classTable.getCourseNumberPerDay(); classIndex++) {
                EricRoundedCornerTextview classIndexText = new EricRoundedCornerTextview(context);
                classIndexText.setGravity(Gravity.CENTER);
                classIndexText.setWidth(perClassWidth);
                classIndexText.setHeight(perClassHeight);
                classIndexText.setBorderWidth(1);

                paint = classIndexText.getPaint();
                paint.setFakeBoldText(true);

                classIndexText.setTextSize(fontSize);

                classIndexText.setText(hour + ":" + minute + (minute == 0 ? "0" : "") + "\n" + classIndex);

                // 更新时间
                // TODO: 16-9-14 修改ClassTable 添加一个没节课的时间，修改掉下面的45（UESTC）
                try {
                    minute += classIntervalArrayList.get(classIndex - 1) + 45;
                    int minuteRemainder = minute % 60;
                    hour += minute / 60;
                    minute = minuteRemainder;
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
                classIndexText.measure(perClassWidth, perClassHeight);
                classIndexText.layout(0, 0, perClassWidth, perClassHeight);
                classIndexText.setDrawingCacheEnabled(true);

                classIndexContainer.addView(classIndexText);
            }

            classIndexContainer.measure(containerWidth, perClassHeight);
            classIndexContainer.layout(0, 0, containerWidth, perClassHeight);
            classIndexContainer.setDrawingCacheEnabled(true);

            classTableRow.addView(classIndexContainer);

            // 添加课程
            for (int day = 0; day < dayToShow; day++) {
                // 循环每天
                LinearLayout dayContainer = new LinearLayout(context);
                dayContainer.setOrientation(LinearLayout.VERTICAL);
                for (int classIndex = 0; classIndex < classTable.getCourseNumberPerDay(); classIndex++) {
                    // 循环每个周
                    // 创建展示的TextView
                    EricRoundedCornerTextview classText = new EricRoundedCornerTextview(context);
                    classText.setTextSize(fontSize);
                    classText.setGravity(Gravity.CENTER);
                    classText.setBorderWidth(1);

                    // 获得最初的CourseInClassTable
                    CourseInClassTable originCourse = classTable.getCourseInClassTable(day, classIndex);
                    // 获得附加信息
                    CourseAppAdditionalInfo originCourseAdditionalInfo = classTableAppAdditionalInfo.getCourseAppAdditionalInfo(originCourse.getCourse());

                    // 记录每节课的尺寸
                    int classHeight;
                    // 合并同一节课
                    for (int isCourseEqualIndex = classIndex + 1; true; isCourseEqualIndex++) {
                        if (!classTable.getCourseInClassTable(day, isCourseEqualIndex).equals(originCourse) || isCourseEqualIndex >= classTable.getCourseNumberPerDay()) {
                            classHeight = perClassHeight * (isCourseEqualIndex - classIndex);
                            classIndex = isCourseEqualIndex - 1;
                            break;
                        }
                    }
                    // 设置课程的尺寸
                    classText.setWidth(perClassWidth);
                    classText.setHeight(classHeight);

                    // 附加的信息不为null 课程不为空 本周有课
                    if (originCourseAdditionalInfo == null || originCourse.toString().equals("") || !originCourse.isCourseExistInWeek(week2show)) {
                        classText.setText("");
                    } else {
                        classText.setText(originCourseAdditionalInfo.getString2Show());
                        if (originCourseAdditionalInfo.getColor() != -1) {
                            classText.setmBgColor(originCourseAdditionalInfo.getColor());
                        }
                    }

                    classText.measure(perClassWidth, perClassHeight);
                    classText.layout(0, 0, perClassWidth, classHeight);
                    classText.setDrawingCacheEnabled(true);
                    dayContainer.addView(classText);
                }

                dayContainer.measure(perClassWidth, containerHeight - perClassHeight);
                dayContainer.layout(0, 0, perClassWidth, containerHeight - perClassHeight);
                dayContainer.setDrawingCacheEnabled(true);

                classTableRow.addView(dayContainer);
            }

            classTableRow.measure(containerWidth, containerHeight - perClassHeight);
            classTableRow.layout(0, 0, containerWidth, containerHeight - perClassHeight);
            classTableRow.setDrawingCacheEnabled(true);

            classTableContainer.addView(classTableRow);

            classTableContainer.measure(containerWidth, containerHeight);
            classTableContainer.layout(0, 0, containerWidth, containerHeight);
            classTableContainer.setDrawingCacheEnabled(true);

            Bitmap bitmap = classTableContainer.getDrawingCache();
            views.setImageViewBitmap(R.id.imageView, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        SharedPreferences widgetConfig = context.getSharedPreferences(AppConstant.SHARED_PREF_WIDGET, MODE_PRIVATE);
        SharedPreferences.Editor editor = widgetConfig.edit();

        editor.putInt(AppConstant.WIDGET_KEY_WIDTH, newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH));
        editor.putInt(AppConstant.WIDGET_KEY_HEIGHT, newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT));

        editor.commit();
        updateAppWidget(context, appWidgetManager, appWidgetId);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

