package com.example.finalprojectapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomerServiceSearchScreen extends AppCompatActivity
{

    // Declare UI elements and helper class
    private EditText etId, etDateMin, etDateMax;
    private Spinner spDistributor;
    private Button btnSearch, btnBack;
    private RecyclerView recyclerView;

    private DatabaseHelper dbHelper;
    private ListingAdapter listingAdapter;
    private List<Listing> listings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service_search_screen);

        // Input field for ID
        etId = findViewById(R.id.et_id);
        // Input field for minimum date
        etDateMin = findViewById(R.id.et_date_min);
        // Input field for maximum date
        etDateMax = findViewById(R.id.et_date_max);
        // Dropdown for distributor selection
        spDistributor = findViewById(R.id.sp_distributor);
        // Search button
        btnSearch = findViewById(R.id.btn_search);
        // Back button
        btnBack = findViewById(R.id.btn_back);
        // RecyclerView for displaying search results
        recyclerView = findViewById(R.id.rv_search_results);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Set up RecyclerView with a vertical layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load distributors into the dropdown menu
        loadDistributors();

        // Set up button listeners
        btnSearch.setOnClickListener(v -> handleSearch());
        btnBack.setOnClickListener(v -> finish());

        // Add TextWatchers to format date inputs
        setupDateTextWatchers();
    }

    // Set up text watchers to auto-format date inputs
    private void setupDateTextWatchers()
    {
        setupTextWatcherForDate(etDateMin);
        setupTextWatcherForDate(etDateMax);
    }

    // Add a TextWatcher to format date fields as MM/DD/YYYY
    private void setupTextWatcherForDate(EditText editText)
    {
        editText.addTextChangedListener(new TextWatcher()
        {
            private boolean isEditing;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s)
            {
                if (isEditing) return; // Avoid recursive edits
                isEditing = true;

                // Format the input to MM/DD/YYYY
                String input = s.toString().replace("/", "");
                StringBuilder formatted = new StringBuilder();

                if (input.length() > 2)
                {
                    formatted.append(input.substring(0, 2)).append("/");
                    if (input.length() > 4)
                    {
                        formatted.append(input.substring(2, 4)).append("/");
                        formatted.append(input.substring(4, Math.min(input.length(), 8)));
                    }
                    else
                    {
                        formatted.append(input.substring(2));
                    }
                }
                else
                {
                    formatted.append(input);
                }

                // Update the input field with the formatted date
                editText.setText(formatted);
                editText.setSelection(formatted.length());
                isEditing = false;
            }
        });
    }

    // Load all distributors into the Spinner dropdown
    private void loadDistributors() {
        Cursor cursor = dbHelper.getAllDistributors();
        List<String> distributors = new ArrayList<>();
        distributors.add("Select an option...");

        if (cursor != null) {
            while (cursor.moveToNext())
            {
                distributors.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            }
            cursor.close();
        }

        // Create and set the adapter for the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, distributors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistributor.setAdapter(adapter);
    }

    // Handle the search button click
    private void handleSearch()
    {
        Log.d("SearchScreen", "Handle search clicked");

        // Retrieve input values
        String id = etId.getText().toString().trim();
        String dateMin = etDateMin.getText().toString().trim();
        String dateMax = etDateMax.getText().toString().trim();
        String selectedDistributor = spDistributor.getSelectedItem().toString();

        Log.d("SearchScreen", "Inputs: ID=" + id + ", DateMin=" + dateMin + ", DateMax=" + dateMax + ", Distributor=" + selectedDistributor);

        // Validate inputs
        if (!validateInputs(id, dateMin, dateMax, selectedDistributor))
        {
            Toast.makeText(this, "Please provide at least one search criteria.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform database query
        listings = queryDatabase(id, dateMin, dateMax, selectedDistributor);

        Log.d("SearchScreen", "Listings size: " + listings.size());

        // Update RecyclerView with search results
        if (listingAdapter == null)
        {
            Log.d("SearchScreen", "Creating new ListingAdapter");
            listingAdapter = new ListingAdapter(this, listings, false, true); // true for Customer Service view
            recyclerView.setAdapter(listingAdapter);
        }
        else
        {
            Log.d("SearchScreen", "Updating existing ListingAdapter");
            listingAdapter.updateListings(listings); // Update existing adapter
        }

        // Show message if no results were found
        if (listings.isEmpty())
        {
            Toast.makeText(this, "No results found.", Toast.LENGTH_SHORT).show();
        }
    }

    // Validate that at least one search field has been filled
    private boolean validateInputs(String id, String dateMin, String dateMax, String distributor) {
        return !TextUtils.isEmpty(id) || !TextUtils.isEmpty(dateMin) || !TextUtils.isEmpty(dateMax) || !"Select an option...".equals(distributor);
    }

    // Query the database based on search criteria
    private List<Listing> queryDatabase(String id, String dateMin, String dateMax, String distributor)
    {
        List<Listing> results = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        // Log raw input values for debugging
        Log.d("SearchScreen", "Raw Inputs: ID=" + id + ", DateMin=" + dateMin + ", DateMax=" + dateMax + ", Distributor=" + distributor);

        // Build the WHERE clause based on inputs
        if (!TextUtils.isEmpty(id))
        {
            selection.append("id = ?");
            selectionArgs.add(id);
        }
        if (!TextUtils.isEmpty(dateMin) && !TextUtils.isEmpty(dateMax))
        {
            dateMin = convertDateToDatabaseFormat(dateMin);
            dateMax = convertDateToDatabaseFormat(dateMax);

            Log.d("SearchScreen", "Converted Dates: DateMin=" + dateMin + ", DateMax=" + dateMax);

            if (selection.length() > 0) selection.append(" AND ");
            selection.append("datePurchased BETWEEN ? AND ?");
            selectionArgs.add(dateMin);
            selectionArgs.add(dateMax);
        }
        if (!"Select an option...".equals(distributor))
        {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append("distributor = ?");
            selectionArgs.add(distributor);
        }

        // Execute the query
        Cursor cursor = db.query(
                "listings",
                null,
                selection.toString(),
                selectionArgs.toArray(new String[0]),
                null,
                null,
                "datePurchased ASC"
        );

        // Process query results
        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                int listingId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String dbDistributor = cursor.getString(cursor.getColumnIndexOrThrow("distributor"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("datePurchased"));
                int damagedCount = cursor.getInt(cursor.getColumnIndexOrThrow("damagedCount"));

                Log.d("SearchScreen", "Listing Found - ID: " + listingId + ", Distributor: " + dbDistributor + ", Date: " + date);

                results.add(new Listing(listingId, dbDistributor, date, damagedCount));
            }
            cursor.close();
        }
        else
        {
            Log.d("SearchScreen", "No results found.");
        }

        return results;
    }

    // Convert date format from MM/DD/YYYY to YYYY-MM-DD for database query
    private String convertDateToDatabaseFormat(String date)
    {
        if (date == null || !date.matches("\\d{2}/\\d{2}/\\d{4}"))
        {
            Log.d("DateConversion", "Invalid date format: " + date);
            return date;
        }
        String[] parts = date.split("/");
        String convertedDate = parts[2] + "-" + parts[0] + "-" + parts[1];
        Log.d("DateConversion", "Converted Date: " + convertedDate);
        return convertedDate;
    }
}