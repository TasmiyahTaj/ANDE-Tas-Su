package com.example.studylink_studio_dit2b03;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CommunityPageFragment extends Fragment {
    private User userInstance;
    private Community communityData;
    private String communityID;
    private TextView communityNameTextView, communityDescriptionTextView, communityMemberCountTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_page, container, false);

        // Retrieve data from arguments
        Bundle args = getArguments();
        if (args != null) {
            communityID = args.getString("communityID");
            // Retrieve notes of that particular community
            // Retrieve community data
            retrieveCommunityData(communityID);

            // Find UI elements
            TabLayout tabLayout = view.findViewById(R.id.tabLayout);
            ViewPager viewPager = view.findViewById(R.id.viewPager);

            // Set up ViewPager and attach adapter
            CommunityPagerAdapter pagerAdapter = new CommunityPagerAdapter(getChildFragmentManager(), communityID);
            viewPager.setAdapter(pagerAdapter);

            // Link the TabLayout to the ViewPager
            tabLayout.setupWithViewPager(viewPager);

            // Set up click listener for the three dots icon
            ImageView menuIcon = view.findViewById(R.id.menuIcon);
            menuIcon.setOnClickListener(v -> showPopupMenu(v));
        }

        ImageView menuIcon = view.findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> showPopupMenu(v));

        return view;
    }

    private void retrieveCommunityData(String communityID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Community").document(communityID);

        // Retrieve the data
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Convert the document snapshot to a Community object
                    communityData = document.toObject(Community.class);

                    if (communityData != null) {
                        // Now you can use the communityData as needed
                        communityNameTextView = getView().findViewById(R.id.communityTitleTextView);
                        communityDescriptionTextView = getView().findViewById(R.id.communityDescriptionTextView);
                        communityMemberCountTextView = getView().findViewById(R.id.memberCountTextView);
                        communityNameTextView.setText(communityData.getTitle());
                        communityDescriptionTextView.setText(communityData.getDescription());
                        communityMemberCountTextView.setText("Member :" + communityData.getMemberCount());
                        String creatorID = communityData.getCreatorId();

                        userInstance = User.getInstance();
                        String userID = userInstance.getUserid();

                        // Check if the userID is the same as the creatorID
                        if (userID != null && userID.equals(creatorID)) {
                            // If true, the current user is the creator, you can show the three dots icon
                            ImageView menuIcon = getView().findViewById(R.id.menuIcon);
                            menuIcon.setVisibility(View.VISIBLE);
                        }
                        // Add other fields as needed
                    } else {
                        // Handle the case where the data couldn't be converted to a Community object
                    }
                } else {
                    // Handle the case where the document does not exist
                }
            } else {
                // Handle exceptions or errors
                Exception exception = task.getException();
                if (exception != null) {
                    // Handle the exception
                }
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.inflate(R.menu.community_options_menu); // Create a menu resource file (res/menu/community_options_menu.xml)

        // Set up click listener for menu items
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_edit:
                    showEditDialog();
                    return true;
                case R.id.menu_delete:
                    showDeleteConfirmationDialog();
                    return true;
                default:
                    return false;
            }
        });

        // Show the popup menu
        popupMenu.show();
    }

    private void showEditDialog() {
        // Create an AlertDialog for editing community title and description
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Community");

        // Set up the layout for the dialog
        View editDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.edit_community_dialog, null);
        EditText editTitleEditText = editDialogView.findViewById(R.id.editTitleEditText);
        EditText editDescriptionEditText = editDialogView.findViewById(R.id.editDescriptionEditText);

        // Use the communityData obtained in retrieveCommunityData to prepopulate the EditText fields
        if (communityData != null) {
            editTitleEditText.setText(communityData.getTitle());
            editDescriptionEditText.setText(communityData.getDescription());
        }

        builder.setView(editDialogView);

        builder.setPositiveButton("Save", (dialog, which) -> {
            // Handle the save button click
            String newTitle = editTitleEditText.getText().toString().trim();
            String newDescription = editDescriptionEditText.getText().toString().trim();

            // Check if the new title and description are not empty
            if (!TextUtils.isEmpty(newTitle) && !TextUtils.isEmpty(newDescription)) {
                // Update the community data in the database
                updateCommunityData(newTitle, newDescription);
            } else {
                // Show an error message or handle the case where title or description is empty
                Toast.makeText(requireContext(), "Title and description cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });


        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Handle the cancel button click
            dialog.dismiss();
        });

        builder.show();
    }

    private void updateCommunityData(String newTitle, String newDescription) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (communityData != null) {
            String communityId = communityData.getCommunityId(); // Assuming you have a method getId() in your Community class

            // Create a map with the updated data
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("title", newTitle);
            updatedData.put("description", newDescription);

            // Update the document in the "Community" collection
            db.collection("Community")
                    .document(communityId)
                    .update(updatedData)
                    .addOnSuccessListener(aVoid -> {

                        if (communityData != null) {
                            communityNameTextView.setText(newTitle);
                            communityDescriptionTextView.setText(newDescription);
                            communityData.setTitle(newTitle);
                            communityData.setDescription(newDescription);
                        }
                        //Toast.makeText(requireContext(), "Community updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to update community", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Community");
        builder.setMessage("Are you sure you want to delete this community?");

        // Set up buttons for the dialog
        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteCommunityAndMembers(communityID);
            // User clicked Yes, proceed with the deletion
            Log.d("click", "yes");
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            Log.d("click", "no");
        });

        // Show the dialog
        builder.show();
    }

    private void deleteCommunityAndMembers(String communityID) {
        // Step 1: Retrieve the list of user IDs from the 'members' subcollection
        FirebaseFirestore.getInstance()
                .collection("Community")
                .document(communityID)
                .collection("members")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String userId = document.getId();

                            // Step 2: Delete the user from the 'joined_communities' subcollection
                            deleteCommunityFromUser(userId, communityID);
                        }

                        // Step 3: Delete the community, questions, and notes
                        deleteCommunity(communityID);
                    } else {
                        Log.e("DeleteQuestions", "deleteCommunityAndMembers");
                    }
                });
    }

    private void deleteCommunityFromUser(String userId, String communityID) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("joined_communities")
                .document(communityID)
                .delete()
                .addOnFailureListener(e -> {
                    // Handle failure to delete 'joined_communities' document
                });
    }

    private void deleteCommunity(String communityID) {
        Log.d("delete community start", "after deleting all members related to community");
        // Step 1: Delete questions related to the community
        deleteQuestions(communityID);
    }

    private void deleteQuestions(String communityID) {
        Log.d("inside", "delete Q ");
        FirebaseFirestore.getInstance()
                .collection("Questions")
                .whereEqualTo("communityID", communityID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Delete each document in the 'Questions' collection with the specified communityID
                        for (DocumentSnapshot document : task.getResult()) {
                            String questionId = document.getId();

                            // Delete the 'Replies' subcollection for each question
                            deleteSubcollection("Questions", questionId, "Replies");

                            // Delete the question document itself
                            document.getReference().delete();
                        }

                        // Continue with the next step in the deletion process
                        deleteNotes(communityID);
                    } else {
                        // Handle failure to retrieve and delete questions
                    }
                });
    }
    private void deleteSubcollection(String collectionPath, String documentId, String subcollectionPath) {
        FirebaseFirestore.getInstance()
                .collection(collectionPath)
                .document(documentId)
                .collection(subcollectionPath)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Delete each document in the subcollection
                        for (DocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                    } else {
                        // Handle failure to retrieve and delete subcollection documents
                    }
                });
    }



    private void deleteNotes(String communityID) {
        Log.d("inside", "delete N ");
        FirebaseFirestore.getInstance()
                .collection("Note")
                .whereEqualTo("communityId", communityID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("delete note", "success");
                        // Delete each document in the 'Notes' collection with the specified communityID
                        for (DocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }

                        // Continue with the next step in the deletion process
                        deleteMembers(communityID);
                    } else {
                        Log.d("delete note", "no");
                    }
                });
    }

    private void deleteMembers(String communityID) {
        FirebaseFirestore.getInstance()
                .collection("Community")
                .document(communityID)
                .collection("members")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Delete each document in the 'members' subcollection
                        for (DocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }

                        // Now delete the community document
                        deleteCommunityDocument(communityID);
                    } else {
                        // Handle failure to retrieve 'members' subcollection
                    }
                });
    }

    private void deleteCommunityDocument(String communityID) {
        FirebaseFirestore.getInstance()
                .collection("Community")
                .document(communityID)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    navigateToHomepage();
                })
                .addOnFailureListener(e -> {
                    // Handle failure to delete the community
                });
    }

    private void navigateToHomepage() {
        Fragment homeFragment = new HomeFragment();


        // Replace the current fragment with the destination fragment
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.container, homeFragment).addToBackStack(null).commit();

    }




}
