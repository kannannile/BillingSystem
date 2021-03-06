package com.kumarangarden.billingsystem.m_UI;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

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

public class LeaveViewHolder extends RecyclerView.ViewHolder {
    private TextView date, leaveText;

    public LeaveViewHolder(View itemView) {
        super(itemView);
        date = (TextView) itemView.findViewById(R.id.textDate);
        leaveText = (TextView) itemView.findViewById(R.id.textLeave);
    }
    public void Initialize(Leave leave) throws ParseException {
        String key = leave.GetKey();
        String date = key.substring(key.length() - 2);
        this.date.setText(date); //"தேதி: " +
        leaveText.setText( leave.days + " : "  + leave.reason);
    }

    public String GetName() {
        return date.getText().toString()  + leaveText.getText();
    }
}
