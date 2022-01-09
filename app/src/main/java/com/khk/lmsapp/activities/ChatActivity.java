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
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.khk.lmsapp.R;
import com.khk.lmsapp.adapters.LoadingImagesAdapter;
import com.khk.lmsapp.adapters.MessageAdapter;
import com.khk.lmsapp.listener.ImageSelectListener;
import com.khk.lmsapp.modules.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements ImageSelectListener {

    private TextView username, warnTxt, userOnline;
    private CircleImageView profile;
    private ImageView more, send, image, back;
    private EditText msgInput;
    private LinearLayout moreLayout;
    private String msgSenderId, msgReceiverId, saveCurrentDate, saveCurrentTime;
    private LinearLayout editImageLayout;
    private Button sendImage, editImage;
    ProgressDialog loadingBar;

    FirebaseAuth auth;
    DatabaseReference msgRef, userRef, chatsRef;

    private RecyclerView msgRv, imageRv;
    final List<Message> msgList = new ArrayList<>();
    MessageAdapter adapter;
    LoadingImagesAdapter imagesAdapter;

    ArrayList<String> imageData = new ArrayList<>();
    ArrayList<String> selectedImageList = new ArrayList<>();
    ArrayList<Uri> selectedImageUriList = new ArrayList<>();

    String senderName, receiverName;

    private static final String TAG = "ChatActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();

        auth = FirebaseAuth.getInstance();
        msgSenderId = auth.getCurrentUser().getUid();
        msgReceiverId = getIntent().getStringExtra("id");

        msgRef = FirebaseDatabase.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference();
        chatsRef = FirebaseDatabase.getInstance().getReference().child("Chats");

        userRef.child("Users").child(msgReceiverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            receiverName = snapshot.child("name").getValue().toString();
                            String imageInput = snapshot.child("image").getValue().toString();

                            username.setText(receiverName);
                            Glide.with(ChatActivity.this.getApplicationContext())
                                    .load(imageInput)
                                    .into(profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        userRef.child("Users").child(msgSenderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            senderName = snapshot.child("name").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        DisplayLastSeen();

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

        msgInput.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SendQuickMsg();
                return true;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        msgRef.child("Messages")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.child("to").getValue().toString().equals(msgReceiverId) && snapshot.child("from").getValue().toString().equals(msgSenderId)
                        || snapshot.child("to").getValue().toString().equals(msgSenderId) && snapshot.child("from").getValue().toString().equals(msgReceiverId)){
                            Message message = snapshot.getValue(Message.class);
                            msgList.add(message);
                            adapter.notifyDataSetChanged();
                            msgRv.smoothScrollToPosition(msgRv.getAdapter().getItemCount());
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
        initLoadingImages();
    }

    private void SendQuickMsg() {
        Dialog dialog = new Dialog(ChatActivity.this);
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

    private void SendImage() {

        loadingBar.setTitle("Sending Image");
        loadingBar.setMessage("Please wait");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image messages files");

        for (int i = 0; i < selectedImageList.size(); i++){
            DatabaseReference userMsgKeyRef = msgRef.child("Messages").push();

            final String msgPushId = userMsgKeyRef.getKey();
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
                            msgTextBody.put("from", msgSenderId);
                            msgTextBody.put("to", msgReceiverId);
                            msgTextBody.put("messageId", msgPushId);
                            msgTextBody.put("date", saveCurrentDate);
                            msgTextBody.put("time", saveCurrentTime);
                            msgTextBody.put("name", imageUri.getLastPathSegment());

                            userMsgKeyRef.updateChildren(msgTextBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    loadingBar.dismiss();
                                    HashMap<String, Object> senderHashMap = new HashMap<>();
                                    senderHashMap.put("state", "friend");
                                    senderHashMap.put("type", "image");
                                    senderHashMap.put("message", downloadUrl);
                                    senderHashMap.put("name", receiverName);
                                    senderHashMap.put("id", msgReceiverId);
                                    chatsRef.child(msgSenderId).child(msgReceiverId).setValue(senderHashMap);

                                    HashMap<String, Object> receiverHashMap = new HashMap<>();
                                    receiverHashMap.put("state", "friend");
                                    receiverHashMap.put("type", "image");
                                    receiverHashMap.put("message", downloadUrl);
                                    receiverHashMap.put("name", senderName);
                                    receiverHashMap.put("id", msgSenderId);
                                    chatsRef.child(msgReceiverId).child(msgSenderId).setValue(receiverHashMap);
                                    Toast.makeText(ChatActivity.this, "Sent Image", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void initLoadingImages() {
        LinearLayout layoutLoadingImages = findViewById(R.id.layout_loading_images);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutLoadingImages);

        imageRv = findViewById(R.id.loading_images_rv);
        imageRv.setHasFixedSize(true);
        imageRv.setLayoutManager(new GridLayoutManager(ChatActivity.this, 3));

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

    private void SendMsg() {
        Log.d(TAG, "SendMsg: "+ System.currentTimeMillis());
        DatabaseReference userMsgKeyRef = msgRef.child("Messages").push();

        String msgPushId = userMsgKeyRef.getKey();

        HashMap<String, Object> msgTextBody = new HashMap<>();
        msgTextBody.put("message", msgInput.getText().toString());
        msgTextBody.put("type", "text");
        msgTextBody.put("from", msgSenderId);
        msgTextBody.put("to", msgReceiverId);
        msgTextBody.put("msgId", msgPushId);
        msgTextBody.put("date", saveCurrentDate);
        msgTextBody.put("time", saveCurrentTime);

        userMsgKeyRef.updateChildren(msgTextBody).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    HashMap<String, Object> senderHashMap = new HashMap<>();
                    senderHashMap.put("state", "friend");
                    senderHashMap.put("type", "text");
                    senderHashMap.put("message", msgInput.getText().toString());
                    senderHashMap.put("name", receiverName);
                    senderHashMap.put("id", msgReceiverId);
                    chatsRef.child(msgSenderId).child(msgReceiverId).setValue(senderHashMap);

                    HashMap<String, Object> receiverHashMap = new HashMap<>();
                    receiverHashMap.put("state", "friend");
                    receiverHashMap.put("type", "text");
                    receiverHashMap.put("message", msgInput.getText().toString());
                    receiverHashMap.put("name", senderName);
                    receiverHashMap.put("id", msgSenderId);
                    chatsRef.child(msgReceiverId).child(msgSenderId).setValue(receiverHashMap);
                    Toast.makeText(ChatActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ChatActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                }

                msgInput.setText("");
            }
        });
    }

    private void DisplayLastSeen(){
        userRef.child("Users").child(msgReceiverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("UserState").hasChild("state")){
                            String state = snapshot.child("UserState").child("state").getValue().toString();
                            String date = snapshot.child("UserState").child("date").getValue().toString();
                            String time = snapshot.child("UserState").child("time").getValue().toString();

                            if (state.equals("online")){
                                userOnline.setText("Online");
                            }else if (state.equals("offline")){
                                userOnline.setText("Last seen:\n"+ date +" "+ time);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
    public void onImageSelect(ArrayList<String> selectedImageList, ArrayList<Uri> selectedImageUriList) {
        this.selectedImageList = selectedImageList;
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

    private class MyTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            imageData = getAllShownImagesPath(ChatActivity.this);
            Log.e("imageData", String.valueOf(imageData.size()) );
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imagesAdapter = new LoadingImagesAdapter(imageData, ChatActivity.this, ChatActivity.this::onImageSelect);
            imageRv.setAdapter(imagesAdapter);
        }
    }

    private final TextWatcher msgInputTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String msg = msgInput.getText().toString().trim();

            if (!msg.isEmpty()){
                send.setColorFilter(ChatActivity.this.getResources().getColor(R.color.white));
                send.setEnabled(true);
            }else {
                send.setColorFilter(ChatActivity.this.getResources().getColor(R.color.grey));
                send.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void init() {
        more = findViewById(R.id.more);
        moreLayout = findViewById(R.id.more_icons_layout);
        username = findViewById(R.id.user_name);
        profile = findViewById(R.id.user_profile);
        send = findViewById(R.id.send);
        msgInput = findViewById(R.id.msg_input);
        warnTxt = findViewById(R.id.warn_txt);
        image = findViewById(R.id.image);
        editImageLayout = findViewById(R.id.image_edit_layout);
        sendImage = editImageLayout.findViewById(R.id.image_send);
        editImage = findViewById(R.id.image_edit);
        back = findViewById(R.id.back);
        userOnline = findViewById(R.id.last_online);
        loadingBar = new ProgressDialog(this);

        msgRv = findViewById(R.id.message_rv);
        adapter = new MessageAdapter(msgList, ChatActivity.this);
        msgRv.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        msgRv.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM, dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        send.setEnabled(false);
    }
}