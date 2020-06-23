package com.example.note;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class ChangePassword extends AppCompatActivity {
    Button change_password_button;
    EditText password_old,username,password_new,password_new_repeating;
    DatabaseHelper2 db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        db = new DatabaseHelper2(this);
        password_old = (EditText)findViewById(R.id.action_password_old);
        password_new = (EditText)findViewById(R.id.action_password_new);
        password_new_repeating = (EditText)findViewById(R.id.action_password_new_repeating);
        change_password_button = (Button)findViewById(R.id.action_change_password_button);
        change_password_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                String pwd = password_old.getText().toString().trim();
                String new_pwd = password_new.getText().toString().trim();
            String new_pwd_repeating = password_new_repeating.getText().toString().trim();

                Boolean res = null;
                try {
                    res = db.checkUser(pwd);
                    if(res == true && new_pwd.equals(new_pwd_repeating))
                    {
                        db.changePassword(new_pwd);
                        Intent HomePage = new Intent(ChangePassword.this,MainActivity.class);
                        startActivity(HomePage);
                    }
                    else
                    {
                        Toast.makeText(ChangePassword.this,"Incorrect pasword",Toast.LENGTH_SHORT).show();
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    }
