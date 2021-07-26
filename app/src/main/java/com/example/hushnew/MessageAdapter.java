package com.example.hushnew;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.hush.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 Adapter for messages
 **/
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private FirebaseAuth mAuth;
    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;

    public MessageAdapter(List<Messages> mMessageList,Cipher cipher, Cipher decipher,SecretKeySpec secretKeySpec) {

        this.mMessageList = mMessageList;
        this.cipher = cipher;
        this.decipher = decipher;
        this.secretKeySpec =secretKeySpec;


    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);

    }





    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        Messages c = mMessageList.get(i);

        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid().toString();


        String from_user = c.getFrom();

        String message_type = c.getType();


        String dateString = new SimpleDateFormat("dd-MM-yy hh:mm a").format(new Date(c.getTime()));
        viewHolder.setDate(dateString);

//        Log.v("msg",c.getMessage());
        String x = " ";
        try {
            x = AESDecryptionMethod(c.getMessage());
            Log.v("enc",x);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        viewHolder.messageText.setText(x);
        viewHolder.displayName.setText(c.getFrom());


        if (from_user != null) {
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String name = dataSnapshot.child("name").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();

                    viewHolder.displayName.setText(name);


                    Picasso.get().load(image).placeholder(R.drawable.avatar).into(viewHolder.profileImage);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            if (message_type.equals("text")) {


                if (from_user != null) {
                    if (from_user.equals(current_user_id)) {
                        viewHolder.messageText.setBackgroundResource(R.drawable.message_send);
//                    viewHolder.messageText.setBackgroundColor(Color.GRAY);
                        viewHolder.messageText.setTextColor(Color.BLACK);

                    } else {
                        viewHolder.messageText.setBackgroundResource(R.drawable.message_receiver);
                        //  viewHolder.messageText.setBackgroundColor(R.drawable.gradient);
                        viewHolder.messageText.setTextColor(Color.WHITE);

                    }
                }

                viewHolder.messageText.setText(x);
                viewHolder.messageImage.setVisibility(View.INVISIBLE);


            } else {

                viewHolder.messageText.setVisibility(View.INVISIBLE);

                viewHolder.messageImage.setImageDrawable(null);
                viewHolder.setIsRecyclable(false);

               Picasso.get().load(c.getMessage()).placeholder(R.drawable.avatar).into(viewHolder.messageImage);


            }


        }

    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        private TextView mtime;

        public ImageView messageImage;
        View mView;

        public MessageViewHolder(View view) {
            super(view);
            mView = view;

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            displayName = view.findViewById(R.id.message_name);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);

            mtime = view.findViewById(R.id.message_timex);
            messageImage = (ImageView) view.findViewById(R.id.messagesendimageview);




        }


        public void setDate (String currentDateandTime){


            TextView userStatusView = (TextView) mView.findViewById(R.id.message_timex);
            userStatusView.setText(currentDateandTime);

        }
        }



    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    private String AESDecryptionMethod(String string) throws UnsupportedEncodingException {
        byte[] EncryptedByte = string.getBytes("ISO-8859-1");
        String decryptedString = string;

        byte[] decryption;

        try {
            decipher.init(cipher.DECRYPT_MODE, secretKeySpec);
            decryption = decipher.doFinal(EncryptedByte);
            decryptedString = new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptedString;
    }






}
