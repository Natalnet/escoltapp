package br.com.escoltapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.Objects;

import br.com.escoltapp.activities.SignInActivity;
import br.com.escoltapp.entities.User;
import br.com.escoltapp.util.SharedPreferencesUtil;
import br.com.escoltapp.util.ToastUtil;
import br.com.scoltapp.R;

public class UserInfoFragment extends Fragment /*implements OnMapReadyCallback*/ {

    private OnUserInfoFragmentInteractionListener onUserInfoFragmentInteractionListener;
    private FirebaseUser currentUser;

    private EditText userFirstName;
    private EditText userLastName;
    private ImageView profileImage;

    public UserInfoFragment() {}

    public static UserInfoFragment newInstance(FirebaseUser firebaseUser) {
        UserInfoFragment fragment = new UserInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable("firebaseUser", firebaseUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = getArguments().getParcelable("firebaseUser");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_info, container, false);

        userFirstName = rootView.findViewById(R.id.userFirstName);
        userLastName = rootView.findViewById(R.id.userLastName);

        setProfileImage((ImageView) rootView.findViewById(R.id.profileImage));

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhotoPicker();
            }
        });

        TextView changeProfileImage = rootView.findViewById(R.id.changeProfileImage);
        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhotoPicker();
            }
        });

        FloatingActionButton submmitProfile = rootView.findViewById(R.id.submmitProfile);
        submmitProfile.setOnClickListener(onClickSubmmitProfile());

        return rootView;
    }



    public void openPhotoPicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(Objects.requireNonNull(getActivity()));
    }

    private View.OnClickListener onClickSubmmitProfile() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    //something went wrong and user could not be retrieved. need to signin
                    Intent intent = new Intent(getActivity(),SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {

                    final String userFirstNameStr = userFirstName.getText().toString();
                    final String userLastNameStr = userLastName.getText().toString();
                    if (userFirstNameStr.isEmpty() || userLastNameStr.isEmpty()) {
                        ToastUtil.show(getContext(),R.string.emptyFildsMessage);
                    } else {
                        if (onUserInfoFragmentInteractionListener != null) {
                            final String userPhone = SharedPreferencesUtil.getString(Objects.requireNonNull(getActivity()), "userPhone");

                            String token = "token";

                            User user = new User();
                            user.setId(currentUser.getUid());
                            user.setFirstName(userFirstNameStr);
                            user.setLastName(userLastNameStr);
                            user.setPhone(userPhone);
                            user.setPhotoBitmap(getProfileImage().getDrawingCache());
                            user.setToken(token);

                            onUserInfoFragmentInteractionListener.onClickSubmmitProfile(user);
                        }
                    }
                }
            }
        };
    }

    public ImageView getProfileImage() {
        return this.profileImage;
    }

    public void setProfileImage(ImageView profileImage) {
        this.profileImage = profileImage;
        this.profileImage.setDrawingCacheEnabled(true);
        this.profileImage.buildDrawingCache();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), resultUri);
                    profileImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                ToastUtil.show(getContext(),R.string.error_getting_image);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserInfoFragmentInteractionListener) {
            onUserInfoFragmentInteractionListener = (OnUserInfoFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUserInfoFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onUserInfoFragmentInteractionListener = null;
    }

    public interface OnUserInfoFragmentInteractionListener {
        void onClickSubmmitProfile(User user);
    }
}
