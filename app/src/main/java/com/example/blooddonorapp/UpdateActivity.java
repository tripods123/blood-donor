package com.example.blooddonorapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UpdateActivity extends AppCompatActivity {

    EditText edt_name,  edt_contactno, edt_email, edt_city;
    String name,  contactno, email, city;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Toolbar toolbar = findViewById(R.id.toolbar_update_prof);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateActivity.this, DonorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        edt_name = findViewById(R.id.name);
        edt_contactno = findViewById(R.id.contactno);
        edt_email = findViewById(R.id.email);
        edt_city = findViewById(R.id.city);

        button = findViewById(R.id.btn_update);

        getData();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

    }
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                                            //METHOD FOR GET DATA FROM DATABASE
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void getData() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("DonorRegisterDetails");
            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").getValue().toString();
                    contactno = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("con").getValue().toString();
                    city = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("city").getValue().toString();
                    email = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("email").getValue().toString();

                    edt_name.setText(name);
                    edt_contactno.setText(contactno);
                    edt_email.setText(email);
                    edt_city.setText(city);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                                            //METHOD FOR UPDATE
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void updateData() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", edt_name.getText().toString());
        map.put("con", edt_contactno.getText().toString());
        map.put("email", edt_email.getText().toString());
        map.put("city", edt_city.getText().toString());


        FirebaseDatabase.getInstance().getReference("DonorRegisterDetails")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            ProgressDialog progressDialog = new ProgressDialog(UpdateActivity.this);
                            progressDialog.setContentView(R.layout.activity_update);
                            progressDialog.setMessage("Please Wait....");
                            progressDialog.show();

                               new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder msgbox = new AlertDialog.Builder(UpdateActivity.this);
                                    msgbox.setMessage("Your Profile Updated Successfully");
                                    msgbox.setTitle("Blood Donation App");
                                    msgbox.setIcon(R.mipmap.ic_launcher);
                                    msgbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(UpdateActivity.this, DonorActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    msgbox.show();

                                }
                            }, 2000);

                        }

                        if (task.isCanceled()) {
                            //Nothing
                        }

                    }
                });
    }
}
