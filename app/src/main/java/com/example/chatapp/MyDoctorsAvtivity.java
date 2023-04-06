package com.example.chatapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapters.Simp_myDoctorsAdapter;
import com.example.adapters.Simp_myPatientsAdapter;
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

import models.UserModel;

public class MyDoctorsAvtivity extends AppCompatActivity {

     private Simp_myDoctorsAdapter simp_adapter;

    Context context =  MyDoctorsAvtivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_doctors);

        setUpDoctorsListFromChat();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

/*
    private void setUpDoctorsList(){
        ArrayList<Doctor> doctorsList = new ArrayList<>();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String userId = firebaseAuth.getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //DatabaseReference myDoctorRef = database.getReference("patients").child(userId).child("MyDooctors");
        DatabaseReference myDoctorRef = database.getReference("patient").child(userId).child("MyDoctors");
        //DatabaseReference myDoctorRef = database.getReference("doctor");

        myDoctorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Doctor doctor = ds.getValue(Doctor.class);
                    doctorsList.add(doctor);
                    Log.d(TAG, "onDataChange: " );
                }
                simp_adapter = new Simp_myDoctorsAdapter(doctorsList,getApplicationContext());
                //ListMyPatients
                RecyclerView recyclerView = findViewById(R.id.ListMyDoctors);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(simp_adapter);
                //pass doctorsList to your adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }
*/
    private void setUpDoctorsListFromChat(){
        ArrayList<UserModel> doctorsList = new ArrayList<>();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String userId = firebaseAuth.getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myDoctorRef = database.getReference("Users");
        myDoctorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                ///////////////////////////////////////////////////////////////////////////////////
                doctorsList.clear();
                ArrayList<String> contactIds = new ArrayList<>();

                if (snapshot.child(userId).hasChild("Contacts"))
                    for (DataSnapshot e : snapshot.child(firebaseAuth.getUid()).child("Contacts").getChildren()) {
                        contactIds.add(e.getKey());

                    }
                for (int i = 0; i < contactIds.size(); i++) {
                    String e = contactIds.get(i);

                    if (snapshot.hasChild(e)){


                        String uName = snapshot.child(e).child("userName").getValue().toString();
                        String uMail = snapshot.child(e).child("userMail").getValue().toString();
                        String uPic = snapshot.child(e).child("profilePic").getValue().toString();
                        String tel = snapshot.child(e).child("tel").getValue().toString();
                        String token = snapshot.child(e).child("token").getValue().toString();
                        String pat_uid = snapshot.child(e).child("uid").getValue().toString();

                        UserModel model = new UserModel(uName, uMail, uPic, tel, token, pat_uid);
                        doctorsList.add(model);
                    }
                }
                simp_adapter = new Simp_myDoctorsAdapter(doctorsList,getApplicationContext());
                //ListMyPatients
                RecyclerView recyclerView = findViewById(R.id.ListMyDoctors);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(simp_adapter);
                simp_adapter.notifyDataSetChanged();
                ///////////////////////////////////////////////////////////////////////////////////

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }
/*
    private void setUpDoctorsList(){
        ArrayList<Doctor> doctorsList = new ArrayList<>();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String userId = firebaseAuth.getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myDoctorRef = database.getReference("patients").child(userId).child("MyDooctors");

        myDoctorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                   */
/* Doctor doctor = ds.getValue(Doctor.class);
                    doctorsList.add(doctor);*//*

                    doctorsList.add(new Doctor(
                            ds.child("name").getValue().toString(),
                            ds.child("address").getValue().toString(),
                            ds.child("tel").getValue().toString(),
                            ds.child("email").getValue().toString(),
                            ds.child("speciality").getValue().toString(),
                            ds.child("uid").getValue().toString()
                    ));

                    Log.d(TAG, "onDataChange: " );
                }
                simp_adapter = new Simp_myDoctorsAdapter(doctorsList,getApplicationContext());
                //ListMyPatients
                RecyclerView recyclerView = findViewById(R.id.ListMyDoctors);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(simp_adapter);
                //pass doctorsList to your adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }
*/

}
