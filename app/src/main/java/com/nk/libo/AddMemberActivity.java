package com.nk.libo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nk.libo.auth.Auth;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.Library;
import com.nk.libo.db.model.Member;
import com.nk.libo.utils.Assure;
import com.nk.libo.utils.Utility;

import java.util.HashMap;
import java.util.Map;

public class AddMemberActivity extends AppCompatActivity {
    private EditText username;
    private Button addMember, cancel;

    private Library library;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        library = (Library) getIntent().getSerializableExtra("library");

        username = findViewById(R.id.username);
        addMember = findViewById(R.id.add_member);
        cancel = findViewById(R.id.cancel);
    }

    @Override
    protected void onStart() {
        super.onStart();
        addMember.setOnClickListener(view -> {
            String u = username.getText().toString();
            if (u.equals("")) {
                Toast.makeText(this, "Email required!", Toast.LENGTH_SHORT).show();
            } else {
                Query query = Database.getInstance().collection("user").whereEqualTo("username", u);
                query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snap : queryDocumentSnapshots) {
                        if (snap.exists() && !snap.get("uid").toString().equals(Auth.getCurrentUserUid())) {
                            String uid = snap.get("uid").toString();
                            Member member = new Member(uid);
                            Database.create("library/" + library.getId() + "/member/" + uid, member, new Assure() {
                                @Override
                                public <T> void accept(T result) {
                                    Map<String, Object> map = new HashMap<>();
                                    library.getAdmin().add(uid);
                                    map.put("admin", library.getAdmin());
                                    Database.update("library/" + library.getId(), map, new Assure() {
                                        @Override
                                        public <T> void accept(T result) {
                                            Toast.makeText(AddMemberActivity.this, "Member added successfully.", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }

                                        @Override
                                        public void reject(String error) {
                                            Toast.makeText(AddMemberActivity.this, "something went wrong.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void reject(String error) {
                                    Toast.makeText(AddMemberActivity.this, "something went wrong!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    }
                });
            }
        });

        cancel.setOnClickListener(view -> finish());
    }
}