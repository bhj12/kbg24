package com.bhjbestkalyangame.realapplication;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResultActivity extends AppCompatActivity {
    private static final String TAG = "ResultActivity";
    private TextView gameTitleTextView;
    private TextView dataBaseStatusTextView;
    private GridLayout resultsGridLayout; // GridLayout to hold results
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);

        // Initialize TextViews
        gameTitleTextView = findViewById(R.id.gameTitleTextView);
        dataBaseStatusTextView = findViewById(R.id.dataBaseStatus);
        resultsGridLayout = findViewById(R.id.resultsGridLayout);

        // Get the game type passed from the previous activity
        String gameType = getIntent().getStringExtra("gameType");
        gameTitleTextView.setText(gameType != null ? gameType : "Game Type Not Specified");

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference(gameType);
        fetchGameData();
    }

    private void fetchGameData() {
        dataBaseStatusTextView.setText("loading...");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataBaseStatusTextView.setText("loading...");

                // Clear previous views
                resultsGridLayout.removeAllViews();

                int index = 0;

                // Iterate through the data snapshot and create TextViews
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Object value = data.getValue();
                    String dataValue = value instanceof String ? (String) value : String.valueOf(value);

                    // Create a vertical LinearLayout to hold both label and data
                    LinearLayout resultLayout = new LinearLayout(ResultActivity.this);
                    resultLayout.setOrientation(LinearLayout.VERTICAL);
                    resultLayout.setPadding(8, 8, 8, 8);
                    resultLayout.setGravity(Gravity.CENTER); // Center content
                    resultLayout.setBackgroundResource(R.drawable.border_background); // Set background

                    // Create the "Super No" TextView
                    TextView superNoTextView = new TextView(ResultActivity.this);
                    superNoTextView.setText(String.format("Super No %d", index + 1));
                    superNoTextView.setTextSize(16);
                    superNoTextView.setTypeface(null, Typeface.BOLD); // Set text to bold
                    superNoTextView.setPadding(0, 0, 0, 4); // Padding for spacing
                    superNoTextView.setGravity(Gravity.CENTER); // Center the label
                    superNoTextView.setTextColor(Color.parseColor("#BE7273")); // Gold color

                    // Create the data TextView
                    TextView dataTextView = new TextView(ResultActivity.this);
                    dataTextView.setText(dataValue);
                    dataTextView.setTextSize(35); // Size 20 for data
                    dataTextView.setTypeface(null, Typeface.BOLD); // Set text to bold
                    dataTextView.setGravity(Gravity.CENTER); // Center the data text
                    dataTextView.setTextColor(Color.parseColor("#5C0002"));
                    //dataTextView.setTextColor(Color.YELLOW); // Yellow color

                    // Add both TextViews to the LinearLayout
                    resultLayout.addView(superNoTextView);
                    resultLayout.addView(dataTextView);

                    // Set layout parameters for the result layout
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                            GridLayout.spec(index / 3),
                            GridLayout.spec(index % 3));
                    params.setMargins(16, 16, 16, 16);
                    resultLayout.setLayoutParams(params);

                    // Add the result layout to the GridLayout
                    resultsGridLayout.addView(resultLayout);
                    index++;
                }
                dataBaseStatusTextView.setTextSize(10);
                dataBaseStatusTextView.setText("");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dataBaseStatusTextView.setText("Failed to load data.");
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }
}