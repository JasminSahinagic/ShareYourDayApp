package com.example.shareyourdayapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.shareyourdayapp.MainActivity;
import com.example.shareyourdayapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import id.zelory.compressor.Compressor;

import static io.opencensus.tags.TagValue.MAX_LENGTH;

public class PostActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ImageView postImageView;
    private EditText postUserInput;
    private Button postButton;
    private Uri postImage;
    private ProgressBar progressBar;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setUpPost();
        postImageView.setOnClickListener(this);
        postButton.setOnClickListener(this);
    }

    public void setUpPost(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressBar = this.findViewById(R.id.postProgressBar);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        postImageView = this.findViewById(R.id.postImageView);
        postUserInput = this.findViewById(R.id.postUserInput);
        postButton = this.findViewById(R.id.postButton);
        toolbar = this.findViewById(R.id.postToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.postImageView:
                userPostImage();
                break;
            case R.id.postButton:
                createPost();
                break;
        }
    }

    public void createPost() {
        final String userInput = postUserInput.getText().toString();
        if(!TextUtils.isEmpty(userInput) && postImage != null) {
            progressDialog.setMessage("Posting...");
            progressDialog.show();
            String random = UUID.randomUUID().toString();
            final StorageReference filepath = storageReference.child("User_post_images").child(postImage.getLastPathSegment());
            filepath.putFile(postImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Date c = Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                            String formattedDate = df.format(c);
                            String random = UUID.randomUUID().toString();
                            Map<String, Object> userPostData = new HashMap<>();
                            userPostData.put("postImage", uri.toString());
                            userPostData.put("desc", userInput);
                            userPostData.put("userID", user.getUid());
                            userPostData.put("timestamp", formattedDate);
                            userPostData.put("orderBy",  FieldValue.serverTimestamp());
                            userPostData.put("postID",  user.getUid()+random);
                            firebaseFirestore.collection("Posts").document(user.getUid()+random).set(userPostData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(PostActivity.this, "Successfully Posted", Toast.LENGTH_SHORT).show();
                                        sendToMain();
                                        progressDialog.dismiss();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    } else {
                                        Toast.makeText(PostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });

                        }
                    });

                }
            });
        }
    }


    public void sendToMain(){
        startActivity(new Intent(PostActivity.this, MainActivity.class));
        finish();
    }

    public void userPostImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .setMinCropResultSize(512,512)
                .setActivityTitle("User Post")
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setCropMenuCropButtonTitle("Done")
                .setRequestedSize(512, 512)
                .setCropMenuCropButtonIcon(R.drawable.donewhite).start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImage = result.getUri();
                postImageView.setImageURI(postImage);
                Toast.makeText(
                        this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG)
                        .show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
