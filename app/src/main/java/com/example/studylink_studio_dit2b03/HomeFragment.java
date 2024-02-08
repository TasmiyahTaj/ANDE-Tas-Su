package com.example.studylink_studio_dit2b03;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Date;


public class HomeFragment extends Fragment {

    private List<CardView> cardViews;
    private List<TextView> trendingCommunityTitles;
    private List<TextView> trendingCommunityDescriptions;
    private List<TextView> memberCounts;
    private List<Button> joinButtons;
    private List<Community> trendingCommunities;
    private User userInstance;
    private List<Question> questionList = new ArrayList<>();
    private RecyclerView recyclerView;
    private QuestionAdapterOther questionAdapterOther;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInstance = User.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        cardViews = new ArrayList<>();
        trendingCommunityTitles = new ArrayList<>();
        trendingCommunityDescriptions = new ArrayList<>();
        memberCounts = new ArrayList<>();
        joinButtons = new ArrayList<>();

        // Add the views for Trending Community 1
        cardViews.add((CardView) view.findViewById(R.id.cardView1));
        trendingCommunityTitles.add((TextView) view.findViewById(R.id.trendingCommunityTitle1));
        trendingCommunityDescriptions.add((TextView) view.findViewById(R.id.trendingCommunityDescription1));
        memberCounts.add((TextView) view.findViewById(R.id.memberCount1));
        joinButtons.add((Button) view.findViewById(R.id.joinButton1));

        // Add the views for Trending Community 2
        // Adjust the IDs accordingly in the findViewById calls
        cardViews.add((CardView) view.findViewById(R.id.cardView2));
        trendingCommunityTitles.add((TextView) view.findViewById(R.id.trendingCommunityTitle2));
        trendingCommunityDescriptions.add((TextView) view.findViewById(R.id.trendingCommunityDescription2));
        memberCounts.add((TextView) view.findViewById(R.id.memberCount2));
        joinButtons.add((Button) view.findViewById(R.id.joinButton2));

        // Add the views for Trending Community 3
        // Adjust the IDs accordingly in the findViewById calls
        cardViews.add((CardView) view.findViewById(R.id.cardView3));
        trendingCommunityTitles.add((TextView) view.findViewById(R.id.trendingCommunityTitle3));
        trendingCommunityDescriptions.add((TextView) view.findViewById(R.id.trendingCommunityDescription3));
        memberCounts.add((TextView) view.findViewById(R.id.memberCount3));
        joinButtons.add((Button) view.findViewById(R.id.joinButton3));

        trendingCommunities = new ArrayList<>();

        // Load top 3 trending communities
        loadTrendingCommunities();

        // Set click listeners for each CardView
        cardViews.get(0).setOnClickListener(v -> handleCardClick(trendingCommunities.get(0)));
        cardViews.get(1).setOnClickListener(v -> handleCardClick(trendingCommunities.get(1)));
        cardViews.get(2).setOnClickListener(v -> handleCardClick(trendingCommunities.get(2)));

        recyclerView = view.findViewById(R.id.questionRecyclerViewinHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        questionAdapterOther = new QuestionAdapterOther(questionList);
        recyclerView.setAdapter(questionAdapterOther);

        return view;
    }

    private void loadTrendingCommunities() {
        // Fetch top 3 communities based on member count
        FirebaseFirestore.getInstance().collection("Community")
                .orderBy("memberCount", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        trendingCommunities.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Community community = document.toObject(Community.class);
                            trendingCommunities.add(community);
                        }

                        // Set data for each trending community
                        for (int i = 0; i < trendingCommunities.size(); i++) {
                            setCommunityData(trendingCommunities.get(i),
                                    trendingCommunityTitles.get(i),
                                    trendingCommunityDescriptions.get(i),
                                    memberCounts.get(i),
                                    joinButtons.get(i));
                        }
                    }
                });
    }
    private void setCommunityData(Community community, TextView titleTextView, TextView descriptionTextView, TextView memberCountTextView, Button joinButton) {
        // Set data for the card
        String userId = userInstance.getUserid(); // Implement the method to get the current user's ID
        if (userId != null) {
            checkIfUserJoined(community, userId, joinButton);
        }
        titleTextView.setText(community.getTitle());
        // Limit the length of the description to a certain number of characters
        int maxDescriptionLength = 25; // Set your desired maximum length
        String truncatedDescription = truncateText(community.getDescription(), maxDescriptionLength);
        descriptionTextView.setText(truncatedDescription);
        memberCountTextView.setText("Members: " + community.getMemberCount());


    }
    private void checkIfUserJoined(Community community, String userId, Button joinButton) {
        FirebaseFirestore.getInstance()
                .collection("Community")
                .document(community.getCommunityId())
                .collection("members")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        // User is already a member, update the button text
                        joinButton.setText("JOINED");
                        joinButton.setEnabled(false); // Optionally, disable the button
                    } else {
                        // User is not a member, set the join button click listener
                        joinButton.setOnClickListener(v -> handleJoinButtonClick(community, joinButton));
                    }
                })
                .addOnSuccessListener(documentSnapshot -> {
                    // Move the UI update logic here, inside the success listener
                    if (documentSnapshot.exists()) {
                        // User is already a member, update the button text
                        joinButton.setText("JOINED");
                        joinButton.setEnabled(false); // Optionally, disable the button
                    } else {
                        // User is not a member, set the join button click listener
                        joinButton.setOnClickListener(v -> handleJoinButtonClick(community, joinButton));
                    }
                });
    }
    private void handleJoinButtonClick(Community community, Button joinButton) {
        // Add logic to save user ID in the community's members subcollection
        String userId = userInstance.getUserid(); // Implement the method to get the current user's ID

        if (userId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Reference to the community document
            DocumentReference communityRef = db.collection("Community").document(community.getCommunityId());

            // Add the user to the members subcollection
            db.runTransaction(transaction -> {
                // Retrieve the current member count
                DocumentSnapshot snapshot = transaction.get(communityRef);

                // Check if the document exists and contains the "member_count" field
                if (snapshot.exists() && snapshot.contains("memberCount")) {
                    long currentMemberCount = snapshot.getLong("memberCount");

                    // Update the member count and add the user to the subcollection
                    transaction.update(communityRef, "memberCount", currentMemberCount + 1);
                    transaction.set(communityRef.collection("members").document(userId), new HashMap<>());

                    // Create a reference to the user's document in the 'users' collection
                    DocumentReference userRef = db.collection("users").document(userId);

                    // Check if the 'joined_communities' subcollection exists, create if not
                    if (!userRef.collection("joined_communities").document(community.getCommunityId()).equals(null)) {
                        transaction.set(userRef.collection("joined_communities").document(community.getCommunityId()), new HashMap<>());
                    }
                } else {
                    // Handle the case where the document doesn't exist or "member_count" is missing
                    Log.e("JoinButtonClick", "Document doesn't exist or missing 'member_count' field");
                }

                return null;
            }).addOnSuccessListener(aVoid -> {
                // Update UI or show a success message if needed
                Log.d("JoinButtonClick", "User joined community: " + community.getTitle());
                // After joining, update the button text
                joinButton.setText("JOINED");
                joinButton.setEnabled(false); // Optionally, disable the button
            }).addOnFailureListener(e -> {
                // Handle the failure to join (e.g., display an error message)
                Log.e("JoinButtonClick", "Failed to join community: " + community.getTitle(), e);
            });
        }
    }

    private Button getJoinButtonForCommunity(Community community) {
        int communityIndex = trendingCommunities.indexOf(community);
        if (communityIndex != -1 && communityIndex < joinButtons.size()) {
            return joinButtons.get(communityIndex);
        }
        return null;
    }
    private String truncateText(String text, int maxLength) {
        if (text.length() > maxLength) {
            // Truncate the text and append an ellipsis (...) at the end
            return text.substring(0, maxLength - 3) + "...";
        } else {
            return text;
        }
    }
    private void handleCardClick(Community community) {
        Log.d("CardClick", "Clicked on community: " + community.getTitle());

        // Create a bundle to pass data
        Bundle bundle = new Bundle();
        bundle.putString("communityID", community.getCommunityId());
        // Create the destination fragment and set arguments
        Fragment communityFragment = new CommunityPageFragment();
        communityFragment.setArguments(bundle);

        // Replace the current fragment with the destination fragment
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.container, communityFragment).addToBackStack(null).commit();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Check if userID is available before loading posts
        String userId = userInstance.getUserid();
        if (userId != null) {
            loadPostsFromJoinedCommunities(userId);
            requireActivity().runOnUiThread(() -> questionAdapterOther.notifyDataSetChanged());
        } else {
            Log.d("userID is null", "userID is null");
        }
    }

    private void loadPostsFromJoinedCommunities(String userId) {
        Log.d("user ID ", userId);
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .collection("joined_communities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> joinedCommunities = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String communityId = document.getId();
                            Log.d("joined community", communityId);
                            joinedCommunities.add(communityId);
                        }
                        // Move the following line here, outside the loop
                        // This ensures that post fetching is done after all communities are processed
                        observeCommunityData(joinedCommunities);
                        requireActivity().runOnUiThread(() -> questionAdapterOther.notifyDataSetChanged());
                    }
                });
    }

    private void observeCommunityData(List<String> joinedCommunities) {
        for (String communityId : joinedCommunities) {
            fetchPostsFromCommunity(communityId);
        }
        requireActivity().runOnUiThread(() -> questionAdapterOther.notifyDataSetChanged());
        sortQuestionsByTimestamp();
    }

    private void sortQuestionsByTimestamp() {
        Collections.sort(questionList, new Comparator<Question>() {
            @Override
            public int compare(Question q1, Question q2) {
                // Assuming createdAt is of type Date
                return q1.getCreatedAt().compareTo(q2.getCreatedAt());
            }
        });

        // Now you can use the sorted questionList as needed
        // For example, update your UI or perform any other operations
    }

    private void fetchPostsFromCommunity(String communityId) {
        // Fetch community name from the "Community" collection
        FirebaseFirestore.getInstance().collection("Community")
                .document(communityId)
                .get()
                .addOnCompleteListener(communityTask -> {
                    if (communityTask.isSuccessful()) {
                        String communityName = communityTask.getResult().getString("title");

                        // Now you have the community name, you can use it as needed

                        // Fetch posts from the "Questions" collection
                        FirebaseFirestore.getInstance().collection("Questions")
                                .whereEqualTo("communityID", communityId)
                                .get()
                                .addOnCompleteListener(postTask -> {
                                    if (postTask.isSuccessful()) {
                                        questionList.clear();
                                        for (QueryDocumentSnapshot document : postTask.getResult()) {
                                            String questionID = document.getString("questionId");
                                            String title = document.getString("title");
                                            String description = document.getString("description");
                                            String questionImage = document.getString("questionImageUrl");
                                            String userID = document.getString("userID");
                                            Timestamp timestamp = document.getTimestamp("createdAt");
                                            // Convert the timestamp to a Date object
                                            Date createdAt = timestamp != null ? timestamp.toDate() : null;

                                            // Fetch username from "users" collection based on userID
                                            FirebaseFirestore.getInstance().collection("users")
                                                    .document(userID)
                                                    .get()
                                                    .addOnCompleteListener(userTask -> {
                                                        if (userTask.isSuccessful()) {
                                                            String username = userTask.getResult().getString("username");

                                                            // Now you have the username, you can use it as needed
                                                            Log.d("Username", username);
                                                            Log.d("question title", title);

                                                            Question newQuestion;
                                                            if (questionImage != null ) {
                                                                Log.d("not null", title);

                                                                newQuestion = new Question(questionID,userID, communityName, title, description, username, questionImage,createdAt);
                                                            } else {
                                                                Log.d("it is null", "Some data is missing");
                                                                newQuestion = new Question(questionID,userID, communityName, title, description, username, createdAt);
                                                            }

                                                            questionList.add(newQuestion);
                                                            questionAdapterOther.notifyDataSetChanged();
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                });
    }


    @Override
    public void onDestroyView() {
        // Clean up resources related to the view
        super.onDestroyView();
    }
}

