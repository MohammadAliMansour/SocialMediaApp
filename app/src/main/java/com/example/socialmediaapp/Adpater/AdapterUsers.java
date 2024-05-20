package com.example.socialmediaapp.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users, parent, false);
//        return new MyHolder(view);
        RowUsersBinding binding = RowUsersBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyHolder(binding);
    }

//    @Override
//    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
//        String userImage = list.get(position).getImage();
//        String username = list.get(position).getName();
//        String userEmail = list.get(position).getEmail();
//        holder.name.setText(username);
//        holder.email.setText(userEmail);
//        try {
//            if (userImage.isEmpty()){
//                holder.profiletv.setVisibility(View.GONE);
//                holder.include.setVisibility(View.VISIBLE);
//            }
//            else {
//                holder.include.setVisibility(View.GONE);
//                holder.profiletv.setVisibility(View.VISIBLE);
//                Glide.with(context).load(userImage).into(holder.profiletv);
//            }
//        } catch (Exception ignored) {
//        }
//
//        holder.itemView.setOnClickListener(v -> {
//            //navigate to chat activity
//            Intent intent = new Intent(context, ChatActivity.class);
//            AndroidUtil.passUserModelAsIntent(intent,new ModelUsers(username, userEmail, hisuid));
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//        });
//    }
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
//        CircleImageView profiletv;
//        TextView name, email;
//        View include;

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
                
            });
        }

//        public MyHolder(@NonNull View itemView) {
//            super(itemView);
//            profiletv = itemView.findViewById(R.id.imagep);
//            name = itemView.findViewById(R.id.namep);
//            email = itemView.findViewById(R.id.emailp);
//            include = itemView.findViewById(R.id.profile_pic_layout);
//        }
    }
}
