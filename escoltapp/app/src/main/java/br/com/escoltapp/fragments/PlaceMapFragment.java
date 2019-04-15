package br.com.escoltapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

import br.com.escoltapp.androidservices.GetInHomeService;
import br.com.escoltapp.androidservices.GetOutHomeService;
import br.com.escoltapp.entities.User;
import br.com.escoltapp.util.SharedPreferencesUtil;
import br.com.escoltapp.util.ToastUtil;
import br.com.scoltapp.R;


public class PlaceMapFragment extends Fragment implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_READ_LOCATION = 1;
    private static final double ONE_HUNDRED_METERS = 100;
    private GoogleMap map;

    private User user;
    private User.Place place;
    private Location lastKnownUserLocation;
    private Location userHomeLocation;
    private Boolean starting;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private FloatingActionButton goOutHomeFAB;
    private FloatingActionButton goInHomeFAB;
    private FloatingActionButton callEscoltFAB;

    private FrameLayout loadingLayout;
    private FrameLayout mainContentLayout;

    public PlaceMapFragment() {}

    public static PlaceMapFragment newInstance(User user, User.Place place) {
        PlaceMapFragment placeMapFragment = new PlaceMapFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        args.putParcelable("place", place);
        placeMapFragment.setArguments(args);
        return placeMapFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
            place = getArguments().getParcelable("place");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_place_map, container, false);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        FloatingActionButton goToCurrentPositionFAB = rootView.findViewById(R.id.goToCurrentPositionFAB);
        goToCurrentPositionFAB.setOnClickListener(onClickGoToCurrentPosition());

        loadingLayout = rootView.findViewById(R.id.loadingLayout);
        mainContentLayout = rootView.findViewById(R.id.mainContentLayout);

        goOutHomeFAB = rootView.findViewById(R.id.goOutHomeFAB);
        goOutHomeFAB.setOnClickListener(onClickGoOutHome());

        goInHomeFAB = rootView.findViewById(R.id.goInHomeFAB);
        goInHomeFAB.setOnClickListener(onClickGoInHome());

        callEscoltFAB = rootView.findViewById(R.id.callEscoltFAB);
        callEscoltFAB.setOnClickListener(onClickCallEscolt());

        starting = true;

        return rootView;
    }

    private View.OnClickListener onClickCallEscolt() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(getContext(),R.string.escolt_required);
            }
        };
    }

    private View.OnClickListener onClickGoInHome() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = Objects.requireNonNull(getActivity());
                Intent intent = new Intent(activity,GetInHomeService.class);
                intent.putExtra("user", user);
                intent.putExtra("place", place);
                activity.startService(intent);
                ToastUtil.show(activity,R.string.count_down_started);
            }
        };
    }

    private View.OnClickListener onClickGoOutHome() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = Objects.requireNonNull(getActivity());
                Intent intent = new Intent(activity,GetOutHomeService.class);
                intent.putExtra("user", user);
                intent.putExtra("place", place);
                activity.startService(intent);
                ToastUtil.show(activity,R.string.count_down_started);
            }
        };
    }

    private View.OnClickListener onClickGoToCurrentPosition() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastKnownUserLocation != null) {
                    LatLng location = new LatLng(lastKnownUserLocation.getLatitude(),lastKnownUserLocation.getLongitude());
                    updateCamera(location);
                }
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        Activity activity = Objects.requireNonNull(getActivity());

        userHomeLocation = new Location(LocationManager.GPS_PROVIDER);
        userHomeLocation.setLatitude(place.latitude);
        userHomeLocation.setLongitude(place.longitude);

        Double lastKnownLatitude = SharedPreferencesUtil.getDouble(activity,"lastKnownLatitude");
        Double lastKnownLongitude= SharedPreferencesUtil.getDouble(activity,"lastKnownLongitude");
        if (lastKnownLatitude != -1 && lastKnownLongitude != -1) {
            LatLng lastKnownLocation = new LatLng(lastKnownLatitude,lastKnownLongitude);
            lastKnownUserLocation = new Location(LocationManager.GPS_PROVIDER);
            lastKnownUserLocation.setLatitude(lastKnownLatitude);
            lastKnownUserLocation.setLongitude(lastKnownLongitude);
            updateCamera(lastKnownLocation);
        }

        LatLng userHomeLatLng = new LatLng(place.latitude, place.longitude);

        map.addMarker(new MarkerOptions()
                .title(getString(R.string.my_home))
                .position(userHomeLatLng));

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(userHomeLatLng);
        circleOptions.radius(ONE_HUNDRED_METERS);
        circleOptions.fillColor(getResources().getColor(R.color.clearColorHome, activity.getTheme()));
        circleOptions.strokeWidth(2);

        map.addCircle(circleOptions);

        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationListener = createLocationListener();

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_READ_LOCATION);
        } else {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        loadingLayout.setVisibility(View.GONE);
        mainContentLayout.setVisibility(View.VISIBLE);
    }

    private void updateCamera(LatLng location) {
        if(this.map != null) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location,17.5f);
            map.moveCamera(update);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }

    private LocationListener createLocationListener() {
        return new LocationListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onLocationChanged(Location location) {
                if (starting) {
                    LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                    updateCamera(userLocation);
                    starting = false;
                }

                lastKnownUserLocation = location;
                if(lastKnownUserLocation.distanceTo(userHomeLocation) < ONE_HUNDRED_METERS) {
                    goOutHomeFAB.setVisibility(View.VISIBLE);
                    goInHomeFAB.setVisibility(View.GONE);
                } else {
                    goOutHomeFAB.setVisibility(View.GONE);
                    goInHomeFAB.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
    }

    @Override
    public void onStop() {
        super.onStop();

        if(lastKnownUserLocation != null) {
            SharedPreferencesUtil.putDouble(Objects.requireNonNull(getContext()),
                    "lastKnownLatitude", lastKnownUserLocation.getLatitude());
            SharedPreferencesUtil.putDouble(Objects.requireNonNull(getContext()),
                    "lastKnownLongitude", lastKnownUserLocation.getLongitude());
        }
    }
}
