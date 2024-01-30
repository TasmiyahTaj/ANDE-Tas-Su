package com.example.studylink_studio_dit2b03;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostFragment extends Fragment  {
    User userInstance = User.getInstance();
    EditText communityNameEditText,communityDescriptionEditText;
    public PostFragment() {
        // Required empty public constructor
    }
    Dialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        if (userInstance.getRoleid() == 1) {
            view = inflater.inflate(R.layout.fragment_student_post, container, false);
            TextView chooseCommunityTextView = view.findViewById(R.id.chooseCommunity);
            chooseCommunityTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog = new Dialog(requireContext());
                    dialog.setContentView(R.layout.community_spinner);
                    dialog.getWindow().setLayout(1050, 2000);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    EditText communitySearch = dialog.findViewById(R.id.communitySearch);
                    ListView communityList = dialog.findViewById(R.id.communityList);

                    FirebaseFirestore.getInstance().collection("Community")
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                List<Community> communities = new ArrayList<>();
                                List<String> communityNames = new ArrayList<>();

                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Community community = document.toObject(Community.class);
                                    communities.add(community);
                                    communityNames.add(community.getTitle());
                                    Log.d("Community Names", "Number of communities: " + communityNames);
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                        requireContext(),
                                        android.R.layout.simple_dropdown_item_1line,
                                        communityNames
                                );
                                communityList.setAdapter(adapter);

                                communitySearch.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        adapter.getFilter().filter(s);
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {}
                                });

                                communityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        // when item selected from list
                                        // set selected item on textView
                                        chooseCommunityTextView.setText(adapter.getItem(position));

                                        // Dismiss dialog
                                        if (dialog != null && dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                    }
                                });
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure to fetch communities from Firebase
                                Log.e("Fetch Communities", "Error getting communities", e);
                            });
                }
            });

            return view;
        }else if (userInstance.getRoleid() == 2) {
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





