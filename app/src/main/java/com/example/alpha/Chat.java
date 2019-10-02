package com.example.alpha;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public MainActivity a;
    TextView tmail, tnm;
    NavigationView navigationView;
    ColorStateList colorStateList;
    CircleImageView img;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private DatabaseReference RootRef;
    private String currentuser;
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView tvInfo, emailTv, fullNameTv;
    private ViewPager mViewPager;
    FloatingActionButton fab;
    private FirebaseAuth mauth;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;
    private CircleImageView picIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_1);
        mauth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.keepSynced(true);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Project Alpha X");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        currentuser = mauth.getCurrentUser().getUid();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        editor.putString("UID", currentuser);
        editor.apply();
        InitializeFields();
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Chat.this, ProfileActivity.class);
                startActivity(i);
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        mViewPager = findViewById(R.id.tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setTabTextColors(colorStateList);
        mTabLayout.setupWithViewPager(mViewPager);
        RetrieveUserInfo();
    }

    private void InitializeFields() {
        tnm = navigationView.getHeaderView(0).findViewById(R.id.t1);
        tmail = navigationView.getHeaderView(0).findViewById(R.id.t2);
        img = navigationView.getHeaderView(0).findViewById(R.id.imageView1);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            mauth.signOut();
            startActivity(new Intent(Chat.this, LoginActivity.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        RootRef.child("online").setValue("true");
        VerifyUserExistance();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentuser != null) {
            RootRef.child("online").setValue("false");
            mauth.signOut();
        }

    }

    private void VerifyUserExistance() {
        RootRef.child("Users").child(currentuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("FullName").exists()))
                    startActivity(new Intent(Chat.this, RegisterActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this, R.style.AlertDialog);
        builder.setTitle("Enter Group name");

        final EditText groupNameField = new EditText(Chat.this);
        groupNameField.setHint("e.g Coding ");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(Chat.this, "Enter Group Name", Toast.LENGTH_SHORT).show();
                } else {
                    CreateNewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void CreateNewGroup(final String groupName) {
        RootRef.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Chat.this, groupName + " is created successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void Logout() {
        mAuth.signOut();
        finish();
        SendUserToLoginActivity();
    }

    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);

    }

    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }


    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(Chat.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToProfileActivity() {
        Intent profileIntent = new Intent(Chat.this, ProfileActivity.class);
        //profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileIntent);
        //finish();
    }

    private void RetrieveUserInfo() {
        String currentUserID = mauth.getCurrentUser().getUid();
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("FullName"))) {
                    String retrieveEmail = dataSnapshot.child("Email").getValue().toString();
                    String retrieveName = dataSnapshot.child("FullName").getValue().toString();
                    String url = dataSnapshot.child("Image").getValue().toString();
                    Picasso.get().load(url).into(img);
                    tmail.setText(retrieveEmail);
                    tnm.setText(retrieveName);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mauth = FirebaseAuth.getInstance();
        FirebaseUser user = mauth.getCurrentUser();
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Profile) {
            //   startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_friends) {
            startActivity(new Intent(this, UserActivity.class));
        } else if (id == R.id.nav_contacts) {
            //          startActivity(new Intent(this,Friends.class));
        } else if (id == R.id.nav_files) {
            //      startActivity(new Intent(this,File.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_logout) {
            mauth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.nav_groups) {
            RequestNewGroup();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
