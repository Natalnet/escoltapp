package br.com.escoltapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

import br.com.escoltapp.entities.User;
import br.com.escoltapp.entityservices.UserService;
import br.com.escoltapp.fragments.FollowersFragment;
import br.com.escoltapp.fragments.FollowingFragment;
import br.com.escoltapp.fragments.PlacesFragment;
import br.com.escoltapp.util.BitmapUtil;
import br.com.escoltapp.util.DialogUtil;
import br.com.escoltapp.util.NetworkUtil;
import br.com.escoltapp.util.SharedPreferencesUtil;
import br.com.scoltapp.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private User currentUser;

    private FrameLayout loadingLayout;
    private FrameLayout mainContentLayout;
    private PlacesFragment placesFragment;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadingLayout = findViewById(R.id.loadingLayout);
        mainContentLayout = findViewById(R.id.mainContentLayout);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        mAuth = FirebaseAuth.getInstance();

        activity = this;

        configureUser();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.main, menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_start) {
            placesFragment = PlacesFragment.newInstance(currentUser);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragLayout, placesFragment).commit();
        } else if (id == R.id.nav_followers) {
            FollowersFragment followersFragment = FollowersFragment.newInstance(currentUser);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragLayout, followersFragment).commit();
        } else if (id == R.id.nav_following) {
            FollowingFragment followingFragment = FollowingFragment.newInstance(currentUser);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragLayout, followingFragment).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void configureUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserService.read(getBaseContext(),user.getUid(), new UserService.OnReadListener() {
                @Override
                public void onSuccess(User user) {
                    String topic = user.getPhone().replaceAll("[+]","");
                    FirebaseMessaging.getInstance().subscribeToTopic(topic);
                    currentUser = user;
                    loadingLayout.setVisibility(View.GONE);
                    mainContentLayout.setVisibility(View.VISIBLE);

                    TextView userName = findViewById(R.id.userName);
                    final TextView userPhone = findViewById(R.id.userPhone);
                    final ProgressBar photoProgress = findViewById(R.id.photoProgress);
                    final ImageView userPhoto = findViewById(R.id.userPhoto);

                    userName.setText(String.format("%s %s",user.getFirstName(),user.getLastName()));
                    userPhone.setText(user.getPhone());

                    BitmapUtil.downloadImage(user.getPhotoUrl(), new BitmapUtil.OnFinishDownloadListener() {
                        @Override
                        public void onFinishDownload(Bitmap bitmap) {
                            photoProgress.setVisibility(View.GONE);
                            userPhoto.setImageBitmap(bitmap);
                            userPhoto.setVisibility(View.VISIBLE);
                        }
                    });

                    UserService.readFollowing(getBaseContext(), currentUser.getId(), new UserService.OnReadFollow() {
                        @Override
                        public void onSuccess(List<User> followingList) {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(currentUser.getPhone().substring(1));
                            for(User following : followingList) {
                                String topic = following.getPhone().substring(1);
                                if(SharedPreferencesUtil.getString(getBaseContext(),topic).isEmpty()) {
                                    FirebaseMessaging.getInstance().subscribeToTopic(topic);
                                    SharedPreferencesUtil.putString(getBaseContext(),topic,topic);
                                }
                            }

                            placesFragment = PlacesFragment.newInstance(currentUser);
                            getSupportFragmentManager().beginTransaction().
                                    replace(R.id.fragLayout, placesFragment).commit();
                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                }

                @Override
                public void onFailure() {
                    if(NetworkUtil.isNetworkConnected(getBaseContext())) {
                        //user authenticated but not registered yet
                        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        DialogUtil.showOkDialog(activity, R.string.error, R.string.no_network_message,null);
                    }
                }
            });
        } else {
            Intent intent = new Intent(this,SignInActivity.class);
            startActivity(intent);
        }
    }

}
