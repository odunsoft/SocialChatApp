package com.odunyazilim.socialcchatt;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.odunyazilim.socialcchatt.Model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchAdapterViewHolder> {


    public Context c;
    public ArrayList<User> arrayList;



    public SearchAdapter(Context c, ArrayList<User> arrayList){

        this.c=c;
        this.arrayList=arrayList;


    }

    @Override
    public int getItemCount() {

        return arrayList.size();



    }


    @Override
    public long getItemId(int position) {

        return position;


    }




    @NonNull
    @Override
    public SearchAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);

        return new SearchAdapterViewHolder(v);


    }


    @Override
    public void onBindViewHolder(@NonNull final SearchAdapterViewHolder holder, final int position) {


        User user = arrayList.get(position);

        final String visit_user_id = arrayList.get(position).getUid();

        holder.userNameDisplay.setText(user.getProfilename());
        Picasso.get().load(user.getProfileimage()).placeholder(R.drawable.ic_profile).into(holder.userImageDisplay);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent visitIntent = new Intent(v.getContext(),UserActivity.class);
                visitIntent.putExtra("visit_user_id",visit_user_id);
                v.getContext().startActivity(visitIntent);


            }
        });


    }















    public class SearchAdapterViewHolder extends RecyclerView.ViewHolder{


        CircleImageView userImageDisplay;
        TextView userNameDisplay;

        public SearchAdapterViewHolder(@NonNull View itemView) {

            super(itemView);

            userImageDisplay =itemView.findViewById(R.id.user_image_display);
            userNameDisplay = itemView.findViewById(R.id.user_name_display);

        }
    }



}
