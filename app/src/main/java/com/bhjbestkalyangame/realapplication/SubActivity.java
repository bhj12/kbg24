package com.bhjbestkalyangame.realapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SubActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_layout);

        Button singleKalyanButton = findViewById(R.id.singleKalyanButton);
        Button jodiKalyanButton = findViewById(R.id.jodiKalyanButton);
        Button panelKalyanButton = findViewById(R.id.panelKalyanButton);

        // Handle Single Kalyan Game button click
        singleKalyanButton.setOnClickListener(v -> {
            Intent intent = new Intent(SubActivity.this, ResultActivity.class);
            intent.putExtra("gameType", "SingleKalyanGame");
            startActivity(intent);
        });

        // Handle Jodi Kalyan Game button click
        jodiKalyanButton.setOnClickListener(v -> {
            Intent intent = new Intent(SubActivity.this, ResultActivity.class);
            intent.putExtra("gameType", "JodiKalyanGame");
            startActivity(intent);
        });

        // Handle Panel Kalyan Game button click
        panelKalyanButton.setOnClickListener(v -> {
            Intent intent = new Intent(SubActivity.this, ResultActivity.class);
            intent.putExtra("gameType", "PanelKalyanGame");
            startActivity(intent);
        });
    }
}
