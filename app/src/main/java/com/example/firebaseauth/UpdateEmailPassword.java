package com.example.firebaseauth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.animation.LayoutTransition;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class UpdateEmailPassword extends AppCompatActivity {
     Button btnUpdateEmailPage,btnUpdatePasswordPage;
     LinearLayout layout;
     FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email_password);
        btnUpdateEmailPage = findViewById(R.id.updateEmailButton);
        btnUpdatePasswordPage = findViewById(R.id.updatePasswordButton);
        layout  = findViewById(R.id.layoutUpdateEmailPassword);
        replaceFragment(new UpdatePassword());

        /**fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        UpdatePassword updatePassword = new UpdatePassword();
        //fragmentTransaction.add(R.id.layoutUpdateEmailPassword,updatePassword);
        fragmentTransaction.replace(layout.getId(), updatePassword);
        fragmentTransaction.commit();
         */

        btnUpdateEmailPage.setTextColor(getResources().getColor(R.color.black));
        btnUpdateEmailPage.setTextSize(10);
        btnUpdatePasswordPage.setTextColor(getResources().getColor(R.color.white));
        btnUpdatePasswordPage.setTextSize(16);

        btnUpdatePasswordPage.setOnClickListener(v->{
            btnUpdateEmailPage.setTextColor(getResources().getColor(R.color.black));
            btnUpdateEmailPage.setTextSize(10);
            btnUpdatePasswordPage.setTextColor(getResources().getColor(R.color.white));
            btnUpdatePasswordPage.setTextSize(16);
            replaceFragment(new UpdatePassword());

        });

        btnUpdateEmailPage.setOnClickListener(v->{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Warning").setMessage("Carefully Enter Your New Password").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    btnUpdateEmailPage.setTextColor(getResources().getColor(R.color.white));
                    btnUpdateEmailPage.setTextSize(16);
                    btnUpdatePasswordPage.setTextColor(getResources().getColor(R.color.black));
                    btnUpdatePasswordPage.setTextSize(10);
                    replaceFragment(new UpdateEmail());
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();


        });

    }

    private void replaceFragment(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(layout.getId(),fragment);
        fragmentTransaction.commit();
    }

}