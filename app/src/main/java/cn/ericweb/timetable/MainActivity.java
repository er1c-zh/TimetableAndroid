package cn.ericweb.timetable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Calendar;

import cn.ericweb.timetable.domain.ClassTable;
import cn.ericweb.timetable.domain.CourseInClassTable;
import cn.ericweb.timetable.util.AppConstant;
import cn.ericweb.timetable.util.EricDate;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        myToolbar.setSubtitle(R.string.version_info_simple);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
        myToolbar.setSubtitleTextColor(getResources().getColor(R.color.colorText));
        setSupportActionBar(myToolbar);
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);

        // 检查是否是第一次运行
        if (isFirstRun()) {
            SharedPreferences config = getSharedPreferences(AppConstant.CONFIG_SHARED_PREF, MODE_PRIVATE);
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
        SharedPreferences config = getSharedPreferences(AppConstant.CONFIG_SHARED_PREF, MODE_PRIVATE);
        SettingsActivity.refreshConfigOnEveryStart(config);

        showTables();
    }

    /**
     * 绘制课程表
     */
    @SuppressLint("SetTextI18n")
    void showTables() {
        //check if table already exist
        SharedPreferences sharedPref = this.getSharedPreferences("CLASS_TABLE", Context.MODE_PRIVATE);
        if (!sharedPref.contains("classtable")) {
            Intent intent = new Intent(this, QueryClassTable.class);
            startActivity(intent);
        } else {
            // get课程表
            Gson gson = new Gson();
            ClassTable classTable;
            try {
                classTable = gson.fromJson(sharedPref.getString("classtable", ""), ClassTable.class);
            } catch (Exception e) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove("classtable");
                Intent intent = new Intent(this, QueryClassTable.class);
                startActivity(intent);
                return;
            }

            // 获得课程表容器并清空
            LinearLayout classTableContainer = (LinearLayout) findViewById(R.id.classtable_container);
            classTableContainer.removeAllViews();

            // 获得显示几天一周
            int dayToShow = getSharedPreferences(AppConstant.CONFIG_SHARED_PREF, MODE_PRIVATE).getBoolean(AppConstant.IF_WEEKENDS, true) ? 7 : 5;
            // 获得尺寸数据
            Point point = new Point();
            getWindowManager().getDefaultDisplay().getSize(point);
            int containerWidth = point.x;
            int perClassWidth = containerWidth / (1 + dayToShow);

            // 添加周几
            LinearLayout weekdayBar = new LinearLayout(this);
            weekdayBar.setOrientation(LinearLayout.HORIZONTAL);
            // 添加周几前的周数
            FrameLayout blank = new FrameLayout(this);
            blank.setBackground(getDrawable(R.drawable.classtable_class_background));
            blank.setLayoutParams(new LinearLayout.LayoutParams(perClassWidth, perClassWidth, 0));
            TextView indexOfWeek = new TextView(this);
            indexOfWeek.setGravity(Gravity.CENTER);
            SharedPreferences configSharedPref = getSharedPreferences(AppConstant.CONFIG_SHARED_PREF, MODE_PRIVATE);
            Calendar tempCalendar = Calendar.getInstance();
            int month = tempCalendar.get(Calendar.MONTH) + 1;
            indexOfWeek.setText("W" + configSharedPref.getInt(AppConstant.NOW_WEEK, 0) + "\nM" + month );
            blank.addView(indexOfWeek);
            weekdayBar.addView(blank);

            for (int i = 0; i < dayToShow; i++) {
                FrameLayout weekDayFrameLayout = new FrameLayout(this);
                weekDayFrameLayout.setBackground(getDrawable(R.drawable.classtable_class_background));
                weekDayFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(perClassWidth, perClassWidth, 0));
                TextView weekDayTextView = new TextView(this);
                weekDayTextView.setGravity(Gravity.CENTER);
                int temp = i + 1;
//                EricDate tempEricDate = new EricDate();
                Calendar tempDateCalendar = Calendar.getInstance();
                int todayOfWeek = tempDateCalendar.get(Calendar.DAY_OF_WEEK) == 1 ? 7 : tempDateCalendar.get(Calendar.DAY_OF_WEEK) - 1;
                tempDateCalendar.add(Calendar.DATE, temp - todayOfWeek);
                weekDayTextView.setText(temp + "" + "\nD" + tempDateCalendar.get(Calendar.DAY_OF_MONTH));
                weekDayFrameLayout.addView(weekDayTextView);
                weekdayBar.addView(weekDayFrameLayout);
            }
            classTableContainer.addView(weekdayBar);

            // 添加课程表
            // 添加一个scroll
            ScrollView scrollView = new ScrollView(this);

            LinearLayout classTableRow = new LinearLayout(this);
            classTableRow.setOrientation(LinearLayout.HORIZONTAL);

            // 添加课程index
            LinearLayout classIndexContainer = new LinearLayout(this);
            classIndexContainer.setOrientation(LinearLayout.VERTICAL);
            for (int classIndex = 1; classIndex <= classTable.getCourseNumberPerDay(); classIndex++) {
                FrameLayout frameLayout = new FrameLayout(this);
                frameLayout.setBackground(getDrawable(R.drawable.classtable_class_background));
                frameLayout.setLayoutParams(new LinearLayout.LayoutParams(perClassWidth, perClassWidth, 0));
                TextView classIndexText = new TextView(this);
                classIndexText.setGravity(Gravity.CENTER);
                classIndexText.setText(classIndex + "");
                frameLayout.addView(classIndexText);
                classIndexContainer.addView(frameLayout);
            }
            classTableRow.addView(classIndexContainer);

            // 添加课程
            for (int day = 0; day < dayToShow; day++) {
                LinearLayout dayContainer = new LinearLayout(this);
                dayContainer.setOrientation(LinearLayout.VERTICAL);
                for (int classIndex = 0; classIndex < classTable.getCourseNumberPerDay(); classIndex++) {
                    FrameLayout classFrameLayout = new FrameLayout(this);
                    CourseInClassTable originCourse = classTable.getCourseInClassTable(day, classIndex);

                    for (int isCourseEqualIndex = classIndex + 1; true; isCourseEqualIndex++) {
                        if (!classTable.getCourseInClassTable(day, isCourseEqualIndex).equals(originCourse) || isCourseEqualIndex >= classTable.getCourseNumberPerDay()) {
                            classFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(perClassWidth, perClassWidth * (isCourseEqualIndex - classIndex), 0));
                            classIndex = isCourseEqualIndex - 1;
                            break;
                        }
                    }

                    TextView classText = new TextView(this);
                    classText.setTextSize(getResources().getInteger(R.integer.classtable_font_size));
                    classText.setGravity(Gravity.CENTER);

                    if (originCourse.toString().equals("") || !originCourse.isCourseExistInWeek(getSharedPreferences(AppConstant.CONFIG_SHARED_PREF, MODE_PRIVATE).getInt(AppConstant.NOW_WEEK, 1))) {
                        classText.setText("");
                    } else {
                        classText.setText(originCourse.toString());
                        classFrameLayout.setBackground(getDrawable(R.drawable.classtable_class_background_radius_round_coner));
                    }

                    classFrameLayout.addView(classText);
                    dayContainer.addView(classFrameLayout);
                }
                classTableRow.addView(dayContainer);
            }
            scrollView.addView(classTableRow);

            classTableContainer.addView(scrollView);
        }
    }

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