package com.example.studylink_studio_dit2b03;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<Question> questionList;

    public QuestionAdapter(List<Question> questionList) {
        if (questionList == null) {
            this.questionList = new ArrayList<>();
        } else {
            this.questionList = questionList;
        }
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
