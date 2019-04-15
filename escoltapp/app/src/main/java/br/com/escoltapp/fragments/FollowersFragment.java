package br.com.escoltapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.List;

import br.com.escoltapp.adapters.FollowRecyclerViewAdapter;
import br.com.escoltapp.entities.User;
import br.com.escoltapp.entityservices.UserService;
import br.com.scoltapp.R;


public class FollowersFragment extends Fragment {

    private User currentUser;

    private FollowRecyclerViewAdapter followRecyclerViewAdapter;
    private RecyclerView followersRecyclerView;
    private FrameLayout loadingLayout;
    private LinearLayout mainContentLayout;

    public FollowersFragment() {}

    public static FollowersFragment newInstance(User currentUser) {
        FollowersFragment mapFragment = new FollowersFragment();
        Bundle args = new Bundle();
        args.putParcelable("currentUser", currentUser);
        mapFragment.setArguments(args);
        return mapFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = getArguments().getParcelable("currentUser");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_followers, container, false);

        followersRecyclerView = rootView.findViewById(R.id.followersRecyclerView);
        followersRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        followersRecyclerView.setLayoutManager(layoutManager);

        loadingLayout = rootView.findViewById(R.id.loadingLayout);
        mainContentLayout = rootView.findViewById(R.id.mainContentLayout);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        UserService.readFollowers(getContext(),currentUser.getId(), new UserService.OnReadFollow() {
            @Override
            public void onSuccess(List<User> followers) {
                followRecyclerViewAdapter = new FollowRecyclerViewAdapter(followers);
                followersRecyclerView.setAdapter(followRecyclerViewAdapter);

                loadingLayout.setVisibility(View.GONE);
                mainContentLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure() {
                int a = 1;
            }
        });
    }
}
