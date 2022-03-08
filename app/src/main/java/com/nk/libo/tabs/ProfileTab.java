package com.nk.libo.tabs;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nk.libo.ChangeProfileActivity;
import com.nk.libo.R;
import com.nk.libo.auth.Auth;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.User;
import com.nk.libo.utils.Assure;

public class ProfileTab extends Fragment {

    private View view;
    private ImageView imageView;
    private TextView nameText, usernameText, departmentText, libraryIdText, emailText, phoneText, addressText;
    private ImageButton editProfileBtn;

    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_profile, container, false);

        imageView = view.findViewById(R.id.image_pic);
        nameText = view.findViewById(R.id.name);
        usernameText = view.findViewById(R.id.username);
        departmentText = view.findViewById(R.id.department);
        libraryIdText = view.findViewById(R.id.library_id);
        emailText = view.findViewById(R.id.email);
        phoneText = view.findViewById(R.id.phone);
        addressText = view.findViewById(R.id.address);
        editProfileBtn = view.findViewById(R.id.edit_profile);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        String path = "/user/" + Auth.getCurrentUserUid();
        Database.read(path, new User(), new Assure() {
            @Override
            public <T> void accept(T result) {
                user = (User) result;
                nameText.setText(user.getName());
                usernameText.setText(user.getUsername());
                departmentText.setText(user.getDepartment());
                libraryIdText.setText(user.getLibraryId());
                emailText.setText(user.getEmail());
                phoneText.setText(user.getPhone());
                addressText.setText(user.getAddress());

                if (!user.getPhotoUrl().equals("")) {
                    Glide.with(view.getContext()).load(user.getPhotoUrl()).into(imageView);
                }
            }

            @Override
            public void reject(String error) {
                Log.e("Error", "reject: " + error);
            }
        });

        editProfileBtn.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), ChangeProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("user", user));
            getActivity().finish();
        });
    }
}