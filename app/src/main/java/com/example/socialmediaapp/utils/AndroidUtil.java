package com.example.socialmediaapp.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.DashboardActivity;
import com.example.socialmediaapp.fragment.UsersFragment;
import com.example.socialmediaapp.model.ModelUsers;

public class AndroidUtil {

    public static  void showToast(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, ModelUsers model){
        intent.putExtra("username",model.getName());
        intent.putExtra("email",model.getEmail());
        intent.putExtra("userId",model.getUid());
        intent.putExtra("fcmToken",model.getFcmToken());
    }

    public static ModelUsers getUserModelFromIntent(Intent intent){
        ModelUsers users = new ModelUsers();
        users.setName(intent.getStringExtra("username"));
        users.setEmail(intent.getStringExtra("email"));
        users.setUid(intent.getStringExtra("userId"));
        users.setFcmToken(intent.getStringExtra("fcmToken"));
        return users;
    }

    public static void LoginOrRegisterIntent(Context old, Class<DashboardActivity> current){
        Intent intent = new Intent(old, current);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        old.startActivity(intent);
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    public static void commitFragment(UsersFragment usersFragment, Fragment fragment){
        FragmentTransaction fragmentTransaction = usersFragment.getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();
    }

    public static void commitFragment(DashboardActivity dashboardActivity, Fragment fragment) {
        FragmentTransaction fragmentTransaction = dashboardActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();
    }
}