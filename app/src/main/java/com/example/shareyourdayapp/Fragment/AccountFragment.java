package com.example.shareyourdayapp.Fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareyourdayapp.Activity.SetupActivity;
import com.example.shareyourdayapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private CircleImageView accountImage;
    private TextView accountDisplayName;
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private FirebaseUser user;



    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_account, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        accountImage = this.view.findViewById(R.id.accountImage);
        accountDisplayName = this.view.findViewById(R.id.accountDisplayName);
        progressDialog = new ProgressDialog(getActivity());
        userData();
        return  view;
    }

    public void userData(){
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        firebaseFirestore.collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        String image = task.getResult().getString("image");
                        accountDisplayName.setText(task.getResult().getString("name"));
                        Picasso.with(getActivity()).load(image)
                                .placeholder(R.drawable.defaultimage)
                                .into(accountImage);
                    }
                }else{
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

                progressDialog.dismiss();

            }
        });
    }

}
