package com.example.learnquick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef; // Reference to Firebase Realtime Database
    private Button logoutButton, navigateToHomeButton;
    private TextView profileUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Ensure this points to your profile activity XML

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("users");

        logoutButton = findViewById(R.id.logoutButton);
        navigateToHomeButton = findViewById(R.id.navigateToHomeButton);
        profileUsername = findViewById(R.id.profileUsername);

        // Fetch the username from the database and display it
        String currentUserId = mAuth.getCurrentUser().getUid();
        databaseRef.child(currentUserId).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.getValue(String.class);
                    profileUsername.setText("Welcome, " + username);
                } else {
                    profileUsername.setText("Welcome, User");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load username", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigate to HomeActivity when the "Go to Home" button is clicked
        navigateToHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Close ProfileActivity
        });

        // Logout the user when the "Logout" button is clicked
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut(); // Sign out the user
            Toast.makeText(ProfileActivity.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();

            // Navigate to LoginActivity after logout
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close ProfileActivity
        });
    }
}