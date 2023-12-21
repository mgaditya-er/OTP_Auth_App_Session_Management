package com.example.otpapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    Button signout,btnlink;

    FusedLocationProviderClient fusedLocationProviderClient;
    TextView lattitude,longitude,address,city,country;
    Button getlocation;

    private final static int REQUEST_CODE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        signout = findViewById(R.id.signOutButton);

        lattitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        address = findViewById(R.id.address);
        city = findViewById(R.id.city);
        country = findViewById(R.id.country);
        getlocation = findViewById(R.id.getLocation);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        btnlink = findViewById(R.id.goToMap);

        getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Pune", Toast.LENGTH_SHORT).show();
                getLastlocation();
                
            }
        });


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
                finish();
            }
        });
    }

    private void getLastlocation() {

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if(location!=null)
                            {

                                try {
                                    Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                                    List<Address> addresses = null;

                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    lattitude.setText("Lattitude :"+addresses.get(0).getLatitude());
                                    longitude.setText("Longititude :"+addresses.get(0).getLongitude());
                                    address.setText("Address :"+addresses.get(0).getAddressLine(0));
                                    city.setText("City :"+addresses.get(0).getLocality());
                                    country.setText("Country :"+addresses.get(0).getCountryName());
                                    btnlink.setEnabled(true);

                                    List<Address> finalAddresses = addresses;
                                    btnlink.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Construct the Google Maps link
                                            String googleMapsLink = "https://www.google.com/maps?q=" +
                                                    finalAddresses.get(0).getLatitude() + "," +
                                                    finalAddresses.get(0).getLongitude();
                                            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                            ClipData clipData = ClipData.newPlainText("Google Maps Link", googleMapsLink);
                                            clipboardManager.setPrimaryClip(clipData);
                                            Toast.makeText(HomeActivity.this, "Link copied to clipboard", Toast.LENGTH_SHORT).show();

                                            // Open the link in a web browser
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(googleMapsLink));
                                            startActivity(intent);
                                        }
                                    });

                                } catch (IOException e) {
                                    Toast.makeText(HomeActivity.this, e+"error", Toast.LENGTH_SHORT).show();

                                    throw new RuntimeException(e);
                                }

                            }
                        }
                    });

        }
        else {
            askpermission();
        }
    }

    private void askpermission() {

        ActivityCompat.requestPermissions(HomeActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE)
        {
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getLastlocation();

            }else {


                Toast.makeText(HomeActivity.this,"Please provide the required permission",Toast.LENGTH_SHORT).show();

            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}