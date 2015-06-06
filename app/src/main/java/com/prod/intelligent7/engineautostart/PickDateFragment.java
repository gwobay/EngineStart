package com.prod.intelligent7.engineautostart;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DecimalFormat;
import java.util.Calendar;

public class PickDateFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
        //DateFormat.is24HourFormat(getActivity()));
    }

    TextView viewToFill;
    public void setViewToFill(TextView v)
    {
        viewToFill=v;
    }
    public void onDateSet(DatePicker view, int year, int month, int day) {
        viewToFill.setText((new DecimalFormat("00")).format(year)+"/"+
                (new DecimalFormat("00")).format(month)+"/"+
                        (new DecimalFormat("00")).format(day));
    }
}