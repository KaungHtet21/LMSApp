package com.khk.lmsapp.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khk.lmsapp.R;
import com.khk.lmsapp.activities.UserInfoActivity;
import com.khk.lmsapp.modules.Students;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchRecommendAdapter extends BaseAdapter implements Filterable {

    public ArrayList<Students> students;
    public ArrayList<Students> orig;
    String activity;
    DatabaseReference stRef;

    public SearchRecommendAdapter(ArrayList<Students> students, String activity){
        super();
        this.students = students;
        this.activity = activity;
    }

    private static final String TAG = "SearchRecommendAdapter";

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final  ArrayList<Students> results = new ArrayList<>();
                if (orig == null)
                    orig = students;
                if (constraint != null){
                    if (orig != null && orig.size() > 0){
                        for (final  Students g : orig){
                            if (g.getName().toLowerCase().contains(constraint.toString()) || g.getName().contains(constraint.toString())){
                                results.add(g);
                            }
                        }
                    }
                    Log.d(TAG, "performFiltering: " + results.size());
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                students = (ArrayList<Students>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder{
        CircleImageView image;
        TextView name, email;
        LinearLayout parent;
        RadioGroup rg;
    }

    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Override
    public Object getItem(int position) {
        return students.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_recommend, parent, false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.recommend_username);
            holder.email = convertView.findViewById(R.id.recommend_email);
            holder.image = convertView.findViewById(R.id.recommend_image);
            holder.parent = convertView.findViewById(R.id.recommend_parent);
            holder.rg = convertView.findViewById(R.id.present_rg);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (activity.equals("PayAtt")){
            holder.rg.setVisibility(View.VISIBLE);
        }else {
            holder.rg.setVisibility(View.GONE);
        }

        holder.name.setText(students.get(position).getName());
        holder.email.setText(students.get(position).getEmail());
        Glide.with(parent.getContext())
                .load(students.get(position).getImage())
                .into(holder.image);

        stRef = FirebaseDatabase.getInstance().getReference().child("Students");
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.equals("teacher_list") || activity.equals("admin_list")){
                    String name = students.get(position).getName();
                    String role = students.get(position).getRole();
                    String phone = students.get(position).getPh();
                    String email = students.get(position).getEmail();
                    String address = students.get(position).getAddress();
                    String image = students.get(position).getImage();

                    Intent intent = new Intent(parent.getContext(), UserInfoActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("role", role);
                    intent.putExtra("phone", phone);
                    intent.putExtra("email", email);
                    intent.putExtra("address", address);
                    intent.putExtra("image", image);
                    parent.getContext().startActivity(intent);
                }else if (activity.equals("student_list")){
                    String batch = students.get(position).getBatch();
                    String name = students.get(position).getName();
                    String role = students.get(position).getRole();
                    String phone = students.get(position).getPh();
                    String email = students.get(position).getEmail();
                    String address = students.get(position).getAddress();
                    String image = students.get(position).getImage();
                    String roll = students.get(position).getRoll();
                    String major = "";

                    switch (batch){
                        case "First Year Batch":
                            if (role.equals("Student / CSE")){
                                major = "First Year / CSE";
                            }else if (role.equals("Student / ECE")){
                                major = "First Year / ECE";
                            }
                            break;
                        case "Second Year Batch":
                            if (role.equals("Student / CSE")){
                                major = "Second Year / CSE";
                            }else if (role.equals("Student / ECE")){
                                major = "Second Year / ECE";
                            }
                            break;
                        case "Third Year Batch":
                            if (role.equals("Student / CSE")){
                                major = "Third Year / CSE";
                            }else if (role.equals("Student / ECE")){
                                major = "Third Year / ECE";
                            }
                            break;
                        case "Forth Year Batch":
                            if (role.equals("Student / CSE")){
                                major = "Forth Year / CSE";
                            }else if (role.equals("Student / ECE")){
                                major = "Forth Year / ECE";
                            }
                            break;
                        case "Fifth Year Batch":
                            if (role.equals("Student / CSE")){
                                major = "Fifth Year / CSE";
                            }else if (role.equals("Student / ECE")){
                                major = "Fifth Year / ECE";
                            }
                            break;
                    }

                    Intent intent1 = new Intent(parent.getContext(), UserInfoActivity.class);
                    intent1.putExtra("name", name);
                    intent1.putExtra("role", role);
                    intent1.putExtra("phone", phone);
                    intent1.putExtra("email", email);
                    intent1.putExtra("address", address);
                    intent1.putExtra("image", image);
                    intent1.putExtra("roll", roll);
                    intent1.putExtra("major", major);
                    parent.getContext().startActivity(intent1);
                }
            }
        });
        return convertView;
    }
}
