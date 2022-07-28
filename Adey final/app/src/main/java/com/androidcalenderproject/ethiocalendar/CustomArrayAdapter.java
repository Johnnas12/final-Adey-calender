package com.androidcalenderproject.ethiocalendar;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomArrayAdapter extends ArrayAdapter {
    String [] dayofholiday;
    String [] holiday;
    Activity context;
    public CustomArrayAdapter(Activity context, String[] dayofholiday, String[] holiday){

        super(context,R.layout.customlistitem, dayofholiday);
        this.context = context;
        this.dayofholiday= dayofholiday;
        this.holiday = holiday;

    }
    public View getView(int pos, View v, ViewGroup parent){
        View row =v;

        LayoutInflater inflater  =  context.getLayoutInflater();

        if(row == null) {
            row = inflater.inflate(R.layout.customlistitem, null, true);
            TextView tview = row.findViewById(R.id.textView);
            TextView tview2 = row.findViewById(R.id.textView2);


            tview.setText(dayofholiday[pos]);
            tview2.setText(holiday[pos]);


        }
        return row;
    }

}
