package br.com.escoltapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import br.com.escoltapp.entities.User;
import br.com.escoltapp.fragments.PlaceMapFragment;
import br.com.scoltapp.R;

public class PlaceMapActivity extends AppCompatActivity {

    private PlaceMapFragment placeMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_map);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            User user = (User) bundle.get("user");
            User.Place place = getIntent().getParcelableExtra("place");
            placeMapFragment = PlaceMapFragment.newInstance(user,place);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragLayout, placeMapFragment).commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        placeMapFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
