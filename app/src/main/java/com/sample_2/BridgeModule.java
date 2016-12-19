package com.sample_2;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

class BridgeModule extends ReactContextBaseJavaModule {
    private static final String TAG = "BridgeModule";

    private final Resizable resizable;

    BridgeModule(ReactApplicationContext reactContext, Resizable resizable) {
        super(reactContext);
        this.resizable = resizable;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @ReactMethod
    public void onLayoutChanged(final ReadableMap layout) {
        double argHeight = layout.getDouble("height");
        double argWidth = layout.getDouble("width");
        Log.d(TAG, "onLayoutChanged() called with: layout.width = ["
                + argWidth
                + "], layout.height = ["
                + argHeight
                + "]");

        final int[] sizes = convertToDp((float) argWidth, (float) argHeight);
        getReactApplicationContext().runOnUiQueueThread(new Runnable() {
            @Override
            public void run() {
                resizable.onSizeChanged(sizes[0], sizes[1]);
            }
        });
    }

    // TODO just for testing purposes, not very efficient nor accurate
    private int[] convertToDp(float argWidth, float argHeight) {
        WindowManager wm = (WindowManager) getReactApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        DisplayMetrics displayMetrics =
                getReactApplicationContext().getResources().getDisplayMetrics();

        final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, argWidth + 5,
                displayMetrics);

        final int newHeight =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, argHeight + 5,
                        displayMetrics);

        final int maxWidth =
                (int) (display.getWidth() - (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        5, displayMetrics) * 10));

        final int newWidth = width > maxWidth ? maxWidth : width;

        return new int[] { newWidth, newHeight };
    }
}