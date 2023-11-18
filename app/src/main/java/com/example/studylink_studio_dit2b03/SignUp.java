package com.example.studylink_studio_dit2b03;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    int token = 0;
    private EditText username,email;
 private String sUsername,sEmail;
    String msg = "From SignUp Class : ";
    private String institution;

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

       username= (EditText)findViewById(R.id.txtUsername);
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
        System.out.println("from generatedToken "+token);
        return token;
    }
private void userInstitution(){
        sUsername=username.getText().toString();
    AlertDialog.Builder getInstitution = new AlertDialog.Builder(this);
    final Spinner institutionDropdown = new Spinner(this);
    ArrayAdapter<String> insitutionArr = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
            new String[]{"Singapore Polytechnic", "National University Of Singapore", "Ngee Ann Polytechnic","Nanyang Polytechnic","Nanyang Technological University"});
    institutionDropdown.setAdapter(insitutionArr);
    getInstitution.setView(institutionDropdown)
            .setTitle("Hi " + sUsername + ", which institution are you from?")
            .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Store the selected institution
                     institution = institutionDropdown.getSelectedItem().toString();
                    // Show the next dialog
                    userCourses();
                }
            })
            .setNegativeButton("Back", null)
            .show();
}
public void userCourses(){
    AlertDialog.Builder courseAlert = new AlertDialog.Builder(this);
    final Spinner courseSpinner = new Spinner(this);

    // Set up the spinner with your course options
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
            new String[]{"Information Technology", "Robotics and mechatronics", "Aerospace","Electrical Engineering","Information systems"});
    courseSpinner.setAdapter(adapter);

    courseAlert.setView(courseSpinner)
            .setTitle("Hi " + sUsername + ",Which course are you from?")
            .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Store the selected course
                    String course = courseSpinner.getSelectedItem().toString();
                    // Now you have all the information you need, you can proceed to the next activity
                    Intent i = new Intent(SignUp.this, MainActivity.class);
                    // Pass the collected information to the next activity if needed
                    sEmail=email.getText().toString();
                    i.putExtra("username", sUsername);
                    i.putExtra("institution", institution);
                    i.putExtra("email",sEmail);
                    i.putExtra("course", course);
                    startActivity(i);
                }
            })
            .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Show the previous dialog
                    userInstitution();
                }
            })
            .show();
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
                            userInstitution();
//                            Intent intent = new Intent(SignUp.this, MainActivity.class);
//                            startActivity(intent);



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
