package com.example.app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Patterns;
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

    // returns true if given string is valid url address
    // else returns false
    public static boolean isURLStringValid(String urlAddress) {
        return Patterns.WEB_URL.matcher(urlAddress).matches();
    }

    // if given string isn't prefixed with "https://" or "http://"
    // returns string with "https://" prefix
    // else returns given string
    public static String formattedURLString(String urlAddress) {
        if (!urlAddress.startsWith("https://") && !urlAddress.startsWith("http://")) {
            urlAddress = "https://" + urlAddress;
        }
        return urlAddress;
    }

    // returns true if device is connected to network
    // else returns false
    public static boolean isNetworkConnected(View view) {
        ConnectivityManager cm = (ConnectivityManager) view.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    // converts size in bytes to size in megabytes
    public static double bytesToMegaBytes(int bytes) {
        return bytes / 1048576.0;
    }

    // returns what percent of secondNumber is firstNumber
    public static int percentOfNumber(int firstNumber, int secondNumber) {
        return (int) (100 * (firstNumber / (double) secondNumber));
    }
}
