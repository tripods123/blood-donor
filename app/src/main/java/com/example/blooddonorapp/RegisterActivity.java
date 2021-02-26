package com.example.blooddonorapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    public static final Pattern PHONE = Pattern.compile("^[0-9]{10}$");
    public static final Pattern NAME = Pattern.compile("^[a-zA-Z\\s]*$");

    Button button;
    TextInputLayout input_name, input_phone, input_email,input_city,input_area,input_birthdate,input_bloodGroup;
    ProgressDialog progressDialog;
    AlertDialog.Builder alertDialog;
    Toolbar toolbar;
    Utilsclass utilsclass;
    RadioButton button_male, button_female;
    private String gender=null;
    RadioGroup radioGroup;
    String contactNo, code;
    String name;
    CountryCodePicker countryCodePicker;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        toolbar = findViewById(R.id.toolbar_AP);
        setSupportActionBar(toolbar);
        alertDialog = new AlertDialog.Builder(this);
        progressDialog=new ProgressDialog(this);


        input_name = findViewById(R.id.txt_input_name);
        input_name.getEditText().requestFocus();

        countryCodePicker = findViewById(R.id.countryCodePicker);
        input_phone = findViewById(R.id.txt_input_contactno);
        input_email = findViewById(R.id.txt_input_email);
        input_city=findViewById(R.id.txt_input_city);
        input_area=findViewById(R.id.txt_input_area);
        input_birthdate=findViewById(R.id.txt_input_birthdate);
        input_bloodGroup=findViewById(R.id.txt_input_bloodGroup);


        button_male = findViewById(R.id.male);
        button_female = findViewById(R.id.female);
        radioGroup = findViewById(R.id.radio_grp);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.male) {
                    gender = "Male";
                    Toast.makeText(RegisterActivity.this, gender, Toast.LENGTH_SHORT).show();
                }
                if (checkedId == R.id.female) {
                    gender = "Female";
                    Toast.makeText(RegisterActivity.this, gender, Toast.LENGTH_SHORT).show();
                }
            }
        });

        button = findViewById(R.id.btn_register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_name.getEditText().getText().toString().isEmpty()
                        || input_email.getEditText().getText().toString().isEmpty() || input_phone.getEditText().getText().toString().isEmpty()
                        || input_city.getEditText().getText().toString().isEmpty() || input_area.getEditText().getText().toString().isEmpty()
                        || input_birthdate.getEditText().getText().toString().isEmpty() || input_bloodGroup.getEditText().getText().toString().isEmpty()
                        || gender==null) {
                    Toast.makeText(RegisterActivity.this, "Couldn't Register With Empty Fields", Toast.LENGTH_LONG).show();
                }
                if (!NAME.matcher(input_name.getEditText().getText().toString().trim()).matches()) {
                    input_name.setError("Invalid Name");
                } else if (!PHONE.matcher(input_phone.getEditText().getText().toString().trim()).matches()) {
                    input_phone.setError("Invalid Phone Number");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(input_email.getEditText().getText().toString().trim()).matches()) {
                    input_email.setError("Invalid Email Address");

                } else {
                    name=input_name.getEditText().getText().toString();
                    code=countryCodePicker.getSelectedCountryCode();
                    contactNo = "+"+code+""+input_phone.getEditText().getText().toString();

                    input_name.setError("");
                    input_email.setError("");
                    input_phone.setError("");

                    insertData();

                    input_name.getEditText().setText(null);
                    input_phone.getEditText().setText(null);
                    input_email.getEditText().setText(null);
                    input_city.getEditText().setText(null);
                    input_area.getEditText().setText(null);
                    input_birthdate.getEditText().setText(null);
                    input_bloodGroup.getEditText().setText(null);
                }

            }
        });
    }

    private void insertData() {
        utilsclass = new Utilsclass();
        if (utilsclass.checkConnection(getApplicationContext()) == null) {
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Could not register without internet");
            alertDialog.setIcon(R.mipmap.ic_icon);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(RegisterActivity.this, RegisterActivity.class));
                    finish();
                }
            });
            alertDialog.show();
        } else {
            HashMap<String, Object> map = new HashMap<>();
            map.put("Name", input_name.getEditText().getText().toString());
            map.put("Contact Number", contactNo);
            map.put("Email Address", input_email.getEditText().getText().toString());
            map.put("City", input_city.getEditText().getText().toString());
            map.put("Area And Pin Code", input_area.getEditText().getText().toString());
            map.put("Blood Group", input_bloodGroup.getEditText().getText().toString());
            map.put("Birth Date", input_birthdate.getEditText().getText().toString());
            map.put("Gender", gender);

            FirebaseDatabase.getInstance().getReference("Donner Profile Details")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent intent=new Intent(RegisterActivity.this,ImageUploadActivity.class);
                                intent.putExtra("Name",name);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
    }
}
