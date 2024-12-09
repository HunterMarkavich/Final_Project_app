package com.example.finalprojectapp;

public class Listing
{
    private int id;
    private String distributor;
    private String datePurchased;
    private int numPallets;
    private int damagedCount;
    private double price;
    private double earnings;
    private double salespersonPay;
    private double profit;

    // Full constructor
    public Listing(int id, String distributor, String datePurchased, int numPallets, int damagedCount, double price, double earnings, double salespersonPay, double profit)
    {
        this.id = id;
        this.distributor = distributor;
        this.datePurchased = datePurchased;
        this.numPallets = numPallets;
        this.damagedCount = damagedCount;
        this.price = price;
        this.earnings = earnings;
        this.salespersonPay = salespersonPay;
        this.profit = profit;
    }

    // Simplified constructor for Customer Service use case
    public Listing(int id, String distributor, String datePurchased, int damagedCount)
    {
        this.id = id;
        this.distributor = distributor;
        this.datePurchased = datePurchased;
        this.damagedCount = damagedCount;

        // Set default values for unused fields
        this.numPallets = 0;
        this.price = 0.0;
        this.earnings = 0.0;
        this.salespersonPay = 0.0;
        this.profit = 0.0;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDistributor() {
        return distributor;
    }

    public String getDatePurchased() {
        return datePurchased;
    }

    public int getNumPallets() {
        return numPallets;
    }

    public int getDamagedCount() {
        return damagedCount;
    }

    public double getPrice() {
        return price;
    }

    public double getEarnings() {
        return earnings;
    }

    public double getSalespersonPay() {
        return salespersonPay;
    }

    public double getProfit() {
        return profit;
    }
}
