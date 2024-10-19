package com.bhjbestkalyangame.realapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Keys for SharedPreferences
    private static final String PREFS_NAME = "PurchasePrefs";
    private static final String KEY_ONE_DAY_GAME = "hasPurchasedOneDayGame";
    private static final String KEY_VIP_MEMBERSHIP = "hasPurchasedVIPMembership";

    // Variables to store whether the user has purchased either One Day Game or VIP Membership
    private boolean hasPurchasedOneDayGame = false;
    private boolean hasPurchasedVIPMembership = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
