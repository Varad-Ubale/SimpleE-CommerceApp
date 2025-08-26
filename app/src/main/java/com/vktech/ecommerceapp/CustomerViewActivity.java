package com.vktech.ecommerceapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; // Import for AlertDialog
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class CustomerViewActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private RecyclerView recyclerView;
    private Button buttonShowCart;
    private ProductAdapter productAdapter;
    private ArrayList<Product> productList;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_view);

        recyclerView = findViewById(R.id.recyclerView);
        buttonShowCart = findViewById(R.id.btnShowCart);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        productList = getProductList(); // Load products when activity is created

        productAdapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(productAdapter);

        buttonShowCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerViewActivity.this, CartActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh product list in case admin added new products
        productList = getProductList();
        productAdapter.updateProductList(productList); // Notify adapter of data change
    }

    private ArrayList<Product> getProductList() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(Constants.KEY_PRODUCTS, null);
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        return json == null ? new ArrayList<>() : gson.fromJson(json, type);
    }

    private void saveProductList(ArrayList<Product> list) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(Constants.KEY_PRODUCTS, json);
        editor.apply();
    }

    @Override
    public void onBuyClick(Product product) {
        ArrayList<Product> cartList = getCartList();
        cartList.add(product);
        saveCartList(cartList);
        Toast.makeText(this, "Added to cart: " + product.getName(), Toast.LENGTH_SHORT).show();
        // Consider if you always want to jump to cart immediately or provide a choice
        // startActivity(new Intent(CustomerViewActivity.this, CartActivity.class));
    }

    // New method for long press delete
    @Override
    public void onProductLongClick(int position) {
        if (position >= 0 && position < productList.size()) {
            final Product productToDelete = productList.get(position);

            new AlertDialog.Builder(this)
                    .setTitle("Delete Product")
                    .setMessage("Are you sure you want to delete '" + productToDelete.getName() + "'?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        productList.remove(position); // Remove from list
                        saveProductList(productList);  // Save updated list
                        productAdapter.notifyItemRemoved(position); // Notify adapter
                        Toast.makeText(CustomerViewActivity.this, "Deleted: " + productToDelete.getName(), Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private ArrayList<Product> getCartList() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(Constants.KEY_CART, null);
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        return json == null ? new ArrayList<>() : gson.fromJson(json, type);
    }

    private void saveCartList(ArrayList<Product> list) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(Constants.KEY_CART, json);
        editor.apply();
    }
}