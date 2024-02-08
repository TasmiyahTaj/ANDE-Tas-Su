package com.example.studylink_studio_dit2b03;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {

    private RecyclerView recyclerView;
    private CommunityAdapter communityAdapter;
    private String tutorId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questions, container, false);

        recyclerView = view.findViewById(R.id.questionOtherRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        communityAdapter = new CommunityAdapter();
        recyclerView.setAdapter(communityAdapter);

        Bundle args = getArguments();
        if (args != null) {
            tutorId = args.getString("tutorId");
        } else {
            Log.e("CommunityFragment", "No tutorId provided");
        }

        // Fetch community data
        fetchCommunityData();

        return view;
    }

    private void fetchCommunityData() {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the "Community" collection for communities created by the tutorId
        db.collection("Community")
                .whereEqualTo("tutorId", tutorId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Community> communities = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Retrieve data from each document and create a Community object
                            Community community = document.toObject(Community.class);
                            communities.add(community);
                        }
                        // Update RecyclerView with fetched communities
                        communityAdapter.setCommunities(communities);
                    } else {
                        // Handle errors
                        // For example, Log error message
                        Log.e("CommunityFragment", "Error getting documents: ", task.getException());
                    }
                });
    }
}
