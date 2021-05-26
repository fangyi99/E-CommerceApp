package com.example.e_commerceapp2.ui.cart;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerceapp2.Cart;
import com.example.e_commerceapp2.Product;
import com.example.e_commerceapp2.ProductDetailActivity;
import com.example.e_commerceapp2.ProductListActivity;
import com.example.e_commerceapp2.R;
import com.example.e_commerceapp2.UserManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FirebaseFirestore database;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter cartAdapter;
    TextView totalPrice;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        if(mAuth.getCurrentUser()!=null){
            Log.d("TAG","User is signed in.");

            View root = inflater.inflate(R.layout.fragment_cart, container, false);

            recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);

            //initialise Firestore & FireAuth
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            totalPrice = root.findViewById(R.id.totalPrice);

            //query returns all cart items based on userID
            Query query = database.collection("Carts").document(UserManager.getUserCartId())
                    .collection("Basket");

            Log.d("TAG","Data retrieved: "+ query);

            //Using Firebase UI to populate RecylerView
            //Configure recycler adapter options
            FirestoreRecyclerOptions<Cart> options = new FirestoreRecyclerOptions.Builder<Cart>()
                    .setQuery(query, Cart.class)
                    .build();

            cartAdapter = new FirestoreRecyclerAdapter<Cart, NotificationsFragment.CartViewHolder>(options) {

                @Override
                public NotificationsFragment.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
                    return new NotificationsFragment.CartViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull NotificationsFragment.CartViewHolder holder, int position, @NonNull Cart model) {

                    Picasso.get().load(model.getImage()).into(holder.image);
                    holder.product.setText(model.getProduct());
                    holder.price.setText("$"+model.getPrice());
                    holder.quantity.setText(String.valueOf(model.getQuantity()));

                    //to compute subtotal
                    Task<QuerySnapshot> computeTotal = database.collection("Carts").document(UserManager.getUserCartId())
                            .collection("Basket")
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    double subtotal=0.0, itemTotalCost;
                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                        itemTotalCost = snapshot.getDouble("price") * snapshot.getDouble("quantity");
                                        subtotal += itemTotalCost;
                                    }
                                    totalPrice.setText(String.format("$"+"%.2f",subtotal));
                                }
                            });


                    holder.btnDelete.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {

                            final String docId = cartAdapter.getSnapshots().getSnapshot(position).toString().split("Basket/")[1].split(",")[0];
                            Log.d("TAG", docId);
                            database.collection("Carts")
                                    .document(UserManager.getUserCartId())
                                    .collection("Basket")
                                    .document(docId)
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                notifyDataSetChanged();
                                                Toast.makeText(getContext(),"Item deleted", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(getContext(),"ERROR: " + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });

                    holder.btnMinus.setOnClickListener(new View.OnClickListener() {
                        final String docId = cartAdapter.getSnapshots().getSnapshot(position).toString().split("Basket/")[1].split(",")[0];
                        @Override
                        public void onClick(View view) {
                            if(model.getQuantity()>1){
                                model.setQuantity(model.getQuantity()-1);
                                model.setTotalPrice(model.getQuantity()*model.getPrice());
                                //update quantity
                                holder.quantity.setText(new StringBuilder().append(model.getQuantity()));
                                updateFirestore(docId, model);
                            }
                        }
                    });

                    holder.btnPlus.setOnClickListener(new View.OnClickListener() {
                        final String docId = cartAdapter.getSnapshots().getSnapshot(position).toString().split("Basket/")[1].split(",")[0];
                        @Override
                        public void onClick(View view) {
                            if(model.getQuantity()>=1){
                                model.setQuantity(model.getQuantity()+1);
                                Log.d("TAG","New quantity: "+ model.getQuantity());
                                model.setTotalPrice(model.getQuantity()*model.getPrice());
                                //update quantity
                                holder.quantity.setText(new StringBuilder().append(model.getQuantity()));
                                updateFirestore(docId, model);
                            }
                        }
                    });
                }
            };

            recyclerView.setAdapter(cartAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            return root;

        }else{
            Log.d("TAG","User is not signed in.");
            return inflater.inflate(R.layout.login_prompt, container, false);
        }

    }

    private class CartViewHolder extends RecyclerView.ViewHolder {

        private ImageView image, btnMinus, btnPlus, btnDelete;
        private TextView product, price, quantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.productImage3);
            product = itemView.findViewById(R.id.productName3);
            price = itemView.findViewById(R.id.productPrice3);
            quantity = itemView.findViewById(R.id.productQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

    }

    private void updateFirestore(String docId, Cart model){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference docRef = database.collection("Carts")
                .document(UserManager.getUserCartId())
                .collection("Basket")
                .document(docId);

        docRef.update("quantity", model.getQuantity())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "Cart item updated successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG","Error updating cart item.");
                    }
                });
    }



    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null) {
            cartAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuth.getCurrentUser()!=null) {
            cartAdapter.stopListening();
        }
    }
}