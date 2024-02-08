package com.example.studylink_studio_dit2b03;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {
    private List<Community> communities;
    private OnCommunityItemClickListener onCommunityItemClickListener;

    public CommunityAdapter(List<Community> communities) {
        this.communities = communities != null ? communities : new ArrayList<>();
    }

    public void setCommunities(List<Community> communities) {
        this.communities = communities;
        notifyDataSetChanged();
    }

    // Define the interface for the click listener
    public interface OnCommunityItemClickListener {
        void onCommunityItemClick(Community community);
    }

    // Set the listener from the outside
    public void setOnCommunityItemClickListener(OnCommunityItemClickListener listener) {
        this.onCommunityItemClickListener = listener;
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_item, parent, false);
        return new CommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        Community community = communities.get(position);
        holder.communityNameTextView.setText(community.getTitle());

        holder.itemView.setOnClickListener(v -> {
            if (onCommunityItemClickListener != null) {
                onCommunityItemClickListener.onCommunityItemClick(community);
            }
        });
    }

    @Override
    public int getItemCount() {
        return communities.size();
    }

    static class CommunityViewHolder extends RecyclerView.ViewHolder {
        TextView communityNameTextView;

        CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            communityNameTextView = itemView.findViewById(R.id.communityNameTextView);
            // Initialize other views as needed
        }
    }
}
