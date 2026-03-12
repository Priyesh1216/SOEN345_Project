package com.example.soen345_project.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soen345_project.R;
import com.example.soen345_project.api.AuthController;
import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.services.AuthService;

public class RegisterActivity extends AppCompatActivity {

    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authController = new AuthController(new AuthService(new FirebaseRepository()));

        setupListeners();
    }

    private void setupListeners() {
        // register button click: call authController.registerWithEmail() or registerWithPhone()
        // success: navigate to EventListActivity
        // failure: show error message
    }
}