package br.com.escoltapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import br.com.escoltapp.entities.User;
import br.com.escoltapp.entityservices.UserService;
import br.com.escoltapp.fragments.LoadingFragment;
import br.com.escoltapp.fragments.UserInfoFragment;
import br.com.escoltapp.util.DialogUtil;
import br.com.scoltapp.R;

public class SignUpActivity extends AppCompatActivity implements UserInfoFragment.OnUserInfoFragmentInteractionListener {

    FirebaseAuth mAuth;

    UserInfoFragment userInfoFragment;
    LoadingFragment loadingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        userInfoFragment = UserInfoFragment.newInstance(mAuth.getCurrentUser());
        loadingFragment = LoadingFragment.newInstance();

        showFragment(userInfoFragment);
    }

    @Override
    public void onClickSubmmitProfile(final User user) {
        showFragment(loadingFragment);
        UserService.create(getBaseContext(), user, new UserService.OnSaveListener() {
            @Override
            public void onSuccess() {
                removeFragment(loadingFragment);
                DialogUtil.showOkDialog(SignUpActivity.this, R.string.profile_created,
                        R.string.profile_created_successfully, new DialogUtil.OnDismissDialogListener() {
                            @Override
                            public void onDismissDialog() {
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });
            }

            @Override
            public void onFailure() {
                showFragment(userInfoFragment);
                DialogUtil.showOkDialog(
                        SignUpActivity.this,
                        R.string.error_saving_profile_title, R.string.error_saving_profile_msg, null);
            }
        });
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    private void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        userInfoFragment.onActivityResult(requestCode, resultCode, data);
    }
}
