package com.example.note;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import java.security.KeyStore;
import java.security.MessageDigest;
import java.sql.Blob;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EditorActivity extends AppCompatActivity {

    private String action;
    private EditText editor;
    private String noteFilter;
    private String oldText;
    TextView outputText;

    ///
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editor = findViewById(R.id.editText2);

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        if(uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));


        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            String decryptedString = null;
            byte[] ivBytes_B = cursor.getBlob(cursor.getColumnIndex(DBOpenHelper.ivBytes));
            byte[] encryptedBytes_B = cursor.getBlob(cursor.getColumnIndex(DBOpenHelper.encryptedBytes));
            final HashMap<String, byte[]> map = new HashMap<String, byte[]>();

            map.put("iv", ivBytes_B);
            map.put("encrypted", encryptedBytes_B);
            try {
//                oldText=decrypt(oldText,"1234");
                Toast.makeText(EditorActivity.this, "Paraw", Toast.LENGTH_SHORT).show();
                final byte[] decryptedBytes = decrypt2(map);
                decryptedString = new String(decryptedBytes, "UTF-8");

            } catch (Exception e) {
                Toast.makeText(EditorActivity.this, "wrong password", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            editor.setText(decryptedString);
            editor.requestFocus();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteNote();
                break;
        }
        return true;
    }

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI,noteFilter, null);
        Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void finishEditing() {
        String newText = editor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if(newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(newText);
                }
                break;
            case Intent.ACTION_EDIT:
                if(newText.length() == 0) {
                    deleteNote();
                } else if(oldText.equals(newText)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newText);
                }
        }
        finish();
    }

    private void updateNote(String noteText) {
        try {
//            String noteText2=encrypt(noteText,password);
            final HashMap<String, byte[]> map = encrypt2(noteText.getBytes("UTF-8"));
            final byte[] encryptedBytes_B = map.get("encrypted");
            final byte[] ivBytes_B = map.get("iv");
            ContentValues values = new ContentValues();
            values.put(DBOpenHelper.NOTE_TEXT, "niby tekst");
            values.put(DBOpenHelper.ivBytes, ivBytes_B);
            values.put(DBOpenHelper.encryptedBytes, encryptedBytes_B);
            Toast.makeText(EditorActivity.this, " password", Toast.LENGTH_SHORT).show();
            getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
            Toast.makeText(this, R.string.note_updated, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertNote(String noteText) {
                try {
//                    String noteText2=encrypt(noteText,password);
////                    outputText.setText(noteText);
                    final HashMap<String, byte[]> map = encrypt2(noteText.getBytes("UTF-8"));
                    final byte[] encryptedBytes_B = map.get("encrypted");
                    final byte[] ivBytes_B = map.get("iv");
                    ContentValues values = new ContentValues();
                    values.put(DBOpenHelper.NOTE_TEXT, "niby tekst");
                    values.put(DBOpenHelper.ivBytes, ivBytes_B);
                    values.put(DBOpenHelper.encryptedBytes, encryptedBytes_B);
                    Toast.makeText(EditorActivity.this, " password", Toast.LENGTH_SHORT).show();

                    getContentResolver().insert(NotesProvider.CONTENT_URI, values);
                    setResult(RESULT_OK);
                } catch (Exception e) {
                    e.printStackTrace();

                }



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


    private byte[] decrypt2(final HashMap<String, byte[]> map)
    {
        byte[] decryptedBytes = null;
        try
        {
            //Get the key
            final KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry)keyStore.getEntry("MyKeyAlias", null);
            final SecretKey secretKey = secretKeyEntry.getSecretKey();

            //Extract info from map
            final byte[] encryptedBytes = map.get("encrypted");
            final byte[] ivBytes = map.get("iv");

            //Decrypt data
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            final GCMParameterSpec spec = new GCMParameterSpec(128, ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            decryptedBytes = cipher.doFinal(encryptedBytes);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

        return decryptedBytes;
    }
}
