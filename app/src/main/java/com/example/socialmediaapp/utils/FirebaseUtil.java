package com.example.socialmediaapp.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Objects;

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

//    public static String timestampToString(Timestamp timestamp){
//        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
//    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference  getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("Users_Profile_Cover_image")
                .child(FirebaseUtil.currentUserId());
    }

    public static StorageReference  getOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("Users_Profile_Cover_image")
                .child(otherUserId);
    }


}