package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.adapters.SearchRecommendAdapter;
import com.khk.lmsapp.adapters.StudentsListAdapter;
import com.khk.lmsapp.modules.Students;

import java.util.ArrayList;

public class StudentListActivity extends AppCompatActivity {

    Toolbar toolbar;
    SearchView sv;
    LinearLayout parent;
    ListView lv;
    RecyclerView firstCseRv, firstEceRv, secondCseRv, secondEceRv, thirdCseRv, thirdEceRv, forthCseRv, forthEceRv, fifthCseRv, fifthEceRv;
    DatabaseReference stRef;
    SearchRecommendAdapter recommendAdapter;
    ArrayList<Students> usersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        toolbar = findViewById(R.id.st_list_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Student List");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        sv = findViewById(R.id.st_list_sv);
        lv = findViewById(R.id.search_recommend_list);
        parent = findViewById(R.id.linear_layout);

        firstCseRv = findViewById(R.id.first_cse_rv);
        firstEceRv = findViewById(R.id.first_ece_rv);
        secondCseRv = findViewById(R.id.second_cse_rv);
        secondEceRv = findViewById(R.id.second_ece_rv);
        thirdCseRv = findViewById(R.id.third_cse_rv);
        thirdEceRv = findViewById(R.id.third_ece_rv);
        forthCseRv = findViewById(R.id.forth_cse_rv);
        forthEceRv = findViewById(R.id.forth_ece_rv);
        fifthCseRv = findViewById(R.id.fifth_cse_rv);
        fifthEceRv = findViewById(R.id.fifth_ece_rv);

        stRef = FirebaseDatabase.getInstance().getReference().child("Students");

        stRef.child("First Year Batch").child("CSE")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        StudentsListAdapter adapter = new StudentsListAdapter("First Year / CSE");;
                        for (DataSnapshot ds: snapshot.getChildren()){
                            Students students = ds.getValue(Students.class);

                            adapter.setStudents(students);
                            adapter.notifyDataSetChanged();

                            firstCseRv.setLayoutManager(new LinearLayoutManager(StudentListActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            firstCseRv.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        stRef.child("First Year Batch").child("ECE")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        StudentsListAdapter adapter = new StudentsListAdapter("First Year / ECE");
                        for (DataSnapshot ds: snapshot.getChildren()){
                            Students students = ds.getValue(Students.class);

                            adapter.setStudents(students);
                            adapter.notifyDataSetChanged();

                            firstEceRv.setLayoutManager(new LinearLayoutManager(StudentListActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            firstEceRv.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        stRef.child("Second Year Batch").child("CSE")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        StudentsListAdapter adapter = new StudentsListAdapter("Second Year / CSE");
                        for (DataSnapshot ds: snapshot.getChildren()){
                            Students students = ds.getValue(Students.class);

                            adapter.setStudents(students);
                            adapter.notifyDataSetChanged();

                            secondCseRv.setLayoutManager(new LinearLayoutManager(StudentListActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            secondCseRv.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        stRef.child("Second Year Batch").child("ECE")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        StudentsListAdapter adapter = new StudentsListAdapter("Second Year / ECE");
                        for (DataSnapshot ds: snapshot.getChildren()){
                            Students students = ds.getValue(Students.class);

                            adapter.setStudents(students);
                            adapter.notifyDataSetChanged();

                            secondEceRv.setLayoutManager(new LinearLayoutManager(StudentListActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            secondEceRv.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        stRef.child("Third Year Batch").child("CSE")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        StudentsListAdapter adapter = new StudentsListAdapter("Third Year / CSE");
                        for (DataSnapshot ds: snapshot.getChildren()){
                            Students students = ds.getValue(Students.class);

                            adapter.setStudents(students);
                            adapter.notifyDataSetChanged();

                            thirdCseRv.setLayoutManager(new LinearLayoutManager(StudentListActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            thirdCseRv.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        stRef.child("Third Year Batch").child("ECE")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        StudentsListAdapter adapter = new StudentsListAdapter("Third Year / ECE");
                        for (DataSnapshot ds: snapshot.getChildren()){
                            Students students = ds.getValue(Students.class);

                            adapter.setStudents(students);
                            adapter.notifyDataSetChanged();

                            thirdEceRv.setLayoutManager(new LinearLayoutManager(StudentListActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            thirdEceRv.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        stRef.child("Forth Year Batch").child("CSE")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        StudentsListAdapter adapter = new StudentsListAdapter("Forth Year / CSE");
                        for (DataSnapshot ds: snapshot.getChildren()){
                            Students students = ds.getValue(Students.class);

                            adapter.setStudents(students);
                            adapter.notifyDataSetChanged();

                            forthCseRv.setLayoutManager(new LinearLayoutManager(StudentListActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            forthCseRv.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        stRef.child("Forth Year Batch").child("ECE")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        StudentsListAdapter adapter = new StudentsListAdapter("Forth Year / ECE");
                        for (DataSnapshot ds: snapshot.getChildren()){
                            Students students = ds.getValue(Students.class);

                            adapter.setStudents(students);
                            adapter.notifyDataSetChanged();

                            forthEceRv.setLayoutManager(new LinearLayoutManager(StudentListActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            forthEceRv.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        stRef.child("Fifth Year Batch").child("CSE")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        StudentsListAdapter adapter = new StudentsListAdapter("Fifth Year / CSE");
                        for (DataSnapshot ds: snapshot.getChildren()){
                            Students students = ds.getValue(Students.class);

                            adapter.setStudents(students);
                            adapter.notifyDataSetChanged();

                            fifthCseRv.setLayoutManager(new LinearLayoutManager(StudentListActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            fifthCseRv.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        stRef.child("Fifth Year Batch").child("ECE")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        StudentsListAdapter adapter = new StudentsListAdapter("Fifth Year / ECE");
                        for (DataSnapshot ds: snapshot.getChildren()){
                            Students students = ds.getValue(Students.class);

                            adapter.setStudents(students);
                            adapter.notifyDataSetChanged();

                            fifthEceRv.setLayoutManager(new LinearLayoutManager(StudentListActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            fifthEceRv.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        stRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot batchSnapshot: snapshot.getChildren()){
                    for (DataSnapshot majorSnapshot: batchSnapshot.getChildren()){
                        for (DataSnapshot ds: majorSnapshot.getChildren()){
                            Students users = ds.getValue(Students.class);
                            usersList.add(users);

                            recommendAdapter = new SearchRecommendAdapter(usersList, "student_list");
                            lv.setAdapter(recommendAdapter);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sv.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    parent.setVisibility(View.GONE);
                }else if (sv.getQuery().length() == 0){
                    parent.setVisibility(View.VISIBLE);
                }
            }
        });

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)){
                    lv.clearTextFilter();
                    lv.setVisibility(View.GONE);
                }else {
                    Filter filter = recommendAdapter.getFilter();
                    filter.filter(newText);
                    lv.setVisibility(View.VISIBLE);
                }
                return true;
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