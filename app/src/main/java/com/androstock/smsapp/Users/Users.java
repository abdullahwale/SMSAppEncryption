package com.androstock.smsapp.Users;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.androstock.smsapp.MainActivity;
import com.androstock.smsapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Users extends AppCompatActivity {

    List<UserModel> datalist = new ArrayList();
    List<UserModel>nouser_datalist=new ArrayList<>();
    private UsersAdapter adapter;
    String userid;
    RecyclerView recyclerView;
    SQLiteDatabase sqLiteDatabase;
    UsersDatabaseClass SQLITEHELPER_USERS;
    private RecyclerView.LayoutManager layoutManager;
    FloatingActionButton floatingActionButton;
    String SQLiteQuery;
    TextView nousers;
  //  MenuItem item;
  public Menu option_Menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_users);
        SQLITEHELPER_USERS=new UsersDatabaseClass(this);
        nousers=(TextView)findViewById(R.id.nousers);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.fab) ;
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(Users.this,AddUsers.class);
                startActivity(intent);
            }
        });
        this.recyclerView.setNestedScrollingEnabled(false);
        this.recyclerView.setHasFixedSize(true);
        this.layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        show_added_users();
            //prepare_recycleview(userid);

    }






    public void setUsers() {
      //  runLayoutAnimation(recyclerView);



        this.adapter = new UsersAdapter(this, this.datalist, new UsersAdapter.VenueAdapterClickCallbacks() {
            @Override
            public void onCardClick(String userid) {
             //   Toast.makeText(Users.this, userid, Toast.LENGTH_SHORT).show();
                delete_user(userid);
            }


        });
        this.recyclerView.setAdapter(this.adapter);
    }

    public void delete_user(String phone)
    {
      //  phone="123456";
        Toast.makeText(this, "yesh" +phone, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, random_id, Toast.LENGTH_SHORT).show();
        OpenSQLiteDataBase_Users();
        SQLITEHELPER_USERS = new UsersDatabaseClass(this);
        SQLiteQuery = "DELETE FROM "+ SQLITEHELPER_USERS.TABLE_NAME+" WHERE user_phone= '" + phone +"' " ;
       // Toast.makeText(this, "DELETE FROM "+ SQLITEHELPER_USERS.TABLE_NAME+" WHERE user_phone = " + phone + "" , Toast.LENGTH_SHORT).show();
        sqLiteDatabase.execSQL(SQLiteQuery);
        checkif_nouserfound();

    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        option_Menu = menu;
        option_Menu.findItem(R.id.searchid).setVisible(false);
        //  int searchSrcTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
       MenuItem item = menu.findItem(R.id.searchid);
      //   doosra=menu;
        SearchView searchview = (SearchView) item.getActionView();
        EditText searchEditText = (EditText)searchview.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.colorPrimary));
        searchEditText.setHintTextColor(getResources().getColor(R.color.black));
        searchEditText.setBackgroundColor(Color.WHITE);

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true; // handled
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(), newText, Toast.LENGTH_SHORT).show();
                // mAdapter.getFilter().filter(newText);
                adapter.getFilter().filter(newText);
                return true;
            }

        });





        return super.onCreateOptionsMenu(menu);
    }


    public void setMenuVisible(boolean visible, int id) {
        if (option_Menu != null) {
            option_Menu.findItem(R.id.searchid).setVisible(true);
        }
    }
*//*
    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation);

        recyclerView.setLayoutAnimation(controller);
        //  recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    */
    public void OpenSQLiteDataBase_Users()
    {
        sqLiteDatabase = openOrCreateDatabase(UsersDatabaseClass.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }


    public void show_added_users()
    {
        datalist.clear();
        OpenSQLiteDataBase_Users();
            Cursor cursor;
        sqLiteDatabase = SQLITEHELPER_USERS.getWritableDatabase();
        // cursor = sqLiteDatabase.rawQuery("SELECT id,name,category,section,price,left,total,quantity FROM "+SQLITEHELPER.TABLE_NAME+" WHERE id=?", new String[] {id + ""});
        //cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+SQLITEHELPER.TABLE_NAME+"WHERE id = '" + strtext + "'", null);
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + SQLITEHELPER_USERS.TABLE_NAME + "", null);

        if (cursor.moveToFirst()) {
            do {


                this.datalist.add(new UserModel(cursor.getString(cursor.getColumnIndex(UsersDatabaseClass.KEY_USER_KEY)),
                        cursor.getString(cursor.getColumnIndex(UsersDatabaseClass.KEY_USER_PHONE)),
                        cursor.getString(cursor.getColumnIndex(UsersDatabaseClass.KEY_USER_NAME))
                ));




            } while (cursor.moveToNext());
        }



        if(datalist.size()==0)
        {
           // item.setVisible(false);
           // setMenuVisible(false,R.id.searchid);
            nousers.setVisibility(View.VISIBLE);
        }
        else {
        //    setMenuVisible(true,R.id.searchid);
            nousers.setVisibility(View.GONE);
            setUsers();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
       Intent intent=new Intent(Users.this, MainActivity.class);
       startActivity(intent);
        super.onBackPressed();
    }


    public void checkif_nouserfound()
    {
        Cursor newcursor;
        nouser_datalist.clear();
        OpenSQLiteDataBase_Users();
        Cursor cursor;
        sqLiteDatabase = SQLITEHELPER_USERS.getWritableDatabase();
        // cursor = sqLiteDatabase.rawQuery("SELECT id,name,category,section,price,left,total,quantity FROM "+SQLITEHELPER.TABLE_NAME+" WHERE id=?", new String[] {id + ""});
        //cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+SQLITEHELPER.TABLE_NAME+"WHERE id = '" + strtext + "'", null);
        newcursor = sqLiteDatabase.rawQuery("SELECT * FROM " + SQLITEHELPER_USERS.TABLE_NAME + "", null);

        if (newcursor.moveToFirst()) {
            do {


                this.nouser_datalist.add(new UserModel(newcursor.getString(newcursor.getColumnIndex(UsersDatabaseClass.KEY_USER_KEY)),
                        newcursor.getString(newcursor.getColumnIndex(UsersDatabaseClass.KEY_USER_PHONE)),
                        newcursor.getString(newcursor.getColumnIndex(UsersDatabaseClass.KEY_USER_NAME))
                ));




            } while (newcursor.moveToNext());
        }



        if(nouser_datalist.size()==0)
        {

            nousers.setVisibility(View.VISIBLE);
        }
        else {
         //   setMenuVisible(true,R.id.searchid);
            nousers.setVisibility(View.GONE);
        }
    }





}
