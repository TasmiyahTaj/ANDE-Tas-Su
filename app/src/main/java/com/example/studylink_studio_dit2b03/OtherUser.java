package com.example.studylink_studio_dit2b03;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class OtherUser extends Fragment {

    private static final String TAG = "OtherUserFragment";
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private RecyclerView questionRecyclerView;
    private QuestionAdapter questionAdapter;
    private List<Question> questionList;
    private FirebaseFirestore db;
    private String userId;
    private int roleId;
    private TextView profile_username, description, years, institution;
    private ImageView userProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;

        Bundle args = getArguments();
        if (args != null) {
            userId = args.getString("searchedUserID");
            roleId = args.getInt("searchedRoleID");
            // Initialize Firestore
            db = FirebaseFirestore.getInstance();
            // Load user details based on user ID and role ID
            loadUserDetails(userId, roleId);
            // Inflate the appropriate layout based on the role
            if (roleId == 1) {
                view = inflater.inflate(R.layout.fragment_student_profile, container, false);
                profile_username = view.findViewById(R.id.txtusername);
                institution = view.findViewById(R.id.schooltxt);
                questionRecyclerView = view.findViewById(R.id.questionRecyclerView);
                questionList = new ArrayList<>();
                questionAdapter = new QuestionAdapter(questionList);
                questionRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                questionRecyclerView.setAdapter(questionAdapter);

            } else if (roleId == 2) {
                view = inflater.inflate(R.layout.fragment_tutor_profile, container, false);
                profile_username = view.findViewById(R.id.txtusername);
                description = view.findViewById(R.id.tutorInfo);
                years = view.findViewById(R.id.yearsOfExperience);
            } else {
                // Redirect to login activity or handle accordingly
                Intent intent = new Intent(requireContext(), Login.class);
                startActivity(intent);
                requireActivity().finish();
            }
            userProfile = view.findViewById(R.id.yourProfile);
            hideButtonsBasedOnRole(view);
        } else {
            Log.e(TAG, "Arguments bundle is null");
        }

        return view;
    }

    private void loadUserDetails(String userId, int roleId) {
        // Query Firestore to get user details
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            // Populate UI based on role
                            profile_username.setText(user.getUsername());
                            if (user.getProfilePicUrl() != null && !user.getProfilePicUrl().isEmpty()) {
                                Picasso.get().load(user.getProfilePicUrl()).into(userProfile);
                            }
                            if (roleId == 1) {
                                // If user is a student, load institution details
                                loadStudentDetails(userId);
                            } else if (roleId == 2) {
                                // If user is a tutor, load tutor details
                                loadTutorDetails(userId);
                            }
                        }
                    } else {
                        Log.e(TAG, "User document does not exist");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching user details: " + e.getMessage()));
    }
    private void hideButtonsBasedOnRole(View view) {
        // Hide buttons based on the role
        if (roleId == 1) {
            view.findViewById(R.id.settingsImg).setVisibility(View.GONE);
            view.findViewById(R.id.btnEditProfile).setVisibility(View.GONE);
            view.findViewById(R.id.btnShareProfile).setVisibility(View.GONE);
        } else if (roleId == 2) {
            // If user is a tutor, hide buttons in the tutor profile layout
            view.findViewById(R.id.settingsImg).setVisibility(View.GONE);
            view.findViewById(R.id.btnEditProfile).setVisibility(View.GONE);
            view.findViewById(R.id.btnShareProfile).setVisibility(View.GONE);
        }
    }
    private void loadStudentDetails(String userId) {
        // Query Firestore to get student details
        db.collection("Student")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Student student = documentSnapshot.toObject(Student.class);
                        if (student != null) {
                            institution.setText(student.getInstitutionid());
                        }
                    } else {
                        Log.e(TAG, "Student document does not exist");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching student details: " + e.getMessage()));
    }

    private void loadTutorDetails(String userId) {
        // Query Firestore to get tutor details
        db.collection("Tutor")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Tutor tutor = documentSnapshot.toObject(Tutor.class);
                        if (tutor != null) {
                            description.setText(tutor.getSpecialised() + " - " + tutor.getQualification());
                            years.setText("Experience:"+  String.valueOf(tutor.getYearsOfExperience()));
                        }
                    } else {
                        Log.e(TAG, "Tutor document does not exist");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching tutor details: " + e.getMessage()));
    }
}

