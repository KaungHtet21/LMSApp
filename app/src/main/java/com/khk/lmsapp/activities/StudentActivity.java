package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.sax.ElementListener;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.SharePreferenceHelper;
import com.khk.lmsapp.adapters.AdminDashboardAdapter;
import com.khk.lmsapp.modules.Dashboard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton menu_fab;
    DrawerLayout drawer;
    RecyclerView dashboardRV;
    CircleImageView profilePicture;
    TextView profileName, date;
    CalendarView calendar;
    String userRole, userRoll, major, profileInput, userId;

    FirebaseAuth auth;
    DatabaseReference studentRef, userRef;
    private SharePreferenceHelper sharePreferenceHelper;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        dashboardRV = findViewById(R.id.main_dashboard_list);
        profileName = findViewById(R.id.main_username);
        profilePicture = findViewById(R.id.main_profile_picture);
        date = findViewById(R.id.main_date);
        calendar = findViewById(R.id.calendar_view);

        auth = FirebaseAuth.getInstance();
        studentRef = FirebaseDatabase.getInstance().getReference().child("Students");
        userRef = FirebaseDatabase.getInstance().getReference();

        sharePreferenceHelper = new SharePreferenceHelper(this);

        drawer = findViewById(R.id.student_drawer_layout);
        NavigationView navigationView = findViewById(R.id.student_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        menu_fab = findViewById(R.id.fab);
        menu_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("MMM, dd, yyyy");
        String selectedDate = sdf.format(new Date(calendar.getDate()));
        date.setText(selectedDate);

        ArrayList<Dashboard> items = new ArrayList<>();
        items.add(new Dashboard("Admin", R.drawable.admin_logo));
        items.add(new Dashboard("Teacher", R.drawable.teacher_logo));
        items.add(new Dashboard("Message", R.drawable.message_logo));
        items.add(new Dashboard("Courses", R.drawable.courses_logo));
        items.add(new Dashboard("Setting", R.drawable.ic_setting));

        AdminDashboardAdapter adapter = new AdminDashboardAdapter(this);
        adapter.setItems(items);

        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        dashboardRV.setAdapter(adapter);
        dashboardRV.setLayoutManager(llm);

        VerifyUser();
    }

    @Override
    protected void onStart() {
        super.onStart();

        UpdateUserState("online");
        VerifyUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (userId != null){
            UpdateUserState("online");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userId != null){
            UpdateUserState("offline");
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setCheckable(true);
        item.setChecked(true);

        switch (item.getItemId()){
            case R.id.student_dashboard:
                break;
            case R.id.student_message:
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            StudentActivity.this,
                             new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                }else {
                    Intent msgIntent = new Intent(StudentActivity.this, MessageActivity.class);
                    msgIntent.putExtra("id", userId);
                    msgIntent.putExtra("profileInput", profileInput);
                    startActivity(msgIntent);
                }
                break;
            case R.id.student_lectures:
                Intent lecturesIntent = new Intent(StudentActivity.this, AddCourseActivity.class);
                lecturesIntent.putExtra("entry", "student_course");
                lecturesIntent.putExtra("major", major);
                startActivity(lecturesIntent);
                break;
            case R.id.student_note:
                Intent noteIntent = new Intent(StudentActivity.this, NoteActivity.class);
                startActivity(noteIntent);
                break;
            case R.id.student_bookmark:
                Intent bookmarkIntent = new Intent(StudentActivity.this, BookmarkActivity.class);
                startActivity(bookmarkIntent);
                break;
            case R.id.student_watch_admin_list:
                Intent adminListIntent = new Intent(StudentActivity.this, TeacherListActivity.class);
                adminListIntent.putExtra("entry", "admin_list");
                startActivity(adminListIntent);
                break;
            case R.id.student_watch_teacher_list:
                Intent teListIntent = new Intent(StudentActivity.this, TeacherListActivity.class);
                teListIntent.putExtra("entry", "teacher_list");
                startActivity(teListIntent);
                break;
            case R.id.roll_call:
                Intent rcIntent = new Intent(StudentActivity.this, AddCourseActivity.class);
                rcIntent.putExtra("entry", "userInfo");
                rcIntent.putExtra("major", major);
                rcIntent.putExtra("roll", userRoll);
                startActivity(rcIntent);
                break;
            case R.id.student_setting:
                Intent settingIntent = new Intent(StudentActivity.this, SettingActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.student_logout:
                Logout();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent msgIntent = new Intent(StudentActivity.this, MessageActivity.class);
                msgIntent.putExtra("id", userId);
                msgIntent.putExtra("profileInput", profileInput);
                startActivity(msgIntent);
            }else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void Logout() {
        Dialog dialog = new Dialog(StudentActivity.this);
        dialog.setContentView(R.layout.dialog_logout);

        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(width, height);
        dialog.show();

        TextView cancel = dialog.findViewById(R.id.logout_cancel);
        TextView logout = dialog.findViewById(R.id.logout);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                UpdateUserState("offline");
                auth.signOut();

                sharePreferenceHelper.delete(SharePreferenceHelper.KEY_LOGIN);
                sharePreferenceHelper.delete(SharePreferenceHelper.KEY_ENTRY);

                Intent intent = new Intent(StudentActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void VerifyUser(){
        userId = auth.getCurrentUser().getUid();
        studentRef
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot batchSnapshot: snapshot.getChildren()){
                            String batchKey = batchSnapshot.getKey();
                            for (DataSnapshot majorSnapshot: batchSnapshot.getChildren()){
                                if (majorSnapshot.child(userId).exists()){
                                    String nameInput = majorSnapshot.child(userId).child("name").getValue().toString();
                                    String emailInput = majorSnapshot.child(userId).child("email").getValue().toString();
                                    profileInput = majorSnapshot.child(userId).child("image").getValue().toString();
                                    userRole = majorSnapshot.child(userId).child("role").getValue().toString();
                                    userRoll = majorSnapshot.child(userId).child("roll").getValue().toString();

                                    NavigationView navigationView = findViewById(R.id.student_nav_view);
                                    View headerView = navigationView.getHeaderView(0);

                                    TextView username = headerView.findViewById(R.id.nav_profile_name);
                                    TextView userEmail = headerView.findViewById(R.id.nav_email);
                                    CircleImageView userProfile = headerView.findViewById(R.id.nav_profile_logo);

                                    username.setText(nameInput);
                                    userEmail.setText(emailInput);
                                    Glide.with(StudentActivity.this.getApplicationContext())
                                            .load(profileInput)
                                            .into(userProfile);

                                    profileName.setText(nameInput);
                                    Glide.with(StudentActivity.this.getApplicationContext())
                                            .load(profileInput)
                                            .into(profilePicture);

                                    if (userRole.equals("Student / CSE")){
                                        switch (batchKey){
                                            case "First Year Batch":
                                                major = "First Year / CSE";
                                                break;
                                            case "Second Year Batch":
                                                major = "Second Year / CSE";
                                                break;
                                            case "Third Year Batch":
                                                major = "Third Year / CSE";
                                                break;
                                            case "Forth Year Batch":
                                                major = "Forth Year / CSE";
                                                break;
                                            case "Fifth Year Batch":
                                                major = "Fifth Year / CSE";
                                                break;
                                        }
                                    }else if (userRole.equals("Student / ECE")){
                                        switch (batchKey){
                                            case "First Year Batch":
                                                major = "First Year / ECE";
                                                break;
                                            case "Second Year Batch":
                                                major = "Second Year / ECE";
                                                break;
                                            case "Third Year Batch":
                                                major = "Third Year / ECE";
                                                break;
                                            case "Forth Year Batch":
                                                major = "Forth Year / ECE";
                                                break;
                                            case "Fifth Year Batch":
                                                major = "Fifth Year / ECE";
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void UpdateUserState(String state){
        String saveCurrentTime, saveCurrentDate;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM, dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("date", saveCurrentDate);
        hashMap.put("time", saveCurrentTime);
        hashMap.put("state", state);

        userRef.child("Users").child(userId).child("UserState")
                .updateChildren(hashMap);
    }
}