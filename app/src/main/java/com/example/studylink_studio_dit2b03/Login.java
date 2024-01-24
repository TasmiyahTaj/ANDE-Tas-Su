package com.example.studylink_studio_dit2b03;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText email, password;
    private String sEmail, sPwd;
    private int userid;
    private FirebaseAuth auth;
    private TextView signupRedirect;
    private Button loginButton;
    SharedPreferences prefs;
    public static final String MyPREFERNCES = "MyPrefs";
    public static final String uid = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        TextView signUpTextView = findViewById(R.id.RegisterFromLogin);
        signUpTextView.setOnClickListener(this);
        email = findViewById(R.id.txtEmailLogin);
        password = findViewById(R.id.txtPwdLogin);

        auth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.btnLogin);
        signupRedirect = findViewById(R.id.RegisterFromLogin);

        loginButton.setOnClickListener(this);

        signupRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, SignUp.class));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                handleLoginButtonClick();
                break;
            case R.id.RegisterFromLogin:
                startActivity(new Intent(Login.this, SignUp.class));
                break;
        }
    }

    private void handleLoginButtonClick() {
        sEmail = email.getText().toString().trim();
        sPwd = password.getText().toString().trim();
        if (!sEmail.isEmpty() && isValidEmail(sEmail)) {
            if (!sPwd.isEmpty()) {
                auth.signInWithEmailAndPassword(sEmail, sPwd)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                saveUserID(authResult.getUser().getUid());
                                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, Home.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                password.setError("Password cannot be empty");
            }
        } else if (sEmail.isEmpty()) {
            email.setError("Email cannot be empty");
        } else {
            email.setError("Please enter a valid email");
        }
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void saveUserID(String userID) {

        prefs = getSharedPreferences(MyPREFERNCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(uid, userID);
        editor.apply();
    }

}
