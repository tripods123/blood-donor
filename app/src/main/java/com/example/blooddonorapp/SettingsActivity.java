package com.example.blooddonorapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {
    ListView listView_Settings;
    String[]listview_items;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                                            //TOOL BAR
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        Toolbar toolbar=findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingsActivity.this,DonorActivity.class);
                startActivity(intent);
                finish();
            }
        });
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                                            //FOR LIST VIEW
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        listView_Settings=findViewById(R.id.listView_Settings);
        listview_items=getResources().getStringArray(R.array.List_Settings);
        final ArrayAdapter<String> arrayAdapter_settings=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listview_items);
        listView_Settings.setAdapter(arrayAdapter_settings);

//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                                     //LISTENER FOR LISTVIEW
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        listView_Settings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               id=arrayAdapter_settings.getItemId(position);
               if(id==0){
                   startActivity(new Intent(SettingsActivity.this,UpdateActivity.class));
                   finish();
               }
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                                     //LISTENER FOR DELETING USER
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

               if(id==1){
                   AlertDialog.Builder msgbox = new AlertDialog.Builder(SettingsActivity.this);
                   msgbox.setMessage("Are You Sure Want Delete Your Existing Account..?");
                   msgbox.setTitle("Blood Donation App");
                   msgbox.setIcon(R.mipmap.ic_launcher);

                   msgbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                           FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                           firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){
                                       ProgressDialog progressDialog=new ProgressDialog(SettingsActivity.this);
                                       progressDialog.setContentView(R.layout.layout_settings);
                                       progressDialog.setMessage("Deleting Your User Please Wait....");
                                       progressDialog.show();

                                       boolean  handler= new Handler().postDelayed(new Runnable() {
                                           @Override
                                           public void run() {
                                               startActivity(new Intent(SettingsActivity.this,LoginActivity.class));
                                               finish();
                                               Toast.makeText(SettingsActivity.this,"User Deleted Successfully...",Toast.LENGTH_LONG).show();
                                           }
                                       },2000);
                                   }

                                   if (task.isCanceled()){
                                       Toast.makeText(SettingsActivity.this,task.toString(),Toast.LENGTH_LONG).show();
                                   }
                               }
                           });
                       }
                   });

                   msgbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                       }
                   });
                   msgbox.show();
               }
            }
        });
    }
}
