package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.khk.lmsapp.R;
import com.khk.lmsapp.adapters.MembersAdapter;
import com.khk.lmsapp.modules.Members;
import com.khk.lmsapp.modules.Message;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetNameGroupActivity extends AppCompatActivity {

    private CircleImageView admin_profile, group_profile;
    private TextView admin_name;
    private RecyclerView members_rv;
    private EditText group_name;
    private ConstraintLayout pick_image;
    private static final int PICK_IMAGE = 1;
    private Uri imageUri = null;
    private String email, id, image, name, adminRole, groupNameInput;

    DatabaseReference tempRef, groupsRef;
    StorageReference profileRef;

    MembersAdapter adapter;
    ArrayList<Members> membersList = new ArrayList<>();

    private static final String TAG = "SetNameGroupActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name_group);

        Toolbar toolbar = findViewById(R.id.new_group_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("New Group");

        admin_profile = findViewById(R.id.admin_profile);
        admin_name = findViewById(R.id.admin_name);
        members_rv = findViewById(R.id.members_rv);
        group_profile = findViewById(R.id.group_image);
        group_name = findViewById(R.id.group_name_input);
        pick_image = findViewById(R.id.pick_image);
        FloatingActionButton fab = findViewById(R.id.done);

        String tempKey = getIntent().getStringExtra("tempKey");
        tempRef = FirebaseDatabase.getInstance().getReference().child("Temp");
        groupsRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        profileRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        tempRef.child(tempKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot membersSnapshot: snapshot.getChildren()){
                            Log.d(TAG, "onDataChange: " +membersSnapshot);
                            String role = membersSnapshot.child("role").getValue().toString();
                            if (role.equals("admin")){
                                email = membersSnapshot.child("email").getValue().toString();
                                id = membersSnapshot.child("id").getValue().toString();
                                image = membersSnapshot.child("image").getValue().toString();
                                name = membersSnapshot.child("name").getValue().toString();
                                adminRole = membersSnapshot.child("role").getValue().toString();

                                admin_name.setText(name);
                                Glide.with(SetNameGroupActivity.this.getApplicationContext())
                                        .load(image)
                                        .into(admin_profile);
                            }else {
                                Members members = membersSnapshot.getValue(Members.class);
                                membersList.add(members);

                                adapter = new MembersAdapter(membersList);
                                adapter.add(membersList);

                                members_rv.setLayoutManager(new LinearLayoutManager(SetNameGroupActivity.this));
                                members_rv.setAdapter(adapter);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        pick_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    StorageReference filePath = profileRef.child(System.currentTimeMillis() + ".jpg");

                    HashMap<String, Object> admin = new HashMap<>();
                    admin.put("email", email);
                    admin.put("id", id);
                    admin.put("image", image);
                    admin.put("name", name);
                    admin.put("role", adminRole);
                    groupsRef.child(groupNameInput).child(id).updateChildren(admin)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String downloadUrl = uri.toString();
                                                        groupsRef.child(groupNameInput).child("image").setValue(downloadUrl)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        tempRef.child(tempKey).removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        Intent intent = new Intent(SetNameGroupActivity.this, MessageActivity.class);
                                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                        startActivity(intent);
                                                                                        Toast.makeText(SetNameGroupActivity.this, "Created Group", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });
                                                                    }
                                                                });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });

                    for (int i = 0; i < membersList.size(); i++){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("email", membersList.get(i).getEmail());
                        hashMap.put("id", membersList.get(i).getId());
                        hashMap.put("image", membersList.get(i).getImage());
                        hashMap.put("name", membersList.get(i).getName());
                        hashMap.put("role", membersList.get(i).getRole());

                        groupsRef.child(groupNameInput).child(membersList.get(i).getId()).updateChildren(hashMap);
                    }
                }
            }
        });
    }

    private boolean validate() {
        groupNameInput = group_name.getText().toString();

        if (TextUtils.isEmpty(group_name.getText().toString())){
            Animation shake = AnimationUtils.loadAnimation(SetNameGroupActivity.this, R.anim.shake);
            group_name.startAnimation(shake);

            Vibrator v = (Vibrator) getSystemService(SetNameGroupActivity.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
            }else {
                v.vibrate(300);
            }
            return false;
        }

        if (imageUri == null){
            Animation shake = AnimationUtils.loadAnimation(SetNameGroupActivity.this, R.anim.shake);
            pick_image.startAnimation(shake);

            Vibrator v = (Vibrator) getSystemService(SetNameGroupActivity.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
            }else {
                v.vibrate(300);
            }
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            pick_image.setVisibility(View.GONE);
            group_profile.setVisibility(View.VISIBLE);
            Glide.with(SetNameGroupActivity.this)
                    .load(imageUri)
                    .into(group_profile);
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