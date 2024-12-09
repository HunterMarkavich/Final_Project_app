package com.example.finalprojectapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ManagerAddDistributorScreen extends AppCompatActivity
{

    private EditText etDistributorName;
    private Button btnAdd, btnBack;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_add_distributor_screen);

        // Initialize views
        etDistributorName = findViewById(R.id.et_distributor_name);
        btnAdd = findViewById(R.id.btn_add);
        btnBack = findViewById(R.id.btn_back);

        dbHelper = new DatabaseHelper(this);

        // Add distributor functionality
        btnAdd.setOnClickListener(v -> addDistributor());

        // Back button functionality
        btnBack.setOnClickListener(v -> finish());
    }

    private void addDistributor()
    {
        String distributorName = etDistributorName.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(distributorName))
        {
            Toast.makeText(this, "Please enter a distributor name.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Capitalize each word
        distributorName = capitalizeWords(distributorName);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", distributorName);

        long result = db.insert("distributors", null, values);
        if (result != -1)
        {
            Toast.makeText(this, "Distributor added successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            Toast.makeText(this, "Failed to add distributor.", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    private String capitalizeWords(String str)
    {
        String[] words = str.split("\\s+");
        StringBuilder capitalized = new StringBuilder();

        for (String word : words)
        {
            if (word.length() > 0)
            {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return capitalized.toString().trim();
    }
}
