package com.example.orientgardenneighbourhoodwatch;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private final String usercode = FirebaseAuth.getInstance().getUid();
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
        // callback is invoked after the user selects a media item or closes the photo picker.
        if (uri != null) {
            Log.d("Photo picker", "Selected URI: " + uri);
        } else {
            Log.d("Photo picker", "No media selected");
        }
    });
    private String houseNumber, stolenItem, description;
    private Bitmap rotatedImage;

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
            // Launch the photo picker and let the user choose images.
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());

//            Intent imageIntent = new Intent();
//            imageIntent.setAction(Intent.ACTION_GET_CONTENT);
//            imageIntent.setType("image/*");
//            startActivityForResult(Intent.createChooser(imageIntent, "Select Picture"), PICK_IMAGE);
        });

        createPostButton.setOnClickListener(view -> {
            houseNumber = houseNumberEditText.getText().toString();
            stolenItem = stolenItemEditText.getText().toString();
            description = descriptionEditText.getText().toString();

            if (houseNumber.isEmpty() || stolenItem.isEmpty() || description.isEmpty()) {
                Toast.makeText(CreatePost.this, "Please fill all available text fields", Toast.LENGTH_SHORT).show();

            } else {

                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {

                        if (selectedImageURI != null) {
                            uploadImage(rotatedImage);
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

            try {
                rotatedImage = rotateImage(selectedImageURI);
            } catch (IOException e) {
                Toast.makeText(CreatePost.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }

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

    public Bitmap rotateImage(Uri imageUri) throws IOException {
        Bitmap bitmap;
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        bitmap = BitmapFactory.decodeStream(inputStream);
        int orientation = getOrientation(imageUri);

        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private int getOrientation(Uri imageUri) throws IOException {
        ExifInterface exifInterface = new ExifInterface(imageUri.getPath());
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        int rotationDegrees = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotationDegrees = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotationDegrees = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotationDegrees = 270;
                break;
        }
        return rotationDegrees;
    }

    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 1000, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        storageReference = FirebaseStorage.getInstance().getReference().child(System.currentTimeMillis() + "." + getFileExtension(selectedImageURI));
        UploadTask uploadTask = storageReference.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Incident incident = new Incident(description, houseNumber, stolenItem, uri.toString(), usercode);
                        databaseReference.child("ogwatchDB").child(incidentID).setValue(incident);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreatePost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreatePost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}