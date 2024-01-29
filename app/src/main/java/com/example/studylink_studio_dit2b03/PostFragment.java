package com.example.studylink_studio_dit2b03;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;


public class PostFragment extends Fragment {

    private int userType;
    private String userID;
    private LinearLayout studentLayout, teacherLayout, createPostLayout, createCommunityLayout;
    private EditText titleEditText, bodyEditText, postTitleEditText, postBodyEditText,
            communityTitleEditText, communityDescriptionEditText;
    private Button createPostButtonStudent, createPostButtonTeacher, createCommunityButton,
            confirmPostButton, confirmCommunityButton;

    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment newInstance(int userType, String userID) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putInt("userType", userType);
        args.putString("userID", userID);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        // Initialize views
        studentLayout = view.findViewById(R.id.studentLayout);
        teacherLayout = view.findViewById(R.id.teacherLayout);
        createPostLayout = view.findViewById(R.id.createPostLayout);
        createCommunityLayout = view.findViewById(R.id.createCommunityLayout);

        titleEditText = view.findViewById(R.id.titleEditText);
        bodyEditText = view.findViewById(R.id.bodyEditText);
        postTitleEditText = view.findViewById(R.id.postTitleEditText);
        postBodyEditText = view.findViewById(R.id.postBodyEditText);
        communityTitleEditText = view.findViewById(R.id.communityTitleEditText);
        communityDescriptionEditText = view.findViewById(R.id.communityDescriptionEditText);

        createPostButtonStudent = view.findViewById(R.id.createPostButtonStudent);
        createPostButtonTeacher = view.findViewById(R.id.createPostButtonTeacher);
        createCommunityButton = view.findViewById(R.id.createCommunityButton);
        confirmPostButton = view.findViewById(R.id.confirmPostButton);
        confirmCommunityButton = view.findViewById(R.id.confirmCommunityButton);

        // Get user type
        if (getArguments() != null) {
            userType = getArguments().getInt("userType", 0);
            userID = getArguments().getString("userID", null);
        }

        // Show/hide layouts based on user type
        if (userType == 1) {
            studentLayout.setVisibility(View.VISIBLE);
            teacherLayout.setVisibility(View.GONE);
        } else if (userType == 2) {
            studentLayout.setVisibility(View.GONE);
            teacherLayout.setVisibility(View.VISIBLE);
        }

        // Set click listeners for buttons
        createPostButtonStudent.setOnClickListener(v -> createPost());
        createPostButtonTeacher.setOnClickListener(v -> showCreatePostLayout());
        createCommunityButton.setOnClickListener(v -> showCreateCommunityLayout());
        confirmPostButton.setOnClickListener(v -> confirmPost());
        confirmCommunityButton.setOnClickListener(v -> confirmCommunity());

        return view;
    }

    private void createPost() {
        // Logic for creating post for students
        // Retrieve data from titleEditText and bodyEditText
    }

    private void showCreatePostLayout() {
        createPostLayout.setVisibility(View.VISIBLE);
        createCommunityLayout.setVisibility(View.GONE);
    }

    private void showCreateCommunityLayout() {
        createPostLayout.setVisibility(View.GONE);
        createCommunityLayout.setVisibility(View.VISIBLE);
    }

    private void confirmPost() {
        // Logic for confirming post for teachers
        // Retrieve data from postTitleEditText and postBodyEditText
    }

    private void confirmCommunity() {
        // Retrieve data from communityTitleEditText and communityDescriptionEditText
        String communityTitle = communityTitleEditText.getText().toString().trim();
        String communityDescription = communityDescriptionEditText.getText().toString().trim();

        // Check if the title and description are not empty
        if (!communityTitle.isEmpty() && !communityDescription.isEmpty()) {
            // Create a new Community object with the provided data
            Community community = new Community();
            community.setTitle(communityTitle);
            community.setDescription(communityDescription);
            community.setCreatorId(userID); // Replace with the actual creator ID

            // Convert the long timestamp to a Date object
            Date creationDate = new Date(System.currentTimeMillis());
            community.setCreationTimestamp(creationDate);
            community.setMemberCount(1); // Initial count including the creator

            // Add the community document to the "Community" collection
            FirebaseFirestore.getInstance().collection("Community")
                    .add(community)
                    .addOnSuccessListener(documentReference -> {
                        // Document added successfully
                        // Retrieve the generated communityId and set it in the community object
                        String communityId = documentReference.getId();
                        community.setCommunityId(communityId);

                        // Now, update the document with the community object including the communityId
                        documentReference.set(community)
                                .addOnSuccessListener(aVoid -> {
                                    // Document updated successfully
                                    // For example, you might want to navigate to the community details page
                                    // after successfully creating the community.
                                    navigateToCommunityDetails(community);
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure (document update failed)
                                    // Handle errors or notify the user...
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure (document addition failed)
                        // Handle errors or notify the user...
                    });
        } else {
            // Display an error message or handle the case where the title or description is empty
        }
    }


    public void setUserType(int userType) {
        this.userType = userType;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    private void navigateToCommunityDetails(Community community) {
        // Use community.getCommunityId() for navigation or further processing
        // ...
    }
}





