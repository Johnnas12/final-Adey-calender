package com.androidcalenderproject.ethiocalendar;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EthioCalendarView  extends LinearLayout {
    ImageButton nextBtn,backBtn;
    TextView CurrentDate, gregdate;
    GridView gridview;
    private static final  int MAX_CALENDAR_DAYS=42;//
    Calendar calendar= Calendar.getInstance(Locale.ENGLISH);
    Calendar currentDate = Calendar.getInstance();
    Context context;
    SimpleDateFormat dateFormat= new SimpleDateFormat("MMMM yyyy",Locale.ENGLISH);
    SimpleDateFormat monthformat=  new SimpleDateFormat("MMM",Locale.ENGLISH);
    SimpleDateFormat yearformat = new SimpleDateFormat("yyyy",Locale.ENGLISH);
    SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
    AlertDialog alertDialog;
    GridAdapter gridAdapter;
    ImageButton holidays;
    List<Date> dates=new ArrayList<>();
    List<Events> eventsList =new ArrayList<>();
    int alarmYear, alarmMonth, alarmDay, alarmhour, alarmMinute;
    DBOpenHelper dbopenhelper;
    public EthioCalendarView(Context context) {

        super(context);
    }

    public EthioCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context= context;
        IntializeLayout();
        setUpCalendar();

        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(calendar.MONTH,-1);
                setUpCalendar();


            }
        });
        nextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(calendar.MONTH,1);
                setUpCalendar();
            }
        });
        holidays.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(context.getApplicationContext(), Holiday.class);
                context.startActivity(it);
            }
        });
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View addview = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_event, null);
                EditText EventName = addview.findViewById(R.id.eventname);
                TextView EventTime = addview.findViewById(R.id.eventtime);
                ImageButton settime = addview.findViewById(R.id.seteventtime);
                CheckBox alarmMe = addview.findViewById(R.id.alarmme);
                Calendar datecalender = Calendar.getInstance();
                datecalender.setTime(dates.get(position));
                alarmYear = datecalender.get(Calendar.YEAR);
                alarmMonth = datecalender.get(Calendar.MONTH);
                alarmDay= datecalender.get(Calendar.DAY_OF_MONTH);

                Button addevent = addview.findViewById(R.id.addevent);
                settime.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calender = Calendar.getInstance();
                        int hours = calender.get(Calendar.HOUR_OF_DAY);
                        int minutes = calender.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog= new TimePickerDialog(addview.getContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                               Calendar c= Calendar.getInstance();
                               c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE, minute);
                                c.setTimeZone(TimeZone.getDefault());
                                SimpleDateFormat Hformat = new SimpleDateFormat("K:mm a" ,Locale.ENGLISH);
                                String event_time = Hformat.format(c.getTime());
                                EventTime.setText(event_time);
                                alarmhour= c.get(Calendar.HOUR_OF_DAY);
                                alarmMinute=c.get(Calendar.MINUTE);


                            }
                        }, hours, minutes, false);
                        timePickerDialog.show();

                    }

                });
                String date = eventDateFormat.format(dates.get(position));
                String month = monthformat.format(dates.get(position));
                String year = yearformat.format(dates.get(position));

                addevent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(alarmMe.isChecked()){
                            SaveEvent(EventName.getText().toString(), EventTime.getText().toString(), date, month, year, "on" );
                            setUpCalendar();
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(alarmYear, alarmMonth, alarmDay, alarmhour, alarmMinute);
                            setAlarm(calendar, EventName.getText().toString(), EventTime.getText().toString(), getRequestCode(date
                                    ,EventName.getText().toString(), EventTime.getText().toString() ));
                            alertDialog.dismiss();
                        }else{

                            SaveEvent(EventName.getText().toString(), EventTime.getText().toString(), date, month, year, "off" );
                            setUpCalendar();
                            alertDialog.dismiss();
                        }

                    }
                });

                builder.setView(addview);
                alertDialog=builder.create();
                alertDialog.show();
            }
        });



gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long l) {
        String date= eventDateFormat.format(dates.get(position));
        AlertDialog.Builder builder= new AlertDialog.Builder(context);
        builder.setCancelable(true);
        View showView = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_layout, null);
        RecyclerView recyclerView = showView.findViewById(R.id.eventsRV);
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(showView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        EventShowAdapter eventShowAdapter = new EventShowAdapter(showView.getContext(), CollectEventByDate(date));
        recyclerView.setAdapter(eventShowAdapter);
        eventShowAdapter.notifyDataSetChanged();
        builder.setView(showView);
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                setUpCalendar();
            }
        });


        return true;
    }
});

    }

    private  int getRequestCode(String date, String event, String time){
        int code = 0;
        dbopenhelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbopenhelper.getReadableDatabase();
        Cursor cursor = dbopenhelper.ReadIDEvent(date,event, time, database);

        while(cursor.moveToNext()){
            int eventindex= cursor.getColumnIndex(DBStructure.ID);
             code= cursor.getInt(eventindex);
        }
        cursor.close();
        dbopenhelper.close();
        return code;


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

    private ArrayList<Events> CollectEventByDate(String date){
        ArrayList<Events> arrayList = new ArrayList<>();
        dbopenhelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbopenhelper.getReadableDatabase();
        Cursor cursor = dbopenhelper.ReadEvent(date, database);

        while(cursor.moveToNext()){
            int eventindex= cursor.getColumnIndex(DBStructure.EVENT);
            String event= cursor.getString(eventindex);
            int timeindex= cursor.getColumnIndex(DBStructure.TIME);
            String time= cursor.getString(timeindex);
            int dateindex= cursor.getColumnIndex(DBStructure.DATE);
            String Date= cursor.getString(dateindex);
            int monthindex= cursor.getColumnIndex(DBStructure.MONTH);
            String Month= cursor.getString(monthindex);
            int yearindex= cursor.getColumnIndex(DBStructure.YEAR);
            String Year= cursor.getString(yearindex);

            Events events = new Events(event, time, Date, Month, Year);
            arrayList.add(events);

        }
     cursor.close();
        dbopenhelper.close();
        return arrayList;

    }

    public EthioCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    private void SaveEvent(String event , String time ,String date, String month, String year, String notify){
        DBOpenHelper dbOpenHelper= new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.SaveEvent(event, time, date,  month, year,notify, database);
        dbOpenHelper.close();
        Toast.makeText(context, "Event Saved", Toast.LENGTH_SHORT).show();
}
    private void IntializeLayout(){
        LayoutInflater inflater= (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.calendar_layout,this);
        nextBtn= view.findViewById(R.id.nextBtn);
        backBtn=view.findViewById(R.id.backBtn);
        CurrentDate=view.findViewById(R.id.CurentDatetxt);
        gridview =view.findViewById(R.id.gridview);
        holidays = findViewById(R.id.holiday);
        gregdate = findViewById(R.id.gregdate);


    }

    private void setUpCalendar(){

        String currentdate= dateFormat.format(calendar.getTime());
        Calendar currentDate = Calendar.getInstance();
        
        currentDate.add(Calendar.YEAR,-8);
        currentDate.add(Calendar.MONTH, 5);
        currentDate.add(Calendar.DATE,-7);

        String etYear = (currentDate.get(Calendar.YEAR)) + " ዓ.ም";
        String etMonth = (currentDate.get(Calendar.MONTH)) + " ";
        String etDate = (currentDate.get(Calendar.DATE)) + " ";

        String etmonthword= "";
        if(etMonth.equals("1 ")){
            etmonthword = "መሰከረም";
        }else if(etMonth.equals("2 ")){
            etmonthword = "ጥቅምት";
        }else if(etMonth.equals("3 ")){
            etmonthword = "ህዳር";
        }else if(etMonth.equals("4 ")){
            etmonthword = "ታህሳስ";
        }else if(etMonth.equals("5 ")){
            etmonthword = "ጥር";
        }else if(etMonth.equals("6 ")){
            etmonthword = "የካቲት";
        }else if(etMonth.equals("7 ")){
            etmonthword = "መጋቢት";
        }else if(etMonth.equals("8 ")){
            etmonthword = "ሚያዝያ";
        }else if(etMonth.equals("9 ")){
            etmonthword = "ግንቦት";
        }else if(etMonth.equals("10 ")){
            etmonthword = "ሰኔ";
        }else if(etMonth.equals("11 ")){
            etmonthword = "ሀምሌ";
        }else if(etMonth.equals("12 ")){
            etmonthword = "ነሃሴ ";
        }

        CurrentDate.setText( etmonthword + etDate  + etYear);
        gregdate.setText(currentdate);
       dates.clear();


       Calendar monthCalender = (Calendar) calendar.clone();
       monthCalender.set(Calendar.DAY_OF_MONTH, 1);
       int FirstDayofMonth = monthCalender.get(Calendar.DAY_OF_WEEK)-1;
       monthCalender.add(Calendar.DAY_OF_MONTH, -FirstDayofMonth);
       CollectEventsPerMonth(monthformat.format(calendar.getTime()), yearformat.format(calendar.getTime()));

       while(dates.size()<MAX_CALENDAR_DAYS){
           dates.add(monthCalender.getTime());
           monthCalender.add(Calendar.DAY_OF_MONTH,1);
       }

       gridAdapter = new GridAdapter(context, dates, calendar, eventsList);
       gridview.setAdapter(gridAdapter);
    }

    private  void CollectEventsPerMonth(String month, String year){
        eventsList.clear();
        dbopenhelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbopenhelper.getReadableDatabase();
        Cursor curs= dbopenhelper.ReadEventperMonth(month, year, database);
            while(curs.moveToNext()){
                int eventindex= curs.getColumnIndex(DBStructure.EVENT);
                String event= curs.getString(eventindex);
                int timeindex= curs.getColumnIndex(DBStructure.TIME);
                String time= curs.getString(timeindex);
                int dateindex= curs.getColumnIndex(DBStructure.DATE);
                String date= curs.getString(dateindex);
                int monthindex= curs.getColumnIndex(DBStructure.MONTH);
                String Month= curs.getString(monthindex);
                int yearindex= curs.getColumnIndex(DBStructure.YEAR);
                String Year= curs.getString(yearindex);
                Events events = new Events(event, time, date, Month, Year);
                eventsList.add(events);
            }
            curs.close();
            dbopenhelper.close();
        }
    }

