package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.Common.Common;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Pat_HomeActivity extends AppCompatActivity {
    Button SignOutBtn;
    Button searchPatBtn;
    Button myDoctors;
    Button BtnRequst;
    Button myChatBtn;
    Button profile;
    ConstraintLayout main_container,ui_container;
    Button appointment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pat_home);
        fragmentManager = getSupportFragmentManager();

        appointment = findViewById(R.id.appointement_p);
        SignOutBtn=findViewById(R.id.signOutBtn);
        myDoctors = (Button)findViewById(R.id.myDoctors);
      //  BtnRequst = findViewById(R.id.btnRequst);
        profile = findViewById(R.id.btn_profile);
        ui_container = findViewById(R.id.ui_container);
        main_container = findViewById(R.id.main_container);
        searchPatBtn = (Button)findViewById(R.id.searchBtn);
        myChatBtn = (Button)findViewById(R.id.myChatBtn);

        appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent k = new Intent(Pat_HomeActivity.this, PatientAppointementsActivity.class);
                startActivity(k);
            }
        });
        searchPatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent k = new Intent(Pat_HomeActivity.this, SearchPatActivity.class);
                Intent k = new Intent(Pat_HomeActivity.this, RegisteredDoctorsActivity.class);
                startActivity(k);
            }
        });

        myChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent k = new Intent(Pat_HomeActivity.this, MainActivity.class);
                startActivity(k);
               /* doc_home_ui_container.setVisibility(View.GONE);

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.doc_home_main_container, Doc_Appointments_Fragment.class, null)
                        .addToBackStack(null)
                        .commit();*/
            }
        });


        SignOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        myDoctors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(Pat_HomeActivity.this, MyDoctorsAvtivity.class);
                startActivity(k);
            }
        });

/*
        BtnRequst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DossierMedical.class);
                intent.putExtra("patient_email",FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
                startActivity(intent);
            }
        });
*/


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent k = new Intent(Pat_HomeActivity.this, ProfilePatientActivity.class);
                ui_container.setVisibility(View.GONE);

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container,ProfileFragment.class,null)
                        .addToBackStack(null)
                        .commit();

              /*  Intent k = new Intent(Pat_HomeActivity.this, ProfileFragment.class);

                startActivity(k);*/
            }
        });

        Common.CurrentUserid= FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        FirebaseFirestore.getInstance().collection("User").document(Common.CurrentUserid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Common.CurrentUserName = documentSnapshot.getString("name");
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentManager fm = getSupportFragmentManager();
        // if (fm.getBackStackEntryCount() > 0) {
        if (fm!=null){
            fm.popBackStack();
            // doc_home_main_container.setVisibility(View.VISIBLE);
            ui_container.setVisibility(View.VISIBLE);
        } else
            super.onBackPressed();

    }

}
