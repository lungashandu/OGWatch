package com.sourcream.orientgardenneighbourhoodwatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private final boolean showOneTapUI = true;
    private FirebaseAuth firebaseAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        openMainActivity(user, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .build())
                .build();

        firebaseAuth = FirebaseAuth.getInstance();

        ImageButton signInButton = findViewById(R.id.signin_imagebutton);

        ActivityResultLauncher<IntentSenderRequest> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            try {
                                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                                String idToken = credential.getGoogleIdToken();
                                if (idToken != null) {
                                    // Got an ID token from Google. Use it to authenticate with firebase
                                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                                    firebaseAuth.signInWithCredential(firebaseCredential).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in User's information
                                                Log.d("LOG_ACTIVITY", "signWithCredential:success");
                                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                                openMainActivity(user, 0);
                                            }
                                        }
                                    });
                                }
                            } catch (ApiException e) {

                            }
                        }
                    }
                });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InternetConnectivityUtil internetConnectivityUtil = new InternetConnectivityUtil();
                if (internetConnectivityUtil.isInternetConnected(getApplicationContext())) {
                    oneTapClient.beginSignIn(signInRequest)
                            .addOnSuccessListener(LoginActivity.this, new OnSuccessListener<BeginSignInResult>() {
                                @Override
                                public void onSuccess(BeginSignInResult result) {
                                    //                                            startIntentSenderForResult(
//                                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
//                                                    null, 0, 0, 0);
                                    IntentSenderRequest intentSenderRequest = new IntentSenderRequest
                                            .Builder(result.getPendingIntent().getIntentSender()).build();
                                    activityResultLauncher.launch(intentSenderRequest);
                                }
                            })
                            .addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    // No saved credentials found. Launch the One Tap sign-up flow, or
                                    // do nothing and continue presenting the signed-out UI.
                                    Log.d("TAG", e.getLocalizedMessage());
                                }
                            });
                } else {
                    TextView signInText = findViewById(R.id.signInTextView);
                    signInText.setText(R.string.noConnection);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    // decides which activity is called depending the where the function is called
    // If the user already exists, switch key = 1
    // else the switch key = 0
    private void openMainActivity(FirebaseUser currentUser, int switchKey) {
        Intent intent;
        if (currentUser != null && switchKey == 1) {
            intent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(intent);
        } else if (currentUser != null && switchKey == 0) {
            intent = new Intent(LoginActivity.this, UserProfile.class);
            LoginActivity.this.startActivity(intent);
        }
    }

}