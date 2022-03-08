package com.nk.libo.sheet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nk.libo.HomeActivity;
import com.nk.libo.R;
import com.nk.libo.auth.Auth;
import com.nk.libo.utils.Promise;

public class LoginSheet extends BottomSheetDialogFragment {

    private EditText emailText, passwordText;
    private Button loginBtn;

    public static LoginSheet getInstance() {
        return new LoginSheet();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sheet_login, container, false);

        emailText = view.findViewById(R.id.email_login_text);
        passwordText = view.findViewById(R.id.password_login_text);
        loginBtn = view.findViewById(R.id.login_btn);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loginBtn.setOnClickListener(view -> {
            if (TextUtils.isEmpty(emailText.getText()) || TextUtils.isEmpty(passwordText.getText())) {
                Toast.makeText(getContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
            } else {
                String email, password;
                email = emailText.getText().toString();
                password = passwordText.getText().toString();

                Auth auth = new Auth(this.getActivity());
                auth.login(email, password, new Promise() {
                    @Override
                    public void inProcess() {
                        uiEnable(false);
                    }

                    @Override
                    public void resolve() {
                        startActivity(new Intent(getContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }

                    @Override
                    public void reject() {
                        uiEnable(true);
                        Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void uiEnable(boolean enables) {
        emailText.setEnabled(enables);
        passwordText.setEnabled(enables);
        loginBtn.setEnabled(enables);
    }
}













