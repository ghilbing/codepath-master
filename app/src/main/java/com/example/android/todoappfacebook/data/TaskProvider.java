package com.example.android.todoappfacebook.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Admin on 2/7/17.
 */

public class TaskProvider extends ContentProvider {

    //Tag for log messages
    public static final String LOG_TAG = TaskProvider.class.getSimpleName();

    //URI matcher code for the content URI for the table
    private static final int TASKS = 100;
    //URI matcher code for the content URI for a single row in the table
    private static final int TASK_ID = 101;

    //UriMatcher object to match a content URI to a corresponding code
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(TaskContract.CONTENT_AUTHORITY, TaskContract.PATH_TASKS, TASKS);
        sUriMatcher.addURI(TaskContract.CONTENT_AUTHORITY, TaskContract.PATH_TASKS + "/#", TASK_ID);

    }

    //Database helper object
    private TaskDbHelper mDbHelper;

    @Override
    public boolean onCreate() {

        mDbHelper = new TaskDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        //Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //This cursor will hold the result of the query
        Cursor cursor;

        //Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);

        switch (match){
            case TASKS:
                //For the TASKS code, query the tasks table directly with the given projection, selection, selection arguments
                //and sort order. The cursor could contain multiple rows of the task table

                cursor = database.query(TaskContract.TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

                break;
            case TASK_ID:
                //For the TASK_ID code, extract out the ID from the URI
                selection = TaskContract.TaskEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                //This will perform a query on the task table to return a Cursor containing a determined row of the table
                cursor = database.query(TaskContract.TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }

        //Set notification URI on the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case TASKS:
                return insertTask(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    //Insert a task into the database with the given content values. Return the new content URI for that specific row in the database.
    private Uri insertTask(Uri uri, ContentValues values){

        //Check that the name is not null
        String name = values.getAsString(TaskContract.TaskEntry.TASK_NAME);
        if(name == null) {
            throw new IllegalArgumentException("Task requires a name");

        }

        //Check that the date is not null
        String date = values.getAsString(TaskContract.TaskEntry.DUE_DATE);
        if(date == null){
            throw new IllegalArgumentException("Task requires a date");

        }

        //Check that the priority is valid
        Integer priority = values.getAsInteger(TaskContract.TaskEntry.PRIORITY);
        if(priority == null || !TaskContract.TaskEntry.isValidPriority(priority)){
            throw new IllegalArgumentException("Task requires a valid priority");

        }

        //Check that the status is valid
        Integer status = values.getAsInteger(TaskContract.TaskEntry.STATUS);
        if(status == null || !TaskContract.TaskEntry.isValidStatus(status)){
            throw new IllegalArgumentException("Task requires a valid status");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Insert the new item with the given values
        long id = database.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
        //If the ID is -1, then the insertion failed. Log an error and return null
        if(id == -1){

            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify all listeners that the data has changed for the task content URI
        getContext().getContentResolver().notifyChange(uri, null);

        //Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);


    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match){
            case TASKS:
                //Delete all rows
                rowsDeleted = database.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TASK_ID:
                //Delete a single row given by the ID in the URI
                selection = TaskContract.TaskEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        //If 1 or more rows were deleted, then notify all listeners that the data at the given URI has changed
        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case TASKS:
                return updateTask(uri, contentValues, selection, selectionArgs);
            case TASK_ID:
                selection = TaskContract.TaskEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateTask(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updateTask(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){

        if(contentValues.containsKey(TaskContract.TaskEntry.TASK_NAME)){
            String name = contentValues.getAsString(TaskContract.TaskEntry.TASK_NAME);
            if (name == null){
                throw new IllegalArgumentException("Task requires a name");
            }
        }

        if(contentValues.containsKey(TaskContract.TaskEntry.DUE_DATE)){
            String date = contentValues.getAsString(TaskContract.TaskEntry.DUE_DATE);
            if (date == null){
                throw new IllegalArgumentException("Task requires a date");
            }
        }

        if(contentValues.containsKey(TaskContract.TaskEntry.PRIORITY)){
            Integer priority = contentValues.getAsInteger(TaskContract.TaskEntry.PRIORITY);
            if(priority == null || !TaskContract.TaskEntry.isValidPriority(priority)){
                throw new IllegalArgumentException("Task requires a priority");
            }
        }

        if(contentValues.containsKey(TaskContract.TaskEntry.STATUS)){
            Integer status = contentValues.getAsInteger(TaskContract.TaskEntry.STATUS);
            if(status == null || !TaskContract.TaskEntry.isValidPriority(status)){
                throw new IllegalArgumentException("Task requires a status");
            }
        }

        //If there are no values to update, then do not try to update the database
        if (contentValues.size() == 0){
            return 0;
        }
        //Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(TaskContract.TaskEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match){
            case TASKS:
                return TaskContract.TaskEntry.CONTENT_LIST_TYPE;
            case TASK_ID:
                return TaskContract.TaskEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);

        }

    }

}

