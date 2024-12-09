package com.example.finalprojectapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SalespersonEditListingOverlay extends BottomSheetDialogFragment
{

    public interface OnListingUpdatedListener
    {
        void onListingUpdated();
    }

    private static final String ARG_LISTING_ID = "listing_id";

    private TextView tvDateValue;
    private EditText etDamagedInventory, etTotalSales;
    private Button btnUpdate;
    private int listingId;
    private DatabaseHelper dbHelper;
    private OnListingUpdatedListener listener;

    public static SalespersonEditListingOverlay newInstance(int listingId)
    {
        SalespersonEditListingOverlay fragment = new SalespersonEditListingOverlay();
        Bundle args = new Bundle();
        args.putInt(ARG_LISTING_ID, listingId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context)
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
        View view = inflater.inflate(R.layout.activity_salesperson_edit_listing_overlay, container, false);

        // Initialize views
        tvDateValue = view.findViewById(R.id.tv_date_value);
        etDamagedInventory = view.findViewById(R.id.et_damaged_inventory);
        etTotalSales = view.findViewById(R.id.et_total_sales);
        btnUpdate = view.findViewById(R.id.btn_update);

        dbHelper = new DatabaseHelper(requireContext());

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

    private void loadListingData(int id)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("listings", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst())
        {
            tvDateValue.setText(cursor.getString(cursor.getColumnIndexOrThrow("datePurchased")));
            etDamagedInventory.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("damagedCount")))); // Set Damaged Count
            etTotalSales.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("earnings"))));

            cursor.close();
        }
    }

    private void updateListing()
    {
        String damagedInventory = etDamagedInventory.getText().toString().trim();
        String totalSales = etTotalSales.getText().toString().trim();

        if (damagedInventory.isEmpty() || totalSales.isEmpty())
        {
            Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        try
        {
            int damagedValue = Integer.parseInt(damagedInventory);
            double totalSalesValue = Double.parseDouble(totalSales);

            // Calculate salesperson pay (10% of total sales)
            double salespersonPay = totalSalesValue * 0.10;

            // Calculate profit: total sales - damaged cost - salesperson pay
            // Assume each damaged item has a fixed cost, e.g., $100 per item (adjust as needed)
            double damagedCost = damagedValue * 100;
            double profit = totalSalesValue - damagedCost - salespersonPay;

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("damagedCount", damagedValue);
            values.put("earnings", totalSalesValue);
            values.put("salespersonPay", salespersonPay);
            values.put("profit", profit);

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
        }
        catch (NumberFormatException e)
        {
            Toast.makeText(requireContext(), "Invalid input. Please check the numbers.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }
}
