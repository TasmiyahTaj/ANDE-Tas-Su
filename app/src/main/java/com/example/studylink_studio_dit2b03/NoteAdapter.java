package com.example.studylink_studio_dit2b03;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<String> noteIDs = new ArrayList<>(); // Update this to match your data model

    // ViewHolder class
    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textNoteID;

        NoteViewHolder(View itemView) {
            super(itemView);
            textNoteID = itemView.findViewById(R.id.textNoteID);
            // Add more TextViews or UI elements as needed for your notes
        }
    }

    // onCreateViewHolder method
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    // onBindViewHolder method
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        String noteID = noteIDs.get(position);
        holder.textNoteID.setText(noteID);
        // Bind other data to UI elements here
    }

    // getItemCount method
    @Override
    public int getItemCount() {
        return noteIDs.size();
    }

    // Helper method to update data
    void setNoteIDs(List<String> noteIDs) {
        this.noteIDs = noteIDs;
        notifyDataSetChanged();
    }
}
