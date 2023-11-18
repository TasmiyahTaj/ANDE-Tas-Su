package com.example.studylink_studio_dit2b03;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    int token = 0;
    private String dUsername;
    TextView rUsername;
    String msg = "From SignUp Class : ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        // Example: Connecting verifyToken method to a button
        Button signUpButton = findViewById(R.id.buttonSignUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verifyToken();
            }
        });

        TextView loginTextView = findViewById(R.id.LoginFromSignUp);
        loginTextView.setOnClickListener(this);

        rUsername=(TextView) findViewById(R.id.txtUsername);
        Bundle getData = getIntent().getExtras();
        if(getData !=null){
            dUsername=getData.getString("rUsername");
            rUsername.setText(dUsername);
        }else{
            Toast.makeText(getApplicationContext(),"Failed to get Data",Toast.LENGTH_LONG).show();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.LoginFromSignUp:
                Intent i = new Intent(this, Login.class);
                startActivity(i);
                break;
        }
    }

    private int generateToken() {
        token = (int) (Math.random() * 900000) + 100000;
        System.out.println("from generatedToken"+token);
        return token;
    }

    private void verifyToken() {
        AlertDialog.Builder tokenAlert = new AlertDialog.Builder(this);
        final EditText userInput = new EditText(this);

        int generatedToken = generateToken();
        tokenAlert.setMessage("Enter Verification token").setTitle("Verification Token")
                .setView(userInput)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = userInput.getText().toString();
                        if (input.equals(String.valueOf(generatedToken))) {
                            Log.d(msg, "Successful");
                            Intent intent = new Intent(SignUp.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d(msg, "Wrong token");
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton("Resend", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        verifyToken();
                    }
                })
                .setCancelable(true);

        AlertDialog dialog = tokenAlert.create();
        dialog.show();
    }
}
