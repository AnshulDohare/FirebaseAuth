package com.example.firebaseauth;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpPage extends AppCompatActivity {
    TextInputEditText etEmail,etFirstName,etLastName,etAddress,etPassword,etPassword2;
    ImageView profilePic;
    Button btnSignUp;
    FirebaseAuth auth;
    FirebaseDatabase database;
    int IMG_REQUEST_ID = 1;
    Uri imageUri;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        etEmail = findViewById(R.id.emailEditTextSignUp);
        etFirstName = findViewById(R.id.firstNameEditTextSignUp);
        etLastName = findViewById(R.id.lastNameEditTextSignUp);
        etAddress = findViewById(R.id.addressEditTextSignUp);
        etPassword = findViewById(R.id.passwordEditTextSignUp);
        etPassword2 = findViewById(R.id.password2EditTextSignUp);
        btnSignUp =findViewById(R.id.signUpButtonSignUp);
        profilePic = findViewById(R.id.profilePicImageViewSignUp);

        profilePic.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"),IMG_REQUEST_ID);
        });

        profilePic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpPage.this);
                builder.setTitle("Remove Pic").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //profilePic.setImageURI(null);
                        imageUri = null;
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(false).show();
                return true;
            }
        });

        btnSignUp.setOnClickListener(v->{
            String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String password2 = etPassword2.getText().toString().trim();

            String regexSimpleEmail = "[a-zA-Z_]+\\d*[.]?[a-zA-Z_\\d]*[.]?[a-zA-Z\\d]+@[a-zA-Z]+.[a-zA-Z]{2,3}";
            if(email.isEmpty()&&firstName.isEmpty()&&lastName.isEmpty()&&address.isEmpty()&&password.isEmpty()&&password2.isEmpty()){
                Toast.makeText(this, "Enter All Information", Toast.LENGTH_SHORT).show();
            }
            else if(email.isEmpty()){
                etEmail.setError("Enter Email");
            }
            else if(firstName.isEmpty()){
                etFirstName.setError("Enter First Name");
            }
            else if(lastName.isEmpty()){
                etLastName.setError("Enter Last Name");
            }
            else if(address.isEmpty()){
                etAddress.setError("Enter Address");
            }
            else if(password.isEmpty()){
                etPassword.setError("Enter Password");
            }
            else if(password2.isEmpty()){
                etPassword2.setError("Enter Password");
            }
            else if(imageUri==null){
                Toast.makeText(this, "Enter Your Profile Pic", Toast.LENGTH_SHORT).show();
            }
            else if(password.length()<6){
                Toast.makeText(this, "Enter minimum 6 characters in password", Toast.LENGTH_SHORT).show();
            }
            else if(!(password.equals(password2))){
                etPassword2.setError("Password Not Match");
            }
            else if(!(Pattern.matches(regexSimpleEmail,email))){
                etEmail.setError("Invalid Email");
            }
            else{
                userSignUp(email,firstName,lastName,address,password,password2);
            }
        });
    }



    private synchronized void  userSignUp(String email,String firstName,String lastName, String address,String password, String password2) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Signing Up...");
        progressDialog.show();
        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //Log.v("AuthResult : ",authResult.toString());
                //Toast.makeText(SignUpPage.this, "Success : "+authResult, Toast.LENGTH_SHORT).show();
                String uid = auth.getUid();
                storeUserInfo(email,firstName,lastName,address,password,uid);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("Exception : ",e.getMessage());
                Toast.makeText(SignUpPage.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    private void storeUserInfo(String email, String firstName, String lastName, String address, String password,String uid) {
        progressDialog.setTitle("Sending Email Verification Link...");
        database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        HashMap<String,String> map = new HashMap<>();
        map.put("email",email);
        map.put("firstName",firstName);
        map.put("lastName",lastName);
        map.put("address",address);
        map.put("password",password);
        databaseReference.child("MyUsers/"+uid).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                storeProfilePic(email,password,uid);
                //Toast.makeText(SignUpPage.this, "Complete", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.v("databaseError : ",e.getMessage());
                Toast.makeText(SignUpPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeProfilePic(String email,String password,String uid){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child("MyUsersPic/"+uid).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    //@Override
                    //public void onSuccess(AuthResult authResult) {
                        auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if(auth.getCurrentUser().isEmailVerified()){
                                    progressDialog.dismiss();
                                    Log.v("Currrent User1 ::::::",auth.getUid());
                                    startActivity(new Intent(SignUpPage.this,Dashboard.class));
                                    finish();
                                }
                                else{
                                    progressDialog.dismiss();
                                    Log.v("Currrent User2 ::::::",auth.getUid());
                                    startActivity(new Intent(SignUpPage.this, EmailVerification.class));
                                    finish();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Log.v("Currrent User3 ::::::",auth.getUid());
                                DeleteMyUser.deleteMyUser(SignUpPage.this);
                            }
                        });

                    }
                //}).addOnFailureListener(new OnFailureListener() {
                //    @Override
                //    public void onFailure(@NonNull Exception e) {
                //        progressDialog.dismiss();
                //        startActivity(new Intent(SignUpPage.this,SignInPage.class));
                //        finish();
                //    }
                //});
            //}
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == IMG_REQUEST_ID && !(data==null) && !(data.getData()==null)){
            profilePic.setImageURI(data.getData());
            imageUri = data.getData();
            /*imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }*/
        }
    }
}