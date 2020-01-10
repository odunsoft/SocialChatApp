package com.odunyazilim.socialcchatt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.odunyazilim.socialcchatt.Model.User;
import com.odunyazilim.socialcchatt.notifications.Data;
import com.odunyazilim.socialcchatt.notifications.Sender;
import com.odunyazilim.socialcchatt.notifications.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {

    private String messageReceiverID, myUid, retChatName, retChatImage;
    private String saveCurrentDate,saveCurrentTime;

    private AppCompatImageButton sendMessageBtn, sendImageBtn;
    private EditText inputMessage;

    private DatabaseReference UsersRef, RootRef, ChatListRef;
    private FirebaseAuth mAuth;

    RecyclerView recyclerMessageList;

    private Toolbar chatToolBar;
    private TextView receiverProfileName, receiverProfileLastSeen,receiverProfileLastSeenTime;





    private List<Chats> chatList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private ChatAdapter chatAdapter;


    private NotificationManagerCompat notificationManagerCompat;

    //volley request queue for notification
   private RequestQueue requestQueue;

   private boolean notify = false;


    private Uri ImageUri;

    private static final int GalleryPick =1;

   private StorageReference ChatImageStoreRef;

   ImageView deleteChatImg;

   Context context = this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatToolBar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        RootRef = FirebaseDatabase.getInstance().getReference();
        ChatListRef = FirebaseDatabase.getInstance().getReference().child("Chats");

        ChatImageStoreRef = FirebaseStorage.getInstance().getReference();

        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();


        receiverProfileName = findViewById(R.id.receiver_profile_name);
        receiverProfileLastSeen = findViewById(R.id.receiver_profile_last_seen);
        receiverProfileLastSeenTime = findViewById(R.id.receiver_profile_last_seen_time);

        sendMessageBtn = findViewById(R.id.send_message_btn);
        sendImageBtn = findViewById(R.id.send_image_btn);
        inputMessage = findViewById(R.id.input_message);


        requestQueue = Volley.newRequestQueue(getApplicationContext());


        chatAdapter = new ChatAdapter(chatList);
        recyclerMessageList = findViewById(R.id.recycler_message_list);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerMessageList.setLayoutManager(linearLayoutManager);
        recyclerMessageList.setAdapter(chatAdapter);



        deleteChatImg = findViewById(R.id.delete_chat_img);


        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendMessage();

            }
        });


        sendImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent imageIntent = new Intent();
                imageIntent.setAction(Intent.ACTION_PICK);

                imageIntent.setType("image/*");
                startActivityForResult(imageIntent,GalleryPick);



            }
        });



        UsersRef.child(messageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){


                    String retChatName = dataSnapshot.child("profilename").getValue().toString();


                    receiverProfileName.setText(retChatName);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


            DisplayLastSeen();




        RootRef.child("Chats").child(myUid).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {



                        Chats chats = dataSnapshot.getValue(Chats.class);

                        chatList.add(chats);

                        chatAdapter.notifyDataSetChanged();

                        recyclerMessageList.smoothScrollToPosition(recyclerMessageList.getAdapter().getItemCount());



                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {


                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {


                    }
                });



        deleteChatImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                alertDialog.setTitle("Do you want to delete chat?")
                        .setCancelable(true)
                        .setIcon(R.drawable.ic_delete)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                //evete tıklayınca yapılacaklar
                                DeleteAllChat();


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //hayıra tıklayınca ne olacak

                               dialog.dismiss();

                            }
                        }).show();


            }
        });



    }  //oncreate



    private void DeleteAllChat() {


        ChatListRef.child(myUid).child(messageReceiverID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        if (task.isSuccessful()){


                            ChatListRef.child(messageReceiverID).child(myUid).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                Toast.makeText(ChatActivity.this, "Messages deleted", Toast.LENGTH_SHORT).show();


                                            }


                                        }
                                    });

                        }

                    }
                });


    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK){


            ImageUri = data.getData();


            Bitmap bmp = null;


            try {

                //fotoyu otomatık dondurcez

                RotateBitmap rotateBitmap = new RotateBitmap();
                bmp = rotateBitmap.HandleSamplingAndRotationBitmap(this,ImageUri);


            } catch (IOException e){

                e.printStackTrace();
            }



            final String my_uid_ref = "Chats/" + myUid + "/" +messageReceiverID;
            final String his_uid_ref = "Chats/" + messageReceiverID + "/" + myUid;


            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
            byte[] fileInBytes = baos.toByteArray();



            DatabaseReference userMessageKeyRef = RootRef.child("Chats")
                    .child(myUid).child(messageReceiverID).push();


            //mesajın push ıdsını alıyoruz
           final String messagePushID = userMessageKeyRef.getKey();


           final StorageReference filePath = ChatImageStoreRef.child("chat_images").child(messagePushID+".jpg");


            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
            saveCurrentDate =currentDate.format(calendar.getTime());


            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
            saveCurrentTime =currentTime.format(calendar.getTime());




            filePath.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {


                            final String downloadUrl = String.valueOf(uri);


                            HashMap<String,Object> messageMap = new HashMap<>();

                            messageMap.put("message",downloadUrl);
                            messageMap.put("sender",myUid);
                            messageMap.put("receiver",messageReceiverID);
                            messageMap.put("time",saveCurrentTime);
                            messageMap.put("date",saveCurrentDate);
                            messageMap.put("type","image");
                            messageMap.put("messageID",messagePushID);


                            HashMap<String,Object> userMap = new HashMap<>();

                            userMap.put(my_uid_ref + "/" +messagePushID,messageMap);
                            userMap.put(his_uid_ref + "/" +messagePushID,messageMap);



                            RootRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                    if (task.isSuccessful()){

                                        Toast.makeText(ChatActivity.this, "message sent", Toast.LENGTH_SHORT).show();

                                    }

                                    else {

                                        Toast.makeText(ChatActivity.this, "Message sent error", Toast.LENGTH_SHORT).show();

                                    }

                                    inputMessage.setText("");


                                }
                            });



                        }
                    });
                }
            });


        }

    }




    private void DisplayLastSeen(){

        RootRef.child("Users").child(messageReceiverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        if (dataSnapshot.child("userState").hasChild("state")){



                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();


                            if (state.equals("online")){



                                receiverProfileLastSeen.setText("online");
                                receiverProfileLastSeenTime.setVisibility(View.INVISIBLE);

                            }

                            else if (state.equals("offline")){


                                receiverProfileLastSeen.setText(date);
                                receiverProfileLastSeenTime.setText(time);

                            }


                        }

                        }



                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }




    private void SendMessage() {

            notify = true;

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
            saveCurrentDate =currentDate.format(calendar.getTime());


            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
            saveCurrentTime =currentTime.format(calendar.getTime());


            final String messageText =inputMessage.getText().toString().trim();


            final String my_uid_ref = "Chats/" + myUid + "/" +messageReceiverID;
            String his_uid_ref = "Chats/" + messageReceiverID + "/" + myUid;


            DatabaseReference userMessageKeyRef = RootRef.child("Chats")
                                        .child(myUid).child(messageReceiverID).push();


            //mesajın push ıdsını alıyoruz
            String messagePushID = userMessageKeyRef.getKey();



            if (TextUtils.isEmpty(messageText)){

                inputMessage.setError("Write your message");
                inputMessage.requestFocus();

            } else {

                HashMap<String,Object> messageMap = new HashMap<>();

                messageMap.put("message",messageText);
                messageMap.put("sender",myUid);
                messageMap.put("receiver",messageReceiverID);
                messageMap.put("time",saveCurrentTime);
                messageMap.put("date",saveCurrentDate);
                messageMap.put("type","text");
                messageMap.put("messageID",messagePushID);



                HashMap<String,Object> userMap = new HashMap<>();

                userMap.put(my_uid_ref + "/" +messagePushID,messageMap);
                userMap.put(his_uid_ref + "/" +messagePushID,messageMap);



                RootRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        if (task.isSuccessful()){

                            Toast.makeText(ChatActivity.this, "message sent", Toast.LENGTH_SHORT).show();

                        }

                        else {

                            Toast.makeText(ChatActivity.this, "Message sent error", Toast.LENGTH_SHORT).show();

                        }

                        inputMessage.setText("");


                    }
                });


                DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users")
                        .child(myUid);


                database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);

                        if (notify){

                            sendNotification(messageReceiverID,user.getProfilename(),messageText);
                        }
                        notify=false;


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }



    }


    private void sendNotification(final String messageReceiverID, final String profilename, final String messageText) {


        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query =allTokens.orderByKey().equalTo(messageReceiverID);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myUid,profilename+":"+messageText,"New Message",messageReceiverID,R.drawable.ic_notifications);


                    Sender sender = new Sender(data,token.getToken());
                    
                   //fcm json object request
                    try {

                        JSONObject senderJsonObject = new JSONObject(new Gson().toJson(sender));

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        //response of the request
                                        Log.d("JSON_RESPONSE", "onResponse: "+response.toString());


                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Log.d("JSON_RESPONSE", "onResponse: "+error.toString());


                            }
                        }){

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {

                                //put params

                                Map<String, String> headers = new HashMap<>();

                                headers.put("Content-Type","application/json");
                                headers.put("Authorization","key=AAAAqyv8Jbw:APA91bGkMiK8DR6GDgz9gtsA3XBpCQeTQtVYSpsGah5mnDhmHzYjniFlgQVFCdIHKLNz5rCULPnMzcMGqSyAAYDdTnlMSX003M1O4AL_ExgOU5DyqSY1eJMqYCoEJmqZIHyjVTU3sWIo");


                                return headers;


                            }
                        };

                        //add this request to queue
                        requestQueue.add(jsonObjectRequest);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


}
