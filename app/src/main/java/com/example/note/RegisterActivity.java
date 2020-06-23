package com.example.note;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.HashMap;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class RegisterActivity extends AppCompatActivity {
    DatabaseHelper2 db;
    EditText mTextPassword;
    EditText mTextCnfPassword;
    Button mButtonRegister;
    TextView mTextViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        db = new DatabaseHelper2(this);
        mTextPassword = (EditText)findViewById(R.id.edittext_password);
        mTextCnfPassword = (EditText)findViewById(R.id.edittext_cnf_password);
        mButtonRegister = (Button)findViewById(R.id.button_register);
       mTextViewLogin = (TextView)findViewById(R.id.textview_login);
        mTextViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(LoginIntent);
            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                String pwd = mTextPassword.getText().toString().trim();
                String cnf_pwd = mTextCnfPassword.getText().toString().trim();

                if(pwd.equals(cnf_pwd)){
                    try {
                        testEncryption();

                        final HashMap<String, byte[]> map = encrypt2(pwd.getBytes("UTF-8"));
                        final byte[] encryptedBytes_B = map.get("encrypted");
                        final byte[] ivBytes_B = map.get("iv");
                        if( db.addUser(encryptedBytes_B,ivBytes_B)!=0){
                            Toast.makeText(RegisterActivity.this, "Możesz tylko raz sie zarejestrować", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(RegisterActivity.this, "Zarejestrowano użytkowanika", Toast.LENGTH_SHORT).show();
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(RegisterActivity.this,"Password is not matching",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void testEncryption()
    {
        try
        {
            final KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder("MyKeyAlias",
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build();
            keyGenerator.init(keyGenParameterSpec);
            keyGenerator.generateKey();

        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    private HashMap<String, byte[]> encrypt2(final byte[] decryptedBytes)
    {
        final HashMap<String, byte[]> map = new HashMap<String, byte[]>();
        try
        {
            final KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry)keyStore.getEntry("MyKeyAlias", null);
            final SecretKey secretKey = secretKeyEntry.getSecretKey();

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