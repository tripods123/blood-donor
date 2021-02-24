package com.example.blooddonorapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, InternetConnectivityListener {
    TextView textView, textView2;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    Snackbar snackbar;
    CircleImageView imageView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnection();
        showUser();

        setContentView(R.layout.activity_donate);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        swipeRefreshLayout = findViewById(R.id.refresh_DonorActivity);

        InternetAvailabilityChecker mInternetAvailabilityChecker = InternetAvailabilityChecker.init(this);
        mInternetAvailabilityChecker.addInternetConnectivityListener(this);
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //FOR DRAWER LAYOUT
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //GETTING USER FROM DATABASE
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        textView = headerview.findViewById(R.id.textView);
        textView2 = headerview.findViewById(R.id.textView1);
        imageView=headerview.findViewById(R.id.img_navheader);



//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //FOR REFRESH LAYOUT
//---------------------------------------------------------------------------------------------------------------------------------------------------------------

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //METHOD FOR SHOWING USER NAME
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void showUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = firebaseDatabase.getReference("DonorRegisterDetails");
            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String Firsttname = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").getValue().toString();
                    textView2.setText("Hello, " + Firsttname);
                    textView.setText(firebaseUser.getEmail());
                   // String imgUrl=dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imageUrl").getValue().toString();
                    Picasso.get().load("gs://blood-donation-app-21eda.appspot.com/Images").into(imageView);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        // retriving image



    }
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //FOR SELECTING OPTION MENU
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_logout:
                AlertDialog.Builder msgbox = new AlertDialog.Builder(this);
                msgbox.setMessage("Are You Sure Want To Exit");
                msgbox.setTitle("Blood Donation App");
                msgbox.setIcon(R.mipmap.ic_launcher);

                msgbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                });

                msgbox.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InterNetCheck();

                        FirebaseAuth.getInstance().signOut();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        progressDialog = new ProgressDialog(DonorActivity.this);
                        progressDialog.setContentView(R.layout.activity_login);
                        progressDialog.setMessage("Logging You Out...");
                        progressDialog.show();

                        boolean handler = new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(DonorActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                Toast.makeText(DonorActivity.this, "Logout Successful", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }, 2000);
                    }
                });
                msgbox.show();
                break;

            case R.id.nav_profile:
                Intent inten1t = new Intent(DonorActivity.this, ProfileActivity.class);
                startActivity(inten1t);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_settings:
                Intent intent = new Intent(DonorActivity.this, SettingsActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);

        } else {
            AlertDialog.Builder msgbox = new AlertDialog.Builder(this);
            msgbox.setMessage("Are You Sure Want To Exit");
            msgbox.setTitle("Blood Donation App");
            msgbox.setIcon(R.mipmap.ic_launcher);
            msgbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    moveTaskToBack(true);
                }
            });
            msgbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            msgbox.show();
        }
    }
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //CHECKING INTERNET STATE USING BROADCAST RECIVER
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    private void checkConnection() {
        Utilsclass utilsclass=new Utilsclass();
        if (utilsclass.checkConnection(getApplicationContext())==null){
            snackbar=Snackbar.make(swipeRefreshLayout,"You Are Disconnected",Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }
        else { 
        }
    }

    public void InterNetCheck(){
        try{
            Utilsclass utilsclass=new Utilsclass();
            if (utilsclass.checkConnection(getApplicationContext())==null){
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("Blood Donation App");
                builder.setMessage("Couldn't Logout Without Internet Connection Please Try Again Later");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(DonorActivity.this,DonorActivity.class));

                    }
                });
            }

        }catch (Exception e){}
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (!isConnected) {
            snackbar=Snackbar.make(swipeRefreshLayout,"You Are Disconnected",Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }
        else {
        }

    }
}