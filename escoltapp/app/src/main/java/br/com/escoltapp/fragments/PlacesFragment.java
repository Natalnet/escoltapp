package br.com.escoltapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.escoltapp.activities.AddPlaceActivity;
import br.com.escoltapp.adapters.PlacesRecyclerViewAdapter;
import br.com.escoltapp.entities.User;
import br.com.scoltapp.R;


public class PlacesFragment extends Fragment {

    private static final String ARG_PARAM1 = "user";
    private static final int ADD_PLACE_REQUEST = 1;

    private PlacesRecyclerViewAdapter placesRecyclerViewAdapter;

    private User user;

    public PlacesFragment() {}

    public static PlacesFragment newInstance(User user) {
        PlacesFragment fragment = new PlacesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places, container, false);

        RecyclerView placesRecyclerView = rootView.findViewById(R.id.placesRecyclerView);
        placesRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        placesRecyclerView.setLayoutManager(layoutManager);

        placesRecyclerViewAdapter = new PlacesRecyclerViewAdapter(getContext(),user);
        placesRecyclerView.setAdapter(placesRecyclerViewAdapter);

        FloatingActionButton addPlaceFAB = rootView.findViewById(R.id.addPlaceFAB);
        addPlaceFAB.setOnClickListener(onClickAddPlace());

        return rootView;
    }

    private View.OnClickListener onClickAddPlace() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),AddPlaceActivity.class);
                intent.putExtra("user",user);
                startActivityForResult(intent,ADD_PLACE_REQUEST);
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == ADD_PLACE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                User.Place place = intent.getParcelableExtra("place");
                user.getPlaces().add(place);
                placesRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

}
