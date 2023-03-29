package com.example.chatapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.Common.Common;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class Doc_HomeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    static String doc;
    LinearLayout doc_home_ui_container;
    ConstraintLayout doc_home_main_container;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Button SignOutBtn2, BtnRequst, listPatients,appointementBtn, profile, myCalendarBtn, myChatBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_home); //ici layout de page d'acceuil MEDECIN

        Common.CurreentDoctor = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        Common.CurrentUserType = "doctor";
        fragmentManager = getSupportFragmentManager();

        doc_home_main_container = findViewById(R.id.doc_home_main_container);
        doc_home_ui_container = findViewById(R.id.doc_home_ui_container);
        listPatients = findViewById(R.id.listPatients);
        profile = findViewById(R.id.profile);
        myCalendarBtn = findViewById(R.id.myCalendarBtn);
        BtnRequst=findViewById(R.id.btnRequst);
        SignOutBtn2=findViewById(R.id.signOutBtn);
        myChatBtn = findViewById(R.id.myChatBtn);
        appointementBtn = findViewById(R.id.appointement);
        SignOutBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        myCalendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*  Toast.makeText(this,"clicked",Toast.LENGTH_SHORT).show();
        Intent k = new Intent(Doc_HomeActivity.this, MyCalendarDoctorActivity.class);
        startActivity(k);*/
                doc_home_ui_container.setVisibility(View.GONE);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.doc_home_main_container, Doc_Calendar_Fragment.class, null)
                        .addToBackStack(null)
                        .commit();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 /*   Intent k = new Intent(Doc_HomeActivity.this, ProfileDoctorActivity.class);
        startActivity(k);
*/
                doc_home_ui_container.setVisibility(View.GONE);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.doc_home_main_container, ProfileFragment.class, null)
                        .addToBackStack(null)
                        .commit();
            }
        });
        BtnRequst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent k = new Intent(Doc_HomeActivity.this, ConfirmedAppointmensActivity.class);
                startActivity(k);*/
                doc_home_ui_container.setVisibility(View.GONE);

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.doc_home_main_container, Doc_Patient_request_Fragment.class, null)
                        .addToBackStack(null)
                        .commit();
            }
        });
        listPatients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(Doc_HomeActivity.this, MyPatientsActivity.class);
                startActivity(k);
              /*  doc_home_ui_container.setVisibility(View.GONE);

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.doc_home_main_container, Doc_MyPatient_Fragment.class, null)
                        .addToBackStack(null)
                        .commit();*/
            }
        });
        appointementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // doc = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
                //showDatePickerDialog(v.getContext());
                Intent k = new Intent(Doc_HomeActivity.this, DoctorAppointementActivity.class);
                startActivity(k);
              /*  doc_home_ui_container.setVisibility(View.GONE);

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.doc_home_main_container, Doc_Appointments_Fragment.class, null)
                        .addToBackStack(null)
                        .commit();*/
            }
        });
        myChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent k = new Intent(Doc_HomeActivity.this, MainActivity.class);
                startActivity(k);
               /* doc_home_ui_container.setVisibility(View.GONE);

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.doc_home_main_container, Doc_Appointments_Fragment.class, null)
                        .addToBackStack(null)
                        .commit();*/
            }
        });

    }

    public void showDatePickerDialog(Context wf){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                wf,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = "month_day_year: " + month + "_" + dayOfMonth + "_" + year;
        openPage(view.getContext(),doc,date);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentManager fm = getSupportFragmentManager();
       // if (fm.getBackStackEntryCount() > 0) {
        if (fm!=null){
            fm.popBackStack();
           // doc_home_main_container.setVisibility(View.VISIBLE);
            doc_home_ui_container.setVisibility(View.VISIBLE);
        } else
            super.onBackPressed();

    }

    private void openPage(Context wf, String d, String day){
       // Intent i = new Intent(wf, AppointementActivity.class);
        Intent i = new Intent(wf, MainActivity.class);
        i.putExtra("key1",d+"");
        i.putExtra("key2",day);
        i.putExtra("key3","doctor");
        wf.startActivity(i);
    }
}
