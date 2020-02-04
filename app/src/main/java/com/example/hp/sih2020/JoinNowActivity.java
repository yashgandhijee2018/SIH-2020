package com.example.hp.sih2020;

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
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class JoinNowActivity extends AppCompatActivity {
    EditText InputName,InputPhone,InputPassword;
    CheckBox checkBox;
    Button create_account;

    Vibrator v;
    ProgressDialog progressDialog;
    Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_now);

        Paper.init(this);
        InputName=(EditText)findViewById(R.id.name_text_view);
        InputPhone=(EditText)findViewById(R.id.phone_no_text_view);
        InputPassword=(EditText)findViewById(R.id.password_text_view);
        checkBox=(CheckBox)findViewById(R.id.checkBox);
        create_account=(Button)findViewById(R.id.login_button);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        progressDialog=new ProgressDialog(this);
    }
    public void create_account_function(View view)
    {
        final String name,phone,password;
        name= InputName.getText().toString();
        password=InputPassword.getText().toString();
        phone=InputPhone.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Enter a valid name", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(300);
            }
        }
        else if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Phone number is required!", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(300);
            }
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Password is required!", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(300);
            }
        }
        else
        {
            //DO HERE
            progressDialog.setTitle("Creating Account...");
            progressDialog.setMessage("Hold back Amigo, While we are creating your account...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            ValidatephoneNumber(name,phone,password);
        }
    }

    private void ValidatephoneNumber(final String name, final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!(dataSnapshot.child("Resident").child(phone).exists()))
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);

                    RootRef.child("Resident").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(JoinNowActivity.this, "You are now successfully registered", Toast.LENGTH_SHORT).show();
                                        Paper.book().write(Prevalent.namekey,name);
                                        if(checkBox.isChecked())
                                        {
                                            Paper.book().write(Prevalent.phonekey,phone);
                                            Paper.book().write(Prevalent.passwordkey,password);
                                        }
                                        progressDialog.dismiss();

                                        Intent intent = new Intent(JoinNowActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(JoinNowActivity.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(JoinNowActivity.this, "This " + phone + " already exists.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Toast.makeText(JoinNowActivity.this, "Please try again using another phone number.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(JoinNowActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
