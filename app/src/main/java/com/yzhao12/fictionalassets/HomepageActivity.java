package com.yzhao12.fictionalassets;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yzhao12.fictionalassets.DataObjects.User;
import com.yzhao12.fictionalassets.Fragments.OpeningPageFrag;
import com.yzhao12.fictionalassets.Fragments.ProfilePageFrag;
import com.yzhao12.fictionalassets.Fragments.ProposePageFrag;

public class HomepageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initVariables();
        m_fmanager.beginTransaction().replace(R.id.frag_container, new OpeningPageFrag()).commit();

        ////////////////////////////////////////////////////////////////////////////////////////////////

        FirebaseUser currentUser = m_auth.getCurrentUser();
        if(currentUser != null) {
            m_ref = m_database.getReference("users");


            m_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    m_currentUser = dataSnapshot.getValue(User.class);
                    Log.d(TAG, "User is: " + m_currentUser);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        } else {
            Intent login = new Intent(this, LoginActivity.class);
            startActivityForResult(login, RC_LOGIN_ACTIVITY);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOGIN_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
                Log.wtf("zhao:", m_auth.getCurrentUser().getEmail());
                //finish();
            } else {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
                Intent login = new Intent(this, LoginActivity.class);
                startActivityForResult(login, RC_LOGIN_ACTIVITY);
            }
        }
    }


    private void initVariables() {
        m_fmanager = getSupportFragmentManager();
        m_auth = FirebaseAuth.getInstance();
        m_database = FirebaseDatabase.getInstance();
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.search_button, menu);
//
//        // Get the SearchView and set the searchable configuration
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        // Assumes current activity is the searchable activity
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName("com.yzhao12.fictionalassets", "SearchActivity.class")));
//        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
//
//        return true;
//    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment frag = null;

        if (id == R.id.nav_home) {
            frag = new OpeningPageFrag();
        } else if (id == R.id.nav_profile) {
            frag = new ProfilePageFrag();
        } else if (id == R.id.nav_vote) {
            frag = new ProposePageFrag();
        }
        m_fmanager.beginTransaction().replace(R.id.frag_container, frag).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void testPage(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }


    private FirebaseUser m_fireUser;
    private FragmentManager m_fmanager;
    private FirebaseAuth m_auth;
    private FirebaseAuth.AuthStateListener m_authListener;
    private User m_currentUser;
    private FirebaseDatabase m_database;
    private DatabaseReference m_ref;
    private static final String TAG = "USER INIT ONSTART";
    public static final int RC_LOGIN_ACTIVITY = 1;
}

