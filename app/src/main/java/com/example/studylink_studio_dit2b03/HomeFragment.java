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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    private List<CardView> cardViews;
    private List<TextView> trendingCommunityTitles;
    private List<TextView> trendingCommunityDescriptions;
    private List<TextView> memberCounts;
    private List<Button> joinButtons;

    private List<Community> trendingCommunities;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize lists for CardViews and related views
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

        return view;
    }
    User userInstance = User.getInstance();
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
        bundle.putString("communityTitle", community.getTitle());
        bundle.putString("communityDescription", community.getDescription());
        bundle.putInt("memberCount", community.getMemberCount());

        // Create the destination fragment and set arguments
        Fragment communityFragment = new CommunityPageFragment();
        communityFragment.setArguments(bundle);

        // Replace the current fragment with the destination fragment
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.container, communityFragment).addToBackStack(null).commit();
    }
}
