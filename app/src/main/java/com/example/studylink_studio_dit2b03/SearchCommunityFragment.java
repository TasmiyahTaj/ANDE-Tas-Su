package com.example.studylink_studio_dit2b03;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class SearchCommunityFragment extends Fragment {
    private static final String TAG = "SearchCommunityFragment";
    private RecyclerView recyclerView;
    private CommunityAdapterSearch adapter;
    private List<Community> communityList;
    private FirebaseFirestore db;

    public SearchCommunityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_community, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_community);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        communityList = new ArrayList<>();
        adapter = new CommunityAdapterSearch(communityList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        return view;
    }

    // Method to perform search and display results
    public List<Community> performSearch(String query) {
        List<Community> searchResults = new ArrayList<>();

        communityList.clear();
        Log.d(TAG,"THe query is: "+ query);
        db.collection("Community")
                .orderBy("title")
                .whereGreaterThanOrEqualTo("title", query.toLowerCase())
                .whereLessThanOrEqualTo("title", query.toLowerCase() + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            long memberCountLong = document.getLong("memberCount");
                            int memberCount = (int) memberCountLong;
                            String creatorId = document.getString("creatorId");

                            // Add community to the list
                            searchResults.add(new Community(title, memberCount, creatorId));
                        }
                        if (searchResults.isEmpty()) {

                            Toast.makeText(getContext(), "No communities found with the given query", Toast.LENGTH_SHORT).show();
                        }
                        updateSearchResults(searchResults);
                    } else {
                        // Log error message
                        Log.e(TAG, "Error searching communities", task.getException());
                        // Handle error condition
                        Toast.makeText(getContext(), "Error searching communities", Toast.LENGTH_SHORT).show();
                    }
                });

        return searchResults;
    }


    // Method to update the search results in the adapter
    public void updateSearchResults(List<Community> communityList) {
        adapter.updateCommunityList(communityList);
    }
}
