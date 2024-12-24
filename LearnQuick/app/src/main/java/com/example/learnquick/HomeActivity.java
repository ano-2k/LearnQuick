package com.example.learnquick;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    private EditText topicEditText;
    private Button generateQuestionsButton, submitButton, toggleThemeButton;
    private LinearLayout questionsLayout, centerContentLayout;
    private ScrollView scrollView;
    private Map<Integer, String> correctAnswers = new HashMap<>();
    private Map<Integer, String> userAnswers = new HashMap<>();
    private ArrayList<String> questionsList = new ArrayList<>(); // to store question text for reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize UI elements
        topicEditText = findViewById(R.id.topicEditText);
        generateQuestionsButton = findViewById(R.id.generateQuestionsButton);
        questionsLayout = findViewById(R.id.questionsLayout);
        submitButton = findViewById(R.id.submitButton);
        centerContentLayout = findViewById(R.id.centerContentLayout);
        scrollView = findViewById(R.id.questionsScrollView);

        

        // Generate questions
        generateQuestionsButton.setOnClickListener(v -> {
            String topic = topicEditText.getText().toString().trim();
            if (!topic.isEmpty()) {
                fetchQuestions(topic);
            } else {
                Toast.makeText(HomeActivity.this, "Please enter a topic", Toast.LENGTH_SHORT).show();
            }
        });

        // Submit answers
        submitButton.setOnClickListener(v -> {
            if (!userAnswers.isEmpty()) {
                int score = calculateScore();
                
                navigateToResultActivity(score);
            } else {
                Toast.makeText(HomeActivity.this, "Please answer the questions first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int calculateScore() {
        int score = 0;
        for (int i : correctAnswers.keySet()) {
            if (correctAnswers.get(i).equals(userAnswers.get(i))) {
                score++;
            }
        }
        return score;
    }
    

    private void navigateToResultActivity(int score) {
        Intent intent = new Intent(HomeActivity.this, ResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("totalQuestions", correctAnswers.size());

        ArrayList<String> correctAnswersList = new ArrayList<>(correctAnswers.values());
        ArrayList<String> userAnswersList = new ArrayList<>(userAnswers.values());

        intent.putStringArrayListExtra("correctAnswers", correctAnswersList);
        intent.putStringArrayListExtra("userAnswers", userAnswersList);

        startActivity(intent);
    }

    private void fetchQuestions(String topic) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://opentdb.com/api.php?amount=5&type=multiple&category=" + getCategoryId(topic);

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    handleApiResponse(response.body().string());
                } else {
                    runOnUiThread(() -> Toast.makeText(HomeActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(HomeActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void handleApiResponse(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray results = jsonResponse.getJSONArray("results");
            generateQuestions(results);
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show());
        }
    }

    private void generateQuestions(JSONArray results) {
        runOnUiThread(() -> {
            questionsLayout.removeAllViews();
            correctAnswers.clear();
            userAnswers.clear();

            try {
                for (int i = 0; i < results.length(); i++) {
                    JSONObject questionData = results.getJSONObject(i);
                    String question = questionData.getString("question");
                    String correctAnswer = questionData.getString("correct_answer");
                    JSONArray incorrectAnswers = questionData.getJSONArray("incorrect_answers");

                    ArrayList<String> options = new ArrayList<>();
                    options.add(correctAnswer);
                    for (int j = 0; j < incorrectAnswers.length(); j++) {
                        options.add(incorrectAnswers.getString(j));
                    }

                    Collections.shuffle(options);

                    correctAnswers.put(i, correctAnswer);
                    questionsList.add(question);

                    TextView questionView = new TextView(this);
                    questionView.setText((i + 1) + ". " + question);
                    questionView.setPadding(8, 16, 8, 8);
                    questionsLayout.addView(questionView);

                    RadioGroup radioGroup = new RadioGroup(this);
                    char optionLabel = 'A';
                    for (String option : options) {
                        RadioButton optionButton = new RadioButton(this);
                        optionButton.setText(optionLabel + ") " + option);
                        optionButton.setPadding(8, 8, 8, 8);
                        optionButton.setTextSize(14);
                        int finalI = i;
                        optionButton.setOnClickListener(v -> userAnswers.put(finalI, option));
                        radioGroup.addView(optionButton);
                        optionLabel++;
                    }

                    questionsLayout.addView(radioGroup);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error generating questions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getCategoryId(String topic) {
        topic = topic.toLowerCase();
        if (topic.contains("general")) return "9";
        if (topic.contains("science")) return "17";
        if (topic.contains("sports")) return "21";
        if (topic.contains("history")) return "23";
        if (topic.contains("music")) return "12";
        if (topic.contains("geography")) return "22";
        if (topic.contains("computers") || topic.contains("tech")) return "18";
        if (topic.contains("books")) return "10";
        if (topic.contains("movies") || topic.contains("films")) return "11";
        if (topic.contains("mythology")) return "20";
        return "9";
    }

    // Helper method to set color of question text
    private void setQuestionsTextColor(int color) {
        for (int i = 0; i < questionsLayout.getChildCount(); i++) {
            if (questionsLayout.getChildAt(i) instanceof TextView) {
                ((TextView) questionsLayout.getChildAt(i)).setTextColor(color);
            }
        }
    }
}