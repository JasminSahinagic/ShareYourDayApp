package com.example.shareyourdayapp.Activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareyourdayapp.Model.NotificationModel;
import com.example.shareyourdayapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailsActivity extends AppCompatActivity {

    private CircleImageView postDetailsUserImage;
    private ImageView postDetailsImage;
    private TextView postDetailsDisplayName;
    private TextView postDetailsDate;
    private TextView postDetailsUserInput;
    private NotificationModel model;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        setUpDetails();
    }

    public void setUpDetails(){
        toolbar = this.findViewById(R.id.detailsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Post Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        postDetailsUserImage = this.findViewById(R.id.postDetailsUserImage);
        postDetailsImage = this.findViewById(R.id.postDetailsImage);
        postDetailsDisplayName = this.findViewById(R.id.postDetailsDisplayName);
        postDetailsDate = this.findViewById(R.id.postDetailsDate);
        postDetailsUserInput = this.findViewById(R.id.postDetailsUserInput);
        firebaseFirestore = FirebaseFirestore.getInstance();
        model = (NotificationModel) getIntent().getSerializableExtra("PostData");
        retrieveData();
    }

    public void retrieveData(){

        firebaseFirestore.collection("Users").document(model.getUserID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    progressDialog.setMessage("Retrieving Post Data");
                    progressDialog.show();
                    postDetailsDisplayName.setText(task.getResult().getString("name"));
                    Picasso.with(PostDetailsActivity.this).load(task.getResult().getString("image"))
                            .into(postDetailsUserImage);
                    firebaseFirestore.collection("Posts")
                            .document(model.getPostID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                postDetailsUserInput.setText(task.getResult().getString("desc"));
                                postDetailsDate.setText(task.getResult().getString("timestamp"));
                                Picasso.with(PostDetailsActivity.this).load(task.getResult().getString("postImage"))
                                        .into(postDetailsImage);
                                progressDialog.dismiss();
                            }else{
                                Toast.makeText(PostDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }else{
                    Toast.makeText(PostDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK)
            Toast.makeText(getApplicationContext(), "Back button disabled",
                    Toast.LENGTH_LONG).show();
        return false;
    }
}
