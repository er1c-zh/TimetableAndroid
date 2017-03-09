package cn.ericweb.timetable;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedList;

import cn.ericweb.timetable.domain.Activity;
import cn.ericweb.timetable.domain.Classtable;
import cn.ericweb.timetable.domain.Color;
import cn.ericweb.timetable.domain.Subject;
import cn.ericweb.timetable.utils.AppConstant;

public class AddActActivity extends AppCompatActivity {
    private Activity newActivity;
    private Classtable classtable;
    private LinkedList<String> subjectTitles;
    private String subjectTitle;

    private TextView subject;
    private TextView actTitle;
    private TextView actLocation;
    private Switch actIsClass;

    private ImageButton noCycleAdd;
    private ListView noCycleList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.newActivity = new Activity();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_act);

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_acty_toolbar);
        toolbar.setTitle(getString(R.string.add_acty_toolbar_title));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // init classtable
        SharedPreferences classtableSharedPref = getSharedPreferences(AppConstant.SHARED_PREF_CLASSTABLE, MODE_PRIVATE);
        String jsonClasstable = classtableSharedPref.getString(AppConstant.CLASSTABLE_KEY_MAIN, "");

        try {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-ddHH:mm:ss").create();
            this.classtable = gson.fromJson(jsonClasstable, Classtable.class);
            this.subjectTitles = new LinkedList<>();
            for(Subject _s : this.classtable.getSubjects()) {
                this.subjectTitles.add(_s.getTitle());
            }
            this.subjectTitles.add(getString(R.string.activity_editor_no_subject_content));
        } catch (Exception e) {
            finish();
        }

        // init view
        LinearLayout targetSubjectll = (LinearLayout) findViewById(R.id.activity_editor_subject_info_container);
        this.subject = (TextView) findViewById(R.id.activity_editor_subject_title_content);
        this.subject.setText(getString(R.string.activity_editor_no_subject_content));
        this.subjectTitle = getString(R.string.activity_editor_no_subject_content);
        this.actTitle = (TextView) findViewById(R.id.activity_editor_acty_title_content);
        this.actLocation = (TextView) findViewById(R.id.activity_editor_acty_location_content);
        this.actIsClass = (Switch) findViewById(R.id.activity_editor_acty_is_class_content);

        // 所属课程
        targetSubjectll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle(getString(R.string.activity_editor_subject_title))
                        .setSingleChoiceItems(subjectTitles.toArray(new String[0]), subjectTitles.indexOf(subject.getText().toString()), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                subjectTitle = subjectTitles.get(which);
                            }
                        })
                        .setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                subject.setText(subjectTitle);

                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                subjectTitle = subject.getText().toString();
                            }
                        })
                        .show();
            }
        });

        // bgcolor
        LinearLayout bgColorContainer = (LinearLayout) findViewById(R.id.activity_editor_acty_background_color_container);
        final TextView newBackgroundColor = (TextView) findViewById(R.id.activity_editor_acty_background_color_content);
        int _tmpColor = getResources().getColor(R.color.colorClassBackground);
        this.newActivity.setColorBg(new Color(255, android.graphics.Color.red(_tmpColor), android.graphics.Color.green(_tmpColor), android.graphics.Color.blue(_tmpColor)));
        newBackgroundColor.setBackgroundColor(_tmpColor);
        bgColorContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
                View colorPicker = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_color_picker, null);
                final SeekBar red = (SeekBar) colorPicker.findViewById(R.id.seekBarRed);
                final SeekBar green = (SeekBar) colorPicker.findViewById(R.id.seekBarGreen);
                final SeekBar blue = (SeekBar) colorPicker.findViewById(R.id.seekBarBlue);

                final TextView canvas = (TextView) colorPicker.findViewById(R.id.color_info);

                int _color = android.graphics.Color.rgb(newActivity.getColorBg().getA(), newActivity.getColorBg().getG(), newActivity.getColorBg().getB());

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
                        Color _newColor = newActivity.getColorBg();
                        _newColor.setR(android.graphics.Color.red(color));
                        _newColor.setG(android.graphics.Color.green(color));
                        _newColor.setB(android.graphics.Color.blue(color));
                        newActivity.setColorBg(_newColor);
                    }
                });
                dialogBuilder.setTitle(R.string.activity_editor_background_color);
                AlertDialog productDialog = dialogBuilder.create();
                productDialog.setCanceledOnTouchOutside(true);
                productDialog.show();
            }
        });
        // 周期性switch
        Switch isCycle = (Switch) findViewById(R.id.activity_editor_acty_time_iscycle_content);
        isCycle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LinearLayout yes = (LinearLayout) findViewById(R.id.activity_editor_acty_time_cycle_yes_container);
                LinearLayout no = (LinearLayout) findViewById(R.id.activity_editor_acty_time_cycle_no_container);

                if(isChecked) {
                    yes.setVisibility(LinearLayout.VISIBLE);
                    no.setVisibility(LinearLayout.INVISIBLE);
                } else {
                    yes.setVisibility(LinearLayout.INVISIBLE);
                    no.setVisibility(LinearLayout.VISIBLE);
                }
            }
        });

        // 非周期性
        this.noCycleAdd = (ImageButton) findViewById(R.id.activity_editor_acty_time_cycle_no_add_button);
        this.noCycleList = (ListView) findViewById(R.id.activity_editor_acty_time_cycle_no_list);

        noCycleAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
