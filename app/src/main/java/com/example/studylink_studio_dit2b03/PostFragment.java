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
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class PostFragment extends Fragment  {
    User userInstance = User.getInstance();
    EditText communityNameEditText,communityDescriptionEditText;
    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;

    private ImageView attachedImage;
    private Uri selectedImageUri;
private      TextView chooseCommunityTextView;
private Button buttonRemoveImage;
private EditText title, description;
    public PostFragment() {
        // Required empty public constructor
    }
    Dialog dialogCommunity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        if (userInstance.getRoleid() == 1) {
            view = inflater.inflate(R.layout.fragment_student_post, container, false);
            chooseCommunityTextView = view.findViewById(R.id.chooseCommunity);
            title = view.findViewById(R.id.editTextTitle);
            description = view.findViewById(R.id.editTextDescription);
            //choose community
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
// Set visibility to visible and load the attached image using Glide
            attachedImage.setVisibility(View.VISIBLE);
             Button attachImageButton=view.findViewById(R.id.buttonAttachImage);

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
            Button postButton = view.findViewById(R.id.buttonPost);

            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPostButtonClick();
                }
            });
            return view;
        }else if (userInstance.getRoleid() == 2) {
            view = inflater.inflate(R.layout.fragment_tutor_post, container, false);

            // Set click listener for the "Create Community" button
            Button createCommunityButton = view.findViewById(R.id.btnCreateCommunity);
            createCommunityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toggle visibility of the EditText fields for community name and description
                    communityNameEditText = view.findViewById(R.id.etCommunityTitle);
                    communityDescriptionEditText = view.findViewById(R.id.etCommunityDescription);
                    Button createButton = view.findViewById(R.id.btnCreateNewCommunity);

                    int visibility = communityNameEditText.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;

                    communityNameEditText.setVisibility(visibility);
                    communityDescriptionEditText.setVisibility(visibility);
                    createButton.setVisibility(visibility);
                }
            });

            // Set click listener for the "Create" button
            Button createButton = view.findViewById(R.id.btnCreateNewCommunity);
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String communityTitle = communityNameEditText.getText().toString();
                    String communityDescription = communityDescriptionEditText.getText().toString();

                    // Check if the community name and description are not empty
                    if (!communityTitle.isEmpty() && !communityDescription.isEmpty()) {
                        Community community = new Community();
                        community.setTitle(communityTitle);
                        community.setDescription(communityDescription);
                        community.setCreatorId(userInstance.getUserid());

                        Date creationDate = new Date(System.currentTimeMillis());
                        community.setCreationTimestamp(creationDate);
                        community.setMemberCount(1);

                        // Add the community to the 'Community' collection
                        FirebaseFirestore.getInstance().collection("Community")
                                .add(community)
                                .addOnSuccessListener(documentReference -> {
                                    String communityId = documentReference.getId();
                                    community.setCommunityId(communityId);

                                    // Set the community document with the updated community ID
                                    documentReference.set(community)
                                            .addOnSuccessListener(aVoid -> {
                                                // Create the 'members' subcollection for the community
                                                FirebaseFirestore.getInstance().collection("Community")
                                                        .document(communityId)
                                                        .collection("members")
                                                        .document(userInstance.getUserid())
                                                        .set(new HashMap<>())
                                                        .addOnSuccessListener(aVoid1 -> {
                                                            // Navigate to community details after successfully creating the community and 'members' subcollection
                                                            navigateToCommunityDetails(community);
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            // Handle failure to create 'members' subcollection
                                                        });
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle failure to update the community document with the community ID
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure to add the community document
                                });
                    } else {
                        // Handle the case when either the name or description is empty
                        Log.e("create new community", "Community name or description is empty");
                    }
                }

            });

            return view;
        }  else {
            Intent intent = new Intent(requireContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
            // Return an empty view if the user is not logged in
            return new View(requireContext());
        }
    }

    private void navigateToCommunityDetails(Community community){
      Bundle bundle = new Bundle();
        bundle.putString("communityID", community.getCommunityId());
        Log.d("communityID", community.getCommunityId());
        // Create the destination fragment and set arguments
        Fragment communityFragment = new CommunityPageFragment();
        communityFragment.setArguments(bundle);

        // Replace the current fragment with the destination fragment
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.container, communityFragment).addToBackStack(null).commit();


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

                            // Create a Post object with the required data
                            Question question = new Question();

                            question.setUserID(userId);
                            question.setCommunityID(communityId);
                            question.setTitle(givenTitle);
                            question.setDescription(givenDescription);

                            // Check if an image is selected
                            if (selectedImageUri != null) {
                                // Upload the image and set the URL in the callback
                                uploadImageToFirebaseStorage(selectedImageUri, taskSnapshot -> {
                                    // Get the download URL from the storage reference
                                    taskSnapshot.getStorage().getDownloadUrl()
                                            .addOnSuccessListener(uri -> {
                                                // Set the download URL in the Question object
                                                question.setQuestionImageUrl(uri.toString());

                                                // Add the post to the "Post" collection in Firestore
                                                FirebaseFirestore.getInstance().collection("Questions")
                                                        .add(question)
                                                        .addOnSuccessListener(documentReference -> {
                                                            // Handle success
                                                            Log.d("Post", "Post added successfully!");
                                                            // You can navigate to a success page or perform other actions
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            // Handle failure
                                                            Log.e("Post", "Error adding post", e);
                                                        });
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle failure to get download URL
                                                Log.e("Download URL", "Error getting download URL", e);
                                            });
                                });
                            } else {
                                // If no image is selected, directly add the post to Firestore
                                FirebaseFirestore.getInstance().collection("Questions")
                                        .add(question)
                                        .addOnSuccessListener(documentReference -> {
                                            // Handle success
                                            Log.d("Post", "Post added successfully!");
                                            // You can navigate to a success page or perform other actions
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle failure
                                            Log.e("Post", "Error adding post", e);
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





