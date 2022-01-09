package com.khk.lmsapp.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.khk.lmsapp.R;
import com.khk.lmsapp.SharePreferenceHelper;
import com.khk.lmsapp.activities.DateActivity;
import com.khk.lmsapp.activities.DetailAttActivity;
import com.khk.lmsapp.activities.LecturesAddActivity;
import com.khk.lmsapp.activities.PayAttListActivity;
import com.khk.lmsapp.modules.Course;

import java.util.ArrayList;

public class TotalCoursesAdapter extends RecyclerView.Adapter<TotalCoursesAdapter.ViewHolder> {

    ArrayList<Course> courses;
    String major, entry, roll;
    private SharePreferenceHelper sharePreferenceHelper;

    public TotalCoursesAdapter(ArrayList<Course> courses, String major, String entry) {
        this.courses = courses;
        this.major = major;
        this.entry = entry;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_course, parent, false);
        sharePreferenceHelper = new SharePreferenceHelper(parent.getContext());
        return new ViewHolder(view);
    }

    private static final String TAG = "TotalCoursesAdapter";
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nameInput = courses.get(position).getName();
        holder.name.setText(nameInput);
        Log.d(TAG, "entry: "+ entry);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entry.equals("teacher_pay_att")){
                    Intent intent = new Intent(holder.itemView.getContext(), PayAttListActivity.class);
                    intent.putExtra("Major", major);
                    intent.putExtra("Course", nameInput);
                    holder.itemView.getContext().startActivity(intent);
                }else if (entry.equals("admin_st_att")){
                    Intent intent = new Intent(holder.itemView.getContext(), DateActivity.class);
                    intent.putExtra("Major", major);
                    intent.putExtra("Course", nameInput);
                    holder.itemView.getContext().startActivity(intent);
                }else if (entry.equals("teacher_course") || entry.equals("admin_course") || entry.equals("student_course")){
                    Intent intent = new Intent(holder.itemView.getContext(), LecturesAddActivity.class);
                    intent.putExtra("Major", major);
                    intent.putExtra("Course", nameInput);
                    intent.putExtra("Entry", entry);
                    holder.itemView.getContext().startActivity(intent);
                }else if (entry.equals("userInfo")){
                    Intent intent = new Intent(holder.itemView.getContext(), DetailAttActivity.class);
                    intent.putExtra("major", major);
                    intent.putExtra("course", nameInput);
                    intent.putExtra("roll", roll);
                    holder.itemView.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }

    public void setRoll(String roll){
        this.roll = roll;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView parent;
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.course_list_cv);
            name = itemView.findViewById(R.id.course_name);
        }
    }
}
