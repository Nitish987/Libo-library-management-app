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
import com.google.firebase.firestore.model.Document;
import com.nk.libo.auth.Auth;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.Library;
import com.nk.libo.utils.Assure;
import com.nk.libo.utils.Utility;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class NewLibraryActivity extends AppCompatActivity {

    private ImageView imageView;
    private FloatingActionButton imagePickBtn;
    private EditText nameText, collegeText, fineText, phoneText;
    private Button createLibraryBtn, cancelBtn;

    private ActivityResultLauncher<Intent> imagePicIntentResult;
    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_library);

        imageView = findViewById(R.id.image_pic);
        imagePickBtn = findViewById(R.id.image_picker);
        nameText = findViewById(R.id.name);
        collegeText = findViewById(R.id.college);
        fineText = findViewById(R.id.fine);
        phoneText = findViewById(R.id.phone);
        createLibraryBtn = findViewById(R.id.create_library);
        cancelBtn = findViewById(R.id.cancel);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

        createLibraryBtn.setOnClickListener(view -> {
            String name, college, fine, phone;
            name = nameText.getText().toString();
            college = collegeText.getText().toString();
            fine = fineText.getText().toString();
            phone = phoneText.getText().toString();
            if (name.equals("") || college.equals("") || fine.equals("") || phone.equals("")) {
                Toast.makeText(this, "Fields are Empty", Toast.LENGTH_SHORT).show();
            } else {
                createLibrary(name, college, fine, phone);
            }
        });

        cancelBtn.setOnClickListener(view -> finish());
    }

    private void createLibrary(String name, String college, String fine, String phone) {
        String id = Utility.getRandomString(20);
        String path = "/user/" + Auth.getCurrentUserUid() + "/library";
        Database.Storage.put(path, "pic.png", imageUri, new Assure() {
            @Override
            public <T> void accept(T result) {
                Uri photoUrl = (Uri) result;
                String path = "/library/" + id;
                Library library = new Library(
                        Collections.singletonList(Auth.getCurrentUserUid()), 0, college.toLowerCase(),
                        id, Integer.parseInt(fine), name.toLowerCase(), phone, photoUrl.toString(),
                        Arrays.asList(name.split(" ")[0].toLowerCase(), college.toLowerCase(), name.toLowerCase())
                );
                Database.create(path, library, new Assure() {
                    @Override
                    public <T> void accept(T result) {
                        Toast.makeText(NewLibraryActivity.this, "Library Created", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void reject(String error) {
                        Toast.makeText(NewLibraryActivity.this, "Something went wrong! Please Try again Later.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void reject(String error) {
                Toast.makeText(NewLibraryActivity.this, "Please Select the Library Photo.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}









