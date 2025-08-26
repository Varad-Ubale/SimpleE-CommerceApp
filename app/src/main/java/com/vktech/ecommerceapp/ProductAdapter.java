package com.vktech.ecommerceapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView; // Make sure ImageView is imported
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private ArrayList<Product> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onBuyClick(Product product);
        void onProductLongClick(int position); // New method for long press
    }

    public ProductAdapter(ArrayList<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    // New method to update product list and notify adapter
    public void updateProductList(ArrayList<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged(); // This is a simple way to refresh the whole list.
        // For better performance on large lists, consider DiffUtil.
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view, listener, productList); // Pass listener and productList to ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.textViewName.setText(product.getName());
        holder.textViewPrice.setText("₹" + String.format("%.2f", product.getPrice())); // Format price
        holder.textViewDescription.setText(product.getDescription());

        // Handle the buy button click
        holder.buttonBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBuyClick(product);
            }
        });
        // The long click listener is now handled inside the ViewHolder constructor
        // or can be set here if the logic is simple enough.
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewPrice, textViewDescription;
        Button buttonBuy;
        ImageView productImage; // Reference to ImageView if you plan to use it

        public ProductViewHolder(@NonNull View itemView, final OnProductClickListener listener, final ArrayList<Product> productList) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.productName);
            textViewPrice = itemView.findViewById(R.id.productPrice);
            textViewDescription = itemView.findViewById(R.id.productDesc);
            buttonBuy = itemView.findViewById(R.id.buyBtn);
            productImage = itemView.findViewById(R.id.productImage); // Initialize ImageView

            // Set OnLongClickListener for the entire item view
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition(); // Get the current position of the item
                        if (position != RecyclerView.NO_POSITION) { // Check if position is valid
                            listener.onProductLongClick(position);
                            return true; // Consume the long click event
                        }
                    }
                    return false;
                }
            });
        }
    }
}