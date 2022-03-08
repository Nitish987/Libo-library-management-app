package com.nk.libo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.nk.libo.auth.Auth;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.Recent;
import com.nk.libo.holder.RecentHolder;
import com.nk.libo.sheet.AdminBookReturnSheet;
import com.nk.libo.sheet.BookReturnSheet;

public class AdminRecentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText searchTxt;
    private ImageButton searchBtn;

    private String libraryId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_recent);

        libraryId = getIntent().getStringExtra("libraryId");

        searchTxt = findViewById(R.id.search_text);
        searchBtn = findViewById(R.id.search_btn);

        recyclerView = findViewById(R.id.recent_book_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onStart() {
        super.onStart();
        searchBtn.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(searchTxt.getText())) {
                load(searchTxt.getText().toString());
            }
        });

        load("");
    }

    private void load(String search) {
        String path = "library/" + libraryId + "/recent/";
        Query query;
        if (search.equals("")) {
            query = Database.getInstance().collection(path).orderBy("time", Query.Direction.DESCENDING);
        } else {
            query = Database.getInstance().collection(path).whereArrayContains("search", search).orderBy("time", Query.Direction.DESCENDING);
        }
        FirestoreRecyclerOptions<Recent> options = new FirestoreRecyclerOptions.Builder<Recent>().setQuery(query, Recent.class).build();
        FirestoreRecyclerAdapter<Recent, RecentHolder> adapter = new FirestoreRecyclerAdapter<Recent, RecentHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecentHolder holder, int position, @NonNull Recent model) {
                holder.setSubmitTime(model.getSubmitTime());

                Database.getInstance().document("library/" + model.getLib() + "/book/" + model.getBookId()).get().addOnSuccessListener(snap -> {
                    if (snap.exists()) {
                        holder.setBookName(snap.get("name").toString());
                        holder.setBookAuthor(snap.get("author").toString());
                        holder.setPhoto(snap.get("photoUrl").toString());
                    }
                });

                holder.view.setOnClickListener(view -> {
                    AdminBookReturnSheet.newInstance(model).show(getSupportFragmentManager(), "DIALOG");
                });
            }

            @NonNull
            @Override
            public RecentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent, parent, false));
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}