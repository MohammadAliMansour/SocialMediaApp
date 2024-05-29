package com.example.socialmediaapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmediaapp.activity.DashboardActivity;
import com.example.socialmediaapp.activity.LoginActivity;
import com.example.socialmediaapp.utils.FirebaseUtil;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ImageView ball = findViewById(R.id.ball);
        playMusic();

        Animation bounce = AnimationUtils.loadAnimation(getBaseContext(), R.anim.bounce);

        ball.startAnimation(bounce);
        new Handler().postDelayed(() -> {
            stopMusic();
            if (!FirebaseUtil.isLoggedIn()) {
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent mainIntent = new Intent(SplashScreen.this, DashboardActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        }, 4800);
    }

    private void playMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.beginning_audio);
        mediaPlayer.start();
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
    }
}
