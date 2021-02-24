package com.example.blooddonorapp;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    ListView listView_Profile;
    ArrayList<String> arrayList;
    ArrayAdapter arrayAdapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        listView_Profile=findViewById(R.id.list_Profile);
        arrayList=new ArrayList<>();

        arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        showProfileDetails();
        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, DonorActivity.class));
                finish();
            }
        });
    }



    public void showProfileDetails() {

        try {

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("DonorRegisterDetails");
                databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").getValue().toString();
                        String contactNo = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("con").getValue().toString();
                        String city = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("city").getValue().toString();
                        String email = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("email").getValue().toString();

                        arrayList.add("Name: " + name);
                        arrayList.add("Contact-No: " + contactNo);
                        arrayList.add("Email: " + email);
                        arrayList.add("City: "+city);

                        listView_Profile.setAdapter(arrayAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }
        catch (Exception e1){
            Toast.makeText(ProfileActivity.this,e1.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
