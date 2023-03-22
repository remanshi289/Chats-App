package com.company.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_Activity extends AppCompatActivity {

    private TextInputEditText email, password;
    private TextView forgotPassword;
    private Button signIn, signUp;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Intent i = new Intent(Login_Activity.this,MainActivity.class);
            startActivity( i );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        email = findViewById( R.id.editTextEmail );
        password = findViewById( R.id.editTextPassword );
        signIn = findViewById( R.id.buttonSignIn );
        signUp = findViewById( R.id.buttonSignUp );
        forgotPassword = findViewById( R.id.textViewForget );

        auth = FirebaseAuth.getInstance();

        signIn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usrEmail = email.getText().toString();
                String usrPswd = password.getText().toString();

                if(!usrEmail.equals( "" ) && !usrPswd.equals( "" )){
                    signin( usrEmail, usrPswd );
                }
                else{
                    Toast.makeText( Login_Activity.this
                            , "Please Enter Email and Password."
                            , Toast.LENGTH_LONG ).show();
                }
            }
        } );

        signUp.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login_Activity.this,Sign_Up.class);
                startActivity( i );
            }
        } );

        forgotPassword.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login_Activity.this,Forgot_Password.class);
                startActivity( i );
            }
        } );
    }

    public void signin(String useEmail,String usrPassword){
        auth.signInWithEmailAndPassword( useEmail, usrPassword )
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent i = new Intent(Login_Activity.this,MainActivity.class);
                            Toast.makeText( Login_Activity.this
                                    , "Sign in is Successful."
                                    , Toast.LENGTH_LONG ).show();
                            startActivity( i );
                        }
                        else{
                            Toast.makeText( Login_Activity.this
                                    , "Sign in is not Successful."
                                    , Toast.LENGTH_LONG ).show();
                        }
                    }
                } );
    }
}