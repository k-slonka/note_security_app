package com.example.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.widget.Toast;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class DatabaseHelper2 extends SQLiteOpenHelper{
        public static final String DATABASE_NAME ="register.db";
        public static final String TABLE_NAME ="registeruser";
        public static final String COL_1 ="ID";
        public static final String COL_2 ="password_encrypted";
    public static final String COL_3 ="password_iv";

    public DatabaseHelper2(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE registeruser (ID INTEGER PRIMARY  KEY AUTOINCREMENT, password_encrypted BLOB, password_iv BLOB)");

//            String code=generate(new char[]{'r', 's', 't', 'u', 'v'},6);
//            SQLiteDatabase db = this.getWritableDatabase();
//            ContentValues contentValues = new ContentValues();
//
//            final HashMap<String, byte[]> map;
//            try {
//                map = encrypt2(code.getBytes("UTF-8"));
//                final byte[] encryptedBytes_B = map.get("encrypted");
//                final byte[] ivBytes_B = map.get("iv");
//
//                contentValues.put("password_encrypted",encryptedBytes_B);
//                contentValues.put("password_iv",ivBytes_B);
//                long res = db.insert("registeruser",null,contentValues);
//                db.close();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//



    }
//
//
//    public String onCreate2(SQLiteDatabase sqLiteDatabase) {
//        sqLiteDatabase.execSQL("CREATE TABLE registeruser (ID INTEGER PRIMARY  KEY AUTOINCREMENT, password_encrypted BLOB, password_iv BLOB)");
//
//        String code=generate(new char[]{'r', 's', 't', 'u', 'v'},6);
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//
//        final HashMap<String, byte[]> map;
//        try {
//            map = encrypt2(code.getBytes("UTF-8"));
//            final byte[] encryptedBytes_B = map.get("encrypted");
//            final byte[] ivBytes_B = map.get("iv");
//
//            contentValues.put("password_encrypted",encryptedBytes_B);
//            contentValues.put("password_iv",ivBytes_B);
//            long res = db.insert("registeruser",null,contentValues);
//            db.close();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    return code;
//
//    }



    public String generate(char[] validchars, int len) {
        char[] password = new char[len];
        Random rand = new Random(System.nanoTime());
        for (int i = 0; i < len; i++) {
            password[i] = validchars[rand.nextInt(validchars.length)];
        }
        return new String(password);
    }
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

        public int addUser(byte[] password_encrypted, byte[]  password_iv){
//            long res= "";
        SQLiteDatabase db = this.getWritableDatabase();


//            SQLiteDatabase db = table.getWritableDatabase();
            String count = "SELECT count(*) FROM registeruser";
            Cursor mcursor = db.rawQuery(count, null);
            mcursor.moveToFirst();
            int icount = mcursor.getInt(0);
            int icount2;

            if(icount==0){

                System.out.println("nie ma w bazie");
                ContentValues contentValues = new ContentValues();
                contentValues.put("password_encrypted",password_encrypted);
                contentValues.put("password_iv",password_iv);
                db.insert("registeruser",null,contentValues);

                db.close();
                icount2=0;

            }else {
                System.out.println("jest w bazie");
                icount2=1;

            }

   
        return  icount2;
    }







    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void changePassword(String decryptedString) throws UnsupportedEncodingException {
        final byte[] decryptedBytes = decryptedString.getBytes(StandardCharsets.UTF_8);





        final HashMap<String, byte[]> map = encrypt2(decryptedBytes);
        final byte[] encryptedBytes_B = map.get("encrypted");
        final byte[] ivBytes_B = map.get("iv");



        ContentValues cv = new ContentValues();
        cv.put("password_encrypted",encryptedBytes_B); //These Fields should be your String values of actual column names
        cv.put("password_iv",ivBytes_B);
        int id=1;
        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res = db.update( "UPDATE registeruser SET password_encrypted="+ encryptedBytes_B  +" ,password_iv="+ ivBytes_B  +" WHERE ID=1", null );

        db.update( TABLE_NAME,cv, "ID="+id, null);


//        byte[] password_encrypted_database=res.getBlob(res.getColumnIndex("password_encrypted"));
//        byte[] password_iv_database=res.getBlob(res.getColumnIndex("password_iv"));
//        final HashMap<String, byte[]> map = new HashMap<String, byte[]>();
//        map.put("iv", password_iv_database);
//        map.put("encrypted", password_encrypted_database);
//        byte[] decryptedBytes2 = decrypt2(map);
//        String decryptedString2 = new String(decryptedBytes2, StandardCharsets.UTF_8);
//
//
//        if(decryptedString.equals(decryptedString2)){
//            System.out.println("jestem w true");
//
//            return  true;}
//        else{
//            return  false;}
    }









        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public boolean checkUser(String decryptedString) throws UnsupportedEncodingException {
            final byte[] decryptedBytes = decryptedString.getBytes(StandardCharsets.UTF_8);


            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery( "SELECT password_encrypted,password_iv FROM registeruser WHERE ID=1", null );
            res.moveToFirst();

            byte[] password_encrypted_database=res.getBlob(res.getColumnIndex("password_encrypted"));
            byte[] password_iv_database=res.getBlob(res.getColumnIndex("password_iv"));
            final HashMap<String, byte[]> map = new HashMap<String, byte[]>();
            map.put("iv", password_iv_database);
            map.put("encrypted", password_encrypted_database);
            byte[] decryptedBytes2 = decrypt2(map);
            String decryptedString2 = new String(decryptedBytes2, StandardCharsets.UTF_8);


        if(decryptedString.equals(decryptedString2)){
            System.out.println("jestem w true");

            return  true;}
        else{
            return  false;}
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



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

