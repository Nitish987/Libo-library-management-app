package com.nk.libo.sheet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nk.libo.AddMemberActivity;
import com.nk.libo.R;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.Library;
import com.nk.libo.utils.Assure;

import java.util.HashMap;
import java.util.Map;

public class MemberSheet extends BottomSheetDialogFragment {
    private Button remove;

    private String uid, libId;

    public static MemberSheet newInstance(String memberUid, String libId) {
        MemberSheet fragment = new MemberSheet();
        Bundle args = new Bundle();
        args.putString("uid", memberUid);
        args.putString("lib", libId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString("uid");
            libId = getArguments().getString("lib");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sheet_member, container, false);

        remove = view.findViewById(R.id.remove);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        remove.setOnClickListener(view -> {
            Database.read("library/" + libId, new Library(), new Assure() {
                @Override
                public <T> void accept(T result) {
                    Library library = (Library) result;
                    Database.delete("library/" + libId + "/member/" + uid, new Assure() {
                        @Override
                        public <T> void accept(T result) {
                            Map<String, Object> map = new HashMap<>();
                            library.getAdmin().remove(uid);
                            map.put("admin", library.getAdmin());
                            Database.update("library/" + libId, map, new Assure() {
                                @Override
                                public <T> void accept(T result) {
                                    Toast.makeText(getContext(), "Member removed successfully.", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }

                                @Override
                                public void reject(String error) {
                                    Toast.makeText(getContext(), "something went wrong.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void reject(String error) {
                            Toast.makeText(getContext(), "something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void reject(String error) {
                    Toast.makeText(getContext(), "something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
















