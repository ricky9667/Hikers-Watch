package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // add <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" /> in manifest
        // setting up locationManager and locationListener
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // update location when location changes
                updateLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // ask for permission when starting app if permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // get location
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation != null) {
                updateLocation(lastKnownLocation);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // request location updates
                locationListening();
            }
        }
    }

    public void locationListening() {
        // get location if permission granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public void refresh(View view) {
        // button for refreshing location

        Toast.makeText(getApplicationContext(), "Updating Your Location...", Toast.LENGTH_SHORT).show();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation != null) {
                updateLocation(lastKnownLocation);
            }
        }
    }

    public void updateLocation(Location location) {
        // updating location information
        Log.i("Location", location.toString());

        TextView latitudeText = findViewById(R.id.latitudeTextView);
        TextView longitudeText = findViewById(R.id.longitudeTextView);
        TextView accuracyText = findViewById(R.id.accuracyTextView);
        TextView altitudeText = findViewById(R.id.altitudeTextView);
        TextView addressText = findViewById(R.id.addressTextView);

        // set textviews for location info
        latitudeText.setText("Latitude: " + location.getLatitude());
        longitudeText.setText("Longitude: " + location.getLongitude());
        accuracyText.setText("Accuracy: " + location.getAccuracy());
        altitudeText.setText("Altitude: " + location.getAltitude());

        // set address text
        String address = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {

                String postalCode = addressList.get(0).getPostalCode();
                String state = addressList.get(0).getLocality();
                String city = addressList.get(0).getAdminArea();
                String street = addressList.get(0).getThoroughfare();
                String country = addressList.get(0).getCountryName();

                address += (postalCode == null ? "" : postalCode + " ");
                address += (city == null ? "" : city + " ");
                address += (state == null ? "" : state + " ");
                address += (street == null ? "" : street + " ");
                address += (country == null ? "" : "\n\n\nYou are in " + country + " !!!");

                addressText.setText("Address:\n\n" + address);

            } else {
                addressText.setText("Address: -");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}