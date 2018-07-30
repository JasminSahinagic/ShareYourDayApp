package com.example.shareyourdayapp.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.shareyourdayapp.MainActivity;
import com.example.shareyourdayapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private CircleImageView setupImage;
    private Uri profileImage;
    private int imageCounter;
    private StorageReference storageReference;
    private TextInputEditText setupDisplayName;
    private Button setupButton;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firebaseFirestore;
    private String imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        setUpData();
        setupImage.setOnClickListener(this);
        setupButton.setOnClickListener(this);
    }

    public void setUpData(){
        imageCounter=0;
        firebaseFirestore = FirebaseFirestore.getInstance();
        setupImage = this.findViewById(R.id.setupProfileImage);
        toolbar = this.findViewById(R.id.setupToolbar);
        setupDisplayName = this.findViewById(R.id.setupDisplayName);
        setupButton = this.findViewById(R.id.setupButton);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Share Your Day");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        retrieveData();
    }

    public void retrieveData(){
        progressDialog.show();
        setupButton.setEnabled(false);
        firebaseFirestore.collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    if(task.getResult().exists()){
                        imageCounter++;
                        imageUri = task.getResult().getString("image");
                        setupDisplayName.setText(task.getResult().getString("name"));
                        Picasso.with(SetupActivity.this).load(task.getResult().getString("image"))
                                .placeholder(R.drawable.defaultimage)
                                .into(setupImage);
                    }
                }else{
                    Toast.makeText(SetupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
                setupButton.setEnabled(true);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(user == null){
            sendToLogin();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.setupProfileImage:
                userPermission();
                break;
            case R.id.setupButton:
                if(imageCounter==0){
                    setupUserData();
                }else{
                    updateUserData();
                }
                break;
        }
    }

    private void updateUserData() {
        final String displayName = setupDisplayName.getText().toString();
        if(!TextUtils.isEmpty(displayName)){
            progressDialog.setMessage("Update User...");
            progressDialog.show();
            Map<String, String> userData = new HashMap<>();
            userData.put("name",displayName);
            userData.put("image",imageUri);
            firebaseFirestore.collection("Users").document(user.getUid()).set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SetupActivity.this, "User Settings Updated", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        startActivity(new Intent(SetupActivity.this, MainActivity.class));
                        finish();
                    }else{
                        Toast.makeText(SetupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });

        }
    }

    private void setupUserData() {
        final String displayName = setupDisplayName.getText().toString();
        if(!TextUtils.isEmpty(displayName) && profileImage != null ) {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            final StorageReference filepath = storageReference.child("User_profile_images").child(profileImage.getLastPathSegment());
            filepath.putFile(profileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String, String> userData = new HashMap<>();
                            userData.put("name",displayName);
                            userData.put("image",uri.toString());
                            firebaseFirestore.collection("Users").document(user.getUid()).set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SetupActivity.this, "User Settings Updated", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        startActivity(new Intent(SetupActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(SetupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }else{
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendToLogin(){
        startActivity(new Intent(SetupActivity.this, LoginActivity.class));
        finish();
    }

    public void userPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }else{
                userProfileImage();
            }
        }else{
            userProfileImage();
        }
    }

    private void userProfileImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .setActivityTitle("My Crop")
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setCropMenuCropButtonTitle("Done")
                .setRequestedSize(400, 400)
                .setCropMenuCropButtonIcon(R.drawable.donewhite).start(this);
                 setupImage.setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profileImage = result.getUri();
                setupImage.setImageURI(profileImage);
                Toast.makeText(
                        this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG)
                        .show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
