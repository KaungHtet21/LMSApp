package com.khk.lmsapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.modules.Students;

import java.util.ArrayList;

public class TotalAttAdapter extends RecyclerView.Adapter<TotalAttAdapter.ViewHolder> {

    ArrayList<Students> students;
    String major, course, date;
    DatabaseReference attRef;

    public TotalAttAdapter(ArrayList<Students> students, String major, String course, String date) {
        this.students = students;
        this.major = major;
        this.course = course;
        this.date = date;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_total_att, parent, false);
        attRef = FirebaseDatabase.getInstance().getReference().child("Attendance").child(major).child(course).child(date);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String rollInput = students.get(position).getRoll();
        holder.roll.setText(rollInput);

        attRef.child(rollInput)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.hasChild("existence")){
                            String existence = snapshot.child("existence").getValue().toString();
                            if (existence.equals("Present")){
                                holder.attend.setText("Present");
                                holder.present.setVisibility(View.VISIBLE);
                            }else if (existence.equals("Absent")){
                                holder.attend.setText("Absent");
                                holder.absent.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public void setStudents(ArrayList<Students> students) {
        this.students = students;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView roll, attend;
        ImageView present, absent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            roll = itemView.findViewById(R.id.roll_no_txt);
            attend = itemView.findViewById(R.id.present_txt);
            present = itemView.findViewById(R.id.present_icon);
            absent = itemView.findViewById(R.id.absent_icon);
        }
    }
}
