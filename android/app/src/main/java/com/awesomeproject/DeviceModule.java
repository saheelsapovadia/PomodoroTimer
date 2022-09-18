package com.awesomeproject;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class DeviceModule extends ReactContextBaseJavaModule {

    private static ReactApplicationContext reactContext;
    DeviceModule(ReactApplicationContext reactApplicationContext){
        super(reactApplicationContext);
        reactContext = reactApplicationContext;
    }

    @ReactMethod
    public void showToast(String name){
        Toast.makeText(reactContext, "Hello " + name + ", Toast from native side", Toast.LENGTH_LONG).show();
    }


    @NonNull
    @Override
    public String getName() {
        return "DeviceModule";
    }
}
