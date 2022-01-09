package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity {

    CircleImageView image;
    TextView name, role, username, phone, email, address;
    ImageView back;
    FloatingActionButton message, att;
    String idInput, nameInput, roleInput, phoneInput, emailInput, addressInput, imageInput, rollInput, majorInput;
    private static final String TAG = "UserInfoActivity";
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        init();
        idInput = getIntent().getStringExtra("id");
        nameInput = getIntent().getStringExtra("name");
        roleInput = getIntent().getStringExtra("role");
        phoneInput = getIntent().getStringExtra("phone");
        emailInput = getIntent().getStringExtra("email");
        addressInput = getIntent().getStringExtra("address");
        imageInput = getIntent().getStringExtra("image");
        rollInput = getIntent().getStringExtra("roll");
        majorInput = getIntent().getStringExtra("major");

        name.setText(nameInput);
        role.setText(roleInput);
        username.setText(nameInput);
        phone.setText(phoneInput);
        email.setText(emailInput);
        address.setText(addressInput);
        Glide.with(this)
                .load(imageInput)
                .into(image);

        if (roleInput.equals("Student / CSE") || roleInput.equals("Student / ECE")){
            att.setVisibility(View.VISIBLE);
        }else {
            att.setVisibility(View.GONE);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        att.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, AddCourseActivity.class);
                intent.putExtra("major", majorInput);
                intent.putExtra("entry", "userInfo");
                intent.putExtra("roll", rollInput);
                startActivity(intent);
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            UserInfoActivity.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                }else {
                    Intent intent = new Intent(UserInfoActivity.this, ChatActivity.class);
                    intent.putExtra("id", idInput);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(UserInfoActivity.this, ChatActivity.class);
                intent.putExtra("id", idInput);
                startActivity(intent);
            }else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void init() {
        image = findViewById(R.id.user_image);
        name = findViewById(R.id.user_name);
        role = findViewById(R.id.user_role);
        message = findViewById(R.id.msg_fab);
        att = findViewById(R.id.att_fab);
        username = findViewById(R.id.name);
        phone = findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        back = findViewById(R.id.back);
    }
}