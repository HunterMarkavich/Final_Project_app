package com.example.finalprojectapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReturnsAdapter extends RecyclerView.Adapter<ReturnsAdapter.ReturnViewHolder>
{
    private Context context;
    private List<ReturnItem> returns;

    public ReturnsAdapter(Context context, List<ReturnItem> returns)
    {
        this.context = context;
        this.returns = returns;
    }

    @NonNull
    @Override
    public ReturnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_return, parent, false);
        return new ReturnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReturnViewHolder holder, int position)
    {
        ReturnItem returnItem = returns.get(position);

        // Debugging - Log the item being bound
        System.out.println("Binding Return - Position: " + position + ", Date: " + returnItem.getReturnDate());

        // Alternate background colors
        if (position % 2 == 0)
        {
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        } else
        {
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
        }

        // Bind data to views
        holder.tvReturnDate.setText("Return Date: " + returnItem.getReturnDate());
        holder.tvRefundAmount.setText("Refunded: $" + returnItem.getRefundAmount());
        holder.tvReason.setText("Reason: " + returnItem.getReason());
    }

    @Override
    public int getItemCount() {
        return returns.size();
    }

    public void updateReturns(List<ReturnItem> newReturns)
    {
        this.returns = newReturns;
        notifyDataSetChanged();
    }

    static class ReturnViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvReturnDate, tvRefundAmount, tvReason;

        public ReturnViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tvReturnDate = itemView.findViewById(R.id.tv_return_date);
            tvRefundAmount = itemView.findViewById(R.id.tv_refund_amount);
            tvReason = itemView.findViewById(R.id.tv_reason);
        }
    }
}
