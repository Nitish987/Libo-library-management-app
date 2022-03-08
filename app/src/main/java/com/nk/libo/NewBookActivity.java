package com.nk.libo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nk.libo.auth.Auth;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.Book;
import com.nk.libo.utils.Assure;
import com.nk.libo.utils.Utility;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewBookActivity extends AppCompatActivity {

    private EditText nameText, authorText, isbnText, quantityText, pubText, bookNumText, classText;
    private Spinner categorySpinner;
    private Button addBookBtn, cancelBtn;
    private ImageView photoView;
    private FloatingActionButton pickPhotoBtn;
    private ImageButton pickPdfBtn;
    private TextView pdfNameText;

    private int bookCount;
    private String libraryID, category = "others", name, author, isbn, quantity, pub, bookNum, bClass;
    private ActivityResultLauncher<Intent> photoClickIntentResult, pickPdfIntentResult;
    private Bitmap photo;
    private Uri photoUri = null, pdfUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_book);

        libraryID = getIntent().getStringExtra("libraryID");
        bookCount = getIntent().getIntExtra("bookCount", 0);

        categorySpinner = findViewById(R.id.category);
        nameText = findViewById(R.id.name);
        authorText = findViewById(R.id.author);
        isbnText = findViewById(R.id.isbn);
        quantityText = findViewById(R.id.quantity);
        pubText = findViewById(R.id.publication);
        bookNumText = findViewById(R.id.book_number);
        classText = findViewById(R.id.book_class);
        addBookBtn = findViewById(R.id.add_book);
        photoView = findViewById(R.id.photo);
        pickPhotoBtn = findViewById(R.id.change_photo);
        pickPdfBtn = findViewById(R.id.pick_book_pdf);
        pdfNameText = findViewById(R.id.book_pdf_name);
        cancelBtn = findViewById(R.id.cancel);
    }

    @SuppressLint("Range")
    @Override
    protected void onStart() {
        super.onStart();
        photoClickIntentResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            photo = (Bitmap) intent.getExtras().get("data");
            photoUri = getImageUri(this, photo);
            Glide.with(getApplicationContext()).load(photo).into(photoView);
        });

        pickPhotoBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoClickIntentResult.launch(intent);
        });

        pickPdfIntentResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            pdfUri = intent.getData();
            Cursor cursor = getContentResolver().query(pdfUri, null, null, null, null);
            int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            pdfNameText.setText(cursor.getString(index));
        });

        pickPdfBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            pickPdfIntentResult.launch(intent);
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = categorySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                category = "others";
            }
        });

        addBookBtn.setOnClickListener(view -> {
            name = nameText.getText().toString();
            author = authorText.getText().toString();
            isbn = isbnText.getText().toString();
            quantity = quantityText.getText().toString();
            pub = pubText.getText().toString();
            bookNum = bookNumText.getText().toString();
            bClass = classText.getText().toString();
            if (name.equals("") || author.equals("") || isbn.equals("") || quantity.equals("") || pub.equals("") || bookNum.equals("") || bClass.equals("")) {
                Toast.makeText(this, "Fields are empty!", Toast.LENGTH_SHORT).show();
            } else {
                if (photoUri == null) {
                    Toast.makeText(this, "Book Cover Photo is not selected!", Toast.LENGTH_SHORT).show();
                } else {
                    createBook();
                }
            }
        });

        cancelBtn.setOnClickListener(view -> finish());
    }

    private void createBook() {
        String path = "/user/" + Auth.getCurrentUserUid() + "/book", filename = Utility.getRandomString(20);
        Database.Storage.put(path, filename + ".png", photoUri, new Assure() {
            @Override
            public <T> void accept(T result) {
                Uri photoUrl = (Uri) result;
                Database.Storage.put(path, filename + ".pdf", pdfUri, new Assure() {
                    @Override
                    public <T> void accept(T result) {
                        Uri pdfUrl = (Uri) result;
                        createBookData(photoUrl.toString(), pdfUrl.toString());
                    }

                    @Override
                    public void reject(String error) {
                        createBookData(photoUrl.toString(), "");
                    }
                });
            }

            @Override
            public void reject(String error) {
                Toast.makeText(NewBookActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createBookData(String photoUrl, String pdfUrl) {
        String id = Utility.getRandomString(20);
        String path = "/library/" + libraryID + "/book/" + id;
        Book book = new Book(
            author, bClass, category.toLowerCase(), System.currentTimeMillis(), id, isbn, 0, libraryID, name, bookNum, pdfUrl, photoUrl,
                pub, Integer.parseInt(quantity), Arrays.asList(category.toLowerCase(), name.split(" ")[0].toLowerCase(), author.toLowerCase())
        );
        Database.create(path, book, new Assure() {
            @Override
            public <T> void accept(T result) {
                String path = "/library/" + libraryID;
                Map<String, Object> map = new HashMap<>();
                map.put("bookCount", bookCount + 1);
                Database.update(path, map, new Assure() {
                    @Override
                    public <T> void accept(T result) {
                        Toast.makeText(NewBookActivity.this, "Book Added Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void reject(String error) {
                        Toast.makeText(NewBookActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void reject(String error) {
                Toast.makeText(NewBookActivity.this, "something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}