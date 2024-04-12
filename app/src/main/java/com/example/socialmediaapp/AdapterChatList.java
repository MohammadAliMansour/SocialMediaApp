package com.example.socialmediaapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.Holder> {

    Context context;
    FirebaseAuth firebaseAuth;
    String uid;

    public AdapterChatList(Context context, List<ModelUsers> users) {
        this.context = context;
        this.usersList = users;
        lastMessageMap = new HashMap<>();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
    }

    List<ModelUsers> usersList;
    private final HashMap<String, String> lastMessageMap;

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {

        final String hisUid = usersList.get(position).getUid();
        String userImage = usersList.get(position).getImage();
        String username = usersList.get(position).getName();
        String lastMessage = lastMessageMap.get(hisUid);
        holder.name.setText(username);
        holder.block.setImageResource(R.drawable.ic_unblock);

        // if no last message then Hide the layout
        if (lastMessage == null || lastMessage.equals("default")) {
            holder.lastMessage.setVisibility(View.GONE);
        } else {
            holder.lastMessage.setVisibility(View.VISIBLE);
            holder.lastMessage.setText(lastMessage);
        }
        try {
            // loading profile pic of user
            Glide.with(context).load(userImage).into(holder.profile);
        } catch (Exception ignored) {

        }

        // redirecting to chat activity on item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);

            // putting uid of user in extras
            intent.putExtra("uid", hisUid);
            context.startActivity(intent);
        });

    }

    // setting last message sent by users.
    public void setLastMessageMap(String userId, String lastMessage) {
        lastMessageMap.put(userId, lastMessage);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView profile, status, block, seen;
        TextView name, lastMessage;

        public Holder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profileimage);
            status = itemView.findViewById(R.id.onlinestatus);
            name = itemView.findViewById(R.id.nameonline);
            lastMessage = itemView.findViewById(R.id.lastmessge);
            block = itemView.findViewById(R.id.blocking);
            seen = itemView.findViewById(R.id.seen);
        }
    }
}
