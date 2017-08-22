package com.kumarangarden.billingsystem.m_UI;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import com.kumarangarden.billingsystem.R;
import com.kumarangarden.billingsystem.m_Model.Customer;
import com.kumarangarden.billingsystem.m_Model.Leave;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 11000257 on 8/22/2017.
 */

public class LeaveDialog extends Dialog {

    public NumberPicker date;
    public EditText days;
    public EditText reason;

    public Button save, cancel;

    public LeaveDialog(@NonNull Context context) {
        super(context);
    }
    public void InitControls() {
        date = (NumberPicker) findViewById(R.id.numberDate);
        date.setMinValue(1);
        date.setMaxValue(31);
        days = (EditText) findViewById(R.id.editDays);
        reason = (EditText) findViewById(R.id.editReason);
        save = (Button) findViewById(R.id.buttonSave);
        cancel = (Button) findViewById(R.id.buttonCancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

    }

    public String getIsValid() {
        String result = "";

       if(days.getText().toString().matches(""))
            result = "Enter Days";
        else if(reason.getText().toString().matches(""))
            result = "Enter Reason";

        return result;
    }

    public Leave getLeave() {
        Leave leave = new Leave();
        //leave.SetName(name.getText().toString());
        leave.days = Float.parseFloat(days.getText().toString());
        leave.reason = reason.getText().toString();
        return leave;
    }

    public void setLeave(Leave leave) throws ParseException {
        String key = leave.GetKey();
        SimpleDateFormat dest = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date r_date = dest.parse(key);
        int t_date = r_date.getDate();
        this.date.setValue(t_date);
        days.setText(leave.days + "");
        reason.setText(leave.reason);
    }

    public void clear() {
        date.setValue(1);
        days.setText("1");
        reason.setText("");
    }
}
