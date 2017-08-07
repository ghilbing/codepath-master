package com.example.android.todoappfacebook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Admin on 2/7/17.
 */

public class TaskDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = TaskDbHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "tasktodo.db";

    public static final int DATABASE_VERSION = 1;


    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Create a string that contains the SQL statement to create the task table
        String SQL_CREATE_TASK_TABLE = "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " ("
                + TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TaskContract.TaskEntry.TASK_NAME + " TEXT NOT NULL, "
                + TaskContract.TaskEntry.DUE_DATE + " TEXT NOT NULL, "
                + TaskContract.TaskEntry.NOTES + " TEXT, "
                + TaskContract.TaskEntry.PRIORITY + " INTEGER NOT NULL, "
                + TaskContract.TaskEntry.STATUS + " INTEGER NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_TASK_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

