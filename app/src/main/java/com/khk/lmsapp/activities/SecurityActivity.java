package com.khk.lmsapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.khk.lmsapp.R;
import com.khk.lmsapp.SharePreferenceHelper;

public class SecurityActivity extends AppCompatActivity {

    ImageView back;
    RelativeLayout passCode, changePassCode;
    TextView passCodeController, chgPwController;
    SharePreferenceHelper sharePreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        back = findViewById(R.id.back);
        passCode = findViewById(R.id.pass_code_layout);
        changePassCode = findViewById(R.id.change_pass_code);
        passCodeController = findViewById(R.id.pass_code_controller);
        chgPwController = findViewById(R.id.changePwController);
        sharePreferenceHelper = new SharePreferenceHelper(this);

        if (sharePreferenceHelper.get(SharePreferenceHelper.IS_LOCK, false)){
            passCodeController.setText("On");
            chgPwController.setText("Unable");
        }else {
            passCodeController.setText("Off");
            chgPwController.setText("Disable");
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        passCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passCodeController.getText().toString().equals("Off")){
                    Intent intent = new Intent(SecurityActivity.this, PassCodeActivity.class);
                    intent.putExtra("isActivate", "Off");
                    startActivityForResult(intent, 1);
                }else if (passCodeController.getText().toString().equals("On")){
                    Intent intent = new Intent(SecurityActivity.this, PassCodeActivity.class);
                    intent.putExtra("isActivate", "On");
                    startActivityForResult(intent, 1);
                }
            }
        });

        changePassCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chgPwController.getText().toString().equals("Unable")){
                    Intent intent = new Intent(SecurityActivity.this, PassCodeActivity.class);
                    intent.putExtra("isActivate", "change");
                    startActivityForResult(intent,1);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == RESULT_OK && data != null){
                String isActivate = data.getStringExtra("isActivate");
                String isDisable = data.getStringExtra("isDisable");
                passCodeController.setText(isActivate);
                chgPwController.setText(isDisable);
            }
        }
    }
}