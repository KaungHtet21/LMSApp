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
import com.khk.lmsapp.modules.DateList;

import java.util.ArrayList;

public class DetailAttAdapter extends RecyclerView.Adapter<DetailAttAdapter.ViewHolder> {

    ArrayList<DateList> dateLists;
    DatabaseReference attRef;
    String major, course, roll;

    public DetailAttAdapter(ArrayList<DateList> dateLists, String major, String course, String roll) {
        this.dateLists = dateLists;
        this.major = major;
        this.course = course;
        this.roll = roll;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_detail_att_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String dateInput = dateLists.get(position).getDate();
        holder.date.setText(dateInput);

        attRef = FirebaseDatabase.getInstance().getReference().child("Attendance").child(major).child(course).child(dateInput).child(roll);
        attRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String existenceInput = snapshot.child("existence").getValue().toString();
                    if (existenceInput.equals("Present")){
                        holder.existence.setText(existenceInput);
                        holder.present.setVisibility(View.VISIBLE);
                    }else if (existenceInput.equals("Absent")){
                        holder.existence.setText(existenceInput);
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
        return dateLists.size();
    }

    public void setDateLists(ArrayList<DateList> dateLists) {
        this.dateLists = dateLists;
        notifyItemInserted(dateLists.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, existence;
        ImageView present, absent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date_txt);
            existence = itemView.findViewById(R.id.present_txt);
            present = itemView.findViewById(R.id.present_icon);
            absent = itemView.findViewById(R.id.absent_icon);
        }
    }
}
