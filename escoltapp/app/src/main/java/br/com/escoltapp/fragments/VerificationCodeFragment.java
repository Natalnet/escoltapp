package br.com.escoltapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import br.com.escoltapp.util.ToastUtil;
import br.com.scoltapp.R;

public class VerificationCodeFragment extends Fragment {

    private OnVerificationCodeFragmentInteractionLinstener onVerificationCodeFragmentInteractionLinstener;

    private View rootView;

    public VerificationCodeFragment() {}

    public static VerificationCodeFragment newInstance() {
        return new VerificationCodeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_verification_code, container, false);

        FloatingActionButton submmitVerificationCodeFAB = rootView.findViewById(R.id.submmitVerificationCodeFAB);
        submmitVerificationCodeFAB.setOnClickListener(onClickSubmmitVerificationCodeFAB());

        TextView sendCodeAgain = rootView.findViewById(R.id.sendCodeAgain);
        sendCodeAgain.setOnClickListener(onClickSendCodeAgain());



        return rootView;
    }

    private View.OnClickListener onClickSendCodeAgain() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onVerificationCodeFragmentInteractionLinstener != null) {
                    onVerificationCodeFragmentInteractionLinstener.onClickSendCodeAgain();
                }
            }
        };
    }

    private View.OnClickListener onClickSubmmitVerificationCodeFAB() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typedCode = getTypedCode();
                if (typedCode.length() < 6) {
                    ToastUtil.show(getContext(),R.string.insert_auth_code);
                } else {
                    if (onVerificationCodeFragmentInteractionLinstener != null) {
                        onVerificationCodeFragmentInteractionLinstener.onFillVerificationCode(typedCode);
                    }
                }
            }
        };
    }

    private String getTypedCode() {
        EditText typedCode = rootView.findViewById(R.id.typedCode);


        return typedCode.getText().toString();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnVerificationCodeFragmentInteractionLinstener) {
            onVerificationCodeFragmentInteractionLinstener = (OnVerificationCodeFragmentInteractionLinstener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnVerificationCodeFragmentInteractionLinstener");
        }
    }

    public interface OnVerificationCodeFragmentInteractionLinstener {
        void onFillVerificationCode(String phoneNumber);
        void onClickSendCodeAgain();
    }
}
