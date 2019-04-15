package br.com.escoltapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import br.com.scoltapp.R;

public class PhoneNumberFragment extends Fragment {

    private EditText phoneState;
    private EditText phoneNumber;
    private TextView errorMessage;

    private Animation mShakeAnimation;

    private OnPhoneNumberFragmentInteractionLinstener mListener;

    public PhoneNumberFragment() {}

    public static PhoneNumberFragment newInstance() {
        return new PhoneNumberFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_phone_number, container, false);

        FloatingActionButton signInFAB = rootView.findViewById(R.id.signInButton);
        signInFAB.setOnClickListener(onClickSignInFAB());

        phoneState = rootView.findViewById(R.id.phoneState);

        phoneNumber = rootView.findViewById(R.id.phoneNumber);

        errorMessage = rootView.findViewById(R.id.errorMessage);

        mShakeAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.shake_animation);

        return rootView;
    }

    private View.OnClickListener onClickSignInFAB() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String state = phoneState.getText().toString();
                String phone = phoneNumber.getText().toString();
                if (state.isEmpty() || phone.isEmpty() ) {
                    errorMessage.setText(R.string.emptyFildsMessage);
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.startAnimation(mShakeAnimation);
                } else {
                    String phoneNumber = "+55" + state + phone;

                    if (mListener != null) {
                        mListener.onFillPhone(phoneNumber);
                    }
                }
            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPhoneNumberFragmentInteractionLinstener) {
            mListener = (OnPhoneNumberFragmentInteractionLinstener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPhoneNumberFragmentInteractionLinstener");
        }
    }

    public interface OnPhoneNumberFragmentInteractionLinstener {
        void onFillPhone(String phoneNumber);
    }
}
