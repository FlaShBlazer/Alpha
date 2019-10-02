package com.example.alpha;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alpha.Model.Requests;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestFragment extends Fragment {

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private RecyclerView mRequestsLists;
    private View mRequestView;
    private FirebaseAuth mAuth;
    private DatabaseReference mRequestsDatabase, mUsersDatabase;
    private String mCurrent_user_id;

    public FriendRequestFragment() {
        // Constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRequestView = inflater.inflate(R.layout.fragment_friend_request, container, false);


        mAuth = FirebaseAuth.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(mRequestView.getContext());
        String id = sp.getString("UID", "");
        if (!id.equalsIgnoreCase(""))
            mCurrent_user_id = id;
        mRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("Friend Req").child(mCurrent_user_id);
        mRequestsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);


        mRequestsLists = mRequestView.findViewById(R.id.requests_list);
        mRequestsLists.setHasFixedSize(true);
        mRequestsLists.setLayoutManager(new LinearLayoutManager(getContext()));

        return mRequestView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Requests> options = new FirebaseRecyclerOptions.Builder<Requests>()
                .setQuery(mRequestsDatabase, Requests.class).build();

        FirebaseRecyclerAdapter<Requests, FriendRequestFragment.RequestsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Requests, FriendRequestFragment.RequestsViewHolder>(options) {
                    @NonNull
                    @Override
                    public FriendRequestFragment.RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_request_layout, viewGroup, false);
                        FriendRequestFragment.RequestsViewHolder viewHolder = new FriendRequestFragment.RequestsViewHolder(view);
                        return viewHolder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final FriendRequestFragment.RequestsViewHolder requestsViewHolder, int position, @NonNull final Requests requests) {
                        //friendsViewHolder.friendDate.setText(friends.getDate());
                        //usersViewHolder.profileIv.//Image Loading Needs Picasso library

                        final String list_user_ID = getRef(position).getKey();

                        mUsersDatabase.child(list_user_ID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String userName = dataSnapshot.child("FullName").getValue().toString();
                                String email = dataSnapshot.child("Email").getValue().toString();

                                requestsViewHolder.requestName.setText(userName);
                                requestsViewHolder.requestEmail.setText(email);
                                //friendsViewHolder.setEmail(email);
                                Picasso.get().load(dataSnapshot.child("Image").getValue().toString()).placeholder(R.drawable.ic_profile).into(requestsViewHolder.requestPic);
                                requestsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent requestIntent = new Intent(getContext(), ProfileRequestActivity.class);
                                        requestIntent.putExtra("user_id", list_user_ID);
                                        startActivity(requestIntent);
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                };

        mRequestsLists.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    public static class RequestsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView requestEmail, requestName;
        CircleImageView requestPic;

        public RequestsViewHolder(View itemView) {
            super(itemView);

            requestName = itemView.findViewById(R.id.requests_single_name);
            requestEmail = itemView.findViewById(R.id.requests_single_email);
            requestPic = itemView.findViewById(R.id.requests_single_image);
            mView = itemView;

        }
    }


}
