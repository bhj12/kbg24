package com.bhjbestkalyangame.realapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.PurchasesResponseListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Keys for SharedPreferences
    private static final String PREFS_NAME = "PurchasePrefs";
    private static final String KEY_ONE_DAY_GAME = "hasPurchasedOneDayGame";
    private static final String KEY_VIP_MEMBERSHIP = "hasPurchasedVIPMembership";

    // Billing Client
    private BillingClient billingClient;

    // Variables to store whether the user has purchased either One Day Game or VIP Membership
    private boolean hasPurchasedOneDayGame = false;
    private boolean hasPurchasedVIPMembership = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize billing client
        setupBillingClient();

        // Find buttons
        Button oneDayGameButton = findViewById(R.id.button3);
        Button vipMembershipBtn = findViewById(R.id.button4);
        Button subMenuOpen = findViewById(R.id.button1);

        // One Day Game button click listener
        oneDayGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PurchaseActivity.class);
            intent.putExtra("product_type", "one_day");
            startActivity(intent);
        });

        // VIP Membership button click listener
        vipMembershipBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PurchaseActivity.class);
            intent.putExtra("product_type", "vip_membership");
            startActivity(intent);
        });

        // SubMenu button click listener
        subMenuOpen.setOnClickListener(v -> {
            // Check if the user has purchased either the One Day Game or VIP Membership
            if (hasPurchasedOneDayGame || hasPurchasedVIPMembership) {
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Please purchase One Day Game or VIP Membership first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload purchase status every time the activity becomes visible
        loadPurchaseStatus();
    }

    // Method to load purchase status from SharedPreferences
    private void loadPurchaseStatus() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        hasPurchasedOneDayGame = prefs.getBoolean(KEY_ONE_DAY_GAME, false);
        hasPurchasedVIPMembership = prefs.getBoolean(KEY_VIP_MEMBERSHIP, false);
    }

    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .setListener((billingResult, purchases) -> {
                    // Handle purchase updates here
                    loadPurchaseStatus(); // Reload status after a purchase
                })
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready, query purchases
                    queryPurchases();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to Google Play
            }
        });
    }

    private void queryPurchases() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                for (Purchase purchase : purchases) {
                    // Check for specific purchase types
                    String sku = purchase.getSkus().get(0); // Get the SKU
                    if ("one_day_game_sku".equals(sku)) {
                        hasPurchasedOneDayGame = true;
                    } else if ("vip_membership_sku".equals(sku)) {
                        hasPurchasedVIPMembership = true;
                    }
                }
                updatePurchaseStatus();
            }
        });
    }

    private void updatePurchaseStatus() {
        // Optionally save purchase status in SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_ONE_DAY_GAME, hasPurchasedOneDayGame);
        editor.putBoolean(KEY_VIP_MEMBERSHIP, hasPurchasedVIPMembership);
        editor.apply();
    }}