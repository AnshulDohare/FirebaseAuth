package com.example.firebaseauth;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class UpdatePassword extends Fragment {
    TextInputEditText etEmail,etOldPassword,etNewPassword,etNewPassword2;
    Button btnUpdate;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance() ;
    final String email = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();
    public UpdatePassword() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //btnUpdate.setOnClickListener(v->{
            //Toast.makeText(getContext(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(), Toast.LENGTH_SHORT).show();
        //});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_password, container, false);
        etEmail = view.findViewById(R.id.emailEditTextUpdatePassword);
        etOldPassword =(view).findViewById(R.id.oldPasswordEditTextUpdatePassword);
        etNewPassword = view.findViewById(R.id.newPasswordEditTextUpdatePassword);
        etNewPassword2 = view.findViewById(R.id.newPasswordEditTextUpdatePassword2);
        btnUpdate = view.findViewById(R.id.updateButtonUpdatePassword);
        etEmail.setText(email);
        btnUpdate.setOnClickListener(v->{
            String oldPassword = etOldPassword.getText().toString().trim();
            String NewPassword = etNewPassword.getText().toString().trim();
            String NewPassword2 = etNewPassword2.getText().toString().trim();

            if(oldPassword.isEmpty()||NewPassword.isEmpty()||NewPassword2.isEmpty()){
                Toast.makeText((UpdateEmailPassword)getContext(), "Enter All Fields", Toast.LENGTH_SHORT).show();
            }
            else if (NewPassword.length()<6){
                etNewPassword.setError("Password is too short");
            }
            else if(!(NewPassword.equals(NewPassword2))){
                etNewPassword2.setError("Password not match");
            }
            else {
                ProgressDialog progressDialog = new ProgressDialog((UpdateEmailPassword)getContext());
                progressDialog.setTitle("Updating Password");
                progressDialog.setCancelable(false);
                progressDialog.setIcon(getResources().getDrawable(R.drawable.launcher_icon_logo));
                progressDialog.show();
                AuthCredential authCredential = EmailAuthProvider.getCredential(email,oldPassword);
                firebaseAuth.getCurrentUser().reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseAuth.getCurrentUser().updatePassword(NewPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Map<String,Object> map = new HashMap<>();
                                map.put("password",NewPassword);
                                FirebaseDatabase.getInstance().getReference().child("MyUsers/"+firebaseAuth.getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        Toast.makeText((UpdateEmailPassword)getContext(), "Password Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText((UpdateEmailPassword)getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText((UpdateEmailPassword)getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText((UpdateEmailPassword)getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return view;
    }
}