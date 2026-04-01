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

public class RegisterActivity extends AppCompatActivity {

    private AuthController authController;
    private EditText etName, etEmail, etPhone, etPassword;
    // Add field at the top of RegisterActivity
    private String pendingVerificationId;
    private String pendingPhone;
    private String pendingName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        FirebaseAuth.getInstance().getFirebaseAuthSettings()
                .setAppVerificationDisabledForTesting(true);
        authController = new AuthController(new AuthService(FirebaseAuth.getInstance(), new FirebaseRepository()));
        etName     = findViewById(R.id.etRegisterName);
        etEmail    = findViewById(R.id.etRegisterEmail);
        etPhone    = findViewById(R.id.etRegisterPhone);
        etPassword = findViewById(R.id.etRegisterPassword);
        Button btnDoRegister = findViewById(R.id.btnDoRegister);
        Button btnGoToLogin  = findViewById(R.id.btnGoToLogin);

        btnDoRegister.setOnClickListener(v -> handleRegister());
        btnGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void handleRegister() {
        String name     = etName.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String phone    = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }
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
            authController.registerWithEmail(email, password, name, new AuthService.AuthCallback() {
                @Override
                public void onSuccess(User user) {
                    startActivity(new Intent(RegisterActivity.this, EventListActivity.class));
                    finish();
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (!phone.matches("^\\+[1-9]\\d{7,14}$")) {
                Toast.makeText(this, "Use format: +15141234567", Toast.LENGTH_SHORT).show();
                return;
            }
            pendingPhone = phone;
            pendingName  = name;

            authController.registerWithPhone(phone, name, this,
                    new AuthService.AuthCallback() {
                        @Override
                        public void onSuccess(User user) {
                            startActivity(new Intent(RegisterActivity.this, EventListActivity.class));
                            finish();
                        }
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(RegisterActivity.this,
                                    "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new AuthService.PhoneCodeSentCallback() {
                        @Override
                        public void onCodeSent(String verificationId) {
                            pendingVerificationId = verificationId;
                            showOtpDialog(); // prompt user to enter the SMS code
                        }
                        @Override
                        public void onFailure(Exception e) {
                            System.out.println(e.getMessage());
                            Toast.makeText(RegisterActivity.this,
                                    "Phone auth failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    authController.verifyOtpAndRegister(pendingVerificationId, otp,
                            pendingPhone, pendingName, new AuthService.AuthCallback() {
                                @Override
                                public void onSuccess(User user) {
                                    startActivity(new Intent(RegisterActivity.this, EventListActivity.class));
                                    finish();
                                }
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(RegisterActivity.this,
                                            "Invalid code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}