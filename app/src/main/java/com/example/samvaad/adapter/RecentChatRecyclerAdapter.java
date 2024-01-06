package com.example.samvaad.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samvaad.ChatActivity;
import com.example.samvaad.R;
import com.example.samvaad.model.ChatroomModel;
import com.example.samvaad.model.UserModel;
import com.example.samvaad.utils.AndroidUtil;
import com.example.samvaad.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.CurrentUserId());
                    UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                    FirebaseUtil.getotherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Uri uri = task.getResult();
                                AndroidUtil.setProfilePic(context,uri,holder.profilePic);
                            }
                        }
                    });

                    holder.usernameText.setText(otherUserModel.getUsername());
                    if (lastMessageSentByMe) {
                        holder.lastmessageText.setText("You: " + model.getLastMessage());
                    }
                    else{
                        holder.lastmessageText.setText(model.getLastMessage());}

                    holder.lastmessageTime.setText(FirebaseUtil.timestampToString(model.getLastmessageTimestamp()));

                    holder.itemView.setOnClickListener(v -> {
                        //navigate to chat activity
                        Intent intent = new Intent(context, ChatActivity.class);
                        AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    });


                }
            }
        });

    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recyclerrow,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView lastmessageText;
        TextView lastmessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            lastmessageText = itemView.findViewById(R.id.last_message_text);
            lastmessageTime=itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_picture);


        }
    }
}
