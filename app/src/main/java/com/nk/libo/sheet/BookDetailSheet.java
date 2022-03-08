package com.nk.libo.sheet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nk.libo.R;
import com.nk.libo.auth.Auth;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.Book;
import com.nk.libo.db.model.Issue;
import com.nk.libo.utils.Assure;
import com.nk.libo.utils.FutureDate;
import com.nk.libo.utils.Utility;

import java.util.HashMap;
import java.util.Map;

public class BookDetailSheet extends BottomSheetDialogFragment {

    private TextView bookName, bookAuthor, bookQuantity;
    private Spinner issueTime;
    private Button apply;

    private Book book;

    public static BookDetailSheet getInstance(Book book) {
        BookDetailSheet sheet = new BookDetailSheet();
        Bundle bundle = new Bundle();
        bundle.putSerializable("book", book);
        sheet.setArguments(bundle);
        return sheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = (Book) getArguments().getSerializable("book");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sheet_book_detail, container, false);

        bookName = view.findViewById(R.id.book_name);
        bookAuthor = view.findViewById(R.id.book_author);
        bookQuantity = view.findViewById(R.id.quantity);
        issueTime = view.findViewById(R.id.issue_time);
        apply = view.findViewById(R.id.apply);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        load();

        apply.setOnClickListener(view -> {
            String id = Utility.getRandomString(20);

            Issue issue = new Issue(
                    book.getId(), Auth.getCurrentUserUid(), id, issueTime.getSelectedItem().toString().toLowerCase(),
                    System.currentTimeMillis(), book.getLib(),
                    FutureDate.getFutureMillis(System.currentTimeMillis(), issueTime.getSelectedItem().toString().toLowerCase())
            );

            String path = "library/" + book.getLib() + "/notification/" + id;
            Database.create(path, issue, new Assure() {
                @Override
                public <T> void accept(T result) {
                    Toast.makeText(getContext(), "Book " + issueTime.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                    dismiss();
                }

                @Override
                public void reject(String error) {
                    Toast.makeText(getContext(), "Unable to issue Book. Try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void load() {
        bookName.setText("Book : " + book.getName());
        bookAuthor.setText("Author : " + book.getAuthor());
        bookQuantity.setText("Quantity : " + (book.getQuantity() - book.getIssueCount()) + "/" + book.getQuantity());

        if (book.getIssueCount() == book.getQuantity()) {
            apply.setEnabled(false);
            issueTime.setEnabled(false);
            bookQuantity.setText("This book is not Available right now.");
            bookQuantity.setTextColor(getContext().getColor(R.color.red));
        }
    }
}
