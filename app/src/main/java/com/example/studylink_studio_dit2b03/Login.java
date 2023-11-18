package com.example.studylink_studio_dit2b03;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        TextView signUpTextView = findViewById(R.id.signUpFromLogin);
        signUpTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.signUpFromLogin:
                Intent i= new Intent(this,SignUp.class );
                startActivity(i);
                break;

            case R.id.buttonLogin:
                Intent homeIntent = new Intent(this, Home.class);
                startActivity(homeIntent);
                break;
        }
    }
}
