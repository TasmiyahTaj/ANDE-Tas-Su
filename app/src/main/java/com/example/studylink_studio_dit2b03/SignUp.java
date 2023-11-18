package com.example.studylink_studio_dit2b03;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
    private EditText username,email,password,cfmPwd;
 private String sUsername,sEmail,sPwd,sCfmPwd;
    String msg = "From SignUp Class : ";
    private String institution;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        TextView loginTextView = findViewById(R.id.LoginFromSignUp);
        loginTextView.setOnClickListener(this);

       username= (EditText)findViewById(R.id.txtUsernameSignUp);
        email= (EditText)findViewById(R.id.txtEmailSignUp);
        password= (EditText)findViewById(R.id.txtPwdRegister);
        cfmPwd=(EditText) findViewById(R.id.txtCfmPwdRegister);

    }
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                // Handle SignUp button click
                sUsername = username.getText().toString();
                sEmail = email.getText().toString();
                sPwd = password.getText().toString();
                sCfmPwd = cfmPwd.getText().toString();
                Log.d(msg, "Username: " + sUsername);
                Log.d(msg, "Email: " + sEmail);
                if (!sUsername.trim().isEmpty() && isValidEmail(sEmail.trim())) {
                    if (sPwd.trim().equals(sCfmPwd.trim())) {
                        verifyToken();
                    } else {
                        Toast.makeText(getApplicationContext(), "Password and confirm password don't match", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid Username or Email", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.LoginFromSignUp:
                // Handle LoginFromSignUp TextView click
                Intent i = new Intent(this, Login.class);
                startActivity(i);
                break;
        }
    }



    private void userInstitution(){

    AlertDialog.Builder getInstitution = new AlertDialog.Builder(this);
    final Spinner institutionDropdown = new Spinner(this);
    ArrayAdapter<String> insitutionArr = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
            new String[]{"Singapore Polytechnic", "National University Of Singapore", "Ngee Ann Polytechnic","Nanyang Polytechnic","Nanyang Technological University"});
    institutionDropdown.setAdapter(insitutionArr);
    getInstitution.setView(institutionDropdown)
            .setTitle("Hi " + sUsername + ", \n\nWhich institution are you from?")
            .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Store the selected institution
                     institution = institutionDropdown.getSelectedItem().toString();
                    // Show the next dialog
                    userCourses();
                }
            })

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
            .setTitle("Hi " + sUsername + ", \n\nWhich course are you from?")
            .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Store the selected course
                    String course = courseSpinner.getSelectedItem().toString();
                    // Now you have all the information you need, you can proceed to the next activity
                    Intent i = new Intent(SignUp.this, MainActivity.class);
                    // Pass the collected information to the next activity if needed


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

    private int generateToken() {
        token = (int) (Math.random() * 900000) + 100000;

        Log.d(msg, "Token: " + token);
        return token;
    }
    private int generatedToken = 0;
    private void verifyToken() {

        AlertDialog.Builder tokenAlert = new AlertDialog.Builder(this);
        final EditText userInputToken = new EditText(this);
        userInputToken.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (generatedToken == 0) {
            generatedToken = generateToken();
        } //token should be not regenerated if wrong token hence if statement to check
        tokenAlert.setMessage("Enter Verification token").setTitle("Verification Token")
                .setView(userInputToken)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = userInputToken.getText().toString();
                        if (input.equals(String.valueOf(generatedToken))) {
                            Log.d(msg, "Successful");
                            userInstitution();

                        } else {
                            Log.d(msg, "Wrong token");
                            Toast.makeText(getApplicationContext(),"Enter the right token", Toast.LENGTH_LONG).show();
                            verifyToken(); //stay in the same page
                        }
                    }
                })
                .setNegativeButton("Resend", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        generatedToken = generateToken();
                        dialog.dismiss();
                        verifyToken();
                    }
                })
                .setCancelable(true);

        AlertDialog dialog = tokenAlert.create();
        dialog.show();
    }
}
