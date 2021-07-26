package com.example.hushnew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hush.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Set;

/**
 This activity manages all the functions related to the settings page of the app
 **/

public class SettingsActivity extends AppCompatActivity {

    protected FirebaseUser mcurrentuser;
    private DatabaseReference myRef;
    TextView mname,mstatus;
    CircleImageView settingsimage;
    Button settingstatbtn,imgbtn;
    private static final int GALLERY_PICK = 1;
    private ProgressDialog mprogress;

    StorageReference mimagestorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mimagestorage = FirebaseStorage.getInstance().getReference();
        settingsimage = findViewById(R.id.settingsimage);



        mcurrentuser = FirebaseAuth.getInstance().getCurrentUser();
        String Curruserid = mcurrentuser.getUid();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Curruserid);
        myRef.keepSynced(true);



        /** Reading values from the database of the current user */
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

                settingstatbtn = findViewById(R.id.settingsstatusbtn);
                imgbtn = findViewById(R.id.settingsimagebtn);

                mname = findViewById(R.id.settingsname);
                mstatus = findViewById(R.id.settingsstatus);
                mname.setText(name);
                mstatus.setText(status);
                if(!image.equals("default")) {
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(settingsimage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).into(settingsimage);
                        }
                    });
                }

                settingstatbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent statusintent = new Intent(SettingsActivity.this,StatusActivity.class);
                        statusintent.putExtra("statusval",mstatus.getText().toString());
                        startActivity(statusintent);
//
                    }
                });

                imgbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent galleryIntent = new Intent();
                        galleryIntent.setType("image/*");
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);
                    }
                });

         }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         checking if the intent is working fine
         **/

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();

            /** start cropping activity for pre-acquired image saved on the device */

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);



        }



        /**
         uploading image
         **/
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mprogress = new ProgressDialog(this);
                mprogress.setTitle("Getting you a new look!");
                mprogress.setMessage("Please wait while Profile is Updated..");
                mprogress.setCanceledOnTouchOutside(false);
                mprogress.show();
                Uri resultUri = result.getUri();






                File thumb_filePath = new File(resultUri.getPath());
                /**
                 compressing the image
                 **/
                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(50)
                            .setMaxHeight(50)
                            .setQuality(20)
                            .compressToBitmap(thumb_filePath);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
                byte[] thumb_byte = baos.toByteArray();




                StorageReference filepath = mimagestorage.child("profile_images").child(mcurrentuser.getUid()+".jpg");
                StorageReference thumbpath = mimagestorage.child("profile_images").child("thumbs").child(mcurrentuser.getUid()+".jpg");


                /**
                 uploading image in thumbnail and proper image path
                 **/

                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                final String downloadUrl = uri.toString();
                                UploadTask uploadTask = thumbpath.putBytes(thumb_byte);      //start
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {




                                                thumbpath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                        thumbpath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri urxi) {

                                                               final String thumb_downloadurl = urxi.toString();


                                                                myRef.child("thumb_image").setValue(thumb_downloadurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if(task.isSuccessful())
                                                                        {
                                                                            mprogress.dismiss();
                                                                            Toast.makeText(SettingsActivity.this,"Success Uploading",Toast.LENGTH_LONG).show();
                                                                        }

                                                                        else {
                                                                            Toast.makeText(SettingsActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                                                            mprogress.dismiss();
                                                                        }

                                                                    }
                                                                });






                                                            }
                                                        });

                                                    }
                                                });


                                    }
                                });

                                //end


                                myRef.child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful())
                                        {
                                            mprogress.dismiss();
                                            Toast.makeText(SettingsActivity.this,"Success Uploading",Toast.LENGTH_LONG).show();
                                        }

                                        else {
                            Toast.makeText(SettingsActivity.this, "Failed", Toast.LENGTH_LONG).show();
                            mprogress.dismiss();
                        }

                                    }
                                });

                            }
                        });

                    }
                });

                //next
//                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//
//
//
//
//
////                    @Override
////                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
////                        if(task.isSuccessful())
////                        {
////                            String download_url = task.getResult().getDownloadUrl().toString;
////
////                            myRef.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
////                                @Override
////                                public void onComplete(@NonNull Task<Void> task) {
////
////                                    if(task.isSuccessful())
////                                    {
////                                        mprogress.dismiss();
////                                        Toast.makeText(SettingsActivity.this,"Success Uploading",Toast.LENGTH_LONG).show();
////                                    }
////
////                                }
////                            });
////
////
////
////                        }
////
////                        else {
////                            Toast.makeText(SettingsActivity.this, "Failed", Toast.LENGTH_LONG).show();
////                            mprogress.dismiss();
////                        }
////                    }
//
//                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

            }
        }


    }


}