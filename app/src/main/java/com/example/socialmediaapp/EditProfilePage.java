package com.example.socialmediaapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class EditProfilePage extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String storagePath = "Users_Profile_Cover_image/";
    String uid;
    ImageView profilePicImageView;
    TextView editName, editPassword;
    ProgressDialog pd;
    String[] cameraPermission;
    String[] storagePermission;
    Uri imageuri;
    String profileOrCoverPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_page);

        initLayout();
        initFirebase();
        loadProfilePicture();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storagePermission = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        }

        editPassword.setOnClickListener(v -> showPasswordChangeDialog());

        profilePicImageView.setOnClickListener(v -> {
            profileOrCoverPhoto = "image";
            showImagePicDialog();
        });

        editName.setOnClickListener(v -> showNamePhoneUpdate("name"));
    }

    private void loadProfilePicture() {
        if (firebaseUser != null){
            databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String image = snapshot.child("image").getValue(String.class);
                    if (image != null){
                        Glide.with(EditProfilePage.this).load(image).into(profilePicImageView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EditProfilePage.this, "Failed to load profile picture", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = firebaseDatabase.getReference("Users");
    }

    private void initLayout() {
        editName = findViewById(R.id.editName);
        profilePicImageView = findViewById(R.id.setting_profile_image);
        editPassword = findViewById(R.id.changePassword);
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String image = "" + dataSnapshot1.child("image").getValue();

                    try {
                        Glide.with(EditProfilePage.this).load(image).into(profilePicImageView);
                    } catch (Exception ignored) {
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editPassword.setOnClickListener(v -> {
            pd.setMessage("Changing Password");
            showPasswordChangeDialog();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String image = "" + dataSnapshot1.child("image").getValue();

                    try {
                        Glide.with(EditProfilePage.this).load(image).into(profilePicImageView);
                    } catch (Exception ignored) {
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        editPassword.setOnClickListener(v -> {
            pd.setMessage("Changing Password");
            showPasswordChangeDialog();
        });
    }

    // checking storage permission ,if given then we can add something in our storage
    private Boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    // requesting for storage permission
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    // checking camera permission ,if given then we can click image using our camera
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // requesting for camera permission if not given
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    // We will show an alert box where we will write our old and new password
    private void showPasswordChangeDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_password, null);
        final EditText oldPass = view.findViewById(R.id.oldpasslog);
        final EditText newPass = view.findViewById(R.id.newpasslog);
        Button editPass = view.findViewById(R.id.updatepass);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        editPass.setOnClickListener(v -> {
            String oldP = oldPass.getText().toString().trim();
            String newp = newPass.getText().toString().trim();
            if (TextUtils.isEmpty(oldP)) {
                Toast.makeText(EditProfilePage.this, "Current Password cant be empty", Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(newp)) {
                Toast.makeText(EditProfilePage.this, "New Password cant be empty", Toast.LENGTH_LONG).show();
                return;
            }
            dialog.dismiss();
            updatePassword(oldP, newp);
        });
    }

    // Now we will check that if old password was authenticated
    // correctly then we will update the new password
    private void updatePassword(String oldP, final String newp) {
        pd.show();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldP);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(aVoid -> user.updatePassword(newp)
                        .addOnSuccessListener(aVoid1 -> {
                            pd.dismiss();
                            Toast.makeText(EditProfilePage.this, "Changed Password", Toast.LENGTH_LONG).show();
                        }).addOnFailureListener(e -> {
                            pd.dismiss();
                            Toast.makeText(EditProfilePage.this, "Failed", Toast.LENGTH_LONG).show();
                        })).addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(EditProfilePage.this, "Failed", Toast.LENGTH_LONG).show();
                });
    }

    // Updating name
    private void showNamePhoneUpdate(final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update" + key);

        // creating a layout to write the new name
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(10, 10, 10, 10);
        final EditText editText = new EditText(this);
        editText.setHint("Enter" + key);
        layout.addView(editText);
        builder.setView(layout);

        builder.setPositiveButton("Update", (dialog, which) -> {
            final String value = editText.getText().toString().trim();
            if (!TextUtils.isEmpty(value)) {
                pd.show();

                // Here we are updating the new name
                HashMap<String, Object> result = new HashMap<>();
                result.put(key, value);
                databaseReference.child(firebaseUser.getUid()).updateChildren(result).addOnSuccessListener(aVoid -> {
                    pd.dismiss();

                    // after updated we will show updated
                    Toast.makeText(EditProfilePage.this, " updated ", Toast.LENGTH_LONG).show();
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(EditProfilePage.this, "Unable to update", Toast.LENGTH_LONG).show();
                });
                if (key.equals("name")) {
                    final DatabaseReference db = FirebaseDatabase.getInstance().getReference("Posts");
                    Query query = db.orderByChild("uid").equalTo(uid);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                db.getKey();
                                dataSnapshot1.getRef().child("uname").setValue(value);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            } else {
                Toast.makeText(EditProfilePage.this, "Unable to update", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> pd.dismiss());
        builder.create().show();
    }

    // Here we are showing image pic dialog where we will select
    // and image either from camera or gallery
    private void showImagePicDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, (dialog, which) -> {
            // if access is not given then we will request for permission
            if (which == 0) {
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickFromCamera();
                }
            } else if (which == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickFromGallery();
                }
            }
        });
        builder.create().show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    // Here we will click a photo and then go to startActivityForResult for updating data

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Handle the result here, if needed
                    uploadProfileCoverPhoto(imageuri);
                } else {
                    // Handle case when the user cancels the camera operation
                    Toast.makeText(this, "Camera operation cancelled", Toast.LENGTH_SHORT).show();
                }
            });

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        imageuri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
        cameraLauncher.launch(cameraIntent);
    }

    // We will select an image from gallery

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {
                    // Handle the result here, if needed
                    // For example, you can use the result Uri directly
                    imageuri = result;
                    uploadProfileCoverPhoto(imageuri);
                } else {
                    // Handle case when the user cancels the gallery operation
                    Toast.makeText(this, "Gallery operation cancelled", Toast.LENGTH_SHORT).show();
                }
            });

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        galleryLauncher.launch("image/*");
    }

    // We will upload the image from here.
    private void uploadProfileCoverPhoto(final Uri uri) {
        pd.show();

        // We are taking the filepath as storagePath + firebaseAuth.getUid()+".png"
        String filePathName = storagePath + profileOrCoverPhoto + "_" + firebaseUser.getUid();
        StorageReference storageReference1 = storageReference.child(filePathName);
        storageReference1.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

            uriTask.addOnSuccessListener(downloadUri -> {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(profileOrCoverPhoto, downloadUri.toString());
                databaseReference.child(firebaseUser.getUid()).updateChildren(hashMap)
                        .addOnSuccessListener(aVoid -> {
                           pd.dismiss();
                            Toast.makeText(EditProfilePage.this, "Updated", Toast.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(EditProfilePage.this, "Error Updating", Toast.LENGTH_LONG).show());
            }).addOnFailureListener(e -> {
                pd.dismiss();
                Toast.makeText(EditProfilePage.this, "Error getting download URL", Toast.LENGTH_LONG).show();
            });
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(EditProfilePage.this, "Error uploading image", Toast.LENGTH_LONG).show();
        });
    }
}
