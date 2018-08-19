package com.elmclass.elmclass.operation;

import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.elmclass.elmclass.manager.NetworkManager;
import com.elmclass.elmclass.operation.IRequest;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by kgu on 4/12/18.
 */

public class SignInRequest implements IRequest {
    @SerializedName("u")
    private String mUid;

    public SignInRequest(@NonNull String uid) {
        mUid = uid;
    }

    public void setUid(String uid) { mUid = uid; }
    public String getUid() { return mUid; }

    @Override
    public String toString() {
        return IRequest.REQUEST_METHOD_TO_STRING[getMethod()] + " " + getEndpoint() + " " + NetworkManager.getInstance().toJson(this);
    }

    public int getMethod() { return Request.Method.POST; }

    public String getEndpoint() { return NetworkManager.ENDPOINT_USERS; }

    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("u", mUid);
        return params;
    }
}
