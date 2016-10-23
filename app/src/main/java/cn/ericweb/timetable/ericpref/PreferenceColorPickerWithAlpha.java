package cn.ericweb.timetable.ericpref;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import cn.ericweb.timetable.R;

/**
 * Created by Eric on 2016/10/23.
 */

public class PreferenceColorPickerWithAlpha extends DialogPreference {
    public PreferenceColorPickerWithAlpha(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected View onCreateDialogView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_color_picker_alpha, null);
        return view;
    }
}
