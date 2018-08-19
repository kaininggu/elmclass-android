package com.example.elmclass.operation;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.elmclass.R;
import com.example.elmclass.manager.AppManager;
import com.example.elmclass.manager.NetworkManager;
import com.google.gson.JsonParseException;

import org.greenrobot.eventbus.EventBus;

/**
 *
 * Created by kgu on 5/18/18.
 */

public class SignInOperation extends BaseOperation {

    public SignInOperation(@NonNull SignInRequest request) {
        super(request);
    }

    Response.Listener<String> onResult() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    SignInResult result = NetworkManager.getInstance().fromJson(response, SignInResult.class);
                    if (result != null) {
                        logResult(result);
                        if (result.getReturnCode() == 0) {
                            AppManager.getInstance().getSessionData().getUserManager().setSignInResult(result);
                            EventBus.getDefault().post(new SignInResponseEvent(null));
                        } else {
                            OperationError error = new OperationError(result.getMessage());
                            logError(error, "Response");
                            EventBus.getDefault().post(new SignInResponseEvent(error));
                        }
                    } else {
                        logError(null, "Empty response");
                        EventBus.getDefault().post(new SignInResponseEvent(new OperationError(R.string.empty_response)));
                    }
                } catch (JsonParseException ex) {
                    logError(null, "Empty response");
                    EventBus.getDefault().post(new SignInResponseEvent(new OperationError(R.string.json_exception)));
                }
            }
        };
    }

    Response.ErrorListener onError() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                OperationError oe = new OperationError(error);
                logError(oe, "Network error");
                EventBus.getDefault().post(new SignInResponseEvent(oe));
            }
        };
    }
}
