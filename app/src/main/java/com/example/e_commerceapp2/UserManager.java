package com.example.e_commerceapp2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserManager {

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static String getUserId() {

        String userId = mAuth.getCurrentUser().getUid();
        return userId;

    }

    public static String getUserCartId() {

        String userCartId = "C" + mAuth.getCurrentUser().getUid();
        return userCartId;
    }
}
