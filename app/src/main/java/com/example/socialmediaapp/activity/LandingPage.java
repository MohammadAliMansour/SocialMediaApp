package com.example.socialmediaapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.SplashScreen;
import com.example.socialmediaapp.data.DataSource;
import com.example.socialmediaapp.databinding.ActivityLandingPageBinding;
import com.example.socialmediaapp.model.ModelUsers;
import com.example.socialmediaapp.utils.FirebaseUtil;
import com.example.socialmediaapp.viewmodel.LandingScreenViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class LandingPage extends AppCompatActivity {
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;
    ActivityLandingPageBinding binding;
    LandingScreenViewModel viewModel;
    private ModelUsers user;
    private ActivityResultLauncher<Intent> selectImageLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.Theme_SocialMediaApp_NoActionBar);

        binding = ActivityLandingPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(LandingScreenViewModel.class);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        binding.btnKick.setOnClickListener(v -> {
            Intent intent = new Intent(this, SplashScreen.class);
            startActivity(intent);
            finish();
        });

        if (!FirebaseUtil.isLoggedIn()){
            binding.btnChangeWallpaper.setVisibility(View.GONE);
        }else {
            user = new ModelUsers(
                    FirebaseAuth.getInstance().getCurrentUser().getEmail()
            );
            initUser();
        }


        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData().getData() != null) {
                            Uri selectedImageUri = result.getData().getData();
                            try {
                                Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                                if (photo != null) {
                                    float density = getResources().getDisplayMetrics().density;
                                    int dp = 140;
                                    int pixels = (int) ((dp * density) + 0.5);
                                    Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, pixels, pixels, true);
                                    Drawable drawable = new BitmapDrawable(getResources(), scaledPhoto);
                                    binding.llMain.setBackground(drawable);
                                    user.setWallpaper(scaledPhoto);

                                    saveWallpaper();
                                } else {
                                    Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        binding.btnChangeWallpaper.setOnClickListener(v -> changeWallpaper());
        DataSource dataSource = new DataSource(this);
        dataSource.open();
    }

    private void saveWallpaper() {
        DataSource dataSource = new DataSource(this);
        dataSource.open();
        ModelUsers temp = null;
        try {
            temp = dataSource.getUser(user.getEmail());
        } catch (Exception ignored) {

        }
        dataSource.close();
        try {
            dataSource.open();
            if (temp == null) {
                dataSource.insertUser(user);
            } else{
                dataSource.updateUser(user);
            }
            dataSource.close();
        } catch (Exception ignored) {

        }
    }

    private void initUser() {
        DataSource dataSource = new DataSource(this);
        ModelUsers temp = null;
        dataSource.open();
        try {
            temp = dataSource.getUser(user.getEmail());
        } catch (Exception ignored){

        }
        dataSource.close();
        if (temp != null){
            user.setWallpaper(temp.getWallpaper());
            if (user.getWallpaper() != null) {
                Drawable drawable = new BitmapDrawable(getResources(), user.getWallpaper());
                binding.llMain.setBackground(drawable);
            }
        }
    }

    private void changeWallpaper() {
        requestStoragePermission();
    }

    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_STORAGE_PERMISSION);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        selectImageLauncher.launch(intent);
    }
}