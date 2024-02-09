package com.example.studylink_studio_dit2b03;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserViewHolder extends RecyclerView.ViewHolder {
    public TextView usernameTextView;
    public TextView roleTextView;
    public ImageView profileImageView;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        usernameTextView = itemView.findViewById(R.id.username_text_view);
        roleTextView = itemView.findViewById(R.id.role_text_view);
        profileImageView = itemView.findViewById(R.id.profile_image);
    }
}
