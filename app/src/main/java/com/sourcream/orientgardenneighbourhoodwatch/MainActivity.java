package com.sourcream.orientgardenneighbourhoodwatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<Incident> incidentsList = new ArrayList<>();
    private IncidentAdapter adapter;
    private String itemCount;
    private int numberOfItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new IncidentAdapter(this, incidentsList);
        ListView incidentListView = findViewById(R.id.main_ListView);
        incidentListView.setAdapter(adapter);

        TextView emptyStateTextView = findViewById(R.id.emptyState);

        InternetConnectivityUtil internetConnectivityUtil = new InternetConnectivityUtil();


        if (internetConnectivityUtil.isInternetConnected(this)) {

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("ogwatchDB");

            ProgressBar progressBar = findViewById(R.id.loading_spinner);


            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot snapshot) {
                    incidentsList.clear();
                    numberOfItems = (int) snapshot.getChildrenCount();
                    itemCount = Integer.toString(numberOfItems + 1);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Incident incident = dataSnapshot.getValue(Incident.class);
                        incidentsList.add(incident);
                    }
                    Collections.reverse(incidentsList);
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NotNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    Log.e(MainActivity.class.getSimpleName(), "Failed to read value", error.toException());
                    emptyStateTextView.setText(R.string.readErrorMessage);
                }
            });
        } else {
            ProgressBar progressBar = findViewById(R.id.loading_spinner);
            progressBar.setVisibility(View.GONE);

            emptyStateTextView.setText(R.string.noConnection);
        }


        incidentListView.setOnItemClickListener((adapterView, view, position, l) -> {
            String databaseKey = getPositionOfItem(position);
            Intent intent = new Intent(MainActivity.this, ViewIncident.class);
            intent.putExtra("key", databaseKey);
            MainActivity.this.startActivity(intent);
        });

        FloatingActionButton fab = findViewById(R.id.add_fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CreatePost.class);
            intent.putExtra("count", itemCount);
            MainActivity.this.startActivity(intent);
        });

        MaterialToolbar toolbar = findViewById(R.id.mainActivityAppBar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.account_profile:
                        intent = new Intent(MainActivity.this, UserProfile.class);
                        MainActivity.this.startActivity(intent);
                        return true;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(MainActivity.this, LoginActivity.class);
                        MainActivity.this.startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }

    // Due to the listview being reversed, the position of the selected item is not the same as
    // the database key for each child.
    private String getPositionOfItem(int selectedItem) {
        return Integer.toString(numberOfItems - selectedItem);
    }

}