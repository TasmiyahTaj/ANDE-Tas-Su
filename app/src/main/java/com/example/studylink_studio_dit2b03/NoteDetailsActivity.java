package com.example.studylink_studio_dit2b03;

// NoteDetailsActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NoteDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        // Retrieve data from the intent
        Intent intent = getIntent();
        String noteTitle = intent.getStringExtra("noteTitle");
        String noteContent = intent.getStringExtra("noteContent");
        String notePrice = intent.getStringExtra("notePrice");

        // Find UI elements
        TextView textNoteTitle = findViewById(R.id.textNoteTitle);
        TextView textNoteContent = findViewById(R.id.textNoteContent);
        TextView textNotePrice = findViewById(R.id.textNotePrice);

        // Set data to UI elements
        textNoteTitle.setText(noteTitle);
        textNoteContent.setText(noteContent);
        textNotePrice.setText(notePrice);

        // Add more code to handle other details if needed
    }
}
