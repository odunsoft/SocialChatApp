package com.odunyazilim.socialcchatt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.odunyazilim.socialcchatt.notifications.Token;

public class MainActivity extends AppCompatActivity {

    String mUID;

       private ProgressBar progressBar;
    int progressDurum =0;
    private Handler handler =new Handler();


    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView registerNow, forgotPassword;

    private FirebaseAuth mAuth;

    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressBar = findViewById(R.id.progress_bar);
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        registerNow = findViewById(R.id.register_now);
        forgotPassword = findViewById(R.id.forgot_password);

        mAuth=FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent forgotIntent = new Intent(MainActivity.this,ForgetPasswordActivity.class);
                startActivity(forgotIntent);

            }
        });

        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerIntent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(registerIntent);

            }
        });



        //eger kullanıcı varsa dırekt All' a gönder
        if (mAuth.getCurrentUser() != null){


            mUID = user.getUid();

            //update token
            updateToken(FirebaseInstanceId.getInstance().getToken());

            //save uid of currently signed in user in shared preference
            SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID",mUID);
            editor.apply();



            Intent intent = new Intent(MainActivity.this, AllActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }




        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginWithAccount();
            }
        });






    }  //oncreate sonu




    public void updateToken(final String token){

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                String token =instanceIdResult.getToken();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
                Token mToken = new Token(token);
                ref.child(mUID).setValue(mToken);

            }
        });




    }







    private void LoginWithAccount() {

        String lemail = loginEmail.getText().toString();
        String lpassword = loginPassword.getText().toString();


        if (!TextUtils.isEmpty(lemail) && !TextUtils.isEmpty(lpassword)){


            progressDurum = 0;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (progressDurum <100){

                        progressDurum +=1;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                progressBar
                                        .setProgress(progressDurum);
                            }
                        });

                        try {

                            Thread.sleep(100);

                        }
                        catch (InterruptedException e){
                            e.printStackTrace();

                        }
                    }
                }
            }).start();
            //progressbar metodu sonu



            mAuth.signInWithEmailAndPassword(lemail,lpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if (task.isSuccessful()){


                        final String currentUserID = mAuth.getCurrentUser().getUid();


                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {

                                //cihaz idsini alıyoruz bu metod ıcınde(new version)
                                String deviceToken = instanceIdResult.getToken();


                                UsersRef.child(currentUserID).child("token")
                                        .setValue(deviceToken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {


                                                if (task.isSuccessful()){

                                                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();

                                                    Intent logIntent = new Intent(MainActivity.this,AllActivity.class);
                                                    startActivity(logIntent);
                                                    finish();

                                                }

                                            }
                                        });

                            }
                        });


                    } else {

                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();


                    }

                }
            });

        }
        else {


            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();

        }


    }
}
