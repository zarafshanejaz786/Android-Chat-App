package com.example.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.AESUtils;
import com.example.chatapp.DossierMedical;
import com.example.chatapp.MessagingActivity;
import com.example.chatapp.R;

import models.MessageModel;
import models.Patient;

import com.example.model.Doctor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;


public class Simp_myPatientsAdapter extends RecyclerView.Adapter<Simp_myPatientsAdapter.ViewHolder> {

    StorageReference pathReference ;

    private final ArrayList<Patient> patientsList;
    Context context;
    private static OnClickListener listener;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public Simp_myPatientsAdapter(ArrayList<Patient> patientsList, Context context) {
        this.patientsList = patientsList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_patient_item, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder myPatientsHolder, int position) {

        myPatientsHolder.textViewTitle.setText(patientsList.get(position).getName());
        myPatientsHolder.textViewTelephone.setText("Tél : "+patientsList.get(position).getTel());
        myPatientsHolder.contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openPage(v.getContext(),patientsList.get(position).getTel());
                movToChat(position);
                //addUsertoChat(patientsList.get(position).getUid(), position);
            }
        });

//        myPatientsHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openPatientMedicalFolder(v.getContext(),patient, position);
//
//            }
//        });
        myPatientsHolder.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPage(myPatientsHolder.contactButton.getContext(),patientsList.get(position).getTel());
            }
        });

        String imageId = patientsList.get(position).getEmail()+".jpg"; //add a title image
        pathReference = FirebaseStorage.getInstance().getReference().child("DoctorProfile/"+ imageId); //storage the image
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()
                        .into(myPatientsHolder.imageViewPatient);//Image location

                // profileImage.setImageURI(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


    }

    private void openPage(Context wf, String phoneNumber){
      /*  Intent i = new Intent(wf, ChatActivity.class);
        i.putExtra("key1",p.getEmail()+"_"+ FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
        i.putExtra("key2",FirebaseAuth.getInstance().getCurrentUser().getEmail().toString()+"_"+p.getEmail());
        wf.startActivity(i);*/
       /* Intent i = new Intent(wf, MainActivity.class);
        wf.startActivity(i);*/
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        wf.startActivity(intent);
    }
    private void openPatientMedicalFolder(Context medicalFolder, Patient patient, int pos){
        Intent intent = new Intent(medicalFolder, DossierMedical.class);
        intent.putExtra("patient_name", patientsList.get(pos).getName());
        intent.putExtra("patient_email",patientsList.get(pos).getEmail());
        intent.putExtra("patient_phone", (CharSequence) patientsList.get(pos).getTel());
        medicalFolder.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return patientsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Button callBtn;
        TextView textViewTitle;
        TextView textViewTelephone;
        ImageView imageViewPatient;
        Button contactButton;
        LinearLayout parentLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            callBtn = itemView.findViewById(R.id.callBtn);
            textViewTitle = itemView.findViewById(R.id.patient_view_title);
            textViewTelephone = itemView.findViewById(R.id.text_view_telephone);
            imageViewPatient = itemView.findViewById(R.id.patient_item_image);
            contactButton = itemView.findViewById(R.id.contact);
            parentLayout = itemView.findViewById(R.id.parent_layout);

/*
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();

                    if (listener != null && pos != RecyclerView.NO_POSITION) {
                        listener.onItemClick(patientsList.get(pos));
                    }
                }
            });
*/
        }
    }

    public interface OnClickListener {
        void onItemClick(Patient userdata);
    }

    public void setOnItemClickListener(OnClickListener listener) {
        Simp_myPatientsAdapter.listener = listener;
    }

    private void movToChat(int pos ){

        String uName = patientsList.get(pos).getName();
        String uMail = patientsList.get(pos).getEmail();
        String uPic = patientsList.get(pos).getProfilePic();
        String token = patientsList.get(pos).getToken();
        String pat_uid = patientsList.get(pos).getUid();

        Intent intent = new Intent(context, MessagingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("USERNAME", uName);
        intent.putExtra("PROFILEIMAGE", uPic);
        intent.putExtra("USERID", pat_uid);
        intent.putExtra("TOKEN", token);
        context.startActivity(intent);
    }
}
