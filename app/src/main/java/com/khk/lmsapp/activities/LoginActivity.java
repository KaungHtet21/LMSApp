package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.SharePreferenceHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView help, sign_up;
    Spinner entry;
    CardView email_cv, pass_cv;
    ProgressDialog loadingBar;

    FirebaseAuth auth;
    DatabaseReference userRef;

    private SharePreferenceHelper sharePreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference();

        sharePreferenceHelper = new SharePreferenceHelper(this);

        if (sharePreferenceHelper.get(SharePreferenceHelper.KEY_LOGIN, false)){
            if (sharePreferenceHelper.get(SharePreferenceHelper.IS_LOCK, false)){
                Intent intent = new Intent(LoginActivity.this, PassCodeActivity.class);
                intent.putExtra("isActivate","start");
                startActivity(intent);
            }else {
                if (sharePreferenceHelper.get(SharePreferenceHelper.KEY_ENTRY, null).equals("admin")){
                    Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                    startActivity(intent);
                    finish();
                }

                if (sharePreferenceHelper.get(SharePreferenceHelper.KEY_ENTRY, null).equals("teacher")){
                    Intent intent = new Intent(LoginActivity.this, TeacherActivity.class);
                    startActivity(intent);
                    finish();
                }

                if (sharePreferenceHelper.get(SharePreferenceHelper.KEY_ENTRY, null).equals("student")){
                    Intent intent = new Intent(LoginActivity.this, StudentActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }

        email.addTextChangedListener(loginTextWatcher);
        password.addTextChangedListener(loginTextWatcher);

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    email_cv.setForeground(getDrawable(R.drawable.bg_cardview_blue_border));
                }else {
                    email_cv.setForeground(null);
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    pass_cv.setForeground(getDrawable(R.drawable.bg_cardview_blue_border));
                }else {
                    pass_cv.setForeground(null);
                }
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowLogin();
            }
        });
    }

    private void AllowLogin() {
        String emailInput = email.getText().toString();
        String passwordInput = password.getText().toString();
        String entryInput = entry.getSelectedItem().toString();

        loadingBar.setTitle("Signing in");
        loadingBar.setMessage("Please wait");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        if (entryInput.equals("Admin")){
            auth.signInWithEmailAndPassword(emailInput, passwordInput)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String userId = auth.getCurrentUser().getUid();
                                userRef.child("Admins").child(userId)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    loadingBar.dismiss();

                                                    sharePreferenceHelper.save(SharePreferenceHelper.KEY_LOGIN, true);
                                                    sharePreferenceHelper.save(SharePreferenceHelper.KEY_ENTRY, "admin");

                                                    Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }else {
                                                    Toast.makeText(LoginActivity.this, "There is no such an account in admin panel.", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }
                    });
        }else if (entryInput.equals("Student")){
            userRef.child("Students")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot batchSnapshot: snapshot.getChildren()){
                                for (DataSnapshot majorSnapshot: batchSnapshot.getChildren()){
                                    for (DataSnapshot studentsSnapshot: majorSnapshot.getChildren()){
                                        if (studentsSnapshot.child("email").getValue().toString().equals(emailInput)){
                                            auth.signInWithEmailAndPassword(emailInput, passwordInput)
                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()){
                                                                String userId = auth.getCurrentUser().getUid();

                                                                userRef.child("Students")
                                                                        .addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                for (DataSnapshot batchSnapshot: snapshot.getChildren()){
                                                                                    for (DataSnapshot majorSnapshot: batchSnapshot.getChildren()){
                                                                                        if (majorSnapshot.child(userId).exists()){
                                                                                            loadingBar.dismiss();

                                                                                            sharePreferenceHelper.save(SharePreferenceHelper.KEY_LOGIN, true);
                                                                                            sharePreferenceHelper.save(SharePreferenceHelper.KEY_ENTRY, "student");

                                                                                            Intent intent = new Intent(LoginActivity.this, StudentActivity.class);
                                                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                            startActivity(intent);
                                                                                            finish();
                                                                                        }else {
                                                                                            loadingBar.dismiss();
                                                                                            Toast.makeText(LoginActivity.this, "There is no such an account in student panel", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                                Toast.makeText(LoginActivity.this, "There is no such an account in student panel", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            userRef.child("Students Pending")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()){
                                if (ds.child("email").getValue().toString().equals(emailInput)){
                                    String major = "";
                                    String address = ds.child("address").getValue().toString();
                                    String batch = ds.child("batch").getValue().toString();
                                    String bd = ds.child("bd").getValue().toString();
                                    String email = ds.child("email").getValue().toString();
                                    String gender = ds.child("gender").getValue().toString();
                                    String id = ds.child("id").getValue().toString();
                                    String image = ds.child("image").getValue().toString();
                                    String name = ds.child("name").getValue().toString();
                                    String password = ds.child("password").getValue().toString();
                                    String ph = ds.child("ph").getValue().toString();
                                    String role = ds.child("role").getValue().toString();
                                    String roll = ds.child("roll").getValue().toString();

                                    if (role.equals("Student / CSE")){
                                        major = "CSE";
                                    }else if (role.equals("Student / ECE")){
                                        major = "ECE";
                                    }

                                    String finalMajor = major;
                                    auth.createUserWithEmailAndPassword(emailInput, passwordInput)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()){
                                                        String userId = auth.getCurrentUser().getUid();

                                                        HashMap<String, Object> hashMap = new HashMap<>();
                                                        hashMap.put("address", address);
                                                        hashMap.put("batch", batch);
                                                        hashMap.put("bd", bd);
                                                        hashMap.put("email", email);
                                                        hashMap.put("gender", gender);
                                                        hashMap.put("id", userId);
                                                        hashMap.put("image", image);
                                                        hashMap.put("name", name);
                                                        hashMap.put("password", password);
                                                        hashMap.put("ph", ph);
                                                        hashMap.put("role", role);
                                                        hashMap.put("roll", roll);

                                                        userRef.child("Students").child(batch).child(finalMajor).child(userId).setValue(hashMap)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){
                                                                            userRef.child("Users").child(userId).setValue(hashMap)
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()){
                                                                                                userRef.child("Students Pending").child(id).removeValue();
                                                                                                loadingBar.dismiss();

                                                                                                sharePreferenceHelper.save(SharePreferenceHelper.KEY_LOGIN, true);
                                                                                                sharePreferenceHelper.save(SharePreferenceHelper.KEY_ENTRY, "student");

                                                                                                Intent intent = new Intent(LoginActivity.this, StudentActivity.class);
                                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                                startActivity(intent);
                                                                                                finish();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }else if (entryInput.equals("Teacher")){
            userRef.child("Teachers")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot teachersSnapshot: snapshot.getChildren()){
                                if (teachersSnapshot.child("email").getValue().toString().equals(emailInput)){
                                    auth.signInWithEmailAndPassword(emailInput, passwordInput)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()){
                                                        String userId = auth.getCurrentUser().getUid();

                                                        userRef.child("Teachers")
                                                                .addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (snapshot.child(userId).exists()){
                                                                            loadingBar.dismiss();

                                                                            sharePreferenceHelper.save(SharePreferenceHelper.KEY_LOGIN, true);
                                                                            sharePreferenceHelper.save(SharePreferenceHelper.KEY_ENTRY, "teacher");

                                                                            Intent intent = new Intent(LoginActivity.this, TeacherActivity.class);
                                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            userRef.child("Teachers Pending")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()){
                                if (ds.child("email").getValue().toString().equals(emailInput)){
                                    String address = ds.child("address").getValue().toString();
                                    String bd = ds.child("bd").getValue().toString();
                                    String email = ds.child("email").getValue().toString();
                                    String gender = ds.child("gender").getValue().toString();
                                    String id = ds.child("id").getValue().toString();
                                    String image = ds.child("image").getValue().toString();
                                    String name = ds.child("name").getValue().toString();
                                    String password = ds.child("password").getValue().toString();
                                    String ph = ds.child("ph").getValue().toString();
                                    String role = ds.child("role").getValue().toString();

                                    auth.createUserWithEmailAndPassword(emailInput, passwordInput)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()){
                                                        String userId = auth.getCurrentUser().getUid();

                                                        HashMap<String, Object> hashMap = new HashMap<>();
                                                        hashMap.put("address", address);
                                                        hashMap.put("bd", bd);
                                                        hashMap.put("email", email);
                                                        hashMap.put("gender", gender);
                                                        hashMap.put("id", userId);
                                                        hashMap.put("image", image);
                                                        hashMap.put("name", name);
                                                        hashMap.put("password", password);
                                                        hashMap.put("ph", ph);
                                                        hashMap.put("role", role);

                                                        userRef.child("Teachers").child(userId)
                                                                .setValue(hashMap)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){
                                                                            userRef.child("Users").child(userId)
                                                                                    .setValue(hashMap)
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()){
                                                                                                userRef.child("Teachers Pending").child(id).removeValue();
                                                                                                loadingBar.dismiss();

                                                                                                sharePreferenceHelper.save(SharePreferenceHelper.KEY_LOGIN, true);
                                                                                                sharePreferenceHelper.save(SharePreferenceHelper.KEY_ENTRY, "teacher");

                                                                                                Intent intent = new Intent(LoginActivity.this, TeacherActivity.class);
                                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                                startActivity(intent);
                                                                                                finish();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                });

                                                    }
                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private final TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String emailInput = email.getText().toString().trim();
            String passInput = password.getText().toString().trim();

            login.setEnabled(!emailInput.isEmpty() && !passInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void init() {
        email = findViewById(R.id.user_email);
        password = findViewById(R.id.user_password);
        login = findViewById(R.id.login_btn);
        help = findViewById(R.id.help_logging);
        sign_up = findViewById(R.id.sign_up);
        email_cv = findViewById(R.id.email_cv);
        pass_cv = findViewById(R.id.password_cv);
        entry = findViewById(R.id.entries_spinner);
        loadingBar = new ProgressDialog(LoginActivity.this);
    }
}