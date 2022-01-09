package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.adapters.DetailAttAdapter;
import com.khk.lmsapp.modules.DateList;

import java.util.ArrayList;

public class DetailAttActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rv;
    TextView present, absent, total;
    String roll, major, course;

    ArrayList<DateList> dateLists = new ArrayList<>();
    DetailAttAdapter adapter;
    DatabaseReference attRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_att);

        roll = getIntent().getStringExtra("roll");
        major = getIntent().getStringExtra("major");
        course = getIntent().getStringExtra("course");

        toolbar = findViewById(R.id.detail_att_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(roll);

        present = findViewById(R.id.present_per);
        absent = findViewById(R.id.absent_per);
        total = findViewById(R.id.total_day);
        rv = findViewById(R.id.detail_att_rv);
        attRef = FirebaseDatabase.getInstance().getReference().child("Attendance").child(major).child(course);

        attRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    dateLists.add(new DateList(ds.getKey()));

                    adapter = new DetailAttAdapter(dateLists, major, course, roll);
                    adapter.setDateLists(dateLists);

                    rv.setLayoutManager(new LinearLayoutManager(DetailAttActivity.this));
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        attRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totalCount = snapshot.getChildrenCount();
                float presentCount = 0;
                float absentCount;
                float presentPer;
                float absentPer;
                int presentInt, absentInt, totalInt;
                for (DataSnapshot dateSnapshot: snapshot.getChildren()){
                    String existence = dateSnapshot.child(roll).child("existence").getValue().toString();
                    if (existence.equals("Present")){

                        presentCount++;
                        absentCount = totalCount - presentCount;

                        presentPer = (presentCount / totalCount) * 100;
                        absentPer = 100 - presentPer;

                        totalInt = (int) totalCount;
                        presentInt = (int) presentCount;
                        absentInt = (int) absentCount;

                        total.setText("Total("+totalInt+")(100%)");
                        present.setText("Present("+presentInt+")("+presentPer+"%)");
                        absent.setText("Absent("+absentInt+")("+absentPer+"%)");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}