package com.example.studylink_studio_dit2b03;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class CommunityPagerAdapter extends FragmentPagerAdapter {

    public CommunityPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // Return the corresponding fragment for each tab
        switch (position) {
            case 0:
                return new QuestionsFragment();
            case 1:
                return new NotesFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Number of tabs
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Set tab titles
        switch (position) {
            case 0:
                return "Questions";
            case 1:
                return "Notes";
            default:
                return null;
        }
    }
}
