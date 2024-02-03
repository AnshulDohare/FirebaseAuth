package com.example.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignInPage extends AppCompatActivity {
    TextInputEditText emailEditText, passwordEditText;
    Button signInButton;
    TextView signUpTextView,forgetPasswordTextView;

    FirebaseAuth auth ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_page);

        emailEditText = findViewById(R.id.emailEditTextSignIn);
        passwordEditText = findViewById(R.id.passwordEditTextSignIn);
        signInButton = findViewById(R.id.signInButtonSignIn);
        signUpTextView =findViewById(R.id.signUpTextViewSignIn);
        forgetPasswordTextView = findViewById(R.id.forgetPasswordTextViewSignIn);
        auth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(v->{
            String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
            String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();
            String regexSimpleEmail = "[a-zA-Z_]+\\d*[.]?[a-zA-Z_\\d]*[.]?[a-zA-Z\\d]+@[a-zA-Z]+.[a-zA-Z]{2,3}";
            if(email.isEmpty()&&password.isEmpty()){
                emailEditText.setError("Enter Email");
                passwordEditText.setError("Enter Password");
            }
            else if(email.isEmpty()){
                emailEditText.setError("Enter Email");
            }
            else if(password.isEmpty()){
                passwordEditText.setError("Enter Password");
            }
            else if(!(Pattern.matches(regexSimpleEmail,email))){
                emailEditText.setError("Wrong Format of Email");
            }
            else{
                userSingIn(email,password);
            }
            //Toast.makeText(this, email+password, Toast.LENGTH_SHORT).show();
        });

        signUpTextView.setOnClickListener(v->startActivity(new Intent(this, SignUpPage.class)));

        forgetPasswordTextView.setOnClickListener(v-> Toast.makeText(this, "Forget Password", Toast.LENGTH_SHORT).show());
    }

    public  void userSingIn(String email,String password){



        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if(auth.getCurrentUser().isEmailVerified()){
                    Toast.makeText(SignInPage.this,"Success: "+ authResult.toString(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignInPage.this, Dashboard.class));
                    finish();
                }
                else{
                    auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(SignInPage.this, "Verification Link Sent On Your Email", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignInPage.this, EmailVerification.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            startActivity(new Intent(SignInPage.this, EmailVerification.class));
                            finish();
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignInPage.this,"Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
            startActivity(new Intent(SignInPage.this, Dashboard.class));
            finish();
        }
    }
}