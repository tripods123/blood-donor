package com.example.blooddonorapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;


public class EmailAndPasswordActivity extends AppCompatActivity {

    TextInputLayout txtInputEmail,txtInputPassword;
    AlertDialog.Builder msgbox;
    Button submitButton;
    FirebaseAuth firebaseAuth;


    public static final Pattern PASSWORD = Pattern.compile("^" +
            "(?=.*[!@#$%*+_])" +     // at least 1 special character
            "(?=\\S+$)" +            // no white spaces
            ".{4,}" +                // at least 4 characters
            "$");


//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------!
    //ON CREATE METHOD
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------!

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ep);

        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar_EP);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailAndPasswordActivity.this,LoginActivity.class));
                finish();
            }
        });
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //INITIALIZING ALL WIDGETS
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        submitButton = (Button) findViewById(R.id.next);
        txtInputEmail = findViewById(R.id.txt_input_email);
        txtInputPassword = findViewById(R.id.txt_input_pw);
        txtInputEmail.getEditText().requestFocus();
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //LISTENER FOR SUBMIT BUTTON1
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (txtInputEmail.getEditText().getText().toString().isEmpty() || txtInputPassword.getEditText().getText().toString().isEmpty()) {
                        Toast.makeText(EmailAndPasswordActivity.this, "Please Give Valid Email Address And Password", Toast.LENGTH_LONG).show();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(txtInputEmail.getEditText().getText().toString()).matches()) {
                        txtInputEmail.setError("Please Enter Valid Email Address");
                    } else if (!PASSWORD.matcher(txtInputPassword.getEditText().getText().toString()).matches()) {
                        txtInputPassword.setError("Invalid Password");
                    } else {
                        txtInputEmail.setError(null);
                        txtInputPassword.setError(null);
                        CreateAccount();
                        txtInputEmail.getEditText().setText("");
                        txtInputPassword.getEditText().setText("");
                    }
                } catch (Exception ex) {}
            }
        });
    }

//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                                            //CREATING ACCOUNT
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void CreateAccount() {
            try {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(txtInputEmail.getEditText().getText().toString(),txtInputPassword.getEditText().getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    ProgressDialog progressDialog=new ProgressDialog(EmailAndPasswordActivity.this);
                                    progressDialog.setContentView(R.layout.activity_ep);
                                    progressDialog.setMessage("Please Wait");
                                    progressDialog.show();

                                    Handler handler=new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(EmailAndPasswordActivity.this,RegisterActivity.class));
                                            finish();
                                        }
                                    },2000);

                                }
                            }
                        });
            } catch (Exception ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                                    //SOME MEHODS
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    public void onBackPressed() {
    }
}
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//END OF CODE
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------