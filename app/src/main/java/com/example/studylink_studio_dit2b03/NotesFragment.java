package com.example.studylink_studio_dit2b03;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotesFragment extends Fragment {
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        // Initialize RecyclerView and adapter
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noteAdapter = new NoteAdapter();
        recyclerView.setAdapter(noteAdapter);

        Bundle args = getArguments();
        if (args != null) {
            String communityID = args.getString("communityID");
            Log.d("communityid",communityID );
            new FetchNotesTask().execute(communityID);
        }
        else{
            Log.d("communityid","no data" );
        }


        return view;
    }

    private class FetchNotesTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            String communityID = params[0];

            // Fetch notes data from Firestore
            return fetchNotesDataFromFirestore(communityID);
        }

        @Override
        protected void onPostExecute(List<String> noteIDs) {
            // Update your UI with the fetched note IDs
            // This runs on the UI thread

            if (noteAdapter != null) {
                noteAdapter.setNoteIDs(noteIDs);
            } else {
                Log.d("Fetched Note ID", "Note IDs list is null");
            }
        }


        private List<String> fetchNotesDataFromFirestore(String communityID) {
            List<String> noteIDs = new ArrayList<>();

            // Initialize Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Query the "Note" collection for documents matching the communityID
            db.collection("Note")
                    .whereEqualTo("communityId", communityID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Retrieve data from each document and add it to the notesData list
                                noteIDs.add(document.getString("content"));
                            }
                        } else {
                            Log.d("FetchNotesTask", "Error getting documents: ", task.getException());
                        }

                        // Call onPostExecute to update the UI with the fetched data
                        onPostExecute(noteIDs);
                    });

            return noteIDs;
        }
    }
}
