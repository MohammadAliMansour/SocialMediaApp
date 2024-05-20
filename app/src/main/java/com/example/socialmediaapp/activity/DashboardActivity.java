package com.example.socialmediaapp.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.databinding.ActivityDashboardBinding;
import com.example.socialmediaapp.fragment.AddBlogsFragment;
import com.example.socialmediaapp.fragment.ChatFragment;
import com.example.socialmediaapp.fragment.HomeFragment;
import com.example.socialmediaapp.fragment.ProfileFragment;
import com.example.socialmediaapp.fragment.UsersFragment;
import com.example.socialmediaapp.utils.AndroidUtil;
import com.google.android.material.navigation.NavigationBarView;

public class DashboardActivity extends AppCompatActivity {

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
    }

    private void initLayout() {
        actionBar = getSupportActionBar();
        ActivityDashboardBinding binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.navigation.setOnItemSelectedListener(selectedListener);
        actionBar.setTitle("Home");
        AndroidUtil.commitFragment(this, new HomeFragment());
    }


    private final NavigationBarView.OnItemSelectedListener selectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            if (menuItem.getItemId() == R.id.nav_home){
                actionBar.setTitle("Home");
                AndroidUtil.commitFragment(DashboardActivity.this, new HomeFragment());
                return true;
            } else if (menuItem.getItemId() == R.id.nav_profile) {
                actionBar.setTitle("Profile");
                AndroidUtil.commitFragment(DashboardActivity.this, new ProfileFragment());
                return true;
            } else if (menuItem.getItemId() == R.id.nav_users) {
                actionBar.setTitle("Users");
                AndroidUtil.commitFragment(DashboardActivity.this, new UsersFragment());
                return true;
            } else if (menuItem.getItemId() == R.id.nav_chat) {
                actionBar.setTitle("Chats");
                AndroidUtil.commitFragment(DashboardActivity.this, new ChatFragment());
                return true;
            } else if (menuItem.getItemId() == R.id.nav_add_blogs) {
                actionBar.setTitle("Add Blogs");
                AndroidUtil.commitFragment(DashboardActivity.this, new AddBlogsFragment());
                return true;
            }
            return false;
        }
    };
}
