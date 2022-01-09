package com.khk.lmsapp.adapters;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khk.lmsapp.R;
import com.khk.lmsapp.listener.FriendSelectListener;
import com.khk.lmsapp.modules.Teachers;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupFriendsContainerAdapter extends RecyclerView.Adapter<CreateGroupFriendsContainerAdapter.ViewHolder> {

    List<Teachers> users;
    List<Teachers> usersSource;
    FriendSelectListener listener;
    List<Teachers> selectedFriendsList = new ArrayList<>();
    Timer timer;

    public CreateGroupFriendsContainerAdapter(List<Teachers> users, FriendSelectListener listener) {
        this.users = users;
        this.listener = listener;
        usersSource = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friends_for_create_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nameInput = users.get(position).getName();
        String imageInput = users.get(position).getImage();

        holder.name.setText(nameInput);
        Glide.with(holder.itemView.getContext())
                .load(imageInput)
                .into(holder.profile);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.selectedIcon.getVisibility() == View.GONE){
                    holder.selectedIcon.setVisibility(View.VISIBLE);
                    Teachers selectedFriend = users.get(holder.getAdapterPosition());
                    selectedFriendsList.add(selectedFriend);

                    listener.onFriendSelectAdd(selectedFriend);
                }else {
                    holder.selectedIcon.setVisibility(View.GONE);
                    Teachers selectedFriend = users.get(holder.getAdapterPosition());
                    selectedFriendsList.remove(selectedFriend);

                    listener.onFriendSelectRemove(selectedFriend);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void add(List<Teachers> users) {
        this.users = users;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile;
        TextView name;
        ImageView selectedIcon;
        ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.user_profile);
            name = itemView.findViewById(R.id.user_name);
            selectedIcon = itemView.findViewById(R.id.selected_icon);
            layout = itemView.findViewById(R.id.selected_layout);
        }
    }

    public void searchFriends(final String searchKeyword){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()){
                    users = usersSource;
                }else {
                    ArrayList<Teachers> temp = new ArrayList<>();
                    for (Teachers user: usersSource){
                        if (user.getName().toLowerCase().contains(searchKeyword.toLowerCase())){
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
