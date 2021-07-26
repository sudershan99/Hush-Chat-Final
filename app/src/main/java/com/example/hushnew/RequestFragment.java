package com.example.hushnew;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hush.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestFragment newInstance(String param1, String param2) {
        RequestFragment fragment = new RequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    private RecyclerView mRequestList;

    private DatabaseReference mRequestDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;



    // method to inflate the fragment with the specified fragment layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mMainView = inflater.inflate(R.layout.fragment_request, container, false);

        mRequestList =  mMainView.findViewById(R.id.request_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mRequestDatabase.keepSynced(true);



        mRequestList.setHasFixedSize(true);
        mRequestList.setLayoutManager(new LinearLayoutManager(getContext()));


        return mMainView;




    }


    @Override
    public void onStart() {
        super.onStart();


        /**
         setting up a query to fetch all the requests of the current user
         **/

        Query query = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
        query.keepSynced(true);

        /**
         setting up the adapter
         **/
        FirebaseRecyclerOptions<Request> options =
                new FirebaseRecyclerOptions.Builder<Request>()
                        .setQuery(query, Request.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Request, RequestViewHolder>(options) {

            @Override
            public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                /**
                 creating a run time view from the single layout xml file and passing it to onbindviewholder
                 **/
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new RequestViewHolder(view);
            }

            /**
             setting up all the details in the singlelayout file
             **/
            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder requestViewHolder, int i, @NonNull Request model) {
                //requestViewHolder.setDate(model.getDate());

                String user_id = getRef(i).getKey();
                String type = model.getRequest_type();
                mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
                        String username = snapshot.child("name").getValue().toString();
                        String status = snapshot.child("status").getValue().toString();
                        String thumb_image = snapshot.child("image").getValue().toString();

                        requestViewHolder.setName(username);
                        requestViewHolder.setUserImage(thumb_image);

                        if(type.equals("sent"))
                            requestViewHolder.setType("SENT");

                        else
                            requestViewHolder.setType("NEW FRIEND REQUEST!");
//

                        requestViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent profileIntents = new Intent(getActivity(), ProfileActivity.class);
                                profileIntents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                profileIntents.putExtra("testid", user_id);
                                startActivity(profileIntents);

                            }
                        });



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        };

        /**
         setting up the adapter to start listening to realtime changes
         **/
        mRequestList.setAdapter(adapter);
        adapter.startListening();



    }


    /**
     This class saves all the references of different items from the single layout file
     **/

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public RequestViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }



        public void setName(String name) {

            TextView userNameView = (TextView) mView.findViewById(R.id.name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.circleimg);
            Picasso.get().load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar).into(userImageView);


        }


        public void setType(String type) {


            TextView typeview = mView.findViewById(R.id.status_);
            typeview.setText(type);
            if(type.equals("NEW FRIEND REQUEST!"))
            typeview.setTypeface(typeview.getTypeface(), Typeface.BOLD);
        }
    }



}