package com.example.studylink_studio_dit2b03;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentFragment extends Fragment {
    private static final int REQUEST_CODE_BROWSER = 100;
    private TextView tutorNameTextView, tutorAccountNumberTextView, currentUserNameTextView, currentUserAccountNumberTextView, noteNameTextView;
    private Button confirmButton, cancelButton;
    User userInstance = User.getInstance();
    private static final String SHARED_PREF_NAME = "payment_prefs"; // Define the SharedPreferences file name
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_fragment, container, false);

        // Initialize views
        tutorNameTextView = view.findViewById(R.id.tutor_name_text_view);
        tutorAccountNumberTextView = view.findViewById(R.id.tutor_account_number_text_view);
        currentUserNameTextView = view.findViewById(R.id.current_user_name_text_view);
        currentUserAccountNumberTextView = view.findViewById(R.id.current_user_account_number_text_view);
        noteNameTextView = view.findViewById(R.id.note_name_text_view);
        confirmButton = view.findViewById(R.id.confirm_button);
        cancelButton = view.findViewById(R.id.cancel_button);

        // Retrieve data from fragment arguments
        Bundle args = getArguments();
        if (args != null) {
            String tutorId = args.getString("teacher_id");
            double price = args.getDouble("price");
            String noteTitle = args.getString("note_title");
            String noteAttachment = args.getString("note_pdf");

            // Fetch tutor details from Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Tutor").document(tutorId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String tutorName = documentSnapshot.getString("username");
                                long tutorAccountNumber = documentSnapshot.getLong("account");
                                String acc = String.valueOf(tutorAccountNumber); // Convert long to String

                                // Set retrieved tutor data to respective TextViews
                                tutorNameTextView.setText(tutorName);
                                tutorAccountNumberTextView.setText(acc);
                            }
                        }
                    });

            TextView priceTextView = view.findViewById(R.id.price_text_view);
            priceTextView.setText("Price: " + String.valueOf(price));

            currentUserNameTextView.setText(userInstance.getUsername());
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String currentUserAccountNumber = sharedPreferences.getString("account_number", "");
            currentUserAccountNumberTextView.setText(currentUserAccountNumber);

            // Set note name
            noteNameTextView.setText(noteTitle);
        }

        // Set up onClickListeners for buttons
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve payment details
                String tutorId = args.getString("teacher_id");
                double price = args.getDouble("price");
                String noteTitle = args.getString("note_title");
                String noteAttachment = args.getString("note_pdf");
                String noteId = args.getString("noteId");
                String currentUserAccountNumber = currentUserAccountNumberTextView.getText().toString();

                // Check for permission before initiating download
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                } else {
                    // Permission is already granted, proceed with the download
                    performDownload(tutorId, price, noteTitle, noteAttachment, noteId, currentUserAccountNumber);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle cancel payment
                // Navigate back to the previous page
                requireActivity().onBackPressed();
            }
        });

        return view;
    }

    // Function to perform download after permission is granted
    // Inside your performDownload() method
    private void performDownload(String tutorId, double price, String noteTitle, String noteAttachment, String noteId, String currentUserAccountNumber) {
        // Push payment details to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        PaymentDetails paymentDetails = new PaymentDetails(
                tutorId, userInstance.getUserid(), price, noteTitle, noteAttachment, currentUserAccountNumber, noteId);
        db.collection("Payments").add(paymentDetails)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Payment details added successfully
                        String paymentId = documentReference.getId();
                        // Modify the noteAttachment URL if necessary to ensure it initiates a download
                        String modifiedNoteAttachmentUrl = modifyDownloadUrl(noteAttachment);
                        // Open the URL in a web browser
                        openUrlInBrowser(modifiedNoteAttachmentUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add payment details
                        Toast.makeText(requireContext(), "Failed to process payment", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to open URL in a web browser
    private void openUrlInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    // Method to initiate download
    private void downloadNoteAttachment(String noteAttachmentUrl) {
        // Create a DownloadManager.Request with the provided URL
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(noteAttachmentUrl));

        // Set the title and description of the download request (optional)
        request.setTitle("Note Attachment");
        request.setDescription("Downloading Note Attachment...");

        // Set the destination directory for the downloaded file
        request.setDestinationInExternalFilesDir(requireContext(), Environment.DIRECTORY_DOWNLOADS, "note.pdf");

        // Get the system's DownloadManager service
        DownloadManager downloadManager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);

        // Enqueue the download request and get a download ID
        long downloadId = downloadManager.enqueue(request);

        // You can optionally listen for the completion of the download using BroadcastReceiver
        // Implement BroadcastReceiver to handle download completion if needed
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with the download
                Bundle args = getArguments();
                if (args != null) {
                    String tutorId = args.getString("teacher_id");
                    double price = args.getDouble("price");
                    String noteTitle = args.getString("note_title");
                    String noteAttachment = args.getString("note_pdf");
                    String noteId = args.getString("noteId");
                    String currentUserAccountNumber = currentUserAccountNumberTextView.getText().toString();
                    performDownload(tutorId, price, noteTitle, noteAttachment, noteId, currentUserAccountNumber);
                }
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to modify the download URL if necessary
    private String modifyDownloadUrl(String originalUrl) {
        // You might need to modify the URL here based on your specific requirements
        // For example, you might need to append a download parameter or change the URL to a direct download link
        return originalUrl;
    }
}
