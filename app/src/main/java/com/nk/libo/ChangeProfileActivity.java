package com.nk.libo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nk.libo.auth.Auth;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.User;
import com.nk.libo.utils.Assure;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ChangeProfileActivity extends AppCompatActivity {

    private ImageView imageView;
    private FloatingActionButton imagePickerBtn;
    private EditText nameText, departmentText, libraryIdText, phoneText, addressText;
    private Button updateProfileBtn;

    private String name, department, libraryID, phone, address;
    private ActivityResultLauncher<Intent> pickImageIntentResult;
    private Uri imageUri = null;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        user = (User) getIntent().getSerializableExtra("user");

        imageView = findViewById(R.id.image_pic);
        imagePickerBtn = findViewById(R.id.image_picker);
        nameText = findViewById(R.id.name);
        departmentText = findViewById(R.id.department);
        libraryIdText = findViewById(R.id.library_id);
        phoneText = findViewById(R.id.phone);
        addressText = findViewById(R.id.address);
        updateProfileBtn = findViewById(R.id.update_profile);
    }

    @Override
    protected void onStart() {
        super.onStart();
        nameText.setText(user.getName());
        departmentText.setText(user.getDepartment());
        libraryIdText.setText(user.getLibraryId());
        phoneText.setText(user.getPhone());
        addressText.setText(user.getAddress());

        if (!user.getPhotoUrl().equals("")) {
            Glide.with(this).load(user.getPhotoUrl()).into(imageView);
        }

        pickImageIntentResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            imageUri = intent.getData();
            Glide.with(getApplicationContext()).load(imageUri).into(imageView);
        });

        imagePickerBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            pickImageIntentResult.launch(intent);
        });

        updateProfileBtn.setOnClickListener(view -> {
            name = nameText.getText().toString();
            department = departmentText.getText().toString();
            libraryID = libraryIdText.getText().toString();
            phone = phoneText.getText().toString();
            address = addressText.getText().toString();
            if (name.equals("") || department.equals("") || libraryID.equals("") || phone.equals("") || address.equals("")) {
                Toast.makeText(this, "Fields are Empty.", Toast.LENGTH_SHORT).show();
            } else {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        String path = "/user/" + Auth.getCurrentUserUid() + "/profile";
        Database.Storage.put(path, "pic.png", imageUri, new Assure() {
            @Override
            public <T> void accept(T result) {
                Uri photoUri = (Uri) result;
                updateProfileData(photoUri.toString());
            }

            @Override
            public void reject(String error) {
                updateProfileData(user.getPhotoUrl());
            }
        });
    }

    private void updateProfileData(String photoUrl) {
        String path = "/user/" + Auth.getCurrentUserUid();
        Map<String, Object> map = new HashMap<>();
        map.put("address", address);
        map.put("department", department);
        map.put("libraryId", libraryID);
        map.put("name", name);
        map.put("phone", phone);
        map.put("photoUrl", photoUrl);
        Database.update(path, map, new Assure() {
            @Override
            public <T> void accept(T result) {
                Toast.makeText(ChangeProfileActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }

            @Override
            public void reject(String error) {
                Toast.makeText(ChangeProfileActivity.this, "Something went wrong! Please try again later.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}