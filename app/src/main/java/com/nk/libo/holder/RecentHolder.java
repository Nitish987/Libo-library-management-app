package com.nk.libo.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nk.libo.R;
import com.nk.libo.utils.FutureDate;

public class RecentHolder extends RecyclerView.ViewHolder {
    public View view;
    private ImageView photo;
    private TextView bookName, bookAuthor, submitTime;

    public RecentHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
        photo = itemView.findViewById(R.id.photo);
        bookName = itemView.findViewById(R.id.book_name);
        bookAuthor = itemView.findViewById(R.id.book_author);
        submitTime = itemView.findViewById(R.id.submit_time);
    }

    public void setPhoto(String photo) {
        Glide.with(itemView.getContext()).load(photo).into(this.photo);
    }

    public void setBookName(String bookName) {
        this.bookName.setText(bookName);
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor.setText(bookAuthor);
    }

    public void setSubmitTime(long submitTime) {
        this.submitTime.setText("Due : " + FutureDate.toDate(submitTime));
    }
}
