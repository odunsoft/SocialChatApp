package com.odunyazilim.socialcchatt;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.odunyazilim.socialcchatt.Model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {


    private RecyclerView recyclerHome;
    RecyclerView.LayoutManager layoutManager;

    private DatabaseReference UsersRef;
    private String currentUserID;
    private FirebaseAuth mAuth;

    private ImageView filterBtnHome;


    private EditText searchEt;

    ArrayList<User> arrayList;


    private FloatingActionButton fab;

    private AppCompatSpinner spinnerCountryHome;




    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");

        recyclerHome = view.findViewById(R.id.recycler_home);
        recyclerHome.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(),4);
        recyclerHome.setLayoutManager(layoutManager);


        filterBtnHome=view.findViewById(R.id.filter_btn_home);

        spinnerCountryHome=view.findViewById(R.id.spinner_country_home);

        final ArrayAdapter<CharSequence> spinadapter = ArrayAdapter.createFromResource
                (getContext(), R.array.countries_array, android.R.layout.simple_spinner_dropdown_item);

        spinnerCountryHome.setAdapter(spinadapter);
        //spinnerdan veriyi spinnerCountryReg.getSelectedItem ile alıcaz




        fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                Intent reqIntent = new Intent(getContext(),NotificationsActivity.class);
                startActivity(reqIntent);


            }
        });


        arrayList=new ArrayList<>();
        searchEt = view.findViewById(R.id.search_et);


        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().isEmpty()){

                    search(s.toString());

                } else {

                    search("");
                }
            }
        });





       FilterList();


        return view;
    } //oncreate sonu




    private void FilterList(){

        spinnerCountryHome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                final Query query2=UsersRef.orderByChild("country").equalTo(String.valueOf(spinnerCountryHome.getSelectedItem()));



                FirebaseRecyclerOptions<User> options =
                        new FirebaseRecyclerOptions.Builder<User>()
                                .setQuery(UsersRef,User.class)
                                .build();


                FirebaseRecyclerAdapter<User, UserViewHolder> adapter =
                        new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull UserViewHolder holder, final int position, @NonNull User model) {


                                holder.userNameDisplay.setText(model.getProfilename());
                                Picasso.get().load(model.getProfileimage()).into(holder.userImageDisplay);


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

                            @NonNull
                            @Override
                            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                                UserViewHolder viewHolder = new UserViewHolder(v);
                                return viewHolder;

                            }
                        };

                recyclerHome.setAdapter(adapter);
                adapter.startListening();






                    filterBtnHome.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {



                            String lolo = spinnerCountryHome.getSelectedItem().toString();

                            if (lolo.equals("Country")){


                                FirebaseRecyclerOptions<User> options =
                                        new FirebaseRecyclerOptions.Builder<User>()
                                                .setQuery(UsersRef,User.class)
                                                .build();


                                FirebaseRecyclerAdapter<User, UserViewHolder> adapter =
                                        new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
                                            @Override
                                            protected void onBindViewHolder(@NonNull UserViewHolder holder, final int position, @NonNull User model) {


                                                holder.userNameDisplay.setText(model.getProfilename());
                                                Picasso.get().load(model.getProfileimage()).into(holder.userImageDisplay);


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

                                            @NonNull
                                            @Override
                                            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                                                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                                                UserViewHolder viewHolder = new UserViewHolder(v);
                                                return viewHolder;

                                            }
                                        };

                                recyclerHome.setAdapter(adapter);
                                adapter.startListening();



                            } else {


                                FirebaseRecyclerOptions<User> options =
                                        new FirebaseRecyclerOptions.Builder<User>()
                                                .setQuery(query2,User.class)
                                                .build();

                                FirebaseRecyclerAdapter<User,UserViewHolder> adapter2 =
                                        new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
                                            @Override
                                            protected void onBindViewHolder(@NonNull UserViewHolder holder, final int position, @NonNull User model) {

                                                holder.userNameDisplay.setText(model.getProfilename());
                                                Picasso.get().load(model.getProfileimage()).placeholder(R.drawable.randommmmm).into(holder.userImageDisplay);

                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        String visit_user_id = getRef(position).getKey();

                                                        Intent visitIntent = new Intent(getActivity(), UserActivity.class);
                                                        visitIntent.putExtra("visit_user_id", visit_user_id);
                                                        startActivity(visitIntent);

                                                    }
                                                });

                                            }

                                            @NonNull
                                            @Override
                                            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                                                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                                                UserViewHolder viewHolder = new UserViewHolder(v);
                                                return viewHolder;
                                            }
                                        };

                                recyclerHome.setAdapter(adapter2);
                                adapter2.startListening();


                            }


                        }
                    });


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }



    private void search(String s) {

        Query query = UsersRef.orderByChild("profilename").startAt(s).endAt(s+"\uf8ff");


        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query,User.class)
                        .build();

        FirebaseRecyclerAdapter<User,UserViewHolder> adapter2 =
                new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserViewHolder holder, final int position, @NonNull User model) {

                        holder.userNameDisplay.setText(model.getProfilename());
                        Picasso.get().load(model.getProfileimage()).into(holder.userImageDisplay);

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

                    @NonNull
                    @Override
                    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                        UserViewHolder viewHolder = new UserViewHolder(v);
                        return viewHolder;
                    }
                };

        recyclerHome.setAdapter(adapter2);
        adapter2.startListening();


    }




    @Override
    public void onStart() {

        super.onStart();






    }



    public static class UserViewHolder extends RecyclerView.ViewHolder{

        CircleImageView userImageDisplay;
        TextView userNameDisplay;

        public UserViewHolder(@NonNull View itemView)
        {

            super(itemView);

            userImageDisplay =itemView.findViewById(R.id.user_image_display);
            userNameDisplay = itemView.findViewById(R.id.user_name_display);

        }
    }






    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }


}
