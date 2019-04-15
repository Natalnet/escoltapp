package br.com.escoltapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import java.util.Objects;

import br.com.escoltapp.adapters.FollowRecyclerViewAdapter;
import br.com.escoltapp.entities.User;
import br.com.escoltapp.entityservices.UserService;
import br.com.escoltapp.util.SharedPreferencesUtil;
import br.com.escoltapp.util.ToastUtil;
import br.com.scoltapp.R;


public class FollowingFragment extends Fragment {

    private User currentUser;

    private FollowRecyclerViewAdapter followRecyclerViewAdapter;
    private RecyclerView followingRecyclerView;
    private FrameLayout loadingLayout;
    private LinearLayout mainContentLayout;

    public FollowingFragment() {}

    public static FollowingFragment newInstance(User currentUser) {
        FollowingFragment mapFragment = new FollowingFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_following, container, false);

        followingRecyclerView = rootView.findViewById(R.id.followingRecyclerView);
        followingRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        followingRecyclerView.setLayoutManager(layoutManager);

        FloatingActionButton addFollowingFAB = rootView.findViewById(R.id.addFollowingFAB);
        addFollowingFAB.setOnClickListener(onClickAddFollowing());

        loadingLayout = rootView.findViewById(R.id.loadingLayout);
        mainContentLayout = rootView.findViewById(R.id.mainContentLayout);

        return rootView;
    }

    private View.OnClickListener onClickAddFollowing() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFollowingDialog newFragment = AddFollowingDialog.getInstance(currentUser);
                newFragment.setFollowRecyclerViewAdapter(followRecyclerViewAdapter);
                newFragment.show(Objects.requireNonNull(getFragmentManager()), "missiles");

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        UserService.readFollowing(getContext(),currentUser.getId(), new UserService.OnReadFollow() {
            @Override
            public void onSuccess(List<User> follow) {
                followRecyclerViewAdapter = new FollowRecyclerViewAdapter(follow);
                followingRecyclerView.setAdapter(followRecyclerViewAdapter);

                loadingLayout.setVisibility(View.GONE);
                mainContentLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure() {
                int a = 1;
            }
        });
    }

    public static class AddFollowingDialog extends DialogFragment {
        private User currentUser;
        private EditText phoneState;
        private EditText phoneNumber;
        private Context context;
        private FollowRecyclerViewAdapter followRecyclerViewAdapter;

        static AddFollowingDialog getInstance(User currentUser) {
            AddFollowingDialog addFollowingDialog = new AddFollowingDialog();
            Bundle bundle = new Bundle();
            bundle.putParcelable("currentUser",currentUser);
            addFollowingDialog.setArguments(bundle);
            return addFollowingDialog;
        }

        public void setFollowRecyclerViewAdapter(FollowRecyclerViewAdapter followRecyclerViewAdapter){
            this.followRecyclerViewAdapter = followRecyclerViewAdapter;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if(getArguments() != null) {
                this.currentUser = getArguments().getParcelable("currentUser");
            }

        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
            View rootView = inflater.inflate(R.layout.dialog_add_following, null);

            phoneState = rootView.findViewById(R.id.phoneState);
            phoneNumber = rootView.findViewById(R.id.phoneNumber);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            this.context = getContext();

            builder.setView(rootView)
            .setMessage(R.string.add)
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String state = phoneState.getText().toString();
                            String phone = phoneNumber.getText().toString();
                            if (state.isEmpty() || phone.isEmpty() ) {
                                ToastUtil.show(context,R.string.emptyFildsMessage);
                            } else {
                                final String phoneNumber = "+55" + state + phone;
                                final String topic = phoneNumber.substring(1);
                                UserService.addFollowing(context, currentUser.getId(), phoneNumber, new UserService.OnAddFollowing() {
                                    @Override
                                    public void onSuccess(User user) {
                                        followRecyclerViewAdapter.addUser(user);
                                        if(SharedPreferencesUtil.getString(context,topic).isEmpty()) {
                                            FirebaseMessaging.getInstance().subscribeToTopic(topic);
                                            SharedPreferencesUtil.putString(context,topic,topic);
                                        }
                                        ToastUtil.show(context,String.format(context.getString(R.string.following_user), phoneNumber));
                                    }

                                    @Override
                                    public void onFailure() {
                                        ToastUtil.show(context,String.format(context.getString(R.string.error_following_user), phoneNumber));
                                    }
                                });
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dismiss();
                        }
                    });
            return builder.create();
        }
    }
}
