package com.example.parivahan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GetLatLng extends AppCompatActivity {
    EditText etplace;
    Button btsubmit;
    TextView tvAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_lat_lng);

        etplace = findViewById(R.id.et_place);
        btsubmit = findViewById(R.id.bt_submit);
        tvAddress = findViewById(R.id.tv_address);


        btsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = etplace.getText().toString();
                geolocationn ggg = new geolocationn();
                ggg.getAddress(address,getApplicationContext(),new Geohendler());
//                geolocationn geolocatio = new geolocationn();
//                geolocatio.getAddress
            }
        });
    }

    private class Geohendler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String address;
            switch (msg.what){
                case 1:
                    Bundle bundle = msg.getData();
                    address = bundle.getString("address");
                    break;
                default:
                    address =null;
            }
            tvAddress.setText(address);
        }
    }
}