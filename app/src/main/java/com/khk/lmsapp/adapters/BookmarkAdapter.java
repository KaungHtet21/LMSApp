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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khk.lmsapp.R;
import com.khk.lmsapp.modules.Bookmark;

import java.util.ArrayList;
import java.util.HashMap;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    ArrayList<Bookmark> files;
    DatabaseReference bookmarkRef;

    public BookmarkAdapter(){
        this.files = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_files_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String saveAsInput = files.get(position).getSaveAs();
        String fileInput = files.get(position).getFile();
        String userId = files.get(position).getUserId();
        String fileId = files.get(position).getFileId();

        bookmarkRef = FirebaseDatabase.getInstance().getReference().child("Bookmark");

        holder.name.setText(saveAsInput);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{
                        "Download and Open",
                        "Edit",
                        "Remove"
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
                                        hashMap.put("file", fileInput);
                                        hashMap.put("fileId", fileId);
                                        hashMap.put("saveAs", changeInput);
                                        hashMap.put("userId", userId);

                                        bookmarkRef.child(userId).child(fileId).updateChildren(hashMap)
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
                                        bookmarkRef.child(userId).child(fileId).removeValue()
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
                                });
                                break;
                        }
                    }
                });

                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void add(Bookmark files){
        this.files.add(files);
        notifyItemInserted(this.files.size());
    }

    public void remove(Bookmark files){
        this.files.remove(files);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout parent;
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parent = itemView.findViewById(R.id.files_parent);
            name = itemView.findViewById(R.id.file_name);
        }
    }
}
