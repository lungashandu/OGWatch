package com.sourcream.orientgardenneighbourhoodwatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewIncident extends AppCompatActivity {
    private Incident incident;
    private String imageUrl;
    private ImageView incidentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_incident);

        Intent intent = getIntent();
        String value = intent.getStringExtra("key");
        String path = "ogwatchDB/" + value;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(path);

        ProgressBar progressBar = findViewById(R.id.viewIncidentProgressBar);

        TextView stolenItem = findViewById(R.id.vp_stolenItemTextView);
        TextView houseNumber = findViewById(R.id.vp_houseNumberTextView);
        TextView description = findViewById(R.id.vp_descriptionTextView);
        incidentImage = findViewById(R.id.vp_stolenItemImageView);


        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(() -> reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                incident = snapshot.getValue(Incident.class);
                assert incident != null;
                stolenItem.setText(incident.getStolenItem());
                houseNumber.setText(incident.getHouseNumber());
                description.setText(incident.getDescription());
                imageUrl = incident.getImageUrl();

                InternetConnectivityUtil internetConnectivityUtil = new InternetConnectivityUtil();
                if (internetConnectivityUtil.isInternetConnected(getApplicationContext())) {
                    Picasso.get().load(imageUrl).into(incidentImage);
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    incidentImage.setImageResource(R.drawable.ic_baseline_no_photography_24);
                    incidentImage.setClickable(false);
                }
            }
            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.e("View Incident", "Error reading data" + error);
                stolenItem.setText(getString(R.string.readErrorMessage));
                description.setText(getString(R.string.noDataContext));
                incidentImage.setImageResource(R.drawable.ic_baseline_no_photography_24);
            }
        }));

        incidentImage.setOnClickListener(view -> {
            showImageDialog();
        });
    }

    private void showImageDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ViewImageFragment viewImageFragment = ViewImageFragment.newInstance(imageUrl);
        viewImageFragment.show(fragmentManager, "fragment_view_image");
    }
}