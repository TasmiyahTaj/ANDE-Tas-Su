package com.example.studylink_studio_dit2b03;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class NotesFragmentpfp extends Fragment {
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_fragmentpfp, container, false);

        // Initialize RecyclerView and adapter
        recyclerView = view.findViewById(R.id.recyclerViewpfp);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noteAdapter = new NoteAdapter();
        recyclerView.setAdapter(noteAdapter);

        // Set click listener for the NoteAdapter
        noteAdapter.setOnNoteItemClickListener(note -> {
            // Handle item click, navigate to NoteDetailFragment
            navigateToNoteDetail(note);
        });

        Bundle args = getArguments();
        if (args != null) {
            String tutorId = args.getString("tutorId");
            Log.d("userID", tutorId);
            new NotesFragmentpfp.FetchNotesTask().execute(tutorId);
        } else {
            Log.d("communityid", "no data");
        }

        return view;
    }


    // Code for navigating to NoteDetailFragment in NotesFragment
    private void navigateToNoteDetail(Note note) {
        NoteDetailFragment noteDetailFragment = new NoteDetailFragment();

        // Pass the clicked note to NoteDetailFragment
        Bundle args = new Bundle();
        args.putSerializable("note", note);
        noteDetailFragment.setArguments(args);

        // Use FragmentTransaction to replace the current fragment with NoteDetailFragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, noteDetailFragment)  // Update the id here
                .addToBackStack(null)
                .commit();
    }

    private class FetchNotesTask extends AsyncTask<String, Void, List<Note>> {

        @Override
        protected List<Note> doInBackground(String... params) {
            String tutorId = params[0];

            // Fetch notes data from Firestore
            return fetchNotesDataFromFirestore(tutorId);
        }

        @Override
        protected void onPostExecute(List<Note> notes) {
            // Update your UI with the fetched notes
            // This runs on the UI thread

            if (noteAdapter != null) {
                noteAdapter.setNotes(notes);
            } else {
                Log.d("Fetched Notes", "Notes list is null");
            }
        }

        private List<Note> fetchNotesDataFromFirestore(String tutorId) {
            List<Note> notes = new ArrayList<>();

            // Initialize Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Query the "Note" collection for documents matching the communityID
            db.collection("Note")
                    .whereEqualTo("teacherId", tutorId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Retrieve data from each document and create a Note object
                                Note note = document.toObject(Note.class);
                                notes.add(note);
                                Log.d("FetchNotesTask", "no error");
                            }
                            // Call onPostExecute to update the UI with the fetched data
                            onPostExecute(notes);
                        } else {
                            Log.d("FetchNotesTask", "Error getting documents: ", task.getException());
                        }
                    });

            return notes;
        }
    }
}