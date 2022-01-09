package com.khk.lmsapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khk.lmsapp.R;
import com.khk.lmsapp.modules.Students;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PayAttAdapter extends RecyclerView.Adapter<PayAttAdapter.ViewHolder> {

    ArrayList<Students> students;
    String major, course, saveCurrentDate;
    DatabaseReference attRef;

    public PayAttAdapter(ArrayList<Students> students, String major, String course) {
        this.students = students;
        this.major = major;
        this.course = course;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_recommend, parent, false);
        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat currentDate = new SimpleDateFormat("MMM, dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        attRef = FirebaseDatabase.getInstance().getReference().child("Attendance").child(major).child(course).child(saveCurrentDate);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nameInput = students.get(position).getName();
        String imageInput = students.get(position).getImage();
        String emailInput = students.get(position).getEmail();
        String rollNoInput = students.get(position).getRoll();

        holder.name.setText(nameInput);
        holder.email.setText(emailInput);
        Glide.with(holder.itemView.getContext())
                .load(imageInput)
                .into(holder.image);

//        attRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (!snapshot.exists()){
//                    for (int i = 0; i < students.size(); i++){
//                        HashMap<String, Object> key = new HashMap<>();
//                        attRef.updateChildren(key);
//
//                        keyRef = attRef.child(students.get(i).getRoll());
//
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("roll number", students.get(i).getRoll());
//                        hashMap.put("existence", "Absent");
//
//                        keyRef.updateChildren(hashMap);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        holder.present.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    attRef.child(rollNoInput).child("existence").setValue("Present")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        holder.present.setChecked(true);
                                        Toast.makeText(holder.itemView.getContext(), rollNoInput + " is present", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        holder.absent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    attRef.child(rollNoInput).child("existence").setValue("Absent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        holder.absent.setChecked(true);
                                        Toast.makeText(holder.itemView.getContext(), rollNoInput+ " is absent", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public void setStudents(ArrayList<Students> students) {
        this.students = students;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name, email;
        RadioButton present, absent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.recommend_image);
            name = itemView.findViewById(R.id.recommend_username);
            email = itemView.findViewById(R.id.recommend_email);
            present = itemView.findViewById(R.id.present_rb);
            absent = itemView.findViewById(R.id.absent_rb);
        }
    }
}
