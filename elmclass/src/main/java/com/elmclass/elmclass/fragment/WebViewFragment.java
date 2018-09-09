package com.elmclass.elmclass.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.elmclass.elmclass.R;
import com.elmclass.elmclass.activity.WebViewActivity;
import com.elmclass.elmclass.manager.AppManager;
import com.elmclass.elmclass.manager.NetworkManager;

import static com.elmclass.elmclass.manager.AppManager.PERMISSIONS_REQUEST_RECORD_AUDIO;

/**
 *
 * Created by kgu on 5/18/18.
 */

public class WebViewFragment extends Fragment {
    private static String [] PERMISSIONS = { Manifest.permission.RECORD_AUDIO};
    private static final String LOG_TAG = WebViewFragment.class.getName();
    private String mUrl;
    private ProgressBar mSpinner;
    private WebView mWebView;
    private PermissionRequest myRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View top = inflater.inflate(R.layout.fragment_webview, container, false);

        if (getArguments() != null) {
            mUrl = getArguments().getString(NetworkManager.KEY_URL);
        }
        if (!TextUtils.isEmpty(mUrl)) {
            mSpinner = top.findViewById(R.id.spinner);
            mSpinner.setVisibility(View.VISIBLE);

            mWebView = top.findViewById(R.id.webview);
            setWebView();
            navigateToWebView(mUrl);
        }

        return top;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
                return true;
            case R.id.menu_item_assignment:
                navigateToWebView(NetworkManager.ENDPOINT_ASSIGNMENT);
                return true;
            case R.id.menu_item_settings:
                navigateToWebView(NetworkManager.ENDPOINT_SETTING);
                return true;
            case R.id.menu_item_log_out:
                showConfirmationDialog();
                return true;
            case R.id.menu_item_help:
                navigateToWebView(NetworkManager.ENDPOINT_HELP);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void navigateToWebView(String url) {
        setCookie();
        mWebView.loadUrl(url);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        myRequest.grant(myRequest.getResources());
        mWebView.loadUrl(mUrl);
    }

    private void setWebView() {
        // Enable cookie by not clearCache nor clearHistory

        // enable JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // enable pinch zoom
        mWebView.getSettings().setBuiltInZoomControls(true);

        // App crashes upon back button if we don't set WebViewClient
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url){
                mSpinner.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                try {
                    mWebView.stopLoading();
                } catch (Exception e) {
                    if (AppManager.DEBUG) {
                        Log.w(LOG_TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }
                String message = getContext() == null ? "" : getContext().getString(R.string.webview_on_error);
                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    message = message + "\n\n" + error.getDescription();
                }
                showDialog(message);
                super.onReceivedError(mWebView, request, error);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                myRequest = request;

                for (String permission : request.getResources()) {
                    switch (permission) {
                        case "android.webkit.resource.AUDIO_CAPTURE":
                            requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_RECORD_AUDIO);
                            break;
                    }
                }
            }

        });
    }

    private void setCookie() {
        String cookieString = "E06=" + AppManager.getInstance().getSessionData().getUserManager().getUserToken() + "; path=/";
        CookieManager.getInstance().setCookie(mUrl, cookieString);
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Material_Light_Dialog));

        builder.setMessage(R.string.confirm_log_out)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (getActivity() != null) {
                            ((WebViewActivity) getActivity()).doLogOut();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        builder.create().show();
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Material_Light_Dialog));
        builder.setMessage(message)
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mWebView.canGoBack()) {
                            mWebView.goBack();
                        }
                    }
                });
        builder.create().show();
    }
}
