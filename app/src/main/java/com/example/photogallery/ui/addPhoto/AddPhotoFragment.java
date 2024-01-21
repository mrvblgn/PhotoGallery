package com.example.photogallery.ui.addPhoto;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.photogallery.R;
import com.example.photogallery.ui.addLabel.AddLabelFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AddPhotoFragment extends Fragment {

    private ActivityResultLauncher<String> mGetContent;
    private ImageView imageView;
    private List<String> selectedLabels;
    private FirebaseAuth mAuth;

    // Firebase
    private FirebaseDatabase database;
    private StorageReference storageRef;
    private DatabaseReference myRef;
    private FirebaseStorage storage;
    private DatabaseReference photoRef;
    private static ArrayList<String> labelList;
    private ArrayAdapter<String> adapter;
    private ArrayList<CheckBox> checkBoxList;

    public class User {
        private String username;
        private String email;

        public User() {
            // Boş parametreli constructor Firebase tarafından kullanılır.
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }
    }
    public class PhotoData {
        private String userId;
        private String userEmail;
        private List<String> labels;
        private String photoUrl;

        public PhotoData(String userId, String userEmail, List<String> labels, String photoUrl) {
            this.userId = userId;
            this.userEmail = userEmail;
            this.labels = labels;
            this.photoUrl = photoUrl;
        }

        public String getUserId() {
            return userId;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public List<String> getLabels() {
            return labels;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }
    }

    private static class Photo {
        private String imageUrl;
        private List<String> labels;

        public Photo() {
            // Default constructor required for Firebase
        }

        public Photo(String imageUrl, List<String> labels) {
            this.imageUrl = imageUrl;
            this.labels = labels;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public List<String> getLabels() {
            return labels;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("photos");

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Initialize Firebase Realtime Database reference for photos
        photoRef = database.getReference("photos");

        mAuth = FirebaseAuth.getInstance();

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        // Handle the returned Uri
                        ImageView imageView = getView().findViewById(R.id.image_view);
                        imageView.setImageURI(uri);

                        // Upload the image to Firebase Storage
                        uploadImage(uri);
                    }
                });
    }

    private void uploadImage(Uri imageUri) {
        // Create a unique filename for the image
        String imageName = "image_" + System.currentTimeMillis() + ".jpg";

        // Create a reference to the Firebase Storage location
        StorageReference imageRef = storageRef.child(imageName);

        // Upload the image
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    // Get the download URL of the uploaded image
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save the image URL and associated labels to Firebase Database
                        saveImageInfoToDatabase(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }
    private void saveImageInfoToDatabase(String imageUrl) {
        // Get selected labels from the CheckBoxes
        LinearLayout labelLayout = getView().findViewById(R.id.label_layout);
        List<String> selectedLabels = new ArrayList<>();

        for (int i = 0; i < labelLayout.getChildCount(); i++) {
            View view = labelLayout.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    selectedLabels.add(checkBox.getText().toString());
                }
            }
        }

        // Create a Photo object with image URL and labels
        Photo photo = new Photo(imageUrl, selectedLabels);

        // Push the Photo object to Firebase Database
        String photoKey = myRef.push().getKey();
        myRef.child(photoKey).setValue(photo);

        Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_photo, container, false);

        imageView = view.findViewById(R.id.image_view);
        LinearLayout labelLayout = view.findViewById(R.id.label_layout);

        // Populate labels from AddLabelFragment
        for (String label : AddLabelFragment.labelList) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(label);
            labelLayout.addView(checkBox);
        }

        Button addPhotoButton = view.findViewById(R.id.add_photo_button);
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        Button registerButton = view.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhotoToFirebase();
            }
        });

        return view;
    }

    private void retrieveLabelsFromFirebase() {

        // User bilgilerini çekmek için yeni bir DatabaseReference oluştur
        DatabaseReference userRef = database.getReference("users").child(mAuth.getCurrentUser().getUid());

        if (myRef != null) {
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                    labelList.clear();
                    for (com.google.firebase.database.DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String labelText = snapshot.getValue(String.class);
                        labelList.add(labelText);
                    }
                    uploadPhotoToFirebase();
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Hataları ele alın
                }
            });
        }
    }

    private void uploadPhotoToFirebase() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Get user information
            String userId = user.getUid();
            String userEmail = user.getEmail();

            // Get selected labels
            selectedLabels = getSelectedLabels();

            // Upload photo to Firebase Storage
            StorageReference photoStorageRef = storageRef.child(userId + System.currentTimeMillis() + ".jpg");
            photoStorageRef.putFile(getImageUri())
                    .addOnSuccessListener(taskSnapshot -> {
                        // Photo uploaded successfully
                        photoStorageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Get the photo URL
                            String photoUrl = uri.toString();

                            // Save data to Firebase Realtime Database
                            PhotoData photoData = new PhotoData(userId, userEmail, selectedLabels, photoUrl);
                            String photoKey = photoRef.push().getKey(); // Unique key for the photo
                            photoRef.child(photoKey).setValue(photoData);

                            // Inform the user
                            Toast.makeText(getContext(), "Photo uploaded successfully!", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors during photo upload
                        Toast.makeText(getContext(), "Failed to upload photo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }



    private List<String> getSelectedLabels() {
        List<String> labels = new ArrayList<>();
        LinearLayout labelLayout = getView().findViewById(R.id.label_layout);
        for (int i = 0; i < labelLayout.getChildCount(); i++) {
            View view = labelLayout.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    labels.add(checkBox.getText().toString());
                }
            }
        }
        return labels;
    }
    private Uri getImageUri() {
        // ImageView'a atanmış olan resmi al
        Drawable drawable = imageView.getDrawable();

        if (drawable instanceof BitmapDrawable) {
            // Eğer resim bir BitmapDrawable ise, Bitmap nesnesini al
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            // Bitmap'i MediaStore.Images.Media insert ederek bir URI elde et
            String imageUriString = MediaStore.Images.Media.insertImage(
                    requireActivity().getContentResolver(),
                    bitmap,
                    "Title",
                    "Description"
            );

            // URI'yi parse et ve döndür
            return Uri.parse(imageUriString);
        }
        return null;
    }


}
