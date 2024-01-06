package com.example.samvaad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.samvaad.adapter.ChatRecyclerAdapter;
import com.example.samvaad.adapter.SearchUserRecyclerAdapter;
import com.example.samvaad.model.ChatMessageModel;
import com.example.samvaad.model.ChatroomModel;
import com.example.samvaad.model.UserModel;
import com.example.samvaad.utils.AndroidUtil;
import com.example.samvaad.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatRoomId;
    ChatroomModel chatroomModel;
    EditText messageInput;
    ImageButton backBtn;
    ImageView sendbtn;
    ImageView imageView;
    TextView otherUsername;
    RecyclerView recyclerView;
    ChatRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get UserModel
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatRoomId = FirebaseUtil.getChatroomId(FirebaseUtil.CurrentUserId(),otherUser.getUserId());

        messageInput = findViewById(R.id.chat_input);
        backBtn = findViewById(R.id.back_button);
        sendbtn = findViewById(R.id.send_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recyclerview);
        imageView = findViewById(R.id.profile_pic);

        FirebaseUtil.getotherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri uri = task.getResult();
                    AndroidUtil.setProfilePic(getApplicationContext(),uri,imageView);
                }
            }
        });

        backBtn.setOnClickListener(view -> {
            onBackPressed();
        });



        otherUsername.setText(otherUser.getUsername());

        sendbtn.setOnClickListener(view -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);

        });

        getOrCreateChatroomModel();
        setUpChatRecyclerView();


    }
    void setUpChatRecyclerView(){
        Query query = FirebaseUtil.getChatroomMessageReference(chatRoomId).orderBy("timestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>().setQuery(query, ChatMessageModel.class).build()
                ;
        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessageToUser(String message){
        chatroomModel.setLastmessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.CurrentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatRoomId).set(chatroomModel);


        ChatMessageModel chatMessageModel = new ChatMessageModel(message,FirebaseUtil.CurrentUserId(),Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatRoomId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    messageInput.setText("");
                    sendNotification(message);
                }
            }
        });
    }
    void getOrCreateChatroomModel(){
        FirebaseUtil.getChatroomReference(chatRoomId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    chatroomModel = task.getResult().toObject(ChatroomModel.class);
                    if(chatroomModel==null){
                        //first time chat
                        chatroomModel = new ChatroomModel(
                             chatRoomId,
                                Arrays.asList(FirebaseUtil.CurrentUserId(),otherUser.getUserId()), Timestamp.now(),
                                ""
                        );
                        FirebaseUtil.getChatroomReference(chatRoomId).set(chatroomModel);
                    }
                }
            }
        });
    }
    void sendNotification(String message){
            // current username,message , currentuserId, otherusertoken
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try{

                    JSONObject jsonObject = new JSONObject();


                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title",currentUser.getUsername());
                    notificationObj.put("body",message);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("userId",currentUser.getUserId());

                    jsonObject.put("notification",notificationObj);
                    jsonObject.put("data",dataObj);
                    jsonObject.put("to",otherUser.getFcmToken());

                    callApi(jsonObject);

                }catch (Exception e){

                }
            }
        });
    }
    void callApi(JSONObject jsonObject){
         MediaType JSON = MediaType.get("application/json;charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder().url(url).post(body).header("Authorization","Bearer AAAAiKZKTT4:APA91bE1EwCiIK1yipMIZYQi_7tEJp2szpysEpXmS2Dfli_u6fc09DBZhviqUq2bP-bOydMTaPRzWaYh-WiMYX2RZy-xNDCgcUyw0ZzmieBB3lSIy-JJHjktX4GcV5D5-eBFL9rwfb_p").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }

}