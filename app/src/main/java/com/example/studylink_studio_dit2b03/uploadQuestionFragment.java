package com.example.studylink_studio_dit2b03;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class uploadQuestionFragment extends Fragment {
    User userInstance = User.getInstance();
    EditText communityNameEditText,communityDescriptionEditText;
    Button createButton;
    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;

    private ImageView attachedImage;
    private Uri selectedImageUri;
    private TextView chooseCommunityTextView, noteTitleLabelTextView, noteContentLabelEditText, notePriceLabelEditText;
    private Button buttonRemoveImage;
    private EditText title, description, noteTitleEditText, noteContentTextView, notePriceTextView;
    Dialog dialogCommunity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_upload_question, container, false);

        chooseCommunityTextView = view.findViewById(R.id.chooseCommunityT);
        title = view.findViewById(R.id.editTextTitleT);
        description = view.findViewById(R.id.editTextDescriptionT);

        chooseCommunityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCommunity = new Dialog(requireContext());
                dialogCommunity.setContentView(R.layout.community_spinner);
                dialogCommunity.getWindow().setLayout(1050, 2000);
                dialogCommunity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogCommunity.show();

                EditText communitySearch = dialogCommunity.findViewById(R.id.communitySearch);
                ListView communityList = dialogCommunity.findViewById(R.id.communityList);
                FirebaseFirestore.getInstance().collection("Community")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            List<Community> communities = new ArrayList<>();
                            List<String> communityNames = new ArrayList<>();

                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Community community = document.toObject(Community.class);
                                communities.add(community);
                                communityNames.add(community.getTitle());
                                Log.d("Community Names", "Number of communities: " + communityNames);
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    requireContext(),
                                    R.layout.textview_derfault,
                                    communityNames
                            );
                            communityList.setAdapter(adapter);

                            communitySearch.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    adapter.getFilter().filter(s);
                                }

                                @Override
                                public void afterTextChanged(Editable s) {}
                            });

                            communityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    // when item selected from list
                                    // set selected item on textView
                                    chooseCommunityTextView.setText(adapter.getItem(position));

                                    // Dismiss dialog
                                    if (dialogCommunity != null && dialogCommunity.isShowing()) {
                                        dialogCommunity.dismiss();
                                    }
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure to fetch communities from Firebase
                            Log.e("Fetch Communities", "Error getting communities", e);
                        });
            }
        });

        attachedImage = view.findViewById(R.id.attachedImage);
        buttonRemoveImage = view.findViewById(R.id.buttonRemoveImage);
        attachedImage.setVisibility(View.VISIBLE);
        Button attachImageButton=view.findViewById(R.id.buttonAttachImageT);

        attachImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSourceDialog();

            }
        });

        buttonRemoveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the image and hide the ImageView
                attachedImage.setImageDrawable(null);
                attachedImage.setVisibility(View.GONE);
                buttonRemoveImage.setVisibility(View.GONE);

                // Optionally, delete the image from Firebase Storage
                // Implement the logic to delete the image from Firebase Storage based on your requirements
            }
        });

        Button postButton = view.findViewById(R.id.buttonPostT);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostButtonClick();
            }
        });

        return view;
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Image Source")
                .setItems(new CharSequence[]{"Gallery", "Camera"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                openGallery();
                                break;
                            case 1:
                                openCamera();
                                break;
                        }
                    }
                })
                .show();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                selectedImageUri = data.getData();
                loadImage(selectedImageUri);

                attachedImage.setVisibility(View.VISIBLE);
                buttonRemoveImage.setVisibility(View.VISIBLE);
            } else if (requestCode == CAMERA_REQUEST_CODE && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                selectedImageUri = saveImageToInternalStorage(photo);
                loadImage(selectedImageUri);

                attachedImage.setVisibility(View.VISIBLE);
                buttonRemoveImage.setVisibility(View.VISIBLE);
            }
        }
    }

    private void loadImage(Uri imageUri) {
        attachedImage.setImageURI(imageUri);
    }

    private Uri saveImageToInternalStorage(Bitmap bitmap) {
        // Generate a unique file name using the current timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "image_" + timeStamp + ".jpg";

        // Get the internal storage directory
        File internalStorageDir = new File(requireContext().getFilesDir(), "images");

        // Create the directory if it doesn't exist
        if (!internalStorageDir.exists()) {
            internalStorageDir.mkdirs();  // Use mkdirs to create parent directories if necessary
        }

        // Create a file within the directory
        File imageFile = new File(internalStorageDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            // Write the bitmap to the file
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            // Return the Uri of the saved image
            return Uri.fromFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception, return null, or take appropriate action based on your requirements
            return null;
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri, OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener) {
        // Get a reference to the Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Create a reference to the "questions" folder and the image file
        StorageReference questionsRef = storageRef.child("questions/" + imageUri.getLastPathSegment());

        // Upload the file to Firebase Storage
        UploadTask uploadTask = questionsRef.putFile(imageUri);

        // Register observers to listen for when the upload is successful or if it fails
        uploadTask.addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                    exception.printStackTrace();
                    Log.e("Upload Image", "Image upload failed: " + exception.getMessage());
                    // You may want to log the error or show a message to the user
                });
    }

    private void onPostButtonClick() {
        // Get the selected community, title, description, and question image URI
        String selectedCommunity = chooseCommunityTextView.getText().toString();
        String givenTitle = title.getText().toString();
        String givenDescription = description.getText().toString();
        Log.d("Post", "Post: !" + selectedCommunity + givenTitle + givenDescription + selectedImageUri);

        if (!selectedCommunity.isEmpty() && !givenTitle.isEmpty() && !givenDescription.isEmpty()) {
            // Get the current user's UID
            String userId = userInstance.getUserid();

            // Get the current community ID based on the selected community name
            FirebaseFirestore.getInstance().collection("Community")
                    .whereEqualTo("title", selectedCommunity)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String communityId = queryDocumentSnapshots.getDocuments().get(0).getId();

                            // Create a Question object with the required data
                            Question question = new Question();

                            question.setUserID(userId);
                            question.setCommunityID(communityId);
                            question.setTitle(givenTitle);
                            question.setDescription(givenDescription);
                            question.setCreatedAt(new Timestamp(new Date()).toDate());

                            // Check if an image is selected
                            if (selectedImageUri != null) {
                                // Upload the image and set the URL in the callback
                                uploadImageToFirebaseStorage(selectedImageUri, taskSnapshot -> {
                                    // Get the download URL from the storage reference
                                    taskSnapshot.getStorage().getDownloadUrl()
                                            .addOnSuccessListener(uri -> {
                                                // Set the download URL in the Question object
                                                question.setQuestionImageUrl(uri.toString());

                                                // Add the question to the "Questions" collection in Firestore
                                                FirebaseFirestore.getInstance().collection("Questions")
                                                        .add(question)
                                                        .addOnSuccessListener(documentReference -> {
                                                            // Get the ID of the newly added question
                                                            String questionId = documentReference.getId();

                                                            // Update the question with the question ID
                                                            documentReference.update("questionId", questionId)
                                                                    .addOnSuccessListener(aVoid -> {

                                                                        chooseCommunityTextView.setText("");
                                                                        title.setText("");
                                                                        description.setText("");
                                                                        attachedImage.setImageDrawable(null);
                                                                        attachedImage.setVisibility(View.GONE);
                                                                        buttonRemoveImage.setVisibility(View.GONE);

                                                                        // Handle success
                                                                        question.setQuestionId(questionId);
                                                                        Log.d("Post", "Question added successfully with ID: " + questionId);

                                                                        Bundle bundle = new Bundle();
                                                                        bundle.putString("communityID", question.getCommunityID());

                                                                        // Create the destination fragment and set arguments
                                                                        Fragment communityFragment = new CommunityPageFragment();
                                                                        communityFragment.setArguments(bundle);

                                                                        // Replace the current fragment with the destination fragment
                                                                        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                                                                        fm.replace(R.id.container, communityFragment).addToBackStack(null).commit();
                                                                        // You can navigate to a success page or perform other actions
                                                                    })
                                                                    .addOnFailureListener(e -> {
                                                                        // Handle failure
                                                                        Log.e("Post", "Error updating question with ID", e);
                                                                    });
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            // Handle failure
                                                            Log.e("Post", "Error adding question", e);
                                                        });
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle failure to get download URL
                                                Log.e("Download URL", "Error getting download URL", e);
                                            });
                                });
                            } else {
                                // If no image is selected, directly add the question to Firestore
                                FirebaseFirestore.getInstance().collection("Questions")
                                        .add(question)
                                        .addOnSuccessListener(documentReference -> {
                                            // Get the ID of the newly added question
                                            String questionId = documentReference.getId();

                                            // Update the question with the question ID
                                            documentReference.update("questionId", questionId)
                                                    .addOnSuccessListener(aVoid -> {

                                                        chooseCommunityTextView.setText("");
                                                        title.setText("");
                                                        description.setText("");
                                                        attachedImage.setImageDrawable(null);
                                                        attachedImage.setVisibility(View.GONE);
                                                        buttonRemoveImage.setVisibility(View.GONE);

                                                        // Handle success
                                                        question.setQuestionId(questionId);
                                                        Log.d("Post", "Question added successfully with ID: " + questionId);

                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("communityID", question.getCommunityID());

                                                        // Create the destination fragment and set arguments
                                                        Fragment communityFragment = new CommunityPageFragment();
                                                        communityFragment.setArguments(bundle);

                                                        // Replace the current fragment with the destination fragment
                                                        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                                                        fm.replace(R.id.container, communityFragment).addToBackStack(null).commit();

                                                        // You can navigate to a success page or perform other actions
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Handle failure
                                                        Log.e("Post", "Error updating question with ID", e);
                                                    });
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle failure
                                            Log.e("Post", "Error adding question", e);
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to fetch community ID
                        Log.e("Fetch Community ID", "Error getting community ID", e);
                    });
        } else {
            // Handle the case when any of the fields is empty
            Log.e("Post", "Some fields are empty");
        }
    }
}