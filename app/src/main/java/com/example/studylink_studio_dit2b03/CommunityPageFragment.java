package com.example.studylink_studio_dit2b03;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class CommunityPageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_page, container, false);

        // Retrieve data from arguments
        Bundle args = getArguments();
        if (args != null) {
            String communityTitle = args.getString("communityTitle");
            String communityDescription = args.getString("communityDescription");
            int memberCount = args.getInt("memberCount");

            // Find UI elements
            TextView titleTextView = view.findViewById(R.id.communityTitleTextView);
            TextView descriptionTextView = view.findViewById(R.id.communityDescriptionTextView);
            TextView memberCountTextView = view.findViewById(R.id.memberCountTextView);

            // Update UI with data
            titleTextView.setText(communityTitle);
            descriptionTextView.setText(communityDescription);
            memberCountTextView.setText("Members: " + memberCount);
        }


        // Find UI elements
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.viewPager);

        // Set up ViewPager and attach adapter
        CommunityPagerAdapter pagerAdapter = new CommunityPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        // Link the TabLayout to the ViewPager
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
