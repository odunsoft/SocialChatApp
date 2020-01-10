package com.odunyazilim.socialcchatt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class UserActivity extends AppCompatActivity {


    private String receiverUserID,Current_State, senderUserID;
    FirebaseAuth mAuth;

    private Toolbar toolbarUser;

    private CardView cardView4;

    private Button followButton, declineButton;
    private ImageView userProfileImage;
    private TextView userProfileGender, userProfileAge, userProfileName, userProfileCountry, userProfileCity, userProfileAbout;

    private TextView userFriendCount, userDiamondCount;
    private DatabaseReference UsersRef, ChatReqRef, ContactsRef, NotificationsRef;

    private int countFriend = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();

        toolbarUser =findViewById(R.id.user_toolbar);
        setSupportActionBar(toolbarUser);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mAuth=FirebaseAuth.getInstance();
        senderUserID=mAuth.getCurrentUser().getUid();


        followButton = findViewById(R.id.follow_button);
        declineButton = findViewById(R.id.decline_button);

        userProfileImage = findViewById(R.id.user_profile_image);
        userProfileGender = findViewById(R.id.user_profile_gender);
        userProfileAge = findViewById(R.id.user_profile_age);
        userProfileName = findViewById(R.id.user_profile_name);
        userProfileCountry = findViewById(R.id.user_profile_country);
        userProfileCity = findViewById(R.id.user_profile_city);
        userProfileAbout = findViewById(R.id.user_profile_about);
        cardView4=findViewById(R.id.cardview4);

        userFriendCount = findViewById(R.id.user_friend_count);
        userDiamondCount = findViewById(R.id.user_diamond_count);

        Current_State ="new";


        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatReqRef =FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef =FirebaseDatabase.getInstance().getReference().child("Contacts");
        NotificationsRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        RetrieveUserInfo();






        //friend sayısını alıyoruz kullanıcının
        ContactsRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){


                    countFriend = (int) dataSnapshot.getChildrenCount();
                    userFriendCount.setText(Integer.toString(countFriend));



                } else {

                    userFriendCount.setText("0");


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }  //oncreate sonu







    private void RetrieveUserInfo() {


        UsersRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.hasChild("profileimage")){


                    String retrieveUsImage = dataSnapshot.child("profileimage").getValue().toString();
                    String retrieveUsName = dataSnapshot.child("profilename").getValue().toString();
                    String retrieveUsAge = dataSnapshot.child("age").getValue().toString();
                    String retrieveUsCountry = dataSnapshot.child("country").getValue().toString();
                    String retrieveUsCity = dataSnapshot.child("city").getValue().toString();
                    String retrieveUsAbout = dataSnapshot.child("about").getValue().toString();
                    String retrieveUsGender = dataSnapshot.child("gender").getValue().toString();
                    String retrieveUsDiamond = dataSnapshot.child("diamond").getValue().toString();

                    userProfileName.setText(retrieveUsName);
                    userProfileAge.setText(retrieveUsAge);
                    userProfileCountry.setText(retrieveUsCountry);
                    userProfileCity.setText(retrieveUsCity);
                    userProfileAbout.setText(retrieveUsAbout);
                    userProfileGender.setText(retrieveUsGender);
                    toolbarUser.setTitle(retrieveUsName);
                    userDiamondCount.setText(retrieveUsDiamond);

                    Picasso.get().load(retrieveUsImage).placeholder(R.drawable.randommmmm).into(userProfileImage);


                    ManageChatRequest();



                } else  {

                    String retrieveUsName = dataSnapshot.child("profilename").getValue().toString();
                    String retrieveUsAge = dataSnapshot.child("age").getValue().toString();
                    String retrieveUsCountry = dataSnapshot.child("country").getValue().toString();
                    String retrieveUsCity = dataSnapshot.child("city").getValue().toString();
                    String retrieveUsAbout = dataSnapshot.child("about").getValue().toString();
                    String retrieveUsGender = dataSnapshot.child("gender").getValue().toString();
                    String retrieveUsDiamond = dataSnapshot.child("diamond").getValue().toString();

                    userProfileName.setText(retrieveUsName);
                    userProfileAge.setText(retrieveUsAge);
                    userProfileCountry.setText(retrieveUsCountry);
                    userProfileCity.setText(retrieveUsCity);
                    userProfileAbout.setText(retrieveUsAbout);
                    userProfileGender.setText(retrieveUsGender);
                    toolbarUser.setTitle(retrieveUsName);
                    userDiamondCount.setText(retrieveUsDiamond);


                    ManageChatRequest();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void ManageChatRequest() {


        //eger ıstek gonderıldıyse hafızada kalsın
        ChatReqRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(receiverUserID)){

                    //sender' ın receiver adında childi varsa
                    //yanı ıstek gondermısse ve Chatre request ref' te istek oluşmuşsa
                    //receiverIdnın altında request_type oluşmuşsa

                    String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();

                    //eger request_type= sent'e eşitse (request_type veritabanındaki child)

                    if (request_type.equals("sent")){

                        Current_State="request_sent";
                        followButton.setText("Cancel");

                    }

                    else if (request_type.equals("received")){

                        //alıcının gorecegı kısım için

                        Current_State="request_received";
                        followButton.setText("Accept Request");

                        declineButton.setVisibility(View.VISIBLE);
                        declineButton.setEnabled(true);


                        declineButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CancelChatRequest();


                            }
                        });

                    }
                }

                else {

                    ContactsRef.child(senderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            if (dataSnapshot.hasChild(receiverUserID)){

                                Current_State ="friends";
                                followButton.setText("Remove Friend");


                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //eger alıcı ve gonderıcı aynı kısı degılse
        if (!senderUserID.equals(receiverUserID)){


            followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //eger followa tıkladıysa artık onu false yapmalıyız
                    //(followa tıkladı cunku setonclick olayının ıcındeyız
                    //arık buttonun kullanılırlıgını false yapabilirz
                    followButton.setEnabled(false);


                    if (Current_State.equals("new")){

                        //eger ilk kez istek gonderılıyorsa
                        SendChatRequest();
                    }

                    if (Current_State.equals("request_sent")){

                        //eger istek zaten gonderıldıyse ve silmek istiyorsak
                        //istek gonderıldıgı ıcın buttonda cancel yazıo ya eeger cancela tıklarsk
                        CancelChatRequest();

                    }

                    if (Current_State.equals("request_received")){

                        //eger alıcı ıstek aldıysa

                        AcceptChatRequest();

                    }

                    if (Current_State.equals("friends")){


                        RemoveContact();

                    }

                }
            });

        }

        else
            {

            //eger kullanıcı bensem kendı profılımde buttonlar gorunmıcek
            //recyclervıewde kendı profılım de cıkıyor cunku
            followButton.setVisibility(View.INVISIBLE);
            cardView4.setVisibility(View.GONE);

        }
    }



    private void RemoveContact() {


        //gonderdıgınn ıstegı silmek için

        ContactsRef.child(senderUserID).child(receiverUserID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if (task.isSuccessful()){

                    ContactsRef.child(receiverUserID).child(senderUserID)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            if (task.isSuccessful()){

                                followButton.setEnabled(true);
                                Current_State ="new";
                                followButton.setText("Follow");

                                declineButton.setVisibility(View.GONE);
                                declineButton.setEnabled(false);

                            }

                        }
                    });

                }

            }
        });





    }


    private void AcceptChatRequest() {


        //ıstegı kabul edılen kısılerden yenı bır Contact lıste olusturuyoruz
        //bunu da frıends fragmentta gostercez
        //hem sendera hem receivera arkadas listesinde gorunecek
        //o yuzden her ıkısıne bırden eklıyoruz

        ContactsRef.child(senderUserID).child(receiverUserID)
                .child("Contacts").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        if (task.isSuccessful()){

                            ContactsRef.child(receiverUserID).child(senderUserID)
                                    .child("Contacts").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                    //istek kabul edıldıkten sonra Chat Requestten sılmelıyız

                                    ChatReqRef.child(senderUserID).child(receiverUserID)
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    ChatReqRef.child(receiverUserID).child(senderUserID)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {


                                                                    followButton.setEnabled(true);
                                                                    Current_State = "friends";
                                                                    followButton.setText("Remove Friend");

                                                                    declineButton.setVisibility(View.GONE);
                                                                    declineButton.setEnabled(false);



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



    private void CancelChatRequest() {


        //gonderdıgınn ıstegı silmek için

        ChatReqRef.child(senderUserID).child(receiverUserID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if (task.isSuccessful()){

                    ChatReqRef.child(receiverUserID).child(senderUserID)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            if (task.isSuccessful()){

                                followButton.setEnabled(true);
                                Current_State ="new";
                                followButton.setText("Follow");

                                declineButton.setVisibility(View.GONE);
                                declineButton.setEnabled(false);

                            }



                        }
                    });


                }




            }
        });



    }



    private void SendChatRequest() {


        ChatReqRef.child(senderUserID).child(receiverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            ChatReqRef.child(receiverUserID).child(senderUserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {


                                            if (task.isSuccessful()){


                                                HashMap<String,String> chatNotifMap = new HashMap<>();

                                                chatNotifMap.put("sender",senderUserID);
                                                chatNotifMap.put("type","request");


                                                NotificationsRef.child(receiverUserID).push()
                                                        .setValue(chatNotifMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {


                                                                if (task.isSuccessful()){

                                                                    followButton.setEnabled(true);
                                                                    Current_State="request_sent";
                                                                    followButton.setText("Cancel");

                                                                }


                                                            }
                                                        });

                                            }

                                        }
                                    });

                        }



                    }
                });


    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.toolbar_user_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        int id = item.getItemId();


        return super.onOptionsItemSelected(item);


    }
}
