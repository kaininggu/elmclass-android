package com.elmclass.elmclass.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.elmclass.elmclass.R;
import com.elmclass.elmclass.activity.SignInActivity;
import com.elmclass.elmclass.manager.AppManager;
import com.elmclass.elmclass.manager.UserManager;
import com.elmclass.elmclass.operation.SignInOperation;
import com.elmclass.elmclass.operation.SignInRequest;
import com.elmclass.elmclass.operation.SignInResponseEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Random;

import static android.text.InputType.TYPE_CLASS_PHONE;
import static com.elmclass.elmclass.manager.AppManager.showKeyboard;
import static com.elmclass.elmclass.manager.UserManager.MIN_UID_LENGTH;

/**
 * The class manages the sign in page
 *
 * Created by kgu on 4/12/18.
 */

public class SignUpFragment extends Fragment implements View.OnClickListener {
    private static String [] PERMISSIONS = { Manifest.permission.SEND_SMS };
    private static int CODE_LENGTH = 4;
    private static final int STATE_ENTER_UID = 1;
    private static final int STATE_SEND_CODE = 2;
    private static final int STATE_ENTER_CODE = 3;
    private static final int STATE_SIGN_IN = 4;
    private static final String LOG_TAG = SignUpFragment.class.getName();

    private View mSignInContainer;
    private EditText mUidView;
    private Button mSendCodeButton;
    private EditText mCodeView;
    private Button mSignInButton;
    private String mUid;
    private int mCodeSent;
    private View mSpinner;
    private AlertDialog mDialog;
    private int mState;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View top = inflater.inflate(R.layout.fragment_sign_up, container, false);

        Bundle args = getArguments();
        mUid = args == null ? "" : args.getString("u");

        mUidView = top.findViewById(R.id.uid);
        mUidView.setInputType(TYPE_CLASS_PHONE);
        mUidView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            // This method is called to notify you that, within s, the count characters beginning at start
            // have just replaced old text that had length before.
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s) || s.length() < MIN_UID_LENGTH) {
                    enableEnterUidView();
                } else {
                    enableSendCodeButton();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mCodeView = top.findViewById(R.id.code);
        mCodeView.setInputType(TYPE_CLASS_PHONE);
        mCodeView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    enableButton(mSendCodeButton, true);
                    enableButton(mSignInButton, false);
                } else if (s.length() >= CODE_LENGTH){
                    enableSignInButton();
                } else {
                    enableButton(mSendCodeButton, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mSendCodeButton = top.findViewById(R.id.send_code);
        mSendCodeButton.setOnClickListener(this);
        enableButton(mSendCodeButton, false);

        mSignInButton = top.findViewById(R.id.sign_up);
        mSignInButton.setOnClickListener(this);
        enableButton(mSignInButton, false);

        TextView link = top.findViewById(R.id.log_in);
        link.setOnClickListener(this);
        link.setPaintFlags(link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        mSignInContainer = top.findViewById(R.id.signin_container);
        mSpinner = top.findViewById(R.id.spinner);

        if (!TextUtils.isEmpty(mUid)) {
            mUidView.setText(mUid);
        }
        return top;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDialog = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_code:
                mUid = UserManager.parsePhoneNumber(mUidView.getText().toString());
                if (mUid != null) {
                    mUidView.setText(mUid);
                    requestPermissions(PERMISSIONS, AppManager.PERMISSION_REQUEST_SEND_SMS);
                } else {
                    Toast.makeText(getContext(), R.string.invalid_uid, Toast.LENGTH_SHORT).show();
                    enableEnterUidView();
                }
                break;
            case R.id.sign_up:
                if (validateCode()) {
                    signUp();
                }
                break;
            case R.id.log_in:
                if (getActivity() != null) {
                    ((SignInActivity) getActivity()).navigateToLogIn(mUid);
                }
                break;
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SignInResponseEvent event) {
        mSpinner.setVisibility(View.GONE);
        if (event == null || !event.hasError()) {
            if (getActivity() != null) {
                ((SignInActivity) getActivity()).navigateToWebView(AppManager.getInstance().getSessionData().getUserManager().getUrl());
            }
        } else {
            mSignInContainer.setVisibility(View.VISIBLE);
            showDialog(event.getError().getMessageId(), event.getError().getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AppManager.PERMISSION_REQUEST_SEND_SMS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Random random = new Random();
            mCodeSent = random.nextInt(9000) + 1000;
            String message = getContext() == null ? String.valueOf(mCodeSent) : getContext().getString(R.string.sms_code, mCodeSent);

            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent("SMS_SENT"), 0);
            if (AppManager.DEBUG) {
                Log.i(LOG_TAG, "sendTextMessage " + mUid + ": " + message);
            }
            try {
                smsManager.sendTextMessage(mUid, null, message, pendingIntent, null);
                Toast.makeText(getContext(), R.string.sms_reminder, Toast.LENGTH_SHORT).show();
                enableEnterCodeView();
            } catch (RuntimeException ex) {
                showDialog(R.string.sms_failure);
                if (AppManager.DEBUG) {
                    Log.e(LOG_TAG, "RuntimException: " + ex.getMessage() + " (mUid=" + mUid + ")");
                }
            }
        }
    }

    private Boolean validateCode() {
        if (!TextUtils.isEmpty(mCodeView.getText())) {
            if (Integer.valueOf(mCodeView.getText().toString()) == mCodeSent) {
                return true;
            } else {
                showDialog(R.string.invalid_code);
            }
        }
        return false;
    }

    private void signUp() {
        AppManager.hideKeyboard(getContext(), mSignInContainer);
        mSignInContainer.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);

        // Save the current sign in data
        UserManager userManager = AppManager.getInstance().getSessionData().getUserManager();
        userManager.setUid(mUid);

        SignInRequest request = new SignInRequest(mUid, null);
        SignInOperation op = new SignInOperation(request);
        op.submit();
    }

    private void enableEnterUidView() {
        if (mState != STATE_ENTER_UID) {
            mState = STATE_ENTER_UID;
            mUidView.setEnabled(true);
            mSendCodeButton.setVisibility(View.GONE);
            mCodeView.setVisibility(View.GONE);
            mSignInButton.setVisibility(View.GONE);
        }
    }

    private void enableSendCodeButton() {
        if (mState != STATE_SEND_CODE) {
            mState = STATE_SEND_CODE;
            enableButton(mSendCodeButton, true);
            mCodeView.setVisibility(View.GONE);
            mSignInButton.setVisibility(View.GONE);
        }
    }

    private void enableEnterCodeView() {
        if (mState != STATE_ENTER_CODE) {
            mState = STATE_ENTER_CODE;
            mSendCodeButton.setVisibility(View.GONE);
            mCodeView.setVisibility(View.VISIBLE);
            mCodeView.requestFocus();
            mSignInButton.setVisibility(View.GONE);
            showKeyboard(getActivity(), mCodeView);
        }
    }

    private void enableSignInButton() {
        if (mState != STATE_SIGN_IN) {
            mState = STATE_SIGN_IN;
            enableButton(mSendCodeButton, false);
            mCodeView.setVisibility(View.VISIBLE);
            enableButton(mSignInButton, true);
        }
    }

    private void enableButton(Button button, Boolean enabled) {
        button.setEnabled(enabled);
        button.setAlpha((float) (enabled ? 1.0 : 0.2));
        if (enabled) {
            button.setVisibility(View.VISIBLE);
        }
    }

    private void showDialog(int stringId) {
        showDialog(stringId, null);
    }

    private void showDialog(final int stringId, String message) {
        if (mDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Material_Light_Dialog));

            if (stringId != 0) {
                builder.setMessage(stringId);
            } else {
                builder.setMessage(message);
            }

            builder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (stringId == R.string.rc_107) {
                                // If a known uid, redirect to login page.
                                if (getActivity() != null) {
                                    ((SignInActivity) getActivity()).navigateToLogIn(mUid);
                                }
                            } else {
                                mCodeView.setText("");
                                enableSendCodeButton();
                            }
                        }
                    });
            mDialog = builder.create();
        } else if (getContext() != null){
            mDialog.setMessage(getContext().getString(stringId));
        }
        mDialog.show();
    }
}
