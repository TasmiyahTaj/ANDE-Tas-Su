package com.example.studylink_studio_dit2b03;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class QuestionDetailsFragment extends Fragment {
    private String questionId;
    private TextView questionTitleTextView;
    private TextView questionDescriptionTextView;
    private TextView communityNameTextView;
    private EditText replyEditText;
    private Button postButton;
    private LinearLayout repliesLinearLayout;
    private FirebaseFirestore db;
    User userInstance = User.getInstance();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.question_detail, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        questionTitleTextView = view.findViewById(R.id.questionTitleTextView);
        questionDescriptionTextView = view.findViewById(R.id.questionDescriptionTextView);
        communityNameTextView = view.findViewById(R.id.communityNameTextView);
        replyEditText = view.findViewById(R.id.replyEditText);
        postButton = view.findViewById(R.id.postButton);
        repliesLinearLayout = view.findViewById(R.id.repliesLinearLayout);

        // Retrieve question details from arguments
        Bundle args = getArguments();
        if (args != null) {
            String questionTitle = args.getString("questionTitle");
            String questionDescription = args.getString("questionDescription");
            String communityName = args.getString("communityName");
            questionId=args.getString("questionId");
            // Set question details to the views
            questionTitleTextView.setText(questionTitle);
            questionDescriptionTextView.setText(questionDescription);
            communityNameTextView.setText(communityName);
            fetchReplies();
        }

        // Set click listener for the post button
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReply();
            }
        });

        return view;
    }

    // Method to post a reply
    private void postReply() {
        String replyText = replyEditText.getText().toString().trim();
        if (!replyText.isEmpty()) {
            // Retrieve user details from User singleton instance
            String userId = userInstance.getUserid();
            String userName = userInstance.getUsername();
            String userProfilePicUrl = userInstance.getProfilePicUrl();

            // If userProfilePicUrl is null, set it to "null"
            if (userProfilePicUrl == null) {
                userProfilePicUrl = "null";
            }

            // Create a new reply object
            Map<String, Object> reply = new HashMap<>();
            reply.put("text", replyText);
            reply.put("timestamp", FieldValue.serverTimestamp()); // Add a timestamp for sorting
            reply.put("userId", userId); // Add user ID to the reply
            reply.put("userName", userName); // Add user name to the reply
            reply.put("userProfilePicUrl", userProfilePicUrl); // Add user profile picture URL to the reply

            // Add the reply to the "Replies" subcollection under the question ID
            db.collection("Questions").document(questionId).collection("Replies")
                    .add(reply)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                // Clear the reply EditText after posting
                                replyEditText.setText("");
                                // Display a success message
                                Toast.makeText(getActivity(), "Reply posted successfully", Toast.LENGTH_SHORT).show();
                                // Refresh the list of replies
                                fetchReplies();
                            } else {
                                // Display an error message if posting fails
                                Toast.makeText(getActivity(), "Failed to post reply", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Display a message if the reply text is empty
            Toast.makeText(getActivity(), "Please enter a reply", Toast.LENGTH_SHORT).show();
        }
    }


    // Method to fetch and display replies
    private void fetchReplies() {
        // Clear the existing replies
        repliesLinearLayout.removeAllViews();

        // Fetch replies from the "Replies" subcollection under the question ID
        db.collection("Questions").document(questionId).collection("Replies")
                .orderBy("timestamp") // Order replies by timestamp
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Iterate through the query snapshot
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get the reply data
                                String replyText = document.getString("text");

                                // Inflate the reply item layout
                                View replyItemView = getLayoutInflater().inflate(R.layout.reply_item, null);

                                // Find views within the reply item layout
                                TextView replyTextView = replyItemView.findViewById(R.id.replyTextView);
                                Button replyButton = replyItemView.findViewById(R.id.replyButton);
                                TextView userNameTextView = replyItemView.findViewById(R.id.userNameTextView);
                                ImageView userProfileImageView = replyItemView.findViewById(R.id.userProfileImageView);

                                // Set the reply text
                                replyTextView.setText(replyText);

                                // Set user profile picture and username
                                String userId = document.getString("userId");
                                String username = document.getString("userName");
                                String profile = document.getString("userProfilePicUrl");
                                userNameTextView.setText(username);
                                if (profile != null && !profile.isEmpty()) {
                                    Picasso.get().load(profile).placeholder(R.drawable.profile).into(userProfileImageView);
                                } else {

                                    userProfileImageView.setImageResource(R.drawable.profile); // Placeholder image
                                }
                                // Retrieve user details from Firestore


                                // Add the reply item to the LinearLayout
                                repliesLinearLayout.addView(replyItemView);
                            }
                        } else {
                            // Handle errors
                            Toast.makeText(getActivity(), "Failed to fetch replies", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
