package com.bhjbestkalyangame.realapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the button for One Day Game
        Button oneDayGameButton = findViewById(R.id.button3);

        // Set click listener for the One Day Game button
        oneDayGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start PurchaseActivity when One Day Game button is clicked
                Intent intent = new Intent(MainActivity.this, PurchaseActivity.class);
                intent.putExtra("product_type", "one_day");  // or "vip_membership"
                startActivity(intent);
            }
        });

        // Find the button for VIP Membership
        Button vipMembershipBtn = findViewById(R.id.button4);

        // Set click listener for the VIP Membership button
        vipMembershipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start PurchaseActivity when VIP Membership button is clicked
                Intent intent = new Intent(MainActivity.this, PurchaseActivity.class);
                intent.putExtra("product_type", "vip_membership");  // or "vip_membership"
                startActivity(intent);
            }
        });
        Button SubManuOpen = findViewById(R.id.button1);

        // Set click listener for the VIP Membership button
        SubManuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start PurchaseActivity when VIP Membership button is clicked
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                startActivity(intent);
            }
        });

    }
}
