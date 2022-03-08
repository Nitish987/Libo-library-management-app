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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.Book;
import com.nk.libo.db.model.Library;
import com.nk.libo.holder.BookHolder;
import com.nk.libo.sheet.BookDetailSheet;
import com.nk.libo.utils.Assure;

public class LibraryAdminActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton addMemberBtn, changeLibraryBtn;
    private ImageButton addBookBtn;
    private ImageView photoView;
    private RecyclerView recyclerView;

    private String libraryID;
    private Library library;
    private FirestoreRecyclerAdapter<Book, BookHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_admin);

        libraryID = getIntent().getStringExtra("libraryID");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addMemberBtn = findViewById(R.id.add_member);
        changeLibraryBtn = findViewById(R.id.change_library);
        photoView = findViewById(R.id.photo);
        addBookBtn = findViewById(R.id.add_book);
        recyclerView = findViewById(R.id.recently_books_rv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        load();

        changeLibraryBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChangeLibraryActivity.class);
            intent.putExtra("library", library);
            startActivity(intent);
        });

        addBookBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewBookActivity.class);
            intent.putExtra("libraryID", libraryID);
            intent.putExtra("bookCount", library.getBookCount());
            startActivity(intent);
        });

        addMemberBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddMemberActivity.class);
            intent.putExtra("library", library);
            startActivity(intent);
        });
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
                Toast.makeText(LibraryAdminActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search)
            startActivity(new Intent(this, SearchBookActivity.class).putExtra("libraryID", libraryID));
        else if (id == R.id.lib_notification)
            startActivity(new Intent(this, LibraryNotificationActivity.class).putExtra("library", libraryID));
        else if (id == R.id.recent)
            startActivity(new Intent(this, AdminRecentActivity.class).putExtra("libraryId", libraryID));
        else if (id == R.id.member)
            startActivity(new Intent(this, MembersActivity.class).putExtra("libraryId", libraryID));
        return super.onOptionsItemSelected(item);
    }
}