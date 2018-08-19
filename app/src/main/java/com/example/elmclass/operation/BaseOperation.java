package com.example.elmclass.operation;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.elmclass.manager.AppManager;
import com.example.elmclass.manager.NetworkManager;

import java.util.Map;

/**
 *
 * Created by kgu on 5/21/18.
 */

public abstract class BaseOperation {
    private static final String LOG_TAG = BaseOperation.class.getName();
    private IRequest mRequest;
    private int mRequestId;

    BaseOperation(@NonNull IRequest request) {
        mRequest = request;
        mRequestId = NetworkManager.getInstance().getNextRequestId();
    }

    abstract Response.Listener<String> onResult();
    abstract Response.ErrorListener onError();

    public void submit() {
        int method = mRequest.getMethod();

        Uri.Builder builder = Uri.parse(mRequest.getEndpoint()).buildUpon();
        String url = builder.build().toString();

        StringRequest request = new StringRequest(method, url, onResult(), onError()) {
            @Override
            protected Map<String, String> getParams() {
                return mRequest.getParams();
            }
        };
        if (AppManager.DEBUG) {
            Log.i(LOG_TAG, "Request " + mRequestId + ": " + mRequest.toString());
        }
        NetworkManager.getInstance().submit(request);
    }

    void logResult(Object result) {
        if (AppManager.DEBUG) {
            Log.i(LOG_TAG, "Response " + mRequestId + ": " + (result == null ? "null" : result.toString()));
        }
    }

    void logError(OperationError error, String message) {
        if (AppManager.DEBUG) {
            Log.w(LOG_TAG, "Response " + mRequestId + ": Error " + message + ": " + error == null ? "" : error.toString());
        }
    }
}
