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
    private static final String TAG = "ResultActivity"; // Tag for logging
    private TextView resultTextView;
    private TextView gameTitleTextView;
    private TextView dataBaseStatusTextView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);

        // Initialize TextViews
        resultTextView = findViewById(R.id.resultTextView);
        gameTitleTextView = findViewById(R.id.gameTitleTextView);
        dataBaseStatusTextView = findViewById(R.id.dataBaseStatus);

        // Get the game type passed from the previous activity
        String gameType = getIntent().getStringExtra("gameType");

        // Update the title TextView with the game type
        gameTitleTextView.setText(gameType != null ? gameType : "Game Type Not Specified");

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference(gameType);

        // Fetch data from Firebase
        fetchGameData(gameType);
    }

    private void fetchGameData(String gameType) {
        dataBaseStatusTextView.setText("Connecting to Firebase...");

        // Listen for data changes in the specified node
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataBaseStatusTextView.setText("Fetching data...");
                StringBuilder resultBuilder = new StringBuilder();

                // Iterate through the data snapshot and append data to the result
                int index = 1;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Object value = data.getValue(); // Get value as Object
                    if (value instanceof String) {
                        resultBuilder.append("Super No ").append(index).append(": ").append(value).append("\n");
                    } else if (value instanceof Long) {
                        resultBuilder.append("Super No ").append(index).append(": ").append(String.valueOf(value)).append("\n");
                    } else {
                        resultBuilder.append("Super No ").append(index).append(": Unknown type\n");
                    }
                    index++;
                }

                // Display the fetched data in the resultTextView
                resultTextView.setText(resultBuilder.toString());
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
