package com.example.socialmediaapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.adpater.AdapterComment;
import com.example.socialmediaapp.databinding.ActivityPostDetailsBinding;
import com.example.socialmediaapp.model.ModelComment;
import com.example.socialmediaapp.utils.AndroidUtil;
import com.example.socialmediaapp.utils.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PostDetailsActivity extends AppCompatActivity {
    String hisuid, ptime, myuid, myname, myemail, mydp, uimage, postId, plike, hisdp, hisname;
    List<ModelComment> commentList;
    AdapterComment adapterComment;
    boolean mlike = false;
    ActionBar actionBar;
    private ActivityPostDetailsBinding binding;
    boolean count = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
        myemail = FirebaseUtil.currentUserEmail();
        myuid = FirebaseUtil.currentUserId();
        loadPostInfo();
        loadUserInfo();
        setLikes();
        loadComments();

        initSendComment();
        initLikePost();
        initLikesNumber();
        initComment();

    }

    private void initComment() {
        binding.post.comment.setOnClickListener(v -> {
            DatabaseReference postReference = FirebaseUtil.getPostReference(postId);
            postReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String title = String.valueOf(snapshot.child("title").getValue());
                    String description = String.valueOf(snapshot.child("description").getValue());
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String postContent = title + "\n\n" + description;
                    shareIntent.putExtra(Intent.EXTRA_TEXT, postContent);
                    startActivity(Intent.createChooser(shareIntent, "Share Post via"));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    private void initLikesNumber() {
        binding.post.postLikesNumber.setOnClickListener(v -> {
            Intent intent = new Intent(PostDetailsActivity.this, PostLikedByActivity.class);
            intent.putExtra("pid", postId);
            startActivity(intent);
        });
    }

    private void initLikePost() {
        binding.post.like.setOnClickListener(v -> likePost());
    }

    private void initSendComment() {
        binding.sendcomment.setOnClickListener(v -> postComment());
    }

    private void initLayout(){
        actionBar = getSupportActionBar();
        postId = getIntent().getStringExtra("pid");
        binding = ActivityPostDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.post.comment.setText(R.string.share);
        binding.post.comment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_share,0,0,0);
        actionBar.setTitle("Post Details");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setSubtitle("SignedInAs:" + myemail);
    }

    private void loadComments() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerComment.setLayoutManager(layoutManager);
        commentList = new ArrayList<>();
        DatabaseReference reference = FirebaseUtil.getCommentsReference(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelComment modelComment = dataSnapshot1.getValue(ModelComment.class);
                    commentList.add(modelComment);
                    adapterComment = new AdapterComment(getApplicationContext(), commentList, myuid, postId);
                    binding.recyclerComment.setAdapter(adapterComment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setLikes() {
        final DatabaseReference likesReference = FirebaseUtil.getLikesReference();
        likesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(postId).hasChild(myuid)) {
                    binding.post.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0, 0, 0);
                    binding.post.like.setText(R.string.liked);
                } else {
                    binding.post.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
                    binding.post.like.setText(R.string.like);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void likePost() {

        mlike = true;
        final DatabaseReference likesReference = FirebaseUtil.getLikesReference();
        final DatabaseReference postReference = FirebaseUtil.getPostsReference();
        likesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (mlike) {
                    if (dataSnapshot.child(postId).hasChild(myuid)) {
                        postReference.child(postId).child("plike").setValue("" + (Integer.parseInt(plike) - 1));
                        likesReference.child(postId).child(myuid).removeValue();
                    } else {
                        postReference.child(postId).child("plike").setValue("" + (Integer.parseInt(plike) + 1));
                        likesReference.child(postId).child(myuid).setValue("Liked");
                    }
                    mlike = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void postComment() {

        final String comments = binding.typecommet.getText().toString().trim();
        if (TextUtils.isEmpty(comments)) {
            AndroidUtil.showToast(this, "Empty Comment");
            return;
        }
        binding.commentProgressBar.progressbar.setVisibility(View.VISIBLE);
        String timestamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference commentsReference = FirebaseUtil.getCommentsReference(postId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("cId", timestamp);
        hashMap.put("comment", comments);
        hashMap.put("ptime", timestamp);
        hashMap.put("uid", myuid);
        hashMap.put("uemail", myemail);
        hashMap.put("udp", mydp);
        hashMap.put("uname", myname);
        commentsReference.child(timestamp).setValue(hashMap).addOnSuccessListener(aVoid -> {
            binding.commentProgressBar.progressbar.setVisibility(View.GONE);
            AndroidUtil.showToast(this, "Added");
            binding.typecommet.setText("");
            updateCommentCount();
        }).addOnFailureListener(e -> {
            binding.commentProgressBar.progressbar.setVisibility(View.GONE);
            AndroidUtil.showToast(this, "Failed");
        });
    }



    private void updateCommentCount() {
        count = true;
        final DatabaseReference postReference = FirebaseUtil.getPostReference(postId);
        postReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (count) {
                    String comments = String.valueOf(dataSnapshot.child("pcomments").getValue());
                    int newComment = Integer.parseInt(comments) + 1;
                    postReference.child("pcomments").setValue(String.valueOf(newComment));
                    count = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUserInfo() {

        Query allUserReference = FirebaseUtil.allUserReference();
        allUserReference.orderByChild("uid").equalTo(myuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    myname = Objects.requireNonNull(dataSnapshot1.child("name").getValue()).toString();
                    mydp = Objects.requireNonNull(dataSnapshot1.child("image").getValue()).toString();
                    try {
                        Glide.with(PostDetailsActivity.this).load(mydp).into(binding.commentimge);
                    } catch (Exception ignored) {}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadPostInfo() {

        DatabaseReference databaseReference = FirebaseUtil.getPostsReference();
        Query query = databaseReference.orderByChild("ptime").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String descriptions = Objects.requireNonNull(dataSnapshot1.child("description").getValue()).toString();
                    uimage = Objects.requireNonNull(dataSnapshot1.child("uimage").getValue()).toString();
                    hisdp = Objects.requireNonNull(dataSnapshot1.child("udp").getValue()).toString();
                    hisuid = Objects.requireNonNull(dataSnapshot1.child("uid").getValue()).toString();
                    hisname = Objects.requireNonNull(dataSnapshot1.child("uname").getValue()).toString();
                    ptime = Objects.requireNonNull(dataSnapshot1.child("ptime").getValue()).toString();
                    plike = Objects.requireNonNull(dataSnapshot1.child("plike").getValue()).toString();
                    String commentCount = Objects.requireNonNull(dataSnapshot1.child("pcomments").getValue()).toString();
                    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                    calendar.setTimeInMillis(Long.parseLong(ptime));
                    String timeDate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                    binding.post.usernamePost.setText(hisname);
                    binding.post.postDescription.setText(descriptions);
                    binding.post.postLikesNumber.setText(plike + " Likes");
                    binding.post.timePost.setText(timeDate);
                    binding.post.postCommentsNumber.setText(commentCount + " Comments");
                    if (uimage.equals("noImage")) {
                        binding.post.pimagetvco.setVisibility(View.INVISIBLE);
                    } else {
                        binding.post.pimagetvco.setVisibility(View.VISIBLE);
                        try {
                            Glide.with(PostDetailsActivity.this).load(uimage).into(binding.post.pimagetvco);
                        } catch (Exception ignored) {}
                    }
                    try {
                        Glide.with(PostDetailsActivity.this).load(hisdp).into(binding.post.profilePicturePost);
                    } catch (Exception ignored) {}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return super.onSupportNavigateUp();
    }

}
