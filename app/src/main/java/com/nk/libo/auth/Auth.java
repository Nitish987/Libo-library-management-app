package com.nk.libo.auth;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nk.libo.utils.Promise;

public class Auth {
    private final Activity activity;

    public Auth(Activity activity) {
        this.activity = activity;
    }

    public void login(String email, String password, Promise promise) {
        promise.inProcess();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) promise.resolve();
            else promise.reject();
        });
    }

    public void signup(String email, String password, Promise promise) {
        promise.inProcess();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) promise.resolve();
            else promise.reject();
        });
    }

    public static FirebaseAuth getInstance() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    public static String getCurrentUserUid() {
        return getCurrentUser().getUid();
    }

    public static void setAuthStateListener(FirebaseAuth.AuthStateListener listener) {
        getInstance().addAuthStateListener(listener);
    }

    public static void logout() {
        getInstance().signOut();
    }
}
