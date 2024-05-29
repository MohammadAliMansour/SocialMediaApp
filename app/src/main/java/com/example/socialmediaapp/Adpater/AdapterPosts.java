package com.example.socialmediaapp.adpater;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.PostDetailsActivity;
import com.example.socialmediaapp.activity.PostLikedByActivity;
import com.example.socialmediaapp.data.SharedPreferencesHelper;
import com.example.socialmediaapp.databinding.RowPostsBinding;
import com.example.socialmediaapp.model.ModelPost;
import com.example.socialmediaapp.utils.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    static Context context;
    static String myuid;
    private static DatabaseReference likeReference;
    private static DatabaseReference postReference;
    List<ModelPost> modelPosts;


    public AdapterPosts(Context context, List<ModelPost> modelPosts) {
        AdapterPosts.context = context;
        this.modelPosts = modelPosts;
        myuid = FirebaseUtil.currentUserId();
        likeReference = FirebaseUtil.getLikesReference();
        postReference = FirebaseUtil.getPostsReference();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowPostsBinding binding = RowPostsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        ModelPost post = modelPosts.get(position);
        holder.bind(post);
    }

    private static void showMoreOptions(ImageButton more, String uid, String myuid, final String pid, final String image) {
        PopupMenu popupMenu = new PopupMenu(context, more, Gravity.END);
        Log.w("myUser id::", uid + "   " + myuid);
        if (uid.equals(myuid)) {
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "DELETE");
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 0) {
                deleteWithImage(pid, image);
            }

            return false;
        });
        popupMenu.show();
    }

    private static void deleteWithImage(final String pid, String image) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting");
        StorageReference picref = FirebaseStorage.getInstance().getReferenceFromUrl(image);
        picref.delete().addOnSuccessListener(aVoid -> {
            Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("ptime").equalTo(pid);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        dataSnapshot1.getRef().removeValue();
                    }
                    pd.dismiss();
                    Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_LONG).show();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }).addOnFailureListener(e -> {

        });
    }

    private static void setLikes(final MyHolder holder, final String pid) {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
        likeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(pid).hasChild(myuid)) {
                    if (sharedPreferencesHelper.getSelectedOption().equals("Bayern Munchen"))
                        holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bayern_munchen, 0, 0, 0);
                    else if (sharedPreferencesHelper.getSelectedOption().equals("Real Madrid")) {
                        holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.real_madrid, 0, 0, 0);
                    }
                    holder.binding.like.setText("Scored");
                } else {
                    holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.soccer_ball_retina, 0, 0, 0);
                    holder.binding.like.setText("Score");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return modelPosts.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        boolean mprocesslike = false;
        private RowPostsBinding binding;

        public MyHolder(@NonNull RowPostsBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ModelPost post){
            binding.setPost(post);


            binding.postLikesNumber.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), PostLikedByActivity.class);
                intent.putExtra("pid", post.getPtime());
                itemView.getContext().startActivity(intent);
            });

            binding.like.setOnClickListener(v -> {
                final int plike1 = Integer.parseInt(post.getPlike());
                mprocesslike = true;
                final String postid = post.getPtime();
                likeReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mprocesslike) {
                            if (dataSnapshot.child(postid).hasChild(myuid)) {
                                post.setPlike("" + (plike1 - 1));
//                                postReference.child(postid).child("plike").setValue("" + (plike1 - 1));
                                likeReference.child(postid).child(myuid).removeValue();
                            } else {
                                post.setPlike("" + (plike1 + 1));
//                                postReference.child(postid).child("plike").setValue("" + (plike1 + 1));
                                likeReference.child(postid).child(myuid).setValue("Liked");
                            }

                            mprocesslike = false;
                        }
                        binding.postLikesNumber.setText(post.getPlike());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            });

            if (!post.getUid().equals(myuid)){
                binding.moreBtnPost.setVisibility(View.GONE);
            }
            binding.moreBtnPost.setOnClickListener(v -> showMoreOptions(binding.moreBtnPost, post.getUid(), myuid, post.getPtime(), post.getUimage()));
            binding.comment.setOnClickListener(v -> {
                Intent intent = new Intent(context, PostDetailsActivity.class);
                intent.putExtra("pid", post.getPtime());
                context.startActivity(intent);
            });

            setLikes(this, binding.getPost().getPtime());


            FirebaseUtil.getCurrentProfilePicStorageRef(post.getUid()).thenAccept(profilePic -> {
                try {
                    Glide.with(context).load(profilePic).into(binding.profilePicturePost);
                } catch (Exception ignored) {
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                }
            });


            binding.pimagetvco.setVisibility(View.VISIBLE);
            try {
                Glide.with(context).load(post.getUimage()).into(binding.pimagetvco);
            } catch (Exception ignored) {

            }
        }
    }
}

