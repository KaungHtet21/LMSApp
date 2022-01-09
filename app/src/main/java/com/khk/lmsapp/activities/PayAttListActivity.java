package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.adapters.PayAttAdapter;
import com.khk.lmsapp.adapters.SearchRecommendAdapter;
import com.khk.lmsapp.modules.Students;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class PayAttListActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rv;

    DatabaseReference stRef, attRef;
    PayAttAdapter adapter;

    ArrayList<Students> studentsList = new ArrayList<>();
    String major, course, saveCurrentDate;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_att_list);

        toolbar = findViewById(R.id.pay_att_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Pay Attendance");

        major = getIntent().getStringExtra("Major");
        course = getIntent().getStringExtra("Course");

        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat currentDate = new SimpleDateFormat("MMM, dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        rv = findViewById(R.id.st_list_rv);
        stRef = FirebaseDatabase.getInstance().getReference().child("Students");
        attRef = FirebaseDatabase.getInstance().getReference().child("Attendance").child(major).child(course).child(saveCurrentDate);

        adapter = new PayAttAdapter(studentsList, major, course);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        switch (major){
            case "First Year / CSE":
                stRef.child("First Year Batch").child("CSE")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    DisplayList(ds);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                break;
            case "First Year / ECE":
                stRef.child("First Year Batch").child("ECE")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    DisplayList(ds);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                break;
            case "Second Year / CSE":
                stRef.child("Second Year Batch").child("CSE")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    DisplayList(ds);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                break;
            case "Second Year / ECE":
                stRef.child("Second Year Batch").child("ECE")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    DisplayList(ds);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                break;
            case "Third Year / CSE":
                stRef.child("Third Year Batch").child("CSE")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    DisplayList(ds);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                break;
            case "Third Year / ECE":
                stRef.child("Third Year Batch").child("ECE")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    DisplayList(ds);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                break;
            case "Forth Year / CSE":
                stRef.child("Forth Year Batch").child("CSE")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    DisplayList(ds);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                break;
            case "Forth Year / ECE":
                stRef.child("Forth Year Batch").child("ECE")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    DisplayList(ds);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                break;
            case "Fifth Year / CSE":
                stRef.child("Fifth Year Batch").child("CSE")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    DisplayList(ds);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                break;
            case "Fifth Year / ECE":
                stRef.child("Fifth Year Batch").child("ECE")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    DisplayList(ds);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                break;
        }
    }

    @Override
    public void onBackPressed() {
        attRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = (int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Dialog dialog = new Dialog(PayAttListActivity.this);
        dialog.setContentView(R.layout.dialog_logout);

        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(width, height);
        dialog.show();

        TextView txt = dialog.findViewById(R.id.txt);
        TextView cancel = dialog.findViewById(R.id.logout_cancel);
        TextView logout = dialog.findViewById(R.id.logout);

        txt.setText("Do you want to save data ?");
        logout.setText("Save");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attRef.removeValue();
                dialog.dismiss();
                PayAttListActivity.super.onBackPressed();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count != studentsList.size()){
                    Toast.makeText(PayAttListActivity.this, "You forgot to pay attendance to some students.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else {
                    Toast.makeText(PayAttListActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    PayAttListActivity.super.onBackPressed();
                }
            }
        });

    }

    private void DisplayList(DataSnapshot snapshot) {
        Iterator iterator = snapshot.getChildren().iterator();
        while (iterator.hasNext()){
            String address = (String) ((DataSnapshot)iterator.next()).getValue();
            String batch = (String) ((DataSnapshot)iterator.next()).getValue();
            String bd = (String) ((DataSnapshot)iterator.next()).getValue();
            String email = (String) ((DataSnapshot)iterator.next()).getValue();
            String gender = (String) ((DataSnapshot)iterator.next()).getValue();
            String id = (String) ((DataSnapshot)iterator.next()).getValue();
            String image = (String) ((DataSnapshot)iterator.next()).getValue();
            String name = (String) ((DataSnapshot)iterator.next()).getValue();
            String password = (String) ((DataSnapshot)iterator.next()).getValue();
            String ph = (String) ((DataSnapshot)iterator.next()).getValue();
            String role = (String) ((DataSnapshot)iterator.next()).getValue();
            String roll = (String) ((DataSnapshot)iterator.next()).getValue();

            studentsList.add(new Students(address, batch, bd, email, gender, id, image, name, password, ph, role, roll));

            adapter.setStudents(studentsList);
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