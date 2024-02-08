package com.example.studylink_studio_dit2b03;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;




import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchViewHolder> {

    private Context context;
    private List<SearchItem> searchItemList;

    public SearchResultsAdapter(Context context, List<SearchItem> searchItemList) {
        this.context = context;
        this.searchItemList = searchItemList;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item_layout, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        SearchItem item = searchItemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return searchItemList.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);

            //textViewResult = itemView.findViewById(R.id.textViewResult);

        }

        public void bind(SearchItem item) {
            titleTextView.setText(item.getTitle());
            descriptionTextView.setText(item.getDescription());
        }
    }
}
