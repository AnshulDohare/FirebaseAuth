package com.example.firebaseauth;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class UpdateEmail extends Fragment {
    TextInputEditText etEmail,etPassword,etNewEmail,etNewEmail2;
    Button btnUpdateEmail;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String email = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();
    public UpdateEmail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_email, container, false);
        etEmail = view.findViewById(R.id.emailEditTextUpdateEmail);
        etPassword = view.findViewById(R.id.passwordEditTextUpdateEmail);
        etNewEmail = view.findViewById(R.id.newEmailEditTextUpdateEmail);
        etNewEmail2 = view.findViewById(R.id.newEmailEditTextUpdateEmail2);
        btnUpdateEmail = view.findViewById(R.id.updateButtonUpdateEmail);

        etEmail.setText(email);
        btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "Email Updated", Toast.LENGTH_SHORT).show();
                String password = etPassword.getText().toString().trim();
                String newEmail = etNewEmail.getText().toString().trim();
                String newEmail2 = etNewEmail2.getText().toString().trim();
                String regexSimpleEmail = "[a-zA-Z_]+\\d*[.]?[a-zA-Z_\\d]*[.]?[a-zA-Z\\d]+@[a-zA-Z]+.[a-zA-Z]{2,3}";
                int i=0;
                if(password.isEmpty()||newEmail.isEmpty()||newEmail2.isEmpty()){
                    Toast.makeText((UpdateEmailPassword)getActivity(), "Enter All Fields", Toast.LENGTH_SHORT).show();
                }
                else if(!(Pattern.matches(regexSimpleEmail,newEmail))){
                    etNewEmail.setError("Invalid Email");
                }
                else if(!(newEmail.equals(newEmail2))){
                    etNewEmail2.setError("Email doesn't match");
                }
                else if(i==1){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder((UpdateEmailPassword)getActivity());
                    alertDialog.setTitle("Update Email").setMessage("Are You Sure ?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                    ProgressDialog progressDialog = new ProgressDialog((UpdateEmailPassword)getContext());
                    progressDialog.setTitle("Updating Email");
                    progressDialog.setCancelable(false);
                    progressDialog.setIcon(getResources().getDrawable(R.drawable.launcher_icon_logo));
                    progressDialog.show();

                    AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(email),password);
                    Objects.requireNonNull(firebaseAuth.getCurrentUser()).reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            firebaseAuth.getCurrentUser().updateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Map<String,Object> map = new HashMap<>();
                                    map.put("email",newEmail);
                                    FirebaseDatabase.getInstance().getReference().child("MyUsers/"+firebaseAuth.getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(), "Email Updated", Toast.LENGTH_SHORT).show();
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
                                    Log.v("Update Email ::::::",e.getMessage());
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
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }
                else{
                    Toast.makeText((UpdateEmailPassword)getActivity(), "You are not eligible to perform this operation", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return  view;
    }
}