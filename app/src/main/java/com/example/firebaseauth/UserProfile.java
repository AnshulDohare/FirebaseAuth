package com.example.firebaseauth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;

public class UserProfile extends AppCompatActivity {
    ScrollView layout;
    TextInputEditText etEmail, etFirstName, etLastName, etAddress, etPassword;
    ImageView profilePic;
    Button btnSave;
    FirebaseDatabase database;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Uri ImageUrl;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        layout = findViewById(R.id.layoutUserProfile);
        etEmail = findViewById(R.id.emailEditTextUserProfile);
        etFirstName = findViewById(R.id.firstNameEditTextUserProfile);
        etLastName = findViewById(R.id.lastNameEditTextUserProfile);
        etAddress = findViewById(R.id.addressEditTextUserProfile);
        etPassword = findViewById(R.id.passwordEditTextUserProfile);
        profilePic = findViewById(R.id.profilePicImageViewUserProfile);
        btnSave = findViewById(R.id.updateButtonUserProfile);
        layout.setVisibility(View.INVISIBLE);

        String uid = FirebaseAuth.getInstance().getUid();

        database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        databaseReference.child("MyUsers/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserInfo userInfo = snapshot.getValue(UserInfo.class);
                    etEmail.setText(Objects.requireNonNull(userInfo).getEmail());
                    etFirstName.setText(userInfo.getFirstName());
                    etLastName.setText(userInfo.getLastName());
                    etAddress.setText(userInfo.getAddress());
                    etPassword.setText(userInfo.getPassword());
                    downloadViaUrl();
                    progressDialog.dismiss();

                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(UserProfile.this, "User Data Not Found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfile.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        profilePic.setOnClickListener(v->{

        });

        profilePic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserProfile.this);
                alertDialog.setTitle("Download Image").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
                                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                                    Toast.makeText(UserProfile.this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    downloadImageInLocalStorage();
                                }
                            }
                            else{
                                downloadImageInLocalStorage();
                            }
                        }

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
                return true;
            }
        });

        btnSave.setOnClickListener(v -> Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show());
    }

    private void downloadImageInLocalStorage() {
        Toast.makeText(this, "download start", Toast.LENGTH_SHORT).show();
        Bitmap bitmap;
        BitmapDrawable bitmapDrawable;
        bitmapDrawable = (BitmapDrawable) profilePic.getDrawable();
        bitmap  = bitmapDrawable.getBitmap();

        FileOutputStream fileOutputStream;

        //File file = Environment.getExternalStorageDirectory();
        //File directory = new File(file.getAbsoluteFile()+"/Download");
        File directory  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        //directory.mkdir();
        String filename;
        try{
            String r = URLUtil.guessFileName(String.valueOf(ImageUrl),null,null);
            filename = String.format("%d.jpg",r);
        }
        catch (Exception e){
            filename = String.format("%d.jpg",System.currentTimeMillis());
        }

        File outFile  = new File(directory,filename);

        try{
            fileOutputStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(outFile));
            sendBroadcast(intent);
            Toast.makeText(this, "Downloaded", Toast.LENGTH_SHORT).show();
        }
        catch (FileNotFoundException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        catch(IOException ioException) {
            throw new RuntimeException(ioException);
        }
        Toast.makeText(this, "download end", Toast.LENGTH_SHORT).show();
    }

    //download with byte
    /** public void downLoadWithByte(){
        StorageReference imageRef = storageReference.child("picture/collage.jpg");
        final long MAXBYTES = 1024*1024;

        imageRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                //convert byte[] to bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                profilePic.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    //Download Image via Url
    public void downloadViaUrl(){
        StorageReference imageRer2 = storageReference.child("MyUsersPic/"+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        imageRer2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //load image
                ImageUrl = uri;
                Glide.with(getApplicationContext()).load(uri).error(R.drawable.launcher_icon_logo).placeholder(R.drawable.profile_pic).into(profilePic);
                progressDialog.dismiss();
                layout.setVisibility(View.VISIBLE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                layout.setVisibility(View.VISIBLE);
            }
        });
    }

    //download all files in the directory
    public void downloadAll(){
        StorageReference imageRer3 = storageReference.child("picture");
        imageRer3.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<StorageReference> list = listResult.getItems();
                for(int i=0;i<list.size();i++){
                    list.get(i).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.v("item1",uri.toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.v("Error2 : ",e.getMessage());
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("Error1 : ",e.getMessage());
            }
        });
    }
}