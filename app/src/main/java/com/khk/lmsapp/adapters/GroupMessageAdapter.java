package com.khk.lmsapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.activities.ImageViewActivity;
import com.khk.lmsapp.modules.Message;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.ViewHolder> {

    Context context;
    List<Message> messagesList;
    FirebaseAuth auth;
    DatabaseReference userRef;

    public GroupMessageAdapter(List<Message> messagesList, Activity activity){
        this.messagesList = messagesList;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_msg_list, parent, false);
        auth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messagesList.get(position);

        String senderId = auth.getCurrentUser().getUid();
        String fromUserId = message.getFrom();
        String fromMsgType = message.getType();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("image")){
                    String retImage = snapshot.child("image").getValue().toString();
                    Glide.with(context.getApplicationContext())
                            .load(retImage)
                            .into(holder.receiverProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.senderMsgInput.setVisibility(View.GONE);
        holder.receiverMsgInput.setVisibility(View.GONE);
        holder.receiverProfile.setVisibility(View.GONE);
        holder.senderTxtDateTime.setVisibility(View.GONE);
        holder.receiverTxtDateTime.setVisibility(View.GONE);
        holder.senderPictureInput.setVisibility(View.GONE);
        holder.receiverPictureInput.setVisibility(View.GONE);
        holder.senderImageDateTime.setVisibility(View.GONE);
        holder.receiverImageDateTime.setVisibility(View.GONE);

        if (fromMsgType.equals("text")){
            if (fromUserId.equals(senderId)){
                holder.senderMsgInput.setVisibility(View.VISIBLE);
                holder.senderMsgInput.setText(message.getMessage());
            }else {
                holder.receiverProfile.setVisibility(View.VISIBLE);
                holder.receiverMsgInput.setVisibility(View.VISIBLE);
                holder.receiverMsgInput.setText(message.getMessage());
            }
        }else if (fromMsgType.equals("image")){
            if (fromUserId.equals(senderId)){
                holder.senderPictureInput.setVisibility(View.VISIBLE);
                Glide.with(holder.itemView.getContext())
                        .load(message.getMessage())
                        .into(holder.senderPictureInput);

                holder.senderImageDateTime.setVisibility(View.VISIBLE);
                holder.senderImageDateTime.setText(message.getDate() + "\n"+ message.getTime());
            }else {
                holder.receiverProfile.setVisibility(View.VISIBLE);
                holder.receiverPictureInput.setVisibility(View.VISIBLE);
                Glide.with(holder.itemView.getContext())
                        .load(message.getMessage())
                        .into(holder.receiverPictureInput);
                holder.receiverImageDateTime.setVisibility(View.VISIBLE);
                holder.receiverImageDateTime.setText(message.getDate() + "\n"+ message.getTime());
            }
        }

        if (fromUserId.equals(senderId)){
            holder.senderMsgInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.senderTxtDateTime.getVisibility() == View.GONE){
                        holder.senderTxtDateTime.setVisibility(View.VISIBLE);
                        holder.senderTxtDateTime.setText(message.getDate() + "\n"+ message.getTime());
                    }else {
                        holder.senderTxtDateTime.setVisibility(View.GONE);
                    }
                }
            });

            holder.senderPictureInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.itemView.getContext(), ImageViewActivity.class);
                    intent.putExtra("url", messagesList.get(holder.getAdapterPosition()).getMessage());
                    holder.itemView.getContext().startActivity(intent);
                }
            });
        }else {
            holder.receiverMsgInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.receiverTxtDateTime.getVisibility() == View.GONE){
                        holder.receiverTxtDateTime.setVisibility(View.VISIBLE);
                        holder.receiverTxtDateTime.setText(message.getDate() + "\n"+ message.getTime());
                    }else {
                        holder.receiverTxtDateTime.setVisibility(View.GONE);
                    }
                }
            });

            holder.receiverPictureInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.itemView.getContext(), ImageViewActivity.class);
                    intent.putExtra("url", messagesList.get(holder.getAdapterPosition()).getMessage());
                    holder.itemView.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView receiverProfile;
        TextView senderMsgInput, receiverMsgInput, senderTxtDateTime, receiverTxtDateTime, senderImageDateTime, receiverImageDateTime;
        ImageView receiverPictureInput, senderPictureInput;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverProfile = itemView.findViewById(R.id.receiver_profile);
            senderMsgInput = itemView.findViewById(R.id.msg_sender_input);
            receiverMsgInput = itemView.findViewById(R.id.msg_receiver_input);
            senderTxtDateTime = itemView.findViewById(R.id.msg_sender_text_date_time);
            receiverTxtDateTime = itemView.findViewById(R.id.msg_receiver_text_date_time);
            receiverPictureInput = itemView.findViewById(R.id.msg_receiver_image_view);
            senderPictureInput = itemView.findViewById(R.id.msg_sender_image_view);
            senderImageDateTime = itemView.findViewById(R.id.msg_sender_image_date_time);
            receiverImageDateTime = itemView.findViewById(R.id.msg_receiver_image_date_time);
        }
    }
}
