package com.example.parivahan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.Long;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.InternalTokenProvider;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

//public class HistorySingleActivuty extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {
public class HistorySingleActivuty extends AppCompatActivity implements OnMapReadyCallback {
    private String rideId,currentUserID,customerId,driverId,userDriverOrCustomer;

    private TextView rideLocation;
    private TextView rideLocation2;
    private TextView rideDistance;
    private TextView dateRide;
    private TextView nameUser;
    private TextView phoneUser;

    private Button mPay;
    private Boolean customerPayed = false;
    private ImageView userImage;
    private String distance;
    private Double ridePrice;

    private RatingBar mRatingBar;
    private DatabaseReference historyRideInfoDb;
    private LatLng destinationLatLng,pickupLatLng;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    Marker UserLocationMarker;
    Marker CustomerLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_single_activuty);


//        Intent intent = new Intent(this,PayPalService.class);
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
//        startActivity(intent);
//        polylines = new ArrayList<>();

        rideId = getIntent().getExtras().getString("rideId");

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);


        rideLocation = (TextView) findViewById(R.id.rideLocation);
        rideLocation2 = (TextView) findViewById(R.id.rideLocation2);
        rideDistance = (TextView) findViewById(R.id.rideDistance);
        dateRide = (TextView) findViewById(R.id.rideDate);
        nameUser = (TextView) findViewById(R.id.userName);
        phoneUser = (TextView) findViewById(R.id.UserPhone);
        userImage = (ImageView) findViewById(R.id.userImage);

        mRatingBar =(RatingBar) findViewById(R.id.ratingBar);
        String Addressss = getCompleteAddress(23.0815775,72.5722331);
        Toast.makeText(this, "Address" + Addressss, Toast.LENGTH_SHORT).show();

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mPay = findViewById(R.id.pay);
        historyRideInfoDb = FirebaseDatabase.getInstance().getReference().child("history").child(rideId);

        getRideinformation();
    }

    private void getRideinformation() {
        historyRideInfoDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot child : snapshot.getChildren()){
                        if (child.getKey().equals("customer")){
                            customerId = child.getValue().toString();
                            if (!customerId.equals(currentUserID)){
                                userDriverOrCustomer = "Drivers";
                                mRatingBar.setVisibility(View.GONE);
                                getUserInformation("Customers",customerId);
                            }
                        }
                        if (child.getKey().equals("driver")){
                            driverId = child.getValue().toString();
                            if (!driverId.equals(currentUserID)){
                                userDriverOrCustomer = "Customers";
                                getUserInformation("Drivers",driverId);
                                displayCustomerReletedeObject();
                            }
                        }
                        if (child.getKey().equals("timeStamp")){
                            dateRide.setText(getDate(Long.valueOf(child.getValue().toString())));
                        }
                        if (child.getKey().equals("rating")){
                            mRatingBar.setRating(Integer.valueOf(child.getValue().toString()));
                        }
                        if (child.getKey().equals("customerPaid")){
                            customerPayed = true;
                        }
                        if (child.getKey().equals("distance")){
                            distance = child.getValue().toString();
                            rideDistance.setText(distance.substring(0,Math.min(distance.length(),5)) + " km");
                            ridePrice = Double.valueOf(distance) * 0.5;
                        }
                        if (child.getKey().equals("destination")){
//                            rideLocation.setText(child.getValue().toString());
                        }
                        if (child.getKey().equals("location")){
                            pickupLatLng = new LatLng(Double.valueOf(child.child("form").child("lat").getValue().toString()),Double.valueOf(child.child("form").child("lng").getValue().toString()));
                            destinationLatLng = new LatLng(Double.valueOf(child.child("to").child("lat").getValue().toString()),Double.valueOf(child.child("to").child("lng").getValue().toString()));
                            Toast.makeText(HistorySingleActivuty.this, "Activity = "+new LatLng(Double.valueOf(child.child("to").child("lat").getValue().toString()),Double.valueOf(child.child("to").child("lng").getValue().toString())), Toast.LENGTH_SHORT).show();
                            String PickupAddress = getCompleteAddress(Double.valueOf(child.child("form").child("lat").getValue().toString()),Double.valueOf(child.child("form").child("lng").getValue().toString()));
                            String DropupAddress = getCompleteAddressTo(Double.valueOf(child.child("to").child("lat").getValue().toString()),Double.valueOf(child.child("to").child("lng").getValue().toString()));
                            rideLocation.setText("From :"+PickupAddress);
                            rideLocation2.setText("To :"+DropupAddress);
                            if (destinationLatLng != new LatLng(0,0)){
//                                getRoutToMarker();
                                setmarker();
                            }
                       }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayCustomerReletedeObject() {
        mRatingBar.setVisibility(View.VISIBLE);
        mPay.setVisibility(View.VISIBLE);
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                historyRideInfoDb.child("rating").setValue(rating);
                DatabaseReference mDriverRating = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("rating");
                mDriverRating.child(rideId).setValue(rating);
            }
        });
        if (customerPayed){
            mPay.setEnabled(false);
        }
        else{
            mPay.setEnabled(true);
        }
        mPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                payPalPayMent();

            }
        });
    }
//    private int PAYPAL_REQUEST_CODE = 1;
//    private static PayPalConfiguration config = new PayPalConfiguration()
//            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
//            .clientId(paypalConfig.PAYPAL_CLIENT_ID);
//    private void payPalPayMent() {
//        PayPalPayment payment =new PayPalPayment(new BigDecimal(ridePrice),"INR","Parivahan",
//                PayPalPayment.PAYMENT_INTENT_SALE);
//        Intent intent = new Intent(this, PaymentActivity.class);
//
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
//        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payment);
//
//        startActivityForResult(intent,PAYPAL_REQUEST_CODE);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PAYPAL_REQUEST_CODE){
//            if (requestCode == Activity.RESULT_OK){
//                PaymentConfirmation conform = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
//                if (conform != null){
//                    try {
//                        JSONObject jsonObject = new JSONObject(conform.toJSONObject().toString());
//                        String paymentResponse = jsonObject.getJSONObject("response").getString("state");
//                        if (paymentResponse.equals("approved")){
//                            Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show();
//                            historyRideInfoDb.child("customerPaid").setValue(true);
//                            mPay.setEnabled(false);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }else{
//                Toast.makeText(this, "Payment unsuccessful", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    @Override
    protected void onDestroy() {
//        stopService(new Intent(this,PayPalService.class));
        super.onDestroy();
    }

    private void setmarker() {
        int width = getResources().getDisplayMetrics().widthPixels;
        int padding = (int) (width*0.2);
        Toast.makeText(HistorySingleActivuty.this, "This is use", Toast.LENGTH_SHORT).show();
        if (UserLocationMarker == null && CustomerLocationMarker == null) {

            //Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(destinationLatLng).title("Dropup Location");
//            Toast.makeText(this, "Thsis is work", Toast.LENGTH_SHORT).show();
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.cariamge));
//                    markerOptions.rotation(location.getBearing());
            markerOptions.anchor(0.5f, 0.5f);
            UserLocationMarker = mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, padding));
//            MarkerOptions markerOptionss = new MarkerOptions();
//            markerOptionss.position(pickupLatLng).title("Pickup Location");

            MarkerOptions markerOptions2 = new MarkerOptions();
            markerOptions2.position(pickupLatLng);
//            Toast.makeText(this, "Thsis is work", Toast.LENGTH_SHORT).show();
            markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickupmark));
//                    markerOptions.rotation(driverlatlag.getBearing());
            markerOptions2.anchor(0.5f, 0.5f);
            markerOptions2.title("Pickup Location");
            CustomerLocationMarker = mMap.addMarker(markerOptions2);

//            Toast.makeText(this, "Thsis is work", Toast.LENGTH_SHORT).show();
//            markerOptionss.icon(BitmapDescriptorFactory.fromResource(R.drawable.cariamge));
//                    markerOptions.rotation(location.getBearing());
//            markerOptionss.anchor(0.5f, 0.5f);
//            CustomerLocationMarker = mMap.addMarker(markerOptionss);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupLatLng, padding));

        } else {
            //User can also Create a marker
//            Toast.makeText(NewDMap.this, "THis is note work", Toast.LENGTH_SHORT).show();
            UserLocationMarker.setPosition(destinationLatLng);
            CustomerLocationMarker.setPosition(pickupLatLng);
//                    UserLocationMarker.setRotation(location.getBearing());
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }
    }

    private void getUserInformation(String otherCustomerOrDriver, String otherUserId) {
        DatabaseReference mOtherUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(otherCustomerOrDriver).child(otherUserId);
        mOtherUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Map<String,Object> map = (Map<String, Object>) snapshot.getValue();
                        if (map.get("name") != null){
                            nameUser.setText(map.get("name").toString());
                        }
                        if (map.get("phone") != null){
                            phoneUser.setText(map.get("phone").toString());
                        }
                        if (map.get("profileImageUrl") != null){
                            Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(userImage);
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getDate(Long timeStamp) {
        Calendar cal =Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(timeStamp*1000);
        String date = DateFormat.format("dd-MM-yyyy hh:mm",cal).toString();
        return date;
    }
//    private void getRoutToMarker() {
////        Toast.makeText(NewDMap.this, "latlng = "+new LatLng(mmlocation.getLatitude(),mmlocation.getLongitude()) , Toast.LENGTH_SHORT).show();
//        Routing routing = new Routing.Builder()
//                .travelMode(AbstractRouting.TravelMode.DRIVING)
//                .withListener(this)
//                .alternativeRoutes(false)
//                .waypoints(pickupLatLng,destinationLatLng)
//                .build();
//        routing.execute();
//    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }

    public String getCompleteAddress(double Latitude,double Longitude){
        String address = "";
        String addressline = "";
        String faddress = "";
        Geocoder geocoder = new Geocoder(HistorySingleActivuty.this,Locale.getDefault());

        try {
            List<Address> addresses =  geocoder.getFromLocation(Latitude,Longitude,1);

            if (address != null){
                Address returnAddress = addresses.get(0);
                String Locality = addresses.get(0).getLocality();
                String Country = addresses.get(0).getCountryName();
                String subLoaclity = addresses.get(0).getSubLocality();
                String City = addresses.get(0).getAdminArea();
                String Pincode = addresses.get(0).getPostalCode();
                faddress = subLoaclity+","+Locality+","+City +","+Country+","+Pincode;
                 addressline = returnAddress.getAddressLine(0);
                StringBuilder stringBuilderReturnAddress = new StringBuilder("");

                for (int i=0; i<=returnAddress.getMaxAddressLineIndex(); i++){
                    stringBuilderReturnAddress.append(returnAddress.getAddressLine(i)).append("\n");
                }
                address = stringBuilderReturnAddress.toString();
            }
            else{
                Toast.makeText(this, "Address not Found", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
        return faddress;
    }






    public String getCompleteAddressTo(double Latitude,double Longitude){
        String address = "";
        String addressline = "";
        String faddress = "";
        Geocoder geocoder = new Geocoder(HistorySingleActivuty.this,Locale.getDefault());

        try {
            List<Address> addresses =  geocoder.getFromLocation(Latitude,Longitude,1);

            if (address != null){
                Address returnAddress = addresses.get(0);
                String Locality = addresses.get(0).getLocality();
                String Country = addresses.get(0).getCountryName();
                String subLoaclity = addresses.get(0).getSubLocality();
                String City = addresses.get(0).getAdminArea();
                String Pincode = addresses.get(0).getPostalCode();
                faddress = subLoaclity+","+Locality+","+City +","+Country+","+Pincode;
                addressline = returnAddress.getAddressLine(0);
                StringBuilder stringBuilderReturnAddress = new StringBuilder("");

                for (int i=0; i<=returnAddress.getMaxAddressLineIndex(); i++){
                    stringBuilderReturnAddress.append(returnAddress.getAddressLine(i)).append("\n");
                }
                address = stringBuilderReturnAddress.toString();
            }
            else{
                Toast.makeText(this, "Address not Found", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
        return faddress;
    }

//    private List<Polyline> polylines;
//    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
//    @Override
//    public void onRoutingFailure(RouteException e) {
//        if(e != null) {
//            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }else {
//            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onRoutingStart() {
//
//    }
//
//    @Override
//    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
//        Toast.makeText(HistorySingleActivuty.this, "Routing Process", Toast.LENGTH_SHORT).show();
//        if(polylines.size()>0) {
//            for (Polyline poly : polylines) {
//                poly.remove();
//            }
//        }
//        polylines = new ArrayList<>();
//        //add route(s) to the map.
//        for (int i = 0; i <route.size(); i++) {
//
//            //In case of more than 5 alternative routes
//            int colorIndex = i % COLORS.length;
//
//            PolylineOptions polyOptions = new PolylineOptions();
//            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
//            polyOptions.width(10 + i * 3);
//            polyOptions.addAll(route.get(i).getPoints());
//            Polyline polyline = mMap.addPolyline(polyOptions);
//            polylines.add(polyline);
//            Toast.makeText(HistorySingleActivuty.this, "This is Toute sucess", Toast.LENGTH_SHORT).show();
//            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onRoutingCancelled() {
//
//    }
}