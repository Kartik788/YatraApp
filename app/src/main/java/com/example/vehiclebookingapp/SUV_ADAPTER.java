package com.example.vehiclebookingapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SUV_ADAPTER extends ArrayAdapter<String> {
    public SUV_ADAPTER(Context context, ArrayList<String> drivers) {
        super(context, 0, drivers);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.suv_row, parent, false);
        }
        String driver_uid = getItem(position);

        ImageView suv_image = listitemView.findViewById(R.id.suv_photo);
        TextView trip_price = listitemView.findViewById(R.id.trip_price);
        TextView tirtha_place = listitemView.findViewById(R.id.tirthaPlace);
        TextView vehicle = listitemView.findViewById(R.id.vehicle);
        ProgressBar progressBar = listitemView.findViewById(R.id.progress_id);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("DRIVERS").document(driver_uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Glide.with(getContext()).load(documentSnapshot.get("vehiclePhoto").toString()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(suv_image);
                trip_price.setText(documentSnapshot.get("Price").toString());
                tirtha_place.setText(documentSnapshot.get("TirthaPlace").toString()+" Trip");
                vehicle.setText(documentSnapshot.get("VehicleName").toString());
            }
        });


        return listitemView;
    }
}
