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
import com.example.soen345_project.domain.InputValidator;
import com.example.soen345_project.domain.models.User;
import com.example.soen345_project.domain.services.AuthService;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private AuthController authController;
    private EditText etName, etEmail, etPhone, etPassword;
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
        // Clear previous errors
        etName.setError(null);
        etEmail.setError(null);
        etPhone.setError(null);
        etPassword.setError(null);

        String name     = etName.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String phone    = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        String nameError = InputValidator.validateRequired(name, "Name");
        if (nameError != null) {
            etName.setError(nameError);
            etName.requestFocus();
            return;
        }

        String passwordError = InputValidator.validatePassword(password);
        if (passwordError != null) {
            etPassword.setError(passwordError);
            etPassword.requestFocus();
            return;
        }

        if (email.isEmpty() && phone.isEmpty()) {
            etEmail.setError("Enter either an email or a phone number");
            etPhone.setError("Enter either an email or a phone number");
            etEmail.requestFocus();
            return;
        }
        if (!email.isEmpty() && !phone.isEmpty()) {
            etEmail.setError("Use only email or phone, not both");
            etPhone.setError("Use only email or phone, not both");
            etEmail.requestFocus();
            return;
        }

        if (!email.isEmpty()) {
            String emailError = InputValidator.validateEmail(email);
            if (emailError != null) {
                etEmail.setError(emailError);
                etEmail.requestFocus();
                return;
            }
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
            String phoneError = InputValidator.validatePhone(phone);
            if (phoneError != null) {
                etPhone.setError(phoneError);
                etPhone.requestFocus();
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
                            showOtpDialog();
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