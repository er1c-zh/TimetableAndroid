package cn.ericweb.timetable;

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

import cn.ericweb.timetable.domain.ClassTable;
import cn.ericweb.timetable.domain.CourseInClassTable;

public class MainActivity extends AppCompatActivity {
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        showTables();
    }

    void showTables() {
        //check if table already exist
        SharedPreferences sharedPref = this.getSharedPreferences("CLASS_TABLE", Context.MODE_PRIVATE);
        if (!sharedPref.contains("classtable")) {
            Intent intent = new Intent(this, cn.ericweb.timetable.QueryClassTable.class);
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
                Intent intent = new Intent(this, cn.ericweb.timetable.QueryClassTable.class);
                startActivity(intent);
                return;
            }

            // 获得课程表容器
            LinearLayout classTableContainer = (LinearLayout) findViewById(R.id.classtable_container);

            // 获得尺寸数据
            Point point = new Point();
            getWindowManager().getDefaultDisplay().getSize(point);
            int containerWidth = point.x;
            int perClassWidth = containerWidth / 8;

            // 添加周几
            LinearLayout weekdayBar = new LinearLayout(this);
            weekdayBar.setOrientation(LinearLayout.HORIZONTAL);
            // 添加周几前的空格
            FrameLayout blank = new FrameLayout(this);
            blank.setBackground(getDrawable(R.drawable.classtable_class_background));
            blank.setLayoutParams(new LinearLayout.LayoutParams(perClassWidth, perClassWidth, 0));
            weekdayBar.addView(blank);

            for (int i = 0; i < 7; i++) {
                FrameLayout weekDayFrameLayout = new FrameLayout(this);
                weekDayFrameLayout.setBackground(getDrawable(R.drawable.classtable_class_background));
                weekDayFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(perClassWidth, perClassWidth, 0));
                TextView weekDayTextView = new TextView(this);
                weekDayTextView.setGravity(Gravity.CENTER);
                int temp = i + 1;
                weekDayTextView.setText(temp + "");
                weekDayFrameLayout.addView(weekDayTextView);
                weekdayBar.addView(weekDayFrameLayout);
            }
            classTableContainer.addView(weekdayBar);

            //添加课程表
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
            for (int day = 0; day < 7; day++) {
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

                    if (originCourse.toString().equals("")) {
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

    //    创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    void refresh() {
        Intent intent = new Intent(this, cn.ericweb.timetable.QueryClassTable.class);
        startActivity(intent);
    }

    void showVersionInfo() {
        Intent intent = new Intent(this, cn.ericweb.timetable.VersionInfo.class);
        startActivity(intent);
    }

    void showSettings() {
        Intent intent = new Intent(this, cn.ericweb.timetable.SettingsActivity.class);
        startActivity(intent);
    }
    //    菜单click listener
    Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.refresh_classtable:
                    refresh();
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