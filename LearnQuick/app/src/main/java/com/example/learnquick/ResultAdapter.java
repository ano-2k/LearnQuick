package com.example.learnquick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {
    private final List<String> questions;
    private final List<String> userAnswers;
    private final List<String> correctAnswers;

    public ResultAdapter(List<String> questions, List<String> userAnswers, List<String> correctAnswers) {
        this.questions = questions;
        this.userAnswers = userAnswers;
        this.correctAnswers = correctAnswers;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_result_adapter, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        holder.questionNumber.setText("Q" + (position + 1));
        holder.yourAnswer.setText(userAnswers.get(position));
        holder.correctAnswer.setText(correctAnswers.get(position));
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView questionNumber, yourAnswer, correctAnswer;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            questionNumber = itemView.findViewById(R.id.questionNumber);
            yourAnswer = itemView.findViewById(R.id.yourAnswer);
            correctAnswer = itemView.findViewById(R.id.correctAnswer);
        }
    }
}