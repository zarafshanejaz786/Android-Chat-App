package com.example.chatapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapters.DoctorsAdapter;
import com.example.chatapp.databinding.ActivitySigninBinding;
import com.example.model.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SigninActivity extends AppCompatActivity {

    String uName_fb ,uMail_fb , userType_fb;

    String userType;
    FirebaseAuth myAuth;
    ActivitySigninBinding activitySigninBinding;
    SharedPreferences sharedPreferences;
    FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySigninBinding = ActivitySigninBinding.inflate(getLayoutInflater());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(activitySigninBinding.getRoot());

//        sharedPreferences = getSharedPreferences("SavedToken",MODE_PRIVATE);


        myAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        activitySigninBinding.progressBar.setVisibility(View.GONE);


        activitySigninBinding.hidePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(activitySigninBinding.signinPassword.getTransformationMethod()!=null)
                     activitySigninBinding.signinPassword.setTransformationMethod(null);
                else activitySigninBinding.signinPassword.setTransformationMethod(new PasswordTransformationMethod());

            }
        });
        activitySigninBinding.moveToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigninActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });

        userType();
        activitySigninBinding.signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String email = activitySigninBinding.signinMail.getText().toString().trim();
                    String password = activitySigninBinding.signinPassword.getText().toString().trim();


                    if (!email.isEmpty() && !password.isEmpty()) {
                       if (userType != null) {
                      //  if (userType.equals(userType_fb)) {
                            activitySigninBinding.progressBar.setVisibility(View.VISIBLE);

                            myAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        activitySigninBinding.progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {

                                            checkUser();
                                            if (userType.equals(userType_fb)){
                                                String id = task.getResult().getUser().getUid();


                                                sharedPreferences = getSharedPreferences("SavedToken", MODE_PRIVATE);
                                                String tokenInMain = sharedPreferences.getString("ntoken", "mynull");
                                                firebaseDatabase.getReference("Users").child(id).child("token").setValue(tokenInMain);


                                                // Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                                                if (userType_fb.equals("patient")){
                                                    Intent intent = new Intent(SigninActivity.this, Pat_HomeActivity.class);
                                                    startActivity(intent);
                                                }else if (userType_fb.equals("doctor")){
                                                    Intent intent = new Intent(SigninActivity.this, Doc_HomeActivity.class);
                                                    startActivity(intent);
                                                }

                                            }else {
                                                FirebaseAuth.getInstance().signOut();
                                                Toast.makeText(SigninActivity.this, " No such user available " , Toast.LENGTH_SHORT).show();

                                            }
                                        } else {
                                            activitySigninBinding.progressBar.setVisibility(View.GONE);
                                            Toast.makeText(SigninActivity.this, "Try again - " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        }else {
                            //Toast.makeText(SigninActivity.this, "Please Select a user type", Toast.LENGTH_SHORT).show();
                            Toast.makeText(SigninActivity.this, "no such user available", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        activitySigninBinding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(SigninActivity.this, "Enter details", Toast.LENGTH_SHORT).show();
                    }
            }
        });

    }

    private void userType(){
        RadioGroup radioGroup = findViewById(R.id.radio_group);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_patient:
                        // Patient selected
                        userType = "patient";
                        break;
                    case R.id.radio_doctor:
                        // Doctor selected
                        userType = "doctor";
                        break;
                }
            }
        });
    }

    private void checkUser(){
      //  userType();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            String userId = firebaseAuth.getCurrentUser().getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myPatientsRef = database.getReference("Users").child(userId);
            myPatientsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                     uName_fb = snapshot.child("userName").getValue().toString();
                     uMail_fb = snapshot.child("userMail").getValue().toString();
                     userType_fb = snapshot.child("userType").getValue().toString();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: " + error.getMessage());
                }
            });

/*        if (userType.equals(userType_fb)){
            return true;
        }else {
            return false;
        }*/
    }
}