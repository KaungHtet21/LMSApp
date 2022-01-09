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
import com.khk.lmsapp.modules.Chats;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    List<Chats> receivers;
    DatabaseReference userRef, chatRef;
    String userId;
    Timer timer;
    List<Chats> usersSource;

    public ChatsAdapter(List<Chats> receivers, String userId){
        this.receivers = receivers;
        this.userId = userId;
        usersSource = receivers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_friends_list, parent, false);
        return new ViewHolder(view);
    }

    private static final String TAG = "ChatAdapter";
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String receiverIds = receivers.get(position).getId();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(receiverIds);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String nameInput = snapshot.child("name").getValue().toString();
                    String imageInput = snapshot.child("image").getValue().toString();

                    holder.name.setText(nameInput);
                    Glide.with(holder.itemView.getContext().getApplicationContext())
                            .load(imageInput)
                            .into(holder.profile);
                }

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

        chatRef = FirebaseDatabase.getInstance().getReference().child("Chats");
        chatRef.child(userId).child(receiverIds)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String type = snapshot.child("type").getValue().toString();
                        String msg = snapshot.child("message").getValue().toString();

                        if (type.equals("text")){
                            holder.lastMsg.setText(msg);
                        }else if (type.equals("image")){
                            holder.lastMsg.setText("sent you a photo");
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
                intent.putExtra("id", receiverIds);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return receivers.size();
    }

    public void add(List<Chats> users){
        this.receivers = users;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile;
        TextView name, lastMsg;
        View online;
        ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.user_profile);
            name = itemView.findViewById(R.id.user_name);
            lastMsg = itemView.findViewById(R.id.last_msg);
            online = itemView.findViewById(R.id.online_icon);
            layout = itemView.findViewById(R.id.chats_container);
        }
    }

    public void searchChats(final String searchKeyword){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()){
                    receivers = usersSource;
                }else {
                    ArrayList<Chats> temp = new ArrayList<>();
                    for (Chats user: usersSource){
                        if (user.getName().toLowerCase().contains(searchKeyword.toLowerCase())){
                            temp.add(user);
                        }
                    }
                    receivers = temp;
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
