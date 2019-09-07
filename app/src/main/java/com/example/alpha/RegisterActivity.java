package com.example.alpha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private Button RegisterButton, AlreadyAccountButton;
    private EditText Email, FullName, RollNo, Class, Password;
    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        InitializeFields();

        AlreadyAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToLoginActivity();
            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount() {
        final String email = Email.getText().toString();
        final String name = FullName.getText().toString();
        final String roll = RollNo.getText().toString();
        final String class1 = Class.getText().toString();
        String password = Password.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter Email...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter Name...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(roll)) {
            Toast.makeText(this, "Enter Roll No...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(class1)) {
            Toast.makeText(this, "Enter Class...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter Password...", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait while we are creating new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String currentUserID = mAuth.getCurrentUser().getUid();
                        Map newPost = new HashMap();
                        newPost.put("FullName", name);
                        newPost.put("Email", email);
                        newPost.put("Roll NO", roll);
                        newPost.put("Class", class1);
                        rootRef.child("Users").child(currentUserID).setValue(newPost);
                        SendUserToLoginActivity();
                        Toast.makeText(RegisterActivity.this, "Account Created Successfully!!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }

    }

    private void InitializeFields() {
        AlreadyAccountButton = findViewById(R.id.alreadyaccount);
        RegisterButton = findViewById(R.id.register);
        Email = findViewById(R.id.r1);
        FullName = findViewById(R.id.r2);
        RollNo = findViewById(R.id.r3);
        Class = findViewById(R.id.r4);
        Password = findViewById(R.id.r5);

        loadingBar = new ProgressDialog(this);
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
}
