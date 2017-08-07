package com.example.android.todoappfacebook.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Admin on 1/31/17.
 */

public final class TaskContract {

    private TaskContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.todonetflix";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TASKS = "tasks";


    //Define the Version table
    public static final class TaskEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TASKS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;

        //Define table name
        public static final String TABLE_NAME = "tasks";

        //Define table columns
        public final static String _ID = BaseColumns._ID;
        public final static String TASK_NAME = "task_name";
        public final static String DUE_DATE = "due_date";
        public final static String NOTES = "notes";
        public final static String PRIORITY = "priority";
        public final static String STATUS = "status";


        public final static int PRIORITY_HIGH = 1;
        public final static int PRIORITY_MEDIUM = 2;
        public final static int PRIORITY_LOW = 3;
        public final static int PRIORITY_UNKNOWN = 0;

        public final static int STATUS_DONE = 1;
        public final static int STATUS_TODO = 2;
        public final static int STATUS_UNKNOWN = 0;

        public static boolean isValidPriority(int priority){

            if(priority == PRIORITY_HIGH || priority == PRIORITY_MEDIUM || priority == PRIORITY_LOW || priority == PRIORITY_UNKNOWN){
                return true;
            }
            return false;
        }

        public static boolean isValidStatus(int status){

            if(status == STATUS_DONE || status == STATUS_TODO|| status == STATUS_UNKNOWN){
                return true;
            }
            return false;
        }

    }

}