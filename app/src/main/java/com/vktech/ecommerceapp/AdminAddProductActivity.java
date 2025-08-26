package com.vktech.ecommerceapp;

import android.content.Intent; // Keep for now in case other intents are added later
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class AdminAddProductActivity extends AppCompatActivity {

    private EditText editTextName, editTextPrice, editTextDescription;
    private Button buttonAddProduct;
    private ArrayList<Product> productList;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

        editTextName = findViewById(R.id.editTextProductName);
        editTextPrice = findViewById(R.id.editTextProductPrice);
        editTextDescription = findViewById(R.id.editTextProductDescription);
        buttonAddProduct = findViewById(R.id.buttonAddProduct);

        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        productList = getProductList();

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String priceStr = editTextPrice.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();

                if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
                    Toast.makeText(AdminAddProductActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double price = Double.parseDouble(priceStr);
                Product product = new Product(name, price, description);
                productList.add(product);
                saveProductList(productList);
                Toast.makeText(AdminAddProductActivity.this, "Product '" + name + "' added successfully!", Toast.LENGTH_SHORT).show(); // More specific message

                // Clear the input fields instead of navigating
                editTextName.setText("");
                editTextPrice.setText("");
                editTextDescription.setText("");

                // No Intent to CustomerViewActivity here
                // Intent intent = new Intent(AdminAddProductActivity.this, CustomerViewActivity.class);
                // startActivity(intent);
                // finish(); // No need to finish if staying on the same activity
            }
        });
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
}