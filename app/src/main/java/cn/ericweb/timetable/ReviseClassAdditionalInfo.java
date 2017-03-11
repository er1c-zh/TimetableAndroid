package cn.ericweb.timetable;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.util.LinkedList;

import cn.ericweb.timetable.domain.Activity;
import cn.ericweb.timetable.domain.Class;
import cn.ericweb.timetable.domain.Classtable;
import cn.ericweb.timetable.domain.Color;
import cn.ericweb.timetable.domain.Subject;
import cn.ericweb.timetable.utils.AppConstant;
import cn.ericweb.timetable.utils.RefreshWidget;
import cn.ericweb.timetable.utils.StringUtils;

public class ReviseClassAdditionalInfo extends AppCompatActivity {
    public static final String TARGET_SUBJECT = "cn.ericweb.ReviseClassAdditionalInfo.target_subject";
    public static final String TARGET_ACTIVITY = "cn.ericweb.ReviseClassAdditionalInfo.target_activity";

    private Classtable classtable;
    private Activity activity;
    private Subject oldSubject;
    private Subject newSubject;

    private TextView subjectTextView;
    private EditText shortNameTextView;
    private EditText locationTextView;
    private Switch isClassSwitch;
    private TextView classBackgroundColorTextView;
    private LinearLayout classBackgroundColorContainer;
    private LinearLayout timeLL;

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
            this.activity = gson.fromJson(sourceIntent.getStringExtra(TARGET_ACTIVITY), Activity.class);
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

        this.subjectTextView = (TextView) findViewById(R.id.activity_editor_subject_title_content);
        this.shortNameTextView = (EditText) findViewById(R.id.activity_editor_acty_title_content);

        this.locationTextView = (EditText) findViewById(R.id.activity_editor_acty_location_content);
        this.isClassSwitch = (Switch) findViewById(R.id.activity_editor_acty_is_class_content);
        this.classBackgroundColorTextView = (TextView) findViewById(R.id.activity_editor_acty_background_color_content);
        this.classBackgroundColorContainer = (LinearLayout) findViewById(R.id.activity_editor_acty_background_color_container);
        this.timeLL = (LinearLayout) findViewById(R.id.activity_editor_acty_time_father);
        this.timeLL.setVisibility(LinearLayout.INVISIBLE);

        initDefaultValue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_revise_class_additional_info, menu);
        return true;
    }

    private void updateAdditionalInfo() {

        LinkedList<Activity> oldList = this.classtable.getActivities();
        // 创建新的subject
        this.newSubject = this.oldSubject;
        // 生成新颜色
        TextView newBackgroundColor = (TextView) findViewById(R.id.activity_editor_acty_background_color_content);
        ColorDrawable _colorTmp = (ColorDrawable) newBackgroundColor.getBackground();
        int _color = _colorTmp.getColor();
        int red = (_color & 0xff0000) >> 16;
        int green = (_color & 0x00ff00) >> 8;
        int blue = (_color & 0x0000ff);
        int alpha = _colorTmp.getAlpha();
        Color newBg = new Color(red, green, blue, alpha);

        // isclass & location
        LinkedList<Activity> tmpList = new LinkedList<>();
        for(Activity _a : oldList) {
            if(_a.equals(this.activity)) {
                _a.setLocation(this.locationTextView.getText().toString());
                this.activity.setLocation(this.locationTextView.getText().toString());
                if(this.isClassSwitch.isChecked()) {
                    _a.setClass(true);
                    this.activity.setClass(true);
                } else {
                    _a.setClass(false);
                    this.activity.setClass(false);
                }
            }
            tmpList.add(_a);
        }
        this.classtable.setActivities(tmpList);

        if(this.activity.isClass()) {
            // 修改缩写
            this.newSubject.setShortTitle(this.shortNameTextView.getText().toString());

            // 更新背景颜色和subject
            LinkedList<Subject> _subjectList = this.classtable.getSubjects();
            _subjectList.remove(this.oldSubject);
            _subjectList.add(this.newSubject);
            this.classtable.setSubjects(_subjectList);

            LinkedList<Activity> _tmpList = new LinkedList<>();
            for(Activity _a : this.classtable.getActivities()) {
                if (_a.isClass() && _a.getSubject().equals(this.oldSubject)) {
                    _a.setSubject(this.newSubject);
                    _a.setTitle(_a.getSubject().getShortTitle());
                    _a.setColorBg(newBg);
                }
                _tmpList.add(_a);
            }
            this.classtable.setActivities(_tmpList);
        } else {
            // 修改背景
            LinkedList<Activity> _tmpList = new LinkedList<>();
            for(Activity _a : this.classtable.getActivities()) {
                if(_a.equals(this.activity)) {
                    _a.setColorBg(newBg);
                }
                _tmpList.add(_a);
            }
            this.classtable.setActivities(_tmpList);
        }

        // 更新SharedPref
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-ddHH:mm:ss").create();
        SharedPreferences sharedPref = getSharedPreferences(AppConstant.SHARED_PREF_CLASSTABLE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(AppConstant.CLASSTABLE_KEY_MAIN, gson.toJson(this.classtable));
        editor.commit();

        // 更新widget
        RefreshWidget.updateWidgetClasstable(getApplicationContext());
    }

    private void initDefaultValue() {
        // 课程 subject
        this.subjectTextView.setText(this.oldSubject.getTitle());
        // is class
        if(this.activity.isClass()) {
            this.isClassSwitch.setChecked(true);
        } else {
            this.isClassSwitch.setChecked(false);
        }
        // 缩写
        this.shortNameTextView.setText(this.oldSubject.getShortTitle());

        // location
        this.locationTextView.setText(this.activity.getLocation());

        // 课程磁贴的颜色
        Activity _tmpActivity = null;
        for(Activity _a : this.classtable.getActivities()) {
            if(_a.getSubject().equals(this.oldSubject)) {
                _tmpActivity = _a;
                break;
            }
        }
        if (_tmpActivity == null || _tmpActivity.getColorBg() == null) {
            this.classBackgroundColorTextView.setBackgroundColor(getResources().getColor(R.color.colorClassBackground));
        } else {
            Color _c = _tmpActivity.getColorBg();
            this.classBackgroundColorTextView.setBackgroundColor(android.graphics.Color.rgb(_c.getR(), _c.getG(), _c.getB()));
        }

        this.classBackgroundColorContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
                View colorPicker = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_color_picker, null);
                final SeekBar red = (SeekBar) colorPicker.findViewById(R.id.seekBarRed);
                final SeekBar green = (SeekBar) colorPicker.findViewById(R.id.seekBarGreen);
                final SeekBar blue = (SeekBar) colorPicker.findViewById(R.id.seekBarBlue);

                final TextView canvas = (TextView) colorPicker.findViewById(R.id.color_info);

                final TextView newBackgroundColor = (TextView) findViewById(R.id.activity_editor_acty_background_color_content);


                Activity _tmpActivity = null;
                for(Activity _a : classtable.getActivities()) {
                    if(_a.getSubject().equals(oldSubject)) {
                        _tmpActivity = _a;
                        break;
                    }
                }

                int _color = -1;
                if (_tmpActivity == null || _tmpActivity.getColorBg() == null) {
                    _color = getResources().getColor(R.color.colorClassBackground);
                } else {
                    Color _c = _tmpActivity.getColorBg();
                    _color = android.graphics.Color.rgb(_c.getR(), _c.getG(), _c.getB());
                }
                canvas.setBackgroundColor(_color);
                red.setProgress(android.graphics.Color.red(_color));
                green.setProgress(android.graphics.Color.green(_color));
                blue.setProgress(android.graphics.Color.blue(_color));

                dialogBuilder.setView(colorPicker);

                SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        canvas.setBackgroundColor(android.graphics.Color.rgb(red.getProgress(), green.getProgress(), blue.getProgress()));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                };

                red.setOnSeekBarChangeListener(listener);
                green.setOnSeekBarChangeListener(listener);
                blue.setOnSeekBarChangeListener(listener);

                dialogBuilder.setPositiveButton(getString(R.string.revise_class_additional_info_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int color = ((ColorDrawable) canvas.getBackground()).getColor();
                        newBackgroundColor.setBackgroundColor(color);
                    }
                });
                dialogBuilder.setTitle(R.string.activity_editor_background_color);
                AlertDialog productDialog = dialogBuilder.create();
                productDialog.setCanceledOnTouchOutside(true);
                productDialog.show();
            }
        });
    }
}
