package com.elmclass.elmclass.operation;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.volley.Request;
import com.elmclass.elmclass.manager.NetworkManager;
import com.elmclass.elmclass.manager.SimpleBase64;
import com.elmclass.elmclass.operation.IRequest;
import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by kgu on 4/12/18.
 */

public class SignInRequest implements IRequest {
    @SerializedName("u")
    private String mUid;

    @SerializedName("h")
    private String mHash;

    @SerializedName("p")
    private String mPassword;

    public void setUid(String uid) { mUid = uid; }
    public String getUid() { return mUid; }

    public String getHash() { return mHash; }

    public void setPassword(String password) { mPassword = password; }
    public String getPassword() { return mPassword; }

    public SignInRequest(@NonNull String uid, String password) {
        byte[] bytes = uid.getBytes();
        mUid = SimpleBase64.encode(bytes);
        mHash = checksum(uid);
        if (!TextUtils.isEmpty(password)) {
            mPassword = SimpleBase64.encode(password.getBytes());
        } else {
            mPassword = null;
        }
    }

    @Override
    public String toString() {
        return IRequest.REQUEST_METHOD_TO_STRING[getMethod()] + " " + getEndpoint() + " " + NetworkManager.getInstance().toJson(this);
    }

    public int getMethod() { return Request.Method.POST; }

    public String getEndpoint() { return NetworkManager.ENDPOINT_USERS; }

    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("u", mUid);
        params.put("h", mHash);
        if (!TextUtils.isEmpty(mPassword)) {
            params.put("p", mPassword);
        }
        return params;
    }

    private String checksum(String data) {
        int hash = 7;
        char[] chars = data.toCharArray();
        for (char c : chars) {
            hash = (hash * 31) % 1000000 + c;
        }
        return String.valueOf(hash);
    }
}
