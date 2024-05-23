package com.example.parivahan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parivahan.HistoryRecyclerView.HistoryAdepter;
import com.example.parivahan.HistoryRecyclerView.HistoryObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {
//    private RecyclerView mhistoryRecyclerView;
//    private RecyclerView.Adapter mHistoryAdepter;
//    private RecyclerView.LayoutManager mHistoryLayoutManager;
    private String customerOrDriver,userId;
    RecyclerView mrecyclerView;
    LinearLayoutManager layoutManager;
    List<HistoryObject>userList;
    RecyclerView.Adapter adapter;
    private TextView mBalance;
    private Double Balance = 0.0;
//    HistoryObject obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        userList = new ArrayList<>();
//        initData();
        customerOrDriver = getIntent().getExtras().getString("customerOrDriver");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUserHistroyIds();

        mBalance=findViewById(R.id.balance);
//        Toast.makeText(HistoryActivity.this, "obj   "+obj, Toast.LENGTH_SHORT).show();
//        userList.add(obj);
//        for (int i=0;i<100;i++){
//            HistoryObject obj= new HistoryObject(Integer.toString(i));
////            userList.add(obj);
//            resultHistory.add(obj);

        if (customerOrDriver.equals("Drivers")){
            mBalance.setVisibility(View.VISIBLE);
        }
//        }

    }

    private void getUserHistroyIds() {
        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(customerOrDriver).child(userId).child("history");
        userHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot history :snapshot.getChildren()){
//                    Toast.makeText(HistoryActivity.this, "This Function is Work", Toast.LENGTH_SHORT).show();
                            FatchRideInformation(history.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FatchRideInformation(String rideKey) {
//        Toast.makeText(HistoryActivity.this, "This Function is Work", Toast.LENGTH_SHORT).show();
        DatabaseReference historyDatabase = FirebaseDatabase.getInstance().getReference().child("history").child(rideKey);
        historyDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
//                    userList = new ArrayList<>();
                    String rideId = snapshot.getKey();
                    Long timeStamp = 0L;
                    String distance = "";
                    Double ridePrice = 0.0;
                        if (snapshot.child("timeStamp").getValue() != null){
//                            timeStamp =  Long.valueOf(child.getValue().toString());
                            timeStamp =  Long.valueOf(snapshot.child("timeStamp").getValue().toString());
                        }
                        if (snapshot.child("customerPaid").getValue() != null&& snapshot.child("driverPaidOut").getValue() != null){
                            if (snapshot.child("distance").getValue() != null) {
                                distance = snapshot.child("distance").getValue().toString();
                                ridePrice = (Double.valueOf(distance) * 0.4);
                                Balance += ridePrice;
                                mBalance.setText("Balance: "+ String.valueOf(Balance));
                            }
                        }

//                    Toast.makeText(HistoryActivity.this, "History Available"+rideId, Toast.LENGTH_SHORT).show();
                    HistoryObject obj = new HistoryObject(rideId,getDate(timeStamp));
                    resultHistory.add(obj);
                }
                initRecyclerView();
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

    private ArrayList resultHistory = new ArrayList<>();
    private ArrayList getDataSetHistory() {
        return resultHistory;
    }


    private void initRecyclerView() {
        mrecyclerView=findViewById(R.id.RecyclerView);
        mrecyclerView.setNestedScrollingEnabled(false);
        mrecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mrecyclerView.setLayoutManager(layoutManager);
        adapter=new Adapter(getDataSetHistory());
//        Toast.makeText(HistoryActivity.this, "t = "+getDataSetHistory(), Toast.LENGTH_SHORT).show();
        mrecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
//    private void initData() {
//        userList = new ArrayList<>();
//
//        userList.add(new ModelClass(R.drawable.ic_baseline_account_circle_24,"Anjali","How are you?","10:45 am","_______________________________________"));
//
//        userList.add(new ModelClass(R.drawable.ic_baseline_account_circle_24,"Brijesh","I am fine","15:08 pm","_______________________________________"));
//
//        userList.add(new ModelClass(R.drawable.ic_baseline_account_circle_24,"Sam","You Know?","1:02 am","_______________________________________"));
//
//        userList.add(new ModelClass(R.drawable.ic_baseline_account_circle_24,"Divya","How are you?","12:55 pm","_______________________________________"));
//
//        userList.add(new ModelClass(R.drawable.ic_baseline_account_circle_24,"Simran","This is Easy","13:50 am","_______________________________________"));
//
//        userList.add(new ModelClass(R.drawable.ic_baseline_account_circle_24,"Karan","I am Don","1:08 am","_______________________________________"));
//
//        userList.add(new ModelClass(R.drawable.ic_baseline_account_circle_24,"Sameer","You Know this?","4:02 am","_______________________________________"));
//
//        userList.add(new ModelClass(R.drawable.ic_baseline_account_circle_24,"Baby","How ?","11:55 pm","_______________________________________"));
//
//        userList.add(new ModelClass(R.drawable.ic_baseline_account_circle_24,"Anjali","How are you?","10:45 am","_______________________________________"));
//
//        userList.add(new ModelClass(R.drawable.ic_baseline_account_circle_24,"Brijesh","I am fine","15:08 pm","_______________________________________"));
//
//        userList.add(new ModelClass(R.drawable.ic_baseline_account_circle_24,"Sam","You Know?","1:02 am","_______________________________________"));
//
//        userList.add(new ModelClass(R.drawable.ic_baseline_account_circle_24,"Divya","How are you?","12:55 pm","_______________________________________"));
//
//        userList.add(new ModelClass(R.drawable.ic_baseline_account_circle_24,"Simran","This is Easy","13:50 am","_______________________________________"));
//
//        userList.add(new ModelClass(R.drawable.ic_baseline_account_circle_24,"Karan","I am Don","1:08 am","_______________________________________"));
//
//        userList.add(new ModelClass(R.drawable.ic_baseline_account_circle_24,"Sameer","You Know this?","4:02 am","_______________________________________"));
//
//        userList.add(new ModelClass(R.drawable.ic_baseline_account_circle_24,"Baby","How ?","11:55 pm","_______________________________________"));
//
//    }


}



//    <?xml version="1.0" encoding="utf-8"?>
//<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
//        xmlns:app="http://schemas.android.com/apk/res-auto"
//        xmlns:tools="http://schemas.android.com/tools"
//        android:layout_width="match_parent"
//        android:layout_height="match_parent"
//        tools:context=".HistoryActivity"
//        android:fitsSystemWindows="true">
//<LinearLayout
//            android:layout_width="match_parent"
//                    android:layout_height="match_parent"
//                    android:orientation="vertical">
//<androidx.core.widget.NestedScrollView
//        android:layout_width="match_parent"
//        android:layout_height="match_parent">
//<androidx.recyclerview.widget.RecyclerView
//        android:layout_width="match_parent"
//        android:layout_height="wrap_content"
//        android:id="@+id/historyRecyclerView"
//        android:scrollbars="vertical">
//
//</androidx.recyclerview.widget.RecyclerView>
//</androidx.core.widget.NestedScrollView>
//</LinearLayout>
//
//</androidx.constraintlayout.widget.ConstraintLayout>



//        mhistoryRecyclerView = (RecyclerView) findViewById(R.id.historyRecyclerView);
//
//        mhistoryRecyclerView.setNestedScrollingEnabled(false);
//        mhistoryRecyclerView.setHasFixedSize(true);
//
//        mHistoryLayoutManager = new LinearLayoutManager(HistoryActivity.this);
//        mhistoryRecyclerView.setLayoutManager(mHistoryLayoutManager);
//        mHistoryAdepter = new HistoryAdepter(getDataSetHistory(),HistoryActivity.this);
//
//        mhistoryRecyclerView.setAdapter(mHistoryAdepter);
//
////            HistoryObject obj= new HistoryObject("123456");
//        for (int i=0;i<100;i++){
//            HistoryObject obj= new HistoryObject(Integer.toString(i));
//            resultHistory.add(obj);
//        }
//        mHistoryAdepter.notifyDataSetChanged();
//    }
//
//    private ArrayList resultHistory = new ArrayList<HistoryObject>();
//    private ArrayList<HistoryObject> getDataSetHistory() {
//        return resultHistory;
//    }
//}