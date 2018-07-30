package com.example.shareyourdayapp.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shareyourdayapp.Adapter.PostAdapter;
import com.example.shareyourdayapp.ExClass.PostID;
import com.example.shareyourdayapp.Model.PostModel;
import com.example.shareyourdayapp.R;
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
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private View view;
    private Context context;
    private FirebaseUser user;
    private List<PostModel> postModelList;
    private FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot documentSnapshot;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = view.getContext();
        postModelList = new ArrayList<>();
        recyclerView = this.view.findViewById(R.id.recyclerViewHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        firebaseFirestore = FirebaseFirestore.getInstance();
        adapter = new PostAdapter(context, postModelList);
        recyclerView.setAdapter(adapter);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            retrieveData();
        }
        return view;
    }

    public void retrieveData() {
            Query query = firebaseFirestore.collection("Posts").orderBy("orderBy", Query.Direction.DESCENDING);
            query.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                   if(queryDocumentSnapshots == null) return;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        documentSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String userPostID = doc.getDocument().getId();
                                PostModel model = doc.getDocument().toObject(PostModel.class).withId(userPostID);
                                postModelList.add(model);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
    }


}
