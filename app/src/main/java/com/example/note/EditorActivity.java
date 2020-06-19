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
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EditorActivity extends AppCompatActivity {

    private String action;
    private EditText editor;
    private String noteFilter;
    private String oldText;
    String password="1234";

    ////
//    EditText inputPassword;
    TextView outputText;
//    Button encBtn,decBtn;
//    String outputString;
    String AES="AES";

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
            try {
                oldText=decrypt(oldText,"1234");
                Toast.makeText(EditorActivity.this, "Paraw", Toast.LENGTH_SHORT).show();


            } catch (Exception e) {
                Toast.makeText(EditorActivity.this, "wrong password", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            editor.setText(oldText);
            editor.requestFocus();
        }


        ////

//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        content_editor =(EditText) findViewById(R.id.editText2);


        //TU ODKOMENTUJ
//        inputPassword=(EditText) findViewById(R.id.password);
//
//        outputText=(TextView) findViewById(R.id.outputText);
//        encBtn=(Button) findViewById(R.id.encBtn);
//        decBtn=(Button) findViewById(R.id.decBtn);
//
//        encBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    outputString=encrypt(outputString,password);
//
//                    outputText.setText(outputString);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        decBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    outputString=decrypt(outputString,password);
//                    Toast.makeText(EditorActivity.this, "Prawidłowe", Toast.LENGTH_SHORT).show();
//
//
//                } catch (Exception e) {
//                    Toast.makeText(EditorActivity.this, "wrong password", Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
//                outputText.setText(outputString);
//            }
//        });
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
            noteText=encrypt(noteText,password);
            outputText.setText(noteText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, R.string.note_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);

    }

    private void insertNote(String noteText) {
                try {
                    noteText=encrypt(noteText,password);
                    outputText.setText(noteText);

                } catch (Exception e) {
                    e.printStackTrace();
                }

        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }
    @Override
    public void onBackPressed() {
        finishEditing();
    }

    /////
    private String decrypt(String outputString, String password) throws Exception{
        SecretKeySpec key =generateKey(password);
        Cipher c =Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] decodedValue= Base64.decode(outputString,Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        String decrypedValue= new String(decValue);
        return decrypedValue;

    }
    private String encrypt(String Data, String password) throws Exception{
        SecretKey key =generateKey(password);
        Cipher c =Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encVal=c.doFinal(Data.getBytes());
        String encrypedValue= Base64.encodeToString(encVal, Base64.DEFAULT);
        return encrypedValue;

    }
    private SecretKeySpec generateKey(String password) throws Exception{
        final MessageDigest digest =MessageDigest.getInstance("SHA-256");
        byte[] bytes= password.getBytes("UTF-8");
        digest.update(bytes,0,bytes.length);
        byte[] key=digest.digest();
        SecretKeySpec secretKeySpec=new SecretKeySpec(key,"AES");
        return secretKeySpec;
    }
}
