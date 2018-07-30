package com.example.shareyourdayapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareyourdayapp.Model.PostModel;
import com.example.shareyourdayapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<PostModel> postModelList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user;



    public PostAdapter(Context context, List<PostModel> postModelList) {
        this.context = context;
        this.postModelList = postModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        final PostModel model = postModelList.get(position);
        Picasso.with(context).load(model.getPostImage()).into(holder.postItemImage);
        holder.postItemUserInput.setText(model.getDesc());
        holder.postItemDate.setText(model.getTimestamp().toString());
        String temp = model.getUserID();
        getUserData(holder,temp);

        //Likes count
        firebaseFirestore.collection("Posts").document(model.getPostID())
                .collection("Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots == null) return;
                if(!queryDocumentSnapshots.isEmpty()){
                    int count = queryDocumentSnapshots.size();
                    if(count == 1){
                        holder.postItemLikeCount.setText(String.valueOf(count)+" Like");
                    }else {
                        holder.postItemLikeCount.setText(String.valueOf(count)+" Likes");
                    }
                }else{
                    holder.postItemLikeCount.setText("");
                }
            }
        });

        //Likes Icon
        firebaseFirestore.collection("Posts").document(model.getPostID())
                .collection("Likes").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot == null) return;
                if(documentSnapshot.exists()){
                    holder.postItemLikeGrey.setImageDrawable(context.getDrawable(R.mipmap.action_like));
                }else{
                    holder.postItemLikeGrey.setImageDrawable(context.getDrawable(R.mipmap.action_like_grey));
                }
            }
        });

        //Add Likes into Database
        holder.postItemLikeGrey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Posts").document(model.getPostID())
                        .collection("Likes").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.getResult().exists()) {
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Posts").document(model.getPostID())
                                    .collection("Likes")
                                    .document(user.getUid())
                                    .set(likesMap);

                            Map<String, String> notification = new HashMap<>();
                            notification.put("userID", user.getUid());
                            notification.put("postID", model.getPostID());
                            firebaseFirestore.collection("Notification").document(model.getUserID())
                                    .collection("UserNotification")
                                    .document(UUID.randomUUID().toString())
                                    .set(notification);

                        }else{
                            firebaseFirestore.collection("Posts").document(model.getPostID())
                                    .collection("Likes")
                                    .document(user.getUid()).delete();

                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    public void getUserData(final ViewHolder holder, String userID){
        firebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    holder.postItemDisplayName.setText(task.getResult().getString("name"));
                    Picasso.with(context).load(task.getResult().getString("image"))
                            .into(holder.postItemUserImage);
                }else{
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView postItemUserImage;
        public ImageView postItemImage;
        public TextView postItemDisplayName;
        public TextView postItemDate;
        public TextView postItemUserInput;
        public TextView postItemLikeCount;
        public ImageView postItemLikeGrey;
        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            postItemUserImage = this.itemView.findViewById(R.id.postDetailsUserImage);
            postItemImage = this.itemView.findViewById(R.id.postDetailsImage);
            postItemDisplayName = this.itemView.findViewById(R.id.postDetailsDisplayName);
            postItemDate = this.itemView.findViewById(R.id.postItemDate);
            postItemUserInput = this.itemView.findViewById(R.id.postDetailsUserInput);
            postItemLikeCount = this.itemView.findViewById(R.id.postItemLikeCount);
            postItemLikeGrey = this.itemView.findViewById(R.id.postItemLikeGrey);
        }
    }
}
