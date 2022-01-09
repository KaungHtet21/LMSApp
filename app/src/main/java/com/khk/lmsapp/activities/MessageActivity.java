package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.SharePreferenceHelper;
import com.khk.lmsapp.adapters.ChatsAdapter;
import com.khk.lmsapp.adapters.FriendsAdapter;
import com.khk.lmsapp.adapters.GroupsAdapter;
import com.khk.lmsapp.modules.Chats;
import com.khk.lmsapp.modules.Teachers;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private FloatingActionButton fab, fab_chat, fab_friend, fab_group, fab_archive;
    private LinearLayout fabChatLayout, fabFriendLayout, fabGroupLayout, fabArchiveLayout;
    private CircleImageView profile;
    private ImageView back;
    private TextView title;
    private EditText inputSearch;
    private RecyclerView chatsRv, friendsRv, groupsRv, archiveRv;
    private LinearLayout groupsContainer, createGroup;
    private View fabBGLayout;

    DatabaseReference userRef, msgRef, chatsRef, groupsRef;
    FirebaseAuth auth;

    List<Teachers> friendsList = new ArrayList<>();
    List<Chats> chatsList = new ArrayList<>();
    List<String> groupsList = new ArrayList<>();
    FriendsAdapter friendsAdapter;
    ChatsAdapter chatAdapter;
    GroupsAdapter groupsAdapter;

    boolean isFABOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        init();

        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        msgRef= FirebaseDatabase.getInstance().getReference().child("Messages");
        chatsRef = FirebaseDatabase.getInstance().getReference().child("Chats").child(userId);
        groupsRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        userRef.child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String profileInput = snapshot.child("image").getValue().toString();
                            Glide.with(MessageActivity.this.getApplicationContext())
                                    .load(profileInput)
                                    .into(profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        chatsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Chats receivers = snapshot.getValue(Chats.class);
                chatsList.add(receivers);

                chatAdapter = new ChatsAdapter(chatsList, userId);
                chatAdapter.add(chatsList);

                chatsRv.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                chatsRv.setAdapter(chatAdapter);
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

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Teachers currentUser = snapshot.child(userId).getValue(Teachers.class);
                for (DataSnapshot ds: snapshot.getChildren()){
                    Teachers users = ds.getValue(Teachers.class);
                    friendsList.add(users);
                    friendsList.remove(currentUser);

                    friendsAdapter = new FriendsAdapter(friendsList);
                    friendsAdapter.add(friendsList);

                    friendsRv.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                    friendsRv.setAdapter(friendsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        groupsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.child(userId).exists()){
                    String groups = snapshot.getKey();
                    groupsList.add(groups);

                    groupsAdapter = new GroupsAdapter(groupsList);
                    groupsAdapter.add(groupsList);

                    groupsRv.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                    groupsRv.setAdapter(groupsAdapter);
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFABOpen){
                    openFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });

        fab_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("Chats");

                chatsRv.setVisibility(View.VISIBLE);
                friendsRv.setVisibility(View.GONE);
                groupsContainer.setVisibility(View.GONE);
                archiveRv.setVisibility(View.GONE);
                fab.setImageResource(R.drawable.ic_asset_2);
                inputSearch.setHint("Search Chats");
                closeFABMenu();
            }
        });

        fab_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("Friends");

                chatsRv.setVisibility(View.GONE);
                friendsRv.setVisibility(View.VISIBLE);
                groupsContainer.setVisibility(View.GONE);
                archiveRv.setVisibility(View.GONE);
                fab.setImageResource(R.drawable.ic_person);
                inputSearch.setHint("Search Friends");
                closeFABMenu();
            }
        });

        fab_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("Groups");

                chatsRv.setVisibility(View.GONE);
                friendsRv.setVisibility(View.GONE);
                groupsContainer.setVisibility(View.VISIBLE);
                archiveRv.setVisibility(View.GONE);
                fab.setImageResource(R.drawable.ic_group);
                inputSearch.setHint("Search Groups");
                closeFABMenu();
            }
        });

        fab_archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("Archives");

                chatsRv.setVisibility(View.GONE);
                friendsRv.setVisibility(View.GONE);
                groupsContainer.setVisibility(View.GONE);
                archiveRv.setVisibility(View.VISIBLE);
                fab.setImageResource(R.drawable.ic_archive);
                inputSearch.setHint("Search Archives");
                closeFABMenu();
            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, CreateGroupActivity.class);
                startActivity(intent);
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (inputSearch.getHint().toString().equals("Search Chats")){
                    chatAdapter.cancelTimer();
                }

                if (inputSearch.getHint().toString().equals("Search Friends")){
                    friendsAdapter.cancelTimer();
                }

                if (inputSearch.getHint().toString().equals("Search Groups")){
                    groupsAdapter.cancelTimer();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (chatsList.size() != 0){
                    chatAdapter.searchChats(s.toString());
                }

                if (friendsList.size() != 0){
                    friendsAdapter.searchFriends(s.toString());
                }

                if (groupsList.size() != 0){
                    groupsAdapter.searchGroups(s.toString());
                }
            }
        });
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotation(0);
        fabChatLayout.animate().translationY(0);
        fabFriendLayout.animate().translationY(0);
        fabGroupLayout.animate().translationY(0);
        fabArchiveLayout.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabChatLayout.setVisibility(View.GONE);
                    fabFriendLayout.setVisibility(View.GONE);
                    fabGroupLayout.setVisibility(View.GONE);
                    fabArchiveLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void openFABMenu() {
        isFABOpen = true;
        fabChatLayout.setVisibility(View.VISIBLE);
        fabFriendLayout.setVisibility(View.VISIBLE);
        fabGroupLayout.setVisibility(View.VISIBLE);
        fabArchiveLayout.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);
        fabChatLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabFriendLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        fabGroupLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
        fabArchiveLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_190));
    }

    private void init() {
        profile = findViewById(R.id.user_profile);
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);
        inputSearch = findViewById(R.id.inputSearch);

        chatsRv = findViewById(R.id.chats_rv);
        friendsRv = findViewById(R.id.friends_rv);
        groupsRv = findViewById(R.id.groups_rv);
        archiveRv = findViewById(R.id.archive_rv);

        groupsContainer = findViewById(R.id.groups_container);
        createGroup = findViewById(R.id.create_group_btn);

        fab = findViewById(R.id.fab);
        fab_chat = findViewById(R.id.fab_chat);
        fab_friend = findViewById(R.id.fab_friends);
        fab_group = findViewById(R.id.fab_groups);
        fab_archive = findViewById(R.id.fab_archive);
        fabChatLayout = findViewById(R.id.fab_chat_layout);
        fabFriendLayout = findViewById(R.id.fab_friends_layout);
        fabGroupLayout = findViewById(R.id.fab_groups_layout);
        fabArchiveLayout = findViewById(R.id.fab_archive_layout);
        fabBGLayout = findViewById(R.id.fabBGLayout);
    }
}