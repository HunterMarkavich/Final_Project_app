package com.example.finalprojectapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder>
{
    private Context context;
    private List<Listing> listings;
    private boolean isSalespersonView;
    private boolean isCustomerServiceView;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener
    {
        void onItemClick(Listing listing);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.onItemClickListener = listener;
    }

    public ListingAdapter(Context context, List<Listing> listings, boolean isSalespersonView, boolean isCustomerServiceView)
    {
        this.context = context;
        this.listings = listings;
        this.isSalespersonView = isSalespersonView;
        this.isCustomerServiceView = isCustomerServiceView;
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        if (isSalespersonView)
        {
            view = LayoutInflater.from(context).inflate(R.layout.item_salesperson_listing, parent, false);
        }
        else if (isCustomerServiceView)
        {
            view = LayoutInflater.from(context).inflate(R.layout.item_customer_service_listing, parent, false);
        }
        else
        {
            view = LayoutInflater.from(context).inflate(R.layout.item_listing, parent, false);
        }
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position)
    {
        Listing listing = listings.get(position);

        // Bind common fields
        holder.tvId.setText("ID: " + listing.getId());
        holder.tvDistributor.setText(listing.getDistributor());
        holder.tvDate.setText("Date: " + listing.getDatePurchased());

        // Bind Manager-specific fields
        if (holder.tvPrice != null)
        {
            holder.tvPrice.setText("Price: $" + listing.getPrice());
        }
        if (holder.tvProfit != null)
        {
            double profit = listing.getProfit();
            holder.tvProfit.setText(String.format("Profit: $%.2f", profit));

            // Set profit text color based on value
            if (profit >= 0)
            {
                holder.tvProfit.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark)); // Green for positive profit
            }
            else
            {
                holder.tvProfit.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark)); // Red for negative profit
            }
        }
        if (holder.tvPallets != null)
        {
            holder.tvPallets.setText(listing.getNumPallets() + " pallets");
        }
        if (holder.tvDamaged != null)
        {
            holder.tvDamaged.setText("Damaged: " + listing.getDamagedCount());
        }
        if (holder.tvEarnings != null)
        {
            holder.tvEarnings.setText("Earnings: $" + listing.getEarnings());
        }
        if (holder.tvPay != null)
        {
            holder.tvPay.setText(String.format("Pay: $%.2f", listing.getEarnings() * 0.1));
        }

        // Set click listener for the item
        holder.itemView.setOnClickListener(v ->
        {
            if (onItemClickListener != null)
            {
                onItemClickListener.onItemClick(listing);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public void updateListings(List<Listing> newListings)
    {
        this.listings = newListings;
        notifyDataSetChanged();
    }

    static class ListingViewHolder extends RecyclerView.ViewHolder
    {
        // Common views
        TextView tvId, tvDistributor, tvDate;

        // Manager-specific views
        TextView tvPrice, tvProfit, tvPallets, tvDamaged, tvEarnings, tvPay;

        public ListingViewHolder(@NonNull View itemView)
        {
            super(itemView);

            // Common views
            tvId = itemView.findViewById(R.id.tv_id);
            tvDistributor = itemView.findViewById(R.id.tv_distributor);
            tvDate = itemView.findViewById(R.id.tv_date);

            // Manager-specific views
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvProfit = itemView.findViewById(R.id.tv_profit);
            tvPallets = itemView.findViewById(R.id.tv_pallets);
            tvDamaged = itemView.findViewById(R.id.tv_damaged);
            tvEarnings = itemView.findViewById(R.id.tv_earnings);
            tvPay = itemView.findViewById(R.id.tv_pay);
        }
    }
}
