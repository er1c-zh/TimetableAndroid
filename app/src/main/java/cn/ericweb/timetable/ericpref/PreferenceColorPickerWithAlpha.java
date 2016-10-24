package cn.ericweb.timetable.ericpref;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import cn.ericweb.timetable.R;

/**
 * Created by Eric on 2016/10/23.
 */

public class PreferenceColorPickerWithAlpha extends DialogPreference {
    static int DEFAULT_COLOR = Color.WHITE;
    int mCurrentColor;
    public PreferenceColorPickerWithAlpha(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            Dialog dialog = getDialog();
            SeekBar red = (SeekBar) dialog.findViewById(R.id.seekBarRed);
            SeekBar green = (SeekBar) dialog.findViewById(R.id.seekBarGreen);
            SeekBar blue = (SeekBar) dialog.findViewById(R.id.seekBarBlue);
            SeekBar alpha = (SeekBar) dialog.findViewById(R.id.seekBarAlpha);

            mCurrentColor = Color.argb(alpha.getProgress(), red.getProgress(), green.getProgress(), blue.getProgress());
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        persistInt(mCurrentColor);
    }

    @Override
    protected View onCreateDialogView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_color_picker_alpha, null);
        final SeekBar red = (SeekBar) view.findViewById(R.id.seekBarRed);
        final SeekBar green = (SeekBar) view.findViewById(R.id.seekBarGreen);
        final SeekBar blue = (SeekBar) view.findViewById(R.id.seekBarBlue);
        final SeekBar alpha = (SeekBar) view.findViewById(R.id.seekBarAlpha);
        final TextView sample = (TextView) view.findViewById(R.id.color_info);

        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sample.setBackgroundColor(Color.rgb(red.getProgress(), green.getProgress(), blue.getProgress()));
                sample.setAlpha((float) (alpha.getProgress() / 255.0));
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
        alpha.setOnSeekBarChangeListener(listener);

        return view;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        SeekBar red = (SeekBar) view.findViewById(R.id.seekBarRed);
        SeekBar green = (SeekBar) view.findViewById(R.id.seekBarGreen);
        SeekBar blue = (SeekBar) view.findViewById(R.id.seekBarBlue);
        SeekBar alpha = (SeekBar) view.findViewById(R.id.seekBarAlpha);

        int redInt = Color.red(mCurrentColor);
        int greenInt = Color.green(mCurrentColor);
        int blueInt = Color.blue(mCurrentColor);
        int alphaInt = Color.alpha(mCurrentColor);

        red.setProgress(redInt);
        green.setProgress(greenInt);
        blue.setProgress(blueInt);
        alpha.setProgress(alphaInt);

        TextView sample = (TextView) view.findViewById(R.id.color_info);
        sample.setBackgroundColor(Color.rgb(redInt, greenInt, blueInt));
        sample.setAlpha((float) (alphaInt / 255.0));
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            mCurrentColor = this.getPersistedInt(DEFAULT_COLOR);
        } else {
            // Set default state from the XML attribute
            mCurrentColor = (Integer) defaultValue;
            persistInt(mCurrentColor);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_COLOR);
    }
}
