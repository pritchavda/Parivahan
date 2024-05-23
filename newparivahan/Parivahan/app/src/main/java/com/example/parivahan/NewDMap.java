package com.example.parivahan;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import com.example.parivahan.databinding.ActivityNewDmapBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//public class NewDMap extends FragmentActivity implements OnMapReadyCallback {
public class NewDMap extends FragmentActivity implements OnMapReadyCallback, RoutingListener {


    private int ACCESS_LOCATION_REQUESTCODE = 1;
    private GoogleMap mMap;
    Location mLastLocation;
    private LinearLayout mmmMap;
    ViewGroup.LayoutParams lp;
    private ActivityNewDmapBinding binding;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Marker UserLocationMarker;


    private Switch mWorkingSwitch;

    private Button mlogout,mSetting,mRideStatus,mHistory;

    private int status = 0;
    private String customerId = "",destination;

    private float rideDistance;
    private LatLng destinationLatLng;

    private Boolean isLoggingOut = false;
    //    private ActivityNewDmapBinding binding;
    private LinearLayout mCustomerInfo;
    private ImageView mCustomerProfileImage;
    private TextView mCustomerName,mCustomerPhone,mCustomerDestination;

    private Location mmlocation;

    private LatLng pickupLatLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewDmapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        polylines  = new ArrayList<>();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        mCustomerInfo = (LinearLayout) findViewById(R.id.customerInfo);
        mCustomerProfileImage = (ImageView) findViewById(R.id.customerProfileImage);
        mCustomerName = (TextView) findViewById(R.id.customerName);
        mCustomerPhone = (TextView) findViewById(R.id.customerPhone);
        mCustomerDestination = (TextView) findViewById(R.id.customerDestination);
        mlogout =(Button) findViewById(R.id.logout);
        mSetting = (Button) findViewById(R.id.setting);
        mHistory = (Button) findViewById(R.id.history);
        mRideStatus = (Button) findViewById(R.id.rideStatus);
        mWorkingSwitch = (Switch) findViewById(R.id.WorkingSwitch);
        mmmMap = findViewById(R.id.mmMap);

        mWorkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    connectDriver();
                }
                else{
                    disconnectDriver();
                }
            }
        });
        mRideStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status){
                    case 1:
                        status=2;
//                        *
                        erasePolyLine();
                        if (destinationLatLng.latitude != 0.0  && destinationLatLng.longitude!=0.0){
//                            *
                            getRoutToMarker(destinationLatLng);
                        }
                        mRideStatus.setText("driverCompleted" + status);
                        break;
                    case 2:
                        recordRide();
                        endRide();
                        break;
                }
            }
        });

        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewDMap.this,DriverSettingActivity.class);
                startActivity(intent);
                return;
            }
        });
        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewDMap.this,HistoryActivity.class);
                intent.putExtra("customerOrDriver","Drivers");
                startActivity(intent);
            }
        });
        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLoggingOut = true;

                disconnectDriver();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(NewDMap.this,MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
//        *
        getAssigndcustomer();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void getAssigndcustomer(){
        String driveId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assigedcustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driveId).child("customerRequest").child("customerRideId");

        assigedcustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    status = 1;
                    customerId = snapshot.getValue().toString();
                    getAssigndcustomerpickuplocaiton();
                    getAssigndcustomerDestination();
                    getAssigndcustomerInfo();
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

    Marker pickupLocation;
    DatabaseReference assigedcustomerpickuplocationRef;
    ValueEventListener assigedcustomerpickuplocaitonRefListener;
    private void getAssigndcustomerpickuplocaiton(){
        assigedcustomerpickuplocationRef = FirebaseDatabase.getInstance().getReference().child("CustomerRequest").child(customerId).child("l");

        assigedcustomerpickuplocaitonRefListener =  assigedcustomerpickuplocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && !customerId.equals("")){
                    List<Object> map = (List<Object>) snapshot.getValue();
                    double locationlat = 0;
                    double locationlng = 0;
                    if (map.get(0)!= null){
                        locationlat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1)!= null){
                        locationlng = Double.parseDouble(map.get(1).toString());
                    }
                    pickupLatLng = new LatLng(locationlat,locationlng);
                    Toast.makeText(NewDMap.this, "Lat log is="+pickupLatLng, Toast.LENGTH_SHORT).show();
                    pickupLocation = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("pickup Location"));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(pickupLatLng);
//            Toast.makeText(this, "Thsis is work", Toast.LENGTH_SHORT).show();
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.customerlocation));
//                    markerOptions.rotation(pickupLatLng.getBearing());
                    markerOptions.anchor(0.5f, 0.5f);
                    UserLocationMarker = mMap.addMarker(markerOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupLatLng, 17));
                    getRoutToMarker(pickupLatLng);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getRoutToMarker(LatLng pickupLatLng) {
        Toast.makeText(NewDMap.this, "latlng = "+new LatLng(mmlocation.getLatitude(),mmlocation.getLongitude()) , Toast.LENGTH_SHORT).show();
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(mmlocation.getLatitude(),mmlocation.getLongitude()), pickupLatLng)
                .build();
        routing.execute();
    }

    public void getAssigndcustomerDestination(){
        String driveId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assigedcustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driveId).child("customerRequest");

        assigedcustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Map<String,Object> map= (Map<String, Object>) snapshot.getValue();
                    if (map.get("destination")!= null){
                        destination = map.get("destination").toString();
                        mCustomerDestination.setText("Destination: "+ destination);
                    }
                    else {
                        mCustomerDestination.setText("Destination: --");
                    }

                    Double destinationLat = 0.0;
                    Double destinationLng = 0.0;
                    if (map.get("destinationLat")!= null){
                        destinationLat = Double.valueOf(map.get("destinationLat").toString());
                    }
                    if (map.get("destinationLng")!= null){
                        destinationLng = Double.valueOf(map.get("destinationLng").toString());
                        destinationLatLng = new LatLng(destinationLat,destinationLng);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getAssigndcustomerInfo(){
        mCustomerInfo.setVisibility(View.VISIBLE);
        lp = mmmMap.getLayoutParams();
        lp.height = 1600;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mmmMap.setLayoutParams(lp);
//        ddd
        DatabaseReference  mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    Map<String,Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name")!=null){
                        mCustomerName.setText(map.get("name").toString());
                    }
                    if (map.get("phone")!=null){
                        mCustomerPhone.setText(map.get("phone").toString());
                    }
                    if (map.get("profileImageUrl")!=null){
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mCustomerProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void endRide(){
        mRideStatus.setText("Pickup Customer");
        erasePolyLine();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
        driverRef.removeValue();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerId);

        customerId = "";
        rideDistance = 0;
        if (pickupLocation != null){
            pickupLocation.remove();
        }
        if (assigedcustomerpickuplocationRef != null){
            assigedcustomerpickuplocationRef.removeEventListener(assigedcustomerpickuplocaitonRefListener);
        }
        mCustomerInfo.setVisibility(View.GONE);
        lp = mmmMap.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mmmMap.setLayoutParams(lp);
        mCustomerName.setText("");
        mCustomerPhone.setText("");
        mCustomerDestination.setText("Destination: --");
        mCustomerProfileImage.setImageResource(R.mipmap.ic_launcher);

    }

    private void recordRide(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("history");
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("history");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
        String requestId = historyRef.push().getKey();
        driverRef.child(requestId).setValue(true);
        customerRef.child(requestId).setValue(true);

        HashMap map = new HashMap();
        map.put("driver",userId);
        map.put("customer",customerId);
        map.put("rating",0);
        map.put("timeStamp",getCurrentTime());
        map.put("destination",destination);
        map.put("location/form/lat",pickupLatLng.latitude);
        map.put("location/form/lng",pickupLatLng.longitude);
        map.put("location/to/lat",destinationLatLng.latitude);
        map.put("location/to/lng",destinationLatLng.longitude);
        map.put("distance",rideDistance);

        historyRef.child(requestId).updateChildren(map);
    }

    private Long getCurrentTime() {
        Long timeStamp = System.currentTimeMillis()/1000;
        return timeStamp;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


    }
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d("lpg","onlocation:-"+locationResult.getLocations());
            mmlocation = locationResult.getLastLocation();

            if (mMap != null){
                setUserLocationMarker(locationResult.getLastLocation());
            }
        }
    };

    private void setUserLocationMarker(Location location){

        if (!customerId.equals("")){
            rideDistance += mLastLocation.distanceTo(location)/1000;
        }

        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        Toast.makeText(this, "11111111111111111111111111111111" + latLng, Toast.LENGTH_SHORT).show();
//        Toast.makeText(NewDMap.this, "location"+new LatLng(mmlocation.getLatitude(),mmlocation.getLongitude())+"    "+new LatLng(location.getLatitude(), location.getLongitude()), Toast.LENGTH_SHORT).show();
        String User_Id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("DriverAvailable");
        DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("DriverWorking");

        GeoFire geoFireAvailable = new GeoFire(refAvailable);
        GeoFire geoFireWorking = new GeoFire(refWorking);
//        Toast.makeText(NewDMap.this, "customer id"+customerId, Toast.LENGTH_SHORT).show();
        switch (customerId){

            case "":
                geoFireWorking.removeLocation(User_Id);
                geoFireAvailable.setLocation(User_Id, new GeoLocation(location.getLatitude(), location.getLongitude()));
//                Toast.makeText(NewDMap.this, "1111111", Toast.LENGTH_SHORT).show();
                break;
            default:
                geoFireAvailable.removeLocation(User_Id);
                geoFireWorking.setLocation(User_Id, new GeoLocation(location.getLatitude(), location.getLongitude()));
//                Toast.makeText(NewDMap.this, "22222222", Toast.LENGTH_SHORT).show();
                break;
        }

        if (UserLocationMarker == null) {

            //Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
//            Toast.makeText(this, "Thsis is work", Toast.LENGTH_SHORT).show();
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.cariamge));
            markerOptions.rotation(location.getBearing());
            markerOptions.anchor(0.5f, 0.5f);
            UserLocationMarker = mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        } else {
            //User can also Create a marker
//            Toast.makeText(NewDMap.this, "THis is note work", Toast.LENGTH_SHORT).show();
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
//        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
//            startLocationUpdate();
//        }else{
//            //need to location permission
//        }
    }
    private void connectDriver(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(NewDMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUESTCODE);
//            Toast.makeText(this, "this is work", Toast.LENGTH_SHORT).show();
//            enableUserLocation();
//            ZoomToUserLocation();
            startLocationUpdate();
        } else {
            ActivityCompat.requestPermissions(NewDMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUESTCODE);
        }
    }
    private void disconnectDriver(){
//        *
        stopLocationUpdate();
        String User_Id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriverAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(User_Id);
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        if (!isLoggingOut){
//            disconnectDriver();
//        }
//    }

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
                Toast.makeText(NewDMap.this, "This lat:-"+latLng, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewDMap.this, "This program is fail", Toast.LENGTH_SHORT).show();
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

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        Toast.makeText(NewDMap.this, "Routing Process", Toast.LENGTH_SHORT).show();
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
            Toast.makeText(NewDMap.this, "This is Toute sucess", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled() {

    }
    private void erasePolyLine(){
        for (Polyline line :polylines){
            line.remove();
        }
        polylines.clear();
    }
}