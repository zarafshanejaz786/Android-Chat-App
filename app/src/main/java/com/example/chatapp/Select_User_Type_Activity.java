package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chatapp.databinding.ActivitySelectUserTypeBinding;

public class Select_User_Type_Activity extends AppCompatActivity {
    ConstraintLayout user_type_main_container, user_type_container;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Button tv_doc, tv_patient;

    // ActivitySelectUserTypeBinding activitySelectUserTypeBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_type);
        fragmentManager = getSupportFragmentManager();

        user_type_container = findViewById(R.id.user_type_container);
        user_type_main_container = findViewById(R.id.user_type_main_container);
        tv_doc = findViewById(R.id.tv_doctor);
        tv_patient = findViewById(R.id.tv_patient);

        tv_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* user_type_container.setVisibility(View.GONE);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.user_type_main_container, Doc_Home_Fragment.class, null)
                        .addToBackStack(null)
                        .commit();*/
                Intent k = new Intent(Select_User_Type_Activity.this, Doc_HomeActivity.class);
                startActivity(k);
                finish();

            }
        });
        tv_patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Pat_HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });
    }


}