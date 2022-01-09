package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.adapters.SearchRecommendAdapter;
import com.khk.lmsapp.adapters.TeacherListAdapter;
import com.khk.lmsapp.modules.Students;
import com.khk.lmsapp.modules.Teachers;

import java.util.ArrayList;

public class TeacherListActivity extends AppCompatActivity {

    Toolbar toolbar;
    SearchView search;
    RecyclerView rv;
    ListView searchList;
    TextView tv;
    ArrayList<Teachers> teachersList = new ArrayList<>();
    ArrayList<Students> usersList = new ArrayList<>();

    DatabaseReference teRef, adminRef;
    TeacherListAdapter adapter;
    SearchRecommendAdapter recommendAdapter;
    String entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_list);

        toolbar = findViewById(R.id.te_list_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Teacher List");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        init();

        entry = getIntent().getStringExtra("entry");

        rv = findViewById(R.id.te_list_rv);
        teRef = FirebaseDatabase.getInstance().getReference().child("Teachers");
        adminRef = FirebaseDatabase.getInstance().getReference().child("Admins");

        adapter = new TeacherListAdapter(teachersList);
        rv.setLayoutManager(new GridLayoutManager(TeacherListActivity.this, 2));
        rv.setAdapter(adapter);

        if (entry.equals("teacher_list")){
            teRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

            teRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()){
                        Teachers teachers = ds.getValue(Teachers.class);
                        teachersList.add(teachers);

                        adapter.setTeachers(teachersList);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            teRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()){
                        Students users = ds.getValue(Students.class);
                        usersList.add(users);

                        recommendAdapter = new SearchRecommendAdapter(usersList, entry);
                        searchList.setAdapter(recommendAdapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else if (entry.equals("admin_list")){
            adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        tv.setVisibility(View.GONE);
                    }else {
                        tv.setText("There is no admin yet.");
                        tv.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            adminRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()){
                        Teachers teachers = ds.getValue(Teachers.class);
                        teachersList.add(teachers);

                        adapter.setTeachers(teachersList);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            adminRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()){
                        Students users = ds.getValue(Students.class);
                        usersList.add(users);

                        recommendAdapter = new SearchRecommendAdapter(usersList, entry);
                        searchList.setAdapter(recommendAdapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    rv.setVisibility(View.GONE);
                }else if (search.getQuery().length() == 0) {
                    rv.setVisibility(View.VISIBLE);
                }
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)){
                    searchList.clearTextFilter();
                    searchList.setVisibility(View.GONE);
                }else {
                    Filter filter = recommendAdapter.getFilter();
                    filter.filter(newText);
                    searchList.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
    }

    private void init() {
        search = findViewById(R.id.te_list_sv);
        searchList = findViewById(R.id.search_recommend_list);
        tv = findViewById(R.id.default_tv);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}