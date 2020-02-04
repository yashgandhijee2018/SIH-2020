package com.example.hp.sih2020;

import Model.Users;
import Prevalent.Prevalent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Vibrator v;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);
        String phonekey = Paper.book().read(Prevalent.phonekey);
        String passwordkey = Paper.book().read(Prevalent.passwordkey);
        progressDialog=new ProgressDialog(this);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (phonekey !=""&&passwordkey!="")
        {
            if((!TextUtils.isEmpty(phonekey))&&(!TextUtils.isEmpty(passwordkey)))
            {
                progressDialog.setTitle("Verifying the user details...");
                progressDialog.setMessage("You are already logged in, We are verifying your details");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                validate(phonekey, passwordkey);
            }
        }
    }

    public void vibrate_function(View view)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26 
            v.vibrate(300);
        }
        Toast.makeText(this, "Hackathon 2020!", Toast.LENGTH_SHORT).show();
    }
    public void login_function(View view)
    {
        Intent i=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(i);
    }
    public void join_now_function(View view)
    {
        Intent i=new Intent(MainActivity.this,JoinNowActivity.class);
        startActivity(i);
    }

    private void validate(final String phone, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Resident").child(phone).exists())
                {
                    Users data=dataSnapshot.child("Resident").child(phone).getValue(Users.class);
                    if(data.getPhone().equals(phone))
                    {
                        if(data.getPassword().equals(password))
                        {
                            Toast.makeText( MainActivity.this, "You are successfully logged in!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Prevalent.currentUser = data;
                            Intent i=new Intent(MainActivity.this, ResidentActivity.class);
                            startActivity(i);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Incorrect Password!! Click forget password or try again", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Why am I seeing this? Report to the developers,cause it's a loop hole", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "You are not registered amigo!!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
