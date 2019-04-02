package ca.bcit.ass2.lee_dong;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText editTextFirstName;
    EditText editTextLastName;
    EditText datePick;
    Button buttonAddStudent;
    DatabaseReference databaseStudents;
    ListView lvStudents;
    List<ToDo> TodoList;
    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");


    private void showUpdateDialog(final String studentId, String firstName, String who , Date date) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText editTextFirstName = dialogView.findViewById(R.id.editTextFirstName);
        editTextFirstName.setText(firstName);
        final EditText editTextLastName = dialogView.findViewById(R.id.editTextLastName);
        editTextLastName.setText(who);
        final EditText datePick  = dialogView.findViewById(R.id.date);

        final Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);
        dialogBuilder.setTitle("Task");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = editTextFirstName.getText().toString().trim();
                String lastName = editTextLastName.getText().toString().trim();
                Date date = null;
                try {
                    date = formatter.parse(datePick.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                if (TextUtils.isEmpty(firstName)) {
                    editTextFirstName.setError("First Name is required");
                    return;
                } else if (TextUtils.isEmpty(lastName)) {
                    editTextLastName.setError("Last Name is required");
                    return;
                }
                updateStudent(studentId, firstName, lastName, date);
                alertDialog.dismiss();
            }
        });
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteStudent(studentId);
                alertDialog.dismiss();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        databaseStudents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TodoList.clear();


                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    ToDo student = studentSnapshot.getValue(ToDo.class);
                    TodoList.add(student);
                }
                ToDoListAdapter adapter = new ToDoListAdapter(MainActivity.this, TodoList);
                lvStudents.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void addStudent() {
        String task = editTextFirstName.getText().toString().trim();
        String who = editTextLastName.getText().toString().trim();
        Date date = new Date();
        try {
            date = formatter.parse(datePick.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(task)) {
            Toast.makeText(this, "You must enter a first name.", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(who)) {
            Toast.makeText(this, "You must enter a last name.", Toast.LENGTH_LONG).show();
            return;
        }
        String id = databaseStudents.push().getKey();
        ToDo toDo = new ToDo(id, task, who, date);
        Task setValueTask = databaseStudents.child(id).setValue(toDo);
        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Student added.", Toast.LENGTH_LONG).show();
            }
        });
        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    void updateStudent(String id, String task, String who, Date date) {
        DatabaseReference dbRef = databaseStudents.child(id);
        ToDo student = new ToDo(id, task, who,  date);
        Task setValueTask = dbRef.setValue(student);
        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Student Updated.", Toast.LENGTH_LONG).show();
            }
        });
        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseStudents = FirebaseDatabase.getInstance().getReference("students");
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        datePick = findViewById(R.id.date);
        buttonAddStudent = findViewById(R.id.buttonAddStudent);

        lvStudents = findViewById(R.id.lvStudents);
        TodoList = new ArrayList<ToDo>();
        buttonAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStudent();
            }
        });

        lvStudents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ToDo student = TodoList.get(position);
                showUpdateDialog(student.getTaskID(),
                        student.getTask(),
                        student.getWho(),
                        student.getDueDate());
                return false;
            }
        });

    }

    private void deleteStudent(String id) {
        DatabaseReference dbRef = databaseStudents.child(id);
        Task setRemoveTask = dbRef.removeValue();
        setRemoveTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Student Deleted.", Toast.LENGTH_LONG).show();
            }
        });
        setRemoveTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

