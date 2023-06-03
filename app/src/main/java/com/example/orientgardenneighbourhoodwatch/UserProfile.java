package com.example.orientgardenneighbourhoodwatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UserProfile extends AppCompatActivity {
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
    private String userName;
    private String userEmail;
    private String userHouseNumber;
    private final String usercode = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userName = user.getDisplayName();
            userEmail = user.getEmail();
        }

        EditText nameEditText = findViewById(R.id.userNameEditText);
        EditText emailEditText = findViewById(R.id.userEmailEditText);
        EditText houseNumberEditText = findViewById(R.id.ProfileHouseNumberEditText);

        nameEditText.setText(userName);
        emailEditText.setText(userEmail);
        houseNumberEditText.requestFocus();

        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);

        Button submitButton = findViewById(R.id.profile_submitButton);
        submitButton.setOnClickListener(view -> {
            // In case there were changes made on the display name and email
            if (!Objects.equals(userName, nameEditText.getText().toString())) {
                userName = nameEditText.getText().toString();
            }
            if (!Objects.equals(userEmail, emailEditText.getText().toString())) {
                userEmail = emailEditText.getText().toString();
            }
            userHouseNumber = houseNumberEditText.getText().toString();

            if (userName.isEmpty() || userEmail.isEmpty() || userHouseNumber.isEmpty()) {
                Toast.makeText(UserProfile.this, "Please fill all available text fields", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("House Number", userHouseNumber);
                editor.apply();

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        User user = new User(userName, userEmail, userHouseNumber);
                        databaseReference.child(usercode).setValue(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            Toast.makeText(UserProfile.this, "Profile Saved", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserProfile.this, MainActivity.class);
            UserProfile.this.startActivity(intent);
        });
    }


}