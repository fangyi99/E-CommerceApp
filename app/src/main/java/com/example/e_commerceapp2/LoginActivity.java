package com.example.e_commerceapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.firebase.ui.auth.AuthUI;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static final int RC_SIGN_IN = 1;
    EditText email, password;
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.YahooBuilder().build()
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        email = (EditText) findViewById(R.id.username);
//        password = (EditText) findViewById(R.id.password);

        //create and launch sign in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logo)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //create new document in Users database using current User UID
                DocumentReference documentReference = database.collection("Users").document(UserManager.getUserId());
                Map<String, Object> docUser = new HashMap<>();
                docUser.put("username", user.getDisplayName());
                docUser.put("image", null);
                docUser.put("email", user.getEmail());
                Log.d("TAG","User Email: "+ user.getEmail());
                docUser.put("phone", null);
                documentReference.set(docUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "Registration Successful");
                        Toast.makeText(getApplicationContext(), "Registration Successful.",
                                Toast.LENGTH_SHORT).show();
                        Intent loggedInUserView = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(loggedInUserView);
                    }
                });
                //Sign in failed
            } else {
                Toast.makeText(getApplicationContext(), "Sign in failed", Toast.LENGTH_LONG).show();
                Log.d("TAG","Sign in failed");
            }

        }
    }

    public void clickBack(View v){
        //prevent user from going back to logged in profile view after logging out
        if(user==null){
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
        }else{
            onBackPressed();
        }
    }

    public void clickForgot(View v) {
        // Directs the user to the ForgotPassword activity
        Intent forgotActivity = new Intent(this, ForgotPasswordActivity.class);
        startActivity(forgotActivity);
    }

}