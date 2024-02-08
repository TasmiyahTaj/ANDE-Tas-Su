package com.example.studylink_studio_dit2b03;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studylink_studio_dit2b03.Community;

import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {

    private List<Community> communities;

    public void setCommunities(List<Community> communities) {
        this.communities = communities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community, parent, false);
        return new CommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        Community community = communities.get(position);
        holder.bind(community);
    }

    @Override
    public int getItemCount() {
        return communities != null ? communities.size() : 0;
    }

    public static class CommunityViewHolder extends RecyclerView.ViewHolder {

        private TextView communityNameTextView;
        private TextView communityDescriptionTextView;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            communityNameTextView = itemView.findViewById(R.id.communityNameTextView);
            communityDescriptionTextView = itemView.findViewById(R.id.communityDescriptionTextView);
        }

        public void bind(Community community) {
            communityNameTextView.setText(community.getTitle());
            communityDescriptionTextView.setText(community.getDescription());
        }
    }
}
