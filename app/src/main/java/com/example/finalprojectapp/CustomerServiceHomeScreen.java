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

public class CustomerServiceHomeScreen extends AppCompatActivity
{

    // Declare UI elements and helper classes
    private RecyclerView recyclerView;
    private ListingAdapter listingAdapter;
    private DatabaseHelper databaseHelper;
    private Button btnSearch, btnLogReturn, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service_home_screen);

        // Initialize views
        // RecyclerView is used to display a list of listings
        recyclerView = findViewById(R.id.rv_listings);

        // Button for opening the Search screen
        btnSearch = findViewById(R.id.btn_search);

        // Button for logging returns
        btnLogReturn = findViewById(R.id.btn_log_return);

        // Button to navigate back to the Login screen
        btnBack = findViewById(R.id.btn_back);

        // Initialize database helper for accessing stored data
        databaseHelper = new DatabaseHelper(this);

        // Set up the RecyclerView with a vertical layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load listings from the database and display them in the RecyclerView
        loadAndDisplayListings();

        // Navigates to the CustomerServiceSearchScreen when clicked
        btnSearch.setOnClickListener(v ->
        {
            Intent intent = new Intent(CustomerServiceHomeScreen.this, CustomerServiceSearchScreen.class);
            startActivity(intent);
        });

        // Navigates to the CustomerServiceLogReturnScreen when clicked
        btnLogReturn.setOnClickListener(v ->
        {
            Intent intent = new Intent(CustomerServiceHomeScreen.this, CustomerServiceLogReturnScreen.class);
            startActivity(intent);
        });

        // Closes database resources and navigates back to the Login screen
        btnBack.setOnClickListener(v ->
        {
            // Close the database to release resources
            databaseHelper.close();

            // Navigate back to the Login screen
            Intent intent = new Intent(CustomerServiceHomeScreen.this, LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    // Loads listings from the database and displays them in the RecyclerView
    private void loadAndDisplayListings()
    {
        // Fetch listings from the database
        List<Listing> listings = loadListings();

        // Show a message if no listings are found
        if (listings.isEmpty())
        {
            Toast.makeText(this, "No listings found", Toast.LENGTH_SHORT).show();
        } else
        {
            // Check if the adapter is already set
            if (listingAdapter == null)
            {
                // Initialize the adapter for Customer Service view
                listingAdapter = new ListingAdapter(this, listings, false, true);
                recyclerView.setAdapter(listingAdapter);

                // Set click listener for each listing item in the RecyclerView
                listingAdapter.setOnItemClickListener(listing ->
                {
                    // Navigate to CustomerServiceViewReturnsScreen and pass the listing ID
                    Intent intent = new Intent(CustomerServiceHomeScreen.this, CustomerServiceViewReturnsScreen.class);
                    intent.putExtra("listing_id", listing.getId());
                    startActivity(intent);
                });
            }

            else
            {
                // If adapter already exists, update its data
                listingAdapter.updateListings(listings);
            }
        }
    }

    // Fetches all listings from the database
    private List<Listing> loadListings()
    {
        // Create a list to hold the fetched listings
        List<Listing> listings = new ArrayList<>();

        // Query the database to retrieve all listings
        Cursor cursor = databaseHelper.getAllListings();

        // Iterate through the cursor to extract listing data
        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                // Extract data for each listing
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String distributor = cursor.getString(cursor.getColumnIndexOrThrow("distributor"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("datePurchased"));
                int damagedCount = cursor.getInt(cursor.getColumnIndexOrThrow("damagedCount"));

                // Create a new Listing object and add it to the list
                Listing listing = new Listing(id, distributor, date, damagedCount);
                listings.add(listing);
            }

            cursor.close();
        }

        // Return the list of listings
        return listings;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Reload and refresh the displayed listings when returning to the screen
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
