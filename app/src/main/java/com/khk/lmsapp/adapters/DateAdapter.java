package com.khk.lmsapp.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khk.lmsapp.R;
import com.khk.lmsapp.activities.TotalAttActivity;
import com.khk.lmsapp.modules.DateList;

import java.util.ArrayList;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {

    private static final String TAG = "DateAdapter";
    ArrayList<DateList> list;
    String major, course;

    public DateAdapter(ArrayList<DateList> list, String major, String course) {
        this.list = list;
        this.major = major;
        this.course = course;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_date_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String dateInput = list.get(position).getDate();
        holder.date.setText(dateInput);
        holder.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), TotalAttActivity.class);
                intent.putExtra("major", major);
                intent.putExtra("course", course);
                intent.putExtra("date", dateInput);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<DateList> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date_txt);
        }
    }
}
