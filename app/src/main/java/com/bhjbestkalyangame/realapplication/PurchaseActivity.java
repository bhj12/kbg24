package com.bhjbestkalyangame.realapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.billingclient.api.*;

import java.util.ArrayList;
import java.util.List;

public class PurchaseActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private BillingClient billingClient;
    private String oneDayGameProductId = "1_day_ticket";
    private String vipMembershipProductId = "onedaygame";
    private String selectedProductId; // Holds the product ID for the current product
    private ProductDetails selectedProductDetails; // Holds the product details for the selected product

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase_layout);

        // Find views
        TextView productTypeTextView = findViewById(R.id.productTypeTextView);
        Button buyButton = findViewById(R.id.buyButton);

        // Get product type from intent
        String productType = getIntent().getStringExtra("product_type");

        // Set up the TextView and determine the product type
        if ("one_day".equals(productType)) {
            productTypeTextView.setText("One Day Game");
            buyButton.setText("Buy One Day Game");
            selectedProductId = oneDayGameProductId;
        } else if ("vip_membership".equals(productType)) {
            productTypeTextView.setText("VIP Membership");
            buyButton.setText("Buy Weekly Game");
            selectedProductId = vipMembershipProductId;
        }

        // Initialize BillingClient
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(this) // Set listener for purchase updates
                .build();

        // Start the connection to Google Play
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Billing setup successful, query the product details
                    queryProductDetails();
                } else {
                    Toast.makeText(PurchaseActivity.this, "Billing setup failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(PurchaseActivity.this, "Billing service disconnected", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Buy Button click
        buyButton.setOnClickListener(v -> {
            if (selectedProductDetails != null) {
                initiatePurchase(selectedProductDetails);
            } else {
                Toast.makeText(PurchaseActivity.this, "Product details not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Query product details
    private void queryProductDetails() {
        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(selectedProductId)
                .setProductType(BillingClient.ProductType.INAPP) // Or BillingClient.ProductType.SUBS for subscriptions
                .build());

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !productDetailsList.isEmpty()) {
                selectedProductDetails = productDetailsList.get(0); // Get the details for the selected product
                Toast.makeText(PurchaseActivity.this, "Product details available", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PurchaseActivity.this, "Product details not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Initiate purchase
    private void initiatePurchase(ProductDetails productDetails) {
        List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<>();
        productDetailsParamsList.add(BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build());

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        BillingResult billingResult = billingClient.launchBillingFlow(this, billingFlowParams);
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Toast.makeText(this, "Error starting purchase: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                Toast.makeText(this, "Purchase successful: " + purchase.getProducts(), Toast.LENGTH_SHORT).show();
                // Start SubActivity upon successful purchase
                Intent intent = new Intent(PurchaseActivity.this, SubActivity.class);
                startActivity(intent);
                finish(); // Optional: Finish this activity if you want to close it
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(this, "Purchase canceled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
