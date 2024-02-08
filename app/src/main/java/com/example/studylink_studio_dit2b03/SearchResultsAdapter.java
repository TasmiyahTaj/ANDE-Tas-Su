package com.example.studylink_studio_dit2b03;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private List<String> searchResults;

    public SearchResultsAdapter(List<String> searchResults) {
        this.searchResults = searchResults;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String result = searchResults.get(position);
        holder.bind(result);
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewResult;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //textViewResult = itemView.findViewById(R.id.textViewResult);
        }

        public void bind(String result) {
            textViewResult.setText(result);
        }
    }
}
