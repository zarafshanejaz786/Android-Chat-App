package com.example.chatapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class MyPatientsActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference myPatientsRef = db.collection("doctor");
    private MyPatientsAdapter adapter;
    private Simp_myPatientsAdapter simp_adapter;

    Context context =  MyPatientsActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_patients);
        setUpRecyclerView();

    }

    public void setUpRecyclerView(){

       /* final String doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        Query query = myPatientsRef.document(""+doctorID+"")
                .collection("MyPatients").orderBy("name", Query.Direction.DESCENDING);
*/
      /*  final String doctorID = FirebaseAuth.getInstance().getUid().toString();
        Query query = myPatientsRef.document(""+doctorID+"")
                .collection("MyPatients").orderBy("name", Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<Patient> options = new FirestoreRecyclerOptions.Builder<Patient>()
                .setQuery(query, Patient.class)
                .build();
       // testIt();
        adapter = new MyPatientsAdapter(options);
        //ListMyPatients
        RecyclerView recyclerView = findViewById(R.id.ListMyPatients);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);*/

        testIt();

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
    private void fetchData(){
        FirebaseAuth   firebaseAuth = FirebaseAuth.getInstance();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference myPatientsRef = db.collection("doctor").document(firebaseAuth.getUid()).collection("MyPatients");

        Query query = myPatientsRef.orderBy("name", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Patient> options = new FirestoreRecyclerOptions.Builder<Patient>()
                .setQuery(query, Patient.class)
                .build();

        // adapter = new MyPatientsAdapter(options);

    }
    private void testIt(){
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
                //pass patientsList to your adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }
}
