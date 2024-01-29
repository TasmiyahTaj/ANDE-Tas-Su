package com.example.studylink_studio_dit2b03;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Settings extends AppCompatActivity {
    private FirebaseAuth auth;
    private Button logoutButton;
    private TextView txtNameSettings;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        auth = FirebaseAuth.getInstance();
        txtNameSettings = findViewById(R.id.txtNameSettings);
        logoutButton=findViewById(R.id.logoutButton);
        firebaseUser = auth.getCurrentUser();
        User userInstance = User.getInstance();
        String username = userInstance.getUsername();


        txtNameSettings.setText(username);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sign out the user
                auth.signOut();

                // Redirect to the login activity
                Intent intent = new Intent(Settings.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
