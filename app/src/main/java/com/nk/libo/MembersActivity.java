package com.nk.libo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.Member;
import com.nk.libo.holder.MemberHolder;
import com.nk.libo.sheet.MemberSheet;

public class MembersActivity extends AppCompatActivity {
    private RecyclerView membersRV;

    private String libraryId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        libraryId = getIntent().getStringExtra("libraryId");

        membersRV = findViewById(R.id.member_rv);
        membersRV.setLayoutManager(new LinearLayoutManager(this));
        membersRV.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String path = "library/" + libraryId + "/member";
        Query query = Database.getInstance().collection(path);
        FirestoreRecyclerOptions<Member> options = new FirestoreRecyclerOptions.Builder<Member>().setQuery(query, Member.class).build();
        FirestoreRecyclerAdapter<Member, MemberHolder> adapter = new FirestoreRecyclerAdapter<Member, MemberHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MemberHolder holder, int position, @NonNull Member model) {
                String path = "user/" + model.getUid();
                Database.getInstance().document(path).get().addOnSuccessListener(snap -> {
                    holder.setPhoto(snap.get("photoUrl").toString());
                    holder.setName(snap.get("name").toString());
                });

                holder.itemView.setOnClickListener(view -> {
                    MemberSheet.newInstance(model.getUid(), libraryId).show(getSupportFragmentManager(), "DIALOG");
                });
            }

            @NonNull
            @Override
            public MemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
                return new MemberHolder(view);
            }
        };
        adapter.startListening();
        membersRV.setAdapter(adapter);
    }
}