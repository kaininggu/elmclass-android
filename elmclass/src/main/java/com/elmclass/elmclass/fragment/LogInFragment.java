package com.elmclass.elmclass.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.elmclass.elmclass.R;
import com.elmclass.elmclass.activity.SignInActivity;
import com.elmclass.elmclass.manager.AppManager;
import com.elmclass.elmclass.manager.NetworkManager;
import com.elmclass.elmclass.manager.UserManager;
import com.elmclass.elmclass.operation.SignInOperation;
import com.elmclass.elmclass.operation.SignInRequest;
import com.elmclass.elmclass.operation.SignInResponseEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.text.InputType.TYPE_CLASS_PHONE;

/**
 *
 * Created by kaininggu on 9/3/18.
 */

public class LogInFragment extends Fragment implements View.OnClickListener {
    private View mSignInContainer;
    private EditText mUidView;
    private EditText mPasswordView;
    private Button mLogInButton;
    private String mUid;
    private View mSpinner;
    private AlertDialog mDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View top = inflater.inflate(R.layout.fragment_log_in, container, false);

        Bundle args = getArguments();
        if (args != null) {
            mUid = args.getString("u");
        }

        mUidView = top.findViewById(R.id.uid);
        mUidView.setInputType(TYPE_CLASS_PHONE);
        mUidView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            // This method is called to notify you that, within s, the count characters beginning at start
            // have just replaced old text that had length before.
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s) || s.length() < UserManager.MIN_UID_LENGTH) {
                    enableUidView();
                } else {
                    enablePasswordView();
                    mUidView.setSelection(s.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mPasswordView = top.findViewById(R.id.password);
        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    enableButton(mLogInButton, false);
                } else {
                    enableButton(mLogInButton, true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mLogInButton = top.findViewById(R.id.log_in);
        mLogInButton.setOnClickListener(this);
        enableButton(mLogInButton, false);

        TextView link = top.findViewById(R.id.sign_up);
        link.setOnClickListener(this);
        link.setPaintFlags(link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        link = top.findViewById(R.id.forgot_password);
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
            case R.id.log_in:
                logIn();
                break;
            case R.id.forgot_password:
                if (getActivity() != null) {
                    ((SignInActivity) getActivity()).navigateToWebView(NetworkManager.ENDPOINT_FORGOT_PASSWORD);
                }
                break;
            case R.id.sign_up:
                mUid = TextUtils.isEmpty(mUidView.getText()) ? null : mUidView.getText().toString();
                if (getActivity() != null) {
                    ((SignInActivity) getActivity()).navigateToSignUp(mUid);
                }
                break;
        }
    }

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

    private void logIn() {
        AppManager.hideKeyboard(getContext(), mSignInContainer);
        mSignInContainer.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);

        mUid = TextUtils.isEmpty(mUidView.getText()) ? null : mUidView.getText().toString();
        String password = TextUtils.isEmpty(mPasswordView.getText()) ? null : mPasswordView.getText().toString();

        // Save the current sign in data
        UserManager userManager = AppManager.getInstance().getSessionData().getUserManager();
        userManager.setUid(mUid);

        SignInRequest request = new SignInRequest(mUid, password);
        SignInOperation op = new SignInOperation(request);
        op.submit();
    }

    private void enableUidView() {
        mUidView.setEnabled(true);
        mPasswordView.setVisibility(View.GONE);
        mLogInButton.setVisibility(View.GONE);
    }

    private void enablePasswordView() {
        mPasswordView.setVisibility(View.VISIBLE);
        mLogInButton.setVisibility(TextUtils.isEmpty(mPasswordView.getText()) ? View.GONE : View.VISIBLE);
    }

    private void enableButton(Button button, Boolean enabled) {
        button.setEnabled(enabled);
        button.setAlpha((float) (enabled ? 1.0 : 0.2));
        if (enabled) {
            button.setVisibility(View.VISIBLE);
        }
    }

    private void showDialog(int stringId, String message) {
        if (mDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Material_Light_Dialog));

            if (stringId != 0) {
                builder.setMessage(stringId);
            } else {
                builder.setMessage(message);
            }

            builder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    enableButton(mLogInButton, true);
                }
            });
            mDialog = builder.create();
        } else if (getContext() != null){
            mDialog.setMessage(getContext().getString(stringId));
        }
        mDialog.show();
    }

}
