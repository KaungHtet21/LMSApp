package com.khk.lmsapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khk.lmsapp.R;
import com.khk.lmsapp.modules.Members;
import com.khk.lmsapp.modules.Teachers;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {

    ArrayList<Members> members;

    public MembersAdapter(ArrayList<Members> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friends, parent, false);
        return new ViewHolder(view);
    }

    private static final String TAG = "MembersAdapter";
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = members.get(position).getName();
        String image = members.get(position).getImage();

        Log.d(TAG, "onBindViewHolder: "+members.size());
        holder.name.setText(name);
        Glide.with(holder.itemView.getContext())
                .load(image)
                .into(holder.profile);
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void add(ArrayList<Members> members){
        this.members = members;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.user_profile);
            name = itemView.findViewById(R.id.user_name);
        }
    }
}
