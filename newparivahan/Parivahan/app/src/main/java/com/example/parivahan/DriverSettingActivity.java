package com.example.parivahan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DriverSettingActivity extends AppCompatActivity {
    private EditText mNameField,mPhoneField,mcarField;

    private Button mConform,mBack;
    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;
    private ImageView mProfileImg;


    private RadioGroup mRadioGroup;
    private String userId;
    private String mName;
    private String mPhone;
    private String mcar;
    private String mService;
    private String mProfileImageUrl;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_setting);


        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);
        mcarField = (EditText) findViewById(R.id.car);


        mConform = (Button) findViewById(R.id.conform);
        mBack = (Button) findViewById(R.id.back);
        mProfileImg = (ImageView) findViewById(R.id.profileImage);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        getUserInfo();

        mProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent= new Intent();
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setAction(Intent.ACTION_PICK);
//                Intent intent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,1);
//                startActivityForResult(intent , 1);

            }
        });

        mConform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveUserInformation();
//                Toast.makeText(DriverSettingActivity.this, "This is conform", Toast.LENGTH_SHORT).show();
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });
    }
    private void getUserInfo(){
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    Map<String,Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name")!=null){
                        mName = map.get("name").toString();
                        mNameField.setText(mName);
                    }
                    if (map.get("phone")!=null){
                        mPhone = map.get("phone").toString();
                        mPhoneField.setText(mPhone);
                    }
                    if (map.get("car")!=null){
                        mcar = map.get("car").toString();
                        mcarField.setText(mcar);
                    }
                    if (map.get("service")!=null){
                        mService = map.get("service").toString();
                        switch (mService){
                            case "UberX":
                                mRadioGroup.check(R.id.UberX);
                                break;
                            case "UberBlack":
                                mRadioGroup.check(R.id.UberBlack);
                                break;

                            case "UberXl":
                                mRadioGroup.check(R.id.UberXl);
                                break;
                        }
                    }
                    if (map.get("profileImageUrl")!=null){
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void saveUserInformation(){
        mName = mNameField.getText().toString();
        mPhone = mPhoneField.getText().toString();
        mcar = mcarField.getText().toString();


        int selectId = mRadioGroup.getCheckedRadioButtonId();

        final RadioButton radioButton = (RadioButton) findViewById(selectId);
        if (radioButton.getText() == null){
            return;
        }

        mService = radioButton.getText().toString();
        Map userInfo = new HashMap();
        userInfo.put("name",mName);
        userInfo.put("phone",mPhone);
        userInfo.put("car",mcar);
        userInfo.put("service",mService);
        mDriverDatabase.updateChildren(userInfo);

        if (imageUri!=null){
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_Image").child(userId);


//            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    filePath.getDownloadUrl();
//                }
//            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//
//                }
//            });

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),imageUri);
            } catch (IOException e) {
                e.printStackTrace();

            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
//                           Model model = new Model(uri.toString());
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl",uri.toString());
                            mDriverDatabase.updateChildren(newImage);
                            Toast.makeText(DriverSettingActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DriverSettingActivity.this, "Image is Not Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            });
        }

        else{
            Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1 && resultCode==Activity.RESULT_OK && data != null){
            imageUri = data.getData();
            mProfileImg.setImageURI(imageUri);
//            resultUri = imageUri;
//            mProfileImg.setImageURI(resultUri);
        }
    }
}

