package com.example.shareyourdayapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.style.TabStopSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareyourdayapp.Activity.PostDetailsActivity;
import com.example.shareyourdayapp.Model.NotificationModel;
import com.example.shareyourdayapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {


    private Context context;
    private List<NotificationModel> notificationModelList;
    private FirebaseFirestore firebaseFirestore;

    public NotificationAdapter(Context context, List<NotificationModel> notificationModelList) {
        this.context = context;
        this.notificationModelList = notificationModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item,parent,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        NotificationModel model = notificationModelList.get(position);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users").document(model.getUserID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if(task.isSuccessful()){
                   holder.notificationItemDisplayName.setText(task.getResult().getString("name"));
                   holder.notificationItemNot.setTextColor(Color.RED);
                   holder.notificationItemNot.setText("Likes Your Post");
                   Picasso.with(context).load(task.getResult().getString("image"))
                           .into(holder.notificationItemImage);
               }    else{
                   Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
               }
            }
        });


    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView notificationItemImage;
        public TextView notificationItemDisplayName;
        public TextView notificationItemNot;

        public ViewHolder(@NonNull View itemView, final Context context) {
            super(itemView);
            notificationItemImage = this.itemView.findViewById(R.id.notificationItemImage);
            notificationItemDisplayName = this.itemView.findViewById(R.id.notificationItemDisplayName);
            notificationItemNot = this.itemView.findViewById(R.id.notificationItemNot);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NotificationModel model = notificationModelList.get(getAdapterPosition());
                    Intent intent = new Intent(context, PostDetailsActivity.class);
                    intent.putExtra("PostData",model);
                    context.startActivity(intent);
                    notificationModelList.clear();
                }
            });
        }
    }
}
