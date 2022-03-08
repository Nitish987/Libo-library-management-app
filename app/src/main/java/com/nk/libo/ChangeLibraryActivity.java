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
import com.nk.libo.db.model.Library;
import com.nk.libo.utils.Assure;
import com.nk.libo.utils.Utility;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChangeLibraryActivity extends AppCompatActivity {

    private ImageView imageView;
    private FloatingActionButton imagePickBtn;
    private EditText nameText, collegeText, fineText, phoneText;
    private Button updateLibraryBtn, cancelBtn;

    private Library library;
    private String name, college, fine, phone;
    private ActivityResultLauncher<Intent> imagePicIntentResult;
    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_library);

        library = (Library) getIntent().getSerializableExtra("library");

        imageView = findViewById(R.id.image_pic);
        imagePickBtn = findViewById(R.id.image_picker);
        nameText = findViewById(R.id.name);
        collegeText = findViewById(R.id.college);
        fineText = findViewById(R.id.fine);
        phoneText = findViewById(R.id.phone);
        updateLibraryBtn = findViewById(R.id.update_library);
        cancelBtn = findViewById(R.id.cancel);
    }

    @Override
    protected void onStart() {
        super.onStart();
        nameText.setText(library.getName());
        collegeText.setText(library.getCollege());
        fineText.setText(Integer.toString(library.getLateFine()));
        phoneText.setText(library.getPhone());
        if (!library.getPhotoUrl().equals(""))
            Glide.with(getApplicationContext()).load(library.getPhotoUrl()).into(imageView);

        imagePicIntentResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            imageUri = intent.getData();
            Glide.with(this).load(imageUri).into(imageView);
        });

        imagePickBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePicIntentResult.launch(intent);
        });

        updateLibraryBtn.setOnClickListener(view -> {
            name = nameText.getText().toString();
            college = collegeText.getText().toString();
            fine = fineText.getText().toString();
            phone = phoneText.getText().toString();
            if (name.equals("") || college.equals("") || fine.equals("") || phone.equals("")) {
                Toast.makeText(this, "Fields are Empty", Toast.LENGTH_SHORT).show();
            } else {
                updateLibrary();
            }
        });

        cancelBtn.setOnClickListener(view -> finish());
    }

    private void updateLibrary() {
        String path = "/user/" + Auth.getCurrentUserUid() + "/library";
        Database.Storage.put(path, "pic.png", imageUri, new Assure() {
            @Override
            public <T> void accept(T result) {
                Uri photoUrl = (Uri) result;
                updateLibraryData(photoUrl.toString());
            }

            @Override
            public void reject(String error) {
                updateLibraryData(library.getPhotoUrl());
                finish();
            }
        });
    }

    private void updateLibraryData(String photoUrl) {
        String path = "/library/" + library.getId();
        Map<String, Object> map = new HashMap<>();
        map.put("name", name.toLowerCase());
        map.put("college", college.toLowerCase());
        map.put("lateFine", Integer.parseInt(fine));
        map.put("phone", phone);
        map.put("photoUrl", photoUrl);
        map.put("search", Arrays.asList(name.split(" ")[0].toLowerCase(), college.toLowerCase(), name.toLowerCase()));
        Database.update(path, map, new Assure() {
            @Override
            public <T> void accept(T result) {
                Toast.makeText(ChangeLibraryActivity.this, "Library Updated", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void reject(String error) {
                Toast.makeText(ChangeLibraryActivity.this, "Something went wrong! Please Try again Later.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}