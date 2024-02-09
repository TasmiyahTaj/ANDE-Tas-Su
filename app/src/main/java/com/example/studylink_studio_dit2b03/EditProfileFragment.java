package com.example.studylink_studio_dit2b03;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;

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

        return rootView;
    }


    private void changeProfilePicture() {
        // Implement profile picture change functionality
    }

    private void changePassword() {
        // Implement change password functionality
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
//        // Get the Student object
//        Student student = userInstance.getStudent();
//        if (student != null) {
//            String institution = student.getInstitutionid();
//            String course = student.getCourseid();
//
//            // Set default value for institution spinner
//            if (institution != null && institutions != null) {
//                int index = institutions.indexOf(institution);
//                if (index != -1) {
//                    institutionSpinner.setSelection(index);
//                }
//            }
//
//            // Set default value for course spinner
//            if (course != null && courses != null) {
//                int index = courses.indexOf(course);
//                if (index != -1) {
//                    courseSpinner.setSelection(index);
//                }
//            }
//        }else{
//            Log.d("Edit profile","not a student");
//        }
//
//        // Get the Tutor object
//        Tutor tutor = userInstance.getTutor();
//        if (tutor != null) {
//            String qualification = tutor.getQualification();
//            String specialisation = tutor.getSpecialised();
//
//            // Set default value for qualification spinner
//            if (qualification != null && qualifications != null) {
//                int index = qualifications.indexOf(qualification);
//                if (index != -1) {
//                    qualificationSpinner.setSelection(index);
//                }
//            }
//
//            // Set default value for specialization spinner
//            if (specialisation != null && specialisations != null) {
//                int index = specialisations.indexOf(specialisation);
//                if (index != -1) {
//                    specializationSpinner.setSelection(index);
//                }
//            }
//        }else{
//            Log.d("Edit profile","not a teacher");
//        }
    }

}
