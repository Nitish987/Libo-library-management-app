package com.nk.libo.sheet;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nk.libo.R;
import com.nk.libo.ViewProfileActivity;
import com.nk.libo.auth.Auth;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.Book;
import com.nk.libo.db.model.Recent;
import com.nk.libo.utils.Assure;
import com.nk.libo.utils.FutureDate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AdminBookReturnSheet extends BottomSheetDialogFragment {
    private View view;
    private TextView bookName, bookAuthor, bookClass, issueTime, submitTime, fine;
    private Button returnBookBtn, issuerDetails;

    private Recent recent;
    private int bookIssueCount = 0;

    public static AdminBookReturnSheet newInstance(Recent recent) {
        AdminBookReturnSheet fragment = new AdminBookReturnSheet();
        Bundle args = new Bundle();
        args.putSerializable("RECENT", recent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recent = (Recent) getArguments().getSerializable("RECENT");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sheet_admin_book_return, container, false);

        bookName = view.findViewById(R.id.book_name);
        bookAuthor = view.findViewById(R.id.book_author);
        bookClass = view.findViewById(R.id.book_class);
        issueTime = view.findViewById(R.id.issue_time);
        submitTime = view.findViewById(R.id.submit_time);
        fine = view.findViewById(R.id.fine);
        returnBookBtn = view.findViewById(R.id.return_book);
        issuerDetails = view.findViewById(R.id.user_details);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart() {
        super.onStart();
        load();

        issuerDetails.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ViewProfileActivity.class);
            intent.putExtra("UUID", recent.getBy());
            startActivity(intent);
        });

        returnBookBtn.setOnClickListener(view -> {
            Thread forUser = new Thread(() -> {
                delete("user/" + recent.getBy() + "/recent/" + recent.getId(),"returned.");
            });

            Map<String, Object> map = new HashMap<>();
            map.put("issueCount", (bookIssueCount - 1));
            Thread forBook = new Thread(() -> Database.update("library/" + recent.getLib() + "/book/" + recent.getBookId(), map, new Assure() {
                @Override
                public <T> void accept(T result) {
                    delete("library/" + recent.getLib() + "/recent/" + recent.getId(),"returned.");
                }

                @Override
                public void reject(String error) {
                    Toast.makeText(getContext(), "something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }));

            forUser.start();
            forBook.start();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void load() {
        issueTime.setText("Issue Date : " + FutureDate.toDate(recent.getIssueTime()));
        submitTime.setText("Submit Date : " + FutureDate.toDate(recent.getSubmitTime()));

        String f;
        if (FutureDate.isDateGreater(recent.getSubmitTime())) {
            fine.setTextColor(getContext().getColor(R.color.red));
            LocalDate d1 = LocalDate.parse(FutureDate.toDate(System.currentTimeMillis()), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate d2 = LocalDate.parse(FutureDate.toDate(recent.getSubmitTime()), DateTimeFormatter.ISO_LOCAL_DATE);
            Duration diff = Duration.between(d1.atStartOfDay(), d2.atStartOfDay());
            f = "Fine : " + Math.abs(5 * diff.toDays());
        } else {
            f = "Fine : No Fine";
        }
        fine.setText(f);

        Database.read("library/" + recent.getLib() + "/book/" + recent.getBookId(), new Book(), new Assure() {
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
                Toast.makeText(view.getContext(), "something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void delete(String path, String message) {
        Database.delete(path, new Assure() {
            @Override
            public <T> void accept(T result) {
                dismiss();
                Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void reject(String error) {
                Toast.makeText(view.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}