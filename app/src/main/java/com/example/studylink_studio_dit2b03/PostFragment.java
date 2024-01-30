package com.example.studylink_studio_dit2b03;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;

public class PostFragment extends Fragment {
    User userInstance = User.getInstance();
    EditText communityNameEditText,communityDescriptionEditText;
    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if (userInstance.getRoleid() == 1) {
            return inflater.inflate(R.layout.fragment_student_post, container, false);
        } else if (userInstance.getRoleid() == 2) {
            view = inflater.inflate(R.layout.fragment_tutor_post, container, false);

            // Set click listener for the "Create Community" button
            Button createCommunityButton = view.findViewById(R.id.btnCreateCommunity);
            createCommunityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toggle visibility of the EditText fields for community name and description
                    communityNameEditText = view.findViewById(R.id.etCommunityTitle);
                    communityDescriptionEditText = view.findViewById(R.id.etCommunityDescription);
                    Button createButton = view.findViewById(R.id.btnCreateNewCommunity);

                    int visibility = communityNameEditText.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;

                    communityNameEditText.setVisibility(visibility);
                    communityDescriptionEditText.setVisibility(visibility);
                    createButton.setVisibility(visibility);
                }
            });

            // Set click listener for the "Create" button
            Button createButton = view.findViewById(R.id.btnCreateNewCommunity);
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String communityTitle = communityNameEditText.getText().toString();
                    String communityDescription = communityDescriptionEditText.getText().toString();
                    // Check if the community name and description are not empty
                    if (!communityTitle.isEmpty() && !communityDescription.isEmpty()) {
                        Community community=new Community();
                        community.setTitle(communityTitle);
                        community.setDescription(communityDescription);
                        community.setCreatorId(userInstance.getUserid());

                        Date creationDate=new Date(System.currentTimeMillis());
                        community.setCreationTimestamp(creationDate);
                        community.setMemberCount(1);


                        FirebaseFirestore.getInstance().collection("Community")
                                .add(community)
                                .addOnSuccessListener(documentReference->{

                                    String communityId=documentReference.getId();
                                    community.setCommunityId(communityId);

                                    documentReference.set(community)
                                            .addOnSuccessListener(aVoid->{

                                                navigateToCommunityDetails(community);
                                            })
                                            .addOnFailureListener(e->{

                                            });
                                })
                                .addOnFailureListener(e->{

                                });
                    }

                                 else {
                        // Handle the case when either the name or description is empty
                        Log.e("create new community", "Community name or description is empty");
                    }
                }
            });

            return view;
        }  else {
            Intent intent = new Intent(requireContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
            // Return an empty view if the user is not logged in
            return new View(requireContext());
        }
    }

    private void navigateToCommunityDetails(Community community){
        Log.d("creating new community completely done", "navigateToCommunityDetails: ");
        Toast.makeText(requireContext(), "Community created successfully", Toast.LENGTH_SHORT).show();        Bundle bundle = new Bundle();
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





