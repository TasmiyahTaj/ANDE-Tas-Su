//package com.example.studylink_studio_dit2b03;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class SignUpPrompt extends AppCompatActivity implements View.OnClickListener{
//    int token = 0;
//    private String dUsername;
//    TextView rUsername;
//    String msg = "From SignUpPrompt Class : ";
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.signup);
//
//        // Example: Connecting verifyToken method to a button
//
//        rUsername=(TextView) findViewById(R.id.txtUsername);
//        Bundle getData = getIntent().getExtras();
//        if(getData !=null){
//            dUsername=getData.getString("rUsername");
//            rUsername.setText(dUsername);
//        }else{
//            Toast.makeText(getApplicationContext(),"Failed to get Data",Toast.LENGTH_LONG).show();
//        }
//    }
//}
