package com.example.orientgardenneighbourhoodwatch;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class CreatePost extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        EditText houseNumberEditText = findViewById(R.id.houseNumber_editText);
        EditText stolenItemEditText = findViewById(R.id.stolenItem_editText);
        EditText descriptionEditText = findViewById(R.id.description_editText);
        Button createPostEditText = findViewById(R.id.createPost_button);

        createPostEditText.setOnClickListener(view -> {
            String houseNumber = houseNumberEditText.getText().toString();
            String stolenItem = stolenItemEditText.getText().toString();
            String description = descriptionEditText.getText().toString();


        });
    }
}