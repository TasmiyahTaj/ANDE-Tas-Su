package com.example.studylink_studio_dit2b03;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CommunityAdapterSearch extends RecyclerView.Adapter<CommunityAdapterSearch.CommunityViewHolder> {
    private List<Community> communityList;

    // Constructor
    public CommunityAdapterSearch(List<Community> communityList) {
        this.communityList = communityList;
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_search, parent, false);
        return new CommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        Community community = communityList.get(position);
        holder.titleTextView.setText("Title: " + community.getTitle());
        holder.memberCountTextView.setText("Member Count: " + community.getMemberCount());
        getTutorName(community.getCreatorId(), holder.creatorNameTextView);
    }

    @Override
    public int getItemCount() {
        return communityList.size();
    }

    // ViewHolder class
    public static class CommunityViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView memberCountTextView;
        TextView creatorNameTextView;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.community_title_text_view);
            memberCountTextView = itemView.findViewById(R.id.member_count_text_view);
            creatorNameTextView = itemView.findViewById(R.id.creator_name_text_view);
        }
    }

    private void getTutorName(String creatorId, TextView creatorNameTextView) {
        FirebaseFirestore.getInstance()
                .collection("Tutor")
                .document(creatorId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String tutorName = documentSnapshot.getString("username");
                        creatorNameTextView.setText("Tutor: " + tutorName);
                    } else {
                        creatorNameTextView.setText("Creator not found");
                    }
                })
                .addOnFailureListener(e -> {
                    creatorNameTextView.setText("Error getting tutor name");
                });
    }

    public void updateCommunityList(List<Community> newCommunityList) {
        communityList.clear();
        communityList.addAll(newCommunityList);
        notifyDataSetChanged();
    }
}
