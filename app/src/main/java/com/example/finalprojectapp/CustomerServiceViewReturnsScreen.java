package com.example.finalprojectapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CustomerServiceViewReturnsScreen extends AppCompatActivity
{

    private TextView tvHeaderListingId, tvTotalRefund;
    private RecyclerView rvReturns;
    private Button btnBack;

    private DatabaseHelper databaseHelper;
    private ReturnsAdapter returnsAdapter;
    private int listingId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service_view_returns_screen);

        // Initialize views
        tvHeaderListingId = findViewById(R.id.tv_header_listing_id);
        tvTotalRefund = findViewById(R.id.tv_total_refund);
        rvReturns = findViewById(R.id.rv_returns);
        btnBack = findViewById(R.id.btn_back);

        // Initialize the Log Return button
        Button btnLogReturn = findViewById(R.id.btn_log_return);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Get the listing ID from the intent
        Intent intent = getIntent();
        listingId = intent.getIntExtra("listing_id", -1);

        if (listingId == -1)
        {
            Toast.makeText(this, "Invalid listing ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set the header with the listing ID
        tvHeaderListingId.setText("Listing ID: " + listingId);

        // Set up the Log Return button
        btnLogReturn.setOnClickListener(v ->
        {
            Intent logReturnIntent = new Intent(CustomerServiceViewReturnsScreen.this, CustomerServiceLogReturnScreen.class);
            logReturnIntent.putExtra("listing_id", listingId);
            startActivity(logReturnIntent);
        });

        // Load and display returns
        loadAndDisplayReturns();

        // Back button functionality
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadAndDisplayReturns();
    }

    private void loadAndDisplayReturns()
    {
        // Load all returns for the specific listing
        List<ReturnItem> returns = loadReturnsForListing();
        if (returns.isEmpty())
        {
            Toast.makeText(this, "No returns found for this listing.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Calculate the total refund amount
            double totalRefund = 0;
            for (ReturnItem returnItem : returns)
            {
                totalRefund += returnItem.getRefundAmount();
            }
            // Display the total refund amount
            tvTotalRefund.setText(String.format("Total Refund: $%.2f", totalRefund));

            // Set up or update the RecyclerView adapter
            if (returnsAdapter == null)
            {
                returnsAdapter = new ReturnsAdapter(this, returns);
                rvReturns.setAdapter(returnsAdapter);
                rvReturns.setLayoutManager(new LinearLayoutManager(this));
            }
            else
            {
                returnsAdapter.updateReturns(returns);
            }
        }
    }

    private List<ReturnItem> loadReturnsForListing()
    {
        // Retrieve the list of return items for the given listing ID
        List<ReturnItem> returnItems = new ArrayList<>();
        Cursor cursor = databaseHelper.getReturnsByListingId(listingId);

        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                // Extract return details from the cursor
                int returnId = cursor.getInt(cursor.getColumnIndexOrThrow("return_id"));
                String returnDate = cursor.getString(cursor.getColumnIndexOrThrow("return_date"));
                double refundAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("refund_amount"));
                String reason = cursor.getString(cursor.getColumnIndexOrThrow("reason"));

                // Add a new ReturnItem to the list
                returnItems.add(new ReturnItem(returnId, listingId, returnDate, refundAmount, reason));
            }
            cursor.close();
        }

        return returnItems;
    }
}