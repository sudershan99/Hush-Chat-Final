package com.example.hushnew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 This activity handles the ui for displaying the profile of other users
 **/

public class ProfileActivity extends AppCompatActivity {

    private TextView mprofileid;
    static private ImageView mProfileImg;
    static private TextView mProfileName, mProfileStatus, mProfileFriendscnt;
    static private Button mProfileSendReq;
    private DatabaseReference mUsersDatabase;
    private ProgressDialog mProgress;
   static private String CurrentState;
    private DatabaseReference mFriendRequests;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;
    private FirebaseUser mCurrentUser;
    private  Button mdeclinebtn;
    private DatabaseReference mRootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String user_id = getIntent().getStringExtra("testid");



        Log.v("uid",user_id);


        /**
         getting refences of all the required locations
         **/

        mUsersDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRequests=FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notification");
        mRootRef = FirebaseDatabase.getInstance().getReference();

        CurrentState="not friends";

        mProgress=new ProgressDialog(this);
        mProgress.setTitle("Loading User Data");
        mProgress.setMessage("Please wait while data is loaded!");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        mProfileImg= findViewById(R.id.userprofimg);
        mProfileName= findViewById(R.id.userdisname);
      //  mProfileFriendscnt= findViewById(R.id.usertotalfrnds);
        mProfileStatus= findViewById(R.id.userstatus);
        mProfileSendReq=findViewById(R.id.rqstbtn);
        mdeclinebtn = findViewById(R.id.dectbtn);


        //setting online
         FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mUserRef;
        mUserRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(mAuth.getCurrentUser().getUid());
        mUserRef.child("online").setValue("true");

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String display_name= snapshot.child("name").getValue().toString();
                String status= snapshot.child("status").getValue().toString();
                String image= snapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                if(!image.equals("default"))
                Picasso.get().load(image).placeholder(R.drawable.avatar).into(mProfileImg);

                // LOAD PREVIOUS STATES

                mFriendRequests.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        if(snapshot.hasChild(user_id))
                        {
                            String req_type = snapshot.child(user_id).child("request_type").getValue().toString();
                            if(req_type.equals("received"))
                            {
                                CurrentState = "req_received";
                                mProfileSendReq.setText("Accept Request");


                            }
                            else if(req_type.equals("sent"))
                            {
                                CurrentState = "req_sent";
                                mProfileSendReq.setText("Cancel Friend Request");


                            }

                        }

                        // Already friends with this person
                        else
                        {
                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if(snapshot.hasChild(user_id))
                                    {
                                        CurrentState = "friends";
                                        mProfileSendReq.setText("Unfriend this person");


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        mProgress.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mProfileSendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProfileSendReq.setEnabled(false);

                // NOT FRIENDS
                if(CurrentState.equals("not friends")){

                    Map requestMap = new HashMap();
                    HashMap<String,String> notificationData = new HashMap<>();
                    notificationData.put("from",mCurrentUser.getUid());
                    notificationData.put("type","request");


                    requestMap.put("Friend_req/"+mCurrentUser.getUid()+"/" + user_id + "/request_type", "sent" );
                    requestMap.put("Friend_req/"+user_id+"/" + mCurrentUser.getUid() + "/request_type", "received" );

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                            if(error!= null)
                            {
                                Toast.makeText(ProfileActivity.this,"Some error bro",Toast.LENGTH_LONG).show();
                            }

                            else
                            {
                                CurrentState = "req_sent";
                                mProfileSendReq.setEnabled(true);
                                mProfileSendReq.setText("Cancel Friend Request");


                            }

                        }
                    });




                }


                //CANCEL REQUEST
                else if(CurrentState.equals("req_sent"))
                {

                    Map CancelMap= new HashMap<>();

                    CancelMap.put("Friend_req/"+mCurrentUser.getUid()+"/"+user_id,null);
                    CancelMap.put("Friend_req/"+user_id+"/"+mCurrentUser.getUid(),null);

                    mRootRef.updateChildren(CancelMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                            if(error!= null)
                            {
                                Toast.makeText(ProfileActivity.this,"Some error bro",Toast.LENGTH_LONG).show();
                            }

                            else
                            {
                                mProfileSendReq.setEnabled(true);
                                CurrentState = "not friends";
                                mProfileSendReq.setText("Send Friend Request");


                            }
                        }
                    });






                }

                // ACCEPT REQUEST
                else if(CurrentState.equals("req_received")) {
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap= new HashMap<>();

                    friendsMap.put("Friends/"+mCurrentUser.getUid()+"/"+user_id + "/date",currentDate);
                    friendsMap.put("Friends/"+user_id+"/"+mCurrentUser.getUid()+"/date",currentDate);


                    friendsMap.put("Friend_req/"+mCurrentUser.getUid()+"/"+user_id,null);
                    friendsMap.put("Friend_req/"+user_id+"/"+mCurrentUser.getUid(),null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {


                            if(error!= null)
                            {
                                Toast.makeText(ProfileActivity.this,"Some error bro",Toast.LENGTH_LONG).show();
                            }
                            else {
                                mProfileSendReq.setEnabled(true);
                                CurrentState = "friends";
                                mProfileSendReq.setText("Unfriend this person");


                            }

                        }
                    });


                }

                else if(CurrentState.equals("friends"))
                {




                    Map friendsMap= new HashMap<>();

                    friendsMap.put("Friends/"+mCurrentUser.getUid()+"/"+user_id ,null);
                    friendsMap.put("Friends/"+user_id+"/"+mCurrentUser.getUid(),null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            CurrentState = "not friends";
                            mProfileSendReq.setText("Send Friend Request");



                        }
                    });



                }
            }
        });


    }
}