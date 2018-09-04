package com.elmclass.elmclass.manager;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.elmclass.elmclass.operation.SignInResult;

import static com.elmclass.elmclass.manager.PersistenceManager.KEY_EXPIRATION;
import static com.elmclass.elmclass.manager.PersistenceManager.KEY_UID;
import static com.elmclass.elmclass.manager.PersistenceManager.KEY_USER_TOKEN;

/**
 *
 * Created by kgu on 5/21/18.
 */

public class UserManager {

    private SharedPreferences mPersistenceStore;
    private String mUid;
    private String mUserToken;
    private long mExpiration;
    private String mUrl;

    UserManager(SharedPreferences persistenceStore) {
        mPersistenceStore = persistenceStore;
        fromPersistenceStore();
    }

    private void fromPersistenceStore() {
        mUid = mPersistenceStore.getString(KEY_UID, "");
        mUserToken = mPersistenceStore.getString(KEY_USER_TOKEN, "");
        mExpiration = mPersistenceStore.getLong(KEY_EXPIRATION, 0);
    }

    // PII data stays in persistence store only
    public @NonNull
    String getUid() { return mUid; }
    public void setUid(@NonNull String uid) {
        mUid = uid;
        PersistenceManager.setString(mPersistenceStore, KEY_UID, uid);
    }

    public @NonNull String getUserToken() { return mUserToken; }
    private void setUserToken(@Nullable String userToken) {
        mUserToken = userToken;
        PersistenceManager.setString(mPersistenceStore, KEY_USER_TOKEN, userToken);
    }

    public long getExpiration() { return mExpiration; }
    private void setmExpiration(long expiration) {
        mExpiration = expiration;
        PersistenceManager.setLong(mPersistenceStore, KEY_EXPIRATION, expiration);
    }

    public String getUrl() { return mUrl; }

    public void setSignInResult(@NonNull SignInResult result) {
        setUserToken(result.getUserToken());
        if (result.getExpiration() > 0) {
            setmExpiration(result.getExpiration() + System.currentTimeMillis() / 1000);
        }
        mUrl = NetworkManager.BASE_URL + result.getUrl();
    }

    void logout() {
        mUserToken = "";
        PersistenceManager.setString(mPersistenceStore, KEY_USER_TOKEN, "");
    }
}
