package com.example.studylink_studio_dit2b03;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

public class CardDialogFragment extends DialogFragment {

    private Stripe stripe;
    private CardInputWidget cardInputWidget;

    public CardDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_input_dialog, container, false);
        cardInputWidget = view.findViewById(R.id.cardInputWidget);

        stripe = new Stripe(requireContext(), PaymentConfiguration.getInstance(requireContext()).getPublishableKey());

        view.findViewById(R.id.btnConfirmPayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Confirm the payment
                PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
                if (params != null) {
                    ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                            .createWithPaymentMethodCreateParams(params, "your_client_secret");
                    stripe.confirmPayment(requireActivity(), confirmParams,
                            new ApiResultCallback<PaymentIntentResult>() {
                                @Override
                                public void onSuccess(@NonNull PaymentIntentResult result) {
                                    // Payment succeeded
                                    Toast.makeText(requireContext(), "Payment successful!", Toast.LENGTH_SHORT).show();
                                    dismiss(); // Dismiss the dialog
                                }

                                @Override
                                public void onError(@NonNull Exception e) {
                                    // Payment failed
                                    Toast.makeText(requireContext(), "Payment failed: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }.toString());
                } else {
                    Toast.makeText(requireContext(), "Invalid payment details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle("Enter Card Details");
    }
}
