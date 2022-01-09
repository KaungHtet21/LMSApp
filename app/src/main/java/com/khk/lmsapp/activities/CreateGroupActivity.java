package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.adapters.CreateGroupFriendsContainerAdapter;
import com.khk.lmsapp.adapters.SelectedFriendsAdapter;
import com.khk.lmsapp.listener.FriendSelectListener;
import com.khk.lmsapp.modules.Teachers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CreateGroupActivity extends AppCompatActivity implements FriendSelectListener {

    private EditText inputSearch;
    private TextView membersCount;
    private RecyclerView friendsRv, membersRv;
    private FloatingActionButton forward;
    private String pushId, userId, userName, userImage, userEmail;

    DatabaseReference userRef, tempRef;
    FirebaseAuth auth;

    List<Teachers> friendsList = new ArrayList<>();
    List<Teachers> selectedFriendsList = new ArrayList<>();

    CreateGroupFriendsContainerAdapter adapter;
    SelectedFriendsAdapter selectedFriendsAdapter = new SelectedFriendsAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Toolbar toolbar = findViewById(R.id.create_group_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Create Group");

        forward = findViewById(R.id.forward_fab);
        forward.hide();
        membersCount = findViewById(R.id.members_count);
        friendsRv = findViewById(R.id.friends_rv);
        membersRv = findViewById(R.id.members_rv);

        tempRef = FirebaseDatabase.getInstance().getReference().child("Temp").push();
        pushId = tempRef.getKey();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        auth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userName = snapshot.child("name").getValue().toString();
                    userImage = snapshot.child("image").getValue().toString();
                    userEmail = snapshot.child("email").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userRef.orderByChild("name").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (!snapshot.getKey().equals(userId)){
                    Teachers users = snapshot.getValue(Teachers.class);
                    friendsList.add(users);

                    adapter = new CreateGroupFriendsContainerAdapter(friendsList, CreateGroupActivity.this);
                    adapter.add(friendsList);

                    friendsRv.setLayoutManager(new LinearLayoutManager(CreateGroupActivity.this));
                    friendsRv.setAdapter(adapter);
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

        membersRv.setLayoutManager(new LinearLayoutManager(CreateGroupActivity.this, LinearLayoutManager.HORIZONTAL, false));
        membersRv.setAdapter(selectedFriendsAdapter);

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", userId);
                hashMap.put("name", userName);
                hashMap.put("image", userImage);
                hashMap.put("email", userEmail);
                hashMap.put("role", "admin");

                tempRef.child(userId).updateChildren(hashMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Intent intent = new Intent(CreateGroupActivity.this, SetNameGroupActivity.class);
                                    intent.putExtra("tempKey", pushId);
                                    startActivity(intent);
                                }
                            }
                        });
            }
        });

        inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String isContain = s.toString();
                if (isContain.length() == 0){
                    friendsRv.scrollToPosition(0);
                }else {
                    for (int i = 0;i < friendsList.size(); i++){
                        if (friendsList.get(i).getName().toLowerCase().equals(isContain)){
                            friendsRv.scrollToPosition(i);
                        }else if (friendsList.get(i).getName().toLowerCase().contains(isContain)){
                            friendsRv.scrollToPosition(i);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onFriendSelectAdd(Teachers selectedFriend) {
        selectedFriendsList.add(selectedFriend);

        selectedFriendsAdapter.add(selectedFriend);
        membersRv.smoothScrollToPosition(membersRv.getAdapter().getItemCount());

        if (selectedFriendsList.size() > 0){
            membersRv.setVisibility(View.VISIBLE);
        }else {
            membersRv.setVisibility(View.GONE);
        }

        if (selectedFriendsList.size() > 1){
            forward.show();
        }else {
            forward.hide();
        }

        membersCount.setText(selectedFriendsList.size() + " members");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", selectedFriend.getId());
        hashMap.put("name", selectedFriend.getName());
        hashMap.put("image", selectedFriend.getImage());
        hashMap.put("email", selectedFriend.getEmail());
        hashMap.put("role", "member");

        tempRef.child(selectedFriend.getId()).updateChildren(hashMap);
    }

    @Override
    public void onFriendSelectRemove(Teachers selectedFriend) {
        selectedFriendsList.remove(selectedFriend);

        selectedFriendsAdapter.remove(selectedFriend);

        if (selectedFriendsList.size() > 0){
            membersRv.setVisibility(View.VISIBLE);
        }else {
            membersRv.setVisibility(View.GONE);
        }

        if (selectedFriendsList.size() > 1){
            forward.show();
        }else {
            forward.hide();
        }

        membersCount.setText(selectedFriendsList.size() + " members");

        tempRef.child(selectedFriend.getId()).removeValue();
    }

    @Override
    public void onBackPressed() {
        if (selectedFriendsList.size() > 0){
            Dialog dialog = new Dialog(CreateGroupActivity.this);
            dialog.setContentView(R.layout.dialog_discard_group);

            int width = WindowManager.LayoutParams.MATCH_PARENT;
            int height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialog.getWindow().setLayout(width, height);
            dialog.show();

            TextView cancel = dialog.findViewById(R.id.cancel);
            TextView discard = dialog.findViewById(R.id.discard);
            TextView warning = dialog.findViewById(R.id.warn_txt);

            warning.setText("If you quit before creating your group,\n your changes won't be saved.");

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            discard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tempRef.removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    dialog.dismiss();
                                    CreateGroupActivity.super.onBackPressed();
                                }
                            });
                }
            });
        }else {
            super.onBackPressed();
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