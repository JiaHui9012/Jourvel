package com.example.mdpcwjourvel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {

    //firebase auth
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //action bar
    ActionBar actionBar;

    //user info
    String username, email, uid;

    //views
    //TextView profileTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Actionbar and its title
        actionBar = getSupportActionBar();

        //init
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        checkUserStatus();

        //init views
        //profileTV = findViewById(R.id.profileTextView);

        //bottom navigation
        BottomNavigationView navigationView = findViewById(R.id.bottomNavigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        //home fragment transaction (default)
        actionBar.setTitle("Home");
        HomeFragment fragmentH =  new HomeFragment();
        FragmentTransaction fragmentTransactionH = getSupportFragmentManager().beginTransaction();
        fragmentTransactionH.replace(R.id.content, fragmentH, "");
        fragmentTransactionH.commit();

        //get current user info from database
        Query query =  databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check until required data get
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    username = "" + ds.child("username").getValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =  new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //handle item clicks
            switch(item.getItemId()) {
                case R.id.home_nav:
                    //home fragment transaction
                    actionBar.setTitle("Home");
                    HomeFragment fragmentH =  new HomeFragment();
                    FragmentTransaction fragmentTransactionH = getSupportFragmentManager().beginTransaction();
                    fragmentTransactionH.replace(R.id.content, fragmentH, "");
                    fragmentTransactionH.commit();
                    return true;
                case R.id.search_nav:
                    //search fragment transaction
                    actionBar.setTitle("Search");
                    SearchFragment fragmentS =  new SearchFragment();
                    FragmentTransaction fragmentTransactionS = getSupportFragmentManager().beginTransaction();
                    fragmentTransactionS.replace(R.id.content, fragmentS, "");
                    fragmentTransactionS.commit();
                    return true;
                case R.id.profile_nav:
                    //profile fragment transaction
                    actionBar.setTitle(username);
                    ProfileFragment fragmentP =  new ProfileFragment();
                    FragmentTransaction fragmentTransactionP = getSupportFragmentManager().beginTransaction();
                    fragmentTransactionP.replace(R.id.content, fragmentP, "");
                    fragmentTransactionP.commit();
                    return true;
                case R.id.camera_nav:
                    //camera fragment transaction
                    actionBar.setTitle("Camera");
                    CameraFragment fragmentC =  new CameraFragment();
                    FragmentTransaction fragmentTransactionC = getSupportFragmentManager().beginTransaction();
                    fragmentTransactionC.replace(R.id.content, fragmentC, "");
                    fragmentTransactionC.commit();
                    return true;
                case R.id.message_nav:
                    //message fragment transaction
                    actionBar.setTitle("Messages");
                    MessageFragment fragmentM =  new MessageFragment();
                    FragmentTransaction fragmentTransactionM = getSupportFragmentManager().beginTransaction();
                    fragmentTransactionM.replace(R.id.content, fragmentM, "");
                    fragmentTransactionM.commit();
                    return true;
            }

            return false;
        }
    };

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
            //user is signed in stay here
            email = user.getEmail();
            uid = user.getUid();
        }
        else {
            //user not signed in, go to main activity
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        checkUserStatus();
        //finish();
    }

    @Override
    protected void onStart() {
        //check on start of app
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    /*//inflate options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflating menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    /*//handle menu item click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        if(id ==R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }*/
}