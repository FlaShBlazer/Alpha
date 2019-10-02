package com.example.alpha;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
public class RegisterActivity extends AppCompatActivity {
    private static final int GALLERY_INTENT = 2;
    private Button RegisterButton, AlreadyAccountButton;
    private final int PICK_IMAGE_REQUEST = 71;
    private EditText Email, FullName, RollNo, Class, Password;
    private FirebaseAuth mAuth;
    Map newPost;
    private ProgressDialog loadingBar;
    Task<Uri> dl;
    Uri u;
    String a1;
    StorageReference mstorage;
    FirebaseStorage storage;
    StorageReference storageReference, ref;
    private ImageView upload;
    private DatabaseReference rootRef, rf;
    private Button btnChoose, btnUpload;
    private ImageView imageView;
    private Uri filePath;

    public static void startWithUri(@NonNull Context context, @NonNull Uri uri) {
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.setData(uri);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        u = getIntent().getData();
        mAuth = FirebaseAuth.getInstance();
        rf = rootRef = FirebaseDatabase.getInstance().getReference();
        rf.keepSynced(true);
        rootRef.keepSynced(true);
        storage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("Image/");
        InitializeFields();
        AlreadyAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToLoginActivity();
            }
        });
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "Please select profile picture!", Toast.LENGTH_SHORT).show();
            }
        });
        loadingBar = new ProgressDialog(this);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, SampleActivity.class));
            }
        });
        if (u != null) {
            upload.setImageURI(u);
            check(u);
        }
    }

    protected void check(final Uri u1) {
        if (u1 != null)
            upload.setImageURI(u1);
        final Uri u = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/ProjectAlphaX" + ".jpg"));
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = Email.getText().toString();

                final String name = FullName.getText().toString();
                final String roll = RollNo.getText().toString();
                final String class1 = Class.getText().toString();
                final String password = Password.getText().toString();
                final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
                ref1.child("Users").equalTo(RollNo.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            Log.e("check", email);
                            if (TextUtils.isEmpty(email)) {
                                Toast.makeText(RegisterActivity.this, "Enter Email...", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(name)) {
                                Toast.makeText(RegisterActivity.this, "Enter Name...", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(roll)) {
                                Toast.makeText(RegisterActivity.this, "Enter Roll No...", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(class1)) {
                                Toast.makeText(RegisterActivity.this, "Enter Class...", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(password)) {
                                Toast.makeText(RegisterActivity.this, "Enter Password...", Toast.LENGTH_SHORT).show();
                            } else {
                                loadingBar.setTitle("Creating New Account");
                                loadingBar.setMessage("Please wait while we are creating new account for you...");
                                loadingBar.setCanceledOnTouchOutside(true);
                                loadingBar.show();
                                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            final StorageReference filepath = storageReference.child(mAuth.getCurrentUser().getUid() + ".jpg");
                                            filepath.putFile(u).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(RegisterActivity.this, "Profile Image uploaded Successfully...", Toast.LENGTH_SHORT).show();
                                                        //FROM Here onwards your download URL loading will take place
                                                        final String downloadUrl = String.valueOf(task.getResult().getStorage().getDownloadUrl());
                                                        newPost = new HashMap();
                                                        newPost.put("FullName", name);
                                                        newPost.put("Email", email);
                                                        newPost.put("Roll NO", roll);
                                                        newPost.put("Class", class1);
                                                        rootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(newPost);
                                                        rootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Image")
                                                                .setValue(downloadUrl)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            loadingBar.dismiss();
                                                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                                        } else {
                                                                            String message = task.getException().toString();
                                                                            Toast.makeText(RegisterActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                                            loadingBar.dismiss();
                                                                        }
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        });
                                                    }
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            String url = uri.toString();
                                                            rootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Image").setValue(url);
                                                            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/ProjectAlphaX" + ".jpg");
                                                            boolean d = f.delete();
                                                            if (d)
                                                                Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    });
                                                }
                                            });
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Username Already exist !! Please select another Username", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
        });
    }
    private void InitializeFields() {
        AlreadyAccountButton = findViewById(R.id.alreadyaccount);
        RegisterButton = findViewById(R.id.register);
        Email = findViewById(R.id.r1);
        FullName = findViewById(R.id.r2);
        RollNo = findViewById(R.id.r3);
        Class = findViewById(R.id.r4);
        Password = findViewById(R.id.r5);
        upload = findViewById(R.id.profButton);
        loadingBar = new ProgressDialog(RegisterActivity.this);
    }
    private void SendUserToLoginActivity() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }
}