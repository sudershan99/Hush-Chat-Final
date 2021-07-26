package com.example.hushnew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//import com.example.hush.ProfileActivity;
import com.example.hush.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

/**
 Activity displays all the users of the application
 **/
public class UsersActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;
    private RecyclerView mUsers;

    /**
     method to inflate the activity with the specified layout

     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = findViewById(R.id.users_appbar);
        setSupportActionBar(mToolbar);

        // setting up the toolbar
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mUsers = findViewById(R.id.users_list);
        mUsers.setHasFixedSize(true);
        mUsers.setLayoutManager(new LinearLayoutManager(this));



    }

    @Override
    protected void onStart() {
        super.onStart();

        /**
         setting up a query to fetch all the users
         **/


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .limitToLast(50);

        query.keepSynced(true);

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();
        Log.v("qre",options.toString());

        /**
         setting up the online status
         **/
        FirebaseUser mAuth;
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mUserRef;
        mUserRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(mAuth.getUid());
        mUserRef.child("online").setValue(true);
        String mCuruser = mAuth.getUid();


        /**
         setting up the adapter
         **/
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {

            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                /**
                 creating a run time view from the  newlayout xml file and passing it to onbindviewholder
                 **/

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.newlayput, parent, false);

                return new UsersViewHolder(view);
            }

            /**
             setting up all the details in the newlayout file
             **/
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder usersViewHolder, int i, @NonNull Users user) {
                usersViewHolder.setName(user.getName());
                usersViewHolder.setStatus(user.getStatus());
                usersViewHolder.setimage(user.getImage());



                String user_id = getRef(i).getKey();
                usersViewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!user_id.equals(mCuruser)) {
                            Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                            profileIntent.putExtra("testid", user_id);
                            startActivity(profileIntent);
                        }

                    }
                });
            }
        };

        /**
         setting up the adapter to start listening to realtime changes
         **/
        mUsers.setAdapter(adapter);
         adapter.startListening();


    }

    /**
     This class saves all the references of different items from the new layout file
     **/
    public class UsersViewHolder extends RecyclerView.ViewHolder {

        View mview;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);

            mview = itemView;
        }

        public void setName(String name) {
            TextView userNameView = (TextView) mview.findViewById(R.id.namen);
            userNameView.setText(name);
        }

        public void setStatus(String status) {
            TextView statusview = mview.findViewById(R.id.status_n);
            statusview.setText(status);
        }

        public void setimage(String thumbimg) {
            CircleImageView imgvw = mview.findViewById(R.id.circleimgn);
//            ImageView iconimg;
//            iconimg = findViewById(R.id.onlineicon);
//            iconimg.setVisibility(View.VISIBLE);

            if(!thumbimg.equals("default"))
            Picasso.get().load(thumbimg).placeholder(R.drawable.avatar).into(imgvw);
        }

//
    }
}