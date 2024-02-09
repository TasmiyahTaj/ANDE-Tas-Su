package com.example.studylink_studio_dit2b03;

import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class NoteDetailFragment extends Fragment {
    private Stripe stripe;
    public NoteDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_detail, container, false);
        PaymentConfiguration.init(getContext(), "pk_test_51OhDNSL9SuBC6TQROKFxV8cjk80pxqaeSFyJ7oxeJh7xC7yTk0q4lQ9gLeStyFlCKu2yQx9J7NqmDU3R8wIpHYxU00nlxMVmTK");
        stripe = new Stripe(requireContext(), PaymentConfiguration.getInstance(requireContext()).getPublishableKey());

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
                priceTextView.setText("Price: " +String.valueOf(note.getPrice())); // Assuming price is a double

                Button buyButton = view.findViewById(R.id.btnBuy);
                CardInputWidget cardInputWidget = view.findViewById(R.id.cardInputWidget); // Get reference to CardInputWidget

                buyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Create and show a dialog to collect payment details
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle("Enter Payment Details");

                        // Inflate the dialog layout
                        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_payment_details, null);
                        builder.setView(dialogView);

                        // Initialize views in the dialog layout
                        EditText accountNumberEditText = dialogView.findViewById(R.id.account_number_edit_text);
                        EditText csvEditText = dialogView.findViewById(R.id.csv_edit_text);
                        EditText expiryDateEditText = dialogView.findViewById(R.id.expiry_date_edit_text);

                        accountNumberEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {}

                            @Override
                            public void afterTextChanged(Editable s) {
                                String input = s.toString().replace(" ", ""); // Remove spaces
                                if (input.length() > 0) {
                                    StringBuilder formatted = new StringBuilder();
                                    for (int i = 0; i < input.length(); i++) {
                                        if (i > 0 && i % 4 == 0) {
                                            formatted.append(" "); // Insert space after every 4 characters
                                        }
                                        formatted.append(input.charAt(i));
                                    }
                                    accountNumberEditText.removeTextChangedListener(this); // Prevent infinite loop
                                    accountNumberEditText.setText(formatted.toString());
                                    accountNumberEditText.setSelection(formatted.length());
                                    accountNumberEditText.addTextChangedListener(this); // Add back TextWatcher
                                }
                            }
                        });

// TextWatcher for formatting CSV
                        csvEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {}

                            @Override
                            public void afterTextChanged(Editable s) {
                                String input = s.toString().replace(" ", ""); // Remove spaces
                                if (input.length() > 0) {
                                    StringBuilder formatted = new StringBuilder();
                                    for (int i = 0; i < input.length(); i++) {
                                        if (i > 0 && i % 3 == 0) {
                                            formatted.append(" "); // Insert space after every 3 characters
                                        }
                                        formatted.append(input.charAt(i));
                                    }
                                    csvEditText.removeTextChangedListener(this); // Prevent infinite loop
                                    csvEditText.setText(formatted.toString());
                                    csvEditText.setSelection(formatted.length());
                                    csvEditText.addTextChangedListener(this); // Add back TextWatcher
                                }
                            }

                        });

                        expiryDateEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {}

                            @Override
                            public void afterTextChanged(Editable s) {
                                String input = s.toString().replace("/", ""); // Remove slashes
                                if (input.length() > 0) {
                                    StringBuilder formatted = new StringBuilder();
                                    for (int i = 0; i < input.length(); i++) {
                                        if (i > 1 && i % 2 == 0) {
                                            formatted.append("/"); // Insert slash after every 2 characters
                                        }
                                        formatted.append(input.charAt(i));
                                    }
                                    expiryDateEditText.removeTextChangedListener(this); // Prevent infinite loop
                                    expiryDateEditText.setText(formatted.toString());
                                    expiryDateEditText.setSelection(formatted.length());
                                    expiryDateEditText.addTextChangedListener(this); // Add back TextWatcher

                                    // Validate month and year when both are entered
                                    String[] parts = formatted.toString().split("/");
                                    if (parts.length == 2 && parts[0].length() == 2 && parts[1].length() == 2) {
                                        try {
                                            int month = Integer.parseInt(parts[0]);
                                            int year = Integer.parseInt(parts[1]);
                                            if (month < 1 || month > 12 || year < 26 || year > 39) {
                                                // Invalid month or year, clear the input
                                                Log.e("Validation", "Invalid month or year: " + month + "/" + year);
                                                Toast.makeText(requireContext(), "Invalid expiry date", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d("Validation", "Valid expiry date: " + month + "/" + year);
                                            }
                                        } catch (Exception e) {
                                            // Exception occurred, clear the input
                                            Log.e("Validation", "Exception occurred: " + e.getMessage());
                                            Toast.makeText(requireContext(), "Invalid expiry date", Toast.LENGTH_SHORT).show();
                                            expiryDateEditText.setText("");
                                        }
                                    }
                                }
                            }
                        });





                        // Set up the buttons
                        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get the entered payment details
                                String accountNumber = accountNumberEditText.getText().toString().trim().replace(" ", ""); // Remove spaces
                                String csv = csvEditText.getText().toString().trim();
                                String expiryDate = expiryDateEditText.getText().toString().trim();

                                // Store the account number locally (you can use SharedPreferences for this)
                                // For example:
                                SharedPreferences.Editor editor = requireContext().getSharedPreferences("payment_prefs", Context.MODE_PRIVATE).edit();
                                editor.putString("account_number", accountNumber);
                                editor.apply();

                                // Navigate to the PaymentFragment
                                navigateToPaymentFragment(note.getTeacherId(), note.getPrice(), note.getTitle(), note.getAttachmentUrl(), note.getNoteId());

                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        // Show the dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });

            }
        }

        return view;
    }
    private void navigateToPaymentFragment(String teacherId, double price, String noteTitle,String noteAttachmentUrl,String noteId) {
        // Create a bundle to pass data to the PaymentFragment
        Bundle bundle = new Bundle();
        bundle.putString("teacher_id", teacherId);
        bundle.putDouble("price", price);
        bundle.putString("note_title", noteTitle);
        bundle.putString("note_pdf", noteAttachmentUrl);
        bundle.putString("noteId", noteAttachmentUrl);
        // Create an instance of the PaymentFragment and set the arguments
        PaymentFragment paymentFragment = new PaymentFragment();
        paymentFragment.setArguments(bundle);

        // Navigate to the PaymentFragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.container, paymentFragment)
                .addToBackStack(null)  // Optional: Add to back stack to enable back navigation
                .commit();
    }



}
