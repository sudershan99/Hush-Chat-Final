package com.example.hushnew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.hush.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.messaging.FirebaseMessaging;

import java.sql.Timestamp;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private androidx.appcompat.widget.Toolbar mtoolbar;
    private ViewPager mviewpager;
    private SectionsPagerAdapter msectionadapter;
    private TabLayout mtablayout;
    private DatabaseReference mUserRef;
    String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseMessaging.getInstance().subscribeToTopic("all");

        //setting the toolbar
        mAuth = FirebaseAuth.getInstance();
        mtoolbar =  findViewById(R.id.mainpagetoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Hush Chat");

        // getting references for viewpager,tablayout and setting up fragments
        mviewpager = findViewById(R.id.maintabpager);
        mtablayout = findViewById(R.id.maintabs);
        msectionadapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mviewpager.setAdapter(msectionadapter);

        mtablayout.setupWithViewPager(mviewpager);

        //getting the current user id
        if(mAuth.getCurrentUser()!=null)
        mUserRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(mAuth.getCurrentUser().getUid());




    }

    /** checking if there is a user logged in or not
        if not then send user to start page **/
    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null) {
           sendToStart();

        }
        else
        {

            /**
             setting the online value to true
             **/
            mUserRef.child("online").setValue("true");
        }

    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//
//    }

    /**
    send the user to start page
     **/
    private void sendToStart() {

        startActivity(new Intent(MainActivity.this, StartActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         setting the last online time
         **/
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    /**
     creating menu in the upper right corner for additional options
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        /**
         if user logs out store the last seen value
         **/
        if (item.getItemId() == R.id.mainlogoutbtn)
        {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            mAuth.signOut();
            Log.v("tes","dgds");
            sendToStart();
        }

        else if(item.getItemId()==R.id.mainaccountbtn)
        {
            startActivity(new Intent(MainActivity.this,SettingsActivity.class));
        }

        else
        {
            startActivity(new Intent(MainActivity.this,UsersActivity.class));
        }


        return true;

    }
}


