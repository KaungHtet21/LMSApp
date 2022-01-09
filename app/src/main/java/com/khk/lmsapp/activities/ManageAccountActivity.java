package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.SharePreferenceHelper;

import java.util.HashMap;

public class ManageAccountActivity extends AppCompatActivity {

    TextView username, phone;
    LinearLayout usernameLayout, phoneLayout;
    ImageView back;
    String nameInput, phoneInput;

    FirebaseAuth auth;
    DatabaseReference userRef;
    private SharePreferenceHelper sharePreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        back = findViewById(R.id.back);
        username = findViewById(R.id.user_name);
        phone = findViewById(R.id.user_phone);
        usernameLayout = findViewById(R.id.username_setting);
        phoneLayout = findViewById(R.id.phone_setting);
        nameInput = getIntent().getStringExtra("username");
        phoneInput = getIntent().getStringExtra("phone");

        sharePreferenceHelper = new SharePreferenceHelper(this);
        String entry = sharePreferenceHelper.get(SharePreferenceHelper.KEY_ENTRY, null);
        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        username.setText(nameInput);
        phone.setText(phoneInput);

        usernameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ManageAccountActivity.this);
                dialog.setContentView(R.layout.dialog_register);

                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width, height);
                dialog.show();

                TextView txt = dialog.findViewById(R.id.txt);
                EditText input = dialog.findViewById(R.id.admin_code_edt);
                TextView cancel = dialog.findViewById(R.id.cancel_admin_code);
                Button change = dialog.findViewById(R.id.insert_admin_code);

                txt.setText("Enter name");
                input.setHint("");
                change.setText("Change");

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nameInput = input.getText().toString();
                        if (TextUtils.isEmpty(nameInput)){
                            Toast.makeText(ManageAccountActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                        }else {
                            if (entry.equals("admin")){
                                userRef.child("Admins").child(userId)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    userRef.child("Admins").child(userId).child("name").setValue(nameInput)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        userRef.child("Users").child(userId).child("name").setValue(nameInput)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()){
                                                                                            dialog.dismiss();
                                                                                            username.setText(nameInput);
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }else if (entry.equals("teacher")){
                                userRef.child("Teachers").child(userId)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    userRef.child("Teachers").child(userId).child("name").setValue(nameInput)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        userRef.child("Users").child(userId).child("name").setValue(nameInput)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()){
                                                                                            dialog.dismiss();
                                                                                            username.setText(nameInput);
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }else if (entry.equals("student")){
                                userRef.child("Students")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot batchSnapshot: snapshot.getChildren()){
                                                    for (DataSnapshot majorSnapshot: batchSnapshot.getChildren()){
                                                        if (majorSnapshot.child(userId).exists()){
                                                            String batchKey = batchSnapshot.getKey();
                                                            String majorKey = majorSnapshot.getKey();
                                                            userRef.child("Students").child(batchKey).child(majorKey).child(userId).child("name").setValue(nameInput)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){
                                                                                userRef.child("Users").child(userId).child("name").setValue(nameInput)
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()){
                                                                                                    dialog.dismiss();
                                                                                                    username.setText(nameInput);
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }
                    }
                });
            }
        });
    }
}