package com.example.studylink_studio_dit2b03;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText email,password;
    private String sEmail,sPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        TextView signUpTextView = findViewById(R.id.RegisterFromLogin);
        signUpTextView.setOnClickListener(this);
        email= (EditText) findViewById(R.id.txtEmailLogin);
        password=(EditText) findViewById(R.id.txtPwdLogin);

    }
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.RegisterFromLogin:
                Intent toSignUp= new Intent(this,SignUp.class );
                startActivity(toSignUp);
                break;

            case R.id.btnLogin:
                sEmail = email.getText().toString();
                sPwd = password.getText().toString();
                if(isValidEmail(sEmail)&& !sPwd.isEmpty()){
                    Intent toHome = new Intent(this,Home.class);
                    startActivity(toHome);
                }else{
                    Toast.makeText(getApplicationContext(),"Invalid Email or password",Toast.LENGTH_LONG).show();
                }
        }
    }
}
