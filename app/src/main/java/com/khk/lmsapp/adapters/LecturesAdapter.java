package com.khk.lmsapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khk.lmsapp.R;
import com.khk.lmsapp.modules.Lectures;

import java.util.ArrayList;

public class LecturesAdapter extends RecyclerView.Adapter<LecturesAdapter.ViewHolder> {

    String major, course, entry;
    ArrayList<Lectures> lectures;
    DatabaseReference fileRef;

    public LecturesAdapter(ArrayList<Lectures> lectures, String major, String course, String entry) {
        this.lectures = lectures;
        this.major = major;
        this.course = course;
        this.entry = entry;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_lectures_list, parent, false);
        return new ViewHolder(view);
    }

    private static final String TAG = "LecturesAdapter";
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String contentInput = lectures.get(position).getContent();
        holder.content.setText(contentInput);

        Log.d(TAG, "size: "+ lectures.size());

        fileRef = FirebaseDatabase.getInstance().getReference().child("Lectures").child(major).child(course).child(contentInput);
        fileRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()){
                    Lectures list = snapshot.getValue(Lectures.class);
                    holder.adapter.add(list);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Lectures list = snapshot.getValue(Lectures.class);
                holder.adapter.delete(list);
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
    public int getItemCount() {
        return lectures.size();
    }

    public void setLectures(ArrayList<Lectures> lectures) {
        this.lectures = lectures;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView content;
        RecyclerView rv;
        LectureFilesAdapter adapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cv = itemView.findViewById(R.id.lectures_cv);
            content = itemView.findViewById(R.id.lectures_titles);
            rv = itemView.findViewById(R.id.lectures_rv);

            adapter = new LectureFilesAdapter(entry, major, course);

            rv.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            rv.setAdapter(adapter);
        }
    }
}
