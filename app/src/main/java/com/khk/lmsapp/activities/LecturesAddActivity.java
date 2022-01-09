package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.khk.lmsapp.R;
import com.khk.lmsapp.adapters.LecturesAdapter;
import com.khk.lmsapp.modules.Lectures;

import java.util.ArrayList;
import java.util.HashMap;

public class LecturesAddActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rv;
    FloatingActionButton fab;
    TextView tv;
    TextView file_name;
    EditText save_content, save_as;

    String content, file, major, course, entry;
    Uri pdfUri;
    ProgressDialog loadingBar;
    Dialog dialog;

    DatabaseReference fileRef, fileKeyRef;
    LecturesAdapter adapter;
    ArrayList<Lectures> lecturesList = new ArrayList<>();
    private static final String TAG = "LecturesAddActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectures_add);

        toolbar = findViewById(R.id.lectures_add_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Lectures");

        major = getIntent().getStringExtra("Major");
        course = getIntent().getStringExtra("Course");
        entry = getIntent().getStringExtra("Entry");

        rv = findViewById(R.id.lectures_rv);
        fab = findViewById(R.id.lectures_add_fab);
        tv = findViewById(R.id.default_tv);

        loadingBar = new ProgressDialog(this);
        dialog  = new Dialog(this);

        fileRef = FirebaseDatabase.getInstance().getReference().child("Lectures").child(major).child(course);

        if (entry.equals("teacher_course")){
            fab.setVisibility(View.VISIBLE);
        }else {
            fab.setVisibility(View.GONE);
        }

        fileRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            tv.setVisibility(View.GONE);
                        }else {
                            tv.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        fileRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()){
                            String contentKey = snapshot.getKey();
                            lecturesList.add(new Lectures(contentKey));

                            adapter = new LecturesAdapter(lecturesList, major, course, entry);
                            adapter.setLectures(lecturesList);
                            adapter.notifyDataSetChanged();

                            rv.setLayoutManager(new LinearLayoutManager(LecturesAddActivity.this));
                            rv.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.dialog_add_lectures);

                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setLayout(width, height);
                dialog.show();

                Button choose, save;
                TextView cancel;

                choose = dialog.findViewById(R.id.choose_file);
                save = dialog.findViewById(R.id.save_file_changes);
                file_name = dialog.findViewById(R.id.file_name_txt);
                cancel = dialog.findViewById(R.id.save_file_cancel);
                save_content = dialog.findViewById(R.id.save_content);
                save_as = dialog.findViewById(R.id.save_file_as);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                choose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(LecturesAddActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                            selectPdf();
                        }else {
                            ActivityCompat.requestPermissions(LecturesAddActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                        }
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (validate()){
                            if (pdfUri != null){
                                uploadPdf(pdfUri);
                                dialog.dismiss();
                            }else {
                                Toast.makeText(LecturesAddActivity.this, "Please select a file", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    private void uploadPdf(Uri pdfUri) {
        loadingBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        loadingBar.setTitle("Uploading file...");
        loadingBar.setProgress(0);
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.setCancelable(false);
        loadingBar.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference filePath = storageReference.child("Lectures").child(content).child(file);
        filePath.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String getFileId = fileRef.child(content).push().getKey();

                                HashMap<String, Object> fileIdKey = new HashMap<>();
                                fileRef.child(content).updateChildren(fileIdKey);

                                fileKeyRef = fileRef.child(content).child(getFileId);

                                String url = uri.toString();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("content", content);
                                hashMap.put("saveAs", file);
                                hashMap.put("file", url);
                                hashMap.put("id", getFileId);
                                fileKeyRef.updateChildren(hashMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(LecturesAddActivity.this, "File is uploaded successfully", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(LecturesAddActivity.this, "Something is wrong. Please try again", Toast.LENGTH_SHORT).show();
                                                }
                                                loadingBar.dismiss();
                                            }
                                        });
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                int currentProgress = (int) (100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                loadingBar.setProgress(currentProgress);
            }
        });
    }

    private boolean validate() {
        content = save_content.getText().toString();
        file = save_as.getText().toString();

        if (TextUtils.isEmpty(content)){
            Toast.makeText(this, "Please enter content", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(file)){
            Toast.makeText(this, "Please enter save as", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void selectPdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 89);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectPdf();
        }else {
            Toast.makeText(this, "Please provide permission", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 89 && resultCode == RESULT_OK && data != null){
            pdfUri = data.getData();
            file_name.setText(data.getData().getLastPathSegment());
        }else {
            Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show();
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