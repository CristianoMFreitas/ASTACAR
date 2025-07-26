package com.taxicaraubas.chamataxi;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        // Inicializa o Firebase, se ainda não estiver inicializado
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
            Log.d(TAG, "FirebaseApp inicializado");
        }
    }
}
