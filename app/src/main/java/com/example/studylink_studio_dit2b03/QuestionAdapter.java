package com.example.studylink_studio_dit2b03;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<Question> questionList;
    private FragmentActivity activity;

    public QuestionAdapter(List<Question> questionList, FragmentActivity activity) {
        this.questionList = questionList;
        this.activity = activity;
    }
    public QuestionAdapter(List<Question> questionList) {
        this.questionList = questionList;

    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_questions, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questionList.get(position);

        // Set the data to your views
        holder.questionTitle.setText(question.getTitle());
        holder.questionDescription.setText(question.getDescription());
        holder.communityName.setText("Community: " + question.getCommunityName());
        holder.tutorName.setText("Tutor: " + question.getTutorName());

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
                bundle.putString("questionDescription", question.getDescription());
                bundle.putString("questionCommunity", question.getCommunityName());
                bundle.putString("questionId", question.getQuestionId());
                bundle.putString("questionImageUrl", question.getQuestionImageUrl());
                bundle.putString("postedBy", question.getUserID());

                QuestionDetailsFragment questionFragment = new QuestionDetailsFragment();
                questionFragment.setArguments(bundle);

                // Replace the current fragment with the destination fragment
                FragmentTransaction fm = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.container, questionFragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        ImageView questionImage;
        TextView questionTitle;
        TextView questionDescription;
        TextView communityName;
        TextView tutorName;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);

            questionImage = itemView.findViewById(R.id.questionImage);
            questionTitle = itemView.findViewById(R.id.questionTitle);
            questionDescription = itemView.findViewById(R.id.questionDescription);
            communityName = itemView.findViewById(R.id.communityNamePost);
            tutorName = itemView.findViewById(R.id.tutorNamePost);
        }
    }
}
