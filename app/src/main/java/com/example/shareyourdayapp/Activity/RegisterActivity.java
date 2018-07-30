package com.example.shareyourdayapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.shareyourdayapp.MainActivity;
import com.example.shareyourdayapp.R;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private TextInputEditText registerUserEmail;
    private TextInputEditText registerUserPassword;
    private TextInputEditText registerConfirmPassword;
    private Button registerCreateAccount;
    private Button registerAlreadyHAC;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setUpRegister();
        registerCreateAccount.setOnClickListener(this);
    }

    public void setUpRegister(){
        progressDialog = new ProgressDialog(this);
        registerUserEmail = this.findViewById(R.id.registerUserEmail);
        registerUserPassword = this.findViewById(R.id.registerUserPassword);
        registerConfirmPassword = this.findViewById(R.id.registerConfirmPassword);
        registerCreateAccount = this.findViewById(R.id.registerCreateAccount);
        registerAlreadyHAC = this.findViewById(R.id.registerAlreadyHAC);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
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
                createAccount();
                break;
            case R.id.registerAlreadyHAC:
                finish();
                break;
        }
    }

    public void createAccount(){
        String email = registerUserEmail.getText().toString();
        String password = registerUserPassword.getText().toString();
        String confPassword = registerConfirmPassword.getText().toString();
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confPassword)){
            if(password.equals(confPassword)){
                progressDialog.setMessage("Registration...");
                progressDialog.show();
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            sendToSetup();
                        }else{
                            Toast.makeText(RegisterActivity.this, "Registration Failed" , Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
            }else{
                Toast.makeText(this, "Confirm Password Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendToMain() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }

    public  void sendToSetup(){
        startActivity(new Intent(RegisterActivity.this, SetupActivity.class));
        finish();
    }

}
