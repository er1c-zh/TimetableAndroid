package cn.ericweb.timetable;

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
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 初始化sharedPref和editor
        this.sharedPref = getSharedPreferences(AppConstant.CONFIG_SHARED_PREF, MODE_PRIVATE);
        this.editor = sharedPref.edit();
        refreshViewStatus();
    }

    private void refreshViewStatus() {
        // classtable
        // 当前周
        TextView resultNowWeek = (TextView) findViewById(R.id.result_now_week);
        resultNowWeek.setText(this.sharedPref.getInt(AppConstant.NOW_WEEK, 1) + "");

        // 是否显示周末
        Switch resultIfWeekends = (Switch) findViewById(R.id.result_if_weekends);
        if (this.sharedPref.getBoolean(AppConstant.IF_WEEKENDS, true) == true) {
            resultIfWeekends.setChecked(true);
        }
    }

    public void reviseNowWeek(View view) {
        // 获得建造器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.setting_classtable_now_week_title));
        // 获得内部构件
        View content = LayoutInflater.from(this).inflate(R.layout.dialog_number_picker, null);
        final NumberPicker numberPicker = (NumberPicker) content.findViewById(R.id.number_picker);
        numberPicker.setMaxValue(52);
        numberPicker.setMinValue(1);


        // 监听器
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int result = numberPicker.getValue();
                editor.putInt(AppConstant.NOW_WEEK, result);
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
        editor.putInt(AppConstant.NOW_WEEK, 1);
        editor.putBoolean(AppConstant.IF_WEEKENDS, true);

        // commit
        editor.commit();
    }

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
}
