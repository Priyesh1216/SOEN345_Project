package com.example.soen345_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soen345_project.R;
import com.example.soen345_project.api.AuthController;
import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.models.User;
import com.example.soen345_project.domain.services.AuthService;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private AuthController authController;
    private EditText etEmail, etPhone, etPassword;

    private String pendingVerificationId;
    private String pendingPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        authController = new AuthController(
                new AuthService(FirebaseAuth.getInstance(), new FirebaseRepository()));

        etEmail    = findViewById(R.id.etEmail);
        etPhone    = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin        = findViewById(R.id.btnLogin);
        Button btnGoToRegister = findViewById(R.id.btnGoToRegister);

        btnLogin.setOnClickListener(v -> handleLogin());
        btnGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void handleLogin() {
        String email    = etEmail.getText().toString().trim();
        String phone    = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (password.isEmpty()) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty() && phone.isEmpty()) {
            Toast.makeText(this, "Please enter either an email or a phone number", Toast.LENGTH_LONG).show();
            return;
        }
        if (!email.isEmpty() && !phone.isEmpty()) {
            Toast.makeText(this, "Please enter either email or phone, not both", Toast.LENGTH_LONG).show();
            return;
        }

        if (!email.isEmpty()) {
            authController.signInWithEmail(email, password, new AuthService.AuthCallback() {
                @Override
                public void onSuccess(User user) {
                    navigateBasedOnRole(user);
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {

            FirebaseAuth.getInstance().getFirebaseAuthSettings()
                    .setAppVerificationDisabledForTesting(true);

            if (!phone.matches("^\\+[1-9]\\d{7,14}$")) {
                Toast.makeText(this, "Use format: +15141234567", Toast.LENGTH_SHORT).show();
                return;
            }
            pendingPhone = phone;
            authController.signInWithPhone(phone, LoginActivity.this, new AuthService.AuthCallback() {
                        @Override
                        public void onSuccess(User user) {
                            navigateBasedOnRole(user);
                        }
                        @Override
                        public void onFailure(Exception e) {
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this,
                                    "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    },new AuthService.PhoneCodeSentCallback() {
                @Override
                public void onCodeSent(String verificationId) {
                    pendingVerificationId = verificationId;
                    runOnUiThread(() -> showOtpDialog());
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            );
        }
    }
    private void showOtpDialog() {
        EditText otpInput = new EditText(this);
        otpInput.setHint("Enter 6-digit code");
        otpInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Enter verification code")
                .setMessage("A code was sent to " + pendingPhone)
                .setView(otpInput)
                .setPositiveButton("Verify", (dialog, which) -> {
                    String otp = otpInput.getText().toString().trim();
                    if (otp.length() != 6) {
                        Toast.makeText(this, "Enter the 6-digit code", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    authController.verifyOtpAndLogin(pendingVerificationId, otp,
                            new AuthService.AuthCallback() {
                                @Override
                                public void onSuccess(User user) {
                                    startActivity(new Intent(LoginActivity.this, EventListActivity.class));
                                    finish();
                                }
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(LoginActivity.this,
                                            "Invalid code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void navigateBasedOnRole(User user) {
        if (user.getIsAdmin()) {
            Intent intent = new Intent(LoginActivity.this, AdminManagementActivity.class);
            intent.putExtra("adminId", user.getId());
            startActivity(intent);
        } else {
            startActivity(new Intent(LoginActivity.this, EventListActivity.class));
        }
        finish();
    }
}