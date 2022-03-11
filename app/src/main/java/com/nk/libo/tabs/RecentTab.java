package com.nk.libo.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.nk.libo.R;
import com.nk.libo.auth.Auth;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.Recent;
import com.nk.libo.holder.RecentHolder;
import com.nk.libo.sheet.BookReturnSheet;

public class RecentTab extends Fragment {

    private RecyclerView recyclerView;
    private ImageView noData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_recent, container, false);

        noData = view.findViewById(R.id.no_data);
        recyclerView = view.findViewById(R.id.recent_book_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        String path = "user/" + Auth.getCurrentUserUid() + "/recent";
        Query query = Database.getInstance().collection(path).orderBy("time", Query.Direction.DESCENDING);
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
                    BookReturnSheet.newInstance(model).show(getParentFragmentManager(), "DIALOG");
                });
            }

            @NonNull
            @Override
            public Recent getItem(int position) {
                if (position >= 0) noData.setVisibility(View.GONE);
                return super.getItem(position);
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













