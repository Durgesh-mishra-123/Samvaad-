package com.example.samvaad.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samvaad.ChatActivity;
import com.example.samvaad.R;
import com.example.samvaad.model.ChatMessageModel;
import com.example.samvaad.utils.AndroidUtil;
import com.example.samvaad.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.w3c.dom.Text;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
     if(model.getSenderId().equals(FirebaseUtil.CurrentUserId())){
         holder.leftchatlayout.setVisibility(View.GONE);
         holder.rightchatlayout.setVisibility(View.VISIBLE);
         holder.rightchatTextview.setText(model.getMessage());
     }
     else{
         holder.leftchatlayout.setVisibility(View.VISIBLE);
         holder.rightchatlayout.setVisibility(View.GONE);
         holder.leftchatTextview.setText(model.getMessage());
     }
       
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recylerrow,parent,false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftchatlayout,rightchatlayout;
        TextView leftchatTextview,rightchatTextview;
        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            leftchatlayout = itemView.findViewById(R.id.leftchat_layout);
            rightchatlayout = itemView.findViewById(R.id.rightchat_layout);
            leftchatTextview = itemView.findViewById(R.id.leftchat_textview);
            rightchatTextview = itemView.findViewById(R.id.rightchat_textview);

        }
    }
}
