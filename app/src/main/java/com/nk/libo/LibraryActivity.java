package com.nk.libo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.Book;
import com.nk.libo.db.model.Library;
import com.nk.libo.holder.BookHolder;
import com.nk.libo.sheet.BookDetailSheet;
import com.nk.libo.utils.Assure;

public class LibraryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView photoView;
    private RecyclerView recyclerView;

    private String libraryID;
    private Library library;
    private FirestoreRecyclerAdapter<Book, BookHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        libraryID = getIntent().getStringExtra("libraryID");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        photoView = findViewById(R.id.photo);
        recyclerView = findViewById(R.id.recently_books_rv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        load();
    }

    private void load() {
        String path = "/library/" + libraryID;
        Database.read(path, new Library(), new Assure() {
            @Override
            public <T> void accept(T result) {
                library = (Library) result;
                toolbar.setTitle(library.getName().toUpperCase());
                if (!library.getPhotoUrl().equals(""))
                    Glide.with(getApplicationContext()).load(library.getPhotoUrl()).into(photoView);
            }

            @Override
            public void reject(String error) {
                Toast.makeText(LibraryActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

        Query query = Database.getInstance().collection("/library/" + libraryID + "/book").orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>().setQuery(query, Book.class).build();
        adapter = new FirestoreRecyclerAdapter<Book, BookHolder>(options) {
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
                return new BookHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false));
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.library_menu, menu);
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);
        menu.getItem(3).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search)
            startActivity(new Intent(this, SearchBookActivity.class).putExtra("libraryID", libraryID));

        return super.onOptionsItemSelected(item);
    }
}