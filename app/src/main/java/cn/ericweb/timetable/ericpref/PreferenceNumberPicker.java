package cn.ericweb.timetable.ericpref;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import cn.ericweb.timetable.R;


/**
 * Created by eric on 16-9-18.
 */
public class PreferenceNumberPicker extends DialogPreference {
    private int DEFAULT_VALUE = 1;
    private int mCurrentValue;
    public PreferenceNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
        setPersistent(false);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        Dialog dialog = getDialog();
        NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.number_picker);
        mCurrentValue = numberPicker.getValue();
    }
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if(positiveResult) {

            persistInt(mCurrentValue);
        }
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
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_VALUE);
    }
}
