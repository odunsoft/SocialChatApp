package com.odunyazilim.socialcchatt;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.odunyazilim.socialcchatt.Model.Friend;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {

    private View chatsView;
    private RecyclerView recyclerChatFragment;

    private DatabaseReference ChatsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatsView = inflater.inflate(R.layout.fragment_chat, container, false);


        mAuth=FirebaseAuth.getInstance();
        currentUserID =mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);



        recyclerChatFragment = chatsView.findViewById(R.id.recycler_chat_fragment);
        recyclerChatFragment.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerChatFragment.addItemDecoration(divider);





        return chatsView;

    }


    @Override
    public void onStart() {

        super.onStart();


        FirebaseRecyclerOptions<Friend> options =
                new FirebaseRecyclerOptions.Builder<Friend>()
                .setQuery(ChatsRef,Friend.class)
                .build();


        FirebaseRecyclerAdapter<Friend, ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Friend, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Friend model) {


                        //contacts listte altımızdakı kısılerın idlerini alıyoruz

                        final String usersIDs = getRef(position).getKey();

                        //pozisyonu gelip de aldıgımız idnin bilgilerini Users nodeundan cekıyoruz
                        //o yuzden usersref ve usersIDs kullandık

                        UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                if (dataSnapshot.exists()){


                                    if (dataSnapshot.hasChild("profileimage")){

                                        final String retChatImage = dataSnapshot.child("profileimage").getValue().toString();
                                        final String retChatName = dataSnapshot.child("profilename").getValue().toString();
                                        final String retChatCountry = dataSnapshot.child("country").getValue().toString();


                                        holder.chatProfileName.setText(retChatName);
                                        holder.chatProfileCountry.setText(retChatCountry);

                                        Picasso.get().load(retChatImage).placeholder(R.drawable.ic_profile).into(holder.chatProfileImage);


                                        if (dataSnapshot.child("userState").hasChild("state")){


                                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                            String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                            if (state.equals("online")){

                                                holder.chatProfileStatus.setText("online");
                                                holder.chatProfileOnlineImg.setVisibility(View.VISIBLE);

                                            }

                                            else if (state.equals("offline")){

                                                holder.chatProfileStatus.setText("Seen: "+date+" "+time);
                                                holder.chatProfileOnlineImg.setVisibility(View.INVISIBLE);

                                            }


                                        } else {


                                            holder.chatProfileStatus.setText("offline");
                                            holder.chatProfileOnlineImg.setVisibility(View.INVISIBLE);


                                        }


                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Intent chatIntent = new Intent(getContext(),ChatActivity.class);

                                                chatIntent.putExtra("visit_user_id",usersIDs);
                                                chatIntent.putExtra("visit_user_name",retChatName);
                                                chatIntent.putExtra("visit_user_image",retChatImage);

                                                startActivity(chatIntent);

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
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.users_chat_layout,parent,false);
                        return new ChatsViewHolder(view);


                    }
                };

        recyclerChatFragment.setAdapter(adapter);
        adapter.startListening();


    }




    public static class ChatsViewHolder extends RecyclerView.ViewHolder{


        CircleImageView chatProfileImage;
        ImageView chatProfileOnlineImg;
        TextView chatProfileName, chatProfileCountry, chatProfileStatus;


        public ChatsViewHolder(@NonNull View itemView) {

            super(itemView);

            chatProfileImage = itemView.findViewById(R.id.chat_profile_image);
            chatProfileName = itemView.findViewById(R.id.chat_profile_name);
            chatProfileCountry = itemView.findViewById(R.id.chat_profile_country);
            chatProfileStatus = itemView.findViewById(R.id.chat_profile_status);
            chatProfileOnlineImg = itemView.findViewById(R.id.chat_profile_online);


        }
    }


}

