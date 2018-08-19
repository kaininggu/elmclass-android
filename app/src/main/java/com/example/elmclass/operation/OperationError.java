package com.example.elmclass.operation;

import android.support.annotation.Nullable;

import com.android.volley.VolleyError;
import com.example.elmclass.R;
import com.example.elmclass.manager.NetworkManager;

/**
 *
 * Created by kaininggu on 5/19/18.
 */

public class OperationError {
    private int mHttpStatusCode;
    private int mMessageId;
    private String mMessage;

    OperationError(@Nullable VolleyError error) {
        if (error != null) {
            if (error.networkResponse != null) {
                mHttpStatusCode = error.networkResponse.statusCode;
                switch (mHttpStatusCode) {
                    case 404:
                        mMessageId = R.string.http_404;
                        break;
                }
            }
        }
        if (mMessageId == 0) {
            mMessageId = R.string.unknown_error;
        }
    }

    OperationError(int messageId) {
        mMessageId = messageId == 0 ? R.string.unknown_error : messageId;
    }

    OperationError(String message) {
        mMessage = message;
    }

    public int getMessageId() { return mMessageId; }

    public String getMessage() { return mMessage; }

    @Override
    public String toString() { return NetworkManager.getInstance().toJson(this); }
}
