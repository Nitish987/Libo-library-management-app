package com.nk.libo.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nk.libo.R;

public class LibraryNotificationHolder extends RecyclerView.ViewHolder {
    public View view;
    private TextView bookName,username, issueTime, submitTime;
    public LibraryNotificationHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
        bookName = itemView.findViewById(R.id.book_name);
        username = itemView.findViewById(R.id.user_name);
        issueTime = itemView.findViewById(R.id.issue_time);
        submitTime = itemView.findViewById(R.id.submit_time);
    }

    public void setBookName(String bookName) {
        String st = "Book : " + bookName;
        this.bookName.setText(st);
    }

    public void setUsername(String username) {
        String st = "Name : " + username;
        this.username.setText(st);
    }

    public void setIssueTime(String issueTime) {
        String st = "Issue Time : " + issueTime;
        this.issueTime.setText(st);
    }

    public void setSubmitTime(String submitTime) {
        String st = "Submit Time : " + submitTime;
        this.submitTime.setText(st);
    }
}
