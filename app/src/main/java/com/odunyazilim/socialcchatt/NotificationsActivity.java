package com.odunyazilim.socialcchatt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.odunyazilim.socialcchatt.Model.Friend;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.odunyazilim.socialcchatt.AppNotif.CHANNEL_1_ID;


public class NotificationsActivity extends AppCompatActivity {


    private RecyclerView recyclerNotifications;

    private DatabaseReference ChatReqRef, UsersRef, ContactRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    private NotificationManagerCompat notificationManagerCompat;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);


        recyclerNotifications = findViewById(R.id.recycler_notifications);
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));

        mAuth =FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatReqRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");


        notificationManagerCompat = NotificationManagerCompat.from(this);


    } //oncreate snu



    @Override
    protected void onStart() {

        super.onStart();



        FirebaseRecyclerOptions<Friend> options =
                new FirebaseRecyclerOptions.Builder<Friend>()
                .setQuery(ChatReqRef.child(currentUserID),Friend.class)
                .build();



        FirebaseRecyclerAdapter<Friend,RequestViewHolder> adapter =
                new FirebaseRecyclerAdapter<Friend, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Friend model) {


                        //once buttonları gorunur yapıyoruz
                        holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);



                        //buradan alcagımız id yi kullanarak Users nodeundan kullanıcının
                        //dıger bilgilerini alıyoruz
                        //tıklanan hangısıyse onun uidsini aldık

                        //chat_req_ids => receiver user id' dir
                        final String chat_req_ids = getRef(position).getKey();

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent notifIntent = new Intent(NotificationsActivity.this,UserActivity.class);
                                notifIntent.putExtra("visit_user_id",chat_req_ids);
                                startActivity(notifIntent);


                            }
                        });

                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();


                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                if (dataSnapshot.exists()){

                                    String type = dataSnapshot.getValue().toString();


                                    if (type.equals("received")){




                                        Intent activityIntent = new Intent(NotificationsActivity.this,NotificationsActivity.class);
                                        PendingIntent contentIntent = PendingIntent.getActivity(NotificationsActivity.this,
                                                0,activityIntent, 0);


                                        Intent broadcastIntent = new Intent(NotificationsActivity.this,NotificationReceiver.class);


                                        PendingIntent actionIntent = PendingIntent.getBroadcast(NotificationsActivity.this,0,broadcastIntent,PendingIntent.FLAG_UPDATE_CURRENT);



                                        Notification notification = new NotificationCompat.Builder(NotificationsActivity.this,CHANNEL_1_ID)
                                                .setSmallIcon(R.drawable.ic_notifications)
                                                .setContentTitle("You Have Friend Request")
                                                .setContentText("Lucky day You have a friend request ")
                                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                                .setColor(Color.BLUE)
                                                .setContentIntent(contentIntent)
                                                .setAutoCancel(true)
                                                .setOnlyAlertOnce(true)
                                                .addAction(R.drawable.ic_launcher_background,"Toast",actionIntent)
                                                .build();

                                        notificationManagerCompat.notify(1,notification);















                                        UsersRef.child(chat_req_ids).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                                if (dataSnapshot.hasChild("profileimage")){

                                                    String retrieveReqImage = dataSnapshot.child("profileimage").getValue().toString();
                                                    String retrieveReqName = dataSnapshot.child("profilename").getValue().toString();
                                                    String retrieveReqCountry = dataSnapshot.child("country").getValue().toString();


                                                    holder.notProfileName.setText(retrieveReqName);
                                                    holder.notProfileCountry.setText("Wants to connect");

                                                    Picasso.get().load(retrieveReqImage).placeholder(R.drawable.ic_profile).into(holder.notProfileImage);



                                                } else {


                                                    String retrieveReqName = dataSnapshot.child("profilename").getValue().toString();
                                                    String retrieveReqCountry = dataSnapshot.child("country").getValue().toString();


                                                    holder.notProfileName.setText(retrieveReqName);
                                                    holder.notProfileCountry.setText(retrieveReqCountry);


                                                }

                                                holder.requestAcceptBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {


                                                        ContactRef.child(currentUserID).child(chat_req_ids).child("Contact")
                                                                .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {


                                                                if (task.isSuccessful()){

                                                                    ContactRef.child(chat_req_ids).child(currentUserID).child("Contact")
                                                                            .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {


                                                                            if (task.isSuccessful()){

                                                                                ChatReqRef.child(currentUserID).child(chat_req_ids)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {


                                                                                                if (task.isSuccessful()){

                                                                                                    ChatReqRef.child(chat_req_ids).child(currentUserID)
                                                                                                            .removeValue()
                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task) {


                                                                                                                    if (task.isSuccessful()){


                                                                                                                        Toast.makeText(NotificationsActivity.this, "New Contact added", Toast.LENGTH_SHORT).show();


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


                                                            }
                                                        });



                                                    }
                                                });


                                                holder.requestCancelBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {


                                                        ChatReqRef.child(currentUserID).child(chat_req_ids)
                                                                .removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {


                                                                        if (task.isSuccessful()){

                                                                            ChatReqRef.child(chat_req_ids).child(currentUserID)
                                                                                    .removeValue()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {


                                                                                            if (task.isSuccessful()){


                                                                                                Toast.makeText(NotificationsActivity.this, "Contact deleted", Toast.LENGTH_SHORT).show();


                                                                                            }

                                                                                        }
                                                                                    });

                                                                        }

                                                                    }
                                                                });

                                                    }
                                                });


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }


                                    else if (type.equals("sent")){

                                        final Button request_sent_btn = holder.itemView.findViewById(R.id.request_accept_btn);
                                        request_sent_btn.setText("Cancel Request");

                                        holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.INVISIBLE);


                                        UsersRef.child(chat_req_ids).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                                if (dataSnapshot.hasChild("profileimage")){


                                                    String retrieveReqImage = dataSnapshot.child("profileimage").getValue().toString();
                                                    String retrieveReqName = dataSnapshot.child("profilename").getValue().toString();
                                                    String retrieveReqCountry = dataSnapshot.child("country").getValue().toString();


                                                    holder.notProfileName.setText(retrieveReqName);
                                                    holder.notProfileCountry.setText(retrieveReqCountry);

                                                    Picasso.get().load(retrieveReqImage).placeholder(R.drawable.ic_profile).into(holder.notProfileImage);


                                                } else {


                                                    String retrieveReqName = dataSnapshot.child("profilename").getValue().toString();
                                                    String retrieveReqCountry = dataSnapshot.child("country").getValue().toString();


                                                    holder.notProfileName.setText(retrieveReqName);
                                                    holder.notProfileCountry.setText(retrieveReqCountry);


                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                        //en son burayı yaptım

                                        request_sent_btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                ChatReqRef.child(currentUserID).child(chat_req_ids)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {


                                                                if (task.isSuccessful()){

                                                                    ChatReqRef.child(chat_req_ids).child(currentUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {


                                                                                    if (task.isSuccessful()){


                                                                                        Toast.makeText(NotificationsActivity.this, "Contact deleted", Toast.LENGTH_SHORT).show();


                                                                                    }

                                                                                }
                                                                            });

                                                                }

                                                            }
                                                        });




                                            }
                                        });


                                    }

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_notifications_layout,parent,false);
                        RequestViewHolder holder = new RequestViewHolder(view);
                        return holder;


                    }
                };

        recyclerNotifications.setAdapter(adapter);
        adapter.startListening();




    } //onstart sonu



        public static class RequestViewHolder extends RecyclerView.ViewHolder {

         CircleImageView notProfileImage;
         TextView notProfileName, notProfileCountry;
         Button requestAcceptBtn, requestCancelBtn;



            public RequestViewHolder(@NonNull View itemView) {

                super(itemView);


                notProfileImage = itemView.findViewById(R.id.not_profile_image);
                notProfileName = itemView.findViewById(R.id.not_profile_name);
                notProfileCountry = itemView.findViewById(R.id.not_profile_country);
                requestAcceptBtn = itemView.findViewById(R.id.request_accept_btn);
                requestCancelBtn = itemView.findViewById(R.id.request_cancel_btn);


            }
        }







}
