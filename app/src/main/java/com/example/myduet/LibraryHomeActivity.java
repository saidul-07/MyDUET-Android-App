package com.example.myduet;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myduet.adapters.LibraryMenuAdapter;
import com.example.myduet.models.LibraryMenuItem;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;

public class LibraryHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_home);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvLibraryMenu);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<LibraryMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new LibraryMenuItem("1", "Search Books", "Find books in the central library", R.drawable.ic_search));
        menuItems.add(new LibraryMenuItem("2", "My Borrowed Books", "View your current and past history", R.drawable.ic_book));
        menuItems.add(new LibraryMenuItem("3", "New Arrivals", "Check latest additions to the library", R.drawable.ic_announcement));
        menuItems.add(new LibraryMenuItem("4", "Popular Books", "Most borrowed books this month", R.drawable.ic_book));
        menuItems.add(new LibraryMenuItem("5", "Due Books", "Check your books nearing due date", R.drawable.ic_notification));
        menuItems.add(new LibraryMenuItem("6", "Library Information", "About the DUET Central Library", R.drawable.ic_info));
        menuItems.add(new LibraryMenuItem("7", "Opening Hours", "Daily schedule and holiday notices", R.drawable.ic_refresh));
        menuItems.add(new LibraryMenuItem("8", "Contact Librarian", "Get assistance from the staff", R.drawable.ic_call));
        menuItems.add(new LibraryMenuItem("9", "Library Location", "Find us on the DUET campus", R.drawable.ic_location));
        menuItems.add(new LibraryMenuItem("10", "DUET Digital Library", "Access online journals and e-books", R.drawable.ic_home));

        LibraryMenuAdapter adapter = new LibraryMenuAdapter(menuItems, item -> {
            Intent intent = new Intent(this, LibraryDetailActivity.class);
            intent.putExtra("title", item.getTitle());
            startActivity(intent);
        });
        rv.setAdapter(adapter);
    }
}