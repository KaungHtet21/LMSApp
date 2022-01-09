package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.adapters.TeachersRegisterListAdapter;
import com.khk.lmsapp.modules.Students;
import com.khk.lmsapp.modules.Teachers;

import java.util.ArrayList;
import java.util.Iterator;

public class TeacherRegisterActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView textView;
    ArrayList<Teachers> teachersList = new ArrayList<>();
    TeachersRegisterListAdapter adapter;

    DatabaseReference teReqRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_register);

        toolbar = findViewById(R.id.te_register_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Teacher Register List");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.te_register_rv);
        teReqRef = FirebaseDatabase.getInstance().getReference();
        textView = findViewById(R.id.default_tv);

        adapter = new TeachersRegisterListAdapter(teachersList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        CheckList();

        teReqRef.child("Teachers Request")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()){
                            Teachers teachers = snapshot.getValue(Teachers.class);
                            teachersList.add(teachers);

                            adapter.setTeachers(teachersList);
                            adapter.notifyDataSetChanged();
                            textView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        Teachers teachers = snapshot.getValue(Teachers.class);

                        adapter.deleteFromRef(teachers);
                        CheckList();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void CheckList() {
        teReqRef.child("Teachers Request")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            textView.setVisibility(View.GONE);
                        }else {
                            textView.setVisibility(View.VISIBLE);
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