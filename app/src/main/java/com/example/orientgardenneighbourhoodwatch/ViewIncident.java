package com.example.orientgardenneighbourhoodwatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewIncident extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_incident);

        Intent intent = getIntent();
        String value = intent.getStringExtra("key");
        String path = "ogwatchDB/" + value;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference(path);

        TextView stolenItem = findViewById(R.id.vp_stolenItemTextView);
        TextView houseNumber = findViewById(R.id.vp_houseNumberTextView);
        TextView description = findViewById(R.id.vp_descriptionTextView);
        ImageView incidentImage = findViewById(R.id.vp_stolenItemImageView);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Incident incident = snapshot.getValue(Incident.class);
                        stolenItem.setText(incident.getStolenItem());
                        houseNumber.setText(incident.getHouseNumber());
                        description.setText(incident.getDescription());

                        Picasso.get().load(incident.getImageUrl()).into(incidentImage);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
            }
        });


    }
}