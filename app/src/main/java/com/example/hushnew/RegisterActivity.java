package com.example.hushnew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hush.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Objects;
/**
 This activity handles the registeration page
 **/
public class RegisterActivity extends AppCompatActivity {

    EditText email,name,pass;
    Button reg;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private Button mCreateBtn;
    String user_token;

    //Progress Dialog
    private ProgressDialog mRegProgress;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //setting toolbar
        mToolbar= (androidx.appcompat.widget.Toolbar) findViewById(R.id.regappbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setting progressbar
        mRegProgress=new ProgressDialog(this);

        email = findViewById(R.id.regemail);
        name = findViewById(R.id.regname);
        pass = findViewById(R.id.regpassword);
        reg = findViewById(R.id.regbutton);
        mAuth = FirebaseAuth.getInstance();

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nm=name.getText().toString();
                String em=email.getText().toString();
                String psd=pass.getText().toString();

                //getting the unique user token required for setting up the notifications

                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (task.isSuccessful()) {
                                    user_token = Objects.requireNonNull(task.getResult()).getToken();

                                }

                            }
                        });



                if (!TextUtils.isEmpty(nm)||!TextUtils.isEmpty(em)||!TextUtils.isEmpty(psd))
                {
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while account is Created!");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    registeruser(nm,em,psd);
                }
            }
        });
    }

    //registering the user and adding different fields for the user
    private void registeruser(String disname,String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    //setting the various user fields
                    HashMap<String, String> userMap= new HashMap<>();
                    userMap.put("name", disname);
                    userMap.put("image","default");
                    userMap.put("status","Hi, I'm using Hushhh");
                    userMap.put("thumb_image","default");
                    userMap.put("token",user_token);

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mRegProgress.dismiss();
                                Intent mainintent= new Intent(RegisterActivity.this, MainActivity.class);

                                //clearing all the old activities from the stack
                                mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainintent);
                                finish();
                            }
                        }
                    });



                }

                else
                {
                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this,"Some Error Occurred",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}