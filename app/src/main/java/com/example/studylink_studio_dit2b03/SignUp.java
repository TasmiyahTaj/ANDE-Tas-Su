package com.example.studylink_studio_dit2b03;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    int token = 0;
    private EditText username,email,password,cfmPwd;
 private String sUsername,sEmail,sPwd,sCfmPwd;
    String msg = "From SignUp Class : ";
    private String institution;
    private DatabaseReference institutionsRef;

    private String course;
    private DatabaseReference courseRef;
    private FirebaseAuth firebaseAuth;
    private Button signupBtn;
    private TextView loginRedirectText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        firebaseAuth = FirebaseAuth.getInstance();
        TextView loginTextView = findViewById(R.id.LoginFromSignUp);
        loginTextView.setOnClickListener(this);

       username= (EditText)findViewById(R.id.txtUsernameSignUp);
        email= (EditText)findViewById(R.id.txtEmailSignUp);
        password= (EditText)findViewById(R.id.txtPwdRegister);
        cfmPwd=(EditText) findViewById(R.id.txtCfmPwdRegister);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        institutionsRef = database.getReference("institutions");
        signupBtn=findViewById(R.id.btnSignUp);
     loginRedirectText=findViewById(R.id.LoginFromSignUp);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sEmail = email.getText().toString().trim();
                sUsername=username.getText().toString().trim();
                sPwd = password.getText().toString().trim();

                if (sEmail.isEmpty() || !isValidEmail(sEmail)) {
                    email.setError("Invalid email");
                } else if (sPwd.isEmpty()) {
                    password.setError("Password cannot be empty");
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(sEmail, sPwd)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUp.this, "SignUp successfully", Toast.LENGTH_SHORT).show();
                                        Intent signUpDetailIntent = new Intent(SignUp.this, SignUpDetail.class);
                                        signUpDetailIntent.putExtra("userId", firebaseAuth.getCurrentUser().getUid());
                                        signUpDetailIntent.putExtra("username", sUsername);
                                        signUpDetailIntent.putExtra("email", sEmail);

                                        startActivity(signUpDetailIntent);
                                    } else {
                                        Toast.makeText(SignUp.this, "Signup failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, Login.class));
            }
        });





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
        }

        tokenAlert.setMessage("Enter Verification token").setTitle("Verification Token")
                .setView(userInputToken)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = userInputToken.getText().toString();
                        if (input.equals(String.valueOf(generatedToken))) {
                            Log.d(msg, "Successful");
                           dialog.dismiss(); // Dismiss the current dialog
//                            userInstitution(); // Proceed to the next step after successful token verification
                            signUpUser(sEmail,sPwd);

                        } else {
                            Log.d(msg, "Wrong token");
                            Toast.makeText(getApplicationContext(), "Enter the right token", Toast.LENGTH_LONG).show();
                            // No need to call verifyToken() here, let the user try again
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
                .setCancelable(true)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        userInputToken.setText("");
                    }
                });


        AlertDialog dialog = tokenAlert.create();
        dialog.show();
    }

    private void signUpUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = firebaseAuth.getCurrentUser().getUid();
                        saveUserIdToSharedPreferences(userId);
                    } else {
                        // If sign up fails, display a message to the user.
                        Log.w("SignUp", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUp.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void saveUserIdToSharedPreferences(String userId) {
        SharedPreferences prefs = getSharedPreferences(Login.MyPREFERNCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Login.uid, userId);
        editor.apply();
    }


}
