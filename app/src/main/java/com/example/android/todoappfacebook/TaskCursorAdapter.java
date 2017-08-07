package com.example.android.todoappfacebook;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.todoappfacebook.data.TaskContract;

/**
 * Created by Admin on 2/7/17.
 */

public class TaskCursorAdapter extends CursorAdapter {
    public TaskCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priorityTextView = (TextView) view.findViewById(R.id.priority);

        //Find the columns fo attibutes that we're interested int
        int nameColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.TASK_NAME);
        int priorityColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.PRIORITY);

        //Read the item attributes from the cursor for the current item
        String taskName = cursor.getString(nameColumnIndex);
        int taskPriority = cursor.getInt(priorityColumnIndex);

        //Update the TextViews
        nameTextView.setText(taskName);

        switch (taskPriority){
            case 0:
                priorityTextView.setText(R.string.priority_unknown);
                priorityTextView.setTextColor(Color.GRAY);
                break;
            case 1:
                priorityTextView.setText(R.string.priority_high);
                priorityTextView.setTextColor(Color.RED);
                break;
            case 2:
                priorityTextView.setText(R.string.priority_medium);
                priorityTextView.setTextColor(Color.YELLOW);
                break;
            case 3:
                priorityTextView.setText(R.string.priority_low);
                priorityTextView.setTextColor(Color.GREEN);
                break;
        }


    }
}
