package com.example.studylink_studio_dit2b03;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    SearchFragment searchFragment = new SearchFragment();
    PostFragment postFragment = new PostFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    User userInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth
        firebaseUser = auth.getCurrentUser(); // Retrieve the current user

        if (!isUserLoggedIn()) {
            // If not logged in, go to the login page
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish(); // Finish the current activity to prevent going back to it
        } else {
            retrieveUserDetails(firebaseUser.getUid());
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
    // Inside retrieveUserDetails method
    private void retrieveUserDetails(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // DocumentSnapshot data is present, set user details
                    String email = document.getString("email");
                    String username = document.getString("username");
                    String profileUrl = document.getString("profilePicUrl");
                    int roleId = document.getLong("roleid").intValue();

                    // Set user details in the singleton instance
                    userInstance = User.getInstance();
                    userInstance.setUserid(userId);
                    userInstance.setEmail(email);
                    userInstance.setUsername(username);
                    Log.d("Main activity", "Profile "+ profileUrl);
                    if(profileUrl!=null){ userInstance.setProfilePicUrl(profileUrl);}

                    userInstance.setRoleid(roleId);

                    // Check role and fetch details accordingly
                    if (roleId == 1) {
                        // Student role, fetch details from "students" collection
                        fetchStudentDetails(userId);
                    } else if (roleId == 2) {
                        // Tutor role, fetch details from "tutors" collection
                        fetchTutorDetails(userId);
                    }
                } else {
                    // Document does not exist
                }
            } else {
                // Handle failures
            }
        });
    }


    private void fetchStudentDetails(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference studentRef = db.collection("Student").document(userId);

        studentRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String institution = document.getString("institutionid");
                    String course = document.getString("courseid");
                    Log.d("Main","found student "+institution);
                    // Set student details in the singleton instance
                    userInstance.setStudent(new Student(userId, userInstance.getUsername(), institution, course));
                } else {
                    // Student document does not exist
                }
            } else {
                // Handle failures
            }
        });
    }

    // Inside fetchTutorDetails method
    private void fetchTutorDetails(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference tutorRef = db.collection("Tutor").document(userId);

        tutorRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String qualification = document.getString("qualification");
                    String specialised = document.getString("specialised");
                    int yearsOfExperience = document.getLong("yearsOfExperience").intValue();
                    Log.d("Main","found cher "+qualification);
                    // Set tutor details in the singleton instance
                    userInstance.setTutor(new Tutor(userId, userInstance.getUsername(), qualification, specialised, yearsOfExperience));
                } else {
                    // Tutor document does not exist
                }
            } else {
                // Handle failures
            }
        });
    }


}