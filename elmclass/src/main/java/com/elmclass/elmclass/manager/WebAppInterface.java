package com.elmclass.elmclass.manager;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.elmclass.elmclass.fragment.WebViewFragment;

import java.lang.reflect.Method;

/**
 * Define interface functions that can be called by html pages from within Javascript.
 * See also elmclass WebViewFragment about how to make a call from JavaScript.
 *
 * Created by kaininggu on 9/9/18.
 */

public class WebAppInterface {
    private static final String LOG_TAG = WebAppInterface.class.getName();
    private Context mContext;
    private WebViewFragment mFragment;

    /** Instantiate the interface and set the context */
    public WebAppInterface(Context context, WebViewFragment fragment) {
        mContext = context;
        mFragment = fragment;
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void invokeAppMethod(final String methodName, final String args) throws Exception {
        if (AppManager.DEBUG) {
            Log.i(LOG_TAG, "invokeAppMethod func=" + methodName + " args=" + args);
        }

        // get all methods defined in this class
        final Method[] methods = this.getClass().getMethods();
        for (final Method method : methods) {
            if (method.getName().equals(methodName) && method.getParameterTypes().length == 1) {
                method.invoke(this, args);
                return;
            }
        }
    }

    // Show a toast from the web page. Must be public to be called from JavaScript
    @SuppressWarnings("unused")
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void relogin(String placeHolder) {
        mFragment.relogin();
    }
}
