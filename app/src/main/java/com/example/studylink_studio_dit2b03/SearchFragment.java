package com.example.studylink_studio_dit2b03;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchResultsAdapter adapter;
    private List<SearchItem> searchItemList;
    private FirebaseFirestore db;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchItemList = new ArrayList<>();
        adapter = new SearchResultsAdapter(getActivity(), searchItemList);
        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handle search when user enters a query in the SearchView
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    clearSearchResults();
                }
                return false;
            }
        });
    }

    private void search(String query) {
        // Clear existing search results
        searchItemList.clear();

        // Perform search in Firestore
        CollectionReference collectionRef = db.collection("your_collection_name");
        Query searchQuery = collectionRef.whereEqualTo("field_to_search", query);
        searchQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Parse search results and add them to searchItemList
                    SearchItem item = document.toObject(SearchItem.class);
                    searchItemList.add(item);
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data change
            } else {
                Toast.makeText(getContext(), "Error searching: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearSearchResults() {
        searchItemList.clear();
        adapter.notifyDataSetChanged();
    }
}
