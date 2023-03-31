package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    Button btn_view_your_record;
    Button profile;
    ConstraintLayout main_container,ui_container;
    Button btn_order_lab;
    Button btn_order_medication;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pat_home);
        fragmentManager = getSupportFragmentManager();

        btn_order_lab = findViewById(R.id.btn_order_lab);
        btn_order_lab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent k = new Intent(Pat_HomeActivity.this, PatientAppointementsActivity.class);
//                startActivity(k);
                Toast.makeText(Pat_HomeActivity.this,"Order Labs",Toast.LENGTH_LONG).show();
            }
        });
        btn_order_medication = findViewById(R.id.btn_order_medication);
        btn_order_medication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent k = new Intent(Pat_HomeActivity.this, PatientAppointementsActivity.class);
//                startActivity(k);
                Toast.makeText(Pat_HomeActivity.this,"Order Medications",Toast.LENGTH_LONG).show();
            }
        });

        searchPatBtn = (Button)findViewById(R.id.searchBtn);
        searchPatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent k = new Intent(Pat_HomeActivity.this, SearchPatActivity.class);
                Intent k = new Intent(Pat_HomeActivity.this, RegisteredDoctorsActivity.class);
                startActivity(k);
            }
        });
        SignOutBtn=findViewById(R.id.signOutBtn);
        SignOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                Pat_HomeActivity.this.finish();
            }
        });

        myDoctors = (Button)findViewById(R.id.myDoctors);
        myDoctors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(Pat_HomeActivity.this, MyDoctorsAvtivity.class);
                startActivity(k);
            }
        });
        btn_view_your_record = findViewById(R.id.btn_view_your_record);
        btn_view_your_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), DossierMedical.class);
//                intent.putExtra("patient_email",FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
//                startActivity(intent);
                Toast.makeText(Pat_HomeActivity.this,"Your Records",Toast.LENGTH_LONG).show();
            }
        });

        profile = findViewById(R.id.btn_profile);
        ui_container = findViewById(R.id.ui_container);
        main_container = findViewById(R.id.main_container);
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
