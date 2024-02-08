package com.example.studylink_studio_dit2b03;

import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
                        // Initiate payment process
                        CardDialogFragment cardDialogFragment = new CardDialogFragment();
                        cardDialogFragment.show(getParentFragmentManager(), "cardInputDialog");


                    }
                });
            }
        }

        return view;
    }
    private void initiatePayment(Note note) {
        // Create PaymentMethodCreateParams from card input (assuming you have a card input widget)
        CardInputWidget cardInputWidget = requireView().findViewById(R.id.cardInputWidget);
        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
        if (params != null) {
            // Confirm the Payment Intent with the created payment method
            ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                    .createWithPaymentMethodCreateParams(params, "your_client_secret");
            stripe.confirmPayment(requireActivity(), confirmParams);
        }
    }

}
