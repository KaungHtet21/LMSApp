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
import com.khk.lmsapp.modules.Students;

import java.util.ArrayList;
import java.util.Timer;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentsListAdapter extends RecyclerView.Adapter<StudentsListAdapter.ViewHolder> {

    ArrayList<Students> students;
    String major;
    private Timer timer;

    public StudentsListAdapter(String major){
        this.students = new ArrayList<>();
        this.major = major;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(students.get(position).getName());
        Glide.with(holder.itemView.getContext())
                .load(students.get(position).getImage())
                .into(holder.image);

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapter_position = holder.getAdapterPosition();
                String id = students.get(adapter_position).getId();
                String name = students.get(adapter_position).getName();
                String role = students.get(adapter_position).getRole();
                String phone = students.get(adapter_position).getPh();
                String email = students.get(adapter_position).getEmail();
                String address = students.get(adapter_position).getAddress();
                String image = students.get(adapter_position).getImage();
                String roll = students.get(adapter_position).getRoll();

                Intent intent = new Intent(holder.itemView.getContext(), UserInfoActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("phone", phone);
                intent.putExtra("email", email);
                intent.putExtra("address", address);
                intent.putExtra("image", image);
                intent.putExtra("roll", roll);
                intent.putExtra("major", major);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public void setStudents(Students students){
        this.students.add(students);
        notifyItemInserted(this.students.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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
