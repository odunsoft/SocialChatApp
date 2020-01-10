package com.odunyazilim.socialcchatt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
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

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    DatabaseReference RootRef;

    private ProgressBar progressBarReg;
    private EditText usernameRegister, emailRegister, passwordRegister, cityRegister, ageRegister;
    private AppCompatSpinner spinnerCountryReg;
    private RadioGroup radioGroup;
    private AppCompatRadioButton radioButton;
    private AppCompatRadioButton radioFemale, radioMale;
    private Button registerButton;



    int progressDurum =0;
    private Handler handler =new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();



        radioGroup = findViewById(R.id.radioGroup);
        radioFemale = findViewById(R.id.radio_female);
        radioMale = findViewById(R.id.radio_male);


        progressBarReg = findViewById(R.id.progress_bar_reg);
        registerButton = findViewById(R.id.register_button);
        usernameRegister = findViewById(R.id.username_register);
        emailRegister = findViewById(R.id.email_register);
        passwordRegister = findViewById(R.id.password_register);
        cityRegister = findViewById(R.id.city_register);
        ageRegister = findViewById(R.id.age_register);





        //spinner
        spinnerCountryReg = findViewById(R.id.spinner_country_reg);

        ArrayAdapter<CharSequence> spinadapter = ArrayAdapter.createFromResource
                (this, R.array.countries_array, android.R.layout.simple_spinner_dropdown_item);

        spinnerCountryReg.setAdapter(spinadapter);
        //spinnerdan veriyi spinnerCountryReg.getSelectedItem ile alıcaz



        //firebasee eklemek icin radiobutton.getText diyeceksin
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);




        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateNewAccount();

            }
        });


    }  //onclick sonu



    private void CreateNewAccount() {

        final String rUsername = usernameRegister.getText().toString();
        final String rEmail = emailRegister.getText().toString();
        final String rPassword = passwordRegister.getText().toString();
        final String rCity = cityRegister.getText().toString();
        final String rAge =ageRegister.getText().toString();



        if (!TextUtils.isEmpty(rUsername) && !TextUtils.isEmpty(rEmail) && !TextUtils.isEmpty(rPassword)
                    && !TextUtils.isEmpty(rCity)
                        && !TextUtils.isEmpty(rAge))
        {


            progressDurum =0;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (progressDurum<100){

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                progressBarReg.setProgress(progressDurum);
                            }
                        });

                        try {

                            Thread.sleep(100);
                        } catch (InterruptedException e){

                            e.printStackTrace();

                        }
                    }
                }
            }).start();


            mAuth.createUserWithEmailAndPassword(rEmail,rPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if (task.isSuccessful()){

                        FirebaseUser currentUser = mAuth.getCurrentUser();


                        HashMap<String,Object> newUserMap = new HashMap<>();

                        newUserMap.put("profilename",rUsername);
                        newUserMap.put("profileimage","http://odunyazilim.com/pp/randomm.jpg");
                        newUserMap.put("city",rCity);
                        newUserMap.put("age",rAge);
                        newUserMap.put("gender",radioButton.getText());
                        newUserMap.put("country",spinnerCountryReg.getSelectedItem());
                        newUserMap.put("about","about");
                        newUserMap.put("email",rEmail);
                        newUserMap.put("uid",currentUser.getUid());
                        newUserMap.put("diamond","0");


                        RootRef.child("Users").child(currentUser.getUid()).setValue(newUserMap);




                        final String currentUserID = mAuth.getCurrentUser().getUid();

                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {


                                String deviceToken = instanceIdResult.getToken();

                                RootRef.child("Users").child(currentUserID).child("token")
                                        .setValue(deviceToken);


                            }
                        });






                        Toast.makeText(RegisterActivity.this, "Register is success", Toast.LENGTH_SHORT).show();

                        Intent allIntent =new Intent(RegisterActivity.this,AllActivity.class);
                        startActivity(allIntent);

                    }

                    else {

                        progressBarReg.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                    }

                }
            });

        }

        else {

            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();

        }


    }






    public void radioClicked(View v){

        int radioId = radioGroup.getCheckedRadioButtonId();

        radioButton = findViewById(radioId);

        //firebasee eklemek icin radiobutton.getText diyeceksin
    }



}
