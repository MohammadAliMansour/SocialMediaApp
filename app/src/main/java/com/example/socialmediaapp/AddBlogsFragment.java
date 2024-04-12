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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;


public class AddBlogsFragment extends Fragment {

    public AddBlogsFragment() {
        // Required empty public constructor
    }

    FirebaseAuth firebaseAuth;
    EditText title, des;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    String[] cameraPermission;
    String[] storagePermission;
    ProgressDialog pd;
    ImageView image;
    Uri imageuri = null;
    String name, email, uid, dp;
    DatabaseReference databaseReference;
    Button upload;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_blogs, container, false);

        initLayout(view);

        // Retrieving the user data like name ,email and profile pic using query
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = databaseReference.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    name = String.valueOf(dataSnapshot1.child("name").getValue());
                    email = "" + dataSnapshot1.child("email").getValue();
                    dp = String.valueOf(dataSnapshot1.child("image").getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Initialising camera and storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storagePermission = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        }

        // After click on image we will be selecting an image
        image.setOnClickListener(v -> showImagePicDialog());

        // Now we will upload out blog
        upload.setOnClickListener(v -> {
            String title = AddBlogsFragment.this.title.getText().toString().trim();
            String description = des.getText().toString().trim();

            // If empty set error
            if (TextUtils.isEmpty(title)) {
                AddBlogsFragment.this.title.setError("Title Cant be empty");
                Toast.makeText(getContext(), "Title can't be left empty", Toast.LENGTH_LONG).show();
                return;
            }

            // If empty set error
            if (TextUtils.isEmpty(description)) {
                des.setError("Description Cant be empty");
                Toast.makeText(getContext(), "Description can't be left empty", Toast.LENGTH_LONG).show();
                return;
            }

            // If empty show error
            if (imageuri == null) {
                Toast.makeText(getContext(), "Select an Image", Toast.LENGTH_LONG).show();
            } else {
                uploadData(title, description, imageuri);
            }
        });
        return view;
    }

    private void initLayout(View view) {
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        title = view.findViewById(R.id.ptitle);
        des = view.findViewById(R.id.pdes);
        image = view.findViewById(R.id.imagep);
        upload = view.findViewById(R.id.pupload);
        pd = new ProgressDialog(getContext());
        pd.setCanceledOnTouchOutside(false);
        getActivity().getIntent();
    }

    private void showImagePicDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Pick Image From");
        builder.setItems(options, (dialog, which) -> {
            // check for the camera and storage permission if
            // not given the request for permission
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

    // check for storage permission
    private Boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
    }

    // if not given then request for permission after that check if request is given or not
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    // if request access given the pick data
                    if (camera_accepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(getContext(), "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }

            // function end
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    // if request access given the pick data
                    if (writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(getContext(), "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    // request for permission to write data into storage
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    // check camera permission to click picture using camera
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // request for permission to click photo using camera in app
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    // if access is given then pick image from camera and then put
    // the imageuri in intent extra and pass to startactivityforresult
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Handle the result here, if needed
                    imageuri = result.getData().getData();
                    image.setImageURI(imageuri);
                } else {
                    // Handle case when the user cancels the camera operation
                    Toast.makeText(getContext(), "Camera operation cancelled", Toast.LENGTH_SHORT).show();
                }
            });

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        imageuri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
        cameraLauncher.launch(cameraIntent);
    }

    // if access is given then pick image from gallery

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {
                    // Handle the result here, if needed
                    // For example, you can use the result Uri directly
                    imageuri = result;
                    image.setImageURI(imageuri);
                } else {
                    // Handle case when the user cancels the gallery operation
                    Toast.makeText(getContext(), "Gallery operation cancelled", Toast.LENGTH_SHORT).show();
                }
            });

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        galleryLauncher.launch("image/*");
    }



    // Upload the value of blog data into firebase
    private void uploadData(final String title, final String description, final Uri uri) {
        // show the progress dialog box
        pd.setMessage("Publishing Post");
        pd.show();
        final String timestamp = String.valueOf(System.currentTimeMillis());
        String filePathName = "Posts/" + "post" + timestamp;

        // initialising the storage reference for updating the data
        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child(filePathName);
        storageReference1.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            // getting the url of image uploaded
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

            uriTask.addOnSuccessListener(downloadUri -> {
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("uid", uid);
                hashMap.put("uname", name);
                hashMap.put("uemail", email);
                hashMap.put("udp", dp);
                hashMap.put("title", title);
                hashMap.put("description", description);
                hashMap.put("uimage", String.valueOf(downloadUri));
                hashMap.put("ptime", timestamp);
                hashMap.put("plike", "0");
                hashMap.put("pcomments", "0");


                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
                databaseReference.child(timestamp).setValue(hashMap)
                        .addOnSuccessListener(aVoid -> {
                            pd.dismiss();
                            Toast.makeText(getContext(),"Published", Toast.LENGTH_LONG).show();
                            this.title.setText("");
                            des.setText("");
                            image.setImageURI(null);
                            imageuri = null;
                            startActivity(new Intent(getContext(), DashboardActivity.class));
                            getActivity().finish();
                        }).addOnFailureListener(e -> {
                           pd.dismiss();
                           Toast.makeText(getContext(),"Failed", Toast.LENGTH_LONG).show();
                        });
            }).addOnFailureListener(e -> {
                pd.dismiss();
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
            });
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
        });
    }

}
