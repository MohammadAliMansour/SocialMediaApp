package com.example.socialmediaapp.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
//    EditText messageInput;
//    ImageButton sendMessageBtn;
//    ImageButton backBtn;
//    TextView otherUsername;
//    RecyclerView recyclerView;
//    ImageView imageView;
    DatabaseReference chatroomRef;
    DatabaseReference chatroomMessageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLayout();

        //get UserModel
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(),otherUser.getUid());

//        messageInput = findViewById(R.id.chat_message_input);
//        sendMessageBtn = findViewById(R.id.message_send_btn);
//        backBtn = findViewById(R.id.back_btn);
//        otherUsername = findViewById(R.id.other_username);
//        recyclerView = findViewById(R.id.chat_recycler_view);
//        imageView = findViewById(R.id.profile_pic_image_view);

        chatroomRef = FirebaseDatabase.getInstance().getReference("chatrooms").child(chatroomId);
        chatroomMessageRef = chatroomRef.child("chats");

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUid()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri  = t.getResult();
                        AndroidUtil.setProfilePic(this,uri,binding.profilePicLayout.profilePicImageView);
                    }
                });

        binding.backBtn.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        //        backBtn.setOnClickListener((v)-> onBackPressed());
//        otherUsername.setText(otherUser.getName());
        binding.otherUsername.setText(otherUser.getName());

//        sendMessageBtn.setOnClickListener((v -> {
//            String message = messageInput.getText().toString().trim();
//            if(message.isEmpty())
//                return;
//            sendMessageToUser(message);
//        }));
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
//        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
//                .orderBy("timestamp", Query.Direction.DESCENDING);
//
//        FirestoreRecyclerOptions<ModelChat> options = new FirestoreRecyclerOptions.Builder<ModelChat>()
//                .setQuery(query,ModelChat.class).build();

        FirebaseRecyclerOptions<ModelChat> options =
                new FirebaseRecyclerOptions.Builder<ModelChat>()
                        .setQuery(chatroomMessageRef.orderByChild("timestamp"), ModelChat.class)
                        .build();

        adapter = new AdapterChat(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        binding.chatRecyclerView.setLayoutManager(manager);
        binding.chatRecyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(manager);
//        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
//                recyclerView.smoothScrollToPosition(0);
                binding.chatRecyclerView.smoothScrollToPosition(0);
            }
        });
    }

//    void sendMessageToUser(String message){
////        Log.w("bla bla bla bla", Timestamp.now().toString());
//        chatroomModel.setLastMessageTimestamp(String.valueOf(System.currentTimeMillis()));
//        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
//        chatroomModel.setLastMessage(message);
//        chatroomRef.setValue(chatroomModel);
////        FirebaseUtil.getChatroomReference(chatroomId).setValue(chatroomModel);
//
//        ModelChat chatMessageModel = new ModelChat(message,FirebaseUtil.currentUserId(),String.valueOf(System.currentTimeMillis()));
//        chatroomMessageRef.push().setValue(chatMessageModel)
//                .addOnCompleteListener(task -> {
//                   if (task.isSuccessful()){
////                       messageInput.setText("");
//                       binding.chatMessageInput.setText("");
//                       sendNotification(message);
//                   }
//                });
//
////        FirebaseUtil.getChatroomMessageReference(chatroomId).setValue(chatMessageModel)
////                .addOnCompleteListener(task -> {
////                    if(task.isSuccessful()){
////                        binding.chatMessageInput.setText("");
////                        sendNotification(message);
////                    }
////                });
//    }

    void sendMessageToUser(String message){
        // Log message being sent for debugging
        Log.d("ChatActivity", "Sending message: " + message);

        chatroomModel.setLastMessageTimestamp(String.valueOf(System.currentTimeMillis()));
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);

        // Update the chatroom model with the last message details
        chatroomRef.setValue(chatroomModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ChatActivity", "Chatroom model updated successfully");
            } else {
                Log.e("ChatActivity", "Failed to update chatroom model", task.getException());
            }
        });

        // Create a new chat message model
        ModelChat chatMessageModel = new ModelChat(message, FirebaseUtil.currentUserId(), String.valueOf(System.currentTimeMillis()));

        // Push the new message to the chatroomMessageRef
        chatroomMessageRef.push().setValue(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear the input field
                        binding.chatMessageInput.setText("");
                        Log.d("ChatActivity", "Message sent successfully and input cleared");
                        // Send notification
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

//        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                chatroomModel = task.getResult().getValue(ModelChatRoom.class);
//                if(chatroomModel==null){
//                    //first time chat
//                    chatroomModel = new ModelChatRoom(
//                            chatroomId,
//                            Arrays.asList(FirebaseUtil.currentUserId(),otherUser.getUid()),
//                            Timestamp.now().toString(),
//                            ""
//                    );
//                    FirebaseUtil.getChatroomReference(chatroomId).setValue(chatroomModel);
//                }
//            }
//        });
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

//        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                ModelUsers currentUser1 = task.getResult().getValue(ModelUsers.class);
//                try{
//                    JSONObject jsonObject  = new JSONObject();
//
//                    JSONObject notificationObj = new JSONObject();
//                    notificationObj.put("title",currentUser1.getName());
//                    notificationObj.put("body",message);
//
//                    JSONObject dataObj = new JSONObject();
//                    dataObj.put("userId",currentUser1.getUid());
//
//                    jsonObject.put("notification",notificationObj);
//                    jsonObject.put("data",dataObj);
//                    jsonObject.put("to",otherUser.getFcmToken());
//
//                    callApi(jsonObject);
//
//
//                }catch (Exception ignored){
//
//                }
//
//            }
//        });

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