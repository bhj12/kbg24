package com.bhjbestkalyangame.realapplication;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private static final String PREFS_NAME = "PurchasePrefs";
    private static final String KEY_ONE_DAY_GAME = "hasPurchasedOneDayGame";
    private static final String KEY_VIP_MEMBERSHIP = "hasPurchasedVIPMembership";

    private String oneDayGameProductId = "1_day_ticket";
    private String vipMembershipProductId = "vip_membership";
    private String selectedProductId;  // The product ID for the selected product
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

        // Determine product type and set the display
        if ("one_day".equals(productType)) {
            productTypeTextView.setText("One Day Game");
            buyButton.setText("Buy One Day Game");
            selectedProductId = oneDayGameProductId;
        } else if ("vip_membership".equals(productType)) {
            productTypeTextView.setText("VIP Membership");
            buyButton.setText("Buy VIP Membership");
            selectedProductId = vipMembershipProductId;
        }

        // Initialize BillingClient
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(this)
                .build();

        // Connect to Google Play Billing
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Billing setup successful, check existing purchases
                    checkExistingPurchases();
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

    // Check if the user already owns any of the products
    private void checkExistingPurchases() {
        billingClient.queryPurchasesAsync(BillingClient.ProductType.INAPP, (billingResult, purchases) -> {
            boolean hasOneDayGame = false;
            boolean hasVipMembership = false;

            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                for (Purchase purchase : purchases) {
                    if (purchase.getProducts().contains(oneDayGameProductId)) {
                        hasOneDayGame = true;
                    } else if (purchase.getProducts().contains(vipMembershipProductId)) {
                        hasVipMembership = true;
                    }
                }
            }

            // Disable purchase if the user already owns the product
            if (hasOneDayGame && selectedProductId.equals(vipMembershipProductId)) {
                Toast.makeText(this, "You already purchased One Day Game. Cannot buy VIP Membership.", Toast.LENGTH_LONG).show();
            } else if (hasVipMembership && selectedProductId.equals(oneDayGameProductId)) {
                Toast.makeText(this, "You already purchased VIP Membership. Cannot buy One Day Game.", Toast.LENGTH_LONG).show();
            } else {
                queryProductDetails(); // Query product details if no conflicts
            }
        });
    }

    // Query product details from Google Play
    private void queryProductDetails() {
        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(selectedProductId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build());

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !productDetailsList.isEmpty()) {
                selectedProductDetails = productDetailsList.get(0); // Get the product details
            } else {
                Toast.makeText(PurchaseActivity.this, "Product details not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Initiate the purchase flow
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

    // Handle purchase updates
    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchaseSuccess(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(this, "Purchase canceled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Handle successful purchase
    private void handlePurchaseSuccess(Purchase purchase) {
        boolean oneDayGamePurchased = purchase.getProducts().contains(oneDayGameProductId);
        boolean vipMembershipPurchased = purchase.getProducts().contains(vipMembershipProductId);

        // Save the purchase status in SharedPreferences
        savePurchaseStatus(oneDayGamePurchased, vipMembershipPurchased);

        // Notify the user and navigate to SubActivity
        Toast.makeText(this, "Purchase successful!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(PurchaseActivity.this, SubActivity.class));
        finish();  // Close the PurchaseActivity
    }

    // Save purchase status to SharedPreferences
    private void savePurchaseStatus(boolean oneDayGamePurchased, boolean vipMembershipPurchased) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (oneDayGamePurchased) {
            editor.putBoolean(KEY_ONE_DAY_GAME, true);
        }
        if (vipMembershipPurchased) {
            editor.putBoolean(KEY_VIP_MEMBERSHIP, true);
        }
        editor.apply();
    }
}
