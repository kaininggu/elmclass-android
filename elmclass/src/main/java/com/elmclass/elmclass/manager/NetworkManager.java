package com.elmclass.elmclass.manager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.elmclass.elmclass.R;
import com.google.gson.Gson;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * The class manages all network operations
 *
 * Created by kgu on 4/11/18.
 */

public class NetworkManager {
    private static final int MY_SOCKET_TIMEOUT_MS = 15000;
    private static final String LOG_TAG = NetworkManager.class.getName();

    // urls
    static final String BASE_URL = "http://www.elmclass.com/v2/";
    public static final String ENDPOINT_USERS = NetworkManager.BASE_URL + "elm/signup";
    public static final String ENDPOINT_ASSIGNMENT = NetworkManager.BASE_URL + "elm/daily";
    public static final String ENDPOINT_SETTING = NetworkManager.BASE_URL + "elm/setup";
    public static final String ENDPOINT_HELP = NetworkManager.BASE_URL + "elm/help";
    public static final String ENDPOINT_FORGOT_PASSWORD = NetworkManager.BASE_URL + "elm/opentalk";
    public static final String KEY_URL = "url";

    private static NetworkManager sInstance;
    private Context mAppContext;
    private RequestQueue mRequestQueue;
    private DefaultRetryPolicy mRetryPolicy;
    private int mRequestId;
    private Gson mGson;

    public @NonNull static NetworkManager getInstance() {
        if (sInstance == null) {
            setup();
        }
        return sInstance;
    }

    public int getNextRequestId() { return ++mRequestId; }

    public void submit(@NonNull StringRequest request) {
        request.setRetryPolicy(mRetryPolicy);
        mRequestQueue.add(request);
    }

    public @Nullable <T> T fromJson(@NonNull String jsonString, @NonNull Type targetType) {
        return mGson.fromJson(jsonString, targetType);
    }

    public String toJson(@NonNull Object object) { return mGson.toJson(object); }

    public boolean hasNetworkConnection() {
        boolean isWifiConn = false;
        boolean isMobileConn = false;
        ConnectivityManager connMgr = (ConnectivityManager) mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (ContextCompat.checkSelfPermission(mAppContext, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
            try {
                NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                isWifiConn = networkInfo.isConnected();
                if (!isWifiConn) {
                    networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                    isMobileConn = networkInfo.isConnected();
                }
            } catch (NullPointerException ex) {
                if (AppManager.DEBUG) {
                    Log.w(LOG_TAG, "Connection NPE: " + ex.getMessage());
                }
            }
        }
        return isWifiConn || isMobileConn;
    }

    private static synchronized void setup() {
        if (sInstance == null) {
            try {
                sInstance = new NetworkManager(AppManager.getInstance().getAppContext());
            } catch (NullPointerException ex) {
                if (AppManager.DEBUG) {
                    Log.w(LOG_TAG, "Setup NPE: " + ex.getMessage());
                }
            }
        }
    }

    private NetworkManager(Context appContext) {
        mAppContext = appContext;

//        SSLSocketFactory pinnedSSLSocketFactory = newPinFactory();
        SSLSocketFactory pinnedSSLSocketFactory = newKeyStoreFactory();

        // Creates a default worker pool and calls {@link RequestQueue#start()} on it.
//        mRequestQueue = Volley.newRequestQueue(appContext);
        mRequestQueue = Volley.newRequestQueue(mAppContext, new HurlStack(null, pinnedSSLSocketFactory));
        mRetryPolicy = new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        mGson = new Gson();
    }

    private SSLSocketFactory newPinFactory() {
        TrustManager tm[] = {new PubKeyManager("jx1myAB/dIlRCC2UpKHT8YgYcYyPX9FtbdxfHqUKZYk=")};
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tm, null);
            return context.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SSLSocketFactory newKeyStoreFactory() {
        try {
            // Get an instance of the Bouncy Castle KeyStore format
//            KeyStore trusted = KeyStore.getInstance(getDefaultType());
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            // Get the raw resource, which contains the keystore with
            // your trusted certificates (root and any intermediate certs)
            InputStream in = mAppContext.getResources().openRawResource(R.raw.keystore);
            try {
                // Initialize the keystore with the provided trusted certificates
                // Provide the password of the keystore
                keyStore.load(in, "changeme".toCharArray());
            } finally {
                in.close();
            }

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            return context.getSocketFactory();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private NetworkManager() {}
}
