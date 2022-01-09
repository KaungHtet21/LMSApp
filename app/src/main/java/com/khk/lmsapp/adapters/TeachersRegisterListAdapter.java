package com.khk.lmsapp.adapters;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class TeachersRegisterListAdapter extends RecyclerView.Adapter<TeachersRegisterListAdapter.ViewHolder> {

    ArrayList<Teachers> teachers;
    FirebaseAuth auth;
    DatabaseReference teReqRef, teRef, userRef, tePendingRef;

    public TeachersRegisterListAdapter(ArrayList<Teachers> teachers){
        this.teachers = teachers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_teachers_register_list, parent, false);
        auth = FirebaseAuth.getInstance();
        teReqRef = FirebaseDatabase.getInstance().getReference().child("Teachers Request");
        teRef = FirebaseDatabase.getInstance().getReference().child("Teachers");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        tePendingRef = FirebaseDatabase.getInstance().getReference().child("Teachers Pending");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String addressInput = teachers.get(position).getAddress();
        String bdInput = teachers.get(position).getBd();
        String emailInput = teachers.get(position).getEmail();
        String genderInput = teachers.get(position).getGender();
        String idInput = teachers.get(position).getId();
        String nameInput = teachers.get(position).getName();
        String phInput = teachers.get(position).getPh();
        String roleInput = teachers.get(position).getRole();
        String passInput = teachers.get(position).getPassword();
        String profileInput = teachers.get(position).getImage();

        holder.email.setText(teachers.get(position).getEmail());
        holder.name.setText(teachers.get(position).getName());
        holder.role.setText(teachers.get(position).getRole());

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

                address.setText("Address: "+ addressInput);
                bd.setText("Birth Date: "+ bdInput);
                email.setText("Email: "+ emailInput);
                gender.setText("Gender: "+ genderInput);
                name.setText("Name: "+ nameInput);
                ph.setText("Phone Number: "+ phInput);
                role.setText("Role: "+ roleInput);
                rollNo.setVisibility(View.GONE);

                Glide.with(holder.itemView.getContext())
                        .load(profileInput)
                        .into(profile);

                back.setOnClickListener(v1 -> dialog.dismiss());
            }
        });

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                hashMap.put("image", profileInput);

                tePendingRef.child(idInput)
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
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromList(holder.getAdapterPosition());
                Toast.makeText(holder.itemView.getContext(), nameInput + " is deleted from the list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteFromRef(Teachers teachersRegister){
        teachers.remove(teachersRegister);
        notifyDataSetChanged();
    }

    private void removeFromList(int position) {
        String teacherId = teachers.get(position).getId();

        teReqRef.child(teacherId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, teachers.size());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public void setTeachers(ArrayList<Teachers> teachers){
        this.teachers = teachers;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView email, name, role;
        Button accept, decline;
        LinearLayout parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            email = itemView.findViewById(R.id.te_register_email);
            name = itemView.findViewById(R.id.te_register_name);
            role = itemView.findViewById(R.id.te_register_role);
            accept = itemView.findViewById(R.id.te_register_accept);
            decline = itemView.findViewById(R.id.te_register_decline);
            parent = itemView.findViewById(R.id.te_register_list_parent);
        }
    }
}
