package com.androstock.smsapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;



public class NewSmsActivity extends AppCompatActivity{

    EditText address, message;
    Button send_btn;
    private ImageButton contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_new);

        address = (EditText) findViewById(R.id.address);
        message = (EditText) findViewById(R.id.message);
        send_btn = (Button) findViewById(R.id.send_btn);
        contact=(ImageButton)findViewById(R.id.contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, 85);
            }
        });
     //   sendBtn.setOnClickListener(this);


        send_btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String str_addtes = address.getText().toString();
                String str_message = message.getText().toString();
                Toast.makeText(NewSmsActivity.this, str_addtes, Toast.LENGTH_SHORT).show();


                if (str_addtes.length() > 0 && str_message.length() > 0) {


                    String encrypted = "";
                    String sourceStr = "This is any source string";
                    try {
                        SharedPreferences sharedPreferences = getSharedPreferences("keyinfo", Context.MODE_PRIVATE);
                        String key_Saved = sharedPreferences.getString("mykey","");
                        encrypted = newclass.encrypt(str_message,key_Saved);
                        Log.d("TEST", "encrypted:" + encrypted);
                        // encryptedText.setText(encrypted);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                    if(Function.sendSMS(str_addtes, encrypted))
                    {

                        ContentValues values = new ContentValues(); values.put("address", str_addtes)
                    ;
                      //  values.put("thread_id",thread_id_main);
                        values.put("body", str_message); getContentResolver().
                            insert(Uri.parse("content://sms/sent"), values);


                        Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

            }
        });
    }

    public void pickContact(View v)
    {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, 85);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
// check whether the result is ok
        if (resultCode == RESULT_OK) {
// Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case 85:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }

    private void contactPicked(Intent data) {

        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
// getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
//Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
// column index of the phone number
            int phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
// column index of the contact name
            int nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            address.setText(phoneNo);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
