package com.example.soen345_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345_project.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        Button btnDoRegister = findViewById(R.id.btnDoRegister);

        btnDoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dummy register: go directly to HomeActivity
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                // Clear the back stack so we can't go back to Register or Login
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        Button btnGoToLogin = findViewById(R.id.btnGoToLogin);
        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to login screen
                finish();
            }
        });
    }
}