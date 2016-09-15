package cn.ericweb.timetable;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
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
        SharedPreferences sharedPref = getSharedPreferences(AppConstant.SHARED_PREF_CLASSTABLE,MODE_PRIVATE);
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
    }

    private CourseAppAdditionalInfo additionalInfo;
}
