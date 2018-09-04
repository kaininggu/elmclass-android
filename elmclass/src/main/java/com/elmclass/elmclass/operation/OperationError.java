package com.elmclass.elmclass.operation;

import android.support.annotation.Nullable;

import com.android.volley.VolleyError;
import com.elmclass.elmclass.R;
import com.elmclass.elmclass.manager.NetworkManager;

/**
 *
 * Created by kaininggu on 5/19/18.
 */

public class OperationError {
    public static final int RC_EMPTY_RESPONSE = 1;
    public static final int RC_JSON_EXCEPTION = 2;

    private int mHttpStatusCode;
    private int mMessageId;
    private String mMessage;

    OperationError(@Nullable VolleyError error) {
        if (error != null) {
            if (error.networkResponse != null) {
                mHttpStatusCode = error.networkResponse.statusCode;
                mapErrorCode(mHttpStatusCode, "");
            }
        }
        if (mMessageId == 0) {
            mMessageId = R.string.unknown_error;
        }
    }

    OperationError(int rc, String message) {
        mapErrorCode(rc, message);
    }

    public int getMessageId() { return mMessageId; }

    public String getMessage() { return mMessage; }

    @Override
    public String toString() { return NetworkManager.getInstance().toJson(this); }

    private void mapErrorCode(int rc, String message) {

        switch(rc) {
            case RC_EMPTY_RESPONSE:
                mMessageId = R.string.empty_response;
                break;
            case RC_JSON_EXCEPTION:
                mMessageId = R.string.json_exception;
                break;
            case 404:
                mMessageId = R.string.http_404;
                break;
            case 101:
            case 103:
                mMessageId = R.string.rc_101;
                break;
            case 107:
                mMessageId = R.string.rc_107;
                break;
            case 109:
                mMessageId = R.string.rc_109;
                break;
            default:
                mMessageId = R.string.unknown_error;
                break;
        }
        mMessage = message;
    }
}
