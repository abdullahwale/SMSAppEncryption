package com.androstock.smsapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androstock.smsapp.Users.UsersDatabaseClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class Chat extends AppCompatActivity {

    ListView listView;
    ChatAdapter adapter;
    LoadSms loadsmsTask;
    String name;
    String address;
    EditText new_message;
    ImageButton send_message;
    int thread_id_main;
    private Handler handler = new Handler();
    Thread t;
    ArrayList<HashMap<String, String>> smsList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> customList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpList = new ArrayList<HashMap<String, String>>();
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        address = intent.getStringExtra("address");
        setTitle(address);
        thread_id_main = Integer.parseInt(intent.getStringExtra("thread_id"));

        listView = (ListView) findViewById(R.id.listView);
        new_message = (EditText) findViewById(R.id.new_message);
        send_message = (ImageButton) findViewById(R.id.send_message);

        startLoadingSms();


        send_message.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String text = new_message.getText().toString();


                if(text.length()>0) {
                    String tmp_msg = text;
                    new_message.setText("Sending....");
                    new_message.setEnabled(false);
                   // Toast.makeText(Chat.this, address, Toast.LENGTH_SHORT).show();


                    String encrypted = "";
                    String sourceStr = "This is any source string";
                    try {
                        SharedPreferences sharedPreferences = getSharedPreferences("keyinfo", Context.MODE_PRIVATE);
                        String key_Saved = sharedPreferences.getString("mykey","");


                        encrypted = newclass.encrypt(text,key_Saved);
                        Log.d("TEST", "encrypted:" + encrypted);
                       // encryptedText.setText(encrypted);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if(Function.sendSMS(address, encrypted))
                    {
                            ContentValues values = new ContentValues(); values.put("address", address)
                    ;
                            values.put("thread_id",thread_id_main);
                        values.put("body", tmp_msg); getContentResolver().
                                insert(Uri.parse("content://sms/sent"), values);
                        new_message.setText("");
                        new_message.setEnabled(true);
                        // Creating a custom list for newly added sms
                        customList.clear();
                        customList.addAll(smsList);
                        customList.add(Function.mappingInbox(null, null, null, null, tmp_msg, "2", null, "Sending..."));
                        adapter = new ChatAdapter(Chat.this, customList);
                        listView.setAdapter(adapter);
                        //=========================
                    }else{
                        new_message.setText(tmp_msg);
                        new_message.setEnabled(true);
                    }


                }
            }
        });


    }




    class LoadSms extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tmpList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            try {
                Uri uriInbox = Uri.parse("content://sms/inbox");
                Cursor inbox = getContentResolver().query(uriInbox, null, "thread_id=" + thread_id_main, null, null);
                Uri uriSent = Uri.parse("content://sms/sent");
                Cursor sent = getContentResolver().query(uriSent, null, "thread_id=" + thread_id_main, null, null);
                Cursor c = new MergeCursor(new Cursor[]{inbox,sent}); // Attaching inbox and sent sms



                if (c.moveToFirst()) {
                    for (int i = 0; i < c.getCount(); i++) {
                        String phone = "";
                        String _id = c.getString(c.getColumnIndexOrThrow("_id"));
                        String thread_id = c.getString(c.getColumnIndexOrThrow("thread_id"));
                        String msg = c.getString(c.getColumnIndexOrThrow("body"));
                        String type = c.getString(c.getColumnIndexOrThrow("type"));
                        String timestamp = c.getString(c.getColumnIndexOrThrow("date"));
                        phone = c.getString(c.getColumnIndexOrThrow("address"));

                        tmpList.add(Function.mappingInbox(_id, thread_id, name, phone, msg, type, timestamp, Function.converToTime(timestamp)));
                        c.moveToNext();
                    }
                }
                c.close();

            }catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Collections.sort(tmpList, new MapComparator(Function.KEY_TIMESTAMP, "asc"));

            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            if(!tmpList.equals(smsList))
            {
                smsList.clear();
                smsList.addAll(tmpList);
                adapter = new ChatAdapter(Chat.this, smsList);
                listView.setAdapter(adapter);

            }





        }
    }




    public void startLoadingSms()
    {
        final Runnable r = new Runnable() {
            public void run() {

                loadsmsTask = new LoadSms();
                loadsmsTask.execute();

                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(r, 0);
    }
}







class ChatAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap< String, String >> data;
    SQLiteDatabase sqLiteDatabase;
    UsersDatabaseClass SQLITEHELPER_USERS;
    public ChatAdapter(Activity a, ArrayList < HashMap < String, String >> d) {
        activity = a;
        data = d;
        SQLITEHELPER_USERS=new UsersDatabaseClass(activity);
    }
    public int getCount() {
        return data.size();
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatViewHolder holder = null;
        if (convertView == null) {
            holder = new ChatViewHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.chat_item, parent, false);


            holder.txtMsgYou = (TextView)convertView.findViewById(R.id.txtMsgYou);
            holder.lblMsgYou = (TextView)convertView.findViewById(R.id.lblMsgYou);
            holder.timeMsgYou = (TextView)convertView.findViewById(R.id.timeMsgYou);
            holder.lblMsgFrom = (TextView)convertView.findViewById(R.id.lblMsgFrom);
            holder.timeMsgFrom = (TextView)convertView.findViewById(R.id.timeMsgFrom);
            holder.txtMsgFrom = (TextView)convertView.findViewById(R.id.txtMsgFrom);
            holder.msgFrom = (LinearLayout)convertView.findViewById(R.id.msgFrom);
            holder.msgYou = (LinearLayout)convertView.findViewById(R.id.msgYou);

            convertView.setTag(holder);
        } else {
            holder = (ChatViewHolder) convertView.getTag();
        }
        holder.txtMsgYou.setId(position);
        holder.lblMsgYou.setId(position);
        holder.timeMsgYou.setId(position);
        holder.lblMsgFrom.setId(position);
        holder.timeMsgFrom.setId(position);
        holder.txtMsgFrom.setId(position);
        holder.msgFrom.setId(position);
        holder.msgYou.setId(position);

        HashMap < String, String > song = new HashMap < String, String > ();
        song = data.get(position);
        try {

            if(song.get(Function.KEY_TYPE).contentEquals("1"))
            {
                String value="";
                final String msgtxt=song.get(Function.KEY_MSG);

               String newNumber = song.get(Function.KEY_PHONE).replace("+92","0");
               Boolean check=checkifexists(newNumber);


               if(check)
               {
                   String mykey=findkey(newNumber);
                   String decrypted = "";
                   try {
                       decrypted = newclass.decrypt(song.get(Function.KEY_MSG),mykey);
                       Log.d("TEST", "decrypted:" + decrypted);
                       //this.decryptedText.setText(decrypted);
                       value=decrypted;
                   } catch (Exception e) {
                       e.printStackTrace();
                   }



               }
               else{
value=song.get(Function.KEY_MSG);
holder.msgFrom.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
       // Toast.makeText(activity,msgtxt , Toast.LENGTH_SHORT).show();

        LayoutInflater layoutInflater=activity.getLayoutInflater();
        final View view1 = layoutInflater.inflate(R.layout.dialog, null);
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Decrypt message");

        alertDialog.setCancelable(false);

        final EditText edt_groupName = (EditText) view1.findViewById(R.id.edt_groupName);
        final  TextView edt_message=(TextView)view1.findViewById(R.id.message);
        final String[] _decrypted = new String[1];
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str_getTextFrom=edt_groupName.getText().toString();
                try {
                     _decrypted[0] = newclass.decrypt(msgtxt,str_getTextFrom);

                    //edt_message.setText(_decrypted);
                  //  edt_message.setVisibility(View.VISIBLE);

                    ///if(edt_message.getText().toString().equals(""))
                    //{ edt_message.setText("Invalid key");

                   // }
                   // Log.d("TEST", "decrypted:" + decrypted);
                    //this.decryptedText.setText(decrypted);
                   // value=decrypted;
                } catch (Exception e) {
                    _decrypted[0]="";
                    e.printStackTrace();
                }



















                //here we have to call Database firebase

                dialog.dismiss();
                showsecond(_decrypted[0]);
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setView(view1);
        alertDialog.show();


    }
});

               }


                holder.lblMsgFrom.setText(newNumber);
                holder.txtMsgFrom.setText(value);
                holder.timeMsgFrom.setText(song.get(Function.KEY_TIME));
                holder.msgFrom.setVisibility(View.VISIBLE);
                holder.msgYou.setVisibility(View.GONE);
            }else{
                holder.lblMsgYou.setText("You");
                holder.txtMsgYou.setText(song.get(Function.KEY_MSG));
                holder.timeMsgYou.setText(song.get(Function.KEY_TIME));
                holder.msgFrom.setVisibility(View.GONE);
                holder.msgYou.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {}
        return convertView;
    }


    public boolean checkifexists(String phoneNo) {
        Cursor cursor;
        OpenSQLiteDataBase_Users();
        SQLITEHELPER_USERS = new UsersDatabaseClass(activity);
        sqLiteDatabase = SQLITEHELPER_USERS.getWritableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + SQLITEHELPER_USERS.TABLE_NAME + " WHERE " +
                "user_phone = '" + phoneNo + "'" , null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public void OpenSQLiteDataBase_Users()
    {
        sqLiteDatabase = activity.openOrCreateDatabase(UsersDatabaseClass.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }

    public String  findkey(String number)
    {
        String alldata="";
        ArrayList<String> addonslist=new ArrayList<>();

        //overall_price=0;
        OpenSQLiteDataBase_Users();
        Cursor cursor1;
        // Toast.makeText(getActivity(),strtext, Toast.LENGTH_SHORT).show();
        sqLiteDatabase = SQLITEHELPER_USERS.getWritableDatabase();
        cursor1 = sqLiteDatabase.rawQuery("SELECT user_key FROM " + SQLITEHELPER_USERS.TABLE_NAME + " WHERE user_phone = '" + number + "' ", null);
        //  cursor = sqLiteDatabase.rawQuery("SELECT id,name,category,section,price,left,total,quantity FROM" +SQLITEHELPER.TABLE_NAME+"WHERE id"=+id+"");

        if (cursor1.moveToFirst()) {
            do {
                addonslist.add(cursor1.getString(cursor1.getColumnIndex(UsersDatabaseClass.KEY_USER_KEY)));


            } while (cursor1.moveToNext());
        }
        /// Toast.makeText(this, Overall_price_ArrayList.t, Toast.LENGTH_SHORT).show();

        StringBuilder builder=new StringBuilder();
      //  try {
            for (int i = 0; i < addonslist.size(); i++) {
                if (i == addonslist.size() - 1) {
                    builder.append(addonslist.get(i) + "");
                }

                else {
                    builder.append(addonslist.get(i) + ",");
                }
            }
            alldata=builder.toString();


        // Toast.makeText(getActivity(), total_count_value, Toast.LENGTH_SHORT).show();
        cursor1.close();


        return alldata;

    }



public void showsecond(String message) {
    LayoutInflater layoutInflater = activity.getLayoutInflater();
    final View view1 = layoutInflater.inflate(R.layout.seconddial, null);
    AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
    alertDialog.setTitle("Decrypted Message");

    alertDialog.setCancelable(false);

    final TextView edt_message = (TextView) view1.findViewById(R.id.message);
    if(message.equals(""))
    {
        edt_message.setText("Invalid Key can't decode messages");
    }
    else {
        edt_message.setText(message);
    }
    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    });
    alertDialog.setView(view1);
    alertDialog.show();







}




}


class ChatViewHolder {
    LinearLayout msgFrom, msgYou;
    TextView txtMsgYou, lblMsgYou, timeMsgYou, lblMsgFrom, txtMsgFrom, timeMsgFrom;
}

