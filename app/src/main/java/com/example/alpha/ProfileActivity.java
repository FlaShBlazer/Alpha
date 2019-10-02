package com.example.alpha;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.alpha.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    User user;
    private FirebaseAuth mAuth;
    StorageReference ref, ref1;
    private DatabaseReference RootRef;
    Button Delete1, save1;
    CircleImageView v;
    private TextView sEmail, Email, FullName, RollNo, Class;
    private String retrieveEmail, retrieveName, retrieveClass, retrieveRoll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        InitializeFields();
        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        ref = FirebaseStorage.getInstance().getReference();
        ref1 = ref.child("/Image/");
        ActionBar actionBar = this.getSupportActionBar();
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Delete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        RetrieveUserInfo();
        setTitle("Profile");
    }

    protected void onStart() {

        super.onStart();

        FullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save1.setVisibility(View.VISIBLE);
            }
        });

        Email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save1.setVisibility(View.VISIBLE);
            }
        });

        Class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save1.setVisibility(View.VISIBLE);
            }
        });

        RollNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save1.setVisibility(View.VISIBLE);
            }
        });


        save1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profileChanged()) {
                    Toast.makeText(ProfileActivity.this, "In Save...", Toast.LENGTH_SHORT).show();
                    Map newPost = new HashMap();
                    newPost.put("FullName", FullName.getText().toString());
                    newPost.put("Email", Email.getText().toString());
                    newPost.put("Roll NO", RollNo.getText().toString());
                    newPost.put("Class", Class.getText().toString());
                    newPost.put("Image", "default");
                    newPost.put("UserID", mAuth.getUid());
                    RootRef.child("Users").child(mAuth.getUid()).setValue(newPost);
                    Toast.makeText(ProfileActivity.this, "Profile Updated Successfully...", Toast.LENGTH_SHORT).show();
                }
            }

            private boolean profileChanged() {
                if (retrieveName.equalsIgnoreCase(FullName.getText().toString().trim())) {
                    return true;
                } else if (retrieveEmail.equalsIgnoreCase(Email.getText().toString().trim())) {
                    return true;
                } else if (retrieveClass.equalsIgnoreCase(Class.getText().toString().trim())) {
                    return true;
                } else return retrieveRoll.equalsIgnoreCase(RollNo.getText().toString().trim());
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void InitializeFields() {
        Email = findViewById(R.id.p2);
        FullName = findViewById(R.id.p1);
        RollNo = findViewById(R.id.p4);
        Class = findViewById(R.id.p5);
        sEmail = findViewById(R.id.p3);
        v = findViewById(R.id.imgProf);
        save1 = findViewById(R.id.s);
        Delete1 = findViewById(R.id.d);
    }

    String getUser() {
        return mAuth.getCurrentUser().getUid();
    }

    private void RetrieveUserInfo() {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("FullName"))) {
                    retrieveEmail = dataSnapshot.child("Email").getValue().toString();
                    retrieveName = dataSnapshot.child("FullName").getValue().toString();
                    retrieveClass = dataSnapshot.child("Class").getValue().toString();
                    retrieveRoll = dataSnapshot.child("Roll NO").getValue().toString();
                    Picasso.get().load(dataSnapshot.child("Image").getValue().toString()).placeholder(R.drawable.ic_profile).into(v);
                    Email.setText(retrieveEmail);
                    sEmail.setText(retrieveEmail);
                    FullName.setText(retrieveName);
                    Class.setText(retrieveClass);
                    RollNo.setText(retrieveRoll);
                } else {
                    //Display Error Message
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
