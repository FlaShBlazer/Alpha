package com.example.alpha;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alpha.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class UserActivity extends AppCompatActivity {
    private SearchView mySearchView;
    FirebaseAuth mAuth;
    RecyclerView usersView;
    User user;
    String currentUserID;
    private Toolbar mToolbar;
    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        mAuth = FirebaseAuth.getInstance();

        ActionBar actionBar = this.getSupportActionBar();
        Toolbar mToolbar = findViewById(R.id.allToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("Users");
        setTitle("Users");
        currentUserID = "";
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mySearchView = (SearchView) findViewById(R.id.searchView);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        usersView = findViewById(R.id.list_view);
        mUsersList = (RecyclerView) findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, R.layout.user_info, R.id.userInfo, list);
        mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // adapter.getFilter().filter(s);
                Toast.makeText(UserActivity.this, "Is to be Implemented soon", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onSupportNavigateUp();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //final String url;
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(UserActivity.this, LoginActivity.class));
        } else {
            currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(mUsersDatabase, User.class).build();

        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<User, UsersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final UsersViewHolder usersViewHolder, final int position, @NonNull User user) {
                        usersViewHolder.email.setText(user.getEmail());
                        usersViewHolder.fullName.setText(user.getFullName());
                        Picasso.get().load(user.getImage()).placeholder(R.drawable.ic_profile).into(usersViewHolder.profileIv);
                        Log.e("HERE", getRef(position).getKey());
                        Log.e("THIS", currentUserID);
                        usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent requestintent = new Intent(UserActivity.this, ProfileRequestActivity.class);
                                requestintent.putExtra("user_id", getRef(position).getKey());
                                requestintent.putExtra("this_user", currentUserID);
                                startActivity(requestintent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_single_layout, viewGroup, false);
                        UsersViewHolder viewHolder = new UsersViewHolder(view);
                        return viewHolder;
                    }
                };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView email, fullName;
        CircleImageView profileIv;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.users_single_email);
            fullName = itemView.findViewById(R.id.users_single_name);
            profileIv = itemView.findViewById(R.id.users_single_image);
            mView = itemView;
        }
    }
}
