package com.example.socialmediaapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.databinding.ActivityLoginBinding;
import com.example.socialmediaapp.utils.AndroidUtil;
import com.example.socialmediaapp.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
        initLogin();
        initNewAccount();
        initRecoverPass();
    }

    private void initLayout() {
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
    }

    private void initRecoverPass() {
        binding.forgetPassword.setOnClickListener(v -> showRecoverPasswordDialog());
    }

    private void initNewAccount() {
        binding.needsNewAccount.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));
    }

    private void initLogin() {
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.loginEmail.getText().toString().trim();
            String pass = binding.loginPassword.getText().toString().trim();

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.loginEmail.setError("Invalid Email");
                binding.loginEmail.setFocusable(true);
            } else {
                loginUser(email, pass);
            }
        });
    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout = new LinearLayout(this);
        final EditText emailEditText = new EditText(this);
        emailEditText.setHint(R.string.email_);
        emailEditText.setMinEms(16);
        emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(emailEditText);
        linearLayout.setPadding(10, 10, 10, 10);
        builder.setView(linearLayout);
        builder.setPositiveButton("Recover", (dialog, which) -> {
            String email = emailEditText.getText().toString().trim();
            beginRecovery(email);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void beginRecovery(String email) {
        binding.loginProgressBar.progressbar.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            binding.loginProgressBar.progressbar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                AndroidUtil.showToast(this, "Done sent");
            } else {
                AndroidUtil.showToast(this, "Error Occurred");
            }
        }).addOnFailureListener(e -> {
            binding.loginProgressBar.progressbar.setVisibility(View.GONE);
            AndroidUtil.showToast(this, "Error Failed");
        });
    }

    private void loginUser(String email, String password) {
        binding.loginProgressBar.progressbar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                binding.loginProgressBar.progressbar.setVisibility(View.GONE);
                currentUser = mAuth.getCurrentUser();
                AndroidUtil.showToast(this, "Logged in" + FirebaseUtil.currentUserEmail(currentUser));
                AndroidUtil.LoginOrRegisterIntent(this, DashboardActivity.class);
                finish();
            } else {
                binding.loginProgressBar.progressbar.setVisibility(View.GONE);
                AndroidUtil.showToast(this, "Login Failed");
            }
        }).addOnFailureListener(e -> {
            binding.loginProgressBar.progressbar.setVisibility(View.GONE);
            AndroidUtil.showToast(this, "Error Occurred");
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return super.onSupportNavigateUp();
    }
}
