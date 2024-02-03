package com.example.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class Dashboard extends AppCompatActivity {
    Button userProfile,logOut,deleteAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        userProfile = findViewById(R.id.userProfileButtonDashBoard);
        logOut = findViewById(R.id.logOutButtonDashBoard);
        deleteAccount = findViewById(R.id.deleteAccountButtonDashBoard);
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(this,SignInPage.class));
        }

        userProfile.setOnClickListener(v->startActivity(new Intent(this, UserProfile.class)));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(FirebaseAuth.getInstance().getCurrentUser().getEmail()).setCancelable(true).show();

        logOut.setOnClickListener(v-> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInPage.class));
            finish();
        });

        deleteAccount.setOnClickListener(v-> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Delete Account").setMessage("Are You Sure ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteUser();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        });

    }

    private synchronized void deleteUser() {
            FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase.getInstance().getReference().child("MyUsers/"+auth.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(Dashboard.this, "data deleted", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.v("User Data Delete :::::",e.getMessage());
                }
            });

            FirebaseStorage.getInstance().getReference().child("MyUsersPic/"+auth.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(Dashboard.this, "Pic Deleted", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Dashboard.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.v("PicDelete ::::::",e.getMessage());
                }
            });


            auth.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
//                        auth.reload();
//                        if(auth==null){
//                            startActivity(new Intent(Dashboard.this,SignInPage.class));
//                            finish();
//                        }
                        Toast.makeText(Dashboard.this, "User Deleted", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Dashboard.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.v("user Delete ::::::",e.getMessage());
                }
            });
    }
}