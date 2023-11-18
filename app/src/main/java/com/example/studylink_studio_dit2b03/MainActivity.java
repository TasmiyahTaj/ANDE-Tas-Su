package com.example.studylink_studio_dit2b03;

import androidx.appcompat.app.AppCompatActivity;
import com.example.studylink_studio_dit2b03.Login;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }
}