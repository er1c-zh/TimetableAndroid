package cn.ericweb.timetable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.Gson;

import cn.ericweb.timetable.domain.Course;
import cn.ericweb.timetable.domain.CourseAppAdditionalInfo;

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
    }
    private CourseAppAdditionalInfo additionalInfo;
}
