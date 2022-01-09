package com.khk.lmsapp.adapters;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khk.lmsapp.R;
import com.khk.lmsapp.SharePreferenceHelper;
import com.khk.lmsapp.activities.AddCourseActivity;
import com.khk.lmsapp.activities.MajorListActivity;
import com.khk.lmsapp.activities.MessageActivity;
import com.khk.lmsapp.activities.SettingActivity;
import com.khk.lmsapp.activities.StudentListActivity;
import com.khk.lmsapp.activities.TeacherListActivity;
import com.khk.lmsapp.modules.Dashboard;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminDashboardAdapter extends RecyclerView.Adapter<AdminDashboardAdapter.ViewHolder> {

    ArrayList<Dashboard> items = new ArrayList<>();
    Context context;
    String entry, major;

    public AdminDashboardAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_admin_dashboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = items.get(position).getTitle();

        holder.title.setText(title);
        Glide.with(context)
                .load(items.get(position).getLogo())
                .into(holder.logo);

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (title){
                    case "Student":
                        Intent stIntent = new Intent(holder.itemView.getContext(), StudentListActivity.class);
                        holder.itemView.getContext().startActivity(stIntent);
                        break;
                    case "Teacher":
                        Intent teIntent = new Intent(holder.itemView.getContext(), TeacherListActivity.class);
                        teIntent.putExtra("entry", "teacher_list");
                        holder.itemView.getContext().startActivity(teIntent);
                        break;
                    case "Admin":
                        Intent adIntent = new Intent(holder.itemView.getContext(), TeacherListActivity.class);
                        adIntent.putExtra("entry", "admin_list");
                        holder.itemView.getContext().startActivity(adIntent);
                        break;
                    case "Message":
                        Intent msgIntent = new Intent(holder.itemView.getContext(), MessageActivity.class);
                        holder.itemView.getContext().startActivity(msgIntent);
                        break;
                    case "Courses":
                        if (entry.equals("admin")){
                            Intent majorIntent = new Intent(holder.itemView.getContext(), MajorListActivity.class);
                            majorIntent.putExtra("entry", "admin_course");
                            holder.itemView.getContext().startActivity(majorIntent);
                        }else if (entry.equals("teacher")){
                            Intent majorIntent = new Intent(holder.itemView.getContext(), MajorListActivity.class);
                            majorIntent.putExtra("entry","teacher_course");
                            holder.itemView.getContext().startActivity(majorIntent);
                        }else if (entry.equals("student")){
                            Intent lecturesIntent = new Intent(holder.itemView.getContext(), AddCourseActivity.class);
                            lecturesIntent.putExtra("entry", "student_course");
                            lecturesIntent.putExtra("major", major);
                            holder.itemView.getContext().startActivity(lecturesIntent);
                        }
                        break;
                    case "Setting":
                        Intent settingIntent = new Intent(holder.itemView.getContext(), SettingActivity.class);
                        holder.itemView.getContext().startActivity(settingIntent);
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<Dashboard> items){
        this.items = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView logo;
        TextView title;
        CardView item;
        SharePreferenceHelper helper;
        FirebaseAuth auth;
        DatabaseReference stRef;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item_holder);
            title = itemView.findViewById(R.id.admin_dashboard_list_title);
            logo = itemView.findViewById(R.id.admin_dashboard_list_logo);

            helper = new SharePreferenceHelper(itemView.getContext());
            entry = helper.get(SharePreferenceHelper.KEY_ENTRY, null);

            auth = FirebaseAuth.getInstance();
            String userId = auth.getCurrentUser().getUid();
            stRef = FirebaseDatabase.getInstance().getReference().child("Students");

            stRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot batchSnapshot: snapshot.getChildren()){
                        String batchKey = batchSnapshot.getKey();
                        for (DataSnapshot majorSnapshot: batchSnapshot.getChildren()){
                            if (majorSnapshot.child(userId).exists()){
                                String userRole = majorSnapshot.child(userId).child("role").getValue().toString();
                                if (userRole.equals("Student / CSE")){
                                    switch (batchKey){
                                        case "First Year Batch":
                                            major = "First Year / CSE";
                                            break;
                                        case "Second Year Batch":
                                            major = "Second Year / CSE";
                                            break;
                                        case "Third Year Batch":
                                            major = "Third Year / CSE";
                                            break;
                                        case "Forth Year Batch":
                                            major = "Forth Year / CSE";
                                            break;
                                        case "Fifth Year Batch":
                                            major = "Fifth Year / CSE";
                                            break;
                                    }
                                }else if (userRole.equals("Student / ECE")){
                                    switch (batchKey){
                                        case "First Year Batch":
                                            major = "First Year / ECE";
                                            break;
                                        case "Second Year Batch":
                                            major = "Second Year / ECE";
                                            break;
                                        case "Third Year Batch":
                                            major = "Third Year / ECE";
                                            break;
                                        case "Forth Year Batch":
                                            major = "Forth Year / ECE";
                                            break;
                                        case "Fifth Year Batch":
                                            major = "Fifth Year / ECE";
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
