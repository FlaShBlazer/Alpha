package com.example.alpha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    RelativeLayout rellay1, rellay2;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        }
    };
    private ProgressDialog loadingBar;
    private FirebaseUser currentuser;
    private Button LoginButton, SignIn, ForgotPassword;
    private EditText Email, Password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rellay1 = findViewById(R.id.rellay1);
        rellay2 = findViewById(R.id.rellay2);

        handler.postDelayed(runnable, 1000); //1000 is delay for splash

        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser();
        if (currentuser != null) {
            startActivity(new Intent(LoginActivity.this, Chat.class));
        }
        InitializeFields();

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToRegisterActivity();
            }
        });
        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotActivity.class));
            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUserToLogin();
            }
        });

    }

    private void AllowUserToLogin() {
        String email = Email.getText().toString();
        String password = Password.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter Email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter Password...", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Logging In");
            loadingBar.setMessage("Please wait while we are logging account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        SendUserToChatActivity();
                        Toast.makeText(LoginActivity.this, "Logged in successfully!!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(LoginActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void InitializeFields() {

        LoginButton = findViewById(R.id.login_button);
        SignIn = findViewById(R.id.signin);
        ForgotPassword = findViewById(R.id.forgotpassword);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        loadingBar = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentuser != null)
            SendUserToChatActivity();
    }

    private void SendUserToChatActivity() {
        Intent chatIntent = new Intent(LoginActivity.this, Chat.class);
        startActivity(chatIntent);
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
