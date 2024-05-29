package com.example.socialmediaapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediaapp.adpater.AdapterChat;
import com.example.socialmediaapp.databinding.ActivityChatBinding;
import com.example.socialmediaapp.model.ModelChat;
import com.example.socialmediaapp.model.ModelChatRoom;
import com.example.socialmediaapp.model.ModelUsers;
import com.example.socialmediaapp.utils.AndroidUtil;
import com.example.socialmediaapp.utils.FirebaseUtil;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private ActivityChatBinding binding;
    ModelUsers otherUser;
    String chatroomId;
    ModelChatRoom chatroomModel;
    AdapterChat adapter;
    DatabaseReference chatroomRef;
    DatabaseReference chatroomMessageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLayout();
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(),otherUser.getUid());

        chatroomRef = FirebaseDatabase.getInstance().getReference("chatrooms").child(chatroomId);
        chatroomMessageRef = chatroomRef.child("chats");

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUid()).thenAccept(profilePic -> {
            try {
                Glide.with(this).load(profilePic).into(binding.profilePicLayout.profilePicImageView);
            } catch (Exception ignored) {
                Toast.makeText(this , "error", Toast.LENGTH_SHORT).show();
            }
        });

        binding.backBtn.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());;
        binding.otherUsername.setText(otherUser.getName());


        binding.messageSendBtn.setOnClickListener(v -> {
            String message = binding.chatMessageInput.getText().toString().trim();
            if (message.isEmpty()){
                return;
            }
            sendMessageToUser(message);
        });

        getOrCreateChatroomModel();
        setupChatRecyclerView();

    }

    private void initLayout() {
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    void setupChatRecyclerView(){
        FirebaseRecyclerOptions<ModelChat> options =
                new FirebaseRecyclerOptions.Builder<ModelChat>()
                        .setQuery(chatroomMessageRef.orderByChild("timestamp"), ModelChat.class)
                        .build();

        adapter = new AdapterChat(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(manager);
        binding.chatRecyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                binding.chatRecyclerView.smoothScrollToPosition(0);
            }
        });
    }


    private void sendMessageToUser(String message) {
        Log.d("ChatActivity", "Sending message: " + message);

        chatroomModel.setLastMessageTimestamp(String.valueOf(System.currentTimeMillis()));
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);

        ModelChat chatMessageModel = new ModelChat(message, FirebaseUtil.currentUserId(), String.valueOf(System.currentTimeMillis()));

        DatabaseReference newMessageRef = chatroomMessageRef.push();

        newMessageRef.setValue(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        binding.chatMessageInput.setText("");
                        Log.d("ChatActivity", "Message sent successfully and input cleared");
                        sendNotification(message);
                    } else {
                        Log.e("ChatActivity", "Failed to send message", task.getException());
                    }
                });
    }


    void getOrCreateChatroomModel(){
        chatroomRef.get().addOnCompleteListener(task -> {
           chatroomModel = task.getResult().getValue(ModelChatRoom.class);
           if (chatroomModel == null){
               chatroomModel = new ModelChatRoom(
                       chatroomId,
                       Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUid()),
                       String.valueOf(System.currentTimeMillis()),
                       ""
               );
               chatroomRef.setValue(chatroomModel);
           }
        });

    }

    void sendNotification(String message){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            String currentUserId = currentUser.getUid();
            FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
                task.onSuccessTask(s -> {
                    ModelUsers currentUserModel = task.getResult().getValue(ModelUsers.class);
                    try {
                        JSONObject jsonObject = new JSONObject();

                        JSONObject notificationObj = new JSONObject();
                        notificationObj.put("title", currentUserModel.getName());
                        notificationObj.put("body", message);

                        JSONObject dataObj = new JSONObject();
                        dataObj.put("userId", currentUserId);

                        jsonObject.put("notification", notificationObj);
                        jsonObject.put("data", dataObj);
                        jsonObject.put("to", otherUser.getFcmToken());

                        callApi(jsonObject);
                    } catch (Exception ignored){}
                    return null;
                });
            });
        }

    }

    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer YOUR_API_KEY")
                .build();
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