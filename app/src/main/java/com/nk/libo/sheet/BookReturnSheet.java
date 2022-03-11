package com.nk.libo.sheet;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nk.libo.R;
import com.nk.libo.ViewProfileActivity;
import com.nk.libo.auth.Auth;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.Book;
import com.nk.libo.db.model.Library;
import com.nk.libo.db.model.Recent;
import com.nk.libo.utils.Assure;
import com.nk.libo.utils.FutureDate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class BookReturnSheet extends BottomSheetDialogFragment {
    private View view;
    private TextView bookName, bookAuthor, bookClass, issueTime, submitTime, fine;
    private Button readBookBtn;

    private Recent recent;
    private String bookPdfUrl = null;
    private int book_fine = 0;

    public static BookReturnSheet newInstance(Recent recent) {
        BookReturnSheet fragment = new BookReturnSheet();
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
        view = inflater.inflate(R.layout.sheet_book_return, container, false);

        bookName = view.findViewById(R.id.book_name);
        bookAuthor = view.findViewById(R.id.book_author);
        bookClass = view.findViewById(R.id.book_class);
        issueTime = view.findViewById(R.id.issue_time);
        submitTime = view.findViewById(R.id.submit_time);
        readBookBtn = view.findViewById(R.id.read_pdf);
        fine = view.findViewById(R.id.fine);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart() {
        super.onStart();
        Database.read("library/" + recent.getLib(), new Library(), new Assure() {
            @Override
            public <T> void accept(T result) {
                Library library = (Library) result;
                book_fine = library.getLateFine();

                load();
            }

            @Override
            public void reject(String error) {
                Toast.makeText(getContext(), "something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

        readBookBtn.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bookPdfUrl));
            startActivity(browserIntent);
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
            f = "Fine : " + Math.abs(book_fine * diff.toDays());
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
                bookPdfUrl = book.getPdfUrl();

                if (bookPdfUrl.equals("")) readBookBtn.setVisibility(View.GONE);
            }

            @Override
            public void reject(String error) {
                Toast.makeText(view.getContext(), "something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

















