package com.example.studylink_studio_dit2b03;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class CommunityPagerAdapter extends FragmentPagerAdapter {
    private String communityID;
    public CommunityPagerAdapter(FragmentManager fm, String communityID)
    {
        super(fm);
        this.communityID = communityID;
    }

    @Override
    public Fragment getItem(int position) {
        // Return the corresponding fragment for each tab
        switch (position) {
            case 0:
                return new QuestionsFragment();
            case 1:
                // Pass the communityID to NotesFragment
                NotesFragment notesFragment = new NotesFragment();
                Bundle args = new Bundle();
                args.putString("communityID", communityID);
                notesFragment.setArguments(args);
                return notesFragment;
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