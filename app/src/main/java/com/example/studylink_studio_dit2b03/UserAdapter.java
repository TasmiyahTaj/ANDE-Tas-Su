package com.example.studylink_studio_dit2b03;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private List<User> userList;
    private FragmentManager fragmentManager;
    User userInstance = User.getInstance();

    public UserAdapter(List<User> userList, FragmentManager fragmentManager) {
        this.userList = userList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    public void updateUserList(List<User> newUserList) {
        userList.clear();
        userList.addAll(newUserList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.usernameTextView.setText(user.getUsername());
        holder.roleTextView.setText("Role: " + (user.getRoleid() == 1 ? "Student" : "Tutor"));

        // Load profile picture using Picasso
        String profilePicUrl = user.getProfilePicUrl();
        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Picasso.get().load(profilePicUrl).into(holder.profileImageView);
        } else {
            // Load default profile picture
            holder.profileImageView.setImageResource(R.drawable.profile_pic);
        }

        // Set OnClickListener for the item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to ProfileFragment with user ID
                navigateToProfileFragment(user.getUserid(),user.getRoleid(),profilePicUrl);
            }
        });
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }


    private void navigateToProfileFragment(String userId, int roleId,String profilePicUrl) {
        Log.d("UserAdapter", "userId: " + userId);
        String currentUserId = userInstance.getUserid();
        if (userId != null && userId.equals(currentUserId)) {
            // Navigate to ProfileFragment
            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);
            ProfileFragment profileFragment = new ProfileFragment();
            profileFragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, profileFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            // Navigate to OtherUserFragment
            Bundle bundle = new Bundle();
            bundle.putString("searchedUserID", userId);
            bundle.putInt("searchedRoleID", roleId);
            bundle.putString("searchedProfilePicUrl", profilePicUrl);
            OtherUser otherUserFragment = new OtherUser();
            otherUserFragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, otherUserFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

}
