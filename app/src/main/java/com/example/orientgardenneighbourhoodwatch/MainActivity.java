package com.example.orientgardenneighbourhoodwatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<Incident> incidentsList = new ArrayList<>();
    private IncidentAdapter adapter;
    private ListView incidentListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new IncidentAdapter(this, incidentsList);
        incidentListView = findViewById(R.id.main_ListView);
        incidentListView.setAdapter(adapter);


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("ogwatchDB");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                incidentsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Incident incident = dataSnapshot.getValue(Incident.class);
                    incidentsList.add(incident);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(MainActivity.class.getSimpleName(), "Failed to read value", error.toException());
            }
        });


        FloatingActionButton fab = findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String listViewCount = Integer.toString(incidentListView.getCount() + 1);
                Intent intent = new Intent(MainActivity.this, CreatePost.class);
                intent.putExtra("count", listViewCount);
                MainActivity.this.startActivity(intent);
            }
        });
    }
}