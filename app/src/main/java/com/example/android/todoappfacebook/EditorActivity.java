package com.example.android.todoappfacebook;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.todoappfacebook.data.TaskContract;

import java.util.Calendar;

/**
 * Created by Admin on 2/7/17.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    //Identifier for the task data loader
    private static final int EXISTING_TASK_LOADER = 0;

    private static final int DATE_DIALOG = 0;

    //Content URI for the existing task (null if it's a new task)
    private Uri mCurrentTaskUri;

    private EditText mNameEditText;
    private EditText mNotesEditText;
    private TextView mDateTextView;
    private Button mBtnDate;
    int year_x, month_x, day_x;
    private Spinner mPrioritySpinner;
    private Spinner mStatusSpinner;

    private int mPriority = TaskContract.TaskEntry.PRIORITY_LOW;
    private int mStatus = TaskContract.TaskEntry.STATUS_TODO;

    //Boolean flag that keeps track of whether the item has been edited(true) or not(false)
    private boolean mTaskHasChanged = false;

    //OnTouchListener that listens for any user touches on a View, implying that they are modifying the view
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mTaskHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Examine the intent that was used to launch this activity
        //in order to figure out if we're creating a new item or editing an existing one

        Intent intent = getIntent();
        mCurrentTaskUri = intent.getData();

        //If the intent DOES NOT contain an item content URI, then we know that we are creating a new item
        if (mCurrentTaskUri == null) {
            //This is a new item, so change the app bar to say "Add a Task"
            setTitle(getString(R.string.editor_activity_title_new_task));

            //Invalidate the options menu, so the "Delete" menu option can be hidden
            //It does not make sense to delete a task that has not been created yet
            invalidateOptionsMenu();

        } else {
            //Otherwise this is an existing task, so change the app bar to say "Edit Task"
            setTitle(getString(R.string.editor_activity_title_edit_task));

            //Initialize a loader to read the task data from the database
            //and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_TASK_LOADER, null, this);

        }

        //Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_task_name);
        mNotesEditText = (EditText) findViewById(R.id.edit_task_note);
        mDateTextView = (TextView) findViewById(R.id.showDate);
        mPrioritySpinner = (Spinner) findViewById(R.id.spinner_priority);
        mStatusSpinner = (Spinner) findViewById(R.id.spinner_status);

        mBtnDate = (Button) findViewById(R.id.btnPicDate);

        mBtnDate.setOnClickListener(this);

        //Setup OnTouchListeners on all the input fields, so we can determine if the user
        //has touched or modified them, this will let us know if there are unsaved changes
        //or not, if the user tries to leave the editor without saving
        mNameEditText.setOnTouchListener(mTouchListener);
        mNotesEditText.setOnTouchListener(mTouchListener);
        mDateTextView.setOnTouchListener(mTouchListener);
        mPrioritySpinner.setOnTouchListener(mTouchListener);
        mStatusSpinner.setOnTouchListener(mTouchListener);

        setupSpinnerPriority();
        setupSpinnerStatus();


    }

    //Setup the dropdown spinner that allows the user to select the priority of the task

    private void setupSpinnerPriority() {
        //Create adapter for spinner. This list options are from the String Array it will use
        //the spinner will use the default layout
        ArrayAdapter prioritySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_priority_options, android.R.layout.simple_spinner_item);

        //Specify dropdown layout style = simple list view with 1 item per line
        prioritySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        //Apply the adapter to the spinner
        mPrioritySpinner.setAdapter(prioritySpinnerAdapter);

        //Set the integer mSelected to the constant values
        mPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);

                if (!TextUtils.isEmpty(selection)) {

                    switch (position){

                        case 0:
                            mPriority = TaskContract.TaskEntry.PRIORITY_UNKNOWN;
                            break;
                        case 1:
                            mPriority = TaskContract.TaskEntry.PRIORITY_HIGH;
                            break;
                        case 2:
                            mPriority = TaskContract.TaskEntry.PRIORITY_MEDIUM;
                            break;
                        case 3:
                            mPriority = TaskContract.TaskEntry.PRIORITY_LOW;
                            break;

                    }


                    if (selection.equals(getString(R.string.priority_high))) {
                      //  mPriority = TaskContract.TaskEntry.PRIORITY_HIGH;


                    } else if (selection.equals(R.string.priority_medium)) {
                     //   mPriority = TaskContract.TaskEntry.PRIORITY_MEDIUM;

                    } else if (selection.equals(R.string.priority_low)) {
                     //   mPriority = TaskContract.TaskEntry.PRIORITY_LOW;

                    } else {
                   //    mPriority = TaskContract.TaskEntry.PRIORITY_UNKNOWN;

                    }


                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
               mPriority = TaskContract.TaskEntry.PRIORITY_UNKNOWN;


            }
        });





    }

    //Setup the dropdown spinner that allows the user to select the status of the task

    private void setupSpinnerStatus(){
        //Create adapter for spinner. This list options are from the String Array it will use
        //the spinner will use the default layout
        ArrayAdapter statusSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_status_options, android.R.layout.simple_spinner_item);

        //Specify dropdown layout style = simple list view with 1 item per line
        statusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        //Apply the adapter to the spinner
        mStatusSpinner.setAdapter(statusSpinnerAdapter);

        //Set the integer mSelected to the constant values
        mStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if(!TextUtils.isEmpty(selection)) {

                    switch (position){

                        case 0:
                            mStatus = TaskContract.TaskEntry.STATUS_UNKNOWN;
                            break;
                        case 1:
                            mStatus = TaskContract.TaskEntry.STATUS_DONE;
                            break;
                        case 2:
                            mStatus = TaskContract.TaskEntry.STATUS_TODO;
                            break;

                    }

                    if (selection.equals(getString(R.string.status_to_do))) {
                      //  mStatus = TaskContract.TaskEntry.STATUS_TODO;
                    } else if (selection.equals(getString(R.string.status_done))) {
                      //  mStatus = TaskContract.TaskEntry.STATUS_DONE;

                    } else {
                      //  mStatus = TaskContract.TaskEntry.STATUS_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mStatus = TaskContract.TaskEntry.STATUS_UNKNOWN;

            }
        });





    }

    //Get user input from editor and save pet into database
    private void saveTask() {

        //Read from input fields
        //Use trim to eliminate leading or trailing white space

        String nameString = mNameEditText.getText().toString().trim();
        String notesString = mNotesEditText.getText().toString().trim();
        String dateString = mDateTextView.getText().toString().trim();



        //Check if this is supposed to be a new task
        //and check if all the fields in the editor are blank

        if(mCurrentTaskUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(notesString) && TextUtils.isEmpty(dateString) &&
               mPriority == TaskContract.TaskEntry.PRIORITY_UNKNOWN && mStatus == TaskContract.TaskEntry.STATUS_UNKNOWN){
            //Since no fields where modified, we can return early without creating a new task
            //No need to crate ContentValues and no need to do any ContentProvider operations

            return;

        }

        //Create ContentValues objects where columns are the keys, and the attributes from the editor are the values

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.TASK_NAME, nameString);
        values.put(TaskContract.TaskEntry.NOTES, notesString);
        values.put(TaskContract.TaskEntry.DUE_DATE, dateString);
        values.put(TaskContract.TaskEntry.PRIORITY, mPriority);
        values.put(TaskContract.TaskEntry.STATUS, mStatus);

        //Determine if this is a new or existing task by checking if mCurrentTaskUri is null or not
        if (mCurrentTaskUri == null) {

            Uri newUri = getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, values);

            //Show a toast message depending on whether or not the insertion was successful

            if (newUri == null) {

                Toast.makeText(this, getString(R.string.editor_insert_row_failed), Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(this, getString(R.string.editor_insert_row_successfull), Toast.LENGTH_SHORT).show();

            }
        } else {

            //Otherwise, this is an EXISTING ITEM, so update the item with content URI:mCurrentTaskUri

            int rowsAffected = getContentResolver().update(mCurrentTaskUri, values, null, null);

            //Show a toast message depending on whether or not the update was successful
            if (rowsAffected == 0) {

                Toast.makeText(this, getString(R.string.editor_update_task_failed), Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(this, getString(R.string.editor_update_task_successful), Toast.LENGTH_SHORT).show();

            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;

    }

    //This method is called after invalidateOptionsMenu()

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        //If this is a new item, hid the "Delete" menu option
        if (mCurrentTaskUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //User clicked on a menu option in the app barr overflow menu
        switch (item.getItemId()){
            //Respond to a click on the "Save" menu option
            case R.id.action_save:
                //save task to database
                saveTask();
                //exit activity
                finish();
                return true;
            //Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                //Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            //Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                //If the item has not changed, continue with navigating up to parent activity
                if (!mTaskHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                //Otherwise if there are unsaved changes, setup a dialog to warn the user

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //User clicked "Discard" button, navigate to parent activity
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                //Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    //This method is called when the back button is pressed
    @Override
    public void onBackPressed(){
        //If item has not changed, continue with handling back button press
        if (!mTaskHasChanged){
            super.onBackPressed();
            return;
        }

        //Otherwise if there are unsaved changes, setup a dialog to warn the user

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //User clicked "Discard" button, close the current activity
                finish();
            }
        };

        //Show dialog that are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);


    }




    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Since the editor shows all task attributes, define a projection that contains all columns from the table
        String[] projection = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.TASK_NAME,
                TaskContract.TaskEntry.NOTES,
                TaskContract.TaskEntry.DUE_DATE,
                TaskContract.TaskEntry.PRIORITY,
                TaskContract.TaskEntry.STATUS
        };

        //This loader will execute the ContentProvider's query method on a background thread

        return new CursorLoader(this,
                mCurrentTaskUri,
                projection,
                null,
                null,
                null);


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Bail early if the cursor is null or therer is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1){
            return;
        }

        //Proceed with moving to first row of the cursor and reading data from it
        if (cursor.moveToFirst()){
            //Find the columns of task attributes we're interested in
            int nameColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.TASK_NAME);
            int notesColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.NOTES);
            int dateColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.DUE_DATE);
            int priorityColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.PRIORITY);
            int statusColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.STATUS);

            //Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String notes = cursor.getString(notesColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            int priority = cursor.getInt(priorityColumnIndex);
            int status = cursor.getInt(statusColumnIndex);

            Log.i("PRIORITY", String.valueOf(priority));

            //Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mNotesEditText.setText(notes);
            mDateTextView.setText(date);


            //Priority Spinner
            switch (priority){
                case TaskContract.TaskEntry.PRIORITY_UNKNOWN:
                    mPrioritySpinner.setSelection(0);
                    break;
                case TaskContract.TaskEntry.PRIORITY_LOW:
                    mPrioritySpinner.setSelection(3);
                    break;
                case TaskContract.TaskEntry.PRIORITY_MEDIUM:
                    mPrioritySpinner.setSelection(2);
                    break;
                case TaskContract.TaskEntry.PRIORITY_HIGH:
                    mPrioritySpinner.setSelection(1);
            }

            //Status Spinner
            switch (status){
                case TaskContract.TaskEntry.STATUS_UNKNOWN:
                    mStatusSpinner.setSelection(0);
                    break;
                case TaskContract.TaskEntry.STATUS_DONE:
                    mStatusSpinner.setSelection(1);
                    break;
                case TaskContract.TaskEntry.STATUS_TODO:
                    mStatusSpinner.setSelection(2);
                    break;

            }

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //If the loader is invalidated, clear out all the data from the input fields
        mNameEditText.setText("");
        mNotesEditText.setText("");
        mDateTextView.setText("");
        mPrioritySpinner.setSelection(0);
        mStatusSpinner.setSelection(0);

    }

    //Show a dialog that warns the user the user there are unsaved changes that will be lost

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        //Create an AlertDialog.Builder and set the message, and click listeners
        //for the positive and negative buttons on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //User clicked the "Keep Editing" button so dismiss the dialog and continue editing
                if (dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });
        //Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void showDeleteConfirmationDialog(){
        //Create an AlertDialog.Builder and set the message, and click listeners
        //for the positive and negative buttons on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //User clicked "Delete" button, so delete the task
                deleteTask();
            }
        });
        //Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Deletion of task in the database
    private void deleteTask(){

        //Only permors the delete if there is an existing item
        if (mCurrentTaskUri != null) {
            //Call the ContentResolver to delete the task ant the given content URI
            int rowsDeleted = getContentResolver().delete(mCurrentTaskUri, null, null);

            //Show a toast message depending on whether or not the delete was successful
            if (rowsDeleted == 0){
                Toast.makeText(this, getString(R.string.editor_delete_task_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_task_successful), Toast.LENGTH_SHORT).show();
            }

        }

        //Close the activity
        finish();
    }


    @Override
    public void onClick(View view) {
        final Calendar calendar = Calendar.getInstance();
        year_x = calendar.get(Calendar.YEAR);
        month_x = calendar.get(Calendar.MONTH);
        day_x = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                String dateFromDatePicker = String.valueOf(month+1) + "/" + String.valueOf(day) + "/" + String.valueOf(year);

                mDateTextView.setText(dateFromDatePicker);


            }
        }
                ,year_x, month_x,day_x);

        datePickerDialog.show();




    }



}
