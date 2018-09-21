package com.elmclass.elmclass.operation;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.elmclass.elmclass.manager.AppManager;
import com.elmclass.elmclass.manager.NetworkManager;

import java.util.HashMap;
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
            public Map<String, String> getHeaders(){
                if (AppManager.DEBUG) {
                    Log.i(LOG_TAG, "USER_AGENT=" + AppManager.USER_AGENT);
                }
                Map<String, String> headers = new HashMap<>();
                headers.put("User-agent", AppManager.USER_AGENT);
                return headers;
            }

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
            Log.w(LOG_TAG, "Response " + mRequestId + ": " + (error == null ? message : error.toString()));
        }
    }

    void logError(VolleyError error) {
        if (AppManager.DEBUG) {
            Log.w(LOG_TAG, "Response " + mRequestId + ": VolleyError: " + error.getMessage());
        }
    }
}
