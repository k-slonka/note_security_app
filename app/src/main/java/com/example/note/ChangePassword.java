package com.example.note;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePassword extends AppCompatActivity {
    Button change_password_button;
    EditText password_old,username,password_new,password_new_repeating;
    DatabaseHelper2 db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        db = new DatabaseHelper2(this);
        username = (EditText)findViewById(R.id.action_user);
        password_old = (EditText)findViewById(R.id.action_password_old);
        password_new = (EditText)findViewById(R.id.action_password_new);

        password_new_repeating = (EditText)findViewById(R.id.action_password_new_repeating);

        change_password_button = (Button)findViewById(R.id.action_change_password_button);
        change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString().trim();
                String pwd = password_old.getText().toString().trim();
                String new_pwd = password_new.getText().toString().trim();
            String new_pwd_repeating = password_new_repeating.getText().toString().trim();
//                Toast.makeText(ChangePassword.this,"Poprawne hasło"+ new_pwd,Toast.LENGTH_SHORT).show();
//                Toast.makeText(ChangePassword.this,"Poprawne hasło powt" +new_pwd_repeating ,Toast.LENGTH_SHORT).show();



                Boolean res = db.checkUser(user, pwd);
                if(res == true && new_pwd.equals(new_pwd_repeating))
                {
                    Toast.makeText(ChangePassword.this,"Poprawne hasło",Toast.LENGTH_SHORT).show();
                    Intent HomePage = new Intent(ChangePassword.this,MainActivity.class);
                    startActivity(HomePage);
                }
                else
                {
                    Toast.makeText(ChangePassword.this,"Incorrect pasword",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    }
