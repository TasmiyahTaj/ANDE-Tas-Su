package com.example.studylink_studio_dit2b03;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class NoteDetailFragment extends Fragment {

    public NoteDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_detail, container, false);

        // Retrieve the Note object from the arguments
        Bundle args = getArguments();
        if (args != null) {
            Note note = (Note) args.getSerializable("note");
            if (note != null) {
                // Populate the UI with the details of the Note
                TextView titleTextView = view.findViewById(R.id.titleTextView);
                TextView contentTextView = view.findViewById(R.id.contentTextView);
                TextView priceTextView = view.findViewById(R.id.priceTextView);

                titleTextView.setText(note.getTitle());
                contentTextView.setText(note.getContent());
                priceTextView.setText(String.valueOf(note.getPrice())); // Assuming price is a double
            }
        }

        return view;
    }
}
