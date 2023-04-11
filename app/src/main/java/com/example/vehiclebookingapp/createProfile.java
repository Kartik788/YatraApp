package com.example.vehiclebookingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class createProfile extends AppCompatActivity {

    ProgressBar progressBar;

    EditText firstName, lastName, age, email;
    CircleImageView profilePhoto;
    TextView saveCustomerInfo;
    Uri ProfilePhotoUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        isProfileCreated();

        progressBar = findViewById(R.id.progressBar);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        age = findViewById(R.id.age);
        email = findViewById(R.id.email);
        profilePhoto = findViewById(R.id.profilePhoto);
        saveCustomerInfo = findViewById(R.id.saveCustomerProfile);

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profilePhotoChooser();
            }
        });

        saveCustomerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit_customer_info();
            }
        });
    }

    private void isProfileCreated() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("DRIVERS").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("FirstName") != null) {
                    Intent intent = new Intent(createProfile.this, EnableLocation.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    public void submit_customer_info() {
        if (checkForIncompleteInfo()) {
            saveCustomerInfo.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            uploadProfilePhoto(ProfilePhotoUri);
            uploadToFirebase();
        } else {
            Toast.makeText(this, "incomplete info", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkForIncompleteInfo() {
        if (ProfilePhotoUri != null && !firstName.getText().toString().trim().isEmpty() && !lastName.getText().toString().trim().isEmpty() && !age.getText().toString().trim().isEmpty() && !email.getText().toString().trim().isEmpty()) {
            return true;
        }
        return false;
    }

    public void profilePhotoChooser() {
        Intent imageChooserIntent = new Intent();
        imageChooserIntent.setType("image/*");
        imageChooserIntent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(imageChooserIntent, "Select Picture"), 10);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case 10:
                    ProfilePhotoUri = data.getData();
                    profilePhoto.setImageURI(ProfilePhotoUri);
                    return;

                default:
                    return;
            }
        }
    }

    public void uploadProfilePhoto(Uri image_uri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(String.valueOf(System.currentTimeMillis()));
        storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        HashMap map = new HashMap<>();
                        map.put("ProfilePhoto", uri.toString());
                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                        firebaseFirestore.collection("CUSTOMERS").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(map).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                progressBar.setVisibility(View.GONE);
                                saveCustomerInfo.setVisibility(View.VISIBLE);
                                //finish
                                Toast.makeText(createProfile.this, "pic saved", Toast.LENGTH_SHORT).show();
//                                if(isLocationPersmissionEnabled()){
//                                    startActivity(new Intent(createProfile.this, MainActivity.class));
//                                }else{
//                                    startActivity(new Intent(createProfile.this, EnableLocation.class));
//                                }

                                Intent intent = new Intent(createProfile.this, EnableLocation.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }
                        });

                    }
                });
            }
        });
    }

    public void uploadToFirebase() {
        Map map = new HashMap();
        map.put("FirstName", firstName.getText().toString().trim());
        map.put("LastName", lastName.getText().toString().trim());
        map.put("Age", age.getText().toString().trim());
        map.put("Email", email.getText().toString().trim());
        map.put("isTripLive", false);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("CUSTOMERS").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(map).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(createProfile.this, "profile saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isLocationPersmissionEnabled() {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 && ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            return true;
        } else {
            return false;
        }
    }


}