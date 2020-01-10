package com.odunyazilim.socialcchatt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AllActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    String saveCurrentDate,saveCurrentTime;

    DatabaseReference UsersRef;
    String currentUserID;

    Context context=this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);

        mAuth=FirebaseAuth.getInstance();

        currentUserID = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");




        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //acılınca dırekt home fragmenttan baslar
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();




    }


    @Override
    public void onBackPressed() {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Do you really want to exit?")
                .setCancelable(true)
                .setIcon(R.drawable.ic_close)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        finish();


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        dialog.cancel();


                    }
                }).show();



    }

    @Override
    protected void onStart() {

        super.onStart();

        if (currentUserID != null) {


            updateUserStatus("online");

        }
    }

    @Override
    protected void onStop() {

        super.onStop();

        if (currentUserID != null){

            updateUserStatus("offline");

        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (currentUserID != null){

            updateUserStatus("offline");

        }


    }




    private Fragment mHomeFragment = new HomeFragment();
    private Fragment mFriendsFragment = new FriendsFragment();
    private Fragment mChatFragment = new ChatFragment();
    private Fragment mProfileFragment = new ProfileFragment();


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment selectedFragment = null;


                    switch (item.getItemId()) {

                        case R.id.nav_home:

                            selectedFragment = mHomeFragment;

                            break;

                        case R.id.nav_friends:

                            selectedFragment = mFriendsFragment;

                            break;

                        case R.id.nav_chats:

                            selectedFragment = mChatFragment;



                            break;

                        case R.id.nav_profile:

                            selectedFragment = mProfileFragment;

                            break;


                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;

                }
            };



        private void updateUserStatus(String state){


            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
            saveCurrentDate =currentDate.format(calendar.getTime());


            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
            saveCurrentTime =currentTime.format(calendar.getTime());



            HashMap<String,Object> onlineStateMap = new HashMap<>();

            onlineStateMap.put("time",saveCurrentTime);
            onlineStateMap.put("date",saveCurrentDate);
            onlineStateMap.put("state",state);


            UsersRef.child(currentUserID).child("userState")
                    .updateChildren(onlineStateMap);


        }




}
