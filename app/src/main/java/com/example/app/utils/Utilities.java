package com.example.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public abstract class Utilities {

    public static Activity getActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    // method to close keyboard
    public static void closeKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // to clear focus
    // parent layout in xml file must have attributes
    // android:focusable="true"
    // android:focusableInTouchMode="true"
    public static void clearFocusAndHideKeyboard(View view) {
        Activity activity = Utilities.getActivity(view);
        if (activity != null) {
            Utilities.closeKeyboard(activity);
            view.clearFocus();
        }
    }

    public static String formattedURLString(String url) {
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            url = "https://" + url;
        }
        return url;
    }

}
