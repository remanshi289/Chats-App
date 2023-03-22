package com.company.chats;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    List<String> usrList;
    String usrName;
    Context mContext;

    FirebaseDatabase database;
    DatabaseReference reference;

    public UsersAdapter(List<String> usrList, String usrName, Context mContext) {
        this.usrList = usrList;
        this.usrName = usrName;
        this.mContext = mContext;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.users_card, parent, false );
        return new ViewHolder( view );

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        reference.child( "Users" ).child( usrList.get(position) )
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String otherName = snapshot.child( "usrName" ).getValue().toString();
                        String imageURL = snapshot.child( "image" ).getValue().toString();

                        holder.textViewUsers.setText( otherName );
                        if(imageURL.equals( "null" )){
                            holder.circleImageViewUsers.setImageResource( R.drawable.user );
                        }
                        else{
                            Picasso.get().load( imageURL ).into( holder.circleImageViewUsers );
                        }

                        holder.cardView.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(mContext,Chat_Activity.class);
                                intent.putExtra( "usrName",usrName );
                                intent.putExtra( "otherName",otherName );
                                mContext.startActivity( intent );
                            }
                        } );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
    }


    @Override
    public int getItemCount() {
        return usrList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textViewUsers;
        private CircleImageView circleImageViewUsers;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );

            textViewUsers = itemView.findViewById( R.id.textViewUsers );
            circleImageViewUsers = itemView.findViewById( R.id.imageViewUsers );
            cardView = itemView.findViewById( R.id.cardView );
        }
    }
}
