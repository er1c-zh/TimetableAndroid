package cn.ericweb.timetable;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextPaint;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.google.gson.Gson;

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

import static android.content.Context.MODE_PRIVATE;
import static cn.ericweb.timetable.ClasstableFragment.CLASSTABLE_ADDITIONAL_JSON;
import static cn.ericweb.timetable.ClasstableFragment.CLASSTABLE_JSON;
import static cn.ericweb.timetable.ClasstableFragment.FIRST_WEEK_DATE_STRING;
import static cn.ericweb.timetable.ClasstableFragment.IF_SHOW_WEEKENDS;
import static cn.ericweb.timetable.ClasstableFragment.WEEK_TO_SHOW;

/**
 * Implementation of App Widget functionality.
 */
public class ClasstableWidget extends AppWidgetProvider {

    public static int FONT_SCALE = 450;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.classtable_widget);

        // TODO: 2016/10/22 加一个监控情况的IF
        try {
            SharedPreferences config = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences classtableSharedPref = context.getSharedPreferences(AppConstant.SHARED_PREF_CLASSTABLE, MODE_PRIVATE);
            SharedPreferences widgetConfig = context.getSharedPreferences(AppConstant.SHARED_PREF_WIDGET, MODE_PRIVATE);

            // 取得颜色设置
            int fontColor = config.getInt(context.getString(R.string.setting_classtable_widget_text_color_key), Color.TRANSPARENT);
            int backgroundColor = config.getInt(context.getString(R.string.setting_classtable_widget_background_color_key), Color.TRANSPARENT);
            // 防止第一次或清空了数据
            String jsonClasstable = classtableSharedPref.getString(AppConstant.CLASSTABLE_KEY_MAIN, "");
            String jsonClasstableAdditionalInfo = classtableSharedPref.getString(AppConstant.CLASSTABLE_KEY_ADDITIONAL_INFO, "");
            if (jsonClasstable.equals("") || jsonClasstableAdditionalInfo.equals("")) {
                return;
            }
            Bundle bundle = new Bundle();
            // 计算当前周
            // TODO: 2016/10/26 清理代码 加下划线是因为与下面的变量名冲突了
            Calendar _now = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat _yyyymmdd = new SimpleDateFormat("yyyyMMdd");
            Date _startDate;
            try {
                _startDate = _yyyymmdd.parse(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.setting_classtable_now_week_first_week_start_date_key), ""));
            } catch (ParseException e) {
                _startDate = new Date();
            }
            Calendar _startCalendar = Calendar.getInstance();
            _startCalendar.setTime(_startDate);

            long tempWeekCal = (_now.getTimeInMillis() - _startCalendar.getTimeInMillis()) / (7 * 1000 * 24 * 60 * 60) + 1;
            int tempWeek = (int) tempWeekCal;

            bundle.putInt(ClasstableFragment.WEEK_TO_SHOW, tempWeek);
            bundle.putBoolean(ClasstableFragment.IF_SHOW_WEEKENDS, config.getBoolean(context.getString(R.string.setting_classtable_show_weekends_key), true));
            bundle.putString(ClasstableFragment.CLASSTABLE_JSON, classtableSharedPref.getString(AppConstant.CLASSTABLE_KEY_MAIN, ""));
            bundle.putString(ClasstableFragment.CLASSTABLE_ADDITIONAL_JSON, classtableSharedPref.getString(AppConstant.CLASSTABLE_KEY_ADDITIONAL_INFO, ""));


            // 获得尺寸数据
            // 宽度
            int containerWidth = widgetConfig.getInt(AppConstant.WIDGET_KEY_WIDTH, 720);
            // 高度
            int containerHeight = widgetConfig.getInt(AppConstant.WIDGET_KEY_HEIGHT, 1280);
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
            int fontSize = perClassHeight * perClassWidth / ClasstableWidget.FONT_SCALE;
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
            indexOfWeek.setTextColor(fontColor);
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
                weekDayTextView.setBorderWidth(1);
                weekDayTextView.setGravity(Gravity.CENTER);
                weekDayTextView.setWidth(perClassWidth);
                weekDayTextView.setHeight(perClassHeight);
                weekDayTextView.setBorderWidth(1);
                weekDayTextView.setTextSize(fontSize);
                weekDayTextView.setTextColor(fontColor);

                paint = weekDayTextView.getPaint();
                paint.setFakeBoldText(true);


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
                classIndexText.setBorderWidth(1);
                classIndexText.setGravity(Gravity.CENTER);
                classIndexText.setWidth(perClassWidth);
                classIndexText.setHeight(perClassHeight);
                classIndexText.setBorderWidth(1);
                classIndexText.setTextSize(fontSize);
                classIndexText.setTextColor(fontColor);

                paint = classIndexText.getPaint();
                paint.setFakeBoldText(true);


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
                    classText.setBorderWidth(1);
                    classText.setTextSize(fontSize);
                    classText.setTextColor(fontColor);
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
                            int temp = originCourseAdditionalInfo.getColor();
                            classText.setmBgColor(Color.argb(Color.alpha(backgroundColor), Color.red(temp), Color.green(temp), Color.blue(temp)));
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

            classTableContainer.setBackgroundColor(backgroundColor);

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
        SharedPreferences config = context.getSharedPreferences(AppConstant.SHARED_PREF_PHONE, MODE_PRIVATE);
        SharedPreferences widgetConfig = context.getSharedPreferences(AppConstant.SHARED_PREF_WIDGET, MODE_PRIVATE);
        float scale = context.getResources().getDisplayMetrics().density;
        SharedPreferences.Editor editor = widgetConfig.edit();

        editor.putInt(AppConstant.WIDGET_KEY_WIDTH, (int) (newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) * scale + 0.5f));
        editor.putInt(AppConstant.WIDGET_KEY_HEIGHT, (int) (newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT) * scale + 0.5f));

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

