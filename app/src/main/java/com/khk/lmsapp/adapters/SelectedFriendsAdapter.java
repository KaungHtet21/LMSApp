package com.khk.lmsapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khk.lmsapp.R;
import com.khk.lmsapp.modules.Teachers;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectedFriendsAdapter extends RecyclerView.Adapter<SelectedFriendsAdapter.ViewHolder> {

    List<Teachers> selectedFriends;

    public SelectedFriendsAdapter() {
        this.selectedFriends = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_selected_for_group, parent, false);
        return new ViewHolder(view);
    }

    private static final String TAG = "SelectedFriendsAdapter";
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nameInput = selectedFriends.get(position).getName();
        String imageInput = selectedFriends.get(position).getImage();

        holder.name.setText(nameInput);
        Glide.with(holder.itemView.getContext())
                .load(imageInput)
                .into(holder.profile);
    }

    @Override
    public int getItemCount() {
        return selectedFriends.size();
    }

    public void add(Teachers selectedFriend){
        this.selectedFriends.add(selectedFriend);
        notifyItemInserted(this.selectedFriends.size());
    }

    public void remove(Teachers selectedFriend){
        this.selectedFriends.remove(selectedFriend);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile;
        TextView name;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.user_profile);
            name = itemView.findViewById(R.id.user_name);
            layout = itemView.findViewById(R.id.selected_layout);
        }
    }
}
