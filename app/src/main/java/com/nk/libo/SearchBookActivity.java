package com.nk.libo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.Book;
import com.nk.libo.holder.BookHolder;
import com.nk.libo.sheet.BookDetailSheet;

import java.util.Arrays;

public class SearchBookActivity extends AppCompatActivity {

    private RecyclerView booksRV;
    private ImageButton searchButton;
    private EditText queryText;
    private Spinner category;

    private String libraryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);

        libraryID = getIntent().getStringExtra("libraryID");


        queryText = findViewById(R.id.search_text);
        searchButton = findViewById(R.id.search_button);
        category = findViewById(R.id.category);
        booksRV = findViewById(R.id.books_rv);

        booksRV.setLayoutManager(new LinearLayoutManager(this));
        booksRV.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        searchButton.setOnClickListener(view -> {
            if (TextUtils.isEmpty(queryText.getText())) {
                Toast.makeText(this, "Search field is empty.", Toast.LENGTH_SHORT).show();
            } else {
                if (category.getSelectedItemPosition() == 0) {
                    loadSearch(queryText.getText().toString(), null, false);
                } else {
                    loadSearch(queryText.getText().toString(), category.getSelectedItem().toString(), true);
                }
            }
        });

        loadSearch("", null, false);
    }

    private void loadSearch(String queryText, String category, boolean isCategorized) {
        String path = "library/" + libraryID + "/book";
        Query query;
        if (!queryText.equals("")) {
            queryText = queryText.toLowerCase();
            String[] searchTags = queryText.split(" ");
            if (isCategorized) {
                category = category.toLowerCase();
                query = Database.getInstance().collection(path).whereArrayContainsAny("search", Arrays.asList(searchTags)).whereEqualTo("category", category);
            } else {
                query = Database.getInstance().collection(path).whereArrayContainsAny("search", Arrays.asList(searchTags));
            }
        } else {
            query = Database.getInstance().collection(path).orderBy("date", Query.Direction.DESCENDING);
        }
        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>().setQuery(query, Book.class).build();
        FirestoreRecyclerAdapter<Book, BookHolder> adapter = new FirestoreRecyclerAdapter<Book, BookHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BookHolder holder, int position, @NonNull Book model) {
                holder.setPhoto(model.getPhotoUrl());
                holder.setBookName(model.getName());
                holder.setBookAuthor(model.getAuthor());
                holder.setBookCategory(model.getCategory());
                holder.setBookCS(model.getbClass());

                holder.view.setOnClickListener(view -> {
                    BookDetailSheet.getInstance(model).show(getSupportFragmentManager(), "DIALOG");
                });
            }

            @NonNull
            @Override
            public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
                return new BookHolder(view);
            }
        };
        adapter.startListening();
        booksRV.setAdapter(adapter);
    }
}