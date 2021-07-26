package com.example.hushnew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hush.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 Activity to change the status of the user
 **/

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DatabaseReference mstatusdb;
    private FirebaseUser mcurruser;
    private EditText statipt;
    private Button statusbtn;
    private ProgressDialog mRegProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mToolbar =  findViewById(R.id.statusappbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Update Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mcurruser = FirebaseAuth.getInstance().getCurrentUser();
        String Curruserid = mcurruser.getUid();
        mstatusdb = FirebaseDatabase.getInstance().getReference().child("Users").child(Curruserid);

        statipt = findViewById(R.id.statusiinput);
        statusbtn = findViewById(R.id.statusbtn);

        String statusval = getIntent().getStringExtra("statusval");
        statipt.setText(statusval.toString());
        Log.v("TAG", statusval);

        //online
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mUserRef;
        mUserRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(mAuth.getCurrentUser().getUid());
        mUserRef.child("online").setValue("true");

        statusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = statipt.getText().toString();
                mRegProgress = new ProgressDialog(StatusActivity.this);
                mRegProgress.setTitle("Registering User");
                mRegProgress.setMessage("Please wait while account is Created!");
                mRegProgress.setCanceledOnTouchOutside(false);
                mRegProgress.show();
                mstatusdb.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {


                            mRegProgress.dismiss();
                            Toast.makeText(StatusActivity.this, "Successfull!", Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            Toast.makeText(StatusActivity.this,"Successfull!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



    }
}