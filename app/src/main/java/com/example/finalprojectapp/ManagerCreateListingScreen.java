package com.example.finalprojectapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ManagerCreateListingScreen extends AppCompatActivity
{

    private EditText etPrice, etDate, etPallets;
    private Spinner spDistributor;
    private TextView tvCreateNewDistributor;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_create_listing_screen);

        // Initialize views
        etPrice = findViewById(R.id.et_price);
        etDate = findViewById(R.id.et_date);
        etPallets = findViewById(R.id.et_pallets);
        spDistributor = findViewById(R.id.sp_distributor);
        tvCreateNewDistributor = findViewById(R.id.tv_create_new_distributor);

        dbHelper = new DatabaseHelper(this);

        // Load distributor dropdown
        loadDistributors();

        // Set navigation to Add Distributor screen
        tvCreateNewDistributor.setOnClickListener(v ->
        {
            Intent intent = new Intent(ManagerCreateListingScreen.this, ManagerAddDistributorScreen.class);
            startActivity(intent);
        });

        // Add functionality for creating a listing
        findViewById(R.id.btn_create).setOnClickListener(v -> createListing());

        // Back button to go to the previous screen
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void loadDistributors()
    {
        List<String> distributors = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("distributors", new String[]{"name"}, null, null, null, null, null);

        while (cursor != null && cursor.moveToNext())
        {
            distributors.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        }

        if (cursor != null)
        {
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, distributors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistributor.setAdapter(adapter);
    }

    private void createListing()
    {
        String price = etPrice.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String pallets = etPallets.getText().toString().trim();
        String distributor = spDistributor.getSelectedItem() != null ? spDistributor.getSelectedItem().toString() : "";

        // Validate inputs
        if (price.isEmpty() || date.isEmpty() || pallets.isEmpty() || distributor.isEmpty())
        {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate date format
        if (!date.matches("\\d{2}-\\d{2}-\\d{4}"))
        {
            Toast.makeText(this, "Date must be in MM-DD-YYYY format.", Toast.LENGTH_SHORT).show();
            return;
        }

        try
        {
            double priceValue = Double.parseDouble(price);
            int palletsValue = Integer.parseInt(pallets);

            // Insert new listing into the database
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("distributor", distributor);
            values.put("datePurchased", date);
            values.put("numPallets", palletsValue);
            values.put("price", priceValue);
            values.put("damagedCount", 0);
            values.put("earnings", 0.0);
            values.put("salespersonPay", 0.0);
            values.put("profit", 0.0);

            long result = db.insert("listings", null, values);
            if (result != -1)
            {
                Toast.makeText(this, "Listing created successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {
                Toast.makeText(this, "Failed to create listing.", Toast.LENGTH_SHORT).show();
            }

            db.close();
        }
        catch (NumberFormatException e)
        {
            Toast.makeText(this, "Invalid input. Please check the numbers.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        refreshDistributors();
    }

    private void refreshDistributors()
    {
        Cursor cursor = dbHelper.getAllDistributors();
        List<String> distributors = new ArrayList<>();

        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                String distributor = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                distributors.add(distributor);
            }
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, distributors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistributor.setAdapter(adapter);
    }

}
