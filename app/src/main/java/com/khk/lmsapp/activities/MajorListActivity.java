package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.khk.lmsapp.R;
import com.khk.lmsapp.SharePreferenceHelper;

public class MajorListActivity extends AppCompatActivity {

    Toolbar toolbar;
    CardView firstCse, firstEce, secondCse, secondEce, thirdCse, thirdEce,forthCse, forthEce, fifthCse, fifthEce;
    String entry;

    private SharePreferenceHelper sharePreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_major_list);

        init();
        entry = getIntent().getStringExtra("entry");

        toolbar = findViewById(R.id.admin_view_course_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Major List");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        sharePreferenceHelper = new SharePreferenceHelper(this);

        firstCse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MajorListActivity.this, AddCourseActivity.class);
                intent.putExtra("major", "First Year / CSE");
                intent.putExtra("entry", entry);
                startActivity(intent);
            }
        });

        firstEce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MajorListActivity.this, AddCourseActivity.class);
                intent.putExtra("major", "First Year / ECE");
                intent.putExtra("entry", entry);
                startActivity(intent);
            }
        });

        secondCse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MajorListActivity.this, AddCourseActivity.class);
                intent.putExtra("major", "Second Year / CSE");
                intent.putExtra("entry", entry);
                startActivity(intent);
            }
        });

        secondEce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MajorListActivity.this, AddCourseActivity.class);
                intent.putExtra("major", "Second Year / ECE");
                intent.putExtra("entry", entry);
                startActivity(intent);
            }
        });

        thirdCse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MajorListActivity.this, AddCourseActivity.class);
                intent.putExtra("major", "Third Year / CSE");
                intent.putExtra("entry", entry);
                startActivity(intent);
            }
        });

        thirdEce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MajorListActivity.this, AddCourseActivity.class);
                intent.putExtra("major", "Third Year / ECE");
                intent.putExtra("entry", entry);
                startActivity(intent);
            }
        });

        forthCse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MajorListActivity.this, AddCourseActivity.class);
                intent.putExtra("major", "Forth Year / CSE");
                intent.putExtra("entry", entry);
                startActivity(intent);
            }
        });

        forthEce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MajorListActivity.this, AddCourseActivity.class);
                intent.putExtra("major", "Forth Year / ECE");
                intent.putExtra("entry", entry);
                startActivity(intent);
            }
        });

        fifthCse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MajorListActivity.this, AddCourseActivity.class);
                intent.putExtra("major", "Fifth Year / CSE");
                intent.putExtra("entry", entry);
                startActivity(intent);
            }
        });

        fifthEce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MajorListActivity.this, AddCourseActivity.class);
                intent.putExtra("major", "Fifth Year / ECE");
                intent.putExtra("entry", entry);
                startActivity(intent);
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

    private void init() {
        firstCse = findViewById(R.id.admin_course_1st_cse);
        firstEce = findViewById(R.id.admin_course_1st_ece);
        secondCse = findViewById(R.id.admin_course_2nd_cse);
        secondEce = findViewById(R.id.admin_course_2nd_ece);
        thirdCse = findViewById(R.id.admin_course_3rd_cse);
        thirdEce = findViewById(R.id.admin_course_3rd_ece);
        forthCse = findViewById(R.id.admin_course_4th_cse);
        forthEce = findViewById(R.id.admin_course_4th_ece);
        fifthCse = findViewById(R.id.admin_course_5th_cse);
        fifthEce = findViewById(R.id.admin_course_5th_ece);
    }
}