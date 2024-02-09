package com.example.studylink_studio_dit2b03;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentFragment extends Fragment {

    private TextView tutorNameTextView, tutorAccountNumberTextView, currentUserNameTextView, currentUserAccountNumberTextView, noteNameTextView;
    private Button confirmButton, cancelButton;
User userInstance=User.getInstance();
    private static final String SHARED_PREF_NAME = "payment_prefs"; // Define the SharedPreferences file name

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

                // Push payment details to Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                PaymentDetails paymentDetails = new PaymentDetails(
                        tutorId, userInstance.getUserid(), price, noteTitle, noteAttachment, currentUserAccountNumber,noteId);
                db.collection("Payments").add(paymentDetails)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // Payment details added successfully
                                String paymentId = documentReference.getId();
                                // Handle further actions, such as downloading the note
                                // You can navigate to another fragment or activity if needed
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
}
