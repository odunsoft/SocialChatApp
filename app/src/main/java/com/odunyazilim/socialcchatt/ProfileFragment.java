package com.odunyazilim.socialcchatt;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {


    CircleImageView profileImage;
    TextView profileName, profileCountry, profileFriendCount, profileDiamondCount, profileGender, profileAge,profileCity;
    TextView editProfileTv, vipModeTv, accountSettingsTv, logoutTv, profileAbout;

    String currentUserID;
    FirebaseAuth mAuth;
    DatabaseReference UsersRef, ContactsRef;

    private int countFriend = 0;




    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profileCountry = view.findViewById(R.id.profile_country);
        editProfileTv = view.findViewById(R.id.edit_profile_tv);
        vipModeTv = view.findViewById(R.id.vip_mode_tv);
        accountSettingsTv = view.findViewById(R.id.account_settings_tv);
        logoutTv = view.findViewById(R.id.logout_tv);
        profileAbout = view.findViewById(R.id.profile_aboutt);
        profileFriendCount =view.findViewById(R.id.profile_friend_count);
        profileDiamondCount =view.findViewById(R.id.profile_diamond_count);
        profileGender =view.findViewById(R.id.profile_gender);
        profileAge =view.findViewById(R.id.profile_age);
        profileCity =view.findViewById(R.id.profile_city);


        mAuth=FirebaseAuth.getInstance();
        currentUserID =mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");


        ContactsRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()){

                    countFriend = (int) dataSnapshot.getChildrenCount();
                    profileFriendCount.setText(Integer.toString(countFriend));




                } else {

                    profileFriendCount.setText("0");


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        RetrieveUserInfo();








        editProfileTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent editIntent = new Intent(getContext(),EditProfileActivity.class);
                startActivity(editIntent);

            }
        });


        logoutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut();

                Intent laoIntent = new Intent(getContext(),MainActivity.class);
                startActivity(laoIntent);

            }
        });



        accountSettingsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent accountIntent = new Intent(getContext(),AccountSettingsActivity.class);
                startActivity(accountIntent);


            }
        });











        return view;

    }  //oncreate sonu




    private void RetrieveUserInfo() {


        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.hasChild("profileimage")){

                    String retrieveProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String retrieveProfileName = dataSnapshot.child("profilename").getValue().toString();
                    String retrieveProfileCountry = dataSnapshot.child("country").getValue().toString();
                    String retrieveProfileAbout = dataSnapshot.child("about").getValue().toString();
                    String retrieveProfileGender = dataSnapshot.child("gender").getValue().toString();
                    String retrieveProfileAge = dataSnapshot.child("age").getValue().toString();
                    String retrieveProfileCity = dataSnapshot.child("city").getValue().toString();
                    String retrieveProfileDiamond = dataSnapshot.child("diamond").getValue().toString();

                    profileName.setText(retrieveProfileName);
                    profileCountry.setText(retrieveProfileCountry);
                    profileAbout.setText(retrieveProfileAbout);
                    profileAge.setText(retrieveProfileAge);
                    profileGender.setText(retrieveProfileGender);
                    profileCity.setText(retrieveProfileCity);
                    profileDiamondCount.setText(retrieveProfileDiamond);

                    Picasso.get().load(retrieveProfileImage).placeholder(R.drawable.randommmmm).into(profileImage);


                } else if (dataSnapshot.exists() && dataSnapshot.hasChild("profilename")){

                    String retrieveProfileName = dataSnapshot.child("profilename").getValue().toString();
                    String retrieveProfileCountry = dataSnapshot.child("country").getValue().toString();
                    String retrieveProfileAbout = dataSnapshot.child("about").getValue().toString();
                    String retrieveProfileGender = dataSnapshot.child("gender").getValue().toString();
                    String retrieveProfileAge = dataSnapshot.child("age").getValue().toString();
                    String retrieveProfileCity = dataSnapshot.child("city").getValue().toString();
                    String retrieveProfileDiamond = dataSnapshot.child("diamond").getValue().toString();

                    profileName.setText(retrieveProfileName);
                    profileCountry.setText(retrieveProfileCountry);
                    profileAbout.setText(retrieveProfileAbout);
                    profileAge.setText(retrieveProfileAge);
                    profileGender.setText(retrieveProfileGender);
                    profileCity.setText(retrieveProfileCity);
                    profileDiamondCount.setText(retrieveProfileDiamond);


                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
