package com.nk.libo.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nk.libo.R;

public class MemberHolder extends RecyclerView.ViewHolder {
    private ImageView photo;
    private TextView name;

    public MemberHolder(@NonNull View itemView) {
        super(itemView);
        photo = itemView.findViewById(R.id.photo);
        name = itemView.findViewById(R.id.name);
    }

    public void setPhoto(String photo) {
        if (!photo.equals("")) {
            Glide.with(itemView.getRootView()).load(photo).into(this.photo);
        }
    }

    public void setName(String name) {
        this.name.setText(name);
    }
}
