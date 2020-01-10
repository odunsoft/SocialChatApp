package com.odunyazilim.socialcchatt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotSettingsActivity extends AppCompatActivity {


    private EditText settingForgotEmailEt;
    private Button settingForgotSendBtn;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_settings);


        settingForgotEmailEt=findViewById(R.id.setting_forgot_email_et);
        settingForgotSendBtn=findViewById(R.id.setting_forgot_send_btn);

        mAuth = FirebaseAuth.getInstance();

        settingForgotSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String femail = settingForgotEmailEt.getText().toString();


                if (!TextUtils.isEmpty(femail)){

                    mAuth.sendPasswordResetEmail(femail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                Toast.makeText(ForgotSettingsActivity.this, "We sent link to you. Check your emails", Toast.LENGTH_SHORT).show();

                                Intent forgotIntent = new Intent(ForgotSettingsActivity.this,AllActivity.class);
                                startActivity(forgotIntent);

                            }

                            else {

                                Toast.makeText(ForgotSettingsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }

                else {

                    Toast.makeText(ForgotSettingsActivity.this, "Please write your email", Toast.LENGTH_SHORT).show();

                }


            }
        });

    }
}
