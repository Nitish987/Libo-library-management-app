package com.nk.libo.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nk.libo.LibraryAdminActivity;
import com.nk.libo.R;
import com.nk.libo.db.model.Library;

import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryHolder> {
    private List<Library> libraries;

    public LibraryAdapter(List<Library> libraries) {
        this.libraries = libraries;
    }

    @NonNull
    @Override
    public LibraryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LibraryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryHolder holder, int position) {
        Library library = libraries.get(position);
        holder.setName(library.getName());
        holder.setCollege(library.getCollege());
        holder.setImage(library.getPhotoUrl());
        holder.view.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), LibraryAdminActivity.class);
            intent.putExtra("libraryID", library.getId());
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return libraries.size();
    }

    public static class LibraryHolder extends RecyclerView.ViewHolder {
        public View view;
        private ImageView imageView;
        private TextView nameText, collegeText;

        public LibraryHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.imageView = itemView.findViewById(R.id.image_pic);
            this.nameText = itemView.findViewById(R.id.name);
            this.collegeText = itemView.findViewById(R.id.college);
        }

        public void setImage(String url) {
            if (!url.equals("")) Glide.with(view.getContext()).load(url).into(this.imageView);
        }

        public void setName(String name) {
            this.nameText.setText(name);
        }

        public void setCollege(String college) {
            this.collegeText.setText(college);
        }
    }
}














