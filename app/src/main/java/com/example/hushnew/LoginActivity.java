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

/**
 Handles the login page
 **/
public class LoginActivity extends AppCompatActivity {

    Toolbar mtoolbar;

    EditText email,pass;
    Button lgbtn;
    FirebaseAuth mauth;
    ProgressDialog mprogressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mtoolbar = findViewById(R.id.loginappbar);
        mauth = FirebaseAuth.getInstance();
        mprogressbar = new ProgressDialog(this);


        /**
         setting up the toolbar
         **/
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Hello there !");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         getting references to various textviews and buttons
         **/
        email = findViewById(R.id.loginemail);
        pass = findViewById(R.id.logpassword);
        lgbtn = findViewById(R.id.logbtn);

        lgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString();
                String psd = pass.getText().toString();

                if(!TextUtils.isEmpty(em) && !TextUtils.isEmpty(psd))
                {
                    mprogressbar.setTitle("Registering User");
                    mprogressbar.setMessage("Please wait while we set you up!");
                    mprogressbar.setCanceledOnTouchOutside(false);
                    mprogressbar.show();
                    logUser(em,psd);
                }

            }
        });



    }

    //logging in user
    private void logUser(String em, String psd) {

        mauth.signInWithEmailAndPassword(em,psd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    mprogressbar.dismiss();
                    Intent mainintent = new Intent(LoginActivity.this,MainActivity.class);
                    mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainintent);
                    finish();
                }

                else
                {
                    mprogressbar.hide();
                    Toast.makeText(LoginActivity.this,"Sach Sach bata bhai",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}