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
            authController.signInWithPhone(phone, LoginActivity.this, new AuthService.AuthCallback() {
                @Override
                public void onSuccess(User user) {
                    navigateBasedOnRole(user);
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void navigateBasedOnRole(User user) {
        if (user.getIsAdmin()) {
            startActivity(new Intent(LoginActivity.this, AdminManagementActivity.class));
        } else {
            startActivity(new Intent(LoginActivity.this, EventListActivity.class));
        }
        finish();
    }
}