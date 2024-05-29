package com.example.socialmediaapp.utils;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class FirebaseUtil {
    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static String currentUserEmail(FirebaseUser user){
        if (user == null)
            return "";
        return user.getEmail();
    }

    public static String currentUserEmail(){
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    }

    public static boolean isLoggedIn(){
        return currentUserId() != null;
    }

    public static DatabaseReference getPostsReference(){
        return FirebaseDatabase.getInstance().getReference().child("Posts");
    }
    public static DatabaseReference getPostReference(String postId){
        return FirebaseDatabase.getInstance().getReference("Posts").child(postId);
    }
    public static DatabaseReference getCommentsReference(String postId){
        return FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
    }

    public static DatabaseReference getLikesReference(){
        return FirebaseDatabase.getInstance().getReference().child("Likes");
    }

    public static DatabaseReference currentUserDetails(){
        return FirebaseDatabase.getInstance().getReference("Users").child(currentUserId());
    }

    public static DatabaseReference allUserReference(){
        return FirebaseDatabase.getInstance().getReference("Users");
    }

    public static DatabaseReference getChatroomReference(String chatroomId){
        return FirebaseDatabase.getInstance().getReference("chatrooms").child(chatroomId);
    }

    public static DatabaseReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).child("chats");
    }

    public static String getChatroomId(String userId1,String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else{
            return userId2+"_"+userId1;
        }
    }

    public static DatabaseReference allChatroomCollectionReference(){
        return FirebaseDatabase.getInstance().getReference("chatrooms");
    }

    public static DatabaseReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())){
            return allUserReference().child(userIds.get(1));
        }else{
            return allUserReference().child(userIds.get(0));
        }
    }


    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static CompletableFuture<String> getCurrentProfilePicStorageRef(String id){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
        CompletableFuture<String> future = new CompletableFuture<>();

        Query query = databaseReference.orderByChild("uid").equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profilePic = "";
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    profilePic = ds.child("image").getValue(String.class);
                    if (profilePic != null) {
                        future.complete(profilePic);
                        return;
                    }
                }
                future.complete(profilePic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        return future;
    }

    public static CompletableFuture<String> getOtherProfilePicStorageRef(String otherUserId){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
        CompletableFuture<String> future = new CompletableFuture<>();

        Query query = databaseReference.orderByChild("uid").equalTo(otherUserId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profilePic = "";
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    profilePic = ds.child("image").getValue(String.class);
                    if (profilePic != null) {
                        future.complete(profilePic);
                        return;
                    }
                }
                future.complete(profilePic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        return future;
    }


}