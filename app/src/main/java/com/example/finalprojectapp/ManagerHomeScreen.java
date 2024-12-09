package com.example.finalprojectapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ManagerHomeScreen extends AppCompatActivity implements ManagerEditListingOverlay.OnListingUpdatedListener
{

    private RecyclerView recyclerView;
    private ListingAdapter listingAdapter;
    private DatabaseHelper databaseHelper;
    private Button btnAddListing, btnBack;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home_screen);

        // Initialize views
        recyclerView = findViewById(R.id.rv_listings);
        btnAddListing = findViewById(R.id.btn_add_listing);
        btnSearch = findViewById(R.id.btn_search);
        btnBack = findViewById(R.id.btn_back);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Set RecyclerView layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load listings from the database
        loadAndDisplayListings();

        // Set up "Add Listing" button functionality
        btnAddListing.setOnClickListener(v ->
        {
            Intent intent = new Intent(ManagerHomeScreen.this, ManagerCreateListingScreen.class);
            startActivity(intent);
        });

        // Set up "Search" button functionality
        btnSearch.setOnClickListener(v ->
        {
            Intent intent = new Intent(ManagerHomeScreen.this, ManagerSearchScreen.class);
            startActivity(intent);
        });

        // Back button functionality
        btnBack.setOnClickListener(v ->
        {
            // Close the database to release resources
            databaseHelper.close();

            // Navigate back to the Login screen
            Intent intent = new Intent(ManagerHomeScreen.this, LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadAndDisplayListings()
    {
        List<Listing> listings = loadListings();
        if (listings.isEmpty()) {
            Toast.makeText(this, "No listings found", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Set up the adapter
            if (listingAdapter == null)
            {
                // Pass the correct layout type for the Manager view
                listingAdapter = new ListingAdapter(this, listings, false, false);
                recyclerView.setAdapter(listingAdapter);

                // Set the click listener for each item
                listingAdapter.setOnItemClickListener(listing ->
                {
                    // Use the listing object directly
                    int listingId = listing.getId();

                    // Show the bottom sheet overlay
                    ManagerEditListingOverlay overlay = ManagerEditListingOverlay.newInstance(listingId);
                    overlay.show(getSupportFragmentManager(), "ManagerEditListingOverlay");
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
        loadAndDisplayReturns();
    }

    private void loadAndDisplayReturns()
    {
    }

    @Override
    public void onListingUpdated()
    {
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
