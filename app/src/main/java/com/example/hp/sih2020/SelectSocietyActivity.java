package com.example.hp.sih2020;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.internal.Util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SelectSocietyActivity extends AppCompatActivity {
    ListView listView;
    String society_name="";
    Vibrator v;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_society);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        listView=(ListView)findViewById(R.id.list_view);
        final ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add("Southern Ocean Residency");
        arrayList.add("Pragati Apartments");
        arrayList.add("The Modern Society");
        arrayList.add("Elephanta Islands");
        arrayList.add("Silver Liviing Residency");
        arrayList.add("Loknayak Appartments");
        arrayList.add("Gokuldham Society");
        arrayList.add("Vision Residency");
        arrayList.add("The Enchanted Garden");
        arrayList.add("Ekta Residents");

        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                /// Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                // Set the text size 25 dip for ListView each item
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30);
                tv.setTextColor(Color.BLUE);
                // Return the view
                return view;
            }
        };

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                society_name=arrayList.get(i).toString();
            }
        });
    }

    public void continue_function(View view)
    {
        if(TextUtils.isEmpty(society_name))
        {
            Toast.makeText(this, "Please select your society to continue!", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(300);
            }
        }
        else
        {
            Intent i=new Intent(SelectSocietyActivity.this,MainActivity.class);
            startActivity(i);
        }
    }
}
