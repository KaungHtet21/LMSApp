package com.khk.lmsapp.adapters;

import android.app.Dialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khk.lmsapp.R;
import com.khk.lmsapp.modules.Teachers;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentsRegisterListAdapter extends RecyclerView.Adapter<StudentsRegisterListAdapter.ViewHolder> {

    ArrayList<Teachers> students;
    FirebaseAuth auth;
    DatabaseReference stReqRef, stPendingRef, userRef;

    public StudentsRegisterListAdapter(ArrayList<Teachers> students){
        this.students = students;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_students_register_list, parent, false);

        auth = FirebaseAuth.getInstance();
        stReqRef = FirebaseDatabase.getInstance().getReference().child("Students Request");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        stPendingRef = FirebaseDatabase.getInstance().getReference();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String addressInput = students.get(position).getAddress();
        String bdInput = students.get(position).getBd();
        String idInput = students.get(position).getId();
        String emailInput = students.get(position).getEmail();
        String genderInput = students.get(position).getGender();
        String nameInput = students.get(position).getName();
        String phInput = students.get(position).getPh();
        String roleInput = students.get(position).getRole();
        String passInput = students.get(position).getPassword();
        String profileInput = students.get(position).getImage();

        holder.email.setText(emailInput);
        holder.name.setText(nameInput);

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(holder.itemView.getContext());
                dialog.setContentView(R.layout.dialog_details);

                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setLayout(width, height);
                dialog.show();

                TextView address = dialog.findViewById(R.id.detail_address);
                TextView bd = dialog.findViewById(R.id.detail_bd);
                TextView email = dialog.findViewById(R.id.detail_email);
                TextView gender = dialog.findViewById(R.id.detail_gender);
                TextView name = dialog.findViewById(R.id.detail_name);
                TextView ph = dialog.findViewById(R.id.detail_ph);
                TextView role = dialog.findViewById(R.id.detail_Role);
                TextView rollNo = dialog.findViewById(R.id.detail_roll_no);
                CircleImageView profile = dialog.findViewById(R.id.detail_profile_picture);
                Button back = dialog.findViewById(R.id.detail_back);

                address.setText("Address: " + addressInput);
                bd.setText("Birth Date: " + bdInput);
                email.setText("Email: "+ emailInput);
                gender.setText("Gender: "+ genderInput);
                name.setText("Name: "+ nameInput);
                ph.setText("Phone Number: "+ phInput);
                role.setText("Major: "+ roleInput);
                rollNo.setVisibility(View.GONE);

                Glide.with(holder.itemView.getContext())
                        .load(profileInput)
                        .into(profile);

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rollNoInput = holder.rollNo.getText().toString();

                if (TextUtils.isEmpty(rollNoInput)){
                    Toast.makeText(holder.itemView.getContext(), "Enter roll number", Toast.LENGTH_SHORT).show();
                }else {
                    String batchString = holder.batch.getSelectedItem().toString();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", idInput);
                    hashMap.put("name", nameInput);
                    hashMap.put("bd", bdInput);
                    hashMap.put("gender", genderInput);
                    hashMap.put("address", addressInput);
                    hashMap.put("ph", phInput);
                    hashMap.put("email", emailInput);
                    hashMap.put("password", passInput);
                    hashMap.put("role", roleInput);
                    hashMap.put("roll", rollNoInput);
                    hashMap.put("image", profileInput);
                    hashMap.put("batch", batchString);

                    stPendingRef.child("Students Pending").child(idInput)
                            .updateChildren(hashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        removeFromList(holder.getAdapterPosition());
                                        Toast.makeText(holder.itemView.getContext(), nameInput + " is added", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromList(holder.getAdapterPosition());
                Toast.makeText(holder.itemView.getContext(), nameInput + " is deleted from the list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public void setStudents(ArrayList<Teachers> students){
        this.students = students;
    }

    public void deleteFromRef(Teachers studentRegister){
        students.remove(studentRegister);
        notifyDataSetChanged();
    }

    private void removeFromList(int position){
        String studentId = students.get(position).getId();

        stReqRef.child(studentId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, students.size());
                    }
                });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView email, name;
        EditText rollNo;
        Button accept, decline;
        Spinner batch;
        LinearLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            email = itemView.findViewById(R.id.st_register_email);
            name = itemView.findViewById(R.id.st_register_name);
            rollNo = itemView.findViewById(R.id.st_register_roll_no);
            accept = itemView.findViewById(R.id.st_register_accept);
            decline = itemView.findViewById(R.id.st_register_decline);
            parent = itemView.findViewById(R.id.st_register_list_parent);
            batch = itemView.findViewById(R.id.st_register_batch);
        }
    }
}
