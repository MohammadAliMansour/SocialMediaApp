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
//        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myuid = FirebaseUtil.currentUserId();
//        likeReference = FirebaseDatabase.getInstance().getReference().child("Likes");
        likeReference = FirebaseUtil.getLikesReference();
//        postReference = FirebaseDatabase.getInstance().getReference().child("Posts");
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

    /*
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);
        return new MyHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        final String uid = modelPosts.get(position).getUid();
        String name = modelPosts.get(position).getUname();
        final String title = modelPosts.get(position).getTitle();
        final String description = modelPosts.get(position).getDescription();
        final String ptime = modelPosts.get(position).getPtime();
        String dp = modelPosts.get(position).getUdp();
        String plike = modelPosts.get(position).getPlike();
        final String image = modelPosts.get(position).getUimage();
//        String email = modelPosts.get(position).getUemail();
        String comm = modelPosts.get(position).getPcomments();
        final String pid = modelPosts.get(position).getPtime();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(ptime));
        String timeDate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.name.setText(name);
        holder.title.setText(title);
        holder.description.setText(description);
        holder.time.setText(timeDate);
        holder.like.setText(plike + " Likes");
        holder.comments.setText(comm + " Comments");
        setLikes(holder, ptime);
        try {
            Glide.with(context).load(dp).into(holder.picture);
        } catch (Exception ignored) {

        }

        holder.image.setVisibility(View.VISIBLE);
        try {
            Glide.with(context).load(image).into(holder.image);
        } catch (Exception ignored) {

        }

        holder.like.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), PostLikedByActivity.class);
            intent.putExtra("pid", pid);
            holder.itemView.getContext().startActivity(intent);
        });
        holder.likebtn.setOnClickListener(v -> {
            final int plike1 = Integer.parseInt(modelPosts.get(position).getPlike());
            mprocesslike = true;
            final String postid = modelPosts.get(position).getPtime();
            likeReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (mprocesslike) {
                        if (dataSnapshot.child(postid).hasChild(myuid)) {
                            postReference.child(postid).child("plike").setValue("" + (plike1 - 1));
                            likeReference.child(postid).child(myuid).removeValue();
                        } else {
                            postReference.child(postid).child("plike").setValue("" + (plike1 + 1));
                            likeReference.child(postid).child(myuid).setValue("Liked");
                        }
                        mprocesslike = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });
        if (!uid.equals(myuid)){
            holder.more.setVisibility(View.GONE);
        }
        holder.more.setOnClickListener(v -> showMoreOptions(holder.more, uid, myuid, ptime, image));
        holder.comment.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailsActivity.class);
            intent.putExtra("pid", ptime);
            context.startActivity(intent);
        });
    }
*/

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
//        ImageView picture, image;
//        TextView name, time, title, description, like, comments;
//        ImageButton more;
//        Button likebtn, comment;
//        LinearLayout profile;

//        public MyHolder(@NonNull View itemView) {
//            super(itemView);
//            picture = itemView.findViewById(R.id.profile_picture_post);
//            image = itemView.findViewById(R.id.pimagetvco);
//            name = itemView.findViewById(R.id.username_post);
//            time = itemView.findViewById(R.id.time_post);
//            more = itemView.findViewById(R.id.more_btn_post);
//            title = itemView.findViewById(R.id.post_title);
//            description = itemView.findViewById(R.id.post_description);
//            like = itemView.findViewById(R.id.post_likes_number);
//            comments = itemView.findViewById(R.id.post_comments_number);
//            likebtn = itemView.findViewById(R.id.like);
//            comment = itemView.findViewById(R.id.comment);
//            profile = itemView.findViewById(R.id.profile_layout);
//        }

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

            try {
                Glide.with(context).load(post.getUdp()).into(binding.profilePicturePost);
            } catch (Exception ignored) {

            }

            binding.pimagetvco.setVisibility(View.VISIBLE);
            try {
                Glide.with(context).load(post.getUimage()).into(binding.pimagetvco);
            } catch (Exception ignored) {

            }
        }
    }
}

