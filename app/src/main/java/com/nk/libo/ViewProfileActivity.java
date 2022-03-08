package com.nk.libo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nk.libo.auth.Auth;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.User;
import com.nk.libo.utils.Assure;

public class ViewProfileActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView nameText, usernameText, departmentText, libraryIdText, emailText, phoneText, addressText;

    private String uuid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        uuid = getIntent().getStringExtra("UUID");

        imageView = findViewById(R.id.image_pic);
        nameText = findViewById(R.id.name);
        usernameText = findViewById(R.id.username);
        departmentText = findViewById(R.id.department);
        libraryIdText = findViewById(R.id.library_id);
        emailText = findViewById(R.id.email);
        phoneText = findViewById(R.id.phone);
        addressText = findViewById(R.id.address);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String path = "/user/" + uuid;
        Database.read(path, new User(), new Assure() {
            @Override
            public <T> void accept(T result) {
                User user = (User) result;
                nameText.setText(user.getName());
                usernameText.setText(user.getUsername());
                departmentText.setText(user.getDepartment());
                libraryIdText.setText(user.getLibraryId());
                emailText.setText(user.getEmail());
                phoneText.setText(user.getPhone());
                addressText.setText(user.getAddress());

                if (!user.getPhotoUrl().equals("")) {
                    Glide.with(getApplicationContext()).load(user.getPhotoUrl()).into(imageView);
                }
            }

            @Override
            public void reject(String error) {
                Log.e("Error", "reject: " + error);
            }
        });
    }
}