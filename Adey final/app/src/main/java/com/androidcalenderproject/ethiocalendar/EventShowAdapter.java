package com.androidcalenderproject.ethiocalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventShowAdapter extends RecyclerView.Adapter<EventShowAdapter.viewHolder> {
    Context context;
    DBOpenHelper dbOpenHelper;

    public EventShowAdapter(Context context, ArrayList<Events> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    ArrayList<Events> arrayList ;

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_rowlayout, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        int x = position;
     final Events events = arrayList.get(position);
        holder.Event.setText(events.getEVENT());
        holder.DateTxt.setText(events.getDATE());
        holder.Time.setText(events.getTIME());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCalEvent(events.getEVENT(), events.getDATE(), events.getTIME());
                arrayList.remove(x);
                notifyDataSetChanged();
            }
        });

        if(isAlarmed(events.getDATE(), events.getEVENT(), events.getTIME())){
            holder.setAlarm.setImageResource(R.drawable.ic_action_notification_on);

        }else{
            holder.setAlarm.setImageResource(R.drawable.ic_action_notification_off);

        }
        Calendar datecalendar = Calendar.getInstance();
        datecalendar.setTime(convertStringToDate(events.getDATE()));
        int alarmyear= datecalendar.get(Calendar.YEAR);
        int alarmmonth = datecalendar.get(Calendar.MONTH);
        int alarmday = datecalendar.get(Calendar.DAY_OF_MONTH);

        Calendar timecalendar = Calendar.getInstance();
        timecalendar.setTime(convertStringToTime(events.getTIME()));
        int alarmHour = timecalendar.get(Calendar.HOUR_OF_DAY);
        int alarmminute = timecalendar.get(Calendar.MINUTE);


        holder.setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAlarmed(events.getDATE(), events.getEVENT(), events.getTIME())){
                    holder.setAlarm.setImageResource(R.drawable.ic_action_notification_off);
                    cancelAlarm(getRequestCode(events.getDATE(), events.getEVENT(), events.getTIME()));
                    updateEvent(events.getDATE(), events.getEVENT(), events.getTIME(), "off");
                    notifyDataSetChanged();
                }else{
                    holder.setAlarm.setImageResource(R.drawable.ic_action_notification_on);
                    Calendar alarmCalender = Calendar.getInstance();
                    alarmCalender.set(alarmyear, alarmmonth, alarmday, alarmHour, alarmminute);
                    setAlarm(alarmCalender, events.getEVENT(), events.getTIME(), getRequestCode(events.getDATE(), events.getEVENT(), events.getTIME()));
                    updateEvent(events.getDATE(), events.getEVENT(), events.getTIME(), "on");
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
       TextView DateTxt, Event, Time;
       Button delete;
       ImageButton  setAlarm;
       public viewHolder(@NonNull View itemView) {
           super(itemView);
           DateTxt = itemView.findViewById(R.id.eventdate);
           Event= itemView.findViewById(R.id.eventname);
           Time = itemView.findViewById(R.id.eventtime);
           delete = itemView.findViewById(R.id.delete);
           setAlarm = itemView.findViewById(R.id.alarmbtn);

       }
   }
    private Date convertStringToDate(String eventDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
        Date date  = null;
        try {
            date = format.parse(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    private Date convertStringToTime(String eventDate){
        SimpleDateFormat format = new SimpleDateFormat("kk:mm", Locale.CHINESE);
        Date date  = null;
        try {
            date = format.parse(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private  void deleteCalEvent(String event, String date, String time){
        dbOpenHelper= new DBOpenHelper(context);
        SQLiteDatabase database1 = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.deleteEvent(event, date, time, database1);
        dbOpenHelper.close();
   }

   private  boolean isAlarmed(String date, String event, String time){
        boolean alaramed = false;
       dbOpenHelper = new DBOpenHelper(context);
       SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
       Cursor cursor = dbOpenHelper.ReadIDEvent(date,event, time, database);

       while(cursor.moveToNext()){
           int notifyIndex= cursor.getColumnIndex(DBStructure.Notify);
           String notify= cursor.getString(notifyIndex);
           if (notify.equals("on")){
               alaramed= true;
           }else{
               alaramed= false;
           }
       }
       cursor.close();
       dbOpenHelper.close();
       return alaramed;
   }
    private void setAlarm(Calendar calender, String event, String time, int RequestCode){
        Intent intent = new Intent(context.getApplicationContext(), AlarmReciever.class);
        intent.putExtra("event", event);
        intent.putExtra("time", time);
        intent.putExtra("id", RequestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RequestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pendingIntent);

    }
    private void cancelAlarm(int RequestCode){
        Intent intent = new Intent(context.getApplicationContext(), AlarmReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RequestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }
    private  int getRequestCode(String date, String event, String time){
        int code = 0;
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadIDEvent(date,event, time, database);

        while(cursor.moveToNext()){
            int Idinex= cursor.getColumnIndex(DBStructure.ID);
            code= cursor.getInt(Idinex);
        }
        cursor.close();
        dbOpenHelper.close();
        return code;


    }
    private void updateEvent(String date, String event, String time, String notify){
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.updateevent(date, event, time, notify, database);
        dbOpenHelper.close();

    }
}
