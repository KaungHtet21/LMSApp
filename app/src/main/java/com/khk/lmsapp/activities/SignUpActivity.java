package com.khk.lmsapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.khk.lmsapp.R;
import com.khk.lmsapp.SharePreferenceHelper;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    Toolbar toolbar;
    Spinner months,days, years, gender, role;
    EditText first_name, last_name, street, city, state, phone, email, pass, confirm;
    CardView first_name_cv, last_name_cv, gender_cv, street_cv, city_cv, state_cv, phone_cv, email_cv, pass_cv, confirm_cv;
    Button register, clear;
    String first_name_input, last_name_input, street_input, city_input, state_input, phone_input, email_input, pass_input, confirm_input, user_name, user_bd, user_gender, user_role, user_address;
    CircleImageView profile;
    CountryCodePicker ccp;

    ProgressDialog loadingBar;
    Uri imageUri;
    String downloadUrl;
    private SharePreferenceHelper sharePreferenceHelper;

    FirebaseAuth auth;
    String currentUserId;
    DatabaseReference adminRef, adminKeyRef, studentsReqRef, teachersReqRef, studentsKeyRef, teachersKeyRef, tempRef, userRef, userKeyRef;
    StorageReference profileRef;

    private static final int PICK_IMAGE = 1;
    private static final String TAG = "SignUpActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        toolbar = findViewById(R.id.sign_up_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Sign Up");

        init();
        ccp.registerCarrierNumberEditText(phone);

        auth = FirebaseAuth.getInstance();
        adminRef = FirebaseDatabase.getInstance().getReference().child("Admins");
        studentsReqRef = FirebaseDatabase.getInstance().getReference().child("Students Request");
        teachersReqRef = FirebaseDatabase.getInstance().getReference().child("Teachers Request");
        tempRef = FirebaseDatabase.getInstance().getReference().child("Images");
        profileRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        sharePreferenceHelper = new SharePreferenceHelper(this);

        days = findViewById(R.id.bd_days_spinner);
        Integer[] days_items = new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30};
        ArrayAdapter<Integer> days_adapter = new ArrayAdapter<Integer>(this, R.layout.support_simple_spinner_dropdown_item, days_items);
        days.setAdapter(days_adapter);

        years = findViewById(R.id.bd_years_spinner);
        Integer[] years_items = new Integer[]{2003,2002,2001,2000,1999,1998,1997,1996,1995,1994,1993,1992,1991,1990,1989,1988,1987,1986,1985,1984,1983,1982,1981,1980,
        1979,1978,1977,1976,1975,1974,1973,1972,1971,1970};
        ArrayAdapter<Integer> years_adapter = new ArrayAdapter<Integer>(this, R.layout.support_simple_spinner_dropdown_item, years_items);
        years.setAdapter(years_adapter);

        first_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    first_name_cv.setForeground(getDrawable(R.drawable.bg_cardview_blue_border));
                }else {
                    first_name_cv.setForeground(null);
                }
            }
        });

        last_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    last_name_cv.setForeground(getDrawable(R.drawable.bg_cardview_blue_border));
                }else {
                    last_name_cv.setForeground(null);
                }
            }
        });

        street.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    street_cv.setForeground(getDrawable(R.drawable.bg_cardview_blue_border));
                }else {
                    street_cv.setForeground(null);
                }
            }
        });

        city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    city_cv.setForeground(getDrawable(R.drawable.bg_cardview_blue_border));
                }else {
                    city_cv.setForeground(null);
                }
            }
        });

        state.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    state_cv.setForeground(getDrawable(R.drawable.bg_cardview_blue_border));
                }else {
                    state_cv.setForeground(null);
                }
            }
        });

        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    phone_cv.setForeground(getDrawable(R.drawable.bg_cardview_blue_border));
                }else {
                    phone_cv.setForeground(null);
                }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    email_cv.setForeground(getDrawable(R.drawable.bg_cardview_blue_border));
                }else {
                    email_cv.setForeground(null);
                }
            }
        });

        pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    pass_cv.setForeground(getDrawable(R.drawable.bg_cardview_blue_border));
                }else {
                    pass_cv.setForeground(null);
                }
            }
        });

        confirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    confirm_cv.setForeground(getDrawable(R.drawable.bg_cardview_blue_border));
                }else {
                    confirm_cv.setForeground(null);
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
                Toast.makeText(SignUpActivity.this, "Clear", Toast.LENGTH_SHORT).show();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    int entry = role.getSelectedItemPosition();
                    if (entry == 0){
                        AdminRegister();
                    }else if (entry == 1 || entry == 2){
                        StudentRegister();
                    }else {
                        TeacherRegister();
                    }
                }
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            profile.setImageURI(imageUri);
        }
    }

    private void reset() {
        first_name.setText("");
        last_name.setText("");
        months.setSelection(0);
        days.setSelection(0);
        years.setSelection(0);
        gender.setSelection(0);
        street.setText("");
        city.setText("");
        state.setText("");
        phone.setText("");
        email.setText("");
        pass.setText("");
        confirm.setText("");
        role.setSelection(0);
    }

    private void TeacherRegister() {
        String getTeacherId = teachersReqRef.push().getKey();

        HashMap<String, Object> teacherIdKey = new HashMap<>();
        teachersReqRef.updateChildren(teacherIdKey);

        teachersKeyRef = teachersReqRef.child(getTeacherId);

        HashMap<String, Object> teachers = new HashMap<>();
        teachers.put("name", user_name);
        teachers.put("bd", user_bd);
        teachers.put("gender", user_gender);
        teachers.put("address", user_address);
        teachers.put("ph", phone_input);
        teachers.put("email", email_input);
        teachers.put("password", pass_input);
        teachers.put("role", user_role);
        teachers.put("id", getTeacherId);

        StorageReference filePath = profileRef.child(email_input + ".jpg");
        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadUrl = uri.toString();
                        teachersKeyRef.child("image").setValue(downloadUrl);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = e.toString();
                Toast.makeText(SignUpActivity.this, "Error: "+ error, Toast.LENGTH_LONG).show();
            }
        });

        teachersKeyRef.updateChildren(teachers);
        Toast.makeText(this, "Thanks for registration", Toast.LENGTH_SHORT).show();
        reset();
    }

    private void StudentRegister() {
        String getStudentId = studentsReqRef.push().getKey();

        HashMap<String, Object> studentIdKey = new HashMap<>();
        studentsReqRef.updateChildren(studentIdKey);

        studentsKeyRef = studentsReqRef.child(getStudentId);

        StorageReference filePath = profileRef.child(email_input + ".jpg");
        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadUrl = uri.toString();
                        studentsKeyRef.child("image").setValue(downloadUrl);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = e.toString();
                Toast.makeText(SignUpActivity.this, "Error: "+ error, Toast.LENGTH_LONG).show();
            }
        });

        HashMap<String, Object> students = new HashMap<>();
        students.put("name", user_name);
        students.put("bd", user_bd);
        students.put("gender", user_gender);
        students.put("address", user_address);
        students.put("ph", phone_input);
        students.put("email", email_input);
        students.put("password", pass_input);
        students.put("role", user_role);
        students.put("id", getStudentId);

        studentsKeyRef.updateChildren(students);
        Toast.makeText(this, "Thanks for registration", Toast.LENGTH_SHORT).show();
        reset();
    }

    private void AdminRegister() {
        Dialog dialog = new Dialog(SignUpActivity.this);
        dialog.setContentView(R.layout.dialog_register);

        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(width, height);
        dialog.show();

        EditText code = dialog.findViewById(R.id.admin_code_edt);
        TextView cancel = dialog.findViewById(R.id.cancel_admin_code);
        Button insert = dialog.findViewById(R.id.insert_admin_code);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code_input = code.getText().toString();
                if (TextUtils.isEmpty(code_input)){
                    Toast.makeText(SignUpActivity.this, "Enter code", Toast.LENGTH_SHORT).show();
                }else if (!code_input.equals("130801")){
                    Toast.makeText(SignUpActivity.this, "Code is wrong", Toast.LENGTH_SHORT).show();
                    code.setText("");
                }else {
                    dialog.dismiss();

                    loadingBar.setTitle("Creating account");
                    loadingBar.setMessage("Please wait");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.setCancelable(false);
                    loadingBar.show();

                    auth.createUserWithEmailAndPassword(email_input, pass_input)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        currentUserId = auth.getCurrentUser().getUid();
                                        adminKeyRef = adminRef.child(currentUserId);
                                        userKeyRef = userRef.child(currentUserId);
                                        StorageReference filePath = profileRef.child(email_input + ".jpg");

                                        HashMap<String, Object> admins = new HashMap<>();
                                        admins.put("name", user_name);
                                        admins.put("bd", user_bd);
                                        admins.put("gender", user_gender);
                                        admins.put("address", user_address);
                                        admins.put("ph", phone_input);
                                        admins.put("email", email_input);
                                        admins.put("password", pass_input);
                                        admins.put("role", user_role);
                                        admins.put("id", currentUserId);

                                        adminKeyRef.setValue(admins)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            userKeyRef.setValue(admins)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){
                                                                                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                    @Override
                                                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                            @Override
                                                                                            public void onSuccess(Uri uri) {
                                                                                                downloadUrl = uri.toString();
                                                                                                userKeyRef.child("image").setValue(downloadUrl);
                                                                                                adminKeyRef.child("image").setValue(downloadUrl)
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                loadingBar.dismiss();

                                                                                                                sharePreferenceHelper.save(SharePreferenceHelper.KEY_LOGIN, true);
                                                                                                                sharePreferenceHelper.save(SharePreferenceHelper.KEY_ENTRY, "admin");

                                                                                                                Intent intent = new Intent(SignUpActivity.this, AdminActivity.class);
                                                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                                                startActivity(intent);
                                                                                                                finish();
                                                                                                                Toast.makeText(SignUpActivity.this, "Create account successfully", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                    }else {
                                        String msg = task.getException().toString();
                                        Toast.makeText(SignUpActivity.this, "Error: "+ msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private boolean validate() {
        String month_input = months.getSelectedItem().toString();
        String day_input = days.getSelectedItem().toString();
        String year_input = years.getSelectedItem().toString();

        first_name_input = first_name.getText().toString();
        last_name_input = last_name.getText().toString();
        street_input = street.getText().toString();
        city_input = city.getText().toString();
        state_input = state.getText().toString();
        phone_input = ccp.getFullNumberWithPlus();
        email_input = email.getText().toString().trim();
        pass_input = pass.getText().toString();
        confirm_input = confirm.getText().toString();

        user_name = first_name_input.concat(" ").concat(last_name_input);
        user_bd = month_input.concat("/").concat(day_input).concat("/").concat(year_input);
        user_gender = gender.getSelectedItem().toString();
        user_role = role.getSelectedItem().toString();
        user_address = street_input.concat("\n").concat(city_input).concat("/").concat(state_input);

        if (imageUri == null){
            Toast.makeText(this, "Profile image is required!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(first_name_input)){
            first_name_cv.setForeground(getDrawable(R.drawable.bg_cardview_red_border));
            Toast.makeText(this, "Enter first name", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            first_name_cv.setForeground(null);
        }

        if (TextUtils.isEmpty(street_input)){
            street_cv.setForeground(getDrawable(R.drawable.bg_cardview_red_border));
            Toast.makeText(this, "Enter street address", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            street_cv.setForeground(null);
        }

        if (TextUtils.isEmpty(city_input)){
            city_cv.setForeground(getDrawable(R.drawable.bg_cardview_red_border));
            Toast.makeText(this, "Enter city name", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            city_cv.setForeground(null);
        }

        if (TextUtils.isEmpty(state_input)){
            state_cv.setForeground(getDrawable(R.drawable.bg_cardview_red_border));
            Toast.makeText(this, "Enter state name", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            state_cv.setForeground(null);
        }

        if (TextUtils.isEmpty(phone_input)){
            phone_cv.setForeground(getDrawable(R.drawable.bg_cardview_red_border));
            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
            return false;
        }else if (phone.getText().length() < 9){
            phone_cv.setForeground(getDrawable(R.drawable.bg_cardview_red_border));
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }else  {
            phone_cv.setForeground(null);
        }

        if (TextUtils.isEmpty(email_input)){
            email_cv.setForeground(getDrawable(R.drawable.bg_cardview_red_border));
            Toast.makeText(this, "Enter email address", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            email_cv.setForeground(null);
        }

        if (TextUtils.isEmpty(pass_input)){
            pass_cv.setForeground(getDrawable(R.drawable.bg_cardview_red_border));
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            pass_cv.setForeground(null);
        }

        if (TextUtils.isEmpty(confirm_input)){
            confirm_cv.setForeground(getDrawable(R.drawable.bg_cardview_red_border));
            Toast.makeText(this, "Enter confirm password", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            confirm_cv.setForeground(null);
        }

        if (pass_input.length() < 6){
            pass_cv.setForeground(getDrawable(R.drawable.bg_cardview_red_border));
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            pass_cv.setForeground(null);
        }

        if (!confirm_input.equals(pass_input)){
            confirm_cv.setForeground(getDrawable(R.drawable.bg_cardview_red_border));
            Toast.makeText(this, "Password must be the same", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            confirm_cv.setForeground(null);
        }

        return true;
    }

    private void init() {
        months = findViewById(R.id.bd_months_spinner);

        gender = findViewById(R.id.gender_spinner);
        gender_cv = findViewById(R.id.gender_cv);

        first_name = findViewById(R.id.first_name_edt);
        first_name_cv = findViewById(R.id.first_name_cv);

        last_name = findViewById(R.id.last_name_edt);
        last_name_cv = findViewById(R.id.last_name_cv);

        street = findViewById(R.id.street_address_edt);
        street_cv = findViewById(R.id.street_address_cv);

        city = findViewById(R.id.city_address_edt);
        city_cv = findViewById(R.id.city_address_cv);

        state = findViewById(R.id.state_address_edt);
        state_cv = findViewById(R.id.state_address_cv);

        phone = findViewById(R.id.phone_edt);
        phone_cv = findViewById(R.id.phone_cv);

        email = findViewById(R.id.email_address_edt);
        email_cv = findViewById(R.id.email_address_cv);

        pass = findViewById(R.id.pass_edt);
        pass_cv = findViewById(R.id.pass_cv);

        confirm = findViewById(R.id.confirm_pass_edt);
        confirm_cv = findViewById(R.id.confirm_pass_cv);

        role = findViewById(R.id.role_spinner);

        register = findViewById(R.id.register_btn);
        clear = findViewById(R.id.clear_btn);

        loadingBar = new ProgressDialog(this);

        profile = findViewById(R.id.sign_up_profile_picture);

        ccp = findViewById(R.id.ccp);
    }
}