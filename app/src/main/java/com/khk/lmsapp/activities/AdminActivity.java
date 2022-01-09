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
import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
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

public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton menu_fab;
    DrawerLayout drawer;
    RecyclerView dashboardRV;
    CircleImageView profilePicture;
    TextView profileName, date;
    CalendarView calendar;
    String profileInput, userId;

    FirebaseAuth auth;
    DatabaseReference adminRef, userRef;

    private SharePreferenceHelper sharePreferenceHelper;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final String TAG = "AdminActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dashboardRV = findViewById(R.id.main_dashboard_list);
        profileName = findViewById(R.id.main_username);
        profilePicture = findViewById(R.id.main_profile_picture);
        date = findViewById(R.id.main_date);
        calendar = findViewById(R.id.calendar_view);

        auth = FirebaseAuth.getInstance();
        adminRef = FirebaseDatabase.getInstance().getReference().child("Admins");
        userRef = FirebaseDatabase.getInstance().getReference();

        sharePreferenceHelper = new SharePreferenceHelper(this);

        drawer = findViewById(R.id.admin_drawer_layout);
        NavigationView navigationView = findViewById(R.id.admin_nav_view);
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
        items.add(new Dashboard("Student", R.drawable.student_logo));
        items.add(new Dashboard("Teacher", R.drawable.teacher_logo));
        items.add(new Dashboard("Message", R.drawable.message_logo));
        items.add(new Dashboard("Courses", R.drawable.courses_logo));
        items.add(new Dashboard("Setting", R.drawable.ic_setting));

        AdminDashboardAdapter adapter = new AdminDashboardAdapter(this);
        adapter.setItems(items);

        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        dashboardRV.setAdapter(adapter);
        dashboardRV.setLayoutManager(llm);

        VerifyUserExistence();
    }

    @Override
    protected void onStart() {
        super.onStart();
        UpdateUserState("online");
        VerifyUserExistence();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (userId != null){
            UpdateUserState("online");
        }
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userId != null){
            UpdateUserState("offline");
        }
        Log.d(TAG, "onDestroy: ");
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
        item.setChecked(true);
        item.setCheckable(true);

        switch (item.getItemId()){
            case R.id.admin_dashboard:
                break;
            case R.id.admin_message:
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            AdminActivity.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                }else {
                    Intent msgIntent = new Intent(AdminActivity.this, MessageActivity.class);
                    msgIntent.putExtra("id", userId);
                    msgIntent.putExtra("profileInput", profileInput);
                    startActivity(msgIntent);
                }
                break;
            case R.id.admin_courses:
                Intent courseIntent = new Intent(AdminActivity.this, MajorListActivity.class);
                courseIntent.putExtra("entry", "admin_course");
                startActivity(courseIntent);
                break;
            case R.id.admin_note:
                Intent noteIntent = new Intent(AdminActivity.this, NoteActivity.class);
                startActivity(noteIntent);
                break;
            case R.id.admin_bookmark:
                Intent bookmarkIntent = new Intent(AdminActivity.this, BookmarkActivity.class);
                startActivity(bookmarkIntent);
                break;
            case R.id.admin_st_list:
                Intent stListIntent = new Intent(AdminActivity.this, StudentListActivity.class);
                startActivity(stListIntent);
                break;
            case R.id.admin_st_registers:
                Intent stRegisterIntent = new Intent(AdminActivity.this, StudentRegisterActivity.class);
                startActivity(stRegisterIntent);
                break;
            case R.id.admin_st_attendance:
                Intent stAttIntent = new Intent(AdminActivity.this, MajorListActivity.class);
                stAttIntent.putExtra("entry", "admin_st_att");
                startActivity(stAttIntent);
                break;
            case R.id.admin_te_list:
                Intent teListIntent = new Intent(AdminActivity.this, TeacherListActivity.class);
                teListIntent.putExtra("entry", "teacher_list");
                startActivity(teListIntent);
                break;
            case R.id.admin_te_registers:
                Intent teRegisterIntent = new Intent(AdminActivity.this, TeacherRegisterActivity.class);
                startActivity(teRegisterIntent);
                break;
            case R.id.admin_setting:
                Intent settingIntent = new Intent(AdminActivity.this, SettingActivity.class);
                startActivity(settingIntent);
                break;
            case  R.id.admin_logout:
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
                Intent msgIntent = new Intent(AdminActivity.this, MessageActivity.class);
                msgIntent.putExtra("id", userId);
                msgIntent.putExtra("profileInput", profileInput);
                startActivity(msgIntent);
            }else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void Logout() {
        Dialog dialog = new Dialog(AdminActivity.this);
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

                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void VerifyUserExistence(){
        userId = auth.getCurrentUser().getUid();
        adminRef.child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String nameInput = snapshot.child("name").getValue().toString();
                            String emailInput = snapshot.child("email").getValue().toString();
                            profileInput = snapshot.child("image").getValue().toString();

                            NavigationView navigationView = findViewById(R.id.admin_nav_view);
                            View headerView = navigationView.getHeaderView(0);

                            TextView username = headerView.findViewById(R.id.nav_profile_name);
                            TextView userEmail = headerView.findViewById(R.id.nav_email);
                            CircleImageView userProfile = headerView.findViewById(R.id.nav_profile_logo);

                            username.setText(nameInput);
                            userEmail.setText(emailInput);
                            Glide.with(AdminActivity.this)
                                    .load(profileInput)
                                    .into(userProfile);

                            profileName.setText(nameInput);
                            Glide.with(AdminActivity.this)
                                    .load(profileInput)
                                    .into(profilePicture);
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