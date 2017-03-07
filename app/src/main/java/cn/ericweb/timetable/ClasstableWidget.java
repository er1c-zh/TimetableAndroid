package cn.ericweb.timetable;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//import cn.ericweb.timetable.domain.ClassTable;
//import cn.ericweb.timetable.domain.ClassTableAppAdditionalInfo;
//import cn.ericweb.timetable.domain.CourseAppAdditionalInfo;
//import cn.ericweb.timetable.domain.CourseInClassTable;
import cn.ericweb.timetable.domain.Activity;
import cn.ericweb.timetable.domain.Classtable;
import cn.ericweb.timetable.ericandroid.EricRoundedCornerTextview;
import cn.ericweb.timetable.utils.AppConstant;

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
            if (jsonClasstable.equals("")) {
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

            // 获得尺寸数据
            float scale = context.getResources().getDisplayMetrics().density;
            // 宽度
            int containerWidth = widgetConfig.getInt(AppConstant.WIDGET_KEY_WIDTH, (int) (250 * scale + 0.5f));
            // 高度
            int containerHeight = widgetConfig.getInt(AppConstant.WIDGET_KEY_HEIGHT, (int) (250 * scale + 0.5f));
            bundle.putInt(ClasstableFragment.CONTAINER_WIDTH, containerWidth);
            bundle.putInt(ClasstableFragment.CONTAINER_HEIGHT, containerHeight);
            bundle.putString(ClasstableFragment.FIRST_WEEK_DATE_STRING, config.getString(context.getString(R.string.setting_classtable_now_week_first_week_start_date_key), ""));
            int week2show = bundle.getInt(WEEK_TO_SHOW);
            // get课程表和附加信息
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-ddHH:mm:ss").create();
            Classtable classtable;
            classtable = gson.fromJson(bundle.getString(CLASSTABLE_JSON), Classtable.class);

            // 获得课程表容器并清空
            LinearLayout classTableContainer = new LinearLayout(context);
            classTableContainer.setOrientation(LinearLayout.VERTICAL);

            // 获得显示几天一周
            int dayToShow = bundle.getBoolean(IF_SHOW_WEEKENDS) ? 7 : 5;
            // 宽度
            int perClassWidth = containerWidth / (1 + dayToShow);
            // 高度
            int perClassHeight = containerHeight / (classtable.getNumberOfClassPerDay() + 1);
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
            int hour = classtable.getClassStartTime().getHour();
            int minute = classtable.getClassStartTime().getMin();
            ArrayList<Integer> classIntervalArrayList = classtable.getIntervals();
            for (int classIndex = 1; classIndex <= classtable.getNumberOfClassPerDay(); classIndex++) {
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

            RelativeLayout classContainerRL = new RelativeLayout(context);
            classContainerRL.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            classTableRow.addView(classContainerRL);
            for(Activity claxx : classtable.getActivities()) {
                // 判断本周是否存在这节课
                if(claxx.getExistedWeek().charAt(week2show) == '1') {
                    int _weekday = claxx.getWhichWeekday();
                    int _indexStart = claxx.getStartClassIndex();
                    int _indexEnd = claxx.getEndClassIndex();
                    int _howLong = _indexEnd - _indexStart + 1;

                    RelativeLayout classContainer = new RelativeLayout(context);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(perClassWidth, perClassHeight * _howLong);
                    lp.setMargins(perClassWidth * (_weekday), perClassHeight * (_indexStart), 0, 0);
                    classContainer.setLayoutParams(lp);
                    EricRoundedCornerTextview classTextview = new EricRoundedCornerTextview(context);
                    classTextview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    classTextview.setBorderWidth(1);
                    classTextview.setTextSize(context.getResources().getInteger(R.integer.classtable_font_size));
                    classTextview.setGravity(Gravity.CENTER);
                    classTextview.setText(claxx.getTitle());
                    classTextview.setTextColor(fontColor);

                    GradientDrawable classBackgroundDrawable = (GradientDrawable) context.getDrawable(R.drawable.classtable_class_background_radius_round_coner);
                    if (classBackgroundDrawable != null) {
                        classBackgroundDrawable.setColor(context.getResources().getColor(R.color.colorClassBackground));
                        if(claxx.getColorBg() != null) {
                            cn.ericweb.timetable.domain.Color _c = claxx.getColorBg();
                            classBackgroundDrawable.setColor(Color.argb(_c.getA(), _c.getR(), _c.getG(), _c.getB()));
                        }
                    }
                    classTextview.setBackground(classBackgroundDrawable);

                    classContainer.addView(classTextview);

                    classContainerRL.addView(classContainer);
                }
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
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        final ComponentName cn = new ComponentName(context, ClasstableWidget.class);
        this.onUpdate(context, mgr, mgr.getAppWidgetIds(cn));
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

