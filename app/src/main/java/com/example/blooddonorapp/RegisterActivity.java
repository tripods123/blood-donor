package com.example.blooddonorapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.regex.Pattern;
import de.hdodenhof.circleimageview.CircleImageView;


public class RegisterActivity extends AppCompatActivity {

    EditText edt_name, edt_email, edt_contactno, edt_pw,edt_city;
    TextInputLayout txtInputName,  txtInputEmail, txtInputContactNo, txtInputPw,textInputCity;
    AlertDialog.Builder msgbox;
    Button submitButton;
    DatabaseReference myRef;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    CircleImageView imageView;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    int REQ_CODE=7;
    Uri FilePathUri;

    String name;
    String con;
    String email;
    String password;
    String city;
    String imageURL;

    public static final Pattern NAME = Pattern.compile("^[a-zA-Z\\s]*$");
    public static final Pattern PHONE = Pattern.compile("^[+(000)][0-9]{6,14}$");

//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------!
    //ON CREATE METHOD
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------!

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar;
        toolbar=findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });


//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //INITIALIZING ALL WIDGETS
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        submitButton = (Button) findViewById(R.id.btn_register);
        imageView=findViewById(R.id.img_register);
        edt_name=findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email);
        edt_contactno = findViewById(R.id.edt_contactno);
        edt_pw = findViewById(R.id.edt_pw);
        edt_city=findViewById(R.id.edt_city);


        edt_name.requestFocus();

        txtInputName=findViewById(R.id.txt_input_name);
        txtInputEmail = findViewById(R.id.txt_input_email);
        txtInputContactNo = findViewById(R.id.txt_input_contactno);
        txtInputPw = findViewById(R.id.txt_input_pw);
        textInputCity=findViewById(R.id.txt_input_city);


        storageReference= FirebaseStorage.getInstance().getReference("Images");

//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //LISTENER FOR SUBMIT BUTTON1
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edt_name.getText().toString();
                con = edt_contactno.getText().toString();
                email = edt_email.getText().toString();
                city=edt_city.getText().toString();
                password = edt_pw.getText().toString();


                try {

                    if (edt_name.getText().toString().isEmpty() ||  edt_email.getText().toString().isEmpty() || edt_contactno.getText().toString().isEmpty() || edt_pw.getText().toString().isEmpty() || edt_city.getText().toString().isEmpty()) {
                        msgbox = new AlertDialog.Builder(RegisterActivity.this);
                        msgbox.setTitle("Blood Donation App");
                        msgbox.setIcon(R.mipmap.ic_launcher);
                        msgbox.setMessage("All Fields Are Necessary Cannot Register With Empty Fields");
                        msgbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAffinity();
                            }
                        });
                        msgbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(RegisterActivity.this, RegisterActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        msgbox.show();
                    } else if (!NAME.matcher(name).matches()) {
                        txtInputName.setError("Please Enter Valid First Name");
                        Intent intent = new Intent(RegisterActivity.this, RegisterActivity.class);
                        startActivity(intent);

                    } else if (!PHONE.matcher(con).matches()) {
                        txtInputContactNo.setError("Please Enter Valid  Contact Number");
                        Intent intent = new Intent(RegisterActivity.this, RegisterActivity.class);
                        startActivity(intent);


                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {//Validating Email Address
                        txtInputEmail.setError("Please Enter Valid Email Address");


                    }
                    else {
                        txtInputName.setError(null);
                        txtInputEmail.setError(null);
                        txtInputPw.setError(null);
                        txtInputContactNo.setError(null);


                        authenticateDatabase();


                        edt_name.setText("");
                        edt_contactno.setText("");
                        edt_email.setText("");
                        edt_pw.setText("");
                        edt_city.setText("");

                    }
                } catch (Exception ex) {

                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),REQ_CODE);

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQ_CODE && resultCode== RESULT_OK && data!=null && data.getData()!=null){

            FilePathUri=data.getData();

            try {
                Bitmap bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),FilePathUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //INSERTING IMAGE IN DATABASE
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void authenticateDatabase() {
        CheckConnection();
        try {
        storageReference= FirebaseStorage.getInstance().getReference("name");
        storageReference.putFile(FilePathUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                               imageURL = task.getResult().getStorage().getDownloadUrl().toString();
                                           }
                                       });
        databaseReference=FirebaseDatabase.getInstance().getReference();
        myRef = FirebaseDatabase.getInstance().getReference("DonorRegisterDetails");
        firebaseAuth = FirebaseAuth.getInstance();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog = new ProgressDialog(RegisterActivity.this);
                                progressDialog.setContentView(R.layout.activity_register);
                                progressDialog.setMessage("Please Wait....");
                                progressDialog.show();
                                DonorRegisterDetails donor = new DonorRegisterDetails(name, con, email, city,password,imageURL);

                                FirebaseDatabase.getInstance().getReference("DonorRegisterDetails")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(donor)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        AlertDialog.Builder msgbox = new AlertDialog.Builder(RegisterActivity.this);
                                        msgbox.setMessage("Donor Registered Successfully");
                                        msgbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                        msgbox.show();

                                    }
                                });

                            }
                            if (task.isCanceled()){
                                Toast.makeText(RegisterActivity.this,"hii",Toast.LENGTH_LONG).show();
                            }

                        }
                    });
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void uploadImage(){
       /* storageReference= FirebaseStorage.getInstance().getReference("name");
        storageReference.putFile(FilePathUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        imageURL=task.getResult().getStorage().getDownloadUrl().toString();
                        uploadinfo uploadinfo=new uploadinfo(imageURL);
                        databaseReference.child("DonorRegisterDetails").child(firebaseAuth.getCurrentUser().getUid()).setValue("imageURL");
                    }
                });
*/
    }

    public void CheckConnection(){
        Utilsclass utilsclass=new Utilsclass();
        try{
            if (utilsclass.checkConnection(getApplicationContext())==null){
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Blood Donation App");
                builder.setMessage("Could Not Register Data Without Internet Connection Please Try Again");
                builder.setIcon(R.mipmap.ic_launcher);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                        finish();
                    }
                });

            }

        }
        catch (Exception e){}
    }

    public void onBackPressed() {
        AlertDialog.Builder msgbox = new AlertDialog.Builder(RegisterActivity.this);
        msgbox.setMessage("Are You Sure Want To Stop Registration");
        msgbox.setIcon(R.mipmap.ic_launcher);
        msgbox.setTitle("Blood Donation App");
        msgbox.setNegativeButton("Cancel Process", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        msgbox.setPositiveButton("Continue With Process", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        msgbox.show();
    }
}
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//END OF CODE
//!----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------