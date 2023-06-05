package com.example.orientgardenneighbourhoodwatch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
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
    private RequestQueue requestQueue;
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

        TextView stolenItem = findViewById(R.id.vp_stolenItemTextView);
        TextView houseNumber = findViewById(R.id.vp_houseNumberTextView);
        TextView description = findViewById(R.id.vp_descriptionTextView);
        incidentImage = findViewById(R.id.vp_stolenItemImageView);

        requestQueue = Volley.newRequestQueue(this);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                incident = snapshot.getValue(Incident.class);
                stolenItem.setText(incident.getStolenItem());
                houseNumber.setText(incident.getHouseNumber());
                description.setText(incident.getDescription());
                imageUrl = incident.getImageUrl();

                Picasso.get().load(imageUrl).into(incidentImage);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                incidentImage.setImageResource(R.drawable.ic_baseline_no_photography_24);
            }
        }));

        incidentImage.setOnClickListener(view -> {
            showImageDialog();
        });
    }

    private void loadImageFromUrl(String imageUrl) {
        ImageRequest imageRequest = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                incidentImage.setImageBitmap(response);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                incidentImage.setImageResource(R.drawable.ic_baseline_no_photography_24);
            }
        });
        requestQueue.add(imageRequest);
    }

    private void showImageDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ViewImageFragment viewImageFragment = ViewImageFragment.newInstance(imageUrl);
        viewImageFragment.show(fragmentManager, "fragment_view_image");
    }
}