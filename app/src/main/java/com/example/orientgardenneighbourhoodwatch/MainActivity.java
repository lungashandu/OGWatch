package com.example.orientgardenneighbourhoodwatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<Incident> incidentsList = new ArrayList<>();
    private IncidentAdapter adapter;
    private String itemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new IncidentAdapter(this, incidentsList);
        ListView incidentListView = findViewById(R.id.main_ListView);
        incidentListView.setAdapter(adapter);


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("ogwatchDB");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                incidentsList.clear();
                itemCount = Integer.toString((int) (snapshot.getChildrenCount() + 1));
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Incident incident = dataSnapshot.getValue(Incident.class);
                    incidentsList.add(incident);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.e(MainActivity.class.getSimpleName(), "Failed to read value", error.toException());
            }
        });

        incidentListView.setOnItemClickListener((adapterView, view, position, l) -> {
            String databaseKey = Integer.toString(position + 1);
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
    }
}