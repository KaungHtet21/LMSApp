package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.SharePreferenceHelper;
import com.khk.lmsapp.adapters.BookmarkAdapter;
import com.khk.lmsapp.modules.Bookmark;

import java.util.ArrayList;

public class BookmarkActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rv;
    TextView txt;

    FirebaseAuth auth;
    DatabaseReference bookmarkRef;
    BookmarkAdapter adapter;
    ArrayList<Bookmark> files = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        toolbar = findViewById(R.id.bookmark_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Bookmark");

        txt = findViewById(R.id.default_tv);
        rv = findViewById(R.id.bookmark_rv);
        adapter = new BookmarkAdapter();
        rv.setLayoutManager(new LinearLayoutManager(BookmarkActivity.this));
        rv.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        bookmarkRef = FirebaseDatabase.getInstance().getReference().child("Bookmark").child(userId);

        bookmarkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    txt.setVisibility(View.VISIBLE);
                }else {
                    txt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bookmarkRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()){
                    Bookmark bookmark = snapshot.getValue(Bookmark.class);
                    adapter.add(bookmark);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Bookmark bookmark = snapshot.getValue(Bookmark.class);
                adapter.remove(bookmark);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}