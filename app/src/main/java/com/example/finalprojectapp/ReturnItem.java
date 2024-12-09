package com.example.finalprojectapp;

public class ReturnItem
{
    private int returnId;
    private int listingId;
    private String returnDate;
    private double refundAmount;
    private String reason;

    public ReturnItem(int returnId, int listingId, String returnDate, double refundAmount, String reason)
    {
        this.returnId = returnId;
        this.listingId = listingId;
        this.returnDate = returnDate;
        this.refundAmount = refundAmount;
        this.reason = reason;
    }

    public int getReturnId() {
        return returnId;
    }

    public int getListingId() {
        return listingId;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public String getReason() {
        return reason;
    }
}
