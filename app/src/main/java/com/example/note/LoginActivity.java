package com.example.note;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class LoginActivity extends AppCompatActivity {
    EditText mTextUsername;
    EditText mTextPassword;
    Button mButtonLogin;
    TextView mTextViewRegister;
    DatabaseHelper2 db;
    ViewGroup progressView;
    private EditText editor;
    static String pwd2;
    protected boolean isProgressShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        db = new DatabaseHelper2(this);
        mTextUsername = (EditText)findViewById(R.id.edittext_username);
        mTextPassword = (EditText)findViewById(R.id.edittext_password);
        mButtonLogin = (Button)findViewById(R.id.button_login);
        mTextViewRegister = (TextView)findViewById(R.id.textview_register);
        mTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                editor = findViewById(R.id.edittext_password);
                String pwd = editor.getText().toString().trim();

                try {

                    Boolean res = db.checkUser(pwd);

                    setPwd(pwd);

                if(res == true)
                {
                    Intent HomePage = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(HomePage);
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Login Error",Toast.LENGTH_SHORT).show();
                }  } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            }
        });
    }

    // Getter
    public static String getPwd() {
        return pwd2;
    }

    // Setter
    public void setPwd(String pwd2) {
        this.pwd2 = pwd2;
    }

    private HashMap<String, byte[]> encrypt2(final byte[] decryptedBytes)
    {
        final HashMap<String, byte[]> map = new HashMap<String, byte[]>();
        try
        {
            //Get the key
            final KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry)keyStore.getEntry("MyKeyAlias", null);
            final SecretKey secretKey = secretKeyEntry.getSecretKey();

            //Encrypt data
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            final byte[] ivBytes = cipher.getIV();
            final byte[] encryptedBytes = cipher.doFinal(decryptedBytes);
            map.put("iv", ivBytes);
            map.put("encrypted", encryptedBytes);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

        return map;
    }



}
