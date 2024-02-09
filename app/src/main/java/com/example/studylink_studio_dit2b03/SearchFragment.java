package com.example.studylink_studio_dit2b03;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView searchResultsRecyclerView;
    private List<String> searchResults; // Example list for search results

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize UI components
        searchView = view.findViewById(R.id.searchView);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);

        // Configure the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        searchResultsRecyclerView.setLayoutManager(layoutManager);
        searchResults = new ArrayList<>(); // Initialize with empty list
        // Initialize and set the adapter for RecyclerView
        SearchResultsAdapter adapter = new SearchResultsAdapter(searchResults);
        searchResultsRecyclerView.setAdapter(adapter);

        // Set up the SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search when user submits query
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform search as user types (optional)
                // performSearch(newText);
                return true;
            }
        });

        return view;
    }

    private void performSearch(String query) {
        // Implement search logic here
        // For example, update the searchResults list with matching results
        // and notify the adapter
        List<String> matchingResults = searchInData(query); // Implement this method
        searchResults.clear();
        searchResults.addAll(matchingResults);
        searchResultsRecyclerView.setVisibility(View.VISIBLE);
        searchResultsRecyclerView.getAdapter().notifyDataSetChanged();
    }

    // Method to simulate search in data
    private List<String> searchInData(String query) {
        // Implement your actual search logic here
        // For now, returning some dummy data
        List<String> dummyData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            dummyData.add("Result " + i);
        }
        return dummyData;
    }
}
