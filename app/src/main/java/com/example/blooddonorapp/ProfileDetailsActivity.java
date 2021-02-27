package com.example.blooddonorapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDetailsActivity extends AppCompatActivity {
    TextView textView_name=null, textView_mobile=null, textView_email=null, textView_city=null, textView_area=null, textView_birthdate=null, textView_bloodgrp=null, textView_gender=null;
    CircleImageView imageView;
    RelativeLayout relativeLayout;
    DatabaseReference databaseReference;
    String url = null;
    Utilsclass utilsclass = new Utilsclass();
    Bitmap bitmap;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        ChekConn();

        databaseReference = FirebaseDatabase.getInstance().getReference("Donner Profile Details");
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        Toolbar toolbar = findViewById(R.id.toolbar_LPD);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileDetailsActivity.this, MainActivity.class));
                finish();
            }
        });

        relativeLayout = findViewById(R.id.layout_content);
        View view = relativeLayout.getRootView();

        imageView = view.findViewById(R.id.image_profile);

        textView_name = view.findViewById(R.id.nameTextView);
        textView_mobile = view.findViewById(R.id.mobileTextView);
        textView_email = view.findViewById(R.id.emailTextView);
        textView_city = view.findViewById(R.id.cityTextView);
        textView_area = view.findViewById(R.id.areaTextView);
        textView_birthdate = view.findViewById(R.id.bdayTextView);
        textView_bloodgrp = view.findViewById(R.id.bloodTextView);
        textView_gender = view.findViewById(R.id.genderTextView);

        showProfileDetails();

    }

    public void showProfileDetails() {

        try {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("Donner Profile Details");
                databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Name").getValue().toString()!=null
                        && dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Email Address").getValue().toString()!=null
                        && dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Contact Number").getValue().toString()!=null
                        && dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("City").getValue().toString()!=null
                        && dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Area And Pin Code").getValue().toString()!=null
                        && dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Birth Date").getValue().toString()!=null
                        && dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Blood Group").getValue().toString()!=null
                        && dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Gender").getValue().toString()!=null
                        && dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ImageUrl").child("ImageUrl").getValue().toString()!=null)
                        {


                            textView_name.setText(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Name").getValue().toString());
                            textView_email.setText(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Email Address").getValue().toString());
                            textView_mobile.setText(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Contact Number").getValue().toString());
                            textView_city.setText(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("City").getValue().toString());
                            textView_area.setText(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Area And Pin Code").getValue().toString());
                            textView_birthdate.setText(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Birth Date").getValue().toString());
                            textView_bloodgrp.setText(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Blood Group").getValue().toString());
                            textView_gender.setText(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Gender").getValue().toString());

                            url = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ImageUrl").child("ImageUrl").getValue().toString();
                            Picasso.get()
                                    .load(url)
                                    .fit()
                                    .into(imageView);


                        }else {
                            textView_name.setText("--");
                            textView_email.setText("--");
                            textView_mobile.setText("--");
                            textView_city.setText("--");
                            textView_area.setText("--");
                            textView_birthdate.setText("--");
                            textView_gender.setText("--");
                            textView_bloodgrp.setText("--");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

        } catch (Exception e1) {
            Toast.makeText(ProfileDetailsActivity.this, e1.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void ChekConn() {
        if (utilsclass.checkConnection(getApplicationContext()) == null) {
            startActivity(new Intent(ProfileDetailsActivity.this, MainActivity.class));
            Toast.makeText(this, "Could Not Load The Page. Check Your Internet Connection", Toast.LENGTH_LONG).show();
            finish();
        }
    }


    public void onBackPressed() {
    }
}
