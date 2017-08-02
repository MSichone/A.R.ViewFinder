package com.masitano.arviewfinder;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;

    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignup, buttonLogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //checking if user session is valid
        if (firebaseAuth.getCurrentUser() != null) {
            //ensure user is on general notifications topic
            startActivity(new Intent(SignUpActivity.this, MapsActivity.class));
            finish();
        }

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
    }

    private void registerUser(){

        //getting email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            //display some message here
                            Toast.makeText(SignUpActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SignUpActivity.this, MapsActivity.class));
                            finish();
                        }else{
                            //display some message here
                            Toast.makeText(SignUpActivity.this,"Registration Error", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    private void authenticateUser(){
        //getting email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        final String password  = editTextPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Logging In Please Wait...");
        progressDialog.show();

        //authenticate user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                //Toast.makeText(SignUpActivity.this,getString(R.string.minimum_password),Toast.LENGTH_LONG).show();
                                openDialog(getString(R.string.minimum_password));
                            } else {
                                //Toast.makeText(SignUpActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                openDialog(getString(R.string.auth_failed));
                            }
                        } else {
                            Intent intent = new Intent(SignUpActivity.this, MapsActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        progressDialog.dismiss();
                    }
                });
    }



    private void openDialog(String message) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(SignUpActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_message_error, null);
        final android.app.AlertDialog messageDialog = new android.app.AlertDialog.Builder(this).create();
        messageDialog.setView(mView);
        final TextView txtMessage = (TextView) mView.findViewById(R.id.txt_dialog_message);
        final ImageButton btnOk = (ImageButton) mView.findViewById(R.id.btn_dialog_message_ok);
        txtMessage.setText(message);
        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                messageDialog.dismiss();
            }
        });
        //capturing the cancel button
        messageDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                messageDialog.dismiss();
            }
        });
        messageDialog.show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.buttonSignup){
            //calling register method on click
            registerUser();
        }

        if(v.getId()==R.id.buttonLogin){
            //calling login method on click
            authenticateUser();
        }


    }
}
