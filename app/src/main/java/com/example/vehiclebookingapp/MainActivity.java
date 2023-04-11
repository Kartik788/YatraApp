package com.example.vehiclebookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    GridView gridView;
    ArrayList<String> uids;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        uids = new ArrayList<>();

        gridView = findViewById(R.id.suv_grid);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);


        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // Handle navigation view item clicks here
                switch (menuItem.getItemId()) {
                    case R.id.nav_rating:
                        Toast.makeText(MainActivity.this, "home", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_yatra_list:
                        startActivity(new Intent(MainActivity.this, TirthaPlaces.class));
                        break;
                    case R.id.nav_profile:
                        startActivity(new Intent(MainActivity.this, createProfile.class));
                        break;
                    case R.id.nav_driver:
                        // Handle the gallery action
                        break;
                    case R.id.nav_logout:
                        // Handle the logout action
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this, phoneNumberAuthentication.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                }

                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        isLiveTripGoingOn();
        fetchFromFirebase();

    }

    private void isLiveTripGoingOn() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("CUSTOMERS").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(Boolean.valueOf(documentSnapshot.get("isTripLive").toString()) == true){
                    Intent intent = new Intent(getApplicationContext(), googleMap.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    private void placeActiveDriversInGridView() {

        SUV_ADAPTER suv_adapter = new SUV_ADAPTER(this, uids);
        gridView.setAdapter(suv_adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent in = new Intent(MainActivity.this, bookTrip.class);
                in.putExtra("UID", uids.get(i));
                startActivity(in);
            }
        });
    }

    private void fetchFromFirebase() {
        uids.clear();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("DRIVERS").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (Boolean.valueOf(documentSnapshot.get("isLive").toString()) == true && Boolean.valueOf(documentSnapshot.get("isVerified").toString()) == true) {
                        uids.add(documentSnapshot.getId());
                    }
                }

                if (uids.isEmpty()) {

                } else {

                    gridView.setVisibility(View.VISIBLE);
                    placeActiveDriversInGridView();
                }


            }
        });
    }


}

