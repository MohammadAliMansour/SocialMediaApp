package com.example.socialmediaapp.adpater;

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

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.ChatActivity;
import com.example.socialmediaapp.model.ModelChatRoom;
import com.example.socialmediaapp.model.ModelUsers;
import com.example.socialmediaapp.utils.AndroidUtil;
import com.example.socialmediaapp.utils.FirebaseUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class RecentChatRecyclerAdapter extends FirebaseRecyclerAdapter<ModelChatRoom, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirebaseRecyclerOptions<ModelChatRoom> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ModelChatRoom model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> task.onSuccessTask(dataSnapshot -> {

                    boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                    ModelUsers otherUserModel = task.getResult().getValue(ModelUsers.class);

                    FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel.getUid()).getDownloadUrl()
                            .addOnCompleteListener(t -> {
                                if (t.isSuccessful()) {
                                    Uri uri = t.getResult();
                                    AndroidUtil.setProfilePic(context, uri, holder.profilePic);
                                }
                            });

                    holder.usernameText.setText(otherUserModel.getName());
                    if (lastMessageSentByMe)
                        holder.lastMessageText.setText("You : " + model.getLastMessage());
                    else
                        holder.lastMessageText.setText(model.getLastMessage());
                    holder.lastMessageTime.setText(model.getLastMessageTimestamp());

                    holder.itemView.setOnClickListener(v -> {
                        //navigate to chat activity
                        Intent intent = new Intent(context, ChatActivity.class);
                        AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    });
                    return null;
                }));
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}