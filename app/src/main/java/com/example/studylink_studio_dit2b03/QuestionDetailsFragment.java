package com.example.studylink_studio_dit2b03;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    private AlertDialog dialog;
    private ImageView questionImageView;
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
        questionImageView = view.findViewById(R.id.questionImageView);

        // Retrieve question details from arguments
        Bundle args = getArguments();
        if (args != null) {
            String questionTitle = args.getString("questionTitle");
            String questionDescription = args.getString("questionDescription");
            String communityName = args.getString("questionCommunity");

            String questionImageUrl = args.getString("questionImageUrl");
            String postedBy = args.getString("postedBy");
            questionId=args.getString("questionId");

            questionTitleTextView.setText(questionTitle);
            questionDescriptionTextView.setText(questionDescription);
            communityNameTextView.setText("By:" + communityName);
            if (questionImageUrl != null && !questionImageUrl.isEmpty()) {
                // If the question image URL is available, load and display it
                questionImageView.setVisibility(View.VISIBLE);
                Picasso.get().load(questionImageUrl).into(questionImageView);
            } else {
                // If there's no question image URL, hide the ImageView
                questionImageView.setVisibility(View.GONE);
            }
            if(postedBy== userInstance.getUserid()){
                ImageView editIcon = view.findViewById(R.id.editIcon);
                ImageView deleteIcon = view.findViewById(R.id.deleteIcon);
                editIcon.setVisibility(View.VISIBLE);
                deleteIcon.setVisibility(View.VISIBLE);
                deleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Prompt the user for confirmation before deleting the question
                        showDeleteConfirmationDialog();
                    }
                });
            }else{
                ImageView saveIcon = view.findViewById(R.id.saveIcon);
                saveIcon.setVisibility(View.VISIBLE);

            }
            if (questionId != null) {
                fetchReplies(); // Only call fetchReplies if questionId is not null
            } else {
                // Handle the case where questionId is null
                Toast.makeText(getActivity(), "Unable to fetch replies.", Toast.LENGTH_SHORT).show();
            }

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
            // Check if replyEditText contains a username mention
            if (replyText.contains("@")) {
                // Extract the mentioned username
                int startIndex = replyText.indexOf("@") + 1;
                int endIndex = replyText.indexOf(" ", startIndex);
                String mentionedUsername;
                if (endIndex != -1) {
                    mentionedUsername = replyText.substring(startIndex, endIndex);
                } else {
                    mentionedUsername = replyText.substring(startIndex);
                }
                // Post nested reply
                postNestedReply(replyText, mentionedUsername);
            } else {
                // Post regular reply
                postRegularReply(replyText);
            }
        } else {
            // Display a message if the reply text is empty
            Toast.makeText(getActivity(), "Please enter a reply", Toast.LENGTH_SHORT).show();
        }
    }

    private void postRegularReply(String replyText) {
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
    }

    private void postNestedReply(String replyText, String mentionedUsername) {
        // Check if the mentioned username exists in any of the replies
        db.collection("Questions").document(questionId).collection("Replies")
                .whereEqualTo("userName", mentionedUsername)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Parent reply found, get its ID
                                String parentReplyId = document.getId();
                                // Retrieve user details from User singleton instance
                                String userId = userInstance.getUserid();
                                String userName = userInstance.getUsername();
                                String userProfilePicUrl = userInstance.getProfilePicUrl();
                                // If userProfilePicUrl is null, set it to "null"
                                if (userProfilePicUrl == null) {
                                    userProfilePicUrl = "null";
                                }
                                // Create a new nested reply object
                                Map<String, Object> nestedReply = new HashMap<>();
                                nestedReply.put("text", replyText);
                                nestedReply.put("timestamp", FieldValue.serverTimestamp());
                                nestedReply.put("userId", userId);
                                nestedReply.put("userName", userName);
                                nestedReply.put("userProfilePicUrl", userProfilePicUrl);
                                // Add the nested reply to the "NestedReplies" subcollection under the parent reply ID
                                db.collection("Questions").document(questionId).collection("Replies").document(parentReplyId)
                                        .collection("NestedReplies")
                                        .add(nestedReply)
                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()) {
                                                    // Clear the reply EditText after posting
                                                    replyEditText.setText("");
                                                    // Display a success message
                                                    Toast.makeText(getActivity(), "Nested reply posted successfully", Toast.LENGTH_SHORT).show();
                                                    // Refresh the list of replies
                                                    fetchReplies();
                                                } else {
                                                    // Display an error message if posting fails
                                                    Toast.makeText(getActivity(), "Failed to post nested reply", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            // Handle errors
                            Toast.makeText(getActivity(), "Failed to find parent reply", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }




    // Method to fetch and display replies
    // Inside the fetchReplies method
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
                                String parentReplyId = document.getId(); // Get the parent reply ID

                                // Inflate the reply item layout
                                View replyItemView = getLayoutInflater().inflate(R.layout.reply_item, null);

                                // Find views within the reply item layout
                                TextView replyTextView = replyItemView.findViewById(R.id.replyTextView);
                                TextView replyToTextView = replyItemView.findViewById(R.id.replyToTextView);
                                TextView userNameTextView = replyItemView.findViewById(R.id.userNameTextView);
                                ImageView userProfileImageView = replyItemView.findViewById(R.id.userProfileImageView);
                                LinearLayout nestedRepliesLinearLayout = replyItemView.findViewById(R.id.nestedRepliesLinearLayout);
                                // Set the reply text
                                replyTextView.setText(replyText);

                                // Set user profile picture and username
                                String userId = document.getString("userId");
                                String username = document.getString("userName");
                                String profile = document.getString("userProfilePicUrl");
                                replyToTextView.setText("Reply to @" + username);
                                if (userId.equals(userInstance.getUserid())) {
                                    // If the user is the creator of the reply, add a dot to the username
                                    username += " - Creator";
                                }
                                userNameTextView.setText(username);

                                if (profile != null && !profile.isEmpty()) {
                                    Picasso.get().load(profile).placeholder(R.drawable.profile).into(userProfileImageView);
                                } else {
                                    userProfileImageView.setImageResource(R.drawable.profile); // Placeholder image
                                }

                                // Set click listener for replyToTextView
                                String finalUsername = username;
                                replyToTextView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Get the username from the replyToTextView

                                        // Append "@username" to the replyEditText
                                        replyEditText.append("@" + finalUsername + " ");
                                    }
                                });

                                // Add the reply item to the LinearLayout
                                repliesLinearLayout.addView(replyItemView);

                                // Fetch nested replies for this reply
                                getNestedReplies(parentReplyId, nestedRepliesLinearLayout);
                            }
                        } else {
                            // Handle errors
                            Toast.makeText(getActivity(), "Failed to fetch replies", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getNestedReplies(String parentReplyId, LinearLayout nestedRepliesLayout) {
        // Fetch nested replies from the "NestedReplies" subcollection under the parent reply ID
        db.collection("Questions").document(questionId).collection("Replies").document(parentReplyId)
                .collection("NestedReplies")
                .orderBy("timestamp") // Order nested replies by timestamp
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Clear existing nested replies
                            nestedRepliesLayout.removeAllViews();

                            // Iterate through the query snapshot
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get the nested reply data
                                String nestedReplyText = document.getString("text");
                                String nestedUserId = document.getString("userId");
                                String nestedUsername = document.getString("userName");
                                String nestedProfile = document.getString("userProfilePicUrl");

                                // Inflate the nested reply item layout
                                View nestedReplyItemView = getLayoutInflater().inflate(R.layout.nested_reply_item, null);

                                // Find views within the nested reply item layout
                                TextView nestedReplyTextView = nestedReplyItemView.findViewById(R.id.nestedReplyTextView);
                                TextView nestedUserNameTextView = nestedReplyItemView.findViewById(R.id.nestedUserNameTextView);
                                ImageView nestedUserProfileImageView = nestedReplyItemView.findViewById(R.id.nestedUserProfileImageView);

                                // Set the nested reply text
                                nestedReplyTextView.setText(nestedReplyText);

                                // Set user profile picture and username
                                nestedUserNameTextView.setText(nestedUsername);
                                if (nestedProfile != null && !nestedProfile.isEmpty()) {
                                    Picasso.get().load(nestedProfile).placeholder(R.drawable.profile).into(nestedUserProfileImageView);
                                } else {
                                    nestedUserProfileImageView.setImageResource(R.drawable.profile); // Placeholder image
                                }

                                // Add the nested reply item to the nested replies layout
                                nestedRepliesLayout.addView(nestedReplyItemView);
                            }
                        } else {
                            // Handle errors
                            Toast.makeText(getActivity(), "Failed to fetch nested replies", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this question?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User confirmed deletion, delete the question
                deleteQuestion();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User canceled deletion, dismiss the dialog
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteQuestion() {
        db.collection("Questions").document(questionId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Question successfully deleted
                            Toast.makeText(getActivity(), "Question successfully deleted", Toast.LENGTH_SHORT).show();
                            // Navigate back to HomeFragment
                            getParentFragmentManager().popBackStack();
                        } else {
                            // Error occurred while deleting the question
                            Toast.makeText(getActivity(), "Failed to delete question", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    // Method to fetch and display nested replies
//    private void fetchNestedReplies(String parentReplyId, LinearLayout nestedRepliesLayout) {
//        // Fetch nested replies from the "NestedReplies" subcollection under the parent reply ID
//        db.collection("Questions").document(questionId).collection("Replies").document(parentReplyId)
//                .collection("NestedReplies")
//                .orderBy("timestamp") // Order nested replies by timestamp
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            // Iterate through the query snapshot
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                // Get the nested reply data
//                                String nestedReplyText = document.getString("text");
//                                String nestedUserId = document.getString("userId");
//                                String nestedUsername = document.getString("userName");
//                                String nestedProfile = document.getString("userProfilePicUrl");
//
//                                // Inflate the nested reply item layout
//                                View nestedReplyItemView = getLayoutInflater().inflate(R.layout.nested_reply_item, null);
//
//                                // Find views within the nested reply item layout
//                                TextView nestedReplyTextView = nestedReplyItemView.findViewById(R.id.nestedReplyTextView);
//                                TextView nestedUserNameTextView = nestedReplyItemView.findViewById(R.id.nestedUserNameTextView);
//                                ImageView nestedUserProfileImageView = nestedReplyItemView.findViewById(R.id.nestedUserProfileImageView);
//
//                                // Set the nested reply text
//                                nestedReplyTextView.setText(nestedReplyText);
//
//                                // Set user profile picture and username
//                                nestedUserNameTextView.setText(nestedUsername);
//                                if (nestedProfile != null && !nestedProfile.isEmpty()) {
//                                    Picasso.get().load(nestedProfile).placeholder(R.drawable.profile).into(nestedUserProfileImageView);
//                                } else {
//                                    nestedUserProfileImageView.setImageResource(R.drawable.profile); // Placeholder image
//                                }
//
//                                // Add the nested reply item to the nested replies layout
//                                nestedRepliesLayout.addView(nestedReplyItemView);
//                            }
//                        } else {
//                            // Handle errors
//                            Toast.makeText(getActivity(), "Failed to fetch nested replies", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

}
