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

import java.util.Random;

public class NewProfileActivity extends AppCompatActivity {

    private ImageView imageView;
    private FloatingActionButton imagePickerBtn;
    private EditText nameText, departmentText, libraryIdText, phoneText, addressText;
    private Button createProfileBtn;

    private String name, department, libraryID, phone, address;
    private ActivityResultLauncher<Intent> pickImageIntentResult;
    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        imageView = findViewById(R.id.image_pic);
        imagePickerBtn = findViewById(R.id.image_picker);
        nameText = findViewById(R.id.name);
        departmentText = findViewById(R.id.department);
        libraryIdText = findViewById(R.id.library_id);
        phoneText = findViewById(R.id.phone);
        addressText = findViewById(R.id.address);
        createProfileBtn = findViewById(R.id.create_profile);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

        createProfileBtn.setOnClickListener(view -> {
            name = nameText.getText().toString();
            department = departmentText.getText().toString();
            libraryID = libraryIdText.getText().toString();
            phone = phoneText.getText().toString();
            address = addressText.getText().toString();
            if (name.equals("") || department.equals("") || libraryID.equals("") || phone.equals("") || address.equals("")) {
                Toast.makeText(this, "Fields are Empty.", Toast.LENGTH_SHORT).show();
            } else {
                createProfile();
            }
        });
    }

    private void createProfile() {
        String path = "/user/" + Auth.getCurrentUserUid() + "/profile";
        Database.Storage.put(path, "pic.png", imageUri, new Assure() {
            @Override
            public <T> void accept(T result) {
                Uri photoUri = (Uri) result;
                createProfileData(photoUri.toString());
            }

            @Override
            public void reject(String error) {
                createProfileData("");
            }
        });
    }

    private void createProfileData(String photoUri) {
        String path = "/user/" + Auth.getCurrentUserUid();
        Random random = new Random();
        User user = new User(
                address, department, Auth.getCurrentUserEmail(), libraryID, name, phone,
                photoUri, Auth.getCurrentUserUid(),
                name.split(" ")[0] + random.nextInt(10000)
        );
        Database.create(path, user, new Assure() {
            @Override
            public <T> void accept(T result) {
                User user = (User) result;
                Toast.makeText(NewProfileActivity.this, "Welcome "+ user.getName(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }

            @Override
            public void reject(String error) {
                Toast.makeText(NewProfileActivity.this, "Something went wrong! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}