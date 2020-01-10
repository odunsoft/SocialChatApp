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

public class ForgetPasswordActivity extends AppCompatActivity {


    private EditText forgotEmailEt;
    private Button forgotSendBtn;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);


        forgotEmailEt = findViewById(R.id.forgot_email_et);
        forgotSendBtn = findViewById(R.id.forgot_send_btn);


        mAuth = FirebaseAuth.getInstance();


        forgotSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String femail = forgotEmailEt.getText().toString();


                if (!TextUtils.isEmpty(femail)){

                    mAuth.sendPasswordResetEmail(femail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                Toast.makeText(ForgetPasswordActivity.this, "We sent link to you. Check your emails", Toast.LENGTH_SHORT).show();

                                Intent forgotIntent = new Intent(ForgetPasswordActivity.this,MainActivity.class);
                                startActivity(forgotIntent);

                            }

                            else {

                                Toast.makeText(ForgetPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }

                else {

                    Toast.makeText(ForgetPasswordActivity.this, "Please write your email", Toast.LENGTH_SHORT).show();

                }

            }
        });


    }
}
