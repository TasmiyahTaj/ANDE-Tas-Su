package com.example.studylink_studio_dit2b03;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    private User userInstance;
    private int roleId;
    private ImageView profilePictureImageView;
    private EditText usernameEditText;
    private Spinner institutionSpinner;
    private Spinner courseSpinner;
    private EditText workExperienceEditText;
    private Spinner qualificationSpinner;
    private Spinner specializationSpinner;
    private EditText yearOfExperienceEditText;
    private EditText accountNumberEditText;
    private Button changePasswordButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int GALLERY_REQUEST_CODE = 1;
    private Uri selectedImageUri;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.edit_profile, container, false);

        // Initialize views
        profilePictureImageView = rootView.findViewById(R.id.imageView_profile_picture);
        usernameEditText = rootView.findViewById(R.id.editText_username);
        institutionSpinner = rootView.findViewById(R.id.spinner_institution);
        courseSpinner = rootView.findViewById(R.id.spinner_course);
        workExperienceEditText = rootView.findViewById(R.id.editText_work_experience);
        qualificationSpinner = rootView.findViewById(R.id.spinner_qualification);
        specializationSpinner = rootView.findViewById(R.id.spinner_specialization);
        yearOfExperienceEditText = rootView.findViewById(R.id.editText_year_of_experience);
        accountNumberEditText = rootView.findViewById(R.id.editText_account_number);
        changePasswordButton = rootView.findViewById(R.id.button_change_password);

        // Get user instance
        userInstance = User.getInstance();
        roleId = userInstance.getRoleid();

        // Set profile picture if available, otherwise set default image
        if (userInstance.getProfilePicUrl() != null) {
            Uri imageUri = Uri.parse(userInstance.getProfilePicUrl());
            Picasso.get().load(imageUri).into(profilePictureImageView);
        } else {
            profilePictureImageView.setImageResource(R.drawable.profile_pic);
            Log.e("EditProfileFragment", "Profile picture URL is null");
        }

        // Set visibility based on role ID
        if (roleId == 1) { // Student
            workExperienceEditText.setVisibility(View.GONE);
            qualificationSpinner.setVisibility(View.GONE);
            specializationSpinner.setVisibility(View.GONE);
            yearOfExperienceEditText.setVisibility(View.GONE);
            accountNumberEditText.setVisibility(View.GONE);
            usernameEditText.setText(userInstance.getUsername());
        } else { // Teacher
            institutionSpinner.setVisibility(View.GONE);
            courseSpinner.setVisibility(View.GONE);
            usernameEditText.setText(userInstance.getUsername());
        }

        // Populate spinners
        populateSpinners();

        // Set onClick listener for profile picture
        profilePictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfilePicture();
            }
        });

        // Set onClick listener for change password button
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        Button saveBtn = rootView.findViewById(R.id.button_save); // Assuming the ID of your save button is button_save_profile

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileChanges();
            }
        });
        return rootView;
    }

    private void updateUserInformation(String newUsername) {
        // Get the reference to the user's document in Firestore
        DocumentReference userDocRef = db.collection("users").document(userInstance.getUserid());

        // Update the "username" field with the new username
        userDocRef.update("username", newUsername)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EditProfileFragment", "User's username updated successfully");
                        // Optionally, perform any action after the update is successful
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("EditProfileFragment", "Error updating user's username", e);
                        // Optionally, handle the error here
                    }
                });
    }

    private void updateStudentInformation(String newUsername, String newInstitution, String newCourse) {
        // Get the reference to the student's document in Firestore
        DocumentReference studentDocRef = db.collection("Student").document(userInstance.getUserid());

        // Create a map with the updated fields
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", newUsername);
        updates.put("institutionid", newInstitution);
        updates.put("courseid", newCourse);

        // Update the student's document with the new data
        studentDocRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EditProfileFragment", "Student information updated successfully");
                        // Optionally, perform any action after the update is successful
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("EditProfileFragment", "Error updating student information", e);
                        // Optionally, handle the error here
                    }
                });
    }
    private void changeProfilePicture() {
        // Implement profile picture change functionality
        // Start an intent to choose an image from the device's gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of the selected image
           selectedImageUri = data.getData();

            // Upload the image to Firebase Storage
            profilePictureImageView.setImageURI(selectedImageUri);
        }else {
            // No image selected, proceed with saving profile changes
            saveProfileChanges();
        }
    }

    private void uploadProfilePicture(Uri imageUri) {
        // Create a reference to the profile picture file in Firebase Storage
        StorageReference profilePicRef = FirebaseStorage.getInstance().getReference()
                .child("profile_pictures")
                .child(userInstance.getUserid() + ".jpg");

        // Upload the image to Firebase Storage
        profilePicRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully, get the download URL
                        profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Update the profile picture URL in the database
                                updateUserProfilePicture(uri.toString());
                                userInstance.setProfilePicUrl(uri.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle unsuccessful upload
                        Log.e("EditProfileFragment", "Failed to upload profile picture", e);
                    }
                });
    }

    private void updateUserProfilePicture(String profilePicUrl) {
        // Get the reference to the user's document in Firestore
        DocumentReference userDocRef = db.collection("users").document(userInstance.getUserid());

        // Update the "profilePicUrl" field with the new profile picture URL
        userDocRef.update("profilePicUrl", profilePicUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EditProfileFragment", "User's profile picture updated successfully");
                        // Optionally, perform any action after the update is successful
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("EditProfileFragment", "Error updating user's profile picture", e);
                        // Optionally, handle the error here
                    }
                });
    }
    private void changePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Change Password");
        builder.setMessage("Are you sure you want to change your password?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                promptForOldPassword();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create(); // Declare dialog as final
        dialog.show();
    }

    private void promptForOldPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter Old Password");

        // Set up the input
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String oldPassword = input.getText().toString();
                // Verify the old password with the database
                verifyOldPassword(oldPassword, new OnPasswordVerificationListener() {
                    @Override
                    public void onVerificationSuccess() {
                        promptForNewPassword();
                    }

                    @Override
                    public void onVerificationFailure() {
                        Toast.makeText(requireContext(), "Incorrect old password", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void promptForNewPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter New Password");

        // Set up the input for new password
        final EditText newPasswordInput = new EditText(requireContext());
        newPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPasswordInput.setHint("New Password");

        // Set up the input for confirming new password
        final EditText confirmPasswordInput = new EditText(requireContext());
        confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPasswordInput.setHint("Confirm New Password");

        // Create a layout to hold the EditText fields
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(newPasswordInput);
        layout.addView(confirmPasswordInput);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPassword = newPasswordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();

                // Check if passwords match
                if (newPassword.equals(confirmPassword)) {
                    // Update the password in the database
                    updatePassword(newPassword);
                    Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void verifyOldPassword(final String oldPassword, final OnPasswordVerificationListener listener) {
        // Get the reference to the current user in your authentication system
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Re-authenticate the user with their current password to verify it
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
        user.reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Password re-authenticated successfully
                        // You can handle this case as the old password being correct
                        promptForNewPassword();
                        listener.onVerificationSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Re-authentication failed, handle this as incorrect old password
                        Toast.makeText(requireContext(), "Incorrect old password", Toast.LENGTH_SHORT).show();
                        listener.onVerificationFailure();
                    }
                });
    }

    // Define an interface to handle password verification callbacks
    interface OnPasswordVerificationListener {
        void onVerificationSuccess();
        void onVerificationFailure();
    }

    private void updatePassword(String newPassword) {
        // Get the reference to the current user in your authentication system
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Update the user's password with the new password
        user.updatePassword(newPassword)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Password updated successfully
                        Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to update password
                        Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveProfileChanges() {
        // Retrieve updated information
        String newUsername = usernameEditText.getText().toString();
        String newQualification = qualificationSpinner.getSelectedItem().toString();
        String newSpecialization = specializationSpinner.getSelectedItem().toString();

        // Update user's information in the Users collection
        updateUserInformation(newUsername);

        // If the user is a student, update their information in the Student collection
        if (roleId == 1) { // Student
            String newInstitution = institutionSpinner.getSelectedItem().toString();
            String newCourse = courseSpinner.getSelectedItem().toString();
            updateStudentInformation(newUsername, newInstitution, newCourse);
            userInstance.setUsername(newUsername);
            Student student=userInstance.getStudent();
            student.setCourseid(newCourse);
            student.setInstitutionid(newInstitution);

            Toast.makeText(getActivity(), "Profile changes saved successfully", Toast.LENGTH_SHORT).show();
        }
        // If the user is a tutor, update their information in the Tutor collection
        else if (roleId == 2) { // Tutor
            updateTutorInformation(newUsername, newQualification, newSpecialization);
            userInstance.setUsername(newUsername);
            Tutor tutor=userInstance.getTutor();
            tutor.setQualification(newQualification);
            tutor.setSpecialised(newSpecialization);

            Toast.makeText(getActivity(), "Profile changes saved successfully", Toast.LENGTH_SHORT).show();
        }
        if (selectedImageUri != null) {
            uploadProfilePicture(selectedImageUri);
        }
        // Optionally, you can show a toast or perform any other action to indicate successful update
    }
    private void updateTutorInformation(String newUsername, String newQualification, String newSpecialization) {
        // Get the reference to the tutor's document in Firestore
        DocumentReference tutorDocRef = db.collection("Tutor").document(userInstance.getUserid());

        // Create a map with the updated fields
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", newUsername);
        updates.put("qualification", newQualification);
        updates.put("specialised", newSpecialization);

        // Update the tutor's document with the new data
        tutorDocRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EditProfileFragment", "Tutor information updated successfully");
                        // Optionally, perform any action after the update is successful
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("EditProfileFragment", "Error updating tutor information", e);
                        // Optionally, handle the error here
                    }
                });
    }

    private void populateSpinners() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_preferences", Context.MODE_PRIVATE);

        // Populate institution spinner
        String institutionsJson = SharedPreferencesHelper.getDataFromSharedPreferences(getActivity(), "institutions", "");
        Type institutionListType = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> institutions = new Gson().fromJson(institutionsJson, institutionListType);
        if (institutions != null) {
            ArrayAdapter<String> institutionAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, institutions);
            institutionSpinner.setAdapter(institutionAdapter);
        } else {
            Log.e("EditProfileFragment", "Institutions list is null");
        }

        // Populate course spinner
        String courseJson = SharedPreferencesHelper.getDataFromSharedPreferences(getActivity(), "courses", "");
        Type courseListType = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> courses = new Gson().fromJson(courseJson, courseListType);
        if (courses != null) {
            ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, courses);
            courseSpinner.setAdapter(courseAdapter);
        } else {
            Log.e("EditProfileFragment", "Courses list is null");
        }

        // Populate qualification spinner
        String qualificationJson = SharedPreferencesHelper.getDataFromSharedPreferences(getActivity(), "qualification", "");
        Type qualificationListType = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> qualifications = new Gson().fromJson(qualificationJson, qualificationListType);
        if (qualifications != null) {
            ArrayAdapter<String> qualificationAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, qualifications);
            qualificationSpinner.setAdapter(qualificationAdapter);
        } else {
            Log.e("EditProfileFragment", "Qualifications list is null");
        }

        // Populate specialization spinner
        String specialisedJson = SharedPreferencesHelper.getDataFromSharedPreferences(getActivity(), "specializations", "");
        Type specialisedListType = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> specialisations = new Gson().fromJson(specialisedJson, specialisedListType);
        if (specialisations != null) {
            ArrayAdapter<String> specialisationAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, specialisations);
            specializationSpinner.setAdapter(specialisationAdapter);
        } else {
            Log.e("EditProfileFragment", "Specialisations list is null");
        }
//
        // Get the Student object
        Student student = userInstance.getStudent();
        if (student != null) {
            String institution = student.getInstitutionid();
            String course = student.getCourseid();

            // Set default value for institution spinner
            if (institution != null && institutions != null) {
                int index = institutions.indexOf(institution);
                if (index != -1) {
                    institutionSpinner.setSelection(index);
                }
            }

            // Set default value for course spinner
            if (course != null && courses != null) {
                int index = courses.indexOf(course);
                if (index != -1) {
                    courseSpinner.setSelection(index);
                }
            }
        }else{
            Log.d("Edit profile","not a student");
        }

        // Get the Tutor object
        Tutor tutor = userInstance.getTutor();
        if (tutor != null) {
            String qualification = tutor.getQualification();
            String specialisation = tutor.getSpecialised();

            // Set default value for qualification spinner
            if (qualification != null && qualifications != null) {
                int index = qualifications.indexOf(qualification);
                if (index != -1) {
                    qualificationSpinner.setSelection(index);
                }
            }

            // Set default value for specialization spinner
            if (specialisation != null && specialisations != null) {
                int index = specialisations.indexOf(specialisation);
                if (index != -1) {
                    specializationSpinner.setSelection(index);
                }
            }
        }else{
            Log.d("Edit profile","not a teacher");
        }
    }

}
