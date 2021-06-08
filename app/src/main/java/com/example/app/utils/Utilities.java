package com.example.app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public abstract class Utilities {

    // method to close keyboard
    public static void closeKeyboard(View view) {
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // to clear focus
    // parent layout in xml file must have attributes
    // android:focusable="true"
    // android:focusableInTouchMode="true"
    public static void clearFocusAndHideKeyboard(View view) {
        if (view != null) {
            Utilities.closeKeyboard(view);
            view.clearFocus();
        }
    }

    public static String formattedURLString(String url) {
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            url = "https://" + url;
        }
        return url;
    }

    public static boolean isNetworkConnected(View view) {
        ConnectivityManager cm = (ConnectivityManager) view.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static double bytesToMegaBytes(int bytes) {
        return bytes / 1048576.0;
    }

}
