package com.example.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.AESUtils;
import com.example.chatapp.DossierMedical;
import com.example.chatapp.MainActivity;
import com.example.chatapp.MessagingActivity;
import com.example.chatapp.R;
import com.example.chatapp.SignupActivity;
import com.example.model.Doctor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import models.MessageModel;
import models.Patient;
import models.UserModel;


public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.ViewHolder> {

    Patient patient;
    StorageReference pathReference ;

    private final ArrayList<UserModel> doctorsList;
    Context context;
    private static OnClickListener listener;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public DoctorsAdapter(ArrayList<UserModel> doctorsList, Context context) {
        this.doctorsList = doctorsList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doctor_item , parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder myDoctorsHolder, int position) {

        myDoctorsHolder.doc_name.setText(doctorsList.get(position).getUserName());
        myDoctorsHolder.doc_description.setText("Speciality : "+doctorsList.get(position).getSpeciality());
/*
        myDoctorsHolder.sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPage(v.getContext(),doctorsList.get(position));
            }
        });
*/
/*
        myDoctorsHolder.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPage(myDoctorsHolder.sendMessageButton.getContext(),doctorsList.get(position).getTel());
            }
        });
*/
        myDoctorsHolder.addDocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openPage(myDoctorsHolder.sendMessageButton.getContext(),doctorsList.get(position).getTel());
                //addDocTo_MyDocList( position);
                uploadMsg_To_Rt_DB(position);
            }
        });
//
        /*String imageId = doctorsList.get(position).getEmail()+".jpg"; //add a title image
       pathReference = FirebaseStorage.getInstance().getReference().child("DoctorProfile/"+ imageId); //storage the image
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()
                        .into(myDoctorsHolder.imageViewDoctor);//Image location

                // profileImage.setImageURI(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        }
        );*/

    }
    private void addDocTo_MyDocList(int pos){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myPatientsRef = database.getReference("doctor");

        String userId = firebaseAuth.getCurrentUser().getUid();

        Doctor doctor = new Doctor(doctorsList.get(pos).getUserName(),doctorsList.get(pos).getAddress(),doctorsList.get(pos).getTel(),doctorsList.get(pos).getUserMail(),doctorsList.get(pos).getSpeciality(),doctorsList.get(pos).getUid());
        database.getReference().child("patient")
                .child(userId)
                .child("MyDoctors")
                .child(doctorsList.get(pos).getUid())
                .setValue(doctor);

        Toast.makeText(this.context, "Added to your doctors",
                Toast.LENGTH_SHORT).show();

    }
    private void openPage(Context wf, String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        wf.startActivity(intent);
    }

    private void openPage(Context wf, Doctor d){
        //  Intent i = new Intent(wf, ChatActivity.class);
        Intent i = new Intent(wf, MainActivity.class);
       /* i.putExtra("key1",d.getEmail()+"_"+ FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
        i.putExtra("key2",FirebaseAuth.getInstance().getCurrentUser().getEmail().toString()+"_"+d.getEmail());*/
        wf.startActivity(i);
    }
    private void openPatientMedicalFolder(Context medicalFolder, Patient patient, int pos){
        Intent intent = new Intent(medicalFolder, DossierMedical.class);
        intent.putExtra("patient_name", doctorsList.get(pos).getUserName());
        intent.putExtra("patient_email",doctorsList.get(pos).getUserMail());
        intent.putExtra("patient_phone", (CharSequence) doctorsList.get(pos).getTel());
        medicalFolder.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return doctorsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView doc_name;
        TextView doc_description;
        TextView textViewStatus;
        ImageView imageViewDoctor;
        Button sendMessageButton;
        Button addDocBtn, bookAppointmentBtn;
        Button contactButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            doc_name = itemView.findViewById(R.id.doc_name);
            doc_description = itemView.findViewById(R.id.doc_description);
            //textViewStatus = itemView.findViewById(R.id.onlineStatut);
           // imageViewDoctor = itemView.findViewById(R.id.di_doctor_item_image);
           // sendMessageButton = itemView.findViewById(R.id.voir_fiche_btn);
            addDocBtn = itemView.findViewById(R.id.addDocBtn);
            bookAppointmentBtn = itemView.findViewById(R.id.bookAppointmentBtn);
           // contactButton = itemView.findViewById(R.id.contact);

/*
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();

                    if (listener != null && pos != RecyclerView.NO_POSITION) {
                        listener.onItemClick(doctorsList.get(pos));
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
        DoctorsAdapter.listener = listener;
    }
    private void uploadMsg_To_Rt_DB(int pos) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        String senderId;
        String receiverId;
        receiverId = doctorsList.get(pos).getUid();
        senderId = firebaseAuth.getUid();
        String msg = "Hi Doctor";
        String encryptedMsg = msg;
        try {
            encryptedMsg = AESUtils.encrypt(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long date = new Date().getTime();
        MessageModel messageModel = new MessageModel();

        messageModel = new MessageModel(senderId, encryptedMsg, date, "null");
        // txtMsg = encryptedMsg;
        //  if (!encryptedMsg.isEmpty()) {
        MessageModel finalMessageModel = messageModel;
        firebaseDatabase.getReference("Users").child(senderId).child("Contacts")
                .child(receiverId).child("Chats").push()
                .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //////////////////////////////////

                        String uName = doctorsList.get(pos).getUserName();
                        String uMail = doctorsList.get(pos).getUserMail();
                        String uPic = doctorsList.get(pos).getProfilePic();
                        String token = doctorsList.get(pos).getToken();
                        String doc_uid = doctorsList.get(pos).getUid();


                        Intent intent = new Intent(context, MessagingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("USERNAME", uName);
                        intent.putExtra("PROFILEIMAGE", uPic);
                        intent.putExtra("USERID", doc_uid);
                        intent.putExtra("TOKEN", token);
                        context.startActivity(intent);
                        /////////////////////////////////


/*
                        FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(receiverToken, senderName
                                , msg, getApplicationContext(), MessagingActivity.this);
                        fcmNotificationsSender.SendNotifications();
*/

                        firebaseDatabase.getReference("Users").child(receiverId).child("Contacts").child(senderId)
                                .child("interactionTime").setValue(date);

                        firebaseDatabase.getReference("Users").child(senderId).child("Contacts").child(receiverId)
                                .child("interactionTime").setValue(date);


                        firebaseDatabase.getReference("Users").child(receiverId).child("Contacts")
                                .child(senderId).child("Chats").push()
                                .setValue(finalMessageModel);


                        firebaseDatabase.getReference("Users").child(senderId).child("Contacts").child(receiverId)
                                .child("recentMessage").setValue(msg);

                        firebaseDatabase.getReference("Users").child(receiverId).child("Contacts").child(senderId)
                                .child("recentMessage").setValue(msg);
                        // tag = "";


                    }
                });
        //  }

    }

}
