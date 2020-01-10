package com.odunyazilim.socialcchatt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountSettingsActivity extends AppCompatActivity {


    private TextView settingPrivacyPolicy, settingTermsConditions, settingResetPassword;

    private Toolbar accountToolBar;

    Context context=this;


    FirebaseAuth mAuth;
    FirebaseUser fUser;
    DatabaseReference UsersRef;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);


        settingResetPassword = findViewById(R.id.setting_reset_password);


        accountToolBar= findViewById(R.id.toolbar_ac_set);
        setSupportActionBar(accountToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mAuth=FirebaseAuth.getInstance();
        fUser=mAuth.getCurrentUser();
        currentUserID=mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference("Users");



        settingResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent resetIntent = new Intent(AccountSettingsActivity.this,ForgotSettingsActivity.class);
                startActivity(resetIntent);


            }
        });



    }


}
