package com.example.android.todoappfacebook;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.todoappfacebook.data.TaskContract;

public class TaskCatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Identifier for the task data loader
    private static final int TASK_LOADER = 0;

    //Adapter for our ListView
    TaskCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_catalog);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskCatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        //Find the ListView wich will be populated with the table data
        ListView taskListView = (ListView) findViewById(R.id.list);

        //Find and set empty view on the ListView, so that it only shoys when the list has 0 items
        View emptyView = findViewById(R.id.empty_view);
        taskListView.setEmptyView(emptyView);

        //Setup an Adapter to create a list item for each row of data in the Cursor
        //this is no table data yet (until the loader finishes) so pass in null for the Cursor
        mCursorAdapter = new TaskCursorAdapter(this, null);
        taskListView.setAdapter(mCursorAdapter);

        //Setup item click listener
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Create a new intent to go to {@link EditorActivity}
                Intent intent = new Intent(TaskCatalogActivity.this, EditorActivity.class);

                Uri currentTaskUri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, id);
                Log.i("Uri", currentTaskUri.toString());

                //Set the URI on the data field of the intent
                intent.setData(currentTaskUri);

                //Launch the {@link EditorActivity to display the data for the current item
                startActivity(intent);
            }


        });

        //Kick off the loader
        getLoaderManager().initLoader(TASK_LOADER, null, this);



    }

    private void insertTask() {

        //Create a ContentValues object where column are the keys

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.TASK_NAME, "Go to the supermarket");
        values.put(TaskContract.TaskEntry.NOTES, "Butter, Milk, Cheese, Fruits");
        values.put(TaskContract.TaskEntry.DUE_DATE, "2017/02/20");
        values.put(TaskContract.TaskEntry.PRIORITY, TaskContract.TaskEntry.PRIORITY_MEDIUM);
        values.put(TaskContract.TaskEntry.STATUS, TaskContract.TaskEntry.STATUS_TODO);

        //Insert a new row into the provider using ContentResolver
        //Receive the new content URI that will allow us to access new row data in the future

        Uri newUri = getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, values);


    }

    //Helper method to delete all tasks in the database

    private void deleteAllTasks() {

        int rowsDeleted = getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI, null, null);
        Log.v("TaskCatalogActivity", rowsDeleted + " rows deleted from the database");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      //User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()){
            //Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertTask();
                return true;
            //Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                //Delete all rows
                deleteAllTasks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Define a projection that specifies columns from the table we care about
        String[] projection = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.TASK_NAME,
                TaskContract.TaskEntry.PRIORITY
        };

        //This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,               //Parent activity context
                TaskContract.TaskEntry.CONTENT_URI,  //Provider content URI to query
                projection,                         //Columns to include in the resulting Cursor
                null,                               //No selection clause
                null,                               //No selection arguments
                null                                //Default sort order

        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Update {@link TaskCursorAdapter with this new cursor containing updated data
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);

    }
}
