package com.example.finalprojectapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper
{

    // Database Information
    private static final String DATABASE_NAME = "FinalProjectApp.db";
    private static final int DATABASE_VERSION = 16;

    // Users Table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username"; // Primary key
    private static final String COLUMN_FIRST_NAME = "firstName";
    private static final String COLUMN_LAST_NAME = "lastName";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE = "role";

    // Listings Table
    private static final String TABLE_LISTINGS = "listings";
    private static final String COLUMN_ID = "id"; // Primary key
    private static final String COLUMN_DISTRIBUTOR = "distributor";
    private static final String COLUMN_DATE = "datePurchased";
    private static final String COLUMN_PALLETS = "numPallets";
    private static final String COLUMN_DAMAGED = "damagedCount";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_EARNINGS = "earnings";
    private static final String COLUMN_PAY = "salespersonPay";
    private static final String COLUMN_PROFIT = "profit";

    // Distributors Table
    private static final String TABLE_DISTRIBUTORS = "distributors";
    private static final String COLUMN_NAME = "name"; // Primary key

    // Returns Table
    private static final String TABLE_RETURNS = "returns";
    private static final String COLUMN_RETURN_ID = "return_id"; // Primary key
    private static final String COLUMN_LISTING_ID = "listing_id";
    private static final String COLUMN_RETURN_DATE = "return_date";
    private static final String COLUMN_REFUND_AMOUNT = "refund_amount";
    private static final String COLUMN_REASON = "reason";

    //Table SQL Statements
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" + COLUMN_USERNAME + " TEXT PRIMARY KEY, " + COLUMN_FIRST_NAME + " TEXT, " + COLUMN_LAST_NAME + " TEXT, " + COLUMN_PASSWORD + " TEXT, " + COLUMN_ROLE + " TEXT)";

    private static final String CREATE_TABLE_LISTINGS =
            "CREATE TABLE " + TABLE_LISTINGS + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_DISTRIBUTOR + " TEXT, " + COLUMN_DATE + " TEXT, " + COLUMN_PALLETS + " INTEGER, " + COLUMN_DAMAGED + " INTEGER, " +
                    COLUMN_PRICE + " REAL, " + COLUMN_EARNINGS + " REAL, " + COLUMN_PAY + " REAL, " + COLUMN_PROFIT + " REAL)";

    private static final String CREATE_TABLE_DISTRIBUTORS =
            "CREATE TABLE " + TABLE_DISTRIBUTORS + " (" + COLUMN_NAME + " TEXT PRIMARY KEY)";

    private static final String CREATE_TABLE_RETURNS =
            "CREATE TABLE " + TABLE_RETURNS + " (" + COLUMN_RETURN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_LISTING_ID + " INTEGER, " + COLUMN_RETURN_DATE + " TEXT, " + COLUMN_REFUND_AMOUNT + " REAL, " +
                    COLUMN_REASON + " TEXT, " + "FOREIGN KEY (" + COLUMN_LISTING_ID + ") REFERENCES " + TABLE_LISTINGS + "(" + COLUMN_ID + ")" + ")";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_LISTINGS);
        db.execSQL(CREATE_TABLE_DISTRIBUTORS);
        db.execSQL(CREATE_TABLE_RETURNS);

        addDummyUsers(db);
        addDummyListings(db);
        addDummyDistributors(db);
        addDummyReturns(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISTRIBUTORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RETURNS);
        onCreate(db);
    }

    // Dummy Data for Users
    private void addDummyUsers(SQLiteDatabase db)
    {
        insertUser(db, "manager1", "John", "Doe", "managerpass", "Manager");
        insertUser(db, "salesperson1", "Jane", "Smith", "salespass", "Salesperson");
        insertUser(db, "customersvc1", "Mike", "Johnson", "servicepass", "Customer Service");
    }

    private void insertUser(SQLiteDatabase db, String username, String firstName, String lastName, String password, String role)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);
        db.insert(TABLE_USERS, null, values);
    }

    // Dummy Data for Listings
    private void addDummyListings(SQLiteDatabase db)
    {
        ContentValues values = new ContentValues();
        String[][] listings =
                {
                {"J&L Pallets", "2024-10-27", "5", "7", "700", "1000", "200", "100"},
                {"Bulk Liq.", "2024-10-20", "6", "10", "1000", "4000", "800", "2200"},
                {"Paul Marts", "2024-10-15", "8", "2", "3000", "12000", "2400", "6600"},
                {"J&L Pallets", "2024-10-07", "5", "4", "1200", "4000", "800", "2000"},
                {"Bulk Liq.", "2024-09-29", "4", "2", "1500", "8000", "1600", "4900"},
                {"J&L Pallets", "2024-09-20", "2", "9", "2000", "1000", "200", "-1200"},
                {"Paul Marts", "2024-09-15", "6", "8", "3000", "9000", "900", "5100"}
        };

        for (String[] listing : listings)
        {
            values.clear();
            values.put(COLUMN_DISTRIBUTOR, listing[0]);
            values.put(COLUMN_DATE, listing[1]);
            values.put(COLUMN_PALLETS, Integer.parseInt(listing[2]));
            values.put(COLUMN_DAMAGED, Integer.parseInt(listing[3]));
            values.put(COLUMN_PRICE, Double.parseDouble(listing[4]));
            values.put(COLUMN_EARNINGS, Double.parseDouble(listing[5]));
            values.put(COLUMN_PAY, Double.parseDouble(listing[6]));
            values.put(COLUMN_PROFIT, Double.parseDouble(listing[7]));
            db.insert(TABLE_LISTINGS, null, values);
        }
    }

    // Dummy Data for Distributors
    private void addDummyDistributors(SQLiteDatabase db)
    {
        String[] distributors = {"J&L Pallets", "Paul Marts", "Bulk Liq."};
        ContentValues values = new ContentValues();

        for (String distributor : distributors)
        {
            values.clear();
            values.put(COLUMN_NAME, distributor);
            db.insert(TABLE_DISTRIBUTORS, null, values);
        }
    }

    // Dummy Data for Returns
    private void addDummyReturns(SQLiteDatabase db)
    {
        String[][] returns =
                {
                // Listing ID 1
                {"1", "10/28/2024", "10", "Wrong color"},
                {"1", "10/30/2024", "15", "Damaged (Broken handle)"},
                {"1", "11/01/2024", "5", "Cancelled order"},
                {"1", "11/02/2024", "15", "Didn’t power on"},
                {"1", "11/03/2024", "10", "Wrong size"},
                {"1", "11/04/2024", "10", "Didn’t like it"},
                {"1", "11/05/2024", "10", "Accidentally bought two"},

                // Listing ID 2
                {"2", "10/28/2024", "20", "Defective"},
                {"2", "10/29/2024", "10", "Wrong color"},
                {"2", "10/30/2024", "5", "Didn’t work as expected"},

                // Listing ID 3
                {"3", "10/28/2024", "10", "Wrong item shipped"},
                {"3", "10/29/2024", "10", "Defective"},

                // Listing ID 4
                {"4", "10/28/2024", "15", "Didn’t work as expected"},
                {"4", "10/29/2024", "20", "Damaged (Cracked)"},
                {"4", "10/30/2024", "10", "Missing parts"},
                {"4", "11/01/2024", "5", "Cancelled order"},

                // Listing ID 5
                {"5", "10/28/2024", "15", "Didn’t power on"},
                {"5", "10/29/2024", "10", "Wrong item shipped"},

                // Listing ID 6
                {"6", "10/28/2024", "10", "Defective"},
                {"6", "10/29/2024", "15", "Damaged (Broken handle)"},
                {"6", "10/30/2024", "5", "Didn’t power on"},
                {"6", "10/31/2024", "20", "Cancelled order"},
                {"6", "11/01/2024", "10", "Wrong size"},
                {"6", "11/02/2024", "5", "Didn’t like it"},
                {"6", "11/03/2024", "10", "Wrong color"},
                {"6", "11/04/2024", "15", "Defective"},
                {"6", "11/05/2024", "10", "Damaged (Scratches)"},

                // Listing ID 7
                {"7", "10/28/2024", "15", "Wrong size"},
                {"7", "10/29/2024", "10", "Didn’t power on"},
                {"7", "10/30/2024", "10", "Damaged (Cracked)"},
                {"7", "10/31/2024", "20", "Wrong item shipped"},
                {"7", "11/01/2024", "5", "Cancelled order"},
                {"7", "11/02/2024", "10", "Didn’t work as expected"},
                {"7", "11/03/2024", "15", "Wrong color"},
                {"7", "11/04/2024", "10", "Defective"}
        };

        for (String[] returnData : returns)
        {
            ContentValues values = new ContentValues();
            values.put(COLUMN_LISTING_ID, Integer.parseInt(returnData[0]));
            values.put(COLUMN_RETURN_DATE, returnData[1]);
            values.put(COLUMN_REFUND_AMOUNT, Double.parseDouble(returnData[2]));
            values.put(COLUMN_REASON, returnData[3]);
            db.insert(TABLE_RETURNS, null, values);
        }
    }

    // Retrieve Returns for a Listing
    public Cursor getReturnsByListingId(int listingId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_RETURNS,
                null,
                COLUMN_LISTING_ID + " = ?",
                new String[]{String.valueOf(listingId)},
                null,
                null,
                COLUMN_RETURN_DATE + " ASC"
        );
    }

    // Add a Return
    public long addReturn(int listingId, String returnDate, double refundAmount, String reason)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LISTING_ID, listingId);
        values.put(COLUMN_RETURN_DATE, returnDate);
        values.put(COLUMN_REFUND_AMOUNT, refundAmount);
        values.put(COLUMN_REASON, reason);
        return db.insert(TABLE_RETURNS, null, values);
    }

    // Add Distributor (for ManagerAddDistributorScreen)
    public boolean addDistributor(String distributorName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, distributorName);

        long result = db.insert(TABLE_DISTRIBUTORS, null, values);
        db.close();
        return result != -1;
    }

    // Query to Get All Listings
    public Cursor getAllListings()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_LISTINGS,
                null, // Select all columns
                null, // No WHERE clause
                null, // No WHERE arguments
                null, // No GROUP BY
                null, // No HAVING
                null  // No ORDER BY
        );
    }

    // Query to Get All Distributors
    public Cursor getAllDistributors()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_DISTRIBUTORS, // Table name
                new String[]{COLUMN_NAME}, // Columns to return
                null, // No WHERE clause
                null, // No WHERE arguments
                null, // No GROUP BY
                null, // No HAVING
                COLUMN_NAME // ORDER BY column
        );
    }

    // Validate User Credentials
    public String validateUser(String username, String password)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COLUMN_ROLE},
                COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{username, password},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst())
        {
            String role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE));
            cursor.close();
            return role;
        }

        if (cursor != null)
        {
            cursor.close();
        }
        return null;
    }

    // Add a User (Used by SignUpScreen)
    public boolean addUser(String username, String firstName, String lastName, String password, String role)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean insertReturn(int listingId, String returnDate, double refundAmount, String reason)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        // Insert the new return
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LISTING_ID, listingId);
        contentValues.put(COLUMN_RETURN_DATE, returnDate);
        contentValues.put(COLUMN_REFUND_AMOUNT, refundAmount);
        contentValues.put(COLUMN_REASON, reason);

        long result = db.insert(TABLE_RETURNS, null, contentValues);

        if (result != -1)
        {
            // Increment the damagedCount for the associated listing
            db.execSQL("UPDATE " + TABLE_LISTINGS + " SET " + COLUMN_DAMAGED + " = " + COLUMN_DAMAGED + " + 1 WHERE " + COLUMN_ID + " = ?", new Object[]{listingId});

            // Deduct refundAmount from earnings, profit, and salesperson pay
            db.execSQL("UPDATE " + TABLE_LISTINGS +
                            " SET " + COLUMN_EARNINGS + " = " + COLUMN_EARNINGS + " - ?, " +
                            COLUMN_PROFIT + " = " + COLUMN_PROFIT + " - ?, " +
                            COLUMN_PAY + " = " + COLUMN_PAY + " - (? * 0.1) " + // Assuming 10% commission for salesperson
                            " WHERE " + COLUMN_ID + " = ?",
                    new Object[]{refundAmount, refundAmount, refundAmount, listingId});

            return true;
        }
        return false;
    }

    private List<Listing> queryDatabase(String id, String dateMin, String dateMax, String distributor)
    {
        List<Listing> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        // Log input
        System.out.println("Input ID: " + id);
        System.out.println("Input Date Min: " + dateMin);
        System.out.println("Input Date Max: " + dateMax);
        System.out.println("Input Distributor: " + distributor);

        // Add ID filter
        if (!TextUtils.isEmpty(id))
        {
            selection.append("id = ?");
            selectionArgs.add(id);
        }

        // Add date range filter
        if (!TextUtils.isEmpty(dateMin) && !TextUtils.isEmpty(dateMax))
        {
            dateMin = convertDateToDatabaseFormat(dateMin);
            dateMax = convertDateToDatabaseFormat(dateMax);

            System.out.println("Converted Date Min: " + dateMin);
            System.out.println("Converted Date Max: " + dateMax);

            if (selection.length() > 0) selection.append(" AND ");
            selection.append("datePurchased BETWEEN ? AND ?");
            selectionArgs.add(dateMin);
            selectionArgs.add(dateMax);
        }

        // Add distributor filter
        if (!"Select an option...".equals(distributor))
        {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append("distributor = ?");
            selectionArgs.add(distributor);
        }

        // Log query details
        System.out.println("Query Selection: " + selection);
        System.out.println("Selection Args: " + selectionArgs);

        // Execute query
        Cursor cursor = db.query(
                TABLE_LISTINGS,
                null,
                selection.toString(),
                selectionArgs.toArray(new String[0]),
                null,
                null,
                "datePurchased ASC"
        );

        // Handle query results
        if (cursor != null && cursor.getCount() > 0)
        {
            System.out.println("Query executed. Results: " + cursor.getCount());
            while (cursor.moveToNext())
            {
                int listingId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String dbDistributor = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DISTRIBUTOR));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                int damagedCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DAMAGED));

                // Log each result
                System.out.println("Listing Found - ID: " + listingId + ", Distributor: " + dbDistributor + ", Date: " + date);

                results.add(new Listing(listingId, dbDistributor, date, damagedCount));
            }
            cursor.close();
        }
        else
        {
            System.out.println("No results found for the query.");
        }

        return results;
    }

    private String convertDateToDatabaseFormat(String date)
    {
        if (date == null || !date.matches("\\d{2}/\\d{2}/\\d{4}"))
        {
            System.out.println("Invalid date format: " + date);
            return date; // Return as-is if invalid or null
        }
        String[] parts = date.split("/");
        String convertedDate = parts[2] + "-" + parts[0] + "-" + parts[1]; // Convert MM/DD/YYYY to YYYY-MM-DD
        System.out.println("Converted Date: " + convertedDate);
        return convertedDate;
    }
}
