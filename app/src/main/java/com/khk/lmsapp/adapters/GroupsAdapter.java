package com.khk.lmsapp.adapters;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.activities.GroupChatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    List<String> groups;
    List<String> groupsSource;
    Timer timer;
    DatabaseReference groupsRef;

    public GroupsAdapter(List<String> groups) {
        this.groups = groups;
        groupsSource = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friends, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String group_name = groups.get(position);
        groupsRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        holder.name.setText(group_name);
        groupsRef.child(group_name)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String image = snapshot.child("image").getValue().toString();
                            Glide.with(holder.itemView.getContext())
                                    .load(image)
                                    .into(holder.profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), GroupChatActivity.class);
                intent.putExtra("group_name", group_name);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void add(List<String> groups){
        this.groups = groups;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layout;
        CircleImageView profile;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.user_profile);
            name = itemView.findViewById(R.id.user_name);
            layout = itemView.findViewById(R.id.friend_layout);
        }
    }

    public void searchGroups(final String searchKeyWord){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyWord.trim().isEmpty()){
                    groups = groupsSource;
                }else {
                    ArrayList<String> temp = new ArrayList<>();
                    for (String group: groupsSource){
                        if (group.toLowerCase().contains(searchKeyWord.toLowerCase())){
                            temp.add(group);
                        }
                    }

                    groups = temp;
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

    public void cancelTimer(){
        if (timer != null){
            timer.cancel();
        }
    }
}
