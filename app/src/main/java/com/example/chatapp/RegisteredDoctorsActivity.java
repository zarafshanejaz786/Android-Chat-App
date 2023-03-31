package com.example.chatapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapters.DoctorsAdapter;
import com.example.model.Doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import models.Patient;

public class RegisteredDoctorsActivity extends AppCompatActivity {

    private DoctorsAdapter simp_adapter;

    Context context =  RegisteredDoctorsActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_doctors);
        setupPatientsList();
    }

    @Override
    protected void onStart() {
        super.onStart();
       // adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }
    private void setupPatientsList(){
        ArrayList<Doctor> patientsList = new ArrayList<>();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String userId = firebaseAuth.getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myPatientsRef = database.getReference("doctor");

        myPatientsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {


                    /* Doctor doctor = ds.getValue(Doctor.class);
                    patientsList.add(doctor);*/

                    patientsList.add(new Doctor(
                            ds.child("name").getValue().toString(),
                            ds.child("address").getValue().toString(),
                            ds.child("tel").getValue().toString(),
                            ds.child("email").getValue().toString(),
                            ds.child("speciality").getValue().toString(),
                            ds.child("uid").getValue().toString()
                    ));
                    Log.d(TAG, "onDataChange: " );
                }
                simp_adapter = new DoctorsAdapter(patientsList,getApplicationContext());
                //ListMyPatients
                RecyclerView recyclerView = findViewById(R.id.ListRegisteredDoctors);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(simp_adapter);
                //pass patientsList to your adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }
}
