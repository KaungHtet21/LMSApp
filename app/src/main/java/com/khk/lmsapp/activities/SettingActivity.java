package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout privacy, manageAcc, security;
    private CircleImageView profile;
    private TextView username, email;
    private String userId, nameInput, phoneInput;

    FirebaseAuth auth;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Setting");

        auth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        userRef.child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            nameInput = snapshot.child("name").getValue().toString();
                            phoneInput = snapshot.child("ph").getValue().toString();
                            String imageInput = snapshot.child("image").getValue().toString();
                            String emailInput = snapshot.child("email").getValue().toString();

                            username.setText(nameInput);
                            email.setText(emailInput);
                            Glide.with(SettingActivity.this.getApplicationContext())
                                    .load(imageInput)
                                    .into(profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, AccountPrivacyActivity.class);
                startActivity(intent);
            }
        });

        manageAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ManageAccountActivity.class);
                intent.putExtra("username", nameInput);
                intent.putExtra("phone", phoneInput);
                startActivity(intent);
            }
        });

        security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SecurityActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.setting_toolbar);
        privacy = findViewById(R.id.privacy);
        manageAcc = findViewById(R.id.manage_account);
        security = findViewById(R.id.security);
        profile = findViewById(R.id.user_profile);
        username = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}