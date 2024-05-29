package com.example.socialmediaapp.adpater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediaapp.activity.OtherUserProfileActivity;
import com.example.socialmediaapp.databinding.RowUsersBinding;
import com.example.socialmediaapp.model.ModelUsers;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {
    Context context;
    FirebaseAuth firebaseAuth;
    String uid;
    List<ModelUsers> list;


    public AdapterUsers(Context context, List<ModelUsers> list) {
        this.context = context;
        this.list = list;
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowUsersBinding binding = RowUsersBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position){
        ModelUsers users = list.get(position);
        holder.bind(users);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private final RowUsersBinding binding;

        public MyHolder(@NonNull RowUsersBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ModelUsers user){
            binding.namep.setText(user.getName());
            binding.emailp.setText(user.getEmail());

            if (user.getImage() != null && !user.getImage().isEmpty()){
                binding.imagep.setVisibility(View.VISIBLE);
                binding.imagep.post(() -> Glide.with(context).load(user.getImage()).into(binding.imagep));
                binding.profilePicLayout.getRoot().setVisibility(View.GONE);
            } else{
                binding.imagep.setVisibility(View.GONE);
                binding.profilePicLayout.getRoot().setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, OtherUserProfileActivity.class);
                intent.putExtra("id", user.getUid());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            });

        }

    }
}
