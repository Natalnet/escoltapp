package br.com.escoltapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import br.com.escoltapp.entities.User;
import br.com.escoltapp.entityservices.UserService;
import br.com.escoltapp.fragments.LoadingFragment;
import br.com.escoltapp.fragments.PhoneNumberFragment;
import br.com.escoltapp.fragments.VerificationCodeFragment;
import br.com.escoltapp.util.DialogUtil;
import br.com.escoltapp.util.SharedPreferencesUtil;
import br.com.escoltapp.util.ToastUtil;
import br.com.scoltapp.R;

public class SignInActivity extends AppCompatActivity implements PhoneNumberFragment.OnPhoneNumberFragmentInteractionLinstener,
        VerificationCodeFragment.OnVerificationCodeFragmentInteractionLinstener {

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken = null;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String phoneNumber;
    private ActionBar actionBar;

    PhoneNumberFragment phoneNumberFragment;
    VerificationCodeFragment verificationCodeFragment;
    LoadingFragment loadingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = onVerificationStateChangedCallbacks();

        actionBar = getSupportActionBar();

        phoneNumberFragment = PhoneNumberFragment.newInstance();
        verificationCodeFragment = VerificationCodeFragment.newInstance();
        loadingFragment = LoadingFragment.newInstance();

        showFragment(phoneNumberFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showActionBarBackButton(false);
                showFragment(phoneNumberFragment);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFillPhone(String phoneNumber) {
        SharedPreferencesUtil.putString(getBaseContext(),"userPhone",phoneNumber);
        this.phoneNumber = phoneNumber;
        showFragment(loadingFragment);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    @Override
    public void onFillVerificationCode(String verificationCode) {
        String verificationId = SharedPreferencesUtil.getString(getBaseContext(), "verificationId");
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verificationCode);
        showFragment(loadingFragment);
        signInWithPhoneAuthCredential(credential);
    }

    @Override
    public void onClickSendCodeAgain() {
        if (forceResendingToken != null) {
            showFragment(loadingFragment);
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    60L,
                    TimeUnit.SECONDS,
                    SignInActivity.this,
                    mCallbacks,
                    forceResendingToken);

            ToastUtil.show(getBaseContext(),getString(R.string.code_requested));
        }
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks onVerificationStateChangedCallbacks() {
        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    DialogUtil.showOkDialog(SignInActivity.this,R.string.auth_failure,R.string.invalid_phone_number, null);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    DialogUtil.showOkDialog(SignInActivity.this,R.string.auth_failure,R.string.too_many_sms_requests,null);
                } else { //(e instanceof FirebaseNetworkException)
                    DialogUtil.showOkDialog(SignInActivity.this,R.string.auth_failure,R.string.network_error_message,null);
                }
                showActionBarBackButton(false);
                showFragment(phoneNumberFragment);
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later
                SharedPreferencesUtil.putString(SignInActivity.this, "verificationId", verificationId);
                SharedPreferencesUtil.putLong(SignInActivity.this, "codeSendTime", System.currentTimeMillis());
                forceResendingToken = token;
                showActionBarBackButton(true);
                showFragment(verificationCodeFragment);
                ToastUtil.show(getBaseContext(),getString(R.string.code_requested));
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = task.getResult().getUser();

                            //check if profile already exists
                            UserService.read(getBaseContext(), user.getUid(), new UserService.OnReadListener() {
                                @Override
                                public void onSuccess(User user) {
                                    //Launch home activity
                                    Intent intent = new Intent(SignInActivity.this,MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure() {
                                    //Launch create profile activity
                                    SharedPreferencesUtil.putString(getBaseContext(),"phoneNumber",phoneNumber);
                                    Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                DialogUtil.showOkDialog(SignInActivity.this,R.string.auth_failure,R.string.invalid_typed_code,null);
                            } else if (task.getException() instanceof  FirebaseNetworkException) {
                                DialogUtil.showOkDialog(SignInActivity.this,R.string.auth_failure,R.string.network_error_message,null);
                            }
                            showActionBarBackButton(true);
                            showFragment(verificationCodeFragment);
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showActionBarBackButton(false);
                showFragment(phoneNumberFragment);
                DialogUtil.showOkDialog(SignInActivity.this,R.string.auth_failure,R.string.network_error_message,null);
            }
        });
    }

    private void showActionBarBackButton(boolean show) {
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(show);
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }
}
