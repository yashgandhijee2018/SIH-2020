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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText InputPhone,InputPassword;
    CheckBox checkBox;
    TextView forget,admin,non_admin;
    Button login;
    Vibrator v;
    ProgressDialog progressDialog;

    String parentDBname="Resident";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InputPhone=(EditText)findViewById(R.id.phone_no_text_view);
        InputPassword=(EditText)findViewById(R.id.password_text_view);
        checkBox=(CheckBox)findViewById(R.id.checkBox);
        forget=(TextView)findViewById(R.id.forget_password_link);
        admin=(TextView)findViewById(R.id.register_as_admin_link);
        non_admin=(TextView)findViewById(R.id.register_as_non_admin_link);
        login=(Button)findViewById(R.id.login_button);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        progressDialog=new ProgressDialog(this);

        Paper.init(this);
    }
    public void login_function(View view)
    {
        String phone,password;
        password=InputPassword.getText().toString();
        phone=InputPhone.getText().toString();

        if(TextUtils.isEmpty(phone))
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
            progressDialog.setTitle("Validating Details");
            progressDialog.setMessage("Hold back amigo while we verify your details..");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            validate(phone,password);
        }
    }

    private void validate(final String phone, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        //Writing to data to android for auto login.
        if(checkBox.isChecked())
        {
            Paper.book().write(Prevalent.phonekey,phone);
            Paper.book().write(Prevalent.passwordkey,password);
        }
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDBname).child(phone).exists())
                {
                    Users data=dataSnapshot.child(parentDBname).child(phone).getValue(Users.class);
                    Prevalent.currentUser=data;
                    if(data.getPhone().equals(phone))
                    {
                        if(data.getPassword().equals(password))
                        {
                            if(parentDBname.equals("Visitors"))
                            {
                                Toast.makeText(LoginActivity.this, "You are successfully logged in as Visitor!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Intent i=new Intent(LoginActivity.this, VisitorActivity.class);
                                startActivity(i);
                            }
                            else if(parentDBname.equals("Resident"))
                            {
                                Toast.makeText(LoginActivity.this, "You are successfully logged in!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Intent i=new Intent(LoginActivity.this, ResidentActivity.class);
                                startActivity(i);
                            }
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Incorrect Password! Try again or click on Forget Password...", Toast.LENGTH_LONG).show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                //deprecated in API 26
                                v.vibrate(300);
                            }
                            progressDialog.dismiss();
                        }
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Why am I seeing this? Report to the developers,cause it's a loop hole", Toast.LENGTH_SHORT).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            v.vibrate(300);
                        }
                        progressDialog.dismiss();
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "You are not registered.. Create a new account now!", Toast.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        v.vibrate(300);
                    }
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void login_as_admin_text_function(View view)
    {
        login.setText("Login as a Visitor");
        admin.setVisibility(View.INVISIBLE);
        non_admin.setVisibility(View.VISIBLE);
        checkBox.setVisibility(View.INVISIBLE);
        parentDBname="Visitors";

        Toast.makeText(this, "For security reasons,We cannot remember information of Visitors...", Toast.LENGTH_SHORT).show();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(300);
        }*/
    }
    public void login_as_non_admin_text_function(View view)
    {
        login.setText("Login");
        admin.setVisibility(View.VISIBLE);
        non_admin.setVisibility(View.INVISIBLE);
        parentDBname="Resident";

        checkBox.setVisibility(View.VISIBLE);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(300);
        }*/
    }
}
