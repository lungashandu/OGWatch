package com.example.orientgardenneighbourhoodwatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreatePost extends AppCompatActivity {
    private Intent intent;
    private String value;
    private String incidentID;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        intent = getIntent();
        value = intent.getStringExtra("count");
        incidentID = "incident_" + value;

        reference = FirebaseDatabase.getInstance().getReference();

        EditText houseNumberEditText = findViewById(R.id.houseNumber_editText);
        EditText stolenItemEditText = findViewById(R.id.stolenItem_editText);
        EditText descriptionEditText = findViewById(R.id.description_editText);
        Button createPostButton = findViewById(R.id.createPost_button);


        createPostButton.setOnClickListener(view -> {
            String houseNumber = houseNumberEditText.getText().toString();
            String stolenItem = stolenItemEditText.getText().toString();
            String description = descriptionEditText.getText().toString();

            if (houseNumber.isEmpty() & stolenItem.isEmpty() & description.isEmpty()) {
                Toast.makeText(CreatePost.this, "Please fill all available text fields", Toast.LENGTH_SHORT).show();

            } else {
                Incident incident = new Incident(description, houseNumber, stolenItem);
                reference.child("ogwatchDB").child(incidentID).setValue(incident).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        houseNumberEditText.setText("");
                        stolenItemEditText.setText("");
                        descriptionEditText.setText("");
                        Toast.makeText(CreatePost.this, "Incident Added Successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(CreatePost.this, MainActivity.class);
                        CreatePost.this.startActivity(intent);
                    }
                });
            }

        });
    }
}