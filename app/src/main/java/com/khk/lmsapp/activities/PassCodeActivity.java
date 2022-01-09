package com.khk.lmsapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.khk.lmsapp.R;
import com.khk.lmsapp.SharePreferenceHelper;

public class PassCodeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView numb1, numb2, numb3, numb4, numb5, numb6, numb7, numb8, numb9, numb0, txt;
    private ImageView numbB, numbOk, numbOkConfirm;
    private View input1, input2, input3, input4;
    private int count = 1;
    private String firstInput, secondInput;
    private LinearLayout inputView;

    private static final String TAG = "PassCodeActivity";
    private SharePreferenceHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_code);

        init();
        String isActivate = getIntent().getStringExtra("isActivate");

        numb0.setOnClickListener(this);
        numb1.setOnClickListener(this);
        numb2.setOnClickListener(this);
        numb3.setOnClickListener(this);
        numb4.setOnClickListener(this);
        numb5.setOnClickListener(this);
        numb6.setOnClickListener(this);
        numb7.setOnClickListener(this);
        numb8.setOnClickListener(this);
        numb9.setOnClickListener(this);

        numbB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count != 1){
                    switch (count){
                        case 2:
                            input1.setBackgroundResource(R.drawable.bg_lock_edt);
                            input1.setTag(null);
                            break;
                        case 3:
                            input2.setBackgroundResource(R.drawable.bg_lock_edt);
                            input2.setTag(null);
                            break;
                        case 4:
                            input3.setBackgroundResource(R.drawable.bg_lock_edt);
                            input3.setTag(null);
                            break;
                        case 5:
                            input4.setBackgroundResource(R.drawable.bg_lock_edt);
                            input4.setTag(null);
                            break;
                    }
                    count--;
                }

            }
        });

        numbOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count == 5){
                    if (isActivate.equals("Off")){
                        firstInput = getPasswordFromView();
                        txt.setText("Re-enter password");

                        input1.setBackgroundResource(R.drawable.bg_lock_edt);
                        input2.setBackgroundResource(R.drawable.bg_lock_edt);
                        input3.setBackgroundResource(R.drawable.bg_lock_edt);
                        input4.setBackgroundResource(R.drawable.bg_lock_edt);
                        count = 1;

                        numbOk.setVisibility(View.GONE);
                        numbOkConfirm.setVisibility(View.VISIBLE);
                    }else if (isActivate.equals("On")){
                        firstInput = helper.get(SharePreferenceHelper.KEY_PASSWORD, null);
                        secondInput = getPasswordFromView();

                        if (firstInput.equals(secondInput)){
                            helper.delete(SharePreferenceHelper.IS_LOCK);
                            helper.delete(SharePreferenceHelper.KEY_PASSWORD);

                            Intent intent = new Intent();
                            intent.putExtra("isActivate", "Off");
                            intent.putExtra("isDisable", "Disable");
                            setResult(RESULT_OK, intent);
                            finish();
                        }else {
                            txt.setText("Password is wrong");
                            Vibrate();
                        }
                    }else if (isActivate.equals("start")){
                        firstInput = helper.get(SharePreferenceHelper.KEY_PASSWORD, null);
                        secondInput = getPasswordFromView();

                        if (firstInput.equals(secondInput)){
                            if (helper.get(SharePreferenceHelper.KEY_LOGIN, false)){
                                if (helper.get(SharePreferenceHelper.KEY_ENTRY, null).equals("admin")){
                                    Intent intent = new Intent(PassCodeActivity.this, AdminActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }

                                if (helper.get(SharePreferenceHelper.KEY_ENTRY, null).equals("teacher")){
                                    Intent intent = new Intent(PassCodeActivity.this, TeacherActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }

                                if (helper.get(SharePreferenceHelper.KEY_ENTRY, null).equals("student")){
                                    Intent intent = new Intent(PassCodeActivity.this, StudentActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }else {
                            txt.setText("Password is wrong");
                            Vibrate();
                        }
                    }else if (isActivate.equals("change")){
                        firstInput = helper.get(SharePreferenceHelper.KEY_PASSWORD, null);
                        secondInput = getPasswordFromView();

                        if (firstInput.equals(secondInput)){
                            txt.setText("Change password");

                            input1.setBackgroundResource(R.drawable.bg_lock_edt);
                            input2.setBackgroundResource(R.drawable.bg_lock_edt);
                            input3.setBackgroundResource(R.drawable.bg_lock_edt);
                            input4.setBackgroundResource(R.drawable.bg_lock_edt);
                            count = 1;

                            numbOk.setVisibility(View.GONE);
                            numbOkConfirm.setVisibility(View.VISIBLE);
                        }else {
                            txt.setText("Password is wrong");
                            Vibrate();
                        }
                    }
                }else {
                    Vibrate();
                }
            }
        });

        numbOkConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count == 5){
                    if (isActivate.equals("Off")){
                        secondInput = getPasswordFromView();
                        if (firstInput.equals(secondInput)){
                            helper.save(SharePreferenceHelper.IS_LOCK, true);
                            helper.save(SharePreferenceHelper.KEY_PASSWORD, secondInput);

                            Intent intent = new Intent();
                            intent.putExtra("isActivate", "On");
                            intent.putExtra("isDisable", "Unable");
                            setResult(RESULT_OK, intent);
                            finish();
                        }else {
                            txt.setText("Password does not match");
                            Vibrate();
                        }
                    }else if (isActivate.equals("change")){
                        secondInput = getPasswordFromView();
                        helper.save(SharePreferenceHelper.IS_LOCK, true);
                        helper.save(SharePreferenceHelper.KEY_PASSWORD, secondInput);

                        Intent intent = new Intent();
                        intent.putExtra("isActivate", "On");
                        intent.putExtra("isDisable","Unable");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }else {
                    Vibrate();
                }
            }
        });
    }

    private void init() {
        txt = findViewById(R.id.password_txt);

        numb0 = findViewById(R.id.number0);
        numb1 = findViewById(R.id.number1);
        numb2 = findViewById(R.id.number2);
        numb3 = findViewById(R.id.number3);
        numb4 = findViewById(R.id.number4);
        numb5 = findViewById(R.id.number5);
        numb6 = findViewById(R.id.number6);
        numb7 = findViewById(R.id.number7);
        numb8 = findViewById(R.id.number8);
        numb9 = findViewById(R.id.number9);
        numbB = findViewById(R.id.numberB);
        numbOk = findViewById(R.id.numberOK);
        numbOkConfirm = findViewById(R.id.numberOKConfirm);

        numb0.setTag(0);
        numb1.setTag(1);
        numb2.setTag(2);
        numb3.setTag(3);
        numb4.setTag(4);
        numb5.setTag(5);
        numb6.setTag(6);
        numb7.setTag(7);
        numb8.setTag(8);
        numb9.setTag(9);

        input1 = findViewById(R.id.pass_input_1);
        input2 = findViewById(R.id.pass_input_2);
        input3 = findViewById(R.id.pass_input_3);
        input4 = findViewById(R.id.pass_input_4);
        inputView = findViewById(R.id.pass_input_view);

        helper = new SharePreferenceHelper(PassCodeActivity.this);
    }

    private void Vibrate(){
        Animation shake = AnimationUtils.loadAnimation(PassCodeActivity.this, R.anim.shake);
        inputView.startAnimation(shake);
        Vibrator vibrator = (Vibrator) getSystemService(PassCodeActivity.VIBRATOR_SERVICE);
        vibrator.vibrate(300);
    }

    @Override
    public void onClick(View v) {
        Vibrator vibrator = (Vibrator) getSystemService(SetNameGroupActivity.VIBRATOR_SERVICE);
        vibrator.vibrate(80);

        if (count < 5){
            switch (count){
                case 1:
                    input1.setBackgroundResource(R.drawable.bg_lock_input);
                    input1.setTag(v.getTag());
                    break;
                case 2:
                    input2.setBackgroundResource(R.drawable.bg_lock_input);
                    input2.setTag(v.getTag());
                    break;
                case 3:
                    input3.setBackgroundResource(R.drawable.bg_lock_input);
                    input3.setTag(v.getTag());
                    break;
                case 4:
                    input4.setBackgroundResource(R.drawable.bg_lock_input);
                    input4.setTag(v.getTag());
                    break;
            }
            count++;
        }
    }

    private String getPasswordFromView(){
        String input1Result = input1.getTag().toString();
        String input2Result = input2.getTag().toString();
        String input3Result = input3.getTag().toString();
        String input4Result = input4.getTag().toString();
        return input1Result.concat(input2Result.concat(input3Result.concat(input4Result)));
    }
}