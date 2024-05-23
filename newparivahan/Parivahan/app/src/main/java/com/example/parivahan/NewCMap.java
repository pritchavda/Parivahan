package com.example.parivahan;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.parivahan.databinding.ActivityNewCmapBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewCMap extends FragmentActivity implements OnMapReadyCallback {

    private int ACCESS_LOCATION_REQUESTCODE = 1;
    private GoogleMap mMap;
    private ActivityNewCmapBinding binding;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Location mstorlocation;
    Location mLastLocation;
    Marker UserLocationMarker;
    public int mlo = 0;

//    private static int AUTOCOMPLETE_REQUEST_CODE = 1;


    private Button mlogout,mrequest,msetting,mHistory;
    private LatLng pickuplocation;
    private Boolean requestbol = false;
    Marker pickupMarker;
    private String destination,requestService;
    private Geocoder geocoder;
    private LatLng destinationLatLng;
    private RadioGroup mRadioGroup;

    private RatingBar mRatingBar;

    EditText enterLocation;
//    Button enterLocationBtn;
//    LinearLayout enterLocationLiner;
//    EditText editText;
//    TextView textView1,textView2;

    private LinearLayout mDriverInfo;
    private ImageView mDriverProfileImage;
    private TextView mDriverName,mDriverPhone,mDriverCar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewCmapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        geocoder = new Geocoder(this);
        destinationLatLng = new LatLng(0.0,0.0);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mRadioGroup.check(R.id.UberX);

        mlogout =(Button) findViewById(R.id.logout);
        mrequest=(Button) findViewById(R.id.request);
        msetting=(Button) findViewById(R.id.setting);
        mHistory=(Button) findViewById(R.id.history);

//        editText = findViewById(R.id.edit_text);
//        textView1 = findViewById(R.id.text_view1);
//        textView2 = findViewById(R.id.text_view2);

        enterLocation = findViewById(R.id.EnterLocation);
//        enterLocationBtn = findViewById(R.id.EnterLocationBtn);
//        enterLocationLiner = findViewById(R.id.EnterLocationLiner);


        mDriverInfo = (LinearLayout) findViewById(R.id.driverInfo);
        mDriverProfileImage = (ImageView) findViewById(R.id.driverProfileImage);
        mDriverName = (TextView) findViewById(R.id.driverName);
        mDriverPhone = (TextView) findViewById(R.id.driverPhone);
        mDriverCar = (TextView) findViewById(R.id.driverCar);

        mRatingBar =(RatingBar) findViewById(R.id.ratingBar);

//        Places.initialize(getApplicationContext(),"AIzaSyCM1KJE0a5lsaHQqpELOJ8PEcC7_VJTspE");
//
//        editText.setFocusable(false);
//        editText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME);
//                Intent intent =new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fieldList).build(NewCMap.this);
//
//                startActivityForResult(intent,100);


        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
//                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        // Start the autocomplete intent.
//                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                        .build(NewCMap.this);
//                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
//            }
//        });

//        if (enterLocation.== null){
//            Toast.makeText(NewCMap.this, "text is ="+enterLocation.getText().toString(), Toast.LENGTH_SHORT).show();
//        }


        enterLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                       destination = enterLocation.getText().toString();
                        try {
                            List<Address> addresses= geocoder.getFromLocationName(destination,1);
                            Address address =addresses.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(latLng)
                                    .title("DropUp Location :" + address.getAddressLine(0));

//                            MarkerOptions markerOptions2 = new MarkerOptions();
//                            markerOptions2.position(latLng);
////            Toast.makeText(this, "Thsis is work", Toast.LENGTH_SHORT).show();
//                            markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_location));
////                    markerOptions.rotation(driverlatlag.getBearing());
//                            markerOptions2.anchor(0.5f, 0.5f);
//                            markerOptions2.title("DropUp Location :" + address.getAddressLine(0));
//                            mDrivermarker = mMap.addMarker(markerOptions2);
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                            destinationLatLng = latLng;
                            destination = address.getAddressLine(0);
                            mMap.addMarker(markerOptions);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                       Toast.makeText(NewCMap.this, "This is search" + enterLocation.getText().toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(NewCMap.this,MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
//        Log.d("destination", "Hi Bro: ");
        mrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean mlocatinchack = true;
                mlo = 1;
            }
        });
        msetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewCMap.this,CustomerSettingActivity.class);
                startActivity(intent);
                return;
            }
        });
        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewCMap.this,HistoryActivity.class);
                intent.putExtra("customerOrDriver","Customers");
                startActivity(intent);
                return;
            }
        });




        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    }
    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode ==100 && resultCode ==RESULT_OK){
//            Place place = Autocomplete.getPlaceFromIntent(data);
//            editText.setText(place.getAddress());
//            textView1.setText(String.format("Locality Name : %s",place.getName()));
//
//            textView2.setText(String.valueOf(place.getLatLng()));
//        }
//        else if (resultCode == AutocompleteActivity.RESULT_ERROR){
//            Status status = Autocomplete.getStatusFromIntent(data);
//
//            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                Place place = Autocomplete.getPlaceFromIntent(data);
//                Toast.makeText(NewCMap.this, "Place: " + place.getName() + ", " + place.getId(), Toast.LENGTH_SHORT).show();
//                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
//            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
//                // TODO: Handle the error.
//                Status status = Autocomplete.getStatusFromIntent(data);
//                Toast.makeText(NewCMap.this, "error"+status.getStatusMessage(), Toast.LENGTH_SHORT).show();
//                Log.i(TAG, status.getStatusMessage());
//            } else if (resultCode == RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
//            return;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(NewCMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUESTCODE);
//            Toast.makeText(this, "this is work", Toast.LENGTH_SHORT).show();
//            enableUserLocation();
//            ZoomToUserLocation();
            startLocationUpdate();
        } else {
            ActivityCompat.requestPermissions(NewCMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUESTCODE);
        }

    }
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d("lpg","onlocation:-"+locationResult.getLocations());

            if (mMap != null){
                setUserLocationMarker(locationResult.getLastLocation());
            }
            if (mlo==1){
                setdatabasevalue(locationResult.getLastLocation());
                mlo = 0;
            }
            if (!getDriverAroundStated){
                Toast.makeText(NewCMap.this, "DriverAround", Toast.LENGTH_SHORT).show();
                getDriverAround();
            }
        }
    };
    private void setdatabasevalue(Location location){
        if (requestbol){
            endRide();
        }
        else{
            int selectId = mRadioGroup.getCheckedRadioButtonId();

            final RadioButton radioButton = (RadioButton) findViewById(selectId);
            if (radioButton.getText() == null){
                return;
            }

            requestService = radioButton.getText().toString();

            requestbol = true;
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.setLocation(userId,new GeoLocation(location.getLatitude(),location.getLongitude()));

            pickuplocation = new LatLng(location.getLatitude(),location.getLongitude());
            pickupMarker = mMap.addMarker(new MarkerOptions().position(pickuplocation).title("Pickup Hear"));

//            MarkerOptions markerOptions2 = new MarkerOptions();
//            markerOptions2.position(pickuplocation);
//            Toast.makeText(this, "Thsis is work", Toast.LENGTH_SHORT).show();
//            markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_location));
//                    markerOptions.rotation(driverlatlag.getBearing());
//            markerOptions2.anchor(0.5f, 0.5f);
//            markerOptions2.title("Pickup Hear");
//            mDrivermarker = mMap.addMarker(markerOptions2);

            mrequest.setText("Getting Your Driver .......");
            Toast.makeText(this, "Closest driver is", Toast.LENGTH_SHORT).show();
            getClosestDriver();
        }
    }
    private int radius = 1;
    GeoQuery geoQuery;
    private Boolean driverFound = false;
    private String driverFoundId;
    private void getClosestDriver(){
        DatabaseReference driverlocation = FirebaseDatabase.getInstance().getReference().child("DriverAvailable");
        GeoFire geoFire = new GeoFire(driverlocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickuplocation.latitude,pickuplocation.longitude),radius);
        Log.d("llp","value of Radius is"+String.valueOf(radius));
        Toast.makeText(this, "Value of radius is"+radius, Toast.LENGTH_SHORT).show();
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && requestbol) {
                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);
                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.getChildrenCount()>0){
                                Map<String,Object> drivermap = (Map<String, Object>) snapshot.getValue();
                                if (driverFound){
                                    return;
                                }
                                Toast.makeText(NewCMap.this, "This is in side the if 111111111111111111111111111", Toast.LENGTH_SHORT).show();
//                                Toast.makeText(NewCMap.this, "This"+ drivermap.get("service"), Toast.LENGTH_SHORT).show();
                                if (drivermap.get("service").equals(requestService)){
                                    Toast.makeText(NewCMap.this, "true in this is work  " +requestService, Toast.LENGTH_SHORT).show();
                                    driverFound = true;
                                    driverFoundId=key;
                                    Toast.makeText(NewCMap.this, key, Toast.LENGTH_SHORT).show();
                                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundId).child("customerRequest");
                                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap map = new HashMap();
                                    map.put("customerRideId",customerId);
                                    map.put("destination",destination);
                                    map.put("destinationLat",destinationLatLng.latitude);
                                    map.put("destinationLng",destinationLatLng.longitude);
                                    driverRef.updateChildren(map);
                                    mrequest.setText("Looking For a Driver Location....");
                                    Toast.makeText(NewCMap.this, "Looking for a driver Location....", Toast.LENGTH_SHORT).show();
                                    getDriverLocation();
                                    getDriverInfo();
                                    getHaseRideEnded();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onKeyExited(String key) {

            }
            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }
            @Override
            public void onGeoQueryReady() {
                if (!driverFound)
                {
                    radius ++;
                    getClosestDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
    private Marker mDrivermarker;
    DatabaseReference driverLocationRef;
    ValueEventListener driverLocationRefListener;

    public void getDriverInfo(){
        mDriverInfo.setVisibility(View.VISIBLE);
        DatabaseReference  mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundId);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    Map<String,Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name")!=null){
                        mDriverName.setText(map.get("name").toString());
                    }
                    if (map.get("phone")!=null){
                        mDriverPhone.setText(map.get("phone").toString());
                    }
                    if (map.get("car")!=null){
                        mDriverCar.setText(map.get("car").toString());
                    }
                    if (map.get("profileImageUrl")!=null){
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mDriverProfileImage);
                    }
                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingavg = 0;
                    for (DataSnapshot child : snapshot.child("rating").getChildren()){
                        ratingSum =ratingSum  +Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if (ratingsTotal!=0){
                        ratingavg = ratingSum/ratingsTotal;
                        mRatingBar.setRating(ratingavg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private DatabaseReference driverHasEndedRef;
    private ValueEventListener driverHasEndedRefListener;
    private void getHaseRideEnded(){
        driverHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundId).child("customerRequest").child("customerRideId");

        driverHasEndedRefListener = driverHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                }
                else{
                    endRide();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void endRide(){
        requestbol = false;
        geoQuery.removeAllListeners();
        driverLocationRef.removeEventListener(driverLocationRefListener);
        driverHasEndedRef.removeEventListener(driverHasEndedRefListener);

        if (driverFound != null){
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundId).child("customerRequest");
            driverRef.removeValue();
            driverFoundId = null;
        }
        driverFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if (pickupMarker != null){
            pickupMarker.remove();
        }
        if (mDrivermarker !=null){
            mDrivermarker.remove();
        }
        mrequest.setText("call Uber");

        mDriverInfo.setVisibility(View.GONE);
        mDriverName.setText("");
        mDriverPhone.setText("");
        mDriverCar.setText("");
        mDriverProfileImage.setImageResource(R.mipmap.ic_launcher);

    }

    private void getDriverLocation(){
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("DriverWorking").child(driverFoundId).child("l");
        Toast.makeText(this, "get driver location", Toast.LENGTH_SHORT).show();
        driverLocationRefListener =driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()&& requestbol){
                    List<Object> map = (List<Object>) snapshot.getValue();
                    double locationlat = 0;
                    double locationlng = 0;
                    mrequest.setText("Driver Found");
                    if (map.get(0)!= null) {
                        locationlat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1)!=null){
                        locationlng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverlatlag = new LatLng(locationlat,locationlng);
                    if (mDrivermarker!= null){
                        mDrivermarker.remove();
                    }

                    Location loc1 = new Location("");
                    loc1.setLatitude(pickuplocation.latitude);
                    loc1.setLongitude(pickuplocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverlatlag.latitude);
                    loc2.setLongitude(driverlatlag.longitude);

                    float distance = loc1.distanceTo(loc2);

                    if (distance<100){
                        mrequest.setText("Driver's Here");
                    }else{
                        mrequest.setText("Driver Found:"+String.valueOf(distance));
                    }
//                    mDrivermarker = mMap.addMarker(new MarkerOptions().position(driverlatlag).title("Your driver"));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(driverlatlag);
//            Toast.makeText(this, "Thsis is work", Toast.LENGTH_SHORT).show();
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.cariamge));
//                    markerOptions.rotation(driverlatlag.getBearing());
                    markerOptions.anchor(0.5f, 0.5f);
                    markerOptions.title("Your driver");
                    mDrivermarker = mMap.addMarker(markerOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverlatlag, 17));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUserLocationMarker(Location location){

        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (UserLocationMarker == null) {
            //Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
//            Toast.makeText(this, "Thsis is work", Toast.LENGTH_SHORT).show();
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.customerlocation));
            markerOptions.rotation(location.getBearing());
            markerOptions.anchor(0.5f, 0.5f);
            UserLocationMarker = mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));


        } else {
            //User can also Create a marker
//            Toast.makeText(NewCMap.this, "THis is note work", Toast.LENGTH_SHORT).show();
            UserLocationMarker.setPosition(latLng);
            UserLocationMarker.setRotation(location.getBearing());
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }
    }



    private void startLocationUpdate() {
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
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }
    private void stopLocationUpdate(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            startLocationUpdate();
        }else{
            //need to location permission
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdate();
    }

    private void enableUserLocation() {
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
        mMap.setMyLocationEnabled(true);
    }

    private void ZoomToUserLocation() {
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
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,20));
                Log.d("lpg","this "+latLng);
                Toast.makeText(NewCMap.this, "This lat:-"+latLng, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewCMap.this, "This program is fail", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_LOCATION_REQUESTCODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "This is also work", Toast.LENGTH_SHORT).show();
//                enableUserLocation();
//                ZoomToUserLocation();
                startLocationUpdate();
            }
        }else{
            //We can show a dialog  permission
        }
    }

    Boolean getDriverAroundStated = false;
    List<Marker> markerList = new ArrayList<Marker>();
    private void getDriverAround(){
        getDriverAroundStated = true;
        DatabaseReference driversLocation = FirebaseDatabase.getInstance().getReference().child("DriverAvailable");

        GeoFire geoFire = new GeoFire(driversLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()),10000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                    for (Marker markerIt : markerList){
                        if (markerIt.getTag().equals(key))
                            return;

                    }
                    LatLng driverLocation = new LatLng(location.latitude,location.longitude);
                    Marker mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLocation));


//                MarkerOptions mDriverMarker = new MarkerOptions();
//                mDriverMarker.position(driverLocation);
//            Toast.makeText(this, "Thsis is work", Toast.LENGTH_SHORT).show();
//                mDriverMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_location));
//                    markerOptions.rotation(driverlatlag.getBearing());
//                mDriverMarker.anchor(0.5f, 0.5f);
//                mDriverMarker.title("Driver Available");
//                mDriverMarker = mMap.addMarker(mDriverMarker);
                mDriverMarker.setTag(key);

                    markerList.add(mDriverMarker);
            }

            @Override
            public void onKeyExited(String key) {
                for (Marker markerIt : markerList){
                    if (markerIt.getTag().equals(key)){
                        markerIt.remove();
                        markerList.remove(markerIt);
                        return;
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker markerIt : markerList){
                    if (markerIt.getTag().equals(key)){
                        markerIt.setPosition(new LatLng(location.latitude,location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
}


//package com.example.parivahan;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.FragmentActivity;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.os.Bundle;
//import android.os.Looper;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.inputmethod.EditorInfo;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.RatingBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.firebase.geofire.GeoFire;
//import com.firebase.geofire.GeoLocation;
//import com.firebase.geofire.GeoQuery;
//import com.firebase.geofire.GeoQueryEventListener;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.example.parivahan.databinding.ActivityNewCmapBinding;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class NewCMap extends FragmentActivity implements OnMapReadyCallback {
//
//    private int ACCESS_LOCATION_REQUESTCODE = 1;
//    private GoogleMap mMap;
//    private ActivityNewCmapBinding binding;
//    FusedLocationProviderClient fusedLocationProviderClient;
//    LocationRequest locationRequest;
//    Location mstorlocation;
//    Location mLastLocation;
//    Marker UserLocationMarker;
//    public int mlo = 0;
//
////    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
//
//
//    private Button mlogout,mrequest,msetting,mHistory;
//    private LatLng pickuplocation;
//    private Boolean requestbol = false;
//    Marker pickupMarker;
//    private String destination,requestService;
//    private Geocoder geocoder;
//    private LatLng destinationLatLng;
//    private RadioGroup mRadioGroup;
//
//    private RatingBar mRatingBar;
//
//    EditText enterLocation;
////    Button enterLocationBtn;
////    LinearLayout enterLocationLiner;
////    EditText editText;
////    TextView textView1,textView2;
//
//    private LinearLayout mDriverInfo;
//    private ImageView mDriverProfileImage;
//    private TextView mDriverName,mDriverPhone,mDriverCar;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        binding = ActivityNewCmapBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//
//        geocoder = new Geocoder(this);
//        destinationLatLng = new LatLng(0.0,0.0);
//        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
//        mRadioGroup.check(R.id.UberX);
//
//        mlogout =(Button) findViewById(R.id.logout);
//        mrequest=(Button) findViewById(R.id.request);
//        msetting=(Button) findViewById(R.id.setting);
//        mHistory=(Button) findViewById(R.id.history);
//
////        editText = findViewById(R.id.edit_text);
////        textView1 = findViewById(R.id.text_view1);
////        textView2 = findViewById(R.id.text_view2);
//
//        enterLocation = findViewById(R.id.EnterLocation);
////        enterLocationBtn = findViewById(R.id.EnterLocationBtn);
////        enterLocationLiner = findViewById(R.id.EnterLocationLiner);
//
//
//        mDriverInfo = (LinearLayout) findViewById(R.id.driverInfo);
//        mDriverProfileImage = (ImageView) findViewById(R.id.driverProfileImage);
//        mDriverName = (TextView) findViewById(R.id.driverName);
//        mDriverPhone = (TextView) findViewById(R.id.driverPhone);
//        mDriverCar = (TextView) findViewById(R.id.driverCar);
//
//        mRatingBar =(RatingBar) findViewById(R.id.ratingBar);
//
////        Places.initialize(getApplicationContext(),"AIzaSyCM1KJE0a5lsaHQqpELOJ8PEcC7_VJTspE");
////
////        editText.setFocusable(false);
////        editText.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME);
////                Intent intent =new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fieldList).build(NewCMap.this);
////
////                startActivityForResult(intent,100);
//
//
//        // Set the fields to specify which types of place data to
//        // return after the user has made a selection.
////                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
//        // Start the autocomplete intent.
////                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
////                        .build(NewCMap.this);
////                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
////            }
////        });
//
////        if (enterLocation.== null){
////            Toast.makeText(NewCMap.this, "text is ="+enterLocation.getText().toString(), Toast.LENGTH_SHORT).show();
////        }
//
//
//        enterLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                switch (actionId) {
//                    case EditorInfo.IME_ACTION_SEARCH:
//                        destination = enterLocation.getText().toString();
//                        try {
//                            List<Address> addresses= geocoder.getFromLocationName(destination,1);
//                            Address address =addresses.get(0);
//                            LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
//                            MarkerOptions markerOptions = new MarkerOptions()
//                                    .position(latLng)
//                                    .title("DropUp Location :" + address.getAddressLine(0));
//                            destinationLatLng = latLng;
//                            destination = address.getAddressLine(0);
//                            mMap.addMarker(markerOptions);
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
////                       Toast.makeText(NewCMap.this, "This is search" + enterLocation.getText().toString(), Toast.LENGTH_SHORT).show();
//                        break;
//                }
//                return false;
//            }
//        });
//
//        mlogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(NewCMap.this,MainActivity.class);
//                startActivity(intent);
//                finish();
//                return;
//            }
//        });
////        Log.d("destination", "Hi Bro: ");
//        mrequest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean mlocatinchack = true;
//                mlo = 1;
//            }
//        });
//        msetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(NewCMap.this,CustomerSettingActivity.class);
//                startActivity(intent);
//                return;
//            }
//        });
//        mHistory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(NewCMap.this,HistoryActivity.class);
//                intent.putExtra("customerOrDriver","Customers");
//                startActivity(intent);
//                return;
//            }
//        });
//
//
//
//
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        locationRequest = LocationRequest.create();
//        locationRequest.setInterval(500);
//        locationRequest.setFastestInterval(1000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//
//    }
//    //    @Override
////    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////        if (requestCode ==100 && resultCode ==RESULT_OK){
////            Place place = Autocomplete.getPlaceFromIntent(data);
////            editText.setText(place.getAddress());
////            textView1.setText(String.format("Locality Name : %s",place.getName()));
////
////            textView2.setText(String.valueOf(place.getLatLng()));
////        }
////        else if (resultCode == AutocompleteActivity.RESULT_ERROR){
////            Status status = Autocomplete.getStatusFromIntent(data);
////
////            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
////        }
////    }
//
//
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
////        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
////            if (resultCode == RESULT_OK) {
////                Place place = Autocomplete.getPlaceFromIntent(data);
////                Toast.makeText(NewCMap.this, "Place: " + place.getName() + ", " + place.getId(), Toast.LENGTH_SHORT).show();
////                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
////            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
////                // TODO: Handle the error.
////                Status status = Autocomplete.getStatusFromIntent(data);
////                Toast.makeText(NewCMap.this, "error"+status.getStatusMessage(), Toast.LENGTH_SHORT).show();
////                Log.i(TAG, status.getStatusMessage());
////            } else if (resultCode == RESULT_CANCELED) {
////                // The user canceled the operation.
////            }
////            return;
////        }
////        super.onActivityResult(requestCode, resultCode, data);
////    }
//
//
//
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
////        LatLng sydney = new LatLng(-34, 151);
////        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
////        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED)) {
//            ActivityCompat.requestPermissions(NewCMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUESTCODE);
////            Toast.makeText(this, "this is work", Toast.LENGTH_SHORT).show();
////            enableUserLocation();
////            ZoomToUserLocation();
//            startLocationUpdate();
//        } else {
//            ActivityCompat.requestPermissions(NewCMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUESTCODE);
//        }
//
//    }
//    LocationCallback locationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(@NonNull LocationResult locationResult) {
//            super.onLocationResult(locationResult);
//            Log.d("lpg","onlocation:-"+locationResult.getLocations());
//
//            if (mMap != null){
//                setUserLocationMarker(locationResult.getLastLocation());
//            }
//            if (mlo==1){
//                setdatabasevalue(locationResult.getLastLocation());
//                mlo = 0;
//            }
//            if (!getDriverAroundStated)
//                getDriverAround();
//        }
//    };
//    private void setdatabasevalue(Location location){
//        if (requestbol){
//            endRide();
//        }
//        else{
//            int selectId = mRadioGroup.getCheckedRadioButtonId();
//
//            final RadioButton radioButton = (RadioButton) findViewById(selectId);
//            if (radioButton.getText() == null){
//                return;
//            }
//
//            requestService = radioButton.getText().toString();
//
//            requestbol = true;
//            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");
//            GeoFire geoFire = new GeoFire(ref);
//            geoFire.setLocation(userId,new GeoLocation(location.getLatitude(),location.getLongitude()));
//
//            pickuplocation = new LatLng(location.getLatitude(),location.getLongitude());
//            pickupMarker = mMap.addMarker(new MarkerOptions().position(pickuplocation).title("Pickup Hear"));
//            mrequest.setText("Getting Your Driver .......");
//
//            getClosestDriver();
//        }
//    }
//    private int radius = 1;
//    GeoQuery geoQuery;
//    private Boolean driverFound = false;
//    private String driverFoundId;
//    private void getClosestDriver(){
//        DatabaseReference driverlocation = FirebaseDatabase.getInstance().getReference().child("DriverAvailable");
//        GeoFire geoFire = new GeoFire(driverlocation);
//        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickuplocation.latitude,pickuplocation.longitude),radius);
//        Log.d("llp","value of Radius is"+String.valueOf(radius));
//        Toast.makeText(this, "Value of radius is"+radius, Toast.LENGTH_SHORT).show();
//        geoQuery.removeAllListeners();
//        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
//            @Override
//            public void onKeyEntered(String key, GeoLocation location) {
//                if (!driverFound && requestbol) {
//                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);
//                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.exists() && snapshot.getChildrenCount()>0){
//                                Map<String,Object> drivermap = (Map<String, Object>) snapshot.getValue();
//                                if (driverFound){
//                                    return;
//                                }
////                                Toast.makeText(NewCMap.this, "This is in side the if 111111111111111111111111111", Toast.LENGTH_SHORT).show();
////                                Toast.makeText(NewCMap.this, "This"+ drivermap.get("service"), Toast.LENGTH_SHORT).show();
//                                if (drivermap.get("service").equals(requestService)){
//                                    Toast.makeText(NewCMap.this, "true in this is work  " +requestService, Toast.LENGTH_SHORT).show();
//                                    driverFound = true;
//                                    driverFoundId=key;
//                                    Toast.makeText(NewCMap.this, key, Toast.LENGTH_SHORT).show();
//                                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundId).child("customerRequest");
//                                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                                    HashMap map = new HashMap();
//                                    map.put("customerRideId",customerId);
//                                    map.put("destination",destination);
//                                    map.put("destinationLat",destinationLatLng.latitude);
//                                    map.put("destinationLng",destinationLatLng.longitude);
//                                    driverRef.updateChildren(map);
//                                    mrequest.setText("Looking For a Driver Location....");
//                                    Toast.makeText(NewCMap.this, "Looking for a driver Location....", Toast.LENGTH_SHORT).show();
//                                    getDriverLocation();
//                                    getDriverInfo();
//                                    getHaseRideEnded();
//                                }
//                            }
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
//                }
//            }
//
//            @Override
//            public void onKeyExited(String key) {
//
//            }
//            @Override
//            public void onKeyMoved(String key, GeoLocation location) {
//
//            }
//            @Override
//            public void onGeoQueryReady() {
//                if (!driverFound)
//                {
//                    radius ++;
//                    getClosestDriver();
//                }
//            }
//
//            @Override
//            public void onGeoQueryError(DatabaseError error) {
//
//            }
//        });
//    }
//    private Marker mDrivermarker;
//    DatabaseReference driverLocationRef;
//    ValueEventListener driverLocationRefListener;
//
//    public void getDriverInfo(){
//        mDriverInfo.setVisibility(View.VISIBLE);
//        DatabaseReference  mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundId);
//        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists() && snapshot.getChildrenCount()>0){
//                    Map<String,Object> map = (Map<String, Object>) snapshot.getValue();
//                    if (map.get("name")!=null){
//                        mDriverName.setText(map.get("name").toString());
//                    }
//                    if (map.get("phone")!=null){
//                        mDriverPhone.setText(map.get("phone").toString());
//                    }
//                    if (map.get("car")!=null){
//                        mDriverCar.setText(map.get("car").toString());
//                    }
//                    if (map.get("profileImageUrl")!=null){
//                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mDriverProfileImage);
//                    }
//                    int ratingSum = 0;
//                    float ratingsTotal = 0;
//                    float ratingavg = 0;
//                    for (DataSnapshot child : snapshot.child("rating").getChildren()){
//                        ratingSum =ratingSum  +Integer.valueOf(child.getValue().toString());
//                        ratingsTotal++;
//                    }
//                    if (ratingsTotal!=0){
//                        ratingavg = ratingSum/ratingsTotal;
//                        mRatingBar.setRating(ratingavg);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//    private DatabaseReference driverHasEndedRef;
//    private ValueEventListener driverHasEndedRefListener;
//    private void getHaseRideEnded(){
//        driverHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundId).child("customerRequest").child("customerRideId");
//
//        driverHasEndedRefListener = driverHasEndedRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
//
//                }
//                else{
//                    endRide();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//    private void endRide(){
//        requestbol = false;
//        geoQuery.removeAllListeners();
//        driverLocationRef.removeEventListener(driverLocationRefListener);
//        driverHasEndedRef.removeEventListener(driverHasEndedRefListener);
//
//        if (driverFound != null){
//            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundId).child("customerRequest");
//            driverRef.removeValue();
//            driverFoundId = null;
//        }
//        driverFound = false;
//        radius = 1;
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");
//        GeoFire geoFire = new GeoFire(ref);
//        geoFire.removeLocation(userId);
//
//        if (pickupMarker != null){
//            pickupMarker.remove();
//        }
//        if (mDrivermarker !=null){
//            mDrivermarker.remove();
//        }
//        mrequest.setText("call Uber");
//
//        mDriverInfo.setVisibility(View.GONE);
//        mDriverName.setText("");
//        mDriverPhone.setText("");
//        mDriverCar.setText("");
//        mDriverProfileImage.setImageResource(R.mipmap.ic_launcher);
//
//    }
//
//    private void getDriverLocation(){
//        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("DriverWorking").child(driverFoundId).child("l");
//        Toast.makeText(this, "get driver location", Toast.LENGTH_SHORT).show();
//        driverLocationRefListener =driverLocationRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()&& requestbol){
//                    List<Object> map = (List<Object>) snapshot.getValue();
//                    double locationlat = 0;
//                    double locationlng = 0;
//                    mrequest.setText("Driver Found");
//                    if (map.get(0)!= null) {
//                        locationlat = Double.parseDouble(map.get(0).toString());
//                    }
//                    if (map.get(1)!=null){
//                        locationlng = Double.parseDouble(map.get(1).toString());
//                    }
//                    LatLng driverlatlag = new LatLng(locationlat,locationlng);
//                    if (mDrivermarker!= null){
//                        mDrivermarker.remove();
//                    }
//
//                    Location loc1 = new Location("");
//                    loc1.setLatitude(pickuplocation.latitude);
//                    loc1.setLongitude(pickuplocation.longitude);
//
//                    Location loc2 = new Location("");
//                    loc2.setLatitude(driverlatlag.latitude);
//                    loc2.setLongitude(driverlatlag.longitude);
//
//                    float distance = loc1.distanceTo(loc2);
//
//                    if (distance<100){
//                        mrequest.setText("Driver's Here");
//                    }else{
//                        mrequest.setText("Driver Found:"+String.valueOf(distance));
//                    }
//                    mDrivermarker = mMap.addMarker(new MarkerOptions().position(driverlatlag).title("Your driver"));
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    private void setUserLocationMarker(Location location){
//
//        mLastLocation = location;
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//        if (UserLocationMarker == null) {
//            //Create a new marker
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(latLng);
////            Toast.makeText(this, "Thsis is work", Toast.LENGTH_SHORT).show();
//            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.cariamge));
//            markerOptions.rotation(location.getBearing());
//            markerOptions.anchor(0.5f, 0.5f);
//            UserLocationMarker = mMap.addMarker(markerOptions);
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
//
//
//        } else {
//            //User can also Create a marker
////            Toast.makeText(NewCMap.this, "THis is note work", Toast.LENGTH_SHORT).show();
//            UserLocationMarker.setPosition(latLng);
//            UserLocationMarker.setRotation(location.getBearing());
////            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
//        }
//    }
//
//
//
//    private void startLocationUpdate() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
//    }
//    private void stopLocationUpdate(){
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
//            startLocationUpdate();
//        }else{
//            //need to location permission
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        stopLocationUpdate();
//    }
//
//    private void enableUserLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
//    }
//
//    private void ZoomToUserLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
//        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,20));
//                Log.d("lpg","this "+latLng);
//                Toast.makeText(NewCMap.this, "This lat:-"+latLng, Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(NewCMap.this, "This program is fail", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == ACCESS_LOCATION_REQUESTCODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "This is also work", Toast.LENGTH_SHORT).show();
////                enableUserLocation();
////                ZoomToUserLocation();
//                startLocationUpdate();
//            }
//        }else{
//            //We can show a dialog  permission
//        }
//    }
//
//    Boolean getDriverAroundStated = false;
//    List<Marker> markerList = new ArrayList<Marker>();
//    private void getDriverAround(){
//        getDriverAroundStated = true;
//        DatabaseReference driversLocation = FirebaseDatabase.getInstance().getReference().child("DriverAvailable");
//
//        GeoFire geoFire = new GeoFire(driversLocation);
//        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()),10000);
//        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
//            @Override
//            public void onKeyEntered(String key, GeoLocation location) {
//                for (Marker markerIt : markerList){
//                    if (markerIt.getTag().equals(key))
//                        return;
//
//                }
//                LatLng driverLocation = new LatLng(location.latitude,location.longitude);
//                Marker mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLocation));
//
//                mDriverMarker.setTag(key);
//
//                markerList.add(mDriverMarker);
//            }
//
//            @Override
//            public void onKeyExited(String key) {
//                for (Marker markerIt : markerList){
//                    if (markerIt.getTag().equals(key)){
//                        markerIt.remove();
//                        markerList.remove(markerIt);
//                        return;
//                    }
//                }
//            }
//
//            @Override
//            public void onKeyMoved(String key, GeoLocation location) {
//                for (Marker markerIt : markerList){
//                    if (markerIt.getTag().equals(key)){
//                        markerIt.setPosition(new LatLng(location.latitude,location.longitude));
//                    }
//                }
//            }
//
//            @Override
//            public void onGeoQueryReady() {
//
//            }
//
//            @Override
//            public void onGeoQueryError(DatabaseError error) {
//
//            }
//        });
//    }
//}