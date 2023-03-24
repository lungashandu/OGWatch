package com.example.orientgardenneighbourhoodwatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewIncident extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_incident);

        Intent intent = getIntent();
        String value = intent.getStringExtra("key");
        String incidentID = "incident_" + value;
        String path = "ogwatchDB/" + incidentID;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference(path);

        TextView stolenItem = findViewById(R.id.vp_stolenItemTextView);
        TextView houseNumber = findViewById(R.id.vp_houseNumberTextView);
        TextView description = findViewById(R.id.vp_descriptionTextView);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Incident incident = snapshot.getValue(Incident.class);
                stolenItem.setText(incident.getStolenItem());
                houseNumber.setText(incident.getHouseNumber());
                description.setText(incident.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ViewIncident.this, "The read failed", Toast.LENGTH_SHORT).show();
            }
        });


    }
}