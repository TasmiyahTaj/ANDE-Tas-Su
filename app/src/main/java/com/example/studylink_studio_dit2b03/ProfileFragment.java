package com.example.studylink_studio_dit2b03;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.net.Uri;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private ImageView settingBtn;
    private TextView profile_username, description,years;
    private FirebaseUser firebaseUser;
    private ImageView userProfile;
    User userInstance = User.getInstance();
    View view;
    private List<Question> questionList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInstance = User.getInstance();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Check user role and inflate the appropriate layout
        if (userInstance.getRoleid() == 1) {
            return inflater.inflate(R.layout.fragment_student_profile, container, false);
        } else if(userInstance.getRoleid()==2) {
            return inflater.inflate(R.layout.fragment_tutor_profile, container, false);
        }else{

            Intent intent = new Intent(requireContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
            // Return an empty view if the user is not logged in
            return new View(requireContext());

        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        settingBtn = view.findViewById(R.id.settingsImg);
        profile_username = view.findViewById(R.id.txtusername);

        userProfile = view.findViewById(R.id.yourProfile);

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirect to the Settings activity
                Intent intent = new Intent(requireContext(), Settings.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });
        if (userInstance.getRoleid() == 1) {
            Student student = userInstance.getStudent();
            if (student != null) {
                description = view.findViewById(R.id.schooltxt);
                String username = userInstance.getUsername();
                String profileUrl = userInstance.getProfilePicUrl();
                String school = userInstance.getStudent().getInstitutionid();
                Log.d("Student", "onViewCreated: Student Details - " + userInstance.getStudent());
                profile_username.setText(username);
                description.setText(school);

                // Check if profileUrl is not null before parsing it as a Uri
                if (profileUrl != null) {
                    Uri imageUri = Uri.parse(profileUrl);

                    // Load the image using Picasso with the Uri
                    Picasso.get().load(imageUri).into(userProfile);
                } else {
                    // Handle the case where profileUrl is null
                    Log.e("ProfileFragment", "Profile picture URL is null");
                }

                QuestionAdapter adapter = new QuestionAdapter(questionList);
                RecyclerView recyclerView = view.findViewById(R.id.questionRecyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                recyclerView.setAdapter(adapter);


                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // Capture user ID before Firestore queries
                String currentUserId = userInstance.getUserid();

// Query questions collection
                db.collection("Questions")
                        .whereEqualTo("userID", currentUserId)
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Clear the existing questionList
                                questionList.clear();

                                // Iterate through the result and populate your RecyclerView adapter
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Extract question details and add them to your RecyclerView adapter
                                    String title = document.getString("title");
                                    String description = document.getString("description");
                                    String communityId = document.getString("communityID");
                                    String questionImage = document.getString("questionImageUrl");
                                    String questionId = document.getId();

                                    // Retrieve community title and tutor ID
                                    db.collection("Community")
                                            .document(communityId)
                                            .get()
                                            .addOnSuccessListener(communitySnapshot -> {
                                                if (communitySnapshot.exists()) {
                                                    String communityTitle = communitySnapshot.getString("title");
                                                    String tutorId = communitySnapshot.getString("creatorId");

                                                    // Retrieve tutor name
                                                    db.collection("Tutor")
                                                            .document(tutorId)
                                                            .get()
                                                            .addOnSuccessListener(tutorSnapshot -> {
                                                                if (tutorSnapshot.exists()) {
                                                                    String tutorName = tutorSnapshot.getString("username");
                                                                    // Add the question to the list
                                                                    if(questionImage!=null){
                                                                        questionList.add(new Question(questionId ,currentUserId, communityTitle, title, description, tutorName,questionImage));

                                                                    }else{         questionList.add(new Question(questionId ,currentUserId, communityTitle, title, description, tutorName));}

                                                                    Log.e("ProfileFragment", "question by me " + questionList.size() + title + tutorName);

                                                                    // Ensure UI updates are on the main thread
                                                                    requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                                                                }
                                                            });
                                                }
                                            });

                                }
                            } else {
                                Log.e("Firestore from profile", "Error getting questions: " + task.getException().getLocalizedMessage());

                            }
                        });

            }
        }



        else if (userInstance.getRoleid() == 2) {
            Tutor tutor = userInstance.getTutor();
            if (tutor != null) {
                description = view.findViewById(R.id.tutorInfo);
                years = view.findViewById(R.id.yearsOfExperience);

                String username = userInstance.getUsername();
                String profileUrl = userInstance.getProfilePicUrl();
                String specialisation = userInstance.getTutor().getSpecialised();
                String qualification = userInstance.getTutor().getQualification();
                int yearsOfExperience = userInstance.getTutor().getYearsOfExperience();

                if (profileUrl != null) {
                    Uri imageUri = Uri.parse(profileUrl);

                    // Load the image using Picasso with the Uri
                    Picasso.get().load(imageUri).into(userProfile);
                } else {
                    // Handle the case where profileUrl is null
                    Log.e("ProfileFragment", "Profile picture URL is null");
                }
                profile_username.setText(username);

                // Convert yearsOfExperience to a string before concatenating
                String yearsOfExperienceText = String.valueOf(yearsOfExperience);

                String descriptionText = specialisation + " - " + qualification;
                description.setText(descriptionText);
                years.setText(yearsOfExperienceText + " years of experience");
            }
        }

    }

}
