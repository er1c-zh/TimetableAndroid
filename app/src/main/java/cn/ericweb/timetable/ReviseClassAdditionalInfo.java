package cn.ericweb.timetable;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.gson.Gson;

import cn.ericweb.timetable.domain.ClassTableAppAdditionalInfo;
import cn.ericweb.timetable.domain.Course;
import cn.ericweb.timetable.domain.CourseAppAdditionalInfo;
import cn.ericweb.timetable.util.AppConstant;

public class ReviseClassAdditionalInfo extends AppCompatActivity {

    public static final String TARGET_COURSE_ADDITIONAL_INFO = "cn.ericweb.ReviseClassAdditionalInfo.target_course_additional_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise_class_additional_info);

        Intent sourceIntent = getIntent();
        Gson gson = new Gson();
        String targetAdditionalInfoJson = sourceIntent.getStringExtra(TARGET_COURSE_ADDITIONAL_INFO);

        try {
            additionalInfo = gson.fromJson(targetAdditionalInfoJson, CourseAppAdditionalInfo.class);
        } catch (Exception e) {
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.revise_additional_info_toolbar);
        toolbar.setTitle(additionalInfo.getCourse().getCourseName());
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.revise_additional_info_sure:
                        updateAdditionalInfo();
                        finish();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        initDefaultValue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_revise_class_additional_info, menu);
        return true;
    }

    private void updateAdditionalInfo() {
        SharedPreferences sharedPref = getSharedPreferences(AppConstant.SHARED_PREF_CLASSTABLE, MODE_PRIVATE);
        Gson gson = new Gson();
        ClassTableAppAdditionalInfo classTableAppAdditionalInfo;
        try {
            String json = sharedPref.getString(AppConstant.CLASSTABLE_KEY_ADDITIONAL_INFO, "");
            classTableAppAdditionalInfo = gson.fromJson(json, ClassTableAppAdditionalInfo.class);
        } catch (Exception e) {
            return;
        }

        // 修改缩写
        EditText newShortName = (EditText) findViewById(R.id.new_short_course_name);
        additionalInfo.setShortOfCourseName(newShortName.getText().toString());

        // 更新背景颜色
        TextView newBackgroundColor = (TextView) findViewById(R.id.new_background_color);
        additionalInfo.setColor(((ColorDrawable) newBackgroundColor.getBackground()).getColor());
        // 更新container
        classTableAppAdditionalInfo.replaceCourseAppAdditionalInfo(additionalInfo);

        // 更新SharedPref
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(AppConstant.CLASSTABLE_KEY_ADDITIONAL_INFO, gson.toJson(classTableAppAdditionalInfo));
        editor.commit();
    }

    private void initDefaultValue() {
        // 缩写
        EditText shortName = (EditText) findViewById(R.id.new_short_course_name);
        shortName.setText(additionalInfo.getShortOfCourseName());

        // 课程磁贴的颜色
        TextView classBackgroundColor = (TextView) findViewById(R.id.new_background_color);
        if (additionalInfo.getColor() == -1) {
            classBackgroundColor.setBackgroundColor(getResources().getColor(R.color.colorClassBackground));
        } else {
            classBackgroundColor.setBackgroundColor(additionalInfo.getColor());
        }
    }

    public void reviseColor(final View view) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View colorPicker = LayoutInflater.from(this).inflate(R.layout.dialog_color_picker, null);

        // 初始化选择器
        final TextView canvas = (TextView) colorPicker.findViewById(R.id.canvas);
        final EditText r = (EditText) colorPicker.findViewById(R.id.red);
        final EditText g = (EditText) colorPicker.findViewById(R.id.green);
        final EditText b = (EditText) colorPicker.findViewById(R.id.blue);

        int color = ((ColorDrawable) findViewById(R.id.new_background_color).getBackground()).getColor();
        canvas.setBackgroundColor(color);

        TextWatcher colorParamsChanged = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int red = Integer.parseInt(String.valueOf(r.getText()));
                    int green = Integer.parseInt(String.valueOf(g.getText()));
                    int blue = Integer.parseInt(String.valueOf(b.getText()));
                    int newColor = Color.rgb(red, green, blue);
                    canvas.setBackgroundColor(newColor);
                } catch (Exception e) {
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        r.setText(Color.red(color) + "");
        r.addTextChangedListener(colorParamsChanged);
        g.setText(Color.green(color) + "");
        g.addTextChangedListener(colorParamsChanged);
        b.setText(Color.blue(color) + "");
        b.addTextChangedListener(colorParamsChanged);


        dialogBuilder.setView(colorPicker);

        dialogBuilder.setPositiveButton(getString(R.string.revise_class_additional_info_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int color = ((ColorDrawable) canvas.getBackground()).getColor();
                view.setBackgroundColor(color);
            }
        });
        AlertDialog productDialog = dialogBuilder.create();
        productDialog.setCanceledOnTouchOutside(true);
        productDialog.show();
    }

    private CourseAppAdditionalInfo additionalInfo;
}
