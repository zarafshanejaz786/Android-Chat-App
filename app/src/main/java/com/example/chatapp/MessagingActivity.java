package com.example.chatapp;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatapp.databinding.ActivityMessagingBinding;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.adapters.messageAdapter;
import models.MessageModel;

public class MessagingActivity extends AppCompatActivity {

    String tagImg;
    String tagPDF;
    String tagAudio;
    Uri currentGallery_File_Uri;
    String current_File_FirebaseUri;

    Uri audioUri_gallery;
    Uri pdfUri_gallery;
    ///////////// tech project
    private static final int REQUEST_AUDIO_PERMISSION_CODE = 101;//audio
    private static final int PICK_PDF_FILE = 1;

    Handler handler;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    MediaRecorder mediaRecorder_tp;
    MediaPlayer mediaPlayer_tp;
    ImageView ibRecorder, ibPlay;
    TextView tvTime, tvRecordingPath, ivSimpleBg;
    EditText typing_space;
    boolean isRecording = false;
    boolean isPlaying = false;
    int seconds = 0;
    String path = null;
   // LottieAnimationView lavPlaying;
    int dummyseconds = 0;
    int playabbleSeconds = 0;

    /////////////
    // private Button StartRecording, StopRecording, StartPlaying, StopPlaying;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String AudioSavaPath = null;

    //////

    private static final int PICK_IMAGE_REQUEST = 1;
    private MediaRecorder mRecorder;
    private String mFileName;
    private Uri mImageUri_gallery;
    private Uri mImageFirebaseUri;
    private String commonUri_firebase;
    private Uri mAudioFirebaseUri;
    private Uri mPDF_FirebaseUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ActivityMessagingBinding activityMessagingBinding;
    public String receiverId;
    String receiverToken, senderName;
    String senderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /////////////TechPro
        mediaRecorder_tp = new MediaRecorder();
        mediaPlayer_tp = new MediaPlayer();
        tvTime = findViewById(R.id.tv_time);
        tvRecordingPath = findViewById(R.id.tv_recording_path);
        typing_space = findViewById(R.id.typing_space);
        /////////////TechPro

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        activityMessagingBinding = ActivityMessagingBinding.inflate(getLayoutInflater());
        setContentView(activityMessagingBinding.getRoot());

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                activityMessagingBinding.parentViewgroup.setBackground(AppCompatResources.getDrawable(MessagingActivity.this, R.drawable.wpdark));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                activityMessagingBinding.parentViewgroup.setBackground(AppCompatResources.getDrawable(MessagingActivity.this, R.drawable.wplight));
                break;
        }

        senderId = firebaseAuth.getUid();

        Intent intent = getIntent();
        String uname = intent.getStringExtra("USERNAME");
        String profileImg = intent.getStringExtra("PROFILEIMAGE");
        receiverId = intent.getStringExtra("USERID");
        receiverToken = intent.getStringExtra("TOKEN");


        activityMessagingBinding.receiverName.setText(uname);
        Picasso.get().load(profileImg).fit().centerCrop()
                .error(R.drawable.user)
                .placeholder(R.drawable.user)
                .into(activityMessagingBinding.profilePicImageview);


        activityMessagingBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent = new Intent(MessagingActivity.this, MainActivity.class);
                //startActivity(intent);
                onBackPressed();
            }
        });


        final ArrayList<MessageModel> msgData = new ArrayList<>();
        final messageAdapter msgAdapter = new messageAdapter(msgData, MessagingActivity.this);
        activityMessagingBinding.msgRecyclerview.setAdapter(msgAdapter);
        activityMessagingBinding.msgRecyclerview.setLayoutManager(new LinearLayoutManager(this));


        firebaseDatabase.getReference("Users")
                .child(senderId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        senderName = dataSnapshot.child("userName").getValue().toString();
                        msgData.clear();

                        for (DataSnapshot e : dataSnapshot.child("Contacts").child(receiverId).child("Chats").getChildren()) {


                            String msg = e.child("msgText").getValue().toString();

                            String decrypted = null;
                            try {
                                decrypted = AESUtils.decrypt(msg);
                            } catch (Exception er) {
                                er.printStackTrace();
                            }
                            msgData.add(new MessageModel(
                                    e.child("uId").getValue().toString()
                                    , decrypted
                                    , (Long) Long.valueOf(e.child("msgTime").getValue().toString())
                                    , e.child("imageUri").getValue().toString()
                                 //   , e.child("audioUri").getValue().toString()
                                  //  , e.child("pdfUri").getValue().toString()
//                                ,mImageUri.toString()
                            ));


/*                    if (mImageUri!= null){
                        msgData.add(new MessageModel(
                                e.child("uId").getValue().toString()
                                ,decrypted
                                ,(Long) Long.valueOf(e.child("msgTime").getValue().toString())
                                ,e.child("imageUri").getValue().toString()
//                                ,mImageUri.toString()
                                ));
                    }else {
                        msgData.add(new MessageModel(e.child("uId").getValue().toString()
                            ,decrypted
                            ,(Long) Long.valueOf(e.child("msgTime").getValue().toString())));
                    }*/

                        }

                        msgAdapter.notifyDataSetChanged();
                        activityMessagingBinding.msgRecyclerview.scrollToPosition(msgAdapter.getItemCount() - 1);

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                    }
                });


//        FirebaseMessaging.getInstance().subscribeToTopic("all");

        //Messaging Mechanism
        activityMessagingBinding.sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  if (mImageUri != null){
                    uploadFile();
                }
                String msgTxt = activityMessagingBinding.typingSpace.getText().toString();
                if (msgTxt != null){
                    sendTextMsg(msgAdapter.getItemCount()-1);
                }*/
                try {
                    sendTextMsg(msgAdapter.getItemCount() - 1);


                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });


        activityMessagingBinding.msgRecyclerview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override

            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                if (bottom < oldBottom) {
                    activityMessagingBinding.msgRecyclerview.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if ((msgAdapter.getItemCount() - 1) > 1)
                                activityMessagingBinding.msgRecyclerview.smoothScrollToPosition(msgAdapter.getItemCount() - 1);
                        }
                    }, 10);
                }

            }
        });

    }


/*
    private void uploadFile() {
        if (mImageUri_gallery != null) {
            // Set a progress bar while the image is being uploaded
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Initialize a reference to the Firebase Storage location where you want to store your images
            //StorageReference storageRef = FirebaseStorage.getInstance().getReference("gs://my-doctor-online-f174e.appspot.com/");
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("chat_images");

            // Create a unique file name for the image
            StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri_gallery));

            // Upload the image to Firebase Storage
            fileReference.putFile(mImageUri_gallery)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Hide the progress bar and show a success message
                            progressDialog.dismiss();
                            Toast.makeText(MessagingActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();

                            // Get the download URL of the image
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mDatabaseRef = firebaseDatabase.getReference("Users");


                                    String uploadId = mDatabaseRef.push().getKey();
                                    Upload upload = new Upload(uploadId, uri.toString());

                                    // Save the download URL to the Firebase Realtime Database
                                    //mDatabaseRef.child(uploadId).setValue(upload);

                                    mDatabaseRef.child(senderId).child("Contacts").child(receiverId)
                                            .child("recentMessage").setValue(upload);

                                    mDatabaseRef.child(receiverId).child("Contacts").child(senderId)
                                            .child("recentMessage").setValue(upload);

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Hide the progress bar and show an error message
                            progressDialog.dismiss();
                            Toast.makeText(MessagingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // Show the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        } else {
            Toast.makeText(MessagingActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
*/
/*
public void sendTextMsg(int msgAdapterPosition){
    String msg = activityMessagingBinding.typingSpace.getText().toString().trim();

    String encryptedMsg = msg;
    try {
        encryptedMsg = AESUtils.encrypt(msg);
    } catch (Exception e) {
        e.printStackTrace();
    }

    long date = new Date().getTime();
    final MessageModel messageModel;
    activityMessagingBinding.typingSpace.setText("");
    if (mImageUri!= null){
        messageModel = new MessageModel(senderId, encryptedMsg, date,mImageUri.toString());
    }else {
        messageModel = new MessageModel(senderId, encryptedMsg, date);

    }

    if(!msg.isEmpty()) {
        firebaseDatabase.getReference("Users").child(senderId).child("Contacts")
                .child(receiverId).child("Chats").push()
                .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        activityMessagingBinding.msgRecyclerview.scrollToPosition(msgAdapterPosition);

                        FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(receiverToken,senderName
                                ,msg,getApplicationContext(),MessagingActivity.this);
                        fcmNotificationsSender.SendNotifications();

                        firebaseDatabase.getReference("Users").child(receiverId).child("Contacts").child(senderId)
                                .child("interactionTime").setValue(date);

                        firebaseDatabase.getReference("Users").child(senderId).child("Contacts").child(receiverId)
                                .child("interactionTime").setValue(date);


                        firebaseDatabase.getReference("Users").child(receiverId).child("Contacts")
                                .child(senderId).child("Chats").push()
                                .setValue(messageModel);


                        firebaseDatabase.getReference("Users").child(senderId).child("Contacts").child(receiverId)
                                .child("recentMessage").setValue(msg);

                        firebaseDatabase.getReference("Users").child(receiverId).child("Contacts").child(senderId)
                                .child("recentMessage").setValue(msg);


                    }
                });
    }



}
*/

    public void sendTextMsg(int msgAdapterPosition) throws Exception {
        String txtMsg = "";
        String msg = activityMessagingBinding.typingSpace.getText().toString().trim();

       if (tagImg == "image"){
           if (currentGallery_File_Uri != null){
               uploadImage(currentGallery_File_Uri, msgAdapterPosition);
              // uploadMsg_To_Rt_DB(msgAdapterPosition,current_File_FirebaseUri.toString(),"image");

           }
       }
       if (tagPDF == "pdf"){
           if (currentGallery_File_Uri != null){
               uploadPDF(currentGallery_File_Uri, msgAdapterPosition);
              // uploadMsg_To_Rt_DB(msgAdapterPosition,current_File_FirebaseUri.toString(),"pdf");
           }
       }
       if (tagAudio == "audio"){
           if (currentGallery_File_Uri != null){
               uploadAudio(currentGallery_File_Uri,msgAdapterPosition);
               //uploadMsg_To_Rt_DB(msgAdapterPosition,current_File_FirebaseUri.toString(),"audio");
           }
       }

        if (!msg.isEmpty()){
           uploadMsg_To_Rt_DB(msgAdapterPosition,"null",msg);

       }

        /*
        if (pdfUri_gallery !=null){
            uploadPDF(pdfUri_gallery);
        }
*/
/*
        if (audioUri_gallery !=null){
            uploadAudio(audioUri_gallery);
        }
*/





      //  uploadMsg_To_Rt_DB(msgAdapterPosition);
    }
/*
    public void sendTextMsg(int msgAdapterPosition) throws Exception {
        if (pdfUri_gallery !=null){
            uploadPDF(pdfUri_gallery);
        }
        if (audioUri_gallery !=null){
            uploadAudio(audioUri_gallery);
        }
        String txtMsg = "";
        String msg = activityMessagingBinding.typingSpace.getText().toString().trim();

        String encryptedMsg = msg;
        try {
            encryptedMsg = AESUtils.encrypt(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long date = new Date().getTime();
        MessageModel messageModel = new MessageModel();
        activityMessagingBinding.typingSpace.setText("");
        if (mImageUri_gallery != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference("chat_images");

            StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri_gallery));

            fileReference.putFile(mImageUri_gallery)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Hide the progress bar and show a success message
                            progressDialog.dismiss();
                            Toast.makeText(MessagingActivity.this, "Image Upload successful", Toast.LENGTH_SHORT).show();

                            // Get the download URL of the image
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                   // mImageFirebaseUri = uri;
                                    commonUri = uri.toString()+".img";
                                    mImageUri_gallery = null;
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Hide the progress bar and show an error message
                            progressDialog.dismiss();
                            Toast.makeText(MessagingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // Show the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });

            ////////
           // if (mImageFirebaseUri != null && mAudioFirebaseUri == null && mPDF_FirebaseUri == null) {


*/
/*
            if (commonUri != null ) {
                txtMsg = AESUtils.encrypt("photo");

             //   messageModel = new MessageModel(senderId, txtMsg, date, mImageFirebaseUri.toString(), "null", "null");
                messageModel = new MessageModel(senderId, txtMsg, date, commonUri.toString());
            }
*//*


        } */
/*else {
           // messageModel = new MessageModel(senderId, encryptedMsg, date, "null","null","null");
            messageModel = new MessageModel(senderId, encryptedMsg, date, "null");
            txtMsg = encryptedMsg;
        }*//*

        if (encryptedMsg.isEmpty()){
            encryptedMsg = "document";
        }
        messageModel = new MessageModel(senderId, txtMsg, date, commonUri.toString());
        txtMsg = encryptedMsg;

        if (!txtMsg.isEmpty()) {
            MessageModel finalMessageModel = messageModel;
            firebaseDatabase.getReference("Users").child(senderId).child("Contacts")
                    .child(receiverId).child("Chats").push()
                    .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            activityMessagingBinding.msgRecyclerview.scrollToPosition(msgAdapterPosition);

                            FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(receiverToken, senderName
                                    , msg, getApplicationContext(), MessagingActivity.this);
                            fcmNotificationsSender.SendNotifications();

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
                        }
                    });
        }
    }

*/
    public void openGallery(View view) {
        openFileChooser();

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
           // mImageUri_gallery = data.getData();
            currentGallery_File_Uri = data.getData();

            // Display the selected image
            ImageView selected_image_iv = findViewById(R.id.selected_image);
            LinearLayout selected_img_container = findViewById(R.id.selected_img_container);
            LinearLayout typing_space_parent = findViewById(R.id.typing_space_parent);
            selected_img_container.setVisibility(View.VISIBLE);
            typing_space_parent.setVisibility(View.GONE);
           // selected_image_iv.setImageURI(mImageUri_gallery);
            selected_image_iv.setImageURI(currentGallery_File_Uri);
        }
        if (requestCode == REQUEST_CODE_PICK_AUDIO && resultCode == RESULT_OK) {
          //  audioUri_gallery = data.getData();
            currentGallery_File_Uri = data.getData();
            //uploadAudio(audioUri_firebase);


        }
        if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //pdfUri_gallery = data.getData();
            currentGallery_File_Uri = data.getData();
            // Do something with the PDF Uri
        }
    }

    private void startRecording() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audio_record.3gp";

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            Log.e(TAG, "startRecording() failed");
        }
    }

    public void startRecorder(View view) {
       // startRec();
       // ibRecord(); //techPro
        pickAudioFromGallery();
    }

    public void stopRecorder(View view) {
      //  StopRecording();
    }

    public void playAudio(View view) {
        //startPlaying();
       // ibPlay();
    }

    public void stopPlay(View view) {
      //  stopPlaying();
    }

    private void startRec() {
        Toast.makeText(MessagingActivity.this, "Recording Method", Toast.LENGTH_SHORT).show();

        if (checkPermissions()) {

            AudioSavaPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + "recordingAudio.mp3";

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(AudioSavaPath);

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                Toast.makeText(MessagingActivity.this, "Recording started", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {

            ActivityCompat.requestPermissions(MessagingActivity.this, new String[]{
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }
    }

    private void startPlaying() {
        Toast.makeText(MessagingActivity.this, "StartPlaying", Toast.LENGTH_SHORT).show();
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(AudioSavaPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(MessagingActivity.this, "Start playing", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void StopRecording() {
        Toast.makeText(MessagingActivity.this, "StopRecording", Toast.LENGTH_SHORT).show();

        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            Toast.makeText(MessagingActivity.this, "Recording stopped", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void stopPlaying() {
        if (mediaPlayer != null) {

            mediaPlayer.stop();
            mediaPlayer.release();
            Toast.makeText(MessagingActivity.this, "Stopped playing", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermissions() {
        int first = ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO);
        int second = ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return first == PackageManager.PERMISSION_GRANTED &&
                second == PackageManager.PERMISSION_GRANTED;
    }

    private void ibRecord() {

        if (checkRecordingPermission()) {
            if (isRecording) {
                isRecording = true;
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        mediaRecorder_tp = new MediaRecorder();
                        mediaRecorder_tp.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder_tp.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mediaRecorder_tp.setOutputFile(getRecordingFilePath());
                        path = getRecordingFilePath();
                        mediaRecorder_tp.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
                        try {
                            mediaRecorder_tp.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        mediaRecorder_tp.start();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                              //  ivSimpleBg.setVisibility(View.VISIBLE);
//                                lavPlaying.setVisibility(View.GONE);
                                tvRecordingPath.setText(getRecordingFilePath());
                                playabbleSeconds = 0;
                                seconds = 0;
                                dummyseconds = 0;
                                ibRecorder.setImageDrawable(ContextCompat.getDrawable(MessagingActivity.this, R.drawable.baseline_pause_circle_outline_24)); //recordingActive
                                runTimer();
                            }
                        });
                    }
                });
            } else {

                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                       // try {
                            mediaRecorder_tp.stop();
                            mediaRecorder_tp.release();
                            mediaRecorder_tp = null;
                            playabbleSeconds = seconds;
                            dummyseconds = seconds;
                            seconds = 0;
                            isRecording = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //  ivSimpleBg.setVisibility(View.GONE);
                                    handler.removeCallbacksAndMessages(null);
                                    ibRecorder.setImageDrawable(ContextCompat.getDrawable(MessagingActivity.this, R.drawable.baseline_play_circle_outline_24)); //recording_in_Active

                                }
                            });

                      /*  }catch (Exception e){
                            e.printStackTrace();
                        }*/


                    }
                });
            }
        } else {
            requestRecordingPermissins();
        }

    }

    private void ibPlay() {
        if (!isPlaying) {
            if (path != null) {
                try {
                    mediaPlayer_tp.setDataSource(getRecordingFilePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "No Recording Present", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                mediaPlayer_tp.prepare();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mediaPlayer_tp.start();
            isPlaying = true;
            ibPlay.setImageDrawable(ContextCompat.getDrawable(MessagingActivity.this, R.drawable.baseline_pause_circle_outline_24));//recording_pause
           // ivSimpleBg.setVisibility(View.GONE);
 //           lavPlaying.setVisibility(View.VISIBLE);
            runTimer();
        } else {
            mediaPlayer_tp.stop();
            mediaPlayer_tp.release();
            mediaPlayer_tp = null;
            mediaPlayer_tp = new MediaPlayer();
            isPlaying = false;
            seconds = 0;
            handler.removeCallbacksAndMessages(null);
          //  ivSimpleBg.setVisibility(View.VISIBLE);
 //           lavPlaying.setVisibility(View.GONE);
            ibPlay.setImageDrawable(ContextCompat.getDrawable(MessagingActivity.this, R.drawable.baseline_play_circle_outline_24));//recording_play


        }
    }

    private void runTimer() {
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
                tvTime.setText(time);
                if (isRecording || (isPlaying && playabbleSeconds != -1)) {
                    seconds++;
                    playabbleSeconds--;
                    if (playabbleSeconds == -1 && isPlaying) {

                        mediaPlayer_tp.stop();
                        mediaPlayer_tp.release();

                        isPlaying = false;
                        mediaPlayer_tp = null;
                        mediaPlayer_tp = new MediaPlayer();
                        playabbleSeconds = dummyseconds;
                        seconds = 0;
                        handler.removeCallbacksAndMessages(null);
     //                   ivSimpleBg.setVisibility(View.VISIBLE);
     //                     lavPlaying.setVisibility(View.GONE);
                        ibPlay.setImageDrawable(ContextCompat.getDrawable(MessagingActivity.this, R.drawable.baseline_play_circle_outline_24));//recording_play
                        return;

                    }
                }
                handler.postDelayed(this,1000);

            }

        });
    }


    private boolean checkRecordingPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            requestRecordingPermissins();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (permissionToRecord) {
                    Toast.makeText(MessagingActivity.this, "Permission Given", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MessagingActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();

                }
            }
        }

    }

    private void requestRecordingPermissins() {
        ActivityCompat.requestPermissions(MessagingActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION_CODE);
    }
    private static final int REQUEST_CODE_PICK_AUDIO = 1;



    public void select_PDF_from_gallery(View view) {
        openFilePicker();
    }
    // Open the file picker when a button is clicked

    private void uploadMsg_To_Rt_DB(int msgAdapterPosition, String currentGallery_File_Uri, String msg){
       // String txtMsg = "";
       // String msg = activityMessagingBinding.typingSpace.getText().toString().trim();

        String encryptedMsg = msg;
        try {
            encryptedMsg = AESUtils.encrypt(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long date = new Date().getTime();
        MessageModel messageModel = new MessageModel();
        activityMessagingBinding.typingSpace.setText("");
        //uploadImage();
/*
        if (encryptedMsg.isEmpty()){
            encryptedMsg = "document";
        }
*/
        messageModel = new MessageModel(senderId, encryptedMsg, date, currentGallery_File_Uri);
       // txtMsg = encryptedMsg;
      //  if (!encryptedMsg.isEmpty()) {
            MessageModel finalMessageModel = messageModel;
            firebaseDatabase.getReference("Users").child(senderId).child("Contacts")
                    .child(receiverId).child("Chats").push()
                    .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            activityMessagingBinding.msgRecyclerview.scrollToPosition(msgAdapterPosition);

                            FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(receiverToken, senderName
                                    , msg, getApplicationContext(), MessagingActivity.this);
                            fcmNotificationsSender.SendNotifications();

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
    private void uploadImage(Uri selectedGallery_File_Uri, int msgAdapterPosition){
        //    if (mImageUri_gallery != null) {

        if (selectedGallery_File_Uri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference("chat_images");

            StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(selectedGallery_File_Uri));
/*
            StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri_gallery));
*/

            //   fileReference.putFile(mImageUri_gallery)
            fileReference.putFile(selectedGallery_File_Uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Hide the progress bar and show a success message
                            progressDialog.dismiss();
                            Toast.makeText(MessagingActivity.this, "Image Upload successful", Toast.LENGTH_SHORT).show();

                            // Get the download URL of the image
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // mImageFirebaseUri = uri;
                                    //commonUri_firebase = uri.toString()+".img";
                                    String current_File_FirebaseUri = uri.toString()+".img";
                                    uploadMsg_To_Rt_DB(msgAdapterPosition,current_File_FirebaseUri.toString(),"image");

                                    //mImageUri_gallery = null;
                                    currentGallery_File_Uri = null;

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Hide the progress bar and show an error message
                            progressDialog.dismiss();
                            Toast.makeText(MessagingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // Show the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });

            ////////
            // if (mImageFirebaseUri != null && mAudioFirebaseUri == null && mPDF_FirebaseUri == null) {


/*
            if (commonUri != null ) {
                txtMsg = AESUtils.encrypt("photo");

             //   messageModel = new MessageModel(senderId, txtMsg, date, mImageFirebaseUri.toString(), "null", "null");
                messageModel = new MessageModel(senderId, txtMsg, date, commonUri.toString());
            }
*/

        } /*else {
           // messageModel = new MessageModel(senderId, encryptedMsg, date, "null","null","null");
            messageModel = new MessageModel(senderId, encryptedMsg, date, "null");
            txtMsg = encryptedMsg;
        }*/

    }
    private void uploadPDF(Uri pdfUri , int msgAdapterPosition) {
        // Generate a unique filename for the audio file
        String filename = UUID.randomUUID().toString();

        // Create a reference to the audio file in Firebase Storage
        //StorageReference audioRef = storageRef.child("audio/" + filename);
        StorageReference pdf_Ref =  FirebaseStorage.getInstance().getReference("chat_pdf");
        StorageReference fileReference = pdf_Ref.child(System.currentTimeMillis()+"."+filename+".pdf");


        // Upload the audio file to Firebase Storage
        fileReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL of the audio file
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Toast.makeText(MessagingActivity.this, " pdf Uploaded successful", Toast.LENGTH_SHORT).show();
                                // pdfUri_gallery = null;
                                //mPDF_FirebaseUri = uri;
                                // commonUri_firebase = uri.toString()+".pdf";
                                String current_File_FirebaseUri = uri.toString()+".pdf";
                                uploadMsg_To_Rt_DB(msgAdapterPosition,current_File_FirebaseUri.toString(),"pdf");

                                currentGallery_File_Uri = null;
                                //pdfUri_gallery = null;
                                // Store the download URL in Firebase Realtime Database
/*
                                FirebaseDatabase.getInstance().getReference("pdf_files")
                                        .push().setValue(uri.toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Audio upload and storage successful
                                                Toast.makeText(MessagingActivity.this, " pdf Uploaded successful", Toast.LENGTH_SHORT).show();
                                                pdfUri_gallery = null;
                                                mPDF_FirebaseUri = uri;
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Audio storage failed
                                            }
                                        });
*/
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Audio upload failed
                    }
                });
    }
    private void uploadAudio(Uri audioUri,int msgAdapterPosition) {
        // Generate a unique filename for the audio file
        String filename = UUID.randomUUID().toString();

        // Get a reference to Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Create a reference to the audio file in Firebase Storage
        //StorageReference audioRef = storageRef.child("audio/" + filename);
        StorageReference audioRef =  FirebaseStorage.getInstance().getReference("chat_audio");
        StorageReference fileReference = audioRef.child(System.currentTimeMillis()+"."+filename);


        // Upload the audio file to Firebase Storage
        fileReference.putFile(audioUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL of the audio file
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Toast.makeText(MessagingActivity.this, " Audio Uploaded successful", Toast.LENGTH_SHORT).show();
                                //  mAudioFirebaseUri = uri;
                                //  commonUri_firebase = uri.toString()+".mp3";
                               String current_File_FirebaseUri = uri.toString()+".mp3";
                                uploadMsg_To_Rt_DB(msgAdapterPosition,current_File_FirebaseUri.toString(),"voice");

                                currentGallery_File_Uri = null;
                                //  audioUri_gallery = null;

                                // Store the download URL in Firebase Realtime Database
/*
                                FirebaseDatabase.getInstance().getReference("audios")
                                        .push().setValue(uri.toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Audio upload and storage successful
                                                Toast.makeText(MessagingActivity.this, " Audio Uploaded successful", Toast.LENGTH_SHORT).show();
                                                audioUri_gallery = null;
                                                mAudioFirebaseUri = uri;
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Audio storage failed
                                            }
                                        });
*/
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Audio upload failed
                    }
                });
    }

    private void openFileChooser() {
        tagImg = "image";
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
        //  uploadFile();

    }
    private void openFilePicker() {
        tagPDF = "pdf";
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_PDF_FILE);
    }
    private void pickAudioFromGallery() {
        tagAudio = "audio";

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_AUDIO);
    }

    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File music = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(music, "testFile" + ".mp3");
        return file.getPath();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
         // finishAffinity();
    }

}
