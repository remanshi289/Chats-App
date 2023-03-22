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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView profilePhoto;
    private TextInputEditText profile;
    private Button updateProfile;

    String image;
    boolean imageControl = false;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageReference;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setTitle( "Profile" );
        setContentView( R.layout.activity_profile );

        profilePhoto = findViewById( R.id.circleImageViewProfile );
        profile = findViewById( R.id.editTextUserProfile );
        updateProfile = findViewById( R.id.buttonUpdate );

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        getUserInfo();

        profilePhoto.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        } );

        updateProfile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateprofile();
            }
        } );

    }

    public void updateprofile(){
        String usrName = profile.getText().toString();
        reference.child( "Users" ).child( user.getUid() )
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
                                                            Toast.makeText( ProfileActivity.this
                                                                    , "Write to database is successful.", Toast.LENGTH_SHORT ).show();
                                                        }
                                                    } )
                                                    .addOnFailureListener( new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText( ProfileActivity.this
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
                    .child( "image" ).setValue( image );
        }

        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
        i.putExtra( "usrName",usrName );
        startActivity( i );
        finish();
    }

    public void imageChooser(){
        Intent intent = new Intent();
        intent.setType( "image/*" );
        intent.setAction( Intent.ACTION_GET_CONTENT );
        startActivityForResult( intent,1 );
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){

            imageUri = data.getData();
            Picasso.get().load( imageUri ).into( profilePhoto );
            imageControl = true;
        }
        else{
            imageControl = false;
        }
    }

    public void getUserInfo(){
        reference.child( "Users" ).child( user.getUid() )
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String name = snapshot.child( "usrName" )
                                .getValue().toString();
                        image = snapshot.child( "image" ).getValue().toString();
                        profile.setText( name );

                        if(image.equals( "null" )){
                            profilePhoto.setImageResource( R.drawable.user );
                        }
                        else{
                            Picasso.get().load( image ).into(profilePhoto);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
    }
}