package com.nk.libo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;

import com.nk.libo.auth.Auth;
import com.nk.libo.sheet.LoginSheet;
import com.nk.libo.sheet.SignupSheet;

public class WelcomeActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button loginBtn, signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        videoView = findViewById(R.id.intro_video);
        loginBtn = findViewById(R.id.login);
        signupBtn = findViewById(R.id.signup);
    }

    @Override
    protected void onStart() {
        super.onStart();
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.library_intro_video));
        videoView.start();

        if (Auth.getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }

        loginBtn.setOnClickListener(view -> LoginSheet.getInstance().show(getSupportFragmentManager(), "login-dialog"));
        signupBtn.setOnClickListener(view -> SignupSheet.getInstance().show(getSupportFragmentManager(), "signup-dialog"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }
}