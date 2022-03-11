package com.nk.libo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.Issue;
import com.nk.libo.holder.LibraryNotificationHolder;
import com.nk.libo.sheet.IssueAcceptSheet;
import com.nk.libo.utils.FutureDate;

public class LibraryNotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView noData;

    private String libraryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_notification);

        libraryId = getIntent().getStringExtra("library");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        noData = findViewById(R.id.no_data);

        recyclerView = findViewById(R.id.library_notification_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String path = "library/" + libraryId + "/notification";
        Query query = Database.getInstance().collection(path).orderBy("issueTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Issue> options = new FirestoreRecyclerOptions.Builder<Issue>().setQuery(query, Issue.class).build();
        FirestoreRecyclerAdapter<Issue, LibraryNotificationHolder> adapter = new FirestoreRecyclerAdapter<Issue, LibraryNotificationHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LibraryNotificationHolder holder, int position, @NonNull Issue model) {
                holder.setIssueTime(FutureDate.toDate(model.getIssueTime()));
                holder.setSubmitTime(FutureDate.toDate(model.getSubmitTime()));

                Database.getInstance().document("user/" + model.getBy()).get().addOnSuccessListener(snap -> {
                    String username = snap.get("name").toString();
                    holder.setUsername(username);
                });

                Database.getInstance().document("library/" + libraryId + "/book/" + model.getBookId()).get().addOnSuccessListener(snap -> {
                    String bookName = snap.get("name").toString();
                    holder.setBookName(bookName);
                });

                holder.view.setOnClickListener(view -> {
                    IssueAcceptSheet.getInstance(model).show(getSupportFragmentManager(), "DIALOG");
                });
            }

            @NonNull
            @Override
            public Issue getItem(int position) {
                if (position >= 0) noData.setVisibility(View.GONE);
                return super.getItem(position);
            }

            @NonNull
            @Override
            public LibraryNotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new LibraryNotificationHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library_notification, parent, false));
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}

















