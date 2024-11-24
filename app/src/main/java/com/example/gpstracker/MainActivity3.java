package com.example.gpstracker;

import static android.content.ContentValues.TAG;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity3 extends AppCompatActivity implements OnMapReadyCallback {
    String UID = null;
    String value;
    FirebaseDatabase firebaseDatabase;
    private EditText name, user, pass;
    private Button reg;
    private TextView UserName;
    DatabaseReference databaseReference;
    int check = 0;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    private LatLng currentLatLng;
    private LatLng enteredLatLng;
    private TextView local;

    // Predefined coordinates
    private final double lat = 7.611669;
    private final double lon = 5.265774;
    private ImageButton checkLocation;
    private ImageButton findDistance;
    private ImageButton settings;
    private CardView save;
    boolean safe;

    boolean safeVal;
    String deviceName2 = "My Device";
    String safeRange2;
    int safeRange3 = 0;
    int dist;

    String City;
    double LAT;
    double LON;

    private long pressedTime;

//    UserDataCall userDataCall;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);

        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();

        UserName = findViewById(R.id.textView4);
        local = findViewById(R.id.textView5);
        checkLocation = findViewById(R.id.imageButton3);
        findDistance = findViewById(R.id.imageButton2);
        settings = findViewById(R.id.imageButton);
        save = findViewById(R.id.saveCard);
        final Button saveButton = findViewById(R.id.save);
        final TextView uid = findViewById(R.id.textView6);
        final EditText deviceName = findViewById(R.id.devicenameInput);
        final EditText safeRange = findViewById(R.id.safeRangeInput);
        final ToggleButton safeCheck = findViewById(R.id.toggleButton);

        save.setVisibility(View.INVISIBLE);
        UID = getIntent().getStringExtra("UID");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        uid.setText("UID:" + UID);
        deviceName2 = sp.getString("deviceName", "");
        safeRange2 = sp.getString("safeRange", "");
        safe = sp.getBoolean("safe", Boolean.parseBoolean(""));
        safeVal = safe;
        deviceName.setHint("Device Name (" + deviceName2 + ")");
        safeRange.setHint("Safe Range in m (" + safeRange2 + ")");

        if (safeVal) {
            safeCheck.setChecked(true);
            Toast.makeText(MainActivity3.this, "Safe Check is On", Toast.LENGTH_SHORT).show();
        } else {
            safeCheck.setChecked(false);
            Toast.makeText(MainActivity3.this, "Safe Check is Off", Toast.LENGTH_SHORT).show();
        }


        Handler handler = new Handler();

        Runnable r = new Runnable() {
            public void run() {
//                Toast.makeText(MainActivity3.this, "working", Toast.LENGTH_SHORT).show();
                getLocation();

                // Calculate the distance
                float[] results = new float[1];
                Location.distanceBetween(LAT, LON, lat, lon, results);
                float distance = results[0];
                int dist2 = (int) distance;

//                Toast.makeText(MainActivity3.this, safeRange2, Toast.LENGTH_SHORT).show();
                if (safeRange2 == null) {
                    safeRange3 = 0;
                } else {
                    try {
                        // Convert the string to an integer
                        safeRange3 = Integer.parseInt(safeRange2);
                    } catch (NumberFormatException e) {
                        // Handle the case where the string is not a valid integer
                        safeRange3 = 0; // Default value or handle accordingly
                        Log.e("ConversionError", "Invalid number format for safeRange2: " + safeRange2);
                    }
                }

                if (safeVal && safeRange3 == 0) {
                    Toast.makeText(MainActivity3.this, "Please set safe range", Toast.LENGTH_SHORT).show();
                } else if (safeVal && safeRange3 > 0) {
//                    int newSafeRange = Integer.parseInt(safeRange2);
                    if (dist2 > safeRange3) {
                        boolean signal = true;
                        Intent serviceIntent = new Intent(MainActivity3.this, BackgroundCheck.class);
                        serviceIntent.putExtra("signal", signal);
                        startService(serviceIntent);
                    }
                    if (dist <= safeRange3) {
                        boolean signal = false;
                        Intent serviceIntent = new Intent(MainActivity3.this, BackgroundCheck.class);
                        serviceIntent.putExtra("signal", signal);
                        startService(serviceIntent);
                    }
                }

                handler.postDelayed(this, 10000);
            }
        };

        handler.post(r);


        if (UID != null && check == 0) {
            check = 1;
        }


        if (check == 1) {
            retrieveData(UID);
//            Toast.makeText(MainActivity3.this, "Check is 1", Toast.LENGTH_SHORT).show();
//            displayPredefinedCoordinates();
            check = 2;
        }

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save.setVisibility(View.VISIBLE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceName1 = deviceName.getText().toString();
                String safeRange1 = safeRange.getText().toString();
                if (deviceName1.isEmpty() && safeRange1.isEmpty()) {
//                    edit.putString("safeRange", safeRange1);
                    Toast.makeText(MainActivity3.this, "Successfully saved and please login again", Toast.LENGTH_SHORT).show();
                } else if (safeRange1.isEmpty()) {
                    edit.putString("deviceName", deviceName1);
                    Toast.makeText(MainActivity3.this, "Successfully saved and please login again", Toast.LENGTH_SHORT).show();
                } else if (deviceName1.isEmpty()) {
                    edit.putString("safeRange", safeRange1);
                    Toast.makeText(MainActivity3.this, "Successfully saved and please login again", Toast.LENGTH_SHORT).show();
                } else {
                    edit.putString("safeRange", safeRange1);
                    edit.putString("deviceName", deviceName1);
                    Toast.makeText(MainActivity3.this, "Successfully saved and please login again", Toast.LENGTH_SHORT).show();
                }
//                edit.putString("safeRange", safeRange1);
                edit.putBoolean("safe", safe);
                edit.apply();
                save.setVisibility(View.GONE);
                recreate();
            }
        });

        checkLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPredefinedCoordinates();
                showDeviceLocation();
            }
        });

        findDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateDistanceAndDirectionWithRoads();
            }
        });

        safeCheck.setOnCheckedChangeListener((buttonView, isChecked) ->

        {
            if (isChecked) {
                safe = true;

                Toast.makeText(this, "Safe Range Activated", Toast.LENGTH_SHORT).show();
            } else {
                safe = false;
                Toast.makeText(this, "Safe Range Deactivate", Toast.LENGTH_SHORT).show();
            }
        });

//        if (check == 2) {
//            calculateDistanceAndDirection();
//            check = 3;
//        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize Fused Location Client

        showDeviceLocation();

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Confirm map is ready
//        Toast.makeText(this, "Google Map is ready", Toast.LENGTH_SHORT).show();

        // Display predefined coordinates
//        displayPredefinedCoordinates();
    }

    private void displayPredefinedCoordinates() {
        try {
            // Toast to show coordinates
            Toast.makeText(this, "Coordinates: Lat=" + lat + ", Lon=" + lon, Toast.LENGTH_LONG).show();

            // Ensure mMap is not null
            if (mMap == null) {
                Toast.makeText(this, "Google Map is not initialized", Toast.LENGTH_LONG).show();
                return;
            }

            // Create LatLng object
            LatLng predefinedLatLng = new LatLng(lat, lon);
            getCityName(lat, lon);
            local.setText("Location: " + (City != null ? City : "Unknown"));

            enteredLatLng = new LatLng(lat, lon);

            // Add marker to map
            Marker Device = mMap.addMarker(new MarkerOptions()
                    .title(deviceName2)
                    .position(predefinedLatLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            if (Device != null) {
                Device.showInfoWindow(); // Show the info window
            }
            // Move camera to marker
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(predefinedLatLng, 17));

            // Display success toast
//            Toast.makeText(this, "Marker added and camera moved", Toast.LENGTH_SHORT).show();

            // Get city name


        } catch (Exception e) {
            Toast.makeText(this, "Error displaying location: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @SuppressLint("SetTextI18n")
    private void calculateDistanceAndDirectionWithRoads() {
        getCityName(lat, lon);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Get the phone's current location
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double currentLat = location.getLatitude();
                double currentLon = location.getLongitude();

                // Predefined location coordinates
                double targetLat = lat;
                double targetLon = lon;

                // Construct the Directions API URL
                String directionsUrl = String.format(
                        Locale.US,
                        "https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&key=AIzaSyDk6tM6q5dXWQ5i7HtQ5k5OXT6CMMfq3nQ",
                        currentLat, currentLon, targetLat, targetLon
                );

                // Calculate the distance
                float[] results = new float[1];
                Location.distanceBetween(currentLat, currentLon, targetLat, targetLon, results);
                float distance = results[0];
                dist = (int) distance;

                local.setText("Location: " + (City != null ? City : "Unknown") + "\n" + (dist) + "m away");

                // Fetch route from Directions API
                new Thread(() -> {
                    try {
                        JSONArray routes = getJsonArray(directionsUrl);
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                            String points = overviewPolyline.getString("points");

                            // Decode polyline points
                            List<LatLng> decodedPath = PolyUtil.decode(points);

                            // Update map on the main thread
                            runOnUiThread(() -> {
                                // Clear existing polylines and markers
                                mMap.clear();

                                // Add a marker for the predefined location
                                LatLng predefinedLatLng = new LatLng(targetLat, targetLon);
                                Marker Device = mMap.addMarker(new MarkerOptions()
                                        .position(predefinedLatLng)
                                        .title(deviceName2));
                                if (Device != null) {
                                    Device.showInfoWindow(); // Show the info window
                                }
                                // Add a marker for the current location
                                LatLng currentLatLng = new LatLng(currentLat, currentLon);
                                Marker Phone = mMap.addMarker(new MarkerOptions()
                                        .position(currentLatLng)
                                        .title("My location"));
//                                if (Phone != null) {
//                                    Phone.showInfoWindow(); // Show the info window
//                                }
                                // Draw the polyline on the map
                                mMap.addPolyline(new PolylineOptions()
                                        .addAll(decodedPath)
                                        .color(Color.BLUE)
                                        .width(10));

                                // Move the camera to show the route
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                                        new LatLngBounds.Builder()
                                                .include(currentLatLng)
                                                .include(predefinedLatLng)
                                                .build(), 100));
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(this, "No route found", Toast.LENGTH_LONG).show());
                        }
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }).start();
            } else {
                Toast.makeText(this, "Unable to fetch current location", Toast.LENGTH_LONG).show();
            }
        });
    }

    @NonNull
    private static JSONArray getJsonArray(String directionsUrl) throws
            IOException, JSONException {
        URL url = new URL(directionsUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        // Read API response
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line);
        }

        // Parse response JSON
        JSONObject responseJson = new JSONObject(responseBuilder.toString());
        JSONArray routes = responseJson.getJSONArray("routes");
        return routes;
    }


    private void getCityName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                City = address.getLocality();

            } else {
                local.setText("Location: Unknown");
            }
        } catch (Exception e) {
            e.printStackTrace();
            local.setText("Location: Error retrieving city name");
        }
    }


    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("My Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            } else {
                Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    getDeviceLocation();
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void retrieveData(String uid) {
        DatabaseReference userRef = databaseReference.child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Print all children of this UID
//                    StringBuilder data = new StringBuilder();
//                    for (DataSnapshot child : snapshot.getChildren()) {
//                        data.append(child.getKey()).append(": ").append(child.getValue()).append("\n");
//                    }

//                    String email = snapshot.child("email").getValue(String.class);
//                    String password = snapshot.child("pass").getValue(String.class);
                    String name = snapshot.child("userName").getValue(String.class);

//                    Toast.makeText(MainActivity3.this, email, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(MainActivity3.this, password, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(MainActivity3.this, name, Toast.LENGTH_SHORT).show();// Display the data in TextView
                    UserName.setText(name);
                } else {
                    Toast.makeText(MainActivity3.this, "No data found for this UID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error retrieving data: " + error.getMessage());
                Toast.makeText(MainActivity3.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here!"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            } else {
                Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LAT = location.getLatitude();
                LON = location.getLongitude();

            } else {
                Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        if (pressedTime + 2000 > System.currentTimeMillis()) {

            save.setVisibility(View.INVISIBLE);

        } else {
            Toast.makeText(getBaseContext(), "Press back again to logout", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

}