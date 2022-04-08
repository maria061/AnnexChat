package com.example.chat2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainChatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ChatUser chatUser;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        Intent intent = getIntent();
        chatUser = (ChatUser)intent.getParcelableExtra("user");
        firebaseUser = intent.getParcelableExtra("firebaseUser");

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView chat_username = headerView.findViewById(R.id.chat_username);
        chat_username.setText(chatUser.getUsername());

        ImageButton imageButton = headerView.findViewById(R.id.btnLogout);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();

                Intent intent = new Intent(MainChatActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if(mAuth.getCurrentUser() == null){
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//        }
//
//    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", chatUser);
        bundle.putParcelable("firebaseUser", firebaseUser);
        switch (menuItem.getItemId()){
            case R.id.navigation_profile:
                ProfileFragment profileFrag = new ProfileFragment();
                profileFrag.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFrag).commit();
                break;
            case R.id.navigation_friends:
                FriendsFragment friendsFrag = new FriendsFragment();
                friendsFrag.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, friendsFrag).commit();
                break;
            case R.id.navigation_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
            case R.id.navigation_movies:
                MoviesFragment moviesFrag = new MoviesFragment();
                moviesFrag.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, moviesFrag).commit();
                break;
            case R.id.navigation_incomingRequests:
                IncomingPendingFragment incomingPendingFrag = new IncomingPendingFragment();
                incomingPendingFrag.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, incomingPendingFrag).commit();
                break;
            case R.id.navigation_favFriends:
                Intent intent = new Intent(getApplicationContext(), FavFriendsActivity.class);
                intent.putExtra("user", chatUser);
                intent.putExtra("firebaseUser", firebaseUser);
                startActivity(intent);
                //FavFriendsFragment favFriendsFragment = new FavFriendsFragment();
               // favFriendsFragment.setArguments(bundle);
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, favFriendsFragment).commit();
                break;
            case R.id.navigation_messagesRep:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesReportFragment()).commit();
                break;
            case R.id.navigation_friendsRep:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FriendsReportFragment()).commit();
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void setUsername(String username){
        View headerView = navigationView.getHeaderView(0);
        TextView chat_username = headerView.findViewById(R.id.chat_username);
        chat_username.setText(username);
    }
}
