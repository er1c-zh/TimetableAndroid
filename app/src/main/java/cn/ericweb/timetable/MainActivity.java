package cn.ericweb.timetable;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchStartX = event.getX();
            touchStartY = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            touchEndX = event.getX();
            touchEndY = event.getY();

            // 分析是否是左右滑动
            if (touchEndX - touchStartX > 50) {
                if (weekShowwing > 1) {
                    LinearLayout classTableContainer = (LinearLayout) findViewById(R.id.classtable_container_new);
                    classTableContainer.setId(R.id.classtable_container_old);

                    LinearLayout classTableContainerNew = new LinearLayout(this);
                    classTableContainerNew.setId(R.id.classtable_container_new);
                    classTableContainerNew.setOrientation(LinearLayout.VERTICAL);
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.classtable_container);
                    relativeLayout.addView(classTableContainerNew);

                    float containerX = classTableContainer.getX();
                    float containerWidth = classTableContainer.getWidth();
                    ObjectAnimator dispear = ObjectAnimator.ofFloat(classTableContainer, "translationX", containerX, containerX + containerWidth, containerX + containerWidth);
                    dispear.setDuration(500);
                    dispear.addListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {
                            showTables(--weekShowwing);
                            LinearLayout classTable = (LinearLayout) findViewById(R.id.classtable_container_new);
                            ObjectAnimator.ofFloat(classTable, "alpha", 0f, 1f, 1f).setDuration(1000).start();
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    dispear.start();
                }
            } else if (touchStartX - touchEndX > 50) {
                if (weekShowwing < 52) {
                    LinearLayout classTableContainer = (LinearLayout) findViewById(R.id.classtable_container_new);
                    classTableContainer.setId(R.id.classtable_container_old);

                    LinearLayout classTableContainerNew = new LinearLayout(this);
                    classTableContainerNew.setId(R.id.classtable_container_new);
                    classTableContainerNew.setOrientation(LinearLayout.VERTICAL);
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.classtable_container);
                    relativeLayout.addView(classTableContainerNew);

                    float containerX = classTableContainer.getX();
                    float containerWidth = classTableContainer.getWidth();
                    ObjectAnimator dispear = ObjectAnimator.ofFloat(classTableContainer, "translationX", containerX, containerX - containerWidth, containerX - containerWidth);
                    dispear.setDuration(500);
                    dispear.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            showTables(++weekShowwing);
                            LinearLayout classTable = (LinearLayout) findViewById(R.id.classtable_container_new);
                            ObjectAnimator.ofFloat(classTable, "alpha", 0f, 1f, 1f).setDuration(1000).start();
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    dispear.start();
                }
            }
            touchStartX = 0;
            touchStartY = 0;
            touchEndX = 0;
            touchEndY = 0;
        }
        return true;
    }

    private float touchStartX;
    private float touchStartY;
    private float touchEndX;
    private float touchEndY;

    @Override
    protected void onResume() {
        super.onResume();
        // 清理掉之前的课程表
        RelativeLayout classTableContainterRelative = (RelativeLayout) findViewById(R.id.classtable_container);
        classTableContainterRelative.removeAllViews();
        LinearLayout containerNew = new LinearLayout(this);
        containerNew.setId(R.id.classtable_container_new);
        containerNew.setOrientation(LinearLayout.VERTICAL);
        classTableContainterRelative.addView(containerNew);


        // 设置weekShowwing 为 当前周
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        weekShowwing = sharedPref.getInt(getString(R.string.setting_classtable_now_week_key), 1);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showTables(weekShowwing);
    }

    /**
     * 绘制课程表
     */
    @SuppressLint({"SetTextI18n", "NewApi"})
    void showTables(int week2show) {
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
            LinearLayout classTableContainer = (LinearLayout) findViewById(R.id.classtable_container_new);

            // 获得显示几天一周
            int dayToShow = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.setting_classtable_show_weekends_key), true) ? 7 : 5;
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

            // 计算时间
            Date now = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat yyyymmdd = new SimpleDateFormat("yyyyMMdd");
            Date startDate;
            try {
                startDate = yyyymmdd.parse(PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.setting_classtable_now_week_first_week_start_date_key), yyyymmdd.format(now)));
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
                tempDateCalendar.setTime(tempCalendar.getTime());
                tempDateCalendar.add(Calendar.DATE, i);

                weekDayTextView.setText(temp + "" + "\nD" + tempDateCalendar.get(Calendar.DAY_OF_MONTH));
                Calendar nowCalendar = Calendar.getInstance();
                if (nowCalendar.get(Calendar.DAY_OF_YEAR) == tempDateCalendar.get(Calendar.DAY_OF_YEAR)) {
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
                    if (originCourseAdditionalInfo == null || originCourse.toString().equals("") || !originCourse.isCourseExistInWeek(week2show)) {
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
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    // 现在课程表正在展示的周数
    private int weekShowwing;

    // 菜单click listener
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