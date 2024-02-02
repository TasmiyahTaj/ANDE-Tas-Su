package com.example.studylink_studio_dit2b03;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
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

public class CommunityPageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_page, container, false);

        // Retrieve data from arguments
        Bundle args = getArguments();
        if (args != null) {
            String communityID = args.getString("communityID");
            //retrieve notes of that particular community
            //retrieve community data
            retrieveCommunityData(communityID);


            // Find UI elements
            TabLayout tabLayout = view.findViewById(R.id.tabLayout);
            ViewPager viewPager = view.findViewById(R.id.viewPager);

            // Set up ViewPager and attach adapter
            CommunityPagerAdapter pagerAdapter = new CommunityPagerAdapter(getChildFragmentManager(), communityID);
            viewPager.setAdapter(pagerAdapter);

            // Link the TabLayout to the ViewPager
            tabLayout.setupWithViewPager(viewPager);
        }
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
                    Community communityData = document.toObject(Community.class);

                    if (communityData != null) {
                        // Now you can use the communityData as needed

                        TextView communityNameTextView = getView().findViewById(R.id.communityTitleTextView);
                        TextView communityDescriptionTextView = getView().findViewById(R.id.communityDescriptionTextView);
                        TextView communityMemberCountTextView = getView().findViewById(R.id.memberCountTextView);
                        communityNameTextView.setText(communityData.getTitle());
                        communityDescriptionTextView.setText(communityData.getDescription());
                        communityMemberCountTextView.setText("Member :"+ communityData.getMemberCount());


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


}