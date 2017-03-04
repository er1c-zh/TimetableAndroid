package cn.ericweb.timetable;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class ClasstableFragment extends Fragment {

    public static final String WEEK_TO_SHOW = "cn.ericweb.ClasstableFragment.week2show";
    public static final String IF_SHOW_WEEKENDS = "cn.ericweb.ClasstableFragment.ifShowWeekends";
    public static final String CLASSTABLE_JSON = "cn.ericweb.ClasstableFragment.classtableJson";
    public static final String CLASSTABLE_ADDITIONAL_JSON = "cn.ericweb.ClasstableFragment.classtableAdditionalJson";
    public static final String CONTAINER_WIDTH = "cn.ericweb.ClasstableFragment.containerWidth";
    public static final String CONTAINER_HEIGHT = "cn.ericweb.ClasstableFragment.containerHeight";
    public static final String FIRST_WEEK_DATE_STRING = "cn.ericweb.ClasstableFragment.firstWeekDateString";

    public ClasstableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            savedInstanceState = getArguments();
            int week2show = savedInstanceState.getInt(WEEK_TO_SHOW);
            // get课程表
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-ddHH:mm:ss").create();
            Classtable classTable = gson.fromJson(savedInstanceState.getString(CLASSTABLE_JSON), Classtable.class);

            // 获得课程表容器并清空
            LinearLayout classTableContainer = new LinearLayout(getContext());
            classTableContainer.setOrientation(LinearLayout.VERTICAL);
            classTableContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 0));

            // 获得显示几天一周
            int dayToShow = savedInstanceState.getBoolean(IF_SHOW_WEEKENDS) ? 7 : 5;
            // 获得尺寸数据
            // 宽度
            int containerWidth = savedInstanceState.getInt(CONTAINER_WIDTH);
            int perClassWidth = containerWidth / (1 + dayToShow);
            // 高度
            int containerHeight = savedInstanceState.getInt(CONTAINER_HEIGHT);
            int perClassHeight = containerHeight / (classTable.getNumberOfClassPerDay() + 1);

            // 添加周几
            LinearLayout weekdayBar = new LinearLayout(getContext());
            weekdayBar.setOrientation(LinearLayout.HORIZONTAL);
            // 添加周几前的周数
            FrameLayout blank = new FrameLayout(getContext());
            blank.setBackground(getContext().getDrawable(R.drawable.classtable_class_background));
            blank.setLayoutParams(new LinearLayout.LayoutParams(perClassWidth, perClassHeight, 0));
            TextView indexOfWeek = new TextView(getContext());
            indexOfWeek.setGravity(Gravity.CENTER);

            // 计算时间
            Date now = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat yyyymmdd = new SimpleDateFormat("yyyyMMdd");
            Date startDate;
            try {
                startDate = yyyymmdd.parse(savedInstanceState.getString(FIRST_WEEK_DATE_STRING));
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
                FrameLayout weekDayFrameLayout = new FrameLayout(getContext());
                weekDayFrameLayout.setBackground(getContext().getDrawable(R.drawable.classtable_class_background));
                weekDayFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(perClassWidth, perClassHeight, 0));
                TextView weekDayTextView = new TextView(getContext());
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
            int tmp = 0;
            classTableContainer.addView(weekdayBar);

            // 添加课程表
            LinearLayout classTableRow = new LinearLayout(getContext());
            classTableRow.setOrientation(LinearLayout.HORIZONTAL);

            // 添加课程index
            LinearLayout classIndexContainer = new LinearLayout(getContext());
            classIndexContainer.setOrientation(LinearLayout.VERTICAL);

            // 时间的计时器
            int hour = classTable.getClassStartTime().getHour();
            int minute = classTable.getClassStartTime().getMin();
            ArrayList<Integer> classIntervalArrayList = classTable.getIntervals();
            for (int classIndex = 1; classIndex <= classTable.getNumberOfClassPerDay(); classIndex++) {
                FrameLayout frameLayout = new FrameLayout(getContext());
                frameLayout.setBackground(getContext().getDrawable(R.drawable.classtable_class_background));
                frameLayout.setLayoutParams(new LinearLayout.LayoutParams(perClassWidth, perClassHeight, 0));
                TextView classIndexText = new TextView(getContext());
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

            RelativeLayout classContainerRL = new RelativeLayout(getContext());
            classContainerRL.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            classTableRow.addView(classContainerRL);
            for(Activity claxx : classTable.getActivities()) {
                // 判断本周是否存在这节课
                if(claxx.getExistedWeek().charAt(week2show) == '1') {
                    int _weekday = claxx.getWhichWeekday();
                    int _indexStart = claxx.getStartClassIndex();
                    int _indexEnd = claxx.getEndClassIndex();
                    int _howLong = _indexEnd - _indexStart + 1;

                    RelativeLayout classContainer = new RelativeLayout(getContext());
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(perClassWidth, perClassHeight * _howLong);
                    lp.setMargins(perClassWidth * (_weekday), perClassHeight * (_indexStart), 0, 0);
                    classContainer.setLayoutParams(lp);
                    EricRoundedCornerTextview classTextview = new EricRoundedCornerTextview(getContext());
                    classTextview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    classTextview.setBorderWidth(1);
                    classTextview.setTextSize(getResources().getInteger(R.integer.classtable_font_size));
                    classTextview.setGravity(Gravity.CENTER);
                    classTextview.setText(claxx.getTitle());

                    GradientDrawable classBackgroundDrawable = (GradientDrawable) getContext().getDrawable(R.drawable.classtable_class_background_radius_round_coner);
                    if (classBackgroundDrawable != null) {
                        classBackgroundDrawable.setColor(getResources().getColor(R.color.colorClassBackground));
                        if(claxx.getColorBg() != null) {
                            cn.ericweb.timetable.domain.Color _c = claxx.getColorBg();
                            classBackgroundDrawable.setColor(Color.argb(_c.getR(), _c.getG(), _c.getB(), _c.getA()));
                        }
                    }
                    classTextview.setBackground(classBackgroundDrawable);

                    // 添加磁铁的点击功能
                    classTextview.setOnClickListener(classInfoListener);

                    classContainer.addView(classTextview);

                    classContainerRL.addView(classContainer);
                }
            }
            classTableContainer.addView(classTableRow);
//
//            // 添加课程
//            for (int day = 0; day < dayToShow; day++) {
//                // 循环每天
//                LinearLayout dayContainer = new LinearLayout(getContext());
//                dayContainer.setOrientation(LinearLayout.VERTICAL);
//                for (int classIndex = 0; classIndex < classTable.getCourseNumberPerDay(); classIndex++) {
//                    // 循环每个周
//                    // 构建盛放每个class的布局
//                    FrameLayout classFrameLayout = new FrameLayout(getContext());
//                    // 获得最初的CourseInClassTable
//                    CourseInClassTable originCourse = classTable.getCourseInClassTable(day, classIndex);
//                    // 获得附加信息
//                    CourseAppAdditionalInfo originCourseAdditionalInfo = classTableAppAdditionalInfo.getCourseAppAdditionalInfo(originCourse.getCourse());
//                    // 合并同一节课
//                    for (int isCourseEqualIndex = classIndex + 1; true; isCourseEqualIndex++) {
//                        if (!classTable.getCourseInClassTable(day, isCourseEqualIndex).equals(originCourse) || isCourseEqualIndex >= classTable.getCourseNumberPerDay()) {
//                            classFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(perClassWidth, perClassHeight * (isCourseEqualIndex - classIndex), 0));
//                            classIndex = isCourseEqualIndex - 1;
//                            break;
//                        }
//                    }
//                    // 创建展示的TextView
//                    EricRoundedCornerTextview classText = new EricRoundedCornerTextview(getContext());
//                    classText.setBorderWidth(1);
//                    classText.setTextSize(getResources().getInteger(R.integer.classtable_font_size));
//                    classText.setGravity(Gravity.CENTER);
//
//                    // 附加的信息不为null 课程不为空 本周有课
//                    if (originCourseAdditionalInfo == null || originCourse.toString().equals("") || !originCourse.isCourseExistInWeek(week2show)) {
//                        classText.setText("");
//                    } else {
//                        classText.setText(originCourseAdditionalInfo.getString2Show());
//
////                        GradientDrawable classBackgroundDrawable = (GradientDrawable) getContext().getDrawable(R.drawable.classtable_class_background_radius_round_coner);
////                        if (classBackgroundDrawable != null) {
////                            if (originCourseAdditionalInfo.getColor() != -1) {
////                                classBackgroundDrawable.setColor(originCourseAdditionalInfo.getColor());
////                            } else {
////                                classBackgroundDrawable.setColor(getResources().getColor(R.color.colorClassBackground));
////                            }
////                        }
////
////                        classFrameLayout.setBackground(classBackgroundDrawable);
//
//                        if (originCourseAdditionalInfo.getColor() != -1) {
//                            classText.setmBgColor(originCourseAdditionalInfo.getColor());
//                        }
//                        classText.setOnClickListener(classInfoListener);
//                    }
//
//                    classFrameLayout.addView(classText);
//                    dayContainer.addView(classFrameLayout);
//                }
//                classTableRow.addView(dayContainer);
//            }
//            classTableContainer.addView(classTableRow);
//
//
            // Inflate the layout for this fragment
            return classTableContainer;
        } catch (Exception e) {
            return null;
        }
    }

    View.OnClickListener classInfoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final TextView textView = (TextView) view;

            AlertDialog.Builder classInfoDialogBuilder = new AlertDialog.Builder(textView.getContext());

            // get课程表
            final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-ddHH:mm:ss").create();
            SharedPreferences tempSharedPref = view.getContext().getSharedPreferences(AppConstant.SHARED_PREF_CLASSTABLE, getContext().MODE_PRIVATE);
            String classtableJson = tempSharedPref.getString(AppConstant.CLASSTABLE_KEY_MAIN, "");
            Classtable classtable;
            Activity _activity = null;
            try {
                classtable = gson.fromJson(classtableJson, Classtable.class);
            } catch (Exception e) {
                return;
            }

            for(Activity _s : classtable.getActivities()) {
                if(_s.getTitle().equals(((TextView) view).getText())) {
                    _activity = _s;
                    break;
                }
            }
            final Activity activity = _activity;
            if(activity == null) {
                return;
            }

            // listener
            DialogInterface.OnClickListener reviseAdditionalInfoListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(textView.getContext(), ReviseClassAdditionalInfo.class);
                    intent.putExtra(ReviseClassAdditionalInfo.TARGET_SUBJECT, gson.toJson(activity.getSubject()));
                    startActivity(intent);
                }
            };

            // 加载布局文件
            View content = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_class_info, null);

            // title
            TextView classTitle = (TextView) content.findViewById(R.id.className);
            classTitle.setText(activity.getSubject().getTitle());

            // short course name
            TextView shortCourseName = (TextView) content.findViewById(R.id.shortClassName);
            shortCourseName.setText(activity.getSubject().getShortTitle());

            // address
            TextView address = (TextView) content.findViewById(R.id.classAddress);
            address.setText(activity.getLocation());

            // teacher name
            TextView teacherName = (TextView) content.findViewById(R.id.classTeacher);
            teacherName.setText(activity.getSubject().getTeacher().getName());


            classInfoDialogBuilder.setView(content);
            // 开启修改按钮
            classInfoDialogBuilder.setPositiveButton(getString(R.string.dialog_class_info_revise_button), reviseAdditionalInfoListener);
            AlertDialog classInfoDialog = classInfoDialogBuilder.create();
            classInfoDialog.setCanceledOnTouchOutside(true);

            classInfoDialog.show();
        }
    };
}
