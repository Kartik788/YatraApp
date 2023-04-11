package com.example.vehiclebookingapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Map;

public class bookTrip extends AppCompatActivity {

    TextView driverName, driverNumber, driverAge, driverMail, driverGender;
    TextView vehicleName, vehicleColor, vehicleNumber, vehicleSeats, tirthaPlace;
    TextView price;
    CircleImageView driverPic, vehiclePic;
    TextView bookRide;
    ProgressBar progressBar;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_trip);

        uid = getIntent().getStringExtra("UID");

        progressBar = findViewById(R.id.progress_booktrip);
        tirthaPlace = findViewById(R.id.TirthaPlace);
        driverName = findViewById(R.id.driverName);
        driverNumber = findViewById(R.id.driverPhone);
        driverAge = findViewById(R.id.driverAge);
        driverMail = findViewById(R.id.driverMail);
        driverGender = findViewById(R.id.driverGender);
        vehicleName = findViewById(R.id.vehicleName);
        vehicleColor = findViewById(R.id.vehicleColor);
        vehicleNumber = findViewById(R.id.vehicleNumber);
        vehicleSeats = findViewById(R.id.vehicleSeats);
        price = findViewById(R.id.price);

        driverPic = findViewById(R.id.driverProfilePic);
        vehiclePic = findViewById(R.id.vehiclePic);
        bookRide = findViewById(R.id.bookRide);

        fetchDriverInfo(uid);

        bookRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookRide();
            }
        });
    }

    private void BookRide() {

        bookRide.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        fetchLocation();


    }

    @SuppressLint("MissingPermission")
    private void fetchLocation() {
        final FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(bookTrip.this);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled("gps") || locationManager.isProviderEnabled("network")) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                public void onComplete(Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        sendLocationToFirebase(location.getLatitude(), location.getLongitude());
                        return;
                    }

                    fusedLocationProviderClient.requestLocationUpdates(new LocationRequest().setPriority(100).setInterval(10000).setFastestInterval(1000).setNumUpdates(1), new LocationCallback() {
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            Location location1 = locationResult.getLastLocation();
                            sendLocationToFirebase(location.getLatitude(), location.getLongitude());
                        }
                    }, Looper.myLooper());
                }
            });
        } else {
            startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void sendLocationToFirebase(Double lat, Double lon) {
        Map map = new HashMap();
        map.put("Latitude", lat);
        map.put("Longitude", lon);
        map.put("isTripLive", true);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("CUSTOMERS").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(map).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                updateDriverInfo();
            }
        });
    }

    private void updateDriverInfo() {
        Map map = new HashMap();
        map.put("isTripLive", true);
        map.put("isLive", false);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("DRIVERS").document(uid).update(map).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

                bookRide.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                Intent intent = new Intent(getApplicationContext(), googleMap.class);
                intent.putExtra("UID", uid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }

    private void fetchDriverInfo(String uid) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("DRIVERS").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Glide.with(bookTrip.this).load(documentSnapshot.get("ProfilePhoto").toString()).into(driverPic);
                Glide.with(bookTrip.this).load(documentSnapshot.get("vehiclePhoto").toString()).into(vehiclePic);

                driverName.setText(documentSnapshot.get("FirstName").toString() + " " + documentSnapshot.get("LastName").toString());
                driverNumber.setText(documentSnapshot.get("number").toString());
                driverAge.setText(documentSnapshot.get("Age").toString());
                driverMail.setText(documentSnapshot.get("Email").toString());
                driverGender.setText(documentSnapshot.get("Gender").toString());

                tirthaPlace.setText(documentSnapshot.get("TirthaPlace").toString() + " tour");
                vehicleName.setText(documentSnapshot.get("VehicleName").toString());
                vehicleColor.setText(documentSnapshot.get("VehicleColor").toString());
                vehicleNumber.setText(documentSnapshot.get("RCNumber").toString());
                vehicleSeats.setText(documentSnapshot.get("Seats").toString() + " seater");

                price.setText(documentSnapshot.get("Price").toString());


            }
        });
    }
}