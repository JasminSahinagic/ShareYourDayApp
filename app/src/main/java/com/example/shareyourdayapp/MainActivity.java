package com.example.shareyourdayapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.shareyourdayapp.Activity.LoginActivity;
import com.example.shareyourdayapp.Activity.PostActivity;
import com.example.shareyourdayapp.Activity.SetupActivity;
import com.example.shareyourdayapp.Fragment.AccountFragment;
import com.example.shareyourdayapp.Fragment.HomeFragment;
import com.example.shareyourdayapp.Fragment.NotificationFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private Toolbar toolbar;
    private BottomNavigationView mainBottomNav;
    private FirebaseFirestore firebaseFirestore;
    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMain();
        mainBottomNav.setOnNavigationItemSelectedListener(this);
    }

    public void setUpMain(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        toolbar = this.findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Share Your Day");
        mainBottomNav = this.findViewById(R.id.mainBottomNav);
        homeFragment = new HomeFragment();
        notificationFragment = new NotificationFragment();
        accountFragment = new AccountFragment();
        replaceFragment(homeFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(user == null){
            sendToLogin();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionLogout:
                logOut();
                break;
            case R.id.actionAccountSettings:
                sendToSetup();
                break;
            case R.id.actionAddPost:
                sendToPostActivity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logOut() {
        auth.signOut();
        sendToLogin();
    }

    public void sendToSetup(){
        startActivity(new Intent(MainActivity.this, SetupActivity.class));
    }

    public void sendToLogin(){
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }


    public void sendToPostActivity(){
        startActivity(new Intent(MainActivity.this, PostActivity.class));
    }

    public void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrameLayout, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionHome:
                replaceFragment(homeFragment);
                return true;
            case R.id.actionNotification:
                replaceFragment(notificationFragment);
                return true;
            case R.id.actionAccount:
                replaceFragment(accountFragment);
                return true;
                default:
                    return false;
        }
    }
}
