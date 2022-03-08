package com.nk.libo.sheet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nk.libo.R;
import com.nk.libo.ViewProfileActivity;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.Book;
import com.nk.libo.db.model.Issue;
import com.nk.libo.db.model.Recent;
import com.nk.libo.db.model.User;
import com.nk.libo.utils.Assure;
import com.nk.libo.utils.FutureDate;
import com.nk.libo.utils.Utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class IssueAcceptSheet extends BottomSheetDialogFragment {

    private TextView bookName, bookAuthor, bookClass, issueTime, submitTime, username, libraryId;
    private Button issuerDetailsBtn, handoverBtn, rejectBtn;

    private Issue issue;
    private String iEmail, iUsername;
    private int bookIssueCount = 0;

    public static IssueAcceptSheet getInstance(Issue issue) {
        IssueAcceptSheet sheet = new IssueAcceptSheet();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ISSUE", issue);
        sheet.setArguments(bundle);
        return sheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            issue = (Issue) getArguments().getSerializable("ISSUE");
        }

        Database.read("user/" + issue.getBy(), new User(), new Assure() {
            @Override
            public <T> void accept(T result) {
                User user = (User) result;
                iEmail = user.getEmail();
                iUsername = user.getUsername();
            }

            @Override
            public void reject(String error) {}
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sheet_issue_accept, container, false);

        bookName = view.findViewById(R.id.book_name);
        bookAuthor = view.findViewById(R.id.book_author);
        bookClass = view.findViewById(R.id.book_class);
        issueTime = view.findViewById(R.id.issue_time);
        submitTime = view.findViewById(R.id.submit_time);
        username = view.findViewById(R.id.username);
        libraryId = view.findViewById(R.id.library_id);
        issuerDetailsBtn = view.findViewById(R.id.user_details);
        handoverBtn = view.findViewById(R.id.handover);
        rejectBtn = view.findViewById(R.id.reject);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        load();

        issuerDetailsBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ViewProfileActivity.class);
            intent.putExtra("UUID", issue.getBy());
            startActivity(intent);
        });

        handoverBtn.setOnClickListener(view -> {
            String id = Utility.getRandomString(20);

            Recent recent = new Recent(
                    issue.getBookId(), issue.getBy(), id, issue.getIssuePeriod(), issue.getIssueTime(), issue.getLib(),
                    Arrays.asList(iUsername, iEmail),
                    issue.getSubmitTime(), System.currentTimeMillis()
            );

            Thread forUser = new Thread(() -> Database.create("user/" + issue.getBy() + "/recent/" + id, recent, new Assure() {
                @Override
                public <T> void accept(T result) {
                    Toast.makeText(getContext(), "Handed.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void reject(String error) {
                    Toast.makeText(getContext(), "something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }));

            Thread forAdmin = new Thread(() -> Database.create("library/" + issue.getLib() + "/recent/" + id, recent, new Assure() {
                @Override
                public <T> void accept(T result) {
                    Toast.makeText(getContext(), "Handed.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void reject(String error) {
                    Toast.makeText(getContext(), "something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }));

            Map<String, Object> map = new HashMap<>();
            map.put("issueCount", (bookIssueCount + 1));
            Thread forBook = new Thread(() -> Database.update("library/" + issue.getLib() + "/book/" + issue.getBookId(), map, new Assure() {
                @Override
                public <T> void accept(T result) {
                    delete("Handed.");
                }

                @Override
                public void reject(String error) {
                    Toast.makeText(getContext(), "something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }));

            forUser.start();
            forAdmin.start();
            forBook.start();
        });

        rejectBtn.setOnClickListener(view -> {
            delete("Rejected Successfully.");
        });
    }

    private void load() {
        issueTime.setText("Issue Date : " + FutureDate.toDate(issue.getIssueTime()));
        submitTime.setText("Submit Date : " + FutureDate.toDate(issue.getSubmitTime()));

        Database.getInstance().document("user/" + issue.getBy()).get().addOnSuccessListener(snap -> {
            username.setText("Issuer Name : " + snap.get("name").toString());
            libraryId.setText("Library Id : " + snap.get("libraryId").toString());
        });

        Database.read("library/" + issue.getLib() + "/book/" + issue.getBookId(), new Book(), new Assure() {
            @Override
            public <T> void accept(T result) {
                Book book = (Book) result;
                bookName.setText("Book : " + book.getName());
                bookAuthor.setText("Book Author : " + book.getAuthor());
                bookClass.setText("Book Class : " + book.getbClass());
                bookIssueCount = book.getIssueCount();
             }

            @Override
            public void reject(String error) {
                Toast.makeText(getContext(), "something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void delete(String message) {
        String path = "library/" + issue.getLib() + "/notification/" + issue.getId();
        Database.delete(path, new Assure() {
            @Override
            public <T> void accept(T result) {
                dismiss();
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void reject(String error) {
                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}





















