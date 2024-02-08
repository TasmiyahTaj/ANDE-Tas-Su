package com.example.studylink_studio_dit2b03;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuestionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private QuestionAdapterOther questionAdapterOther;
    private List<Question> questionList = new ArrayList<>();
    private View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_questions, container, false);

        Bundle args = getArguments();
        if (args != null) {
            String communityID = args.getString("communityID");
            Log.d("communityid", communityID);

            // Initialize RecyclerView and adapter
            recyclerView = fragmentView.findViewById(R.id.questionOtherRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            questionAdapterOther = new QuestionAdapterOther(questionList);
            recyclerView.setAdapter(questionAdapterOther);

            // Fetch questions based on userID
            fetchQuestionsFromFirestore(communityID);
        } else {
            Log.d("communityid", "no data");
        }
        return fragmentView;
    }
    private void fetchQuestionsFromFirestore(String communityID) {
        Log.d("fetchQuestions", "Fetching questions from Firestore");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Query questions collection
        db.collection("Questions")
                .whereEqualTo("communityID", communityID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear the existing questionList
                        questionList.clear();

                        // Iterate through the result and populate your RecyclerView adapter
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("fetchQuestions", "Handling document: " + document.getId());
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String questionImage = document.getString("questionImageUrl");
                            String userID = document.getString("userID");
                            String questionId = document.getString("questionId");
                            // Check for null values before using them
                            String communityTitle = document.getString("communityTitle");
                            // Add the question to the list
                            fetchUsernameAndAddQuestion(questionId,userID, communityTitle, title, description, questionImage);
                        }
                    } else {
                        Log.e("Firestore", "Error getting questions", task.getException());
                    }
                });
    }

    private void fetchUsernameAndAddQuestion(String questionId,String userID, String communityTitle, String title, String description, String questionImage) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Query users collection to get the username
        db.collection("users")
                .document(userID)
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    if (userSnapshot.exists()) {
                        String username = userSnapshot.getString("username");

                        // Add the question to the list
                        Question newQuestion;
                        if (questionImage != null) {
                            Log.d("not null", title);
                            newQuestion = new Question(questionId,userID, "DefaultCommunityTitle", title, description, username, questionImage);
                        } else {
                            Log.d("it is null", "Some data is missing");
                            newQuestion = new Question(questionId,userID, "DefaultCommunityTitle", title, description, username);
                        }

                        questionList.add(newQuestion);

                        Log.e("ProfileFragment", "question by me " + questionList.size() + title + username);

                        // Ensure UI updates are on the main thread
                        requireActivity().runOnUiThread(() -> questionAdapterOther.notifyDataSetChanged());
                    }
                });
    }

}