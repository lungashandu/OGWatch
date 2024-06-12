package com.sourcream.orientgardenneighbourhoodwatch;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
    private Uri selectedImageURI;
    private Button createPostButton;
    private TextView addImageTextView;
    private ProgressBar createPostProgressBar;
    private SharedPreferences sharedPreferences;
    private LinearLayout selectedImageLL;
    private String houseNumber, stolenItem, description;
    private Bitmap rotatedImage;
    private final String usercode = FirebaseAuth.getInstance().getUid();
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
        // callback is invoked after the user selects a media item or closes the photo picker.
        if (uri != null) {
            selectedImageURI = uri;
            Log.d("Photo picker", "Selected URI: " + uri);
            Context context = getApplicationContext();
            final int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            getContentResolver().takePersistableUriPermission(uri, flag);
            String filePath = getRealPathFromURI(context, uri);

            ContentResolver resolver = context.getContentResolver();
            try (InputStream stream = resolver.openInputStream(uri)) {
                if (stream != null) {

                    Bitmap downsampledBitmap = DownsamplingImage.decodeSampleBitmapFromInputStream(filePath);

                    addImageTextView.setVisibility(View.GONE);
                    createPostProgressBar.setVisibility(View.VISIBLE);
                    rotatedImage = rotateImage(downsampledBitmap, filePath);
                    handleResponse();
                }

            } catch (IOException e) {
                Toast.makeText(this, "Stream = Null", Toast.LENGTH_SHORT).show();
            }


        } else {
            Log.d("Photo picker", "No media selected");
        }
    });
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            launchPhotoPicker();
        } else {
            Toast.makeText(this, "Permission rejected", Toast.LENGTH_SHORT).show();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        selectedImageLL = findViewById(R.id.selected_image_LinearLayout);
        selectedImageLL.setVisibility(View.GONE);
        createPostProgressBar = findViewById(R.id.createPost_progressbar);
        createPostProgressBar.setVisibility(View.GONE);

        Intent intentIncident = getIntent();
        incidentID = intentIncident.getStringExtra("count");

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        EditText houseNumberEditText = findViewById(R.id.houseNumber_editText);
        EditText stolenItemEditText = findViewById(R.id.stolenItem_editText);
        EditText descriptionEditText = findViewById(R.id.description_editText);
        createPostButton = findViewById(R.id.createPost_button);

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        houseNumber = sharedPreferences.getString(getString(R.string.house_number), getString(R.string.no_house_number));
        houseNumberEditText.setText(houseNumber);

        addImageTextView = findViewById(R.id.add_imageTextView);
        addImageTextView.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                launchPhotoPicker();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_MEDIA_LOCATION);
            }
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

    private void launchPhotoPicker() {
        // Launch the photo picker and let the user choose images.
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void handleResponse() throws IOException {
        createPostProgressBar.setVisibility(View.GONE);
        selectedImageLL.setVisibility(View.VISIBLE);
        createPostButton.setEnabled(true);
    }

    private String getFileExtension(Uri mUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(mUri));
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private Bitmap rotateImage(Bitmap bitmap, String filePath) throws IOException {
        // Bitmap bitmap = BitmapFactory.decodeStream(stream);
        int orientation = getOrientation(filePath);

        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private int getOrientation(String filePath) throws IOException {
        ExifInterface exifInterface = new ExifInterface(filePath);
        int rotationDegrees = 0;
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Log.d("Orientation", "Image Orientation: " + orientation);
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
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