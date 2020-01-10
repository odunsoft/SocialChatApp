package com.odunyazilim.socialcchatt;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {


    private List<Chats> userChatsList;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;



    private NotificationManagerCompat notificationManagerCompat;


    public ChatAdapter (List<Chats> userChatsList) {

        this.userChatsList = userChatsList;

    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        public TextView senderMessageText, receiverMessageText;
        public ImageView messageSenderImage, messageReceiverImage;



        public ChatViewHolder(@NonNull View itemView) {

            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            messageReceiverImage = itemView.findViewById(R.id.message_receiver_image);
            messageSenderImage = itemView.findViewById(R.id.message_sender_image);



        }
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout,parent,false);

       mAuth=FirebaseAuth.getInstance();

        notificationManagerCompat = NotificationManagerCompat.from(view.getContext());


       return new ChatViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolder holder, final int position) {


        String messageSenderID = mAuth.getCurrentUser().getUid();
        Chats chats = userChatsList.get(position);


        String messageSendid = chats.getSender();
        String messageType = chats.getType();


        holder.receiverMessageText.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.messageSenderImage.setVisibility(View.GONE);
        holder.messageReceiverImage.setVisibility(View.GONE);


        if (messageType.equals("text")){


            if (messageSendid.equals(messageSenderID)){


                holder.senderMessageText.setVisibility(View.VISIBLE);

                holder.senderMessageText.setBackgroundResource(R.drawable.sender_message_layout);
                holder.senderMessageText.setText(chats.getMessage());


            }
            else {

                holder.receiverMessageText.setVisibility(View.VISIBLE);

                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_message_layout);
                holder.receiverMessageText.setText(chats.getMessage());


            }
        }

       else if (messageType.equals("image")){

           if (messageSendid.equals(messageSenderID)){

               holder.messageSenderImage.setVisibility(View.VISIBLE);

               Picasso.get().load(chats.getMessage()).into(holder.messageSenderImage);


               holder.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                       Intent intent = new Intent(holder.itemView.getContext(),ImageViewerActivity.class);
                       intent.putExtra("url",userChatsList.get(position).getMessage());
                       holder.itemView.getContext().startActivity(intent);


                   }
               });



           }

           else {

               holder.messageReceiverImage.setVisibility(View.VISIBLE);

               Picasso.get().load(chats.getMessage()).into(holder.messageReceiverImage);


               holder.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                       Intent intent = new Intent(holder.itemView.getContext(),ImageViewerActivity.class);
                       intent.putExtra("url",userChatsList.get(position).getMessage());
                       holder.itemView.getContext().startActivity(intent);


                   }
               });




           }

        }



    } //onbindview holder sonu






    @Override
    public int getItemCount() {

        return userChatsList.size();

    }






}
