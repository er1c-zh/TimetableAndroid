package cn.ericweb.timetable.ericpref;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.ericweb.timetable.R;


/**
 * Created by eric on 16-9-18.
 */
public class PreferenceNowWeekNumberPicker extends DialogPreference {
    private int DEFAULT_VALUE = 1;
    private int mCurrentValue;

    public PreferenceNowWeekNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            Dialog dialog = getDialog();
            NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.number_picker);
            mCurrentValue = numberPicker.getValue();
        }
    }

    protected void onDialogClosed(boolean positiveResult) {
        persistInt(mCurrentValue);

        // 设置开始的周一的日期
        Calendar calendar = Calendar.getInstance();
        // -((离周一有几天) + (离第一周有几天))
        // -2 是因为在中国是以周日返回1 周一返回2...(我怎么就是按每周第一天是周一过的呢)
        int day2Monday = (calendar.get(Calendar.DAY_OF_WEEK) - 2) < 0 ? calendar.get(Calendar.DAY_OF_WEEK) - 2 + 7 : calendar.get(Calendar.DAY_OF_WEEK) - 2;
        int day2FirstWeek = (mCurrentValue - 1) * 7;
        calendar.add(Calendar.DATE, -(day2Monday + day2FirstWeek));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat yyyymmdd = new SimpleDateFormat("yyyyMMdd");
        getSharedPreferences().edit().putString(getContext().getString(R.string.setting_classtable_now_week_first_week_start_date_key, ""), yyyymmdd.format(calendar.getTime())).commit();
    }

    @Override
    protected View onCreateDialogView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_number_picker, null);
        NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
        numberPicker.setMaxValue(52);
        numberPicker.setMinValue(1);
        return view;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
        numberPicker.setValue(mCurrentValue);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            mCurrentValue = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            // Set default state from the XML attribute
            mCurrentValue = (Integer) defaultValue;
            persistInt(mCurrentValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_VALUE);
    }
}
