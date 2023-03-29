package com.example.chatapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adapters.DoctorAppointementAdapter;
import com.example.model.ApointementInformation;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Doc_Appointments_Fragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference doctorAppointementRef = db.collection("Doctor");
    private DoctorAppointementAdapter adapter;
    private View view;
    RecyclerView recyclerView;

    public Doc_Appointments_Fragment() {
        // Required empty public constructor
    }


    public static Doc_Appointments_Fragment newInstance(String param1, String param2) {
        Doc_Appointments_Fragment fragment = new Doc_Appointments_Fragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_doc__appointments_, container, false);
        recyclerView = view.findViewById(R.id.DoctorAppointement);
        setUpRecyclerView();
        return view;
    }

    public void setUpRecyclerView() {
        //Get the doctors by patient id
        final String doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        Query query = doctorAppointementRef.document("" + doctorID + "")
                .collection("apointementrequest")
                .orderBy("time", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ApointementInformation> options = new FirestoreRecyclerOptions.Builder<ApointementInformation>()
                .setQuery(query, ApointementInformation.class)
                .build();


            adapter = new DoctorAppointementAdapter(options);
            //ListMyDoctors
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);


    }

}