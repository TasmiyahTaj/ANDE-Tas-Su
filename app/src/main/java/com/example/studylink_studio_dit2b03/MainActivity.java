package com.example.studylink_studio_dit2b03;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    SearchFragment searchFragment = new SearchFragment();
    PostFragment postFragment = new PostFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isUserLoggedIn()) {
            // If not logged in, go to the login page
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish(); // Finish the current activity to prevent going back to it
        } else {

            setContentView(R.layout.activity_main);

            bottomNavigationView = findViewById(R.id.bottomNavigationView);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.home:
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                            return true;

                        case R.id.search:
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, searchFragment).commit();
                            return true;

                        case R.id.post:
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, postFragment).commit();
                            return true;

                        case R.id.notification:
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, notificationFragment).commit();
                            return true;

                        case R.id.profile:
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                            return true;
                    }
                    return false;
                }
            });
        }

    }
    private boolean isUserLoggedIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        return user != null; // Returns true if a user is currently authenticated, false otherwise
    }
    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }
}