package com.vktech.ecommerceapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    LinearLayout cartLayout;
    TextView totalAmountText;
    Button placeOrderBtn;
    SharedPreferences sharedPreferences; // Renamed to be consistent
    private ArrayList<Product> cartList; // To hold the list of products in the cart

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartLayout = findViewById(R.id.cartItemsLayout);
        totalAmountText = findViewById(R.id.totalAmount);
        placeOrderBtn = findViewById(R.id.placeOrderBtn);

        // Initialize SharedPreferences using the consistent PREF_NAME from Constants
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);

        loadCartItems();

        placeOrderBtn.setOnClickListener(v -> {
            if (cartList.isEmpty()) {
                Toast.makeText(this, "Your cart is empty. Please add items before placing an order.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Order is Placed Successfully. Thank you for using our app!", Toast.LENGTH_LONG).show();
                // Clear the cart after placing the order
                sharedPreferences.edit().remove(Constants.KEY_CART).apply();
                loadCartItems(); // Reload the UI to show the now empty cart
            }
        });
    }

    private void loadCartItems() {
        cartLayout.removeAllViews(); // Clear existing views before loading
        cartList = getCartList(); // Get the current cart list

        double total = 0;

        if (cartList.isEmpty()) {
            TextView emptyCartMessage = new TextView(this);
            emptyCartMessage.setText("Your cart is empty.");
            emptyCartMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
            emptyCartMessage.setPadding(0, 32, 0, 0);
            cartLayout.addView(emptyCartMessage);
        } else {
            for (int i = 0; i < cartList.size(); i++) {
                Product product = cartList.get(i);
                total += product.getPrice(); // Accumulate total price

                // Inflate the item_product layout for each cart item
                View itemView = getLayoutInflater().inflate(R.layout.item_product, null);
                TextView nameText = itemView.findViewById(R.id.productName);
                TextView priceText = itemView.findViewById(R.id.productPrice);
                TextView descriptionText = itemView.findViewById(R.id.productDesc);
                Button buyBtn = itemView.findViewById(R.id.buyBtn);

                // Set "Remove" text and distinct background for the button in cart
                buyBtn.setText("Remove");
                buyBtn.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark)); // Example red color

                nameText.setText(product.getName());
                priceText.setText("Price: ₹" + product.getPrice());
                descriptionText.setText(product.getDescription());

                final int itemIndex = i; // Use final variable for click listener
                buyBtn.setOnClickListener(v -> {
                    removeCartItem(itemIndex);
                    Toast.makeText(CartActivity.this, product.getName() + " removed from cart.", Toast.LENGTH_SHORT).show();
                });

                cartLayout.addView(itemView);
            }
        }

        totalAmountText.setText("Total Amount: ₹" + String.format("%.2f", total)); // Format total to 2 decimal places
    }

    // Helper method to retrieve the cart list from SharedPreferences
    private ArrayList<Product> getCartList() {
        Gson gson = new Gson();
        // Use the consistent KEY_CART from Constants
        String json = sharedPreferences.getString(Constants.KEY_CART, null);
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        return json == null ? new ArrayList<>() : gson.fromJson(json, type);
    }

    // Helper method to save the cart list to SharedPreferences
    private void saveCartList(ArrayList<Product> list) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        // Use the consistent KEY_CART from Constants
        editor.putString(Constants.KEY_CART, json);
        editor.apply();
    }


    private void removeCartItem(int index) {
        if (cartList != null && index >= 0 && index < cartList.size()) {
            cartList.remove(index); // Remove from the ArrayList
            saveCartList(cartList); // Save the updated list back to SharedPreferences
            loadCartItems(); // Reload the UI to reflect changes
        }
    }
}