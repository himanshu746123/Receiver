package com.example.abans_000.receiver;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.ui.IconGenerator;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String LOG_TAG = "MapsActivity";

    private static final long MAX_TARDINESS = 1000;
    private GoogleMap mMap;
    private UiSettings mUiSettings;

    private boolean mMapReady = false;

    private Timer myTimer;

    HashMap<Marker, Event> mMarkerEventMap;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final String[] PERMISSION_LOCATION = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Button btnMap, btnSatellite, btnHybrid;
        btnMap = (Button) findViewById(R.id.btnMap);
        btnSatellite = (Button) findViewById(R.id.btnSatellite);
        btnHybrid = (Button) findViewById(R.id.btnHybrid);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMapReady)
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        btnSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMapReady)
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        btnHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMapReady)
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMapReady = true;
        mMap = googleMap;

        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);

        // Add a marker in Sydney and move the camera
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.3193, 87.31), 14.0f));

        addMarkers();

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - scheduledExecutionTime() >=
                        MAX_TARDINESS)
                    return;  // Too late; skip this execution.
                // Perform the task
                TimerMethod();
            }
        }, 0, 5 * 60 * 1000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void requestLocationPermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i(LOG_TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
//            Snackbar.make(mLayout, R.string.permission_location_rationale,
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.ok, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            ActivityCompat
//                                    .requestPermissions(MapActivity.this, PERMISSION_LOCATION,
//                                            MY_PERMISSIONS_REQUEST_LOCATION);
//                        }
//                    })
//                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSION_LOCATION, MY_PERMISSIONS_REQUEST_LOCATION);
        }
        // END_INCLUDE(contacts_permission_request)
    }

    /**
     * Callbacks from request location
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                Log.i(LOG_TAG, "Received response for contact permissions request.");

                // We have requested multiple permissions for contacts, so all of them need to be
                // checked.
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    // All required permissions have been granted, display contacts fragment.
//                    Snackbar.make(mLayout, R.string.permision_available_location,
//                            Snackbar.LENGTH_SHORT)
//                            .show();
                } else {
                    Log.i(LOG_TAG, "Contacts permissions were NOT granted.");
//                    Snackbar.make(mLayout, R.string.permissions_not_granted,
//                            Snackbar.LENGTH_SHORT)
//                            .show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void addMarkers() {
        mMap.clear();

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference mEventReference = mFirebaseDatabase.getReference().child("events");
        mMarkerEventMap = new HashMap<>();

        final IconGenerator iconGenerator = new IconGenerator(MapsActivity.this);
        iconGenerator.setStyle(IconGenerator.STYLE_BLUE);

        mEventReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);
                long tm = System.currentTimeMillis();
                if (tm >= (event.getStart() - 30 * 60 * 1000) && tm <= event.getFinish()) {
                    Bitmap bitmap = iconGenerator.makeIcon(event.getTitle());
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(event.getLatitude(), event.getLongitude()))
                            .icon(icon)
                            .title(event.getTitle()));
                    mMarkerEventMap.put(marker, event);
                } else if (tm > event.getFinish()) {
                    //delete event from the database...
                    String key = dataSnapshot.getKey();
                    mEventReference.child(key).removeValue();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Event event = mMarkerEventMap.get(marker);
                //Toast.makeText(MapsActivity.this, event.getDescription(), Toast.LENGTH_SHORT).show();
                Intent eventIntent = new Intent(MapsActivity.this, EventActivity.class);
                eventIntent.putExtra("title", event.getTitle());
                eventIntent.putExtra("stime", event.getStart());
                eventIntent.putExtra("ftime", event.getFinish());
                eventIntent.putExtra("description", event.getDescription());
                startActivity(eventIntent);
                return true;
            }
        });
    }

    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }


    private Runnable Timer_Tick = new Runnable() {
        public void run() {

            //This method runs in the same thread as the UI.

            //Do something to the UI thread here
            addMarkers();
        }
    };
}
