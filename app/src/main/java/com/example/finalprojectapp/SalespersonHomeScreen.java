package com.example.finalprojectapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SalespersonHomeScreen extends AppCompatActivity implements SalespersonEditListingOverlay.OnListingUpdatedListener
{

    private RecyclerView recyclerView;
    private ListingAdapter listingAdapter;
    private DatabaseHelper databaseHelper;
    private TextView tvTotalPay;
    private Button btnSearch, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesperson_home_screen);

        // Initialize views
        recyclerView = findViewById(R.id.rv_listings);
        tvTotalPay = findViewById(R.id.tv_salesperson_total_pay);
        btnSearch = findViewById(R.id.btn_search);
        btnBack = findViewById(R.id.btn_back);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Set RecyclerView layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load and display listings
        loadAndDisplayListings();

        // Set up "Search" button functionality
        btnSearch.setOnClickListener(v ->
        {
            Intent intent = new Intent(SalespersonHomeScreen.this, SalespersonSearchScreen.class);
            startActivity(intent);
        });

        // Back button functionality
        btnBack.setOnClickListener(v ->
        {
            // Close the database to release resources
            databaseHelper.close();

            // Navigate back to the Login screen
            Intent intent = new Intent(SalespersonHomeScreen.this, LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadAndDisplayListings()
    {
        List<Listing> listings = loadListings();
        if (listings.isEmpty())
        {
            Toast.makeText(this, "No listings found", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Calculate total pay
            double totalPay = 0;
            for (Listing listing : listings)
            {
                totalPay += listing.getSalespersonPay();
            }
            tvTotalPay.setText(String.format("Pay: $%.2f", totalPay));

            // Set up the adapter
            if (listingAdapter == null)
            {
                listingAdapter = new ListingAdapter(this, listings, true, false); // 'true' for Salesperson view, 'false' for Customer Service view
                recyclerView.setAdapter(listingAdapter);

                // Set the click listener for each item
                listingAdapter.setOnItemClickListener(listing ->
                {
                    int listingId = listing.getId();

                    // Show a simplified bottom sheet overlay for salesperson
                    SalespersonEditListingOverlay overlay = SalespersonEditListingOverlay.newInstance(listingId);
                    overlay.show(getSupportFragmentManager(), "SalespersonViewListingOverlay");
                });
            }
            else
            {
                listingAdapter.updateListings(listings); // Refresh existing adapter
            }
        }
    }

    // Load listings from the database
    private List<Listing> loadListings()
    {
        List<Listing> listings = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllListings();

        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String distributor = cursor.getString(cursor.getColumnIndexOrThrow("distributor"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("datePurchased"));
                int pallets = cursor.getInt(cursor.getColumnIndexOrThrow("numPallets"));
                int damaged = cursor.getInt(cursor.getColumnIndexOrThrow("damagedCount"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                double earnings = cursor.getDouble(cursor.getColumnIndexOrThrow("earnings"));
                double pay = cursor.getDouble(cursor.getColumnIndexOrThrow("salespersonPay"));
                double profit = cursor.getDouble(cursor.getColumnIndexOrThrow("profit"));

                Listing listing = new Listing(id, distributor, date, pallets, damaged, price, earnings, pay, profit);
                listings.add(listing);
            }
            cursor.close();
        }

        return listings;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadAndDisplayListings();
    }

    @Override
    public void onListingUpdated()
    {
        // Reload data to reflect updates
        loadAndDisplayListings();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // Close the database helper to avoid resource leaks
        if (databaseHelper != null)
        {
            databaseHelper.close();
        }
    }
}
