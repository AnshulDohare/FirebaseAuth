package com.example.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class DeleteUserPage extends AppCompatActivity {
    TextInputEditText etEmail,etPassword;
    Button btnDelete,btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user_page);

        etEmail = findViewById(R.id.emailEditTextDeleteUserPage);
        etPassword =findViewById(R.id.passwordEditTextDeleteUserPage);
        btnDelete  = findViewById(R.id.deleteButtonDeleteUserPage);
        btnBack =findViewById(R.id.backDeleteUserPage);
        btnBack.setVisibility(View.GONE);
        etEmail.setEnabled(false);
        etEmail.setFocusable(false);
        etEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        btnDelete.setOnClickListener(v->{
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String password = etPassword.getText().toString().trim();

            if(password.isEmpty()){
                etPassword.setError("Enter Password");
            }
            else{
                AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);
                FirebaseAuth.getInstance().getCurrentUser().reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(DeleteUserPage.this);
                        builder1.setTitle("Delete Account");
                        builder1.setMessage("Are You Sure ?");
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DeleteMyUser.deleteMyUser(DeleteUserPage.this);
                                btnDelete.setVisibility(View.GONE);
                                btnDelete.setEnabled(false);
                                btnBack.setVisibility(View.VISIBLE);
                            }
                        });
                        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder1.show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DeleteUserPage.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnBack.setOnClickListener(v->{
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                startActivity(new Intent(DeleteUserPage.this, SignInPage.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(DeleteUserPage.this, SignInPage.class));
            finish();
        }
    }
}