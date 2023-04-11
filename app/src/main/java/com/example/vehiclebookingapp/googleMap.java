package com.example.vehiclebookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class googleMap extends AppCompatActivity {

    private GoogleMap mMap;
    private MapView mMapView;

    private WebView webView;
    String uid;
    String c_lat,c_long;
    String d_lat,d_long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        webView = findViewById(R.id.webView);

        uid = getIntent().getStringExtra("UID");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        fetchLocation();


//        mMapView = findViewById(R.id.map);
//        mMapView.onCreate(savedInstanceState);
//        mMapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap googleMap) {
//                mMap = googleMap;
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(20.5937, 78.9629)));
//                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                    @Override
//                    public void onMapClick(@NonNull LatLng latLng) {
//                        Toast.makeText(googleMap.this, String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude), Toast.LENGTH_SHORT).show();
//                    }
//                });

//                LatLng india = new LatLng(20.5937,78.9629);
//                mMap.addMarker(new MarkerOptions()
//                        .position(india)
//                        .title("Inida"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(india));
//            }
//        });


    }

    private void fetchLocation(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("DRIVERS").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                c_lat = documentSnapshot.get("Latitude").toString();
                c_long = documentSnapshot.get("Longitude").toString();

                firebaseFirestore.collection("DRIVERS").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        d_lat = documentSnapshot.get("Latitude").toString();
                        d_long = documentSnapshot.get("Longitude").toString();

                        webView.loadUrl("http://maps.google.com/maps?saddr="+c_lat+","+c_long+"&daddr="+d_lat+","+d_long);

                    }
                });
            }
        });
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        mMapView.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mMapView.onPause();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mMapView.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mMapView.onLowMemory();
//    }
}