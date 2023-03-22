package com.company.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Sign_Up extends AppCompatActivity {

    private CircleImageView imageViewCircle;
    private TextInputEditText editTextEmailSignUp, editTextPswdSignUp, editTextUsrNameSignUp;
    private Button buttonSignUp;
    boolean imageControl = false;

    Uri imageUri;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setTitle( "Sign Up" );
        setContentView( R.layout.activity_sign_up );

        imageViewCircle = findViewById( R.id.circleImageView );
        editTextEmailSignUp = findViewById( R.id.editTextUserProfile );
        editTextPswdSignUp = findViewById( R.id.editTextPswdSignUp );
        editTextUsrNameSignUp = findViewById( R.id.editTextUsrNameSignUp );
        buttonSignUp = findViewById( R.id.signUp );

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        imageViewCircle.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        } );

        buttonSignUp.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = editTextEmailSignUp.getText().toString();
                String password = editTextPswdSignUp.getText().toString();
                String usrName = editTextUsrNameSignUp.getText().toString();

                if(!email.equals( "" ) && !password.equals( "" ) && !usrName.equals( "" )){
                    signup(email,password,usrName);
                }
                else{

                }
            }
        } );
    }

    public void imageChooser(){
        Intent intent = new Intent();
        intent.setType( "image/*" );
        intent.setAction( Intent.ACTION_GET_CONTENT );
        startActivityForResult( intent,1 );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){

            imageUri = data.getData();
            Picasso.get().load( imageUri ).into( imageViewCircle );
            imageControl = true;
        }
        else{
            imageControl = false;
        }
    }

    public void signup(String email, String password, String usrName) {

        auth.createUserWithEmailAndPassword( email, password )
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            reference.child( "Users" ).child( auth.getUid() )
                                    .child( "usrName" ).setValue( usrName );

                            if(imageControl){
                                UUID randomID = UUID.randomUUID();
                                String imageName = "image/"+randomID+".jpg";
                                storageReference.child( imageName ).putFile( imageUri )
                                        .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                StorageReference myStorageRef = storage.getReference(imageName);
                                                myStorageRef.getDownloadUrl()
                                                        .addOnSuccessListener( new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                String filePath = uri.toString();
                                                                reference.child( "Users" ).child( auth.getUid() )
                                                                        .child( "image" ).setValue( filePath )
                                                                        .addOnSuccessListener( new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                Toast.makeText( Sign_Up.this
                                                                                        , "Write to database is successful.", Toast.LENGTH_SHORT ).show();
                                                                            }
                                                                        } )
                                                                        .addOnFailureListener( new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText( Sign_Up.this
                                                                                        , "Write to database is not successful.", Toast.LENGTH_SHORT ).show();
                                                                            }
                                                                        } );
                                                            }
                                                        } );
                                            }
                                        } );
                            }
                            else {
                                reference.child( "Users" ).child( auth.getUid() )
                                        .child( "image" ).setValue( "null" );
                            }

                            Intent i = new Intent(Sign_Up.this, MainActivity.class);
                            startActivity( i );
                            finish();

                        }
                        else{
                            Toast.makeText( Sign_Up.this, "There is a Problem.", Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );

    }
}