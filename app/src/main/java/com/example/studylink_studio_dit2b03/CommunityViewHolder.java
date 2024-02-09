package com.example.studylink_studio_dit2b03;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommunityViewHolder extends RecyclerView.ViewHolder {
    TextView titleTextView;
    TextView memberCountTextView;
    TextView creatorIdTextView;

    public CommunityViewHolder(@NonNull View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.community_title_text_view);
        memberCountTextView = itemView.findViewById(R.id.member_count_text_view);
        creatorIdTextView = itemView.findViewById(R.id.creator_name_text_view);
    }
}

