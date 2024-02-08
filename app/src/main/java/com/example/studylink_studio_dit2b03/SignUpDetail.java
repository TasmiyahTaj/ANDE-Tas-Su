package com.example.studylink_studio_dit2b03;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class SignUpDetail extends AppCompatActivity{
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private EditText acct, cfm_acct;
    private Spinner institutionSpinner, courseSpinner, educationSpinner, specializedCourseSpinner;
    private EditText workExperienceEditText;
    private RadioButton studentRadioButton, teacherRadioButton;
    private LinearLayout studentFieldsLayout, teacherFieldsLayout;
    private String userId, username, email;
    private ImageView profile;
    User user;
    int roleid;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri profileImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_detail);

        userId = getIntent().getStringExtra("userId");
        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");

        institutionSpinner = findViewById(R.id.institutionSpinner);
        courseSpinner = findViewById(R.id.courseSpinner);
        educationSpinner = findViewById(R.id.educationSpinner);
        specializedCourseSpinner = findViewById(R.id.specializedCourseSpinner);
        workExperienceEditText = findViewById(R.id.workExperienceEditText);
        acct = findViewById(R.id.account_number);
        cfm_acct = findViewById(R.id.confirm_accNo);
        studentRadioButton = findViewById(R.id.studentRadioButton);
        teacherRadioButton = findViewById(R.id.teacherRadioButton);
        studentFieldsLayout = findViewById(R.id.studentFieldsLayout);
        teacherFieldsLayout = findViewById(R.id.teacherFieldsLayout);
        profile=findViewById(R.id.profileImageView);
        // Populate spinners with data from Android Studio (dummy data for demonstration)
        populateInstitutionsSpinner();
        populateCoursesSpinner();
        populateEducationSpinner();
        populateSpecializedCourseSpinner();
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        // Add a button click listener to proceed to the next step
        Button btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get selected institution and course
                String selectedInstitution = institutionSpinner.getSelectedItem().toString();
                String selectedCourse = courseSpinner.getSelectedItem().toString();

                // Check the selected role
                if (studentRadioButton.isChecked()) {
                    // User selected role as Student
                    saveUserToDatabase();
                    saveStudentToDatabase(selectedInstitution, selectedCourse);
                    startActivity(new Intent(SignUpDetail.this, Login.class));
                    finish();
                } else if (teacherRadioButton.isChecked()) {
                    // User selected role as Teacher
                    saveUserToDatabase();

                    saveTeacherToDatabase();
                    startActivity(new Intent(SignUpDetail.this, Login.class));
                    finish();

                } else {
                    // No role selected
                    Toast.makeText(SignUpDetail.this, "Please select a role", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add radio group listener to handle visibility of Student and Teacher fields
        RadioGroup roleRadioGroup = findViewById(R.id.roleRadioGroup);
        roleRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.studentRadioButton) {
                    // User selected role as Student
                    roleid=1;
                    studentFieldsLayout.setVisibility(View.VISIBLE);
                    teacherFieldsLayout.setVisibility(View.GONE);
                    btnNext.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.teacherRadioButton) {
                    // User selected role as Teacher
                    roleid=2;
                    studentFieldsLayout.setVisibility(View.GONE);
                    teacherFieldsLayout.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.VISIBLE);

                }
            }
        });
    }



    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profileImageUri = data.getData();
            // Set the selected image to the ImageView
            profile.setImageURI(profileImageUri);
        }
    }


    // Other methods (populateInstitutionsSpinner, populateCoursesSpinner, etc.) remain unchanged
    private void saveUserToDatabase() {
        // Save user information in the "users" table of your database
        user = new User(userId, email, username, roleid);

        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            uploadProfilePicture();
                            Toast.makeText(SignUpDetail.this, "User details saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpDetail.this, "Error saving user details", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadProfilePicture() {
        if (profileImageUri != null) {
            StorageReference profilePicRef = storageRef.child("profile_pictures/" + userId + ".jpg");

            profilePicRef.putFile(profileImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the download URL for the uploaded image
                            profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    // Save the download URL to the user object
                                    user.setProfilePicUrl(downloadUri.toString());
                                    // Update the user details in the database
                                    FirebaseFirestore.getInstance().collection("users")
                                            .document(userId)
                                            .set(user); // Update the user with the profile picture URL
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle unsuccessful upload
                            Toast.makeText(SignUpDetail.this, "Error uploading profile picture", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void saveStudentToDatabase(String institution, String course) {
        // Save user information in the "students" table of your database
        Student student = new Student(userId, username, institution, course);

        FirebaseFirestore.getInstance().collection("Student")
                .document(userId)
                .set(student)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpDetail.this, "Student details saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpDetail.this, "Error saving Student details", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveTeacherToDatabase() {
        String selectedEducation = educationSpinner.getSelectedItem().toString();
        String selectedSpecializedCourse = specializedCourseSpinner.getSelectedItem().toString();

        // Get the text from EditText fields
        String accountNoText = acct.getText().toString();
        String cfmActNoText = cfm_acct.getText().toString();

        // Check if EditText fields are not empty
        if (!accountNoText.isEmpty() && !cfmActNoText.isEmpty()) {
            // Parse the strings into integers
            long accountNo = Long.parseLong(accountNoText);
            long cfmActNo = Long.parseLong(cfmActNoText);

            if (accountNo == cfmActNo) {
                // Proceed with saving to the database
                int workExperience = Integer.parseInt(workExperienceEditText.getText().toString());

                Tutor tutor = new Tutor(userId, username, selectedEducation, selectedSpecializedCourse, workExperience, accountNo);

                FirebaseFirestore.getInstance().collection("Tutor")
                        .document(userId)
                        .set(tutor)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpDetail.this, "Tutor details saved successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignUpDetail.this, "Error saving Tutor details", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(SignUpDetail.this, "Account Number doesn't match", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle case where EditText fields are empty
            Toast.makeText(SignUpDetail.this, "Please enter account numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateInstitutionsSpinner() {
        // Dummy data for institutions (replace this with your data)
        List<String> institutions = new ArrayList<>();
        institutions.add("Singapore polytechnic");
        institutions.add("Nanyang Polytechnic");
        institutions.add("Ngee Ann Polytechnic");
        institutions.add("Republic Polytechnic");
        institutions.add("Singapore Institute Of Technolgy");

        institutions.add("National University Singapore");
        institutions.add("Nanyang Technological University");
        institutions.add("Singapore Management University");


        ArrayAdapter<String> institutionAdapter = new ArrayAdapter<>(SignUpDetail.this,
                android.R.layout.simple_spinner_dropdown_item, institutions);
        institutionSpinner.setAdapter(institutionAdapter);
    }

    private void populateCoursesSpinner() {
        // Dummy data for courses (replace this with your data)
        List<String> courses = new ArrayList<>();
        courses.add("Information tech");
        courses.add("Mechanical Engineering");
        courses.add("Chemical Engineering");
        courses.add("Media and Arts");
        courses.add("BioMedical Engineering");


        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(SignUpDetail.this,
                android.R.layout.simple_spinner_dropdown_item, courses);
        courseSpinner.setAdapter(courseAdapter);
    }

    private void populateEducationSpinner() {
        // Dummy data for education (replace this with your data)
        List<String> educationLevels = new ArrayList<>();
        educationLevels.add("PSLE");
        educationLevels.add("O Level");
        educationLevels.add("A Level");
        educationLevels.add("Diploma");
        educationLevels.add("Bachelor's Degree");
        educationLevels.add("Master's Degree");
        educationLevels.add("Ph.D.");

        ArrayAdapter<String> educationAdapter = new ArrayAdapter<>(SignUpDetail.this,
                android.R.layout.simple_spinner_dropdown_item, educationLevels);
        educationSpinner.setAdapter(educationAdapter);
    }

    private void populateSpecializedCourseSpinner() {
        // Dummy data for specialized courses (replace this with your data)
        List<String> specializedCourses = new ArrayList<>();
        specializedCourses.add("Information tech");
        specializedCourses.add("Mechanical Engineering");
        specializedCourses.add("Chemical Engineering");
        specializedCourses.add("Media and Arts");
        specializedCourses.add("BioMedical Engineering");
        specializedCourses.add("Marine Engineering");


        ArrayAdapter<String> specializedCourseAdapter = new ArrayAdapter<>(SignUpDetail.this,
                android.R.layout.simple_spinner_dropdown_item, specializedCourses);
        specializedCourseSpinner.setAdapter(specializedCourseAdapter);
    }

}
