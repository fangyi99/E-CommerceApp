package com.example.e_commerceapp2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    TextView name;
    ImageView profilePic;
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mAuth.getCurrentUser()!=null){
            Log.d("TAG","User is signed in.");

            return inflater.inflate(R.layout.fragment_logged_in_profile, container, false);

        }else{
            Log.d("TAG","User is not signed in.");
            return inflater.inflate(R.layout.fragment_account, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(mAuth.getCurrentUser()!=null) {
            name = (TextView) view.findViewById(R.id.username3);
            profilePic = (ImageView) view.findViewById(R.id.profilePic1);
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            DocumentReference docRef = database.document("Users/" + UserManager.getUserId());

            docRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d("TAG", "User credentials retrieved.");
                            Picasso.get().load(documentSnapshot.getString("photo")).into(profilePic);
                            name.setText(documentSnapshot.getString("username"));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "Error retrieving user credentials.");
                            Toast.makeText(getContext(), "Error retrieving user credentials!", Toast.LENGTH_SHORT).show();
                        }
                    });

            super.onViewCreated(view, savedInstanceState);
        }
    }



}