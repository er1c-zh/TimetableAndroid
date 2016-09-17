package cn.ericweb.timetable;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.ericweb.timetable.util.AppConstant;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        toolbar.setTitle("Settings");

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 初始化sharedPref和editor
        this.sharedPref = getSharedPreferences(AppConstant.SHARED_PREF_CONFIG, MODE_PRIVATE);
        this.editor = sharedPref.edit();
        refreshViewStatus();
    }

    private void refreshViewStatus() {
        // classtable
        // 当前周
        TextView resultNowWeek = (TextView) findViewById(R.id.result_now_week);
        resultNowWeek.setText(this.sharedPref.getInt(AppConstant.NOW_WEEK, 0) + "");

        // 是否显示周末
        Switch resultIfWeekends = (Switch) findViewById(R.id.result_if_weekends);
        if (this.sharedPref.getBoolean(AppConstant.IF_WEEKENDS, true)) {
            resultIfWeekends.setChecked(true);
        }
    }

    public void reviseNowWeek(View view) {
        // 获得建造器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.setting_classtable_now_week_title));
        // 获得内部构件
        @SuppressLint("InflateParams") View content = LayoutInflater.from(this).inflate(R.layout.dialog_number_picker, null);
        final NumberPicker numberPicker = (NumberPicker) content.findViewById(R.id.number_picker);
        numberPicker.setMaxValue(52);
        numberPicker.setMinValue(1);
        numberPicker.setValue(this.sharedPref.getInt(AppConstant.NOW_WEEK, 1));


        // 监听器
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int result = numberPicker.getValue();
                editor.putInt(AppConstant.NOW_WEEK, result);

                // 设置开始的周一的日期
                Calendar calendar = Calendar.getInstance();
                // -((离周一有几天) + (离第一周有几天))
                // -2 是因为在中国是以周日返回1 周一返回2...(我怎么就是按每周第一天是周一过的呢)
                int day2Monday = (calendar.get(Calendar.DAY_OF_WEEK) - 2) < 0 ? calendar.get(Calendar.DAY_OF_WEEK) - 2 + 7 : calendar.get(Calendar.DAY_OF_WEEK) - 2;
                int day2FirstWeek = (result - 1) * 7;
                calendar.add(Calendar.DATE, -(day2Monday + day2FirstWeek));
                @SuppressLint("SimpleDateFormat") SimpleDateFormat yyyymmdd = new SimpleDateFormat("yyyyMMdd");
                editor.putString(AppConstant.FIRST_WEEK_START_MONDAY_DATE, yyyymmdd.format(calendar.getTime()));

                editor.commit();
                refreshViewStatus();
            }
        };


        // 建造对话框
        builder.setView(content);
        builder.setPositiveButton("确认", listener);
        builder.setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void reviseIfWeekends(View view) {
        editor.putBoolean(AppConstant.IF_WEEKENDS, !sharedPref.getBoolean(AppConstant.IF_WEEKENDS, true));
        editor.commit();

        refreshViewStatus();
    }

    /**
     * 设置默认设置
     *
     * @param editor 设置的sharedPref的编辑类
     */
    public static void setDefaultConfig(SharedPreferences.Editor editor) {
        // clear config
        editor.clear();

        // set default config

        // 设置当前周数
        editor.putInt(AppConstant.NOW_WEEK, 1);

        // 设置开始的周一的日期
        Calendar calendar = Calendar.getInstance();
        // -((离周一有几天) + (离第一周有几天))
        // -2 是因为在中国是以周日返回1 周一返回2...(我怎么就是按每周第一天是周一过的呢)
        int day2Monday = (calendar.get(Calendar.DAY_OF_WEEK) - 2) < 0 ? calendar.get(Calendar.DAY_OF_WEEK) - 2 + 7 : calendar.get(Calendar.DAY_OF_WEEK) - 2;
        calendar.add(Calendar.DATE, -day2Monday);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat yyyymmdd = new SimpleDateFormat("yyyyMMdd");
        editor.putString(AppConstant.FIRST_WEEK_START_MONDAY_DATE, yyyymmdd.format(calendar.getTime()));

        editor.putBoolean(AppConstant.IF_WEEKENDS, true);

        // commit
        editor.commit();
    }

    public static void refreshConfigOnEveryStart(SharedPreferences sharedPref) {
        // 计算周数
        Date now = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat yyyymmdd = new SimpleDateFormat("yyyyMMdd");
        Date startDate;
        try {
            startDate = yyyymmdd.parse(sharedPref.getString(AppConstant.FIRST_WEEK_START_MONDAY_DATE, yyyymmdd.format(now)));
        } catch (ParseException e) {
            e.printStackTrace();
            startDate = now;
        }
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(AppConstant.NOW_WEEK, ((int) ((now.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000))) / 7 + 1);
        editor.commit();
    }

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
}
