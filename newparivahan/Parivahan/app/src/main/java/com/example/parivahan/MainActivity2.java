package com.example.parivahan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.parivahan.Mode.MainProject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity2 extends AppCompatActivity {
    EditText edit_text;
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;

    ApiInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        edit_text = findViewById(R.id.edit_text);
        recyclerView = findViewById(R.id.recyclerview);
        relativeLayout = findViewById(R.id.nodata_found);
        relativeLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .build();

        apiInterface = retrofit.create(ApiInterface.class);

        edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getData(s.toString());
            }
        });
    }
    private void getData(String text){
        apiInterface.getPlace(text,getString(R.string.api_key)).enqueue(new Callback<MainProject>() {
            @Override
            public void onResponse(Call<MainProject> call, Response<MainProject> response) {
                if (response.isSuccessful()){
                    relativeLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

//                    RecycleVIewAdepter recycleVIewAdepter = new RecycleVIewAdepter(response.body().getPredictions());
//                    recyclerView.setAdapter(recycleVIewAdepter);
                }else{
                    relativeLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<MainProject> call, Throwable t) {
                relativeLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(MainActivity2.this, "Error occured", Toast.LENGTH_SHORT).show();
            }
        });
    }
}