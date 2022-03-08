package com.nk.libo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nk.libo.auth.Auth;
import com.nk.libo.db.Database;
import com.nk.libo.db.model.User;
import com.nk.libo.tabs.ProfileTab;
import com.nk.libo.tabs.RecentTab;
import com.nk.libo.tabs.SearchTab;
import com.nk.libo.tabs.adapter.PagerAdapter;
import com.nk.libo.utils.Assure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView navigationView;
    private ViewPager2 pager;

    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_bar);
        pager = findViewById(R.id.pager);

        fragments = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Auth.setAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                startActivity(new Intent(this, WelcomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        String path = "/user/" + Auth.getCurrentUserUid();
        Database.exists(path, new User(), new Assure() {
            @Override
            public <T> void accept(T result) {
                User user = (User) result;
                Toast.makeText(getApplicationContext(), "Welcome " + user.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void reject(String error) {
                startActivity(new Intent(getApplicationContext(), NewProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        fragments.clear();
        fragments.addAll(Arrays.asList(new RecentTab(), new SearchTab(), new ProfileTab()));
        pager.setAdapter(new PagerAdapter(this, fragments));

        navigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.recent) pager.setCurrentItem(0);
            else if (id == R.id.search) pager.setCurrentItem(1);
            else if (id == R.id.profile) pager.setCurrentItem(2);
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sign_out) Auth.logout();
        return super.onOptionsItemSelected(item);
    }
}