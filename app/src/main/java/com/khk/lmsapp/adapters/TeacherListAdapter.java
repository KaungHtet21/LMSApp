package com.khk.lmsapp.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khk.lmsapp.R;
import com.khk.lmsapp.activities.UserInfoActivity;
import com.khk.lmsapp.modules.Teachers;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherListAdapter extends RecyclerView.Adapter<TeacherListAdapter.ViewHolder> {

    ArrayList<Teachers> teachers;

    public TeacherListAdapter(ArrayList<Teachers> teachers) {
        this.teachers = teachers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nameInput = teachers.get(position).getName();
        String imageInput = teachers.get(position).getImage();

        holder.name.setText(nameInput);
        Glide.with(holder.itemView.getContext())
                .load(imageInput)
                .into(holder.image);

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapter_position = holder.getAdapterPosition();
                String id = teachers.get(adapter_position).getId();
                String name = teachers.get(adapter_position).getName();
                String role = teachers.get(adapter_position).getRole();
                String phone = teachers.get(adapter_position).getPh();
                String email = teachers.get(adapter_position).getEmail();
                String address = teachers.get(adapter_position).getAddress();
                String image = teachers.get(adapter_position).getImage();

                Intent intent = new Intent(holder.itemView.getContext(), UserInfoActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("phone", phone);
                intent.putExtra("email", email);
                intent.putExtra("address", address);
                intent.putExtra("image", image);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public void setTeachers(ArrayList<Teachers> teachers) {
        this.teachers = teachers;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        CircleImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cv = itemView.findViewById(R.id.user_list_cv);
            image = itemView.findViewById(R.id.user_list_profile_picture);
            name = itemView.findViewById(R.id.user_list_username);
        }
    }
}
