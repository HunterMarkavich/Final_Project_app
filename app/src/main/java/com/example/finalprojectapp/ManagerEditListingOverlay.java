package com.example.finalprojectapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.ArrayList;
import java.util.List;

public class ManagerEditListingOverlay extends BottomSheetDialogFragment
{

    public interface OnListingUpdatedListener
    {
        void onListingUpdated();
    }

    private static final String ARG_LISTING_ID = "listing_id";

    private EditText etPrice, etEarnings, etDate, etPallets, etDamaged;
    private Spinner spDistributor;
    private TextView tvCreateNewDistributor;
    private Button btnUpdate;
    private int listingId;
    private DatabaseHelper dbHelper;
    private OnListingUpdatedListener listener;

    public static ManagerEditListingOverlay newInstance(int listingId)
    {
        ManagerEditListingOverlay fragment = new ManagerEditListingOverlay();
        Bundle args = new Bundle();
        args.putInt(ARG_LISTING_ID, listingId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull android.content.Context context)
    {
        super.onAttach(context);
        if (context instanceof OnListingUpdatedListener)
        {
            listener = (OnListingUpdatedListener) context;
        }
        else
        {
            throw new RuntimeException(context + " must implement OnListingUpdatedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_manager_edit_listing_overlay, container, false);

        // Initialize views
        etPrice = view.findViewById(R.id.et_price);
        etEarnings = view.findViewById(R.id.et_earnings);
        etDate = view.findViewById(R.id.et_date);
        etPallets = view.findViewById(R.id.et_pallets);
        etDamaged = view.findViewById(R.id.et_damaged);
        spDistributor = view.findViewById(R.id.sp_distributor);
        tvCreateNewDistributor = view.findViewById(R.id.tv_create_new_distributor);
        btnUpdate = view.findViewById(R.id.btn_update);

        dbHelper = new DatabaseHelper(requireContext());

        // Load distributor dropdown
        loadDistributors();

        // Set navigation to Add Distributor screen
        tvCreateNewDistributor.setOnClickListener(v ->
        {
            Intent intent = new Intent(requireContext(), ManagerAddDistributorScreen.class);
            startActivity(intent);
        });

        // Load listing data
        if (getArguments() != null)
        {
            listingId = getArguments().getInt(ARG_LISTING_ID);
            loadListingData(listingId);
        }

        // Set listener for the update button
        btnUpdate.setOnClickListener(v -> updateListing());

        return view;
    }

    private void loadDistributors()
    {
        Cursor cursor = dbHelper.getAllDistributors();
        List<String> distributors = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext())
            {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                distributors.add(name);
            }
            cursor.close();
        }

        // Set distributors in Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, distributors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistributor.setAdapter(adapter);
    }

    private void loadListingData(int id)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("listings", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst())
        {
            etPrice.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("price"))));
            etEarnings.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("earnings"))));
            etDate.setText(cursor.getString(cursor.getColumnIndexOrThrow("datePurchased")));
            etPallets.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("numPallets"))));
            etDamaged.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("damagedCount")))); // Set Damaged Count

            // Set distributor dropdown value
            String distributor = cursor.getString(cursor.getColumnIndexOrThrow("distributor"));
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spDistributor.getAdapter();
            if (adapter != null)
            {
                int position = adapter.getPosition(distributor);
                if (position != -1)
                {
                    spDistributor.setSelection(position);
                }
            }
            cursor.close();
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onResume()
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, distributors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistributor.setAdapter(adapter);
    }

    private void updateListing()
    {
        String earnings = etEarnings.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String pallets = etPallets.getText().toString().trim();
        String damaged = etDamaged.getText().toString().trim();
        String distributor = spDistributor.getSelectedItem().toString();

        if (earnings.isEmpty() || date.isEmpty() || pallets.isEmpty() || damaged.isEmpty() || distributor.isEmpty())
        {
            Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        try
        {
            double earningsValue = Double.parseDouble(earnings);
            int palletsValue = Integer.parseInt(pallets);
            int damagedValue = Integer.parseInt(damaged);

            // Calculate profit (e.g., earnings minus damaged cost)
            double damagedCost = damagedValue * 100;
            double profitValue = earningsValue - damagedCost;

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("earnings", earningsValue);
            values.put("datePurchased", date);
            values.put("numPallets", palletsValue);
            values.put("damagedCount", damagedValue);
            values.put("profit", profitValue); // Store the calculated profit
            values.put("distributor", distributor);

            int rows = db.update("listings", values, "id = ?", new String[]{String.valueOf(listingId)});
            if (rows > 0)
            {
                Toast.makeText(requireContext(), "Listing updated successfully!", Toast.LENGTH_SHORT).show();
                if (listener != null)
                {
                    listener.onListingUpdated(); // Notify listener
                }
                dismiss();
            }
            else
            {
                Toast.makeText(requireContext(), "Failed to update listing.", Toast.LENGTH_SHORT).show();
            }

            db.close();
        } catch (NumberFormatException e)
        {
            Toast.makeText(requireContext(), "Invalid input. Please check the numbers.", Toast.LENGTH_SHORT).show();
        }
    }
}
