package com.androstock.smsapp.Users;
        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

public class UsersDatabaseClass extends SQLiteOpenHelper {

    public static String DATABASE_NAME="db_boom";

    public static final String TABLE_NAME="users";
    public static final String KEY_USER_KEY="user_key";
    public static final String KEY_USER_PHONE="user_phone";
    public static final String KEY_USER_NAME="user_name";

    public UsersDatabaseClass(Context context) {

        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+KEY_USER_NAME+" VARCHAR, "+KEY_USER_PHONE+" VARCHAR, "+KEY_USER_KEY+" VARCHAR)";
        database.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }






}