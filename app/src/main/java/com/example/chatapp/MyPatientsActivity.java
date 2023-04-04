package com.example.chatapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapters.DoctorsAdapter;
import com.example.adapters.MyPatientsAdapter;
import com.example.adapters.Simp_myPatientsAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import models.Patient;
import models.UserModel;

public class MyPatientsActivity extends AppCompatActivity {

    private Simp_myPatientsAdapter simp_adapter;

    Context context = MyPatientsActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_patients);
        //setupPatientsList();
        setupPatientsListFromChat();
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

    /*
        private void setupPatientsList(){
            ArrayList<Patient> patientsList = new ArrayList<>();

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            String userId = firebaseAuth.getCurrentUser().getUid();

            FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference myPatientsRef = database.getReference("doctor").child(userId).child("MyPatients");

            myPatientsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot patientSnapshot : snapshot.getChildren()) {
                        Patient patient = patientSnapshot.getValue(Patient.class);
                        patientsList.add(patient);
                        Log.d(TAG, "onDataChange: " );
                    }
                    simp_adapter = new Simp_myPatientsAdapter(patientsList,getApplicationContext());
                    //ListMyPatients
                    RecyclerView recyclerView = findViewById(R.id.ListMyPatients);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(simp_adapter);
                    //simp_adapter.notifyDataSetChanged();
                    //pass patientsList to your adapter
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: " + error.getMessage());
                }
            });
        }
    */
    private void setupPatientsListFromChat() {
        ArrayList<UserModel> patientsList = new ArrayList<>();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String userId = firebaseAuth.getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myDoctorRef = database.getReference("Users");
/////////////////////////////////////////////////////////////////////////////////////////////

        myDoctorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                patientsList.clear();
                ArrayList<String> contactIds = new ArrayList<>();
                ArrayList<Long> recentMsgTimes = new ArrayList<>();
                ArrayList<String> recentMsg = new ArrayList<>();


                if (snapshot.child(userId).hasChild("Contacts"))
                    for (DataSnapshot e : snapshot.child(firebaseAuth.getUid()).child("Contacts").getChildren()) {
                        contactIds.add(e.getKey());
/*


                        if(e.hasChild("interactionTime")) {
                            recentMsgTimes.add((long)e.child("interactionTime").getValue());
                        }

                        if(e.hasChild("recentMessage")){
                            recentMsg.add(e.child("recentMessage").getValue().toString());
                        }

*/

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
                    patientsList.add(model);
                    }
                }
                simp_adapter = new Simp_myPatientsAdapter(patientsList, getApplicationContext());
                RecyclerView recyclerView = findViewById(R.id.ListMyPatients);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(simp_adapter);
                simp_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

/////////////////////////////////////////////////////////////////////////////////////////////
    }
/*
    private void setupPatientsListFromChat() {
        ArrayList<UserModel> patientsList = new ArrayList<>();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String userId = firebaseAuth.getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myDoctorRef = database.getReference("Users");
        myDoctorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                patientsList.clear();
                ArrayList<String> contactIds = new ArrayList<>();
                ArrayList<Long> recentMsgTimes = new ArrayList<>();
                ArrayList<String> recentMsg = new ArrayList<>();


                if (snapshot.child(userId).hasChild("Contacts"))
                    for (DataSnapshot e : snapshot.child(firebaseAuth.getUid()).child("Contacts").getChildren()) {
                        contactIds.add(e.getKey());
                    }

                for (int i = 0; i < contactIds.size(); i++) {

////////////////////////////////////////////////////////////////////////////////////////////////////
                    DatabaseReference myDoctorChatRef = database.getReference("Users").child(contactIds.get(i));

                    myDoctorChatRef.addValueEventListener(new ValueEventListener() {
                                                              @Override
                                                              public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                  for (DataSnapshot ds : snapshot.getChildren()) {
                                                                      if (ds.child("userType").getValue().toString().equals("doctor")) {
                                                                          patientsList.add(new UserModel(
                                                                                  ds.child("userName").getValue().toString(),
                                                                                  ds.child("address").getValue().toString(),
                                                                                  ds.child("tel").getValue().toString(),
                                                                                  ds.child("userMail").getValue().toString(),
                                                                                  ds.child("speciality").getValue().toString(),
                                                                                  ds.child("uid").getValue().toString()
                                                                          ));

                                                                      }

                                                                      Log.d(TAG, "onDataChange: ");
                                                                  }
                                                              }

                                                              @Override
                                                              public void onCancelled(@NonNull DatabaseError error) {
                                                                  Log.d(TAG, "onCancelled: " + error.getMessage());
                                                              }
                                                          });
////////////////////////////////////////////////////////////////////////////////////////////////////


                }
                simp_adapter = new Simp_myPatientsAdapter(patientsList, getApplicationContext());
                RecyclerView recyclerView = findViewById(R.id.ListMyPatients);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(simp_adapter);
                simp_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
*/
}

