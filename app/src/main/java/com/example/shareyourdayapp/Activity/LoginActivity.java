package com.example.shareyourdayapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.shareyourdayapp.MainActivity;
import com.example.shareyourdayapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText loginUserEmail;
    private TextInputEditText loginUserPassword;
    private Button loginButton;
    private Button loginCreateAccountButton;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpLogin();
        loginButton.setOnClickListener(this);
        loginCreateAccountButton.setOnClickListener(this);
    }

    public void setUpLogin(){
        loginUserEmail = this.findViewById(R.id.loginUserEmail);
        loginUserPassword = this.findViewById(R.id.loginUserPassword);
        loginButton = this.findViewById(R.id.registerCreateAccount);
        loginCreateAccountButton = this.findViewById(R.id.loginCreateAccountButton);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(user != null){
            sendToMain();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.registerCreateAccount:
                userLogin();
                break;
            case R.id.loginCreateAccountButton:
                sendToRegisterActivity();
                break;
        }
    }

    private void userLogin() {
        String email = loginUserEmail.getText().toString();
        String password = loginUserPassword.getText().toString();
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        sendToMain();
                    }else{
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            });
        }
    }

    public void sendToMain(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    public void sendToRegisterActivity(){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}
