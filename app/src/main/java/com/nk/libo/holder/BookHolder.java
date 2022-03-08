package com.nk.libo.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nk.libo.R;

public class BookHolder extends RecyclerView.ViewHolder {
    public View view;
    private ImageView photo;
    private TextView bookName, bookAuthor, bookCategory, bookClass;

    public BookHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
        photo = itemView.findViewById(R.id.photo);
        bookName = itemView.findViewById(R.id.name);
        bookAuthor = itemView.findViewById(R.id.author);
        bookCategory = itemView.findViewById(R.id.category);
        bookClass = itemView.findViewById(R.id.cs);
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

    public void setBookCategory(String bookCategory) {
        this.bookCategory.setText(bookCategory);
    }

    public void setBookCS(String bookCS) {
        this.bookClass.setText(bookCS);
    }
}
