package com.bhjbestkalyangame.realapplication;

import android.os.Bundle;
import android.util.Log;
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
    private TextView[] resultTextViews = new TextView[4]; // Array for 4 TextViews
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);

        // Initialize TextViews
        gameTitleTextView = findViewById(R.id.gameTitleTextView);
        dataBaseStatusTextView = findViewById(R.id.dataBaseStatus);
        resultTextViews[0] = findViewById(R.id.resultTextView1);
        resultTextViews[1] = findViewById(R.id.resultTextView2);
        resultTextViews[2] = findViewById(R.id.resultTextView3);
        resultTextViews[3] = findViewById(R.id.resultTextView4);

        // Get the game type passed from the previous activity
        String gameType = getIntent().getStringExtra("gameType");
        gameTitleTextView.setText(gameType != null ? gameType : "Game Type Not Specified");

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference(gameType);
        fetchGameData();
    }

    private void fetchGameData() {
        dataBaseStatusTextView.setText("Connecting to Firebase...");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataBaseStatusTextView.setText("Fetching data...");
                int index = 0;

                // Clear previous data
                for (TextView resultTextView : resultTextViews) {
                    resultTextView.setText("");
                }

                // Iterate through the data snapshot and append data to the result TextViews
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (index >= resultTextViews.length) break; // Limit to 4 results

                    Object value = data.getValue();
                    String resultText = "Super No " + (index + 1) + ": " + (value instanceof String ? value : String.valueOf(value));
                    resultTextViews[index].setText(resultText);
                    index++;
                }

                dataBaseStatusTextView.setText("Data fetched successfully.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dataBaseStatusTextView.setText("Failed to load data.");
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }
}
