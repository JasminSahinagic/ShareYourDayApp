package com.example.shareyourdayapp.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shareyourdayapp.Adapter.NotificationAdapter;
import com.example.shareyourdayapp.Model.NotificationModel;
import com.example.shareyourdayapp.Model.PostModel;
import com.example.shareyourdayapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private View view;
    private Context context;
    private FirebaseUser user;
    private List<NotificationModel> notificationModelList;
    private FirebaseFirestore firebaseFirestore;


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        context = view.getContext();
        notificationModelList = new ArrayList<>();
        recyclerView = this.view.findViewById(R.id.recyclerViewNotification);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        firebaseFirestore = FirebaseFirestore.getInstance();
        adapter = new NotificationAdapter(context, notificationModelList);
        recyclerView.setAdapter(adapter);
        retrieveNotification();
        return view;
    }

    public void retrieveNotification() {
        firebaseFirestore.collection("Notification").document(user.getUid()).collection("UserNotification").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots == null) return;
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    String notificationID = doc.getDocument().getId();
                    firebaseFirestore.collection("Notification")
                            .document(user.getUid())
                            .collection("UserNotification").document(notificationID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                NotificationModel model = new NotificationModel();
                                model.setPostID(task.getResult().getString("postID"));
                                model.setUserID(task.getResult().getString("userID"));
                                Log.d("Testiranjeeee", model.getUserID() +"-----"+ model.getPostID());
                                notificationModelList.add(model);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });

    }
}