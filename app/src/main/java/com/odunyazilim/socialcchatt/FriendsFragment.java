package com.odunyazilim.socialcchatt;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class FriendsFragment extends Fragment {


   private RecyclerView recyclerFriends;
    private View contactsView;

    private DatabaseReference ContactsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    Toolbar friendToolbar;




    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsView =  inflater.inflate(R.layout.fragment_friends, container, false);



        friendToolbar=contactsView.findViewById(R.id.friendToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(friendToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);



        recyclerFriends = contactsView.findViewById(R.id.recycler_friends);
        recyclerFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerFriends.addItemDecoration(divider);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");





        return contactsView;


    }  //oncreate sonu






    @Override
    public void onStart() {

        super.onStart();




        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Friend>()
                .setQuery(ContactsRef,Friend.class)
                .build();



        FirebaseRecyclerAdapter<Friend,FriendViewHolder> adapter
                = new FirebaseRecyclerAdapter<Friend, FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendViewHolder holder, final int position, @NonNull Friend model) {


                String friendIDs = getRef(position).getKey();

                UsersRef.child(friendIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        if (dataSnapshot.hasChild("profileimage")){

                            String retFriendImage = dataSnapshot.child("profileimage").getValue().toString();
                            String retFriendName = dataSnapshot.child("profilename").getValue().toString();
                            String retFriendAbout = dataSnapshot.child("about").getValue().toString();

                            holder.friendProfileName.setText(retFriendName);
                            holder.friendProfileAbout.setText(retFriendAbout);

                            Picasso.get().load(retFriendImage).placeholder(R.drawable.ic_profile).into(holder.friendProfileImage);


                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String visit_user_id = getRef(position).getKey();

                                    Intent visitIntent = new Intent(getActivity(),UserActivity.class);
                                    visitIntent.putExtra("visit_user_id",visit_user_id);
                                    startActivity(visitIntent);



                                }
                            });


                        }
                        else {

                            String retFriendName = dataSnapshot.child("profilename").getValue().toString();
                            String retFriendAbout = dataSnapshot.child("about").getValue().toString();

                            holder.friendProfileName.setText(retFriendName);
                            holder.friendProfileAbout.setText(retFriendAbout);


                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_friend_layout,parent,false);
                FriendViewHolder viewHolder = new FriendViewHolder(view);
                return viewHolder;

            }
        };

        recyclerFriends.setAdapter(adapter);
        adapter.startListening();






    } //onstart sonu



    public static class FriendViewHolder extends RecyclerView.ViewHolder{


        TextView friendProfileName, friendProfileAbout;
        CircleImageView friendProfileImage;


        public FriendViewHolder(@NonNull View itemView) {

            super(itemView);


            friendProfileImage  =itemView.findViewById(R.id.friend_profile_image);
            friendProfileName  =itemView.findViewById(R.id.friend_profile_name);
            friendProfileAbout  =itemView.findViewById(R.id.friend_profile_about);


        }
    }





}
