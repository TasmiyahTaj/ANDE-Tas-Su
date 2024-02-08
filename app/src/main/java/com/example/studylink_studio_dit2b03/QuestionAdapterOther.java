package com.example.studylink_studio_dit2b03;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapterOther extends RecyclerView.Adapter<QuestionAdapterOther.QuestionViewHolder> {

    private List<Question> questionList;

    public QuestionAdapterOther(List<Question> questionList) {
        if (questionList == null) {
            this.questionList = new ArrayList<>();
        } else {
            this.questionList = questionList;
        }
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_questions_other, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questionList.get(position);

        // Set the data to your views
        holder.questionTitle.setText(question.getTitle());

        // Display only 2 lines of the description
        String limitedDescription = truncateDescriptionLines(question.getDescription(), 2);
        holder.questionDescription.setText(limitedDescription);

        holder.tutorName.setText("Post By: " + question.getTutorName());

        // Load image using Picasso (replace with your image loading library)
        Picasso.get()
                .load(question.getQuestionImageUrl())
                .placeholder(R.drawable.question_default) // Add a placeholder image resource
                .error(R.drawable.ic_red_cross) // Add an error image resource
                .into(holder.questionImage);

        // Set click listener for the entire item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the context from the view
                Context context = v.getContext();

                // Create the destination fragment and set arguments
                Bundle bundle = new Bundle();
                // Add any data you want to pass to the fragment
                bundle.putString("questionTitle", question.getTitle());
                bundle.putString("questionDescription", question.getDescription());
                bundle.putString("questionCommunity", question.getCommunityName());
                bundle.putString("questionId", question.getQuestionId());
                bundle.putString("questionImageUrl", question.getQuestionImageUrl());
                QuestionDetailsFragment questionFragment = new QuestionDetailsFragment();
                questionFragment.setArguments(bundle);

                // Replace the current fragment with the destination fragment
                FragmentTransaction fm = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.container, questionFragment).addToBackStack(null).commit();
            }
        });


    }

    // Helper method to truncate the description to a specified number of lines
    private String truncateDescriptionLines(String description, int maxLines) {
        String[] lines = description.split("\n");

        if (lines.length > maxLines) {
            StringBuilder truncatedDescription = new StringBuilder();
            for (int i = 0; i < maxLines; i++) {
                truncatedDescription.append(lines[i]).append("\n");
            }
            return truncatedDescription.toString().trim();
        } else {
            return description;
        }
    }





    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        ImageView questionImage;
        TextView questionTitle;
        TextView questionDescription;
        TextView tutorName;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionImage = itemView.findViewById(R.id.questionImage);
            questionTitle = itemView.findViewById(R.id.questionTitle);
            questionDescription = itemView.findViewById(R.id.questionDescription);
            tutorName = itemView.findViewById(R.id.tutorNamePost);
        }
    }
}
