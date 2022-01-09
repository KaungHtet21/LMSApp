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
import com.khk.lmsapp.activities.ChatActivity;
import com.khk.lmsapp.modules.Note;
import com.khk.lmsapp.modules.Teachers;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    List<Teachers> users;
    DatabaseReference userRef;
    Timer timer;
    List<Teachers> usersSource;

    public FriendsAdapter(List<Teachers> users) {
        this.users = users;
        usersSource = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friends, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nameInput = users.get(position).getName();
        String imageInput = users.get(position).getImage();
        String idInput = users.get(position).getId();

        holder.username.setText(nameInput);
        Glide.with(holder.itemView.getContext())
                .load(imageInput)
                .into(holder.profile);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.child(idInput)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("UserState")){
                            String state = snapshot.child("UserState").child("state").getValue().toString();
                            if (state.equals("online")){
                                holder.online.setVisibility(View.VISIBLE);
                            }else {
                                holder.online.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), ChatActivity.class);
                intent.putExtra("id", idInput);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void add(List<Teachers> users){
        this.users = users;
        notifyItemInserted(users.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View online;
        ConstraintLayout layout;
        CircleImageView profile;
        TextView username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            online = itemView.findViewById(R.id.online_icon);
            layout = itemView.findViewById(R.id.friend_layout);
            profile = itemView.findViewById(R.id.user_profile);
            username = itemView.findViewById(R.id.user_name);
        }
    }

    public void searchFriends(final String searchKeyWord){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyWord.trim().isEmpty()){
                    users = usersSource;
                }else {
                    ArrayList<Teachers> temp = new ArrayList<>();
                    for (Teachers user: usersSource){
                        if (user.getName().toLowerCase().contains(searchKeyWord.toLowerCase())){
                            temp.add(user);
                        }
                    }

                    users = temp;
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
