package com.example.studylink_studio_dit2b03;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView; // Import from androidx.appcompat.widget
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class SearchFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SearchView searchView;
    private static final String TAG = "SearchFragment";

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);
        searchView = view.findViewById(R.id.search_view);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupSearchView();

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        SearchPagerAdapter adapter = new SearchPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new SearchUsersFragment(), "Users");
        adapter.addFragment(new SearchCommunityFragment(), "Community");
        viewPager.setAdapter(adapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: Query submitted: " + query);

                int selectedTabIndex = tabLayout.getSelectedTabPosition();
                Fragment currentFragment = getChildFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + selectedTabIndex);

                if (currentFragment instanceof SearchUsersFragment) {
                    List<User> usersResult = ((SearchUsersFragment) currentFragment).performSearch(query);
                    // Update the UI with the search results in the SearchUsersFragment
                    ((SearchUsersFragment) currentFragment).updateSearchResults(usersResult);
                } else if (currentFragment instanceof SearchCommunityFragment) {
                    List<Community> communityResult = ((SearchCommunityFragment) currentFragment).performSearch(query);
                    // Update the UI with the search results in the SearchCommunityFragment
                    ((SearchCommunityFragment) currentFragment).updateSearchResults(communityResult);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Implement any filtering logic here if needed
                return false;
            }
        });
    }


}
