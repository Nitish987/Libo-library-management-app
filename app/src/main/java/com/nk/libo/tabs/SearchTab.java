package com.nk.libo.tabs;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.nk.libo.NewLibraryActivity;
import com.nk.libo.R;
import com.nk.libo.SearchLibraryActivity;
import com.nk.libo.adapter.LibraryAdapter;
import com.nk.libo.auth.Auth;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.Library;
import com.nk.libo.utils.Assure;

import java.util.ArrayList;
import java.util.List;

public class SearchTab extends Fragment {

    private EditText searchText;
    private ImageButton searchBtn;
    private FloatingActionButton addLibraryBtn;
    private RecyclerView recyclerView;
    private ImageView noData;

    private List<Library> libraries;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        libraries = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_search, container, false);

        searchText = view.findViewById(R.id.search);
        searchBtn = view.findViewById(R.id.search_btn);
        addLibraryBtn = view.findViewById(R.id.add_Library);
        recyclerView = view.findViewById(R.id.library_rv);
        noData = view.findViewById(R.id.no_data);

        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        addLibraryBtn.setOnClickListener(view -> startActivity(new Intent(getContext(), NewLibraryActivity.class)));

        searchBtn.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(searchText.getText())) {
                startActivity(new Intent(getContext(), SearchLibraryActivity.class).putExtra("search", searchText.getText().toString()));
            }
        });

        Query query = Database.getInstance().collection("library").whereArrayContains("admin", Auth.getCurrentUserUid());
        Database.readCollection(query, new Assure() {
            @Override
            public <T> void accept(T result) {
                List<DocumentSnapshot> snapshots = (List<DocumentSnapshot>) result;
                libraries.clear();
                for (DocumentSnapshot snap: snapshots) {
                    libraries.add((Library) snap.toObject(Library.class));
                }
                LibraryAdapter adapter = new LibraryAdapter(libraries);
                recyclerView.setAdapter(adapter);

                if (adapter.getItemCount() == 0) noData.setVisibility(View.VISIBLE);
                else noData.setVisibility(View.GONE);
            }

            @Override
            public void reject(String error) {
                Toast.makeText(getContext(), "Unable to get Your Library.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}