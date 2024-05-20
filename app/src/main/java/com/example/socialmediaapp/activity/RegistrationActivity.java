package com.example.socialmediaapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmediaapp.databinding.ActivityRegistrationBinding;
import com.example.socialmediaapp.utils.AndroidUtil;
import com.example.socialmediaapp.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ActivityRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
        initRegisterButton();
        initExistAccount();
    }

    private void initLayout() {
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
    }

    private void initExistAccount() {
        binding.haveAccount.setOnClickListener(v -> startActivity(new Intent(RegistrationActivity.this, LoginActivity.class)));
    }

    private void initRegisterButton() {
        binding.registerButton.setOnClickListener(view -> {
            String email = binding.registerEmail.getText().toString().trim();
            String username = binding.registerName.getText().toString().trim();
            String password = binding.registerPassword.getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.registerEmail.setError("Invalid Email");
                binding.registerEmail.setFocusable(true);
            } else if (password.length() < 6) {
                binding.registerPassword.setError("Length Must be greater than 6 characters");
            } else {
                registerUser(email,password, username);
            }
        });
    }

    private void registerUser(String email, final String password, final String username) {
        binding.registrationProgressBar.progressbar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            binding.registrationProgressBar.progressbar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                String email1 = FirebaseUtil.currentUserEmail(user);
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("email", email1);
                hashMap.put("uid", FirebaseUtil.currentUserId());
                hashMap.put("name", username);
                hashMap.put("image", "");
                FirebaseUtil.currentUserDetails().setValue(hashMap);
                AndroidUtil.showToast(this, "Registered User " + email1);
                AndroidUtil.LoginOrRegisterIntent(this, DashboardActivity.class);
                finish();
            } else {
                AndroidUtil.showToast(this, "Error");
            }
        }).addOnFailureListener(e -> {
           binding.registrationProgressBar.progressbar.setVisibility(View.GONE);
           AndroidUtil.showToast(this, "Error Occurred");
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return super.onSupportNavigateUp();
    }
}
