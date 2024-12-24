package com.example.learnquick;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef; // Firebase Realtime Database reference
    private EditText emailEditText, usernameEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("users");

        // Find views for email, username, password, confirm password, and sign-up button
        emailEditText = findViewById(R.id.signupEmail); // Email field
        usernameEditText = findViewById(R.id.signupUsername); // Username field
        passwordEditText = findViewById(R.id.signupPassword); // Password field
        confirmPasswordEditText = findViewById(R.id.signupConfirmPassword); // Confirm Password field
        signUpButton = findViewById(R.id.signupButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                // Validate inputs
                if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate email format
                if (!isValidEmail(email)) {
                    Toast.makeText(SignUpActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if passwords match
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate password length
                if (password.length() < 6) {
                    Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a user in Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Successfully created user, now store user data in Realtime Database
                                String userId = mAuth.getCurrentUser().getUid();
                                User user = new User(email, username); // Store email and username

                                // Save user data to Firebase Realtime Database
                                databaseRef.child(userId).setValue(user)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                            finish(); // Navigate back to login
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("SignUpActivity", "Failed to save user data: " + e.getMessage());
                                            Toast.makeText(SignUpActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Sign Up Failed";
                                Log.e("SignUpActivity", "Sign-up failed: " + errorMessage);
                                Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    // Helper method to validate email format
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.contains(".com");
    }

    // User class for Firebase storage
    public static class User {
        public String email;
        public String username;

        // Default constructor for Firebase
        public User() {
        }

        public User(String email, String username) {
            this.email = email;
            this.username = username;
        }
    }
}