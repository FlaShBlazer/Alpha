package com.example.alpha;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView sEmail, RollNo, Class;
    private TextView Email, FullName;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        InitializeFields();
        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        ActionBar actionBar = this.getSupportActionBar();
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        RetrieveUserInfo();
        setTitle("Profile");
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
    }

    private void RetrieveUserInfo() {
        String currentUserID = mAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("FullName"))) {
                    String retrieveEmail = dataSnapshot.child("Email").getValue().toString();
                    String retrieveName = dataSnapshot.child("FullName").getValue().toString();
                    String retrieveClass = dataSnapshot.child("Class").getValue().toString();
                    String retrieveRoll = dataSnapshot.child("Roll NO").getValue().toString();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
