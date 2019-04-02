package ca.bcit.ass2.lee_dong;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ToDoListAdapter extends ArrayAdapter<ToDo> {

    private Activity context;
    private List<ToDo> ToDoList;

    public ToDoListAdapter(Activity context, List<ToDo> ToDoList) {
        super(context, R.layout.list_layout, ToDoList);
        this.context = context;
        this.ToDoList = ToDoList;
    }

    public ToDoListAdapter(Context context, int resource, List<ToDo> objects, Activity context1, List<ToDo>
            ToDoList) {
        super(context, resource, objects);
        this.context = context1;
        this.ToDoList = ToDoList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);
        TextView tvName = listViewItem.findViewById(R.id.textViewName);
        TextView tvSchool = listViewItem.findViewById(R.id.textViewSchool);
        ToDo List = ToDoList.get(position);
        tvName.setText(List.getTask()
                + " " + List.getWho());

        return listViewItem;
    }
}

