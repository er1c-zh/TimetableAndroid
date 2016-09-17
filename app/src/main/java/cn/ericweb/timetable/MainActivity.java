package cn.ericweb.timetable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

import cn.ericweb.timetable.domain.ClassTable;
import cn.ericweb.timetable.domain.ClassTableAppAdditionalInfo;
import cn.ericweb.timetable.domain.CourseAppAdditionalInfo;
import cn.ericweb.timetable.domain.CourseInClassTable;
import cn.ericweb.timetable.util.AppConstant;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        myToolbar.setSubtitle(R.string.version_info_simple);
        setSupportActionBar(myToolbar);
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);

        // 检查是否是第一次运行
        if (isFirstRun()) {
            SharedPreferences config = getSharedPreferences(AppConstant.SHARED_PREF_CONFIG, MODE_PRIVATE);
            SettingsActivity.setDefaultConfig(config.edit());

            // 设置不是第一次运行
            SharedPreferences appStatus = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor appStatusEditor = appStatus.edit();
            appStatusEditor.putBoolean(KEY_IS_FIRST_RUN, false);
            appStatusEditor.commit();
        }
    }

    /**
     * 检测是否是第一次运行
     *
     * @return 是第一次返回true
     */
    private boolean isFirstRun() {
        SharedPreferences appStatus = getPreferences(MODE_PRIVATE);
        return appStatus.getBoolean(KEY_IS_FIRST_RUN, true);
    }

    /**
     * 重绘课程表的表格
     */
    @Override
    protected void onResume() {
        super.onResume();

        // 刷新数据
        SharedPreferences config = getSharedPreferences(AppConstant.SHARED_PREF_CONFIG, MODE_PRIVATE);
        SettingsActivity.refreshConfigOnEveryStart(config);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showTables();
    }

    /**
     * 绘制课程表
     */
    @SuppressLint({"SetTextI18n", "NewApi"})
    void showTables() {
        //check if table already exist
        SharedPreferences sharedPref = this.getSharedPreferences(AppConstant.SHARED_PREF_CLASSTABLE, Context.MODE_PRIVATE);
        if (!sharedPref.contains(AppConstant.CLASSTABLE_KEY_MAIN)) {
            Intent intent = new Intent(this, QueryClassTable.class);
            startActivity(intent);
        } else {
            // get课程表和附加信息
            Gson gson = new Gson();
            ClassTable classTable;
            ClassTableAppAdditionalInfo classTableAppAdditionalInfo;
            try {
                classTable = gson.fromJson(sharedPref.getString(AppConstant.CLASSTABLE_KEY_MAIN, ""), ClassTable.class);
                classTableAppAdditionalInfo = gson.fromJson(sharedPref.getString(AppConstant.CLASSTABLE_KEY_ADDITIONAL_INFO, ""), ClassTableAppAdditionalInfo.class);
            } catch (Exception e) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove(AppConstant.CLASSTABLE_KEY_MAIN);
                Intent intent = new Intent(this, QueryClassTable.class);
                startActivity(intent);
                return;
            }

            // 获得课程表容器并清空
            LinearLayout classTableContainer = (LinearLayout) findViewById(R.id.classtable_container);
            classTableContainer.removeAllViews();

            // 获得显示几天一周
            int dayToShow = getSharedPreferences(AppConstant.SHARED_PREF_CONFIG, MODE_PRIVATE).getBoolean(AppConstant.IF_WEEKENDS, true) ? 7 : 5;
            // 获得尺寸数据
            // 宽度
            Point point = new Point();
            getWindowManager().getDefaultDisplay().getSize(point);
            int containerWidth = point.x;
            int perClassWidth = containerWidth / (1 + dayToShow);
            // 高度
            Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
            Rect viewCanvasRect = new Rect();
            getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(viewCanvasRect);
            int containerHeight = viewCanvasRect.height() - toolbar.getHeight();
            int perClassHeight = containerHeight / (classTable.getCourseNumberPerDay() + 1);

            // 添加周几
            LinearLayout weekdayBar = new LinearLayout(this);
            weekdayBar.setOrientation(LinearLayout.HORIZONTAL);
            // 添加周几前的周数
            FrameLayout blank = new FrameLayout(this);
            blank.setBackground(getDrawable(R.drawable.classtable_class_background));
            blank.setLayoutParams(new LinearLayout.LayoutParams(perClassWidth, perClassHeight, 0));
            TextView indexOfWeek = new TextView(this);
            indexOfWeek.setGravity(Gravity.CENTER);
            SharedPreferences configSharedPref = getSharedPreferences(AppConstant.SHARED_PREF_CONFIG, MODE_PRIVATE);
            Calendar tempCalendar = Calendar.getInstance();
            int month = tempCalendar.get(Calendar.MONTH) + 1;
            indexOfWeek.setText("W" + configSharedPref.getInt(AppConstant.NOW_WEEK, 0) + "\nM" + month);
            blank.addView(indexOfWeek);
            weekdayBar.addView(blank);

            for (int i = 0; i < dayToShow; i++) {
                FrameLayout weekDayFrameLayout = new FrameLayout(this);
                weekDayFrameLayout.setBackground(getDrawable(R.drawable.classtable_class_background));
                weekDayFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(perClassWidth, perClassHeight, 0));
                TextView weekDayTextView = new TextView(this);
                weekDayTextView.setGravity(Gravity.CENTER);
                int temp = i + 1;
                Calendar tempDateCalendar = Calendar.getInstance();
                int todayOfWeek = tempDateCalendar.get(Calendar.DAY_OF_WEEK) == 1 ? 7 : tempDateCalendar.get(Calendar.DAY_OF_WEEK) - 1;
                tempDateCalendar.add(Calendar.DATE, temp - todayOfWeek);
                weekDayTextView.setText(temp + "" + "\nD" + tempDateCalendar.get(Calendar.DAY_OF_MONTH));
                if (todayOfWeek == temp) {
                    weekDayTextView.setTextColor(getResources().getColor(R.color.colorAccent));
                }
                weekDayFrameLayout.addView(weekDayTextView);
                weekdayBar.addView(weekDayFrameLayout);
            }
            classTableContainer.addView(weekdayBar);

            // 添加课程表
            LinearLayout classTableRow = new LinearLayout(this);
            classTableRow.setOrientation(LinearLayout.HORIZONTAL);

            // 添加课程index
            LinearLayout classIndexContainer = new LinearLayout(this);
            classIndexContainer.setOrientation(LinearLayout.VERTICAL);

            // 时间的计时器
            int hour = classTable.getStartHour();
            int minute = classTable.getStartMinute();
            ArrayList<Integer> classIntervalArrayList = classTable.getIntervalPerCourse();
            for (int classIndex = 1; classIndex <= classTable.getCourseNumberPerDay(); classIndex++) {
                FrameLayout frameLayout = new FrameLayout(this);
                frameLayout.setBackground(getDrawable(R.drawable.classtable_class_background));
                frameLayout.setLayoutParams(new LinearLayout.LayoutParams(perClassWidth, perClassHeight, 0));
                TextView classIndexText = new TextView(this);
                classIndexText.setGravity(Gravity.CENTER);
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
                frameLayout.addView(classIndexText);
                classIndexContainer.addView(frameLayout);
            }
            classTableRow.addView(classIndexContainer);

            // 添加课程
            for (int day = 0; day < dayToShow; day++) {
                // 循环每天
                LinearLayout dayContainer = new LinearLayout(this);
                dayContainer.setOrientation(LinearLayout.VERTICAL);
                for (int classIndex = 0; classIndex < classTable.getCourseNumberPerDay(); classIndex++) {
                    // 循环每个周
                    // 构建盛放每个class的布局
                    FrameLayout classFrameLayout = new FrameLayout(this);
                    // 获得最初的CourseInClassTable
                    CourseInClassTable originCourse = classTable.getCourseInClassTable(day, classIndex);
                    // 获得附加信息
                    CourseAppAdditionalInfo originCourseAdditionalInfo = classTableAppAdditionalInfo.getCourseAppAdditionalInfo(originCourse.getCourse());
                    // 合并同一节课
                    for (int isCourseEqualIndex = classIndex + 1; true; isCourseEqualIndex++) {
                        if (!classTable.getCourseInClassTable(day, isCourseEqualIndex).equals(originCourse) || isCourseEqualIndex >= classTable.getCourseNumberPerDay()) {
                            classFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(perClassWidth, perClassHeight * (isCourseEqualIndex - classIndex), 0));
                            classIndex = isCourseEqualIndex - 1;
                            break;
                        }
                    }
                    // 创建展示的TextView
                    TextView classText = new TextView(this);
                    classText.setTextSize(getResources().getInteger(R.integer.classtable_font_size));
                    classText.setGravity(Gravity.CENTER);

                    // 附加的信息不为null 课程不为空 本周有课
                    if (originCourseAdditionalInfo == null || originCourse.toString().equals("") || !originCourse.isCourseExistInWeek(getSharedPreferences(AppConstant.SHARED_PREF_CONFIG, MODE_PRIVATE).getInt(AppConstant.NOW_WEEK, 1))) {
                        classText.setText("");
                    } else {
                        classText.setText(originCourseAdditionalInfo.getString2Show());

                        GradientDrawable classBackgroundDrawable = (GradientDrawable) getDrawable(R.drawable.classtable_class_background_radius_round_coner);
                        if (classBackgroundDrawable != null) {
                            if (originCourseAdditionalInfo.getColor() != -1) {
                                classBackgroundDrawable.setColor(originCourseAdditionalInfo.getColor());
                            } else {
                                classBackgroundDrawable.setColor(getResources().getColor(R.color.colorClassBackground));
                            }
                        }

                        classFrameLayout.setBackground(classBackgroundDrawable);

                        classText.setOnClickListener(this.classInfoListener);
                    }

                    classFrameLayout.addView(classText);
                    dayContainer.addView(classFrameLayout);
                }
                classTableRow.addView(dayContainer);
            }
            classTableContainer.addView(classTableRow);
        }
    }

    View.OnClickListener classInfoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final TextView textView = (TextView) view;

            AlertDialog.Builder classInfoDialogBuilder = new AlertDialog.Builder(textView.getContext());

            // 获得附加信息类
            final Gson gson = new Gson();
            SharedPreferences tempSharedPref = view.getContext().getSharedPreferences(AppConstant.SHARED_PREF_CLASSTABLE, MODE_PRIVATE);
            String additionalInfoJson = tempSharedPref.getString(AppConstant.CLASSTABLE_KEY_ADDITIONAL_INFO, "");
            ClassTableAppAdditionalInfo additionalInfo;
            final CourseAppAdditionalInfo courseAppAdditionalInfo;
            try {
                additionalInfo = gson.fromJson(additionalInfoJson, ClassTableAppAdditionalInfo.class);
                courseAppAdditionalInfo = additionalInfo.getCourseAppAdditionalInfo((String) textView.getText());
            } catch (Exception e) {
                return;
            }

            // listener
            DialogInterface.OnClickListener reviseAdditionalInfoListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(textView.getContext(), ReviseClassAdditionalInfo.class);
                    intent.putExtra(ReviseClassAdditionalInfo.TARGET_COURSE_ADDITIONAL_INFO, gson.toJson(courseAppAdditionalInfo));
                    startActivity(intent);
                }
            };

            // 加载布局文件
            View content = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_class_info, null);

            // title
            TextView classTitle = (TextView) content.findViewById(R.id.className);
            classTitle.setText(courseAppAdditionalInfo.getCourse().getCourseName());

            // short course name
            TextView shortCourseName = (TextView) content.findViewById(R.id.shortClassName);
            shortCourseName.setText(courseAppAdditionalInfo.getShortOfCourseName());

            // address
            TextView address = (TextView) content.findViewById(R.id.classAddress);
            address.setText(courseAppAdditionalInfo.getCourse().getAddress());

            // teacher name
            TextView teacherName = (TextView) content.findViewById(R.id.classTeacher);
            teacherName.setText(courseAppAdditionalInfo.getCourse().getTeacher().getName());


            classInfoDialogBuilder.setView(content);
            // 开启修改按钮
            classInfoDialogBuilder.setPositiveButton(getString(R.string.dialog_class_info_revise_button), reviseAdditionalInfoListener);
            AlertDialog classInfoDialog = classInfoDialogBuilder.create();
            classInfoDialog.setCanceledOnTouchOutside(true);

            classInfoDialog.show();
        }


    };


    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 显示重新获取的Activity
     */
    void showRefresh() {
        Intent intent = new Intent(this, cn.ericweb.timetable.QueryClassTable.class);
        startActivity(intent);
    }

    /**
     * 显示版本信息
     */
    void showVersionInfo() {
        Intent intent = new Intent(this, cn.ericweb.timetable.VersionInfo.class);
        startActivity(intent);
    }

    /**
     * 展示设置页面
     */
    void showSettings() {
        Intent intent = new Intent(this, cn.ericweb.timetable.SettingsActivity.class);
        startActivity(intent);
    }

    private static final String KEY_IS_FIRST_RUN = "cn.ericweb.IS_FIRST_RUN";
    //    菜单click listener
    Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.refresh_classtable:
                    showRefresh();
                    break;
                case R.id.version_info:
                    showVersionInfo();
                    break;
                case R.id.settings:
                    showSettings();
                    break;
                default:
                    break;
            }
            return true;
        }
    };
}