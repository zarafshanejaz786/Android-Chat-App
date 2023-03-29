package com.example.chatapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;

import com.example.Common.Common;
import com.example.Interface.ITimeSlotLoadListener;
import com.example.adapters.MyTimeSlotAdapter;
import com.example.model.TimeSlot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class Doc_Calendar_Fragment extends Fragment implements ITimeSlotLoadListener {


    DocumentReference doctorDoc;

    ITimeSlotLoadListener iTimeSlotLoadListener;
    android.app.AlertDialog alertDialog;
    @BindView(R.id.recycle_time_slot2)
    RecyclerView recycler_time_slot;
    @BindView(R.id.calendarView2)
    HorizontalCalendarView calendarView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");

    public Doc_Calendar_Fragment() {
        // Required empty public constructor
    }


    public static Doc_Calendar_Fragment newInstance(String param1, String param2) {
        Doc_Calendar_Fragment fragment = new Doc_Calendar_Fragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(requireActivity());
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doc__calendar_, container, false);
    }


    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(getContext(),timeSlotList);
        recycler_time_slot.setAdapter(adapter);
        alertDialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
        alertDialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(getContext());
        recycler_time_slot.setAdapter(adapter);
        alertDialog.dismiss();
    }
    private void init() {
        iTimeSlotLoadListener = this;
        alertDialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext())
                .build();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE,0);
        loadAvailabelTimeSlotOfDoctor(Common.CurreentDoctor,simpleDateFormat.format(date.getTime()));
    try {
        recycler_time_slot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        recycler_time_slot.setLayoutManager(gridLayoutManager);
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE,0);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE,5);
        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(requireActivity(),R.id.calendarView2)
                .range(startDate,endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if(Common.currentDate.getTimeInMillis() != date.getTimeInMillis()){
                    Common.currentDate = date;
                    loadAvailabelTimeSlotOfDoctor(Common.CurreentDoctor,simpleDateFormat.format(date.getTime()));

                }
            }
        });
    }catch (Exception e){
        e.printStackTrace();
        }



    }
    private void loadAvailabelTimeSlotOfDoctor(String curreentDoctor, String bookDate) {
        alertDialog.show();

        doctorDoc = FirebaseFirestore.getInstance()
                .collection("Doctor")
                .document(Common.CurreentDoctor);
        doctorDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        CollectionReference date =FirebaseFirestore.getInstance()
                                .collection("Doctor")
                                .document(Common.CurreentDoctor)
                                .collection(bookDate);

                        date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot.isEmpty())
                                    {
                                        iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                                    }else {
                                        List<TimeSlot> timeSlots = new ArrayList<>();
                                        for (QueryDocumentSnapshot document:task.getResult())
                                            timeSlots.add(document.toObject(TimeSlot.class));
                                        iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlots);
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage());
                            }
                        });
                    }
                }
            }
        });
    }


}