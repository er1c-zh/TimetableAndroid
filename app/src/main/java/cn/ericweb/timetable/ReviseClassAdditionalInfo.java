package cn.ericweb.timetable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedList;

import cn.ericweb.timetable.domain.Activity;
import cn.ericweb.timetable.domain.Classtable;
import cn.ericweb.timetable.domain.Color;
import cn.ericweb.timetable.domain.Subject;
import cn.ericweb.timetable.utils.AppConstant;

public class ReviseClassAdditionalInfo extends AppCompatActivity {

    public static final String TARGET_SUBJECT = "cn.ericweb.ReviseClassAdditionalInfo.target_subject";
    private Classtable classtable;
    private Subject oldSubject;
    private Subject newSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.classtable = null;
        this.oldSubject = null;
        this.newSubject = null;

        setContentView(R.layout.activity_revise_class_additional_info);

        Intent sourceIntent = getIntent();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-ddHH:mm:ss").create();
        String jsonSubject = sourceIntent.getStringExtra(TARGET_SUBJECT);
        SharedPreferences sharedPref = getSharedPreferences(AppConstant.SHARED_PREF_CLASSTABLE, MODE_PRIVATE);
        try {
            String json = sharedPref.getString(AppConstant.CLASSTABLE_KEY_MAIN, "");
            this.classtable = gson.fromJson(json, Classtable.class);
            this.oldSubject = gson.fromJson(jsonSubject, Subject.class);
        } catch (Exception e) {
            finish();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.revise_additional_info_toolbar);
        toolbar.setTitle(this.oldSubject.getTitle());
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
        // 创建新的subject
        this.newSubject = this.oldSubject;
        // 修改缩写
        EditText newShortName = (EditText) findViewById(R.id.new_short_course_name);
        this.oldSubject.setShortTitle(newShortName.getText().toString());

        // 更新背景颜色
        TextView newBackgroundColor = (TextView) findViewById(R.id.new_background_color);
        ColorDrawable _colorTmp = (ColorDrawable) newBackgroundColor.getBackground();
        int _color = _colorTmp.getColor();
        int red = (_color & 0xff0000) >> 16;
        int green = (_color & 0x00ff00) >> 8;
        int blue = (_color & 0x0000ff);
        int alpha = _colorTmp.getAlpha();

        this.classtable.getSubjects().add(this.classtable.getSubjects().indexOf(this.oldSubject), this.newSubject);

        LinkedList<Activity> _tmpList = new LinkedList<>();
        for(Activity _a : this.classtable.getActivities()) {
            if (_a.getSubject().equals(this.oldSubject)) {
                _a.setSubject(this.newSubject);
                _a.setColorBg(new Color(red, green, blue, alpha));
            }
            _tmpList.add(_a);
        }
        this.classtable.setActivities(_tmpList);
//        additionalInfo.setColor(((ColorDrawable) newBackgroundColor.getBackground()).getColor());
        // 更新container
//        classTableAppAdditionalInfo.replaceCourseAppAdditionalInfo(additionalInfo);

        // 更新SharedPref
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-ddHH:mm:ss").create();
        SharedPreferences sharedPref = getSharedPreferences(AppConstant.SHARED_PREF_CLASSTABLE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(AppConstant.CLASSTABLE_KEY_MAIN, gson.toJson(this.classtable));
        editor.commit();
    }

    private void initDefaultValue() {
        // 缩写
        EditText shortName = (EditText) findViewById(R.id.new_short_course_name);
        shortName.setText(this.oldSubject.getShortTitle());

        // 课程磁贴的颜色
//        TextView classBackgroundColor = (TextView) findViewById(R.id.new_background_color);
//        if (this..getColor() == -1) {
//            classBackgroundColor.setBackgroundColor(getResources().getColor(R.color.colorClassBackground));
//        } else {
//            classBackgroundColor.setBackgroundColor(additionalInfo.getColor());
//        }
    }

    public void reviseColor(final View view) {
//        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        View colorPicker = LayoutInflater.from(this).inflate(R.layout.dialog_color_picker, null);
//        final SeekBar red = (SeekBar) colorPicker.findViewById(R.id.seekBarRed);
//        final SeekBar green = (SeekBar) colorPicker.findViewById(R.id.seekBarGreen);
//        final SeekBar blue = (SeekBar) colorPicker.findViewById(R.id.seekBarBlue);
//
//        final TextView canvas = (TextView) colorPicker.findViewById(R.id.color_info);
//
//        final TextView newBackgroundColor = (TextView) findViewById(R.id.new_background_color);
//
//        canvas.setBackgroundColor(additionalInfo.getColor());
//        red.setProgress(Color.red(additionalInfo.getColor()));
//        green.setProgress(Color.green(additionalInfo.getColor()));
//        blue.setProgress(Color.blue(additionalInfo.getColor()));
//
//        dialogBuilder.setView(colorPicker);
//
//        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                canvas.setBackgroundColor(Color.rgb(red.getProgress(), green.getProgress(), blue.getProgress()));
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        };
//
//        red.setOnSeekBarChangeListener(listener);
//        green.setOnSeekBarChangeListener(listener);
//        blue.setOnSeekBarChangeListener(listener);
//
//        dialogBuilder.setPositiveButton(getString(R.string.revise_class_additional_info_confirm), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                int color = ((ColorDrawable) canvas.getBackground()).getColor();
//                newBackgroundColor.setBackgroundColor(color);
//            }
//        });
//        dialogBuilder.setTitle(R.string.revise_class_additional_info_class_background_color_id);
//        AlertDialog productDialog = dialogBuilder.create();
//        productDialog.setCanceledOnTouchOutside(true);
//        productDialog.show();
    }

//    private CourseAppAdditionalInfo additionalInfo;
}
