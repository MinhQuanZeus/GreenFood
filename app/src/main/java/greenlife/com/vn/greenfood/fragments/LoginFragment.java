package greenlife.com.vn.greenfood.fragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.UIManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import greenlife.com.vn.greenfood.R;
import greenlife.com.vn.greenfood.activities.MainActivity;
import greenlife.com.vn.greenfood.managers.ScreenManager;
import greenlife.com.vn.greenfood.utils.NetworkUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private final String prefname = "my_data";
    private static final int REQUEST_SIGNUP = 0;

    private String TAG = "LoginFragment";
    private Button _loginButton;
    private TextView _emailText;
    private TextView _passwordText;
    private TextView _sign_up;
    private CheckBox _saveLoginInfomationCb;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    //TODO account kit
    private static final int FRAMEWORK_REQUEST_CODE = 1;
    AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder;
    UIManager uiManager;

    private int nextPermissionsRequestCode = 4000;
    private final Map<Integer, OnCompleteListener> permissionsListeners = new HashMap<>();

    private interface OnCompleteListener {
        void onComplete();
    }

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        setupUI(view);
        AccountKit.logOut();
        return view;
    }

    private void setupUI(View view) {
        _loginButton = view.findViewById(R.id.btn_send);
        _emailText = view.findViewById(R.id.input_email);
        _passwordText = view.findViewById(R.id.input_password);
        _sign_up = view.findViewById(R.id.link_signup);
        _saveLoginInfomationCb = (CheckBox) view.findViewById(R.id.cb_remember);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
        _sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLogin(LoginType.PHONE);
            }
        });
    }


    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Đang đăng nhập...");
        progressDialog.show();

        if (NetworkUtils.isConnected()) {
            signIn();
        } else {
            Toast.makeText(getContext(), "Vui lòng kiểm tra kết nối internet của bạn",
                    Toast.LENGTH_SHORT).show();
            _loginButton.setEnabled(true);
        }
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 2000);
    }

    private void signIn() {
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new com.google.android.gms.tasks.OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.getIdToken(true);
                            Toast.makeText(getContext(), getResources().getString(R.string.login_success),
                                    Toast.LENGTH_SHORT).show();
                            sendRegistrationToServer(user.getUid());
                            FirebaseMessaging.getInstance().subscribeToTopic(user.getUid());
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                            _loginButton.setEnabled(true);

                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), getResources().getString(R.string.invalid_username_or_password),
                                    Toast.LENGTH_SHORT).show();
                            _loginButton.setEnabled(true);
                        }

                    }
                });
    }

    public void onLoginFailed() {
        Toast.makeText(getContext(), "Đăngn nhập không thàh công", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getResources().getString(R.string.invalid_email));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length()<6) {
            _passwordText.setError(getResources().getString(R.string.short_password));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    @Override
    public void onPause() {
        super.onPause();
        savingPreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        restoringPreferences();
        if (AccountKit.getCurrentAccessToken() != null) {
            AccountKit.logOut();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AccountKit.getCurrentAccessToken() != null) {
            AccountKit.logOut();
        }
    }

    public void savingPreferences() {
        SharedPreferences pre = this.getActivity().getSharedPreferences(prefname, MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        String user = _emailText.getText().toString();
        String pwd = _passwordText.getText().toString();
        boolean bchk = _saveLoginInfomationCb.isChecked();
        if (!bchk) {
            editor.clear();
        } else {
            editor.putString("user", user);
            editor.putString("pwd", pwd);
            editor.putBoolean("checked", bchk);
        }
        editor.commit();
    }

    public void restoringPreferences() {
        SharedPreferences pre = this.getActivity().getSharedPreferences(prefname, MODE_PRIVATE);
        boolean bchk = pre.getBoolean("checked", false);
        if (bchk) {
            String user = pre.getString("user", "");
            String pwd = pre.getString("pwd", "");
            _emailText.setText(user);
            _passwordText.setText(pwd);
        }
        _saveLoginInfomationCb.setChecked(bchk);
    }

    private void setDefaultVariable() throws IOException {
        mDatabaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("users");
        String key = mDatabaseReference.push().getKey();
    }

    private void sendRegistrationToServer(String userID) {
        // TODO: Implement this method to send token to your app server.
        SharedPreferences pre = this.getActivity().getSharedPreferences(getResources().getString(R.string.firebase_token), 0);
        String token = pre.getString(getResources().getString(R.string.reg_token_ID), "");
        try {
            setDefaultVariable();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mDatabaseReference.child(userID).child("tokenID").setValue(token);
    }

    //TODO Account kit
    @Override
    public void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != FRAMEWORK_REQUEST_CODE) {
            return;
        }

        final String toastMessage;
        final AccountKitLoginResult loginResult = AccountKit.loginResultWithIntent(data);
        if (loginResult == null || loginResult.wasCancelled()) {
            toastMessage = getResources().getString(R.string.cancel_register);
        } else if (loginResult.getError() != null) {
            toastMessage = loginResult.getError().getErrorType().getMessage();
        } else {
            final AccessToken accessToken = loginResult.getAccessToken();
            final long tokenRefreshIntervalInSeconds =
                    loginResult.getTokenRefreshIntervalInSeconds();
            if (accessToken != null) {
                toastMessage = "Success:" + accessToken.getAccountId()
                        + tokenRefreshIntervalInSeconds;
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(final Account account) {
                        PhoneNumber phoneNumber = account.getPhoneNumber();
                        String phoneNumberString = phoneNumber.toString();
                        ScreenManager.openFragment(getActivity().getSupportFragmentManager(), new RegisterFragment().setPhoneNumber(phoneNumberString), R.id.content, false);
                    }

                    @Override
                    public void onError(final AccountKitError error) {
                    }
                });

            } else {
                toastMessage = "Unknown response type";
            }
        }

        Toast.makeText(
                getActivity(),
                toastMessage,
                Toast.LENGTH_LONG)
                .show();
    }

    private void onLogin(final LoginType loginType) {
        final Intent intent = new Intent(getActivity(), AccountKitActivity.class);
        final AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder
                = new AccountKitConfiguration.AccountKitConfigurationBuilder(
                loginType,
                AccountKitActivity.ResponseType.TOKEN);
        final AccountKitConfiguration configuration = configurationBuilder.build();
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configuration);
        OnCompleteListener completeListener = new OnCompleteListener() {
            @Override
            public void onComplete() {
                startActivityForResult(intent, FRAMEWORK_REQUEST_CODE);
            }
        };
        switch (LoginType.PHONE) {
            case EMAIL:
                if (!isGooglePlayServicesAvailable()) {
                    final OnCompleteListener getAccountsCompleteListener = completeListener;
                    completeListener = new OnCompleteListener() {
                        @Override
                        public void onComplete() {
                            requestPermissions(
                                    Manifest.permission.GET_ACCOUNTS,
                                    R.string.permissions_get_accounts_title,
                                    R.string.permissions_get_accounts_message,
                                    getAccountsCompleteListener);
                        }
                    };
                }
                break;
            case PHONE:
                if (configuration.isReceiveSMSEnabled() && !canReadSmsWithoutPermission()) {
                    final OnCompleteListener receiveSMSCompleteListener = completeListener;
                    completeListener = new OnCompleteListener() {
                        @Override
                        public void onComplete() {
                            requestPermissions(
                                    Manifest.permission.RECEIVE_SMS,
                                    R.string.permissions_receive_sms_title,
                                    R.string.permissions_receive_sms_message,
                                    receiveSMSCompleteListener);
                        }
                    };
                }
                if (configuration.isReadPhoneStateEnabled() && !isGooglePlayServicesAvailable()) {
                    final OnCompleteListener readPhoneStateCompleteListener = completeListener;
                    completeListener = new OnCompleteListener() {
                        @Override
                        public void onComplete() {
                            requestPermissions(
                                    Manifest.permission.READ_PHONE_STATE,
                                    R.string.permissions_read_phone_state_title,
                                    R.string.permissions_read_phone_state_message,
                                    readPhoneStateCompleteListener);
                        }
                    };
                }
                break;
        }
        completeListener.onComplete();
    }

    private boolean isGooglePlayServicesAvailable() {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int googlePlayServicesAvailable = apiAvailability.isGooglePlayServicesAvailable(getActivity());
        return googlePlayServicesAvailable == ConnectionResult.SUCCESS;
    }

    private boolean canReadSmsWithoutPermission() {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int googlePlayServicesAvailable = apiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (googlePlayServicesAvailable == ConnectionResult.SUCCESS) {
            return true;
        }

        return false;
    }

    private void requestPermissions(
            final String permission,
            final int rationaleTitleResourceId,
            final int rationaleMessageResourceId,
            final OnCompleteListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (listener != null) {
                listener.onComplete();
            }
            return;
        }

        checkRequestPermissions(
                permission,
                rationaleTitleResourceId,
                rationaleMessageResourceId,
                listener);
    }

    @TargetApi(23)
    private void checkRequestPermissions(
            final String permission,
            final int rationaleTitleResourceId,
            final int rationaleMessageResourceId,
            final OnCompleteListener listener) {
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            if (listener != null) {
                listener.onComplete();
            }
            return;
        }

        final int requestCode = nextPermissionsRequestCode++;
        permissionsListeners.put(requestCode, listener);

        if (shouldShowRequestPermissionRationale(permission)) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(rationaleTitleResourceId)
                    .setMessage(rationaleMessageResourceId)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            requestPermissions(new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            // ignore and clean up the listener
                            permissionsListeners.remove(requestCode);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            requestPermissions(new String[]{permission}, requestCode);
        }
    }

    @TargetApi(23)
    @SuppressWarnings("unused")
    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           final @NonNull String permissions[],
                                           final @NonNull int[] grantResults) {
        final OnCompleteListener permissionsListener = permissionsListeners.remove(requestCode);
        if (permissionsListener != null
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionsListener.onComplete();
        }
    }

    @TargetApi(23)
    public int checkSelfPermission(String permission) {
        return getContext().checkSelfPermission(permission);
    }


}
