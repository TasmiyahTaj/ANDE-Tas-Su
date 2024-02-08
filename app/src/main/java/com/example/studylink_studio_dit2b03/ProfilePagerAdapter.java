package com.example.studylink_studio_dit2b03;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ProfilePagerAdapter extends FragmentPagerAdapter {
    private String tutorId;
    public ProfilePagerAdapter(FragmentManager fm, String tutorId)
    {
        super(fm);
        this.tutorId = tutorId;
    }


    @Override
    public Fragment getItem(int position) {
        // Return the corresponding fragment for each tab
        switch (position) {
            case 0:
                // Pass the tutorId to the CommunityFragment
                CommunityFragment communityFragment = new CommunityFragment();
          Bundle args = new Bundle();
                args.putString("tutorId", tutorId);
                communityFragment.setArguments(args);
                return communityFragment;
            case 1:
                // Pass the tutorId to the NotesFragment
                NotesForProfileFragment notesFragment = new NotesForProfileFragment();
                args = new Bundle();
                args.putString("tutorId", tutorId);
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
                return "Community";
            case 1:
                return "Notes";
            default:
                return null;
        }
    }
}
