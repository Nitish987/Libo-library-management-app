package com.nk.libo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.nk.libo.adapter.LibraryAdapter;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.Library;

public class SearchLibraryActivity extends AppCompatActivity {

    private EditText searchText;
    private ImageButton searchButton;
    private RecyclerView libraryRV;
    private ImageView noData;

    private String search = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_library);

        search = getIntent().getStringExtra("search");

        searchText = findViewById(R.id.search_text);
        searchButton = findViewById(R.id.search_button);
        libraryRV = findViewById(R.id.search_library_rv);
        noData = findViewById(R.id.no_data);

        searchText.setText(search);

        libraryRV.setLayoutManager(new LinearLayoutManager(this));
        libraryRV.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        searchButton.setOnClickListener(view -> {
            if (TextUtils.isEmpty(searchText.getText())) {
                Toast.makeText(this, "Search Field is empty.", Toast.LENGTH_SHORT).show();
            } else {
                loadSearch(searchText.getText().toString().trim());
            }
        });

        loadSearch(search.trim());
    }

    private void loadSearch(String search) {
        Query query = Database.getInstance().collection("library").whereArrayContains("search", search);
        FirestoreRecyclerOptions<Library> options = new FirestoreRecyclerOptions.Builder<Library>().setQuery(query, Library.class).build();
        FirestoreRecyclerAdapter<Library, LibraryAdapter.LibraryHolder> adapter = new FirestoreRecyclerAdapter<Library, LibraryAdapter.LibraryHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LibraryAdapter.LibraryHolder holder, int position, @NonNull Library model) {
                holder.setImage(model.getPhotoUrl());
                holder.setName(model.getName());
                holder.setCollege(model.getCollege());
                holder.view.setOnClickListener(view -> {
                    Intent intent = new Intent(SearchLibraryActivity.this, LibraryActivity.class);
                    intent.putExtra("libraryID", model.getId());
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public Library getItem(int position) {
                if (position >= 0) noData.setVisibility(View.GONE);
                return super.getItem(position);
            }

            @NonNull
            @Override
            public LibraryAdapter.LibraryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library, parent, false);
                return new LibraryAdapter.LibraryHolder(view);
            }
        };
        adapter.startListening();
        libraryRV.setAdapter(adapter);
    }
}