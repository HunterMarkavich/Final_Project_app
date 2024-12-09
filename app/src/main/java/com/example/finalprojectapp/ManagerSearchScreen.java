package com.example.finalprojectapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.Editable;
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

public class ManagerSearchScreen extends AppCompatActivity implements ManagerEditListingOverlay.OnListingUpdatedListener
{

    private EditText etPriceMin, etPriceMax, etDateMin, etDateMax, etPalletMin, etPalletMax;
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
        setContentView(R.layout.activity_manager_search_screen);

        // Initialize views
        etPriceMin = findViewById(R.id.et_price_min);
        etPriceMax = findViewById(R.id.et_price_max);
        etDateMin = findViewById(R.id.et_date_min);
        etDateMax = findViewById(R.id.et_date_max);
        etPalletMin = findViewById(R.id.et_pallet_min);
        etPalletMax = findViewById(R.id.et_pallet_max);
        spDistributor = findViewById(R.id.sp_distributor);
        btnSearch = findViewById(R.id.btn_search);
        btnBack = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.rv_search_results);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load distributors into the dropdown
        loadDistributors();

        // Set up button listeners
        btnSearch.setOnClickListener(v -> handleSearch());
        btnBack.setOnClickListener(v -> navigateBack());

        setupTextWatchers();
    }

    @Override
    public void onListingUpdated()
    {
        handleSearch(); // Refresh results after an update
    }

    private void loadDistributors()
    {
        Cursor cursor = dbHelper.getAllDistributors();
        List<String> distributors = new ArrayList<>();
        distributors.add("Select an option...");

        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                distributors.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            }
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, distributors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistributor.setAdapter(adapter);
    }

    private void handleSearch()
    {
        Log.d("ManagerSearchScreen", "Search clicked");

        String priceMin = etPriceMin.getText().toString().trim();
        String priceMax = etPriceMax.getText().toString().trim();
        String dateMin = etDateMin.getText().toString().trim();
        String dateMax = etDateMax.getText().toString().trim();
        String palletMin = etPalletMin.getText().toString().trim();
        String palletMax = etPalletMax.getText().toString().trim();
        String selectedDistributor = spDistributor.getSelectedItem().toString();

        if (!validateInputs(priceMin, priceMax, dateMin, dateMax, palletMin, palletMax, selectedDistributor))
        {
            Toast.makeText(this, "Please provide valid search criteria.", Toast.LENGTH_SHORT).show();
            return;
        }

        listings = queryDatabase(priceMin, priceMax, dateMin, dateMax, palletMin, palletMax, selectedDistributor);

        Log.d("ManagerSearchScreen", "Listings found: " + listings.size());

        if (listings.isEmpty())
        {
            Toast.makeText(this, "No results found.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (listingAdapter == null)
            {
                listingAdapter = new ListingAdapter(this, listings, false, false);
                listingAdapter.setOnItemClickListener(listing ->
                {
                    ManagerEditListingOverlay overlay = ManagerEditListingOverlay.newInstance(listing.getId());
                    overlay.show(getSupportFragmentManager(), "ManagerEditListingOverlay");
                });
                recyclerView.setAdapter(listingAdapter);
            }
            else
            {
                listingAdapter.updateListings(listings);
            }
        }
    }

    private boolean validateInputs(String priceMin, String priceMax, String dateMin, String dateMax, String palletMin, String palletMax, String distributor)
    {
        return !TextUtils.isEmpty(priceMin) || !TextUtils.isEmpty(priceMax) ||
                !TextUtils.isEmpty(dateMin) || !TextUtils.isEmpty(dateMax) ||
                !TextUtils.isEmpty(palletMin) || !TextUtils.isEmpty(palletMax) ||
                !"Select an option...".equals(distributor);
    }

    private List<Listing> queryDatabase(String priceMin, String priceMax, String dateMin, String dateMax, String palletMin, String palletMax, String distributor)
    {
        List<Listing> results = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        // Build the WHERE clause
        if (!TextUtils.isEmpty(priceMin) && !TextUtils.isEmpty(priceMax))
        {
            selection.append("price BETWEEN ? AND ?");
            selectionArgs.add(priceMin);
            selectionArgs.add(priceMax);
        }
        if (!TextUtils.isEmpty(dateMin) && !TextUtils.isEmpty(dateMax))
        {
            dateMin = convertDateToDatabaseFormat(dateMin);
            dateMax = convertDateToDatabaseFormat(dateMax);
            if (selection.length() > 0) selection.append(" AND ");
            selection.append("datePurchased BETWEEN ? AND ?");
            selectionArgs.add(dateMin);
            selectionArgs.add(dateMax);
        }
        if (!TextUtils.isEmpty(palletMin) && !TextUtils.isEmpty(palletMax))
        {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append("numPallets BETWEEN ? AND ?");
            selectionArgs.add(palletMin);
            selectionArgs.add(palletMax);
        }
        if (!"Select an option...".equals(distributor))
        {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append("distributor = ?");
            selectionArgs.add(distributor);
        }

        Cursor cursor = db.query(
                "listings",
                null,
                selection.toString(),
                selectionArgs.toArray(new String[0]),
                null,
                null,
                null
        );

        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String dbDistributor = cursor.getString(cursor.getColumnIndexOrThrow("distributor"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("datePurchased"));
                int pallets = cursor.getInt(cursor.getColumnIndexOrThrow("numPallets"));
                int damaged = cursor.getInt(cursor.getColumnIndexOrThrow("damagedCount"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                double earnings = cursor.getDouble(cursor.getColumnIndexOrThrow("earnings"));
                double pay = cursor.getDouble(cursor.getColumnIndexOrThrow("salespersonPay"));
                double profit = cursor.getDouble(cursor.getColumnIndexOrThrow("profit"));

                results.add(new Listing(id, dbDistributor, date, pallets, damaged, price, earnings, pay, profit));
            }
            cursor.close();
        }

        return results;
    }

    private String convertDateToDatabaseFormat(String date) {
        if (date == null || !date.matches("\\d{2}/\\d{2}/\\d{4}"))
        {
            Log.d("DateConversion", "Invalid date format: " + date);
            return date;
        }
        String[] parts = date.split("/");
        return parts[2] + "-" + parts[0] + "-" + parts[1];
    }

    private void navigateBack()
    {
        Intent intent = new Intent(ManagerSearchScreen.this, ManagerHomeScreen.class);
        startActivity(intent);
        finish();
    }

    private void setupTextWatchers()
    {
        setupTextWatcherForDate(etDateMin);
        setupTextWatcherForDate(etDateMax);
    }

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
                if (isEditing) return;
                isEditing = true;

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

                editText.setText(formatted);
                editText.setSelection(formatted.length());
                isEditing = false;
            }
        });
    }
}
