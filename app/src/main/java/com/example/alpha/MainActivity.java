package com.example.alpha;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;
    private FirebaseUser currentuser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser();
        if (currentuser != null) {
            startActivity(new Intent(MainActivity.this, Chat.class));
        }
        mToolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Alpha");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentuser == null) {
            SendUserToLoginActivity();
        }
    }

    public void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
}