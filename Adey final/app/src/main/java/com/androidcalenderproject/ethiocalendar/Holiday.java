package com.androidcalenderproject.ethiocalendar;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class Holiday extends AppCompatActivity {
    ListView lv;

    String[] names = {"መሰከረም 1", "መሰከረም 17", "ጥቀምት 09", "ታህሳስ 29" , "ጥር 11" , "የካቲት 23", "ሚያዝያ 14", "ሚያዝያ 16", "ሚያዝያ 23", "ሚያዝያ 25", "ሚያዝያ 27", "ግንቦት 20", "ሐመሌ 03",};
    String[] roles = {"እንቁጣጣሽ", "መስቀል", "መዉሊድ", "ገና", "ጥመቀት", "አድዋ ድል", "ስቅለት", "ትንሳኤ", "የላባደሮች ቀን", "ኢድ-አልፈጥር", "የአርበኞች ቀን", "ግነቦት 20", "ኢድ-አልአድሃ - አረፋ"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listactivity);
        lv =findViewById(R.id.listView);


        CustomArrayAdapter customArrayAdapter= new CustomArrayAdapter(this,names, roles);
        lv.setAdapter(customArrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(),studentList[i], Toast.LENGTH_LONG).show();
            }
        });
    }
}