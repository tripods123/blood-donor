package com.example.blooddonorapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    Button button_login,button_register;
    EditText edt_uname, edt_pass;
    TextInputLayout txt_in_uname, txt_in_pass;
    String email, password;
    AlertDialog.Builder msgbox;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    LinearLayout linearLayout;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        linearLayout=findViewById(R.id.layout_login);
        CheckConnection();
        CheckUser();


//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //INITIALIZING WIDGETS
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        edt_uname = findViewById(R.id.edt_uname);
        edt_pass = findViewById(R.id.edt_pass);


        edt_uname.requestFocus();

        txt_in_uname = findViewById(R.id.txt_input_uname);
        txt_in_pass = findViewById(R.id.txt_input_pass);
        button_login = findViewById(R.id.Login_Button);
        button_register=findViewById(R.id.btn_bcd);

        firebaseAuth = FirebaseAuth.getInstance();
        
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //SETTING ON CLICK LISTENER FOR LOGIN BUTTON
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

       button_register.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
               finish();
           }
       });
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    email=edt_uname.getText().toString();
                    password=edt_pass.getText().toString();

                    if (edt_uname.getText().toString().isEmpty() || edt_pass.getText().toString().isEmpty()){

                        msgbox = new AlertDialog.Builder(LoginActivity.this);
                        msgbox.setTitle("Blood Donation App");
                        msgbox.setIcon(R.mipmap.ic_launcher);
                        msgbox.setMessage("Cannot Login With Empty Fields Try Again");
                        msgbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                edt_uname.requestFocus();
                            }
                        });
                        msgbox.show();

                    }

                    else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {//Validating Email Address
                        txt_in_uname.setError("Please Enter Valid Email Address");
                    }

                    else {
                        txt_in_uname.setError(null);
                        txt_in_pass.setError(null);

                        CheckConnection();
                        login();

                        edt_uname.setText("");
                        edt_pass.setText("");

                    }


                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void login(){
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog=new ProgressDialog(LoginActivity.this);
                                progressDialog.setContentView(R.layout.activity_login);
                                progressDialog.setMessage("Please Wait");
                                Toast.makeText(LoginActivity.this,"Login Successfull",Toast.LENGTH_LONG).show();
                                progressDialog.show();

                                boolean handler=new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent=new Intent(LoginActivity.this,DonorActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                },2000);
                            }

                            else {
                                msgbox = new AlertDialog.Builder(LoginActivity.this);
                                msgbox.setTitle("Blood Donation App");
                                msgbox.setIcon(R.mipmap.ic_launcher);
                                msgbox.setMessage("Bad Username And Password Or User Does Not Exist");
                                msgbox.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        edt_uname.setText("");
                                        edt_pass.setText("");
                                        edt_uname.requestFocus();
                                    }
                                });
                                msgbox.show();
                            }
                        }
                    });
        }
        
        catch (Exception e){}
    }
    
    
    
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //BACK PRESS METH FOR TERMINATE PROCESS
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void onBackPressed() {
        AlertDialog.Builder msgbox = new AlertDialog.Builder(this);
        msgbox.setMessage("Are You Sure Want To Exit");
        msgbox.setTitle("Blood Donation App");
        msgbox.setIcon(R.mipmap.ic_launcher);
        msgbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        msgbox.show();
    }

    public void CheckUser(){
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

        if(firebaseUser!=null){
            startActivity(new Intent(LoginActivity.this,DonorActivity.class));
        }
    }

    public void CheckConnection(){
        Snackbar snackbar = null;
        Utilsclass utilsclass=new Utilsclass();
        try{
            if (utilsclass.checkConnection(getApplicationContext())==null){
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Blood Donation App");
                builder.setMessage("Could Not Login Without Internet Connection Please Try Again");
                builder.setIcon(R.mipmap.ic_launcher);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(LoginActivity.this,LoginActivity.class));
                        finish();
                    }
                });

            }
            else if (utilsclass.checkConnection(getApplicationContext())==null){
                snackbar=Snackbar.make(linearLayout,"No Internet Please Try Again",Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
            }
            else {
            }

        }
        catch (Exception e){}
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckConnection();
    }
}