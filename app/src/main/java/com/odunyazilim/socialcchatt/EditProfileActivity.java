package com.odunyazilim.socialcchatt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    AppCompatSpinner spinnerCountryEdit;
    RadioGroup editRadioGroup;
    AppCompatRadioButton editRadioFemale, editRadioMale;
    //yukarkiler viplere acilacak


    CircleImageView editProfileImage;
    EditText editAge, editProfileName, editCity, editAbout;
    Button editSaveButton;

    ProgressBar editProgressBar;
    int progressDurum =0;
    private Handler handler =new Handler();


    FirebaseAuth mAuth;
    String currentUserID;
    DatabaseReference RootRef;
    StorageReference UserImageStoreRef;
    Uri ImageUri;
    private static final int GalleryPick =1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        editProfileImage = findViewById(R.id.edit_profile_image);
        editAge = findViewById(R.id.edit_age);
        editProfileName = findViewById(R.id.edit_profile_name);
        editCity = findViewById(R.id.edit_city);
        editAbout = findViewById(R.id.edit_about);
        editSaveButton = findViewById(R.id.edit_save_button);
        editProgressBar =findViewById(R.id.edit_progressbar);


        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();
        UserImageStoreRef = FirebaseStorage.getInstance().getReference().child("Profile Images");



        editSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpdateEditProfile();

            }
        });



        RetrieveEditProfile();


        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_PICK);

                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GalleryPick);


            }
        });





    } //oncreate sonu



    private void RetrieveEditProfile() {

        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.hasChild("profilename")){

                    String retrieveEditImage = dataSnapshot.child("profileimage").getValue().toString();
                    String retrieveEditName = dataSnapshot.child("profilename").getValue().toString();
                    String retrieveEditCity = dataSnapshot.child("city").getValue().toString();
                    String retrieveEditAge = dataSnapshot.child("age").getValue().toString();
                    String retrieveEditAbout = dataSnapshot.child("about").getValue().toString();

                    editProfileName.setText(retrieveEditName);
                    editAge.setText(retrieveEditAge);
                    editCity.setText(retrieveEditCity);
                    editAbout.setText(retrieveEditAbout);
                    Picasso.get().load(retrieveEditImage).placeholder(R.drawable.randommmmm).into(editProfileImage);

                }
                else if (dataSnapshot.exists() && dataSnapshot.hasChild("profilename")){

                    String retrieveEditName = dataSnapshot.child("profilename").getValue().toString();
                    String retrieveEditCity = dataSnapshot.child("city").getValue().toString();
                    String retrieveEditAge = dataSnapshot.child("age").getValue().toString();
                    String retrieveEditAbout = dataSnapshot.child("about").getValue().toString();

                    editProfileName.setText(retrieveEditName);
                    editAge.setText(retrieveEditAge);
                    editCity.setText(retrieveEditCity);
                    editAbout.setText(retrieveEditAbout);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }




    private void UpdateEditProfile() {


        String retrieveEdAge =editAge.getText().toString();
        String retrieveEdUsername =editProfileName.getText().toString();
        String retrieveEdCity =editCity.getText().toString();
        String retrieveEdAbout =editAbout.getText().toString();



        if (!TextUtils.isEmpty(retrieveEdAge) && !TextUtils.isEmpty(retrieveEdUsername)
                && !TextUtils.isEmpty(retrieveEdCity) && !TextUtils.isEmpty(retrieveEdAbout)){


            progressDurum =0;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (progressDurum<100){

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                editProgressBar.setProgress(progressDurum);
                            }
                        });

                        try {

                            Thread.sleep(100);
                        } catch (InterruptedException e){

                            e.printStackTrace();

                        }
                    }
                }
            }).start();




            HashMap<String,Object> editProfileMap = new HashMap<>();

            editProfileMap.put("profilename",retrieveEdUsername);
            editProfileMap.put("city",retrieveEdCity);
            editProfileMap.put("age",retrieveEdAge);
            editProfileMap.put("about",retrieveEdAbout);
            editProfileMap.put("uid",currentUserID);


            RootRef.child("Users").child(currentUserID).updateChildren(editProfileMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                         public void onComplete(@NonNull Task<Void> task) {

                                                  if (task.isSuccessful()){

                                                      Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();


                                                  }
                                                  else {

                                                      Toast.makeText(EditProfileActivity.this, "Profile couldn't updated", Toast.LENGTH_SHORT).show();

                                                  }

                                                }
                                            });

        }

        else {

            Toast.makeText(this, "You must fill all the fields", Toast.LENGTH_SHORT).show();

        }

    }  //update edit profile sonu





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if (data != null){

            ImageUri = data.getData();

            Bitmap bmp = null;


            try {

                //fotoyu otomatık dondurcez

                RotateBitmap rotateBitmap = new RotateBitmap();
                bmp = rotateBitmap.HandleSamplingAndRotationBitmap(this,ImageUri);


            } catch (IOException e){

                e.printStackTrace();
            }


            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
            byte[] fileInBytes = baos.toByteArray();


            final StorageReference filePath = UserImageStoreRef.child(currentUserID+".jpg");

            filePath.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {


                            final String downloadUrl = String.valueOf(uri);


                            RootRef.child("Users").child(currentUserID).child("profileimage").setValue(downloadUrl)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()){

                                                                        Toast.makeText(EditProfileActivity.this, "Image saved successfully", Toast.LENGTH_SHORT).show();

                                                                    }
                                                                    else {

                                                                        String message = task.getException().toString();

                                                                        Toast.makeText(EditProfileActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();

                                                                    }

                                                                }
                                                            });

                        }
                    });
                }
            });

        }


    }  //onactivity resul sonu
}
