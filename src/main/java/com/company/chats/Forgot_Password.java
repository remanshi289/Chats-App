package com.company.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Forgot_Password extends AppCompatActivity {

    private TextInputEditText editTextEmailForgot;
    private Button buttonForgot;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setTitle( "Reset Password" );
        setContentView( R.layout.activity_forgot_password );

        editTextEmailForgot = findViewById( R.id.editTextEmailForgot );
        buttonForgot = findViewById( R.id.buttonForgot );

        auth = FirebaseAuth.getInstance();

        buttonForgot.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = editTextEmailForgot.getText().toString();
                if(!email.equals( "" )){
                    passwordReset( email );
                }
            }
        } );
    }

    public void passwordReset(String email){
        auth.sendPasswordResetEmail( email )
                .addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText( Forgot_Password.this, "Please check your Email.", Toast.LENGTH_SHORT ).show();
                        }
                        else{
                            Toast.makeText( Forgot_Password.this, "There is a problem.", Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );
    }
}