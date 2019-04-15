package br.com.escoltapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import br.com.escoltapp.entities.User;
import br.com.escoltapp.entityservices.UserService;
import br.com.escoltapp.util.ToastUtil;
import br.com.scoltapp.R;

public class AddPlaceActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int PLACE_PICKER_REQUEST = 1;

    private GoogleMap map;
    private User user;

    private EditText label;
    private LatLng userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        user = getIntent().getParcelableExtra("user");

        userLocation = null;

        Button chooseLocationButton = findViewById(R.id.chooseLocationButton);
        chooseLocationButton.setOnClickListener(onClickChooseLocationButton());

        FloatingActionButton doneFAB = findViewById(R.id.doneFAB);
        doneFAB.setOnClickListener(onClickDoneFAB());

        label = findViewById(R.id.label);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
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

    private View.OnClickListener onClickDoneFAB() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String labelStr = label.getText().toString();
                if (labelStr.isEmpty()) {
                    ToastUtil.show(AddPlaceActivity.this,R.string.emptyFildsMessage);
                } else if (userLocation == null) {
                    ToastUtil.show(AddPlaceActivity.this,R.string.chooseHomeLocation);
                } else {
                    final User.Place place = new User.Place(labelStr,userLocation.latitude,userLocation.longitude);
                    UserService.addPlace(getBaseContext(),user.getId(),place, new UserService.OnAddPlace() {
                        @Override
                        public void onSuccess() {
                            Intent intent = new Intent();
                            intent.putExtra("place",place);
                            setResult(Activity.RESULT_OK,intent);
                            finish();
                        }

                        @Override
                        public void onFailure() {
                            setResult(Activity.RESULT_CANCELED);
                            finish();
                        }
                    });
                }
            }
        };
    }

    private View.OnClickListener onClickChooseLocationButton() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(AddPlaceActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        };

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(AddPlaceActivity.this, data);
                userLocation = place.getLatLng();
                map.addMarker(new MarkerOptions().position(userLocation));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        LatLng location = new LatLng(-15.4806067,-55.7588657); //Brasília coords
        map.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
}
