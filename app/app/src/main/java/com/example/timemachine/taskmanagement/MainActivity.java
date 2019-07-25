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

public class MainActivity extends AppCompatActivity {

    private TextView signup_text;

    private EditText email, password;
    private Button btn_login;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check Current User And Delegate to HomeActivity Accordingly
        // Fire base related
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        signup_text = findViewById(R.id.signup_text);

        signup_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });

        // Activity Widget Assignment
        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        btn_login = findViewById(R.id.login_btn);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String mEmail = email.getText().toString().trim();
                final String mPassword = password.getText().toString().trim();
                if(TextUtils.isEmpty(mEmail)){
                    email.setError("Please Enter Valid Email");
                    return;
                }
                if(TextUtils.isEmpty(mPassword)){
                    password.setError("Check Password");
                    return;
                }

                mProgress.setMessage("Please wait");
                mProgress.show();

                mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            mProgress.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "Problem Occur While Login", Toast.LENGTH_LONG).show();
                            mProgress.dismiss();
                        }
                    }
                });
            }
        });

    }
}
