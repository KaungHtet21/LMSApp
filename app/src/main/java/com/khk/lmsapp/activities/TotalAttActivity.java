package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.adapters.TotalAttAdapter;
import com.khk.lmsapp.modules.Students;

import java.util.ArrayList;

public class TotalAttActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rv;
    String major, course, date;
    DatabaseReference stRef;
    ArrayList<Students> studentsList = new ArrayList<>();
    TotalAttAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_att);

        toolbar = findViewById(R.id.total_att_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Attendance");

        major = getIntent().getStringExtra("major");
        course = getIntent().getStringExtra("course");
        date = getIntent().getStringExtra("date");

        rv = findViewById(R.id.total_att_rv);
        stRef = FirebaseDatabase.getInstance().getReference().child("Students");

        if (major.equals("First Year / CSE")){
            stRef.child("First Year Batch").child("CSE")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()){
                                Students students = ds.getValue(Students.class);
                                studentsList.add(students);

                                adapter = new TotalAttAdapter(studentsList, major, course, date);
                                adapter.setStudents(studentsList);
                                adapter.notifyDataSetChanged();

                                rv.setAdapter(adapter);
                                rv.setLayoutManager(new LinearLayoutManager(TotalAttActivity.this));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
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