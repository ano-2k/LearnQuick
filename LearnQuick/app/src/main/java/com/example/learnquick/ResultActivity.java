package com.example.learnquick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private TextView scoreTextView;
    private RecyclerView recyclerView;
    private Button backButton;

    // Firebase references
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        scoreTextView = findViewById(R.id.scoreTextView);
        recyclerView = findViewById(R.id.recyclerView);
        backButton = findViewById(R.id.backButton);

        // Retrieve data from intent
        int score = getIntent().getIntExtra("score", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        ArrayList<String> correctAnswers = getIntent().getStringArrayListExtra("correctAnswers");
        ArrayList<String> userAnswers = getIntent().getStringArrayListExtra("userAnswers");

        // Calculate and display the score and percentage
        int percentage = (score * 100) / totalQuestions;
        scoreTextView.setText("Percentage Correct: " + percentage + "%\nCorrect Answers: " + score + "/" + totalQuestions);

        // Set up the RecyclerView for the result table
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ResultAdapter(correctAnswers, userAnswers, correctAnswers));

        // Save the result to Firebase
        saveResultToDatabase(score, totalQuestions, percentage, correctAnswers, userAnswers);

        // Back button functionality
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Close the ResultActivity
        });
    }

    private void saveResultToDatabase(int score, int totalQuestions, int percentage, ArrayList<String> correctAnswers, ArrayList<String> userAnswers) {
        String userId = auth.getCurrentUser().getUid(); // Get current user ID
        String resultId = databaseReference.child("results").push().getKey(); // Generate unique ID for the result

        if (resultId != null) {
            // Create a Result object
            QuizResult result = new QuizResult(score, totalQuestions, percentage, correctAnswers, userAnswers);

            // Store the result in the Firebase Realtime Database
            databaseReference.child("results").child(userId).child(resultId).setValue(result)
                    .addOnSuccessListener(aVoid -> {
                        // Optional: Notify user of successful save
                    })
                    .addOnFailureListener(e -> {
                        // Optional: Handle any errors
                    });
        }
    }

    // Define the QuizResult model class
    public static class QuizResult {
        public int score;
        public int totalQuestions;
        public int percentage;
        public ArrayList<String> correctAnswers;
        public ArrayList<String> userAnswers;

        public QuizResult() {
            // Default constructor required for calls to DataSnapshot.getValue(QuizResult.class)
        }

        public QuizResult(int score, int totalQuestions, int percentage, ArrayList<String> correctAnswers, ArrayList<String> userAnswers) {
            this.score = score;
            this.totalQuestions = totalQuestions;
            this.percentage = percentage;
            this.correctAnswers = correctAnswers;
            this.userAnswers = userAnswers;
        }
    }
}