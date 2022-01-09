package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.adapters.DateAdapter;
import com.khk.lmsapp.modules.DateList;

import java.util.ArrayList;

public class DateActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rv;
    TextView tv;
    String major, course;

    DatabaseReference attRef;
    ArrayList<DateList> list = new ArrayList<>();

    DateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        toolbar = findViewById(R.id.date_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Date");

        major = getIntent().getStringExtra("Major");
        course = getIntent().getStringExtra("Course");
        tv = findViewById(R.id.default_tv);

        rv = findViewById(R.id.date_rv);
        attRef = FirebaseDatabase.getInstance().getReference().child("Attendance").child(major).child(course);

        attRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    tv.setVisibility(View.VISIBLE);
                }else {
                    tv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        attRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String dateKey = ds.getKey();
                    list.add(new DateList(dateKey));

                    adapter = new DateAdapter(list, major, course);
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();

                    rv.setLayoutManager(new LinearLayoutManager(DateActivity.this));
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}