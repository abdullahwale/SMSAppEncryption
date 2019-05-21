package com.androstock.smsapp.Users;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.androstock.smsapp.R;

import java.util.ArrayList;

public class AddUsers extends AppCompatActivity {

    ArrayList<String> functions=new ArrayList<>();
    ArrayList<String> functions_show=new ArrayList<>();
    ArrayList<String> selectedItems=new ArrayList<>();
    StringBuilder function_builder;
    EditText phone_edittext,key_edittext,name_edittext;
    String phone_string,key_string,name_string;
    Button add_user;
    SQLiteDatabase sqLiteDatabase;
    UsersDatabaseClass SQLITEHELPER_USERS;
    String SQLiteQuery;
    private ImageButton contact;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);
        contact=(ImageButton)findViewById(R.id.contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, 85);
            }
        });



        SQLITEHELPER_USERS=new UsersDatabaseClass(this);
        key_edittext=(EditText)findViewById(R.id.key);
        name_edittext=(EditText)findViewById(R.id.name_edittext);

        phone_edittext=(EditText)findViewById(R.id.phone);
        phone_edittext.setText("");
        //key_edittext.setText("");
        name_edittext.setText("");
        key_edittext.setText("");
        add_user=(Button)findViewById(R.id.btn_next);

        add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone_string= phone_edittext.getText().toString();
                key_string= key_edittext.getText().toString();
                name_string=name_edittext.getText().toString();
                if(phone_string.equals("") &&key_string.equals("") &&name_string.equals(""))
                {
                    phone_edittext.setError("Enter Phone");
                    name_edittext.setError("Enter Name");
                    key_edittext.setError("Enter Key");
                }
              else if(phone_string.equals("") )
                {
                    phone_edittext.setError("Enter Phone");
                }


                else if(name_edittext.equals("") )
                {
                    name_edittext.setError("Enter Name");
                }
                else if(key_string.equals(""))
                {
                    key_edittext.setError("Select Key");
                }
                else{
                    if(checkifexists(phone_string))
                    {
                        Toast.makeText(AddUsers.this, "User already exists with the same number", Toast.LENGTH_SHORT).show();

                    }
                    else {

                        add_user_todatabase(name_string, phone_string, key_string);
                    }
                }
            }
        });

    }



    public void add_user_todatabase(final String user_name, final String userphone,final String user_key)
    {

        OpenSQLiteDataBase_Users();
        SQLITEHELPER_USERS=new UsersDatabaseClass(this);
        Long tsLong = System.currentTimeMillis()/1000;
        String id = tsLong.toString();

        SQLiteQuery = "INSERT INTO users (user_key,user_name,user_phone)VALUES('"
                + user_key+ "','" + user_name  + "','" + userphone + "')";


        sqLiteDatabase.execSQL(SQLiteQuery);
        sqLiteDatabase.close();
        addsuccess();


    }


    public boolean checkifexists(String phoneNo) {
        Cursor cursor;
        OpenSQLiteDataBase_Users();
        SQLITEHELPER_USERS = new UsersDatabaseClass(this);
        sqLiteDatabase = SQLITEHELPER_USERS.getWritableDatabase();


        cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + SQLITEHELPER_USERS.TABLE_NAME + " WHERE " +
                "user_phone = '" + phoneNo + "'" , null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }






    public void OpenSQLiteDataBase_Users()
    {
        sqLiteDatabase = openOrCreateDatabase(UsersDatabaseClass.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }
    public void addsuccess()
    {
        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(new Intent(AddUsers.this,Users.class));
        startActivity(intent);
      /*  AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddUsers.this);

        // ...Irrelevant code for customizing the buttons and title


        LayoutInflater inflater = this.getLayoutInflater();

        View dialogView= inflater.inflate(R.layout.newuseradded, null);


        dialogBuilder.setView(dialogView);




        Button no = (Button) dialogView.findViewById(R.id.no);
        TextView title= (TextView) dialogView.findViewById(R.id.title);
        TextView desc = (TextView) dialogView.findViewById(R.id.desc);
        title.setText("User Added");
        desc.setText("You have added a new user");



        final AlertDialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent=new Intent(new Intent(AddUsers.this,Users.class));
                startActivity(intent);

            }
        });

        dialog.show();
    */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
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
            phone_edittext.setText(phoneNo);
            name_edittext.setText(name);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }





}
