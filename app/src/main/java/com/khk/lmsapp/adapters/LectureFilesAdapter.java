package com.khk.lmsapp.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khk.lmsapp.R;
import com.khk.lmsapp.modules.Lectures;

import java.util.ArrayList;
import java.util.HashMap;

public class LectureFilesAdapter extends RecyclerView.Adapter<LectureFilesAdapter.ViewHolder> {

    ArrayList<Lectures> files;
    String entry, major, course;
    DatabaseReference lecturesRef, bookmarkRef;
    FirebaseAuth auth;
    StorageReference filePath;

    public LectureFilesAdapter(String entry, String major, String course) {
        this.files = new ArrayList<>();
        this.entry = entry;
        this.major = major;
        this.course = course;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_files_list, parent, false);
        return new ViewHolder(view);
    }

    private static final String TAG = "LectureFilesAdapter";
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String idInput = files.get(position).getId();
        String fileInput = files.get(position).getSaveAs();
        String contentInput = files.get(position).getContent();
        String pdf = files.get(position).getFile();

        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        bookmarkRef = FirebaseDatabase.getInstance().getReference().child("Bookmark");
        lecturesRef = FirebaseDatabase.getInstance().getReference().child("Lectures").child(major).child(course);
        String fileName = contentInput +"/"+ fileInput;
        filePath = FirebaseStorage.getInstance().getReference().child("Lectures").child(fileName);

        holder.name.setText(fileInput);
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entry.equals("teacher_course")){
                    CharSequence options[] = new CharSequence[]{
                            "Download and Open",
                            "Edit",
                            "Save to bookmark",
                            "Delete"
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle(holder.name.getText());

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(files.get(holder.getAdapterPosition()).getFile()));
                                    holder.itemView.getContext().startActivity(intent);
                                    break;
                                case 1:
                                    Dialog edtDialog = new Dialog(holder.itemView.getContext());
                                    edtDialog.setContentView(R.layout.dialog_edt_file);

                                    int width = WindowManager.LayoutParams.MATCH_PARENT;
                                    int height = WindowManager.LayoutParams.WRAP_CONTENT;
                                    edtDialog.getWindow().setLayout(width, height);
                                    edtDialog.show();

                                    EditText edt = edtDialog.findViewById(R.id.change_file_edt);
                                    TextView cancel = edtDialog.findViewById(R.id.cancel);
                                    Button change = edtDialog.findViewById(R.id.change);

                                    cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            edtDialog.dismiss();
                                        }
                                    });

                                    change.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String changeInput = edt.getText().toString();

                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("content", contentInput);
                                            hashMap.put("file", pdf);
                                            hashMap.put("id", idInput);
                                            hashMap.put("saveAs", changeInput);

                                            lecturesRef.child(contentInput).child(idInput).updateChildren(hashMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                holder.name.setText(changeInput);
                                                                edtDialog.dismiss();
                                                                Toast.makeText(holder.itemView.getContext(), "Edited successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                                    break;
                                case 2:
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("saveAs", fileInput);
                                    hashMap.put("file", pdf);
                                    hashMap.put("userId", userId);
                                    hashMap.put("fileId", idInput);

                                    bookmarkRef.child(userId).child(idInput).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(holder.itemView.getContext(), fileInput +" is added to bookmark successfully.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    break;
                                case 3:
                                    Dialog delDialog = new Dialog(holder.itemView.getContext());
                                    delDialog.setContentView(R.layout.dialog_logout);

                                    int width1 = WindowManager.LayoutParams.MATCH_PARENT;
                                    int height1 = WindowManager.LayoutParams.WRAP_CONTENT;

                                    delDialog.getWindow().setLayout(width1, height1);
                                    delDialog.show();

                                    TextView txt, no, yes;
                                    txt = delDialog.findViewById(R.id.txt);
                                    no = delDialog.findViewById(R.id.logout_cancel);
                                    yes = delDialog.findViewById(R.id.logout);

                                    txt.setText("Are you sure to delete this file?");
                                    no.setText("No");
                                    yes.setText("Yes");

                                    no.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            delDialog.dismiss();
                                        }
                                    });

                                    yes.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            filePath.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        lecturesRef.child(contentInput).child(idInput).removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){
                                                                            delDialog.dismiss();
                                                                            notifyItemRemoved(holder.getAdapterPosition());
                                                                            notifyItemRangeChanged(holder.getAdapterPosition(), files.size());
                                                                            Toast.makeText(holder.itemView.getContext(), "File is deleted successfully", Toast.LENGTH_SHORT).show();
                                                                        }else {
                                                                            String msg = task.getException().toString();
                                                                            Toast.makeText(holder.itemView.getContext(), "Error: "+ msg, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });

                                        }
                                    });
                                    break;
                            }
                        }
                    });
                    builder.show();
                }else if (entry.equals("admin_course") || entry.equals("student_course")){
                    CharSequence options[] = new CharSequence[]{
                            "Download and Open",
                            "Save to bookmark"
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle(holder.name.getText());

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(files.get(holder.getAdapterPosition()).getFile()));
                                    holder.itemView.getContext().startActivity(intent);
                                    break;
                                case 1:
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("saveAs", fileInput);
                                    hashMap.put("file", pdf);
                                    hashMap.put("userId", userId);
                                    hashMap.put("fileId", idInput);

                                    bookmarkRef.child(userId).child(idInput).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(holder.itemView.getContext(), fileInput +" is added to bookmark successfully.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    break;
                            }
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void delete(Lectures lectures){
        this.files.remove(lectures);
        notifyDataSetChanged();
    }

    public void add(Lectures lectures) {
        this.files.add(lectures);
        notifyItemInserted(files.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.file_name);
        }
    }
}
