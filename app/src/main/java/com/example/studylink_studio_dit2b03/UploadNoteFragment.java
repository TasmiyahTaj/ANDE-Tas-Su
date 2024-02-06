package com.example.studylink_studio_dit2b03;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class UploadNoteFragment extends Fragment {
    User userInstance = User.getInstance();
    private TextView chooseCommunityTextView;
    private EditText title, content, price;
    private ImageView attachedImage;
    private Uri selectedImageUri;
    private Button buttonRemoveImage;
    private ImageView uploadPdfImageView;
    private static final int PICK_PDF_REQUEST = 1;
    private Uri pdfUri = null;

    public UploadNoteFragment() {
        // Required empty public constructor
    }
    Dialog dialogCommunity;
    Note note = new Note();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_note, container, false);

        uploadPdfImageView = view.findViewById(R.id.uploadpdf);
        uploadPdfImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the file picker to choose a PDF
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, PICK_PDF_REQUEST);
            }
        });
        chooseCommunityTextView = view.findViewById(R.id.chooseCommunity);
        // Check if the user has created any communities
        FirebaseFirestore.getInstance().collection("Community")
                .whereEqualTo("creatorId", userInstance.getUserid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Community> communities = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Community community = document.toObject(Community.class);
                        communities.add(community);
                    }

                    if (communities.isEmpty()) {
                        // User has no communities, show a message or handle as needed
                        // For example, display a toast or set a TextView with a message
                        showMessageAndNavigateBack();
                    } else {
                        // User has communities, set up the click listener
                        chooseCommunityTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openCommunityDialog();
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure to fetch communities from Firebase
                    Log.e("Fetch Communities", "Error getting communities", e);
                });


        // Handle the Post button click
        Button postButton = view.findViewById(R.id.buttonPost);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = view.findViewById(R.id.editTextTitle);
                content = view.findViewById(R.id.editTextContent);
                price = view.findViewById(R.id.editTextPrice);
                // Check if a PDF is selected
                if (pdfUri != null) {
                    // Upload the PDF
                    uploadPDF();

                } else {
                    Toast.makeText(requireContext(), "Please choose a PDF file", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    // Method to upload the selected PDF to Firebase Storage
    private void uploadPDF() {
        // Show a progress dialog
        ProgressDialog dialog = new ProgressDialog(requireContext());
        dialog.setMessage("Uploading PDF");
        dialog.setCancelable(false);
        dialog.show();

        // Create a storage reference with "notes" as the folder
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("notes");
        String fileName = "pdf_" + System.currentTimeMillis() + ".pdf";
        StorageReference pdfReference = storageReference.child(fileName);

        // Upload the PDF file
        pdfReference.putFile(pdfUri)
                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.storage.UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            // PDF upload successful, get the download URL
                            pdfReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUrl = task.getResult();
                                        String noteTitle = title.getText().toString();
                                        String noteContent = content.getText().toString();
                                        Double notePrice = Double.parseDouble(price.getText().toString());
                                        if (!noteTitle.isEmpty() && !noteContent.isEmpty()) {
                                            note.setTitle(noteTitle);
                                            note.setContent(noteContent);
                                            note.setPrice(notePrice);
                                            note.setTeacherId(userInstance.getUserid());
                                            note.setAttachmentUrl(task.getResult().toString());

                                            FirebaseFirestore.getInstance().collection("Note")
                                                    .add(note)
                                                    .addOnSuccessListener(documentReference -> {
                                                        String noteId = documentReference.getId();
                                                        note.setNoteId(noteId);

                                                        // Set the community document with the updated community ID
                                                        documentReference.set(note)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    navigateToCommunityDetails(note.getCommunityId());
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    // Handle failure to update the community document with the community ID
                                                                });
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Handle failure to add the community document
                                                    });
                                        }

                                        //Toast.makeText(requireContext(), "PDF Uploaded successfully. Download URL: " + downloadUrl, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(requireContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                    }
                                    // Dismiss the progress dialog
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            // PDF upload failed
                            Toast.makeText(requireContext(), "Failed to upload PDF", Toast.LENGTH_SHORT).show();
                            // Dismiss the progress dialog
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void showMessageAndNavigateBack() {
        // Show a message to the user indicating that they should create a community first
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("No Community Found");
        builder.setMessage("You haven't created any communities yet. Create a community first.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Navigate back to PostFragment
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        builder.show();
    }

    private void openCommunityDialog() {
        dialogCommunity = new Dialog(requireContext());
        dialogCommunity.setContentView(R.layout.community_spinner);
        dialogCommunity.getWindow().setLayout(1050, 2000);
        dialogCommunity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCommunity.show();

        EditText communitySearch = dialogCommunity.findViewById(R.id.communitySearch);
        ListView communityList = dialogCommunity.findViewById(R.id.communityList);

        FirebaseFirestore.getInstance().collection("Community")
                .whereEqualTo("creatorId", userInstance.getUserid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Community> communities = new ArrayList<>();
                    List<String> communityNames = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Community community = document.toObject(Community.class);
                        communities.add(community);
                        communityNames.add(community.getTitle());
                        Log.d("Community Names", "Number of communities: " + communityNames);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            R.layout.textview_derfault,
                            communityNames
                    );
                    communityList.setAdapter(adapter);

                    communitySearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            adapter.getFilter().filter(s);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {}
                    });

                    communityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // when item selected from list
                            // set selected item on textView
                            Community selectedCommunity = communities.get(position);

                            // Get the community ID from the selected community
                            String selectedCommunityId = selectedCommunity.getCommunityId();
                            note.setCommunityId(selectedCommunityId);

                            // Set the community name to the TextView
                            chooseCommunityTextView.setText(adapter.getItem(position));



                            // Dismiss dialog
                            if (dialogCommunity != null && dialogCommunity.isShowing()) {
                                dialogCommunity.dismiss();
                            }
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle failure to fetch communities from Firebase
                    Log.e("Fetch Communities", "Error getting communities", e);
                });
    }


    // Handle the result from file picker
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            pdfUri = data.getData();
            // You can display a message or the selected file name if needed
            // Toast.makeText(requireContext(), "Selected PDF: " + pdfUri.getPath(), Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToCommunityDetails(String communityID){
        Bundle bundle = new Bundle();
        bundle.putString("communityID", communityID);

        // Create the destination fragment and set arguments
        Fragment communityFragment = new CommunityPageFragment();
        communityFragment.setArguments(bundle);

        // Replace the current fragment with the destination fragment
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.container, communityFragment).addToBackStack(null).commit();


    }
}