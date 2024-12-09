package com.example.finalprojectapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CustomerServiceLogReturnScreen extends AppCompatActivity
{

    // Declare UI elements and helper class
    private EditText etLoadId, etReturnDate, etRefundedAmount, etReturnReason;
    private Button btnLogReturn, btnBack;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service_log_return_screen);

        // Initialize views
        // Input field for Load ID
        etLoadId = findViewById(R.id.et_load_id);
        // Input field for Return Date
        etReturnDate = findViewById(R.id.et_return_date);
        // Input field for Refunded Amount
        etRefundedAmount = findViewById(R.id.et_refunded_amount);
        // Input field for Return Reason
        etReturnReason = findViewById(R.id.et_return_reason);
        // Button to log the return
        btnLogReturn = findViewById(R.id.btn_log_return);
        // Button to navigate back to the previous screen
        btnBack = findViewById(R.id.btn_back);

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Pre-fill Load ID if navigating from the View Returns Screen
        // Get the listing ID passed via Intent
        int listingId = getIntent().getIntExtra("listing_id", -1);
        if (listingId != -1)
        {
            etLoadId.setText(String.valueOf(listingId));
        }

        // Set functionality for the Log Return button
        btnLogReturn.setOnClickListener(v ->
        {
            // Get the input values from the fields
            String loadIdStr = etLoadId.getText().toString().trim();
            String returnDate = etReturnDate.getText().toString().trim();
            String refundAmountStr = etRefundedAmount.getText().toString().trim();
            String reason = etReturnReason.getText().toString().trim();

            // Check if any field is empty
            if (loadIdStr.isEmpty() || returnDate.isEmpty() || refundAmountStr.isEmpty() || reason.isEmpty())
            {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert input values to appropriate data types
            int loadId = Integer.parseInt(loadIdStr); // Convert Load ID to an integer
            double refundAmount = Double.parseDouble(refundAmountStr); // Convert Refunded Amount to a double

            // Attempt to insert the return details into the database
            boolean success = databaseHelper.insertReturn(loadId, returnDate, refundAmount, reason);
            if (success)
            {
                // Show a success message and navigate back to the previous screen
                Toast.makeText(this, "Return logged successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {
                // Show an error message if the operation failed
                Toast.makeText(this, "Failed to log return", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigates to the previous screen without performing any action
        btnBack.setOnClickListener(v -> finish());
    }
}
