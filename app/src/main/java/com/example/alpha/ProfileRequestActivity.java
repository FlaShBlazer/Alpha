package com.example.alpha;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

// 0 -not friends 1 -req sent 2- req received 3-friends
public class ProfileRequestActivity extends AppCompatActivity {
    CircleImageView c;
    private String currentUserName;
    private TextView FullName, Email;
    private Button mSendRequestButton, mDeclineRequestButton;
    private DatabaseReference mUsersDatabase;
    private ProgressDialog mProgressDialog;
    private int mCurrent_state;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mRootRef;
    private FirebaseUser mCurrent_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_request);


        final String user_ID = getIntent().getStringExtra("user_id");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_ID);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend Req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friend");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        ActionBar actionBar = this.getSupportActionBar();
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle("Make some friends");
        mToolbar.setTitle("Make some friends");
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        InitializeFields();

        mCurrent_state = 0;
        mDeclineRequestButton.setVisibility(View.INVISIBLE);
        mDeclineRequestButton.setEnabled(false);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load user data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String fullName = dataSnapshot.child("FullName").getValue().toString();
                String email = dataSnapshot.child("Email").getValue().toString();

                FullName.setText(fullName);
                Email.setText(email);
                Picasso.get().load(dataSnapshot.child("Image").getValue().toString()).placeholder(R.drawable.ic_profile).into(c);

                // FRIENDS LIST / REQUEST FEATURE
                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_ID)) {
                            String req_type = dataSnapshot.child(user_ID).child("Request Type").getValue().toString();

                            if (req_type.equals("receive")) {
                                mCurrent_state = 2;
                                mSendRequestButton.setText("ACCEPT FRIEND REQUEST");

                                mDeclineRequestButton.setVisibility(View.VISIBLE);
                                mDeclineRequestButton.setEnabled(true);
                            } else if (req_type.equals("sent")) {
                                mCurrent_state = 1;
                                mSendRequestButton.setText("CANCEL FRIEND REQUEST");

                                mDeclineRequestButton.setVisibility(View.INVISIBLE);
                                mDeclineRequestButton.setEnabled(false);
                            }
                            mProgressDialog.dismiss();
                        } else {
                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_ID)) {

                                        mCurrent_state = 3; // 0 -not friends 1 -req sent 3-friends
                                        mSendRequestButton.setText("UNFRIEND");

                                        mDeclineRequestButton.setVisibility(View.INVISIBLE);
                                        mDeclineRequestButton.setEnabled(false);
                                    }
                                    mProgressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    mProgressDialog.dismiss();
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Button Disabled
                mSendRequestButton.setEnabled(false);

                // NOT FRIENDS STATE
                if (mCurrent_state == 0) {
                    DatabaseReference newNotificationref = mRootRef.child("Notifications").child(user_ID).push();
                    String newNotificationId = newNotificationref.getKey();


                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("From", mCurrent_user.getUid());
                    notificationData.put("Type", "Request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend Req/" + mCurrent_user.getUid() + "/" + user_ID + "/" + "Request Type", "sent");
                    requestMap.put("Friend Req/" + user_ID + "/" + mCurrent_user.getUid() + "/" + "Request Type", "receive");
                    requestMap.put("Notifications/" + user_ID + "/" + newNotificationId, notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError != null) {
                                Toast.makeText(ProfileRequestActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                            } else {
                                mCurrent_state = 1;
                                mSendRequestButton.setText("CANCEL FRIEND REQUEST");
                            }

                            mSendRequestButton.setEnabled(true);
                        }
                    });
                }

                // CANCEL REQUEST STATE
                if (mCurrent_state == 1) {
                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_ID).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendReqDatabase.child(user_ID).child(mCurrent_user.getUid())
                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mSendRequestButton.setEnabled(true);
                                            mCurrent_state = 0; // 0 -not friends 1 -req sent
                                            mSendRequestButton.setText("SEND FRIEND REQUEST");

                                            mDeclineRequestButton.setVisibility(View.INVISIBLE);
                                            mDeclineRequestButton.setEnabled(false);
                                        }
                                    });
                                }
                            });
                }

                // REQUEST RECEIVED STATE
                if (mCurrent_state == 2) {
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friend/" + mCurrent_user.getUid() + "/" + user_ID + "/date", currentDate);
                    friendsMap.put("Friend/" + user_ID + "/" + mCurrent_user.getUid() + "/date", currentDate);


                    friendsMap.put("Friend Req/" + mCurrent_user.getUid() + "/" + user_ID, null);
                    friendsMap.put("Friend Req/" + user_ID + "/" + mCurrent_user.getUid(), null);


                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if (databaseError == null) {

                                mSendRequestButton.setEnabled(true);
                                mCurrent_state = 3;
                                mSendRequestButton.setText("UNFRIEND");

                                mDeclineRequestButton.setVisibility(View.INVISIBLE);
                                mDeclineRequestButton.setEnabled(false);

                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileRequestActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

                //UNFRIEND
                if (mCurrent_state == 3) {

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friend/" + mCurrent_user.getUid() + "/" + user_ID, null);
                    unfriendMap.put("Friend/" + user_ID + "/" + mCurrent_user.getUid(), null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if (databaseError == null) {

                                mCurrent_state = 0;
                                mSendRequestButton.setText("SEND FRIEND REQUEST");

                                mDeclineRequestButton.setVisibility(View.INVISIBLE);
                                mDeclineRequestButton.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileRequestActivity.this, error, Toast.LENGTH_SHORT).show();


                            }

                            mSendRequestButton.setEnabled(true);

                        }
                    });
                }


            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void InitializeFields() {
        c = findViewById(R.id.imageView1);
        FullName = findViewById(R.id.request_fullname);
        Email = findViewById(R.id.Emailtv);
        mSendRequestButton = findViewById(R.id.request_button);
        mDeclineRequestButton = findViewById(R.id.request_decline);
    }
}
