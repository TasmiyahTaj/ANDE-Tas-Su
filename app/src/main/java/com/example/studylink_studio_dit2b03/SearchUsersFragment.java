package com.example.studylink_studio_dit2b03;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchUsersFragment extends Fragment {
    private static final String TAG = "SearchUsersFragment";

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private List<User> userList;
    private UserAdapter adapter;

    public SearchUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_users, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_users);
        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        adapter = new UserAdapter(userList, requireActivity().getSupportFragmentManager());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return view;
    }


    // Method to perform search and display results
    public List<User> performSearch(String query) {
        List<User> searchResults = new ArrayList<>();

        // Implement your search logic here
        // Clear previous search results
        userList.clear();

        db.collection("users")
                .orderBy("username") // Order by username field
                .whereGreaterThanOrEqualTo("username", query.toLowerCase()) // Convert query to lowercase
                .whereLessThanOrEqualTo("username", query.toLowerCase() + "\uf8ff") // Convert query to lowercase
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Retrieve user data
                                String name = document.getString("username");
                                String userId = document.getId();
                                String email = document.getString("email");
                                int roleId = document.getLong("roleid").intValue();
                                String profilePicUrl = document.getString("profilePicUrl");
                                // Add user to the list
                                searchResults.add(new User(userId,name,email, profilePicUrl, roleId));
                            }
                            // Add search results to userList
                            userList.addAll(searchResults);
                            // Notify adapter about data changes
                            adapter.notifyDataSetChanged();
                        } else {
                            // No user found with the given username
                            Toast.makeText(getContext(), "No users found with username containing: " + query, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Log error message
                        Log.e(TAG, "Error searching users", task.getException());
                        // Handle error condition
                        Toast.makeText(getContext(), "Error searching users", Toast.LENGTH_SHORT).show();
                    }
                });

        return searchResults;
    }

    public void updateSearchResults(List<User> userList) {
        Log.d(TAG,"THe userList is: "+ userList);
        adapter.updateUserList(userList);
    }

}
