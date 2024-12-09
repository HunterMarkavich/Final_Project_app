package com.example.finalprojectapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginScreen extends AppCompatActivity
{

    private EditText etUsername, etPassword;
    private Button btnLogin;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Initialize views
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Login button click listener
        btnLogin.setOnClickListener(v ->
        {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validate user credentials
            String role = dbHelper.validateUser(username, password);
            if (role != null)
            {
                navigateToRole(role);
            } else
            {
                Toast.makeText(LoginScreen.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Navigate to respective role screen
    private void navigateToRole(String role)
    {
        Intent intent;
        switch (role)
        {
            case "Manager":
                intent = new Intent(LoginScreen.this, ManagerHomeScreen.class);
                break;
            case "Salesperson":
                intent = new Intent(LoginScreen.this, SalespersonHomeScreen.class);
                break;
            case "Customer Service":
                intent = new Intent(LoginScreen.this, CustomerServiceHomeScreen.class);
                break;
            default:
                return;
        }
        startActivity(intent);
        finish();
    }
}

