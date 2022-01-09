package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.SharePreferenceHelper;
import com.khk.lmsapp.adapters.TotalCoursesAdapter;
import com.khk.lmsapp.modules.Course;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AddCourseActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rv;
    FloatingActionButton fab;
    String major, entry, roll;
    TextView tv;
    DatabaseReference courseRef;
    TotalCoursesAdapter adapter;
    ArrayList<Course> coursesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        entry = getIntent().getStringExtra("entry");
        major = getIntent().getStringExtra("major");
        roll = getIntent().getStringExtra("roll");
        tv = findViewById(R.id.default_tv);

        toolbar = findViewById(R.id.add_course_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(major);

        fab = findViewById(R.id.add_course_fab);
        rv = findViewById(R.id.total_courses_rv);
        courseRef = FirebaseDatabase.getInstance().getReference().child("Courses");

        adapter = new TotalCoursesAdapter(coursesList, major, entry);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        rv.setAdapter(adapter);

        courseRef.child(major)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    tv.setVisibility(View.GONE);
                }else {
                    tv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        courseRef.child(major)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()){
                            DisplayList(snapshot);
                            tv.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        if (entry.equals("admin_course")){
            fab.setVisibility(View.VISIBLE);
        }else {
            fab.setVisibility(View.GONE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(AddCourseActivity.this);
                dialog.setContentView(R.layout.dialog_add_course);

                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setLayout(width, height);
                dialog.show();

                EditText course = dialog.findViewById(R.id.add_course_name_edt);
                EditText lecturer = dialog.findViewById(R.id.add_course_lecturer_edt);
                Spinner credit = dialog.findViewById(R.id.add_course_credit_spinner);
                Button add = dialog.findViewById(R.id.add_course_btn);

                Integer[] credits_items = new Integer[]{1,2,3,4,5};
                ArrayAdapter<Integer> credits_adapter = new ArrayAdapter<Integer>(AddCourseActivity.this, R.layout.support_simple_spinner_dropdown_item, credits_items);
                credit.setAdapter(credits_adapter);

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String courseInput = course.getText().toString();
                        String lecturerInput = lecturer.getText().toString();
                        String creditInput = credit.getSelectedItem().toString();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("name", courseInput);
                        hashMap.put("lecturer", lecturerInput);
                        hashMap.put("credit", creditInput);

                        courseRef.child(major).child(courseInput).updateChildren(hashMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            dialog.dismiss();
                                            Toast.makeText(AddCourseActivity.this, courseInput +" course is added successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }

    private void DisplayList(DataSnapshot snapshot) {
        Iterator iterator = snapshot.getChildren().iterator();
        while (iterator.hasNext()){
            String credit = (String) ((DataSnapshot) iterator.next()).getValue();
            String lecturer = (String) ((DataSnapshot) iterator.next()).getValue();
            String name = (String) ((DataSnapshot) iterator.next()).getValue();

            coursesList.add(new Course(name, lecturer, credit));

            adapter.setCourses(coursesList);
            adapter.setRoll(roll);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}