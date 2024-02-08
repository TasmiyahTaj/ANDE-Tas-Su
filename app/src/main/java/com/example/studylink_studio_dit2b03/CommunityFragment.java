package com.example.studylink_studio_dit2b03;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {
    private String tutorId;
    private RecyclerView recyclerView;
    private CommunityAdapter communityAdapter;
    private List<Community> communities; // Declare the list

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.community_pfp, container, false);


        Bundle args = getArguments();
        if (args != null) {
            tutorId = args.getString("tutorId");
        } else {
            Log.e("CommunityFragment", "No tutorId provided");
        }

        recyclerView = view.findViewById(R.id.recyclerViewCommunity);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch community data
        fetchCommunityData();


        return view;
    }


    private void fetchCommunityData() {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the "Community" collection for communities created by the tutorId
        db.collection("Community")
                .whereEqualTo("creatorId", tutorId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        communities = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Retrieve data from each document and create a Community object
                            Community community = document.toObject(Community.class);
                            communities.add(community);
                            Log.d("community", community.getCommunityId());
                        }
                        // Initialize the adapter after data is fetched
                        initCommunityAdapter();

                    } else {
                        // Handle errors
                        // For example, Log error message
                        Log.e("CommunityFragment", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void initCommunityAdapter() {
        // Adapter initialization should be done after data is fetched
        communityAdapter = new CommunityAdapter(communities);
        recyclerView.setAdapter(communityAdapter);

        communityAdapter.setOnCommunityItemClickListener(community -> {
            // Handle item click, navigate to CommunityPageFragment
            navigateToCommunityPage(community);
        });
    }

    private void navigateToCommunityPage(Community community) {
        Bundle bundle = new Bundle();
        bundle.putString("communityID", community.getCommunityId());
        Log.d("communityID", community.getCommunityId());
        // Create the destination fragment and set arguments
        Fragment communityFragment = new CommunityPageFragment();
        communityFragment.setArguments(bundle);

        // Replace the current fragment with the destination fragment
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.container, communityFragment).addToBackStack(null).commit();


    }


}
