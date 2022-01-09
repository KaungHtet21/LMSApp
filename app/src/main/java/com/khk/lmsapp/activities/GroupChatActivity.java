package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.khk.lmsapp.R;
import com.khk.lmsapp.adapters.GroupMessageAdapter;
import com.khk.lmsapp.adapters.LoadingImagesAdapter;
import com.khk.lmsapp.listener.ImageSelectListener;
import com.khk.lmsapp.modules.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity implements ImageSelectListener {

    private CircleImageView profile;
    private TextView name, warnTxt;
    private ImageView back, more, send, image;
    private LinearLayout moreLayout;
    private EditText msgInput;
    private RecyclerView msgRv, imageRv;
    private String userId, groupName, saveCurrentDate, saveCurrentTime;
    private Button sendImage, editImage;
    private LinearLayout editImageLayout;
    ProgressDialog loadingBar;

    DatabaseReference groupsRef, groupMsgRef;
    FirebaseAuth auth;

    GroupMessageAdapter adapter;
    LoadingImagesAdapter imagesAdapter;

    final List<Message> msgList = new ArrayList<>();
    ArrayList<String> imageData = new ArrayList<>();
    ArrayList<String> selectedImageList = new ArrayList<>();
    ArrayList<Uri> selectedImageUriList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        init();
        groupName = getIntent().getStringExtra("group_name");

        groupsRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupName);
        groupMsgRef = FirebaseDatabase.getInstance().getReference().child("Groups Messages");
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        name.setText(groupName);
        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("image").getValue().toString();

                Glide.with(GroupChatActivity.this)
                        .load(image)
                        .into(profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moreLayout.getVisibility() == View.VISIBLE){
                    moreLayout.setVisibility(View.GONE);
                    warnTxt.setVisibility(View.GONE);
                }else {
                    moreLayout.setVisibility(View.VISIBLE);
                    warnTxt.setVisibility(View.VISIBLE);
                }
            }
        });

        msgInput.addTextChangedListener(msgInputTextWatcher);

        msgInput.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SendQuickMsg();
                return true;
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMsg();
            }
        });

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendImage();
            }
        });

        groupMsgRef.child(groupName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                msgList.add(message);
                adapter.notifyDataSetChanged();
                msgRv.smoothScrollToPosition(msgRv.getAdapter().getItemCount());
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

        initLoadingImages();
    }

    private void SendImage() {
        loadingBar.setTitle("Sending Image");
        loadingBar.setMessage("Please wait");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image messages files");

        for (int i = 0; i < selectedImageList.size(); i++){
            DatabaseReference msgKeyRef = groupMsgRef.child(groupName).push();
            String msgPushId = msgKeyRef.getKey();

            StorageReference filePath = storageReference.child(msgPushId +"."+"jpg");

            Uri imageUri = selectedImageUriList.get(i);

            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();

                            HashMap<String, Object> msgTextBody = new HashMap<>();
                            msgTextBody.put("message", downloadUrl);
                            msgTextBody.put("type", "image");
                            msgTextBody.put("from", userId);
                            msgTextBody.put("to", groupName);
                            msgTextBody.put("messageId", msgPushId);
                            msgTextBody.put("date", saveCurrentDate);
                            msgTextBody.put("time", saveCurrentTime);
                            msgTextBody.put("name", imageUri.getLastPathSegment());

                            msgKeyRef.updateChildren(msgTextBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    loadingBar.dismiss();
                                    Toast.makeText(GroupChatActivity.this, "Sent Image", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(GroupChatActivity.this, "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GroupChatActivity.this, "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void SendMsg() {
        DatabaseReference msgKeyRef = groupMsgRef.child(groupName).push();
        String msgPushId = msgKeyRef.getKey();

        HashMap<String, Object> msgTextBody = new HashMap<>();
        msgTextBody.put("message", msgInput.getText().toString());
        msgTextBody.put("type", "text");
        msgTextBody.put("from", userId);
        msgTextBody.put("msgId", msgPushId);
        msgTextBody.put("to", groupName);
        msgTextBody.put("date", saveCurrentDate);
        msgTextBody.put("time", saveCurrentTime);

        msgKeyRef.updateChildren(msgTextBody).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(GroupChatActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(GroupChatActivity.this, "Error: "+ task.getException(), Toast.LENGTH_SHORT).show();
                }
                msgInput.setText("");
            }
        });
    }

    private final TextWatcher msgInputTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String msg = msgInput.getText().toString().trim();

            if (!msg.isEmpty()){
                send.setColorFilter(GroupChatActivity.this.getResources().getColor(R.color.white));
                send.setEnabled(true);
            }else {
                send.setColorFilter(GroupChatActivity.this.getResources().getColor(R.color.grey));
                send.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void SendQuickMsg() {
        Dialog dialog = new Dialog(GroupChatActivity.this);
        dialog.setContentView(R.layout.dialog_quick_msg);

        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.bg_message_selection));
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        TextView quickMsg1, quickMsg2, quickMsg3;

        quickMsg1 = dialog.findViewById(R.id.quick_msg_1);
        quickMsg2 = dialog.findViewById(R.id.quick_msg_2);
        quickMsg3 = dialog.findViewById(R.id.quick_msg_3);

        quickMsg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgInput.setText(quickMsg1.getText().toString());
                dialog.dismiss();
            }
        });

        quickMsg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgInput.setText(quickMsg2.getText().toString());
                dialog.dismiss();
            }
        });

        quickMsg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgInput.setText(quickMsg3.getText().toString());
                dialog.dismiss();
            }
        });
    }

    private void initLoadingImages() {
        LinearLayout layoutLoadingImages = findViewById(R.id.layout_loading_images);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutLoadingImages);

        imageRv = findViewById(R.id.loading_images_rv);
        imageRv.setHasFixedSize(true);
        imageRv.setLayoutManager(new GridLayoutManager(GroupChatActivity.this, 3));

        MyTask myTask = new MyTask();
        myTask.execute();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        layoutLoadingImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    private ArrayList<String> getAllShownImagesPath(Activity activity){
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null, null, null);

        cursor.moveToFirst();
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        while (cursor.moveToNext()){
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }

        return listOfAllImages;
    }

    @Override
    public void onImageSelect(ArrayList<String> selectedImagesList, ArrayList<Uri> selectedImageUriList) {
        this.selectedImageList = selectedImagesList;
        this.selectedImageUriList = selectedImageUriList;

        sendImage.setText("SEND("+selectedImageList.size()+")");
        if (selectedImageList.size() == 1) {
            editImageLayout.setVisibility(View.VISIBLE);
            editImage.setEnabled(true);
        }else if (selectedImageList.size() > 1){
            editImage.setEnabled(false);
        }else {
            editImageLayout.setVisibility(View.GONE);
        }
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            imageData = getAllShownImagesPath(GroupChatActivity.this);
            Log.e("imageData", String.valueOf(imageData.size()) );
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imagesAdapter = new LoadingImagesAdapter(imageData, GroupChatActivity.this, GroupChatActivity.this::onImageSelect);
            imageRv.setAdapter(imagesAdapter);
        }
    }

    private void init() {
        profile = findViewById(R.id.group_profile);
        name = findViewById(R.id.group_name);
        back = findViewById(R.id.back);
        more = findViewById(R.id.more);
        moreLayout = findViewById(R.id.more_icons_layout);
        warnTxt = findViewById(R.id.warn_txt);
        msgInput = findViewById(R.id.msg_input);
        send = findViewById(R.id.send);
        image = findViewById(R.id.image);
        editImageLayout = findViewById(R.id.image_edit_layout);
        sendImage = editImageLayout.findViewById(R.id.image_send);
        editImage = findViewById(R.id.image_edit);

        loadingBar = new ProgressDialog(GroupChatActivity.this);

        msgRv = findViewById(R.id.message_rv);
        adapter = new GroupMessageAdapter(msgList, GroupChatActivity.this);
        msgRv.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
        msgRv.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM, dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        send.setEnabled(false);
    }
}