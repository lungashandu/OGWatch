package com.example.orientgardenneighbourhoodwatch;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreatePost extends AppCompatActivity {
    private String incidentID;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int PICK_IMAGE = 1;
    private Uri selectedImageURI;
    private Button createPostButton;
    private TextView addImageTextView;
    private LinearLayout selectedImageLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        selectedImageLL = findViewById(R.id.selected_image_LinearLayout);
        selectedImageLL.setVisibility(View.GONE);

        Intent intentIncident = getIntent();
        incidentID = intentIncident.getStringExtra("count");

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        EditText houseNumberEditText = findViewById(R.id.houseNumber_editText);
        EditText stolenItemEditText = findViewById(R.id.stolenItem_editText);
        EditText descriptionEditText = findViewById(R.id.description_editText);
        createPostButton = findViewById(R.id.createPost_button);

        addImageTextView = findViewById(R.id.add_imageTextView);
        addImageTextView.setOnClickListener(view -> {
            Intent imageIntent = new Intent();
            imageIntent.setAction(Intent.ACTION_GET_CONTENT);
            imageIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(imageIntent, "Select Picture"), PICK_IMAGE);
        });

        createPostButton.setOnClickListener(view -> {
            String houseNumber = houseNumberEditText.getText().toString();
            String stolenItem = stolenItemEditText.getText().toString();
            String description = descriptionEditText.getText().toString();

            if (houseNumber.isEmpty() & stolenItem.isEmpty() & description.isEmpty()) {
                Toast.makeText(CreatePost.this, "Please fill all available text fields", Toast.LENGTH_SHORT).show();

            } else {

                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {

                        if (selectedImageURI != null) {
                            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(selectedImageURI));
                            fileReference.putFile(selectedImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Incident incident = new Incident(description, houseNumber, stolenItem, uri.toString());
                                            databaseReference.child("ogwatchDB").child(incidentID).setValue(incident);
                                        }
                                    });
                                }
                            });
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
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

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageURI = data.getData();
            addImageTextView.setVisibility(View.GONE);
            selectedImageLL.setVisibility(View.VISIBLE);
            createPostButton.setEnabled(true);
        }
    }

    private String getFileExtension(Uri mUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(mUri));
    }

}