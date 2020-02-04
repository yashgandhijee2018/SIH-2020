package com.example.hp.sih2020;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class VisitorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);
        Toast.makeText(this, "Inside visitor activity", Toast.LENGTH_SHORT).show();
    }
    public void logout_function(View view)
    {
        Intent i=new Intent(VisitorActivity.this,MainActivity.class);
        startActivity(i);
    }
}
