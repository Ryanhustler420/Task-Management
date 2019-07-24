package com.example.timemachine.taskmanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private EditText username, email, password;
    private Button btnReg;
    private TextView loginTxt;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProgress = new ProgressDialog(this);

        username = findViewById(R.id.username_signup);
        email = findViewById(R.id.email_signup);
        password = findViewById(R.id.password_signup);

        btnReg = findViewById(R.id.signup_btn);
        loginTxt = findViewById(R.id.login_text);

        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();
                final String mUsername = username.getText().toString().trim();

                if(TextUtils.isEmpty(mEmail)){
                    email.setError("Required Field");
                    return;
                }
                if(TextUtils.isEmpty(mPassword)){
                    password.setError("Required Field");
                    return;
                }

                if(TextUtils.isEmpty(mUsername)) {
                    username.setError("Required Field");
                    return;
                }

                mProgress.setMessage("Processing...");
                mProgress.show();

                mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Successfully Account Created", Toast.LENGTH_SHORT).show();
                            // add additional fields related to user,
                            String id = mDatabase.push().getKey();
                            User newUser = new User(mUsername,mEmail,id);
                            mDatabase.child("Users").child(id).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mProgress.dismiss();
                                        startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                                    }else {
                                        Toast.makeText(getApplicationContext(), "Problem Occur", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(getApplicationContext(), "Problem Occur", Toast.LENGTH_LONG).show();
                            mProgress.dismiss();
                        }
                    }
                });
            }
        });
    }
}
