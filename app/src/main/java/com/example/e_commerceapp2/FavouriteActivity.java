package com.example.e_commerceapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerceapp2.ui.cart.NotificationsFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class FavouriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter favouriteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //Initialise Firestore & FireAuth
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        //Query returns all cart items based on userID
        Query query = database.collection("Carts").document(UserManager.getUserCartId())
                .collection("Favourite");

        Log.d("TAG","Data retrieved: "+ query);

        //Using Firebase UI to populate RecylerView
        //Configure recycler adapter options
        FirestoreRecyclerOptions<Favourite> options = new FirestoreRecyclerOptions.Builder<Favourite>()
                .setQuery(query, Favourite.class)
                .build();

        favouriteAdapter = new FirestoreRecyclerAdapter<Favourite, FavouriteViewHolder>(options) {

            @Override
            public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_item, parent, false);
                return new FavouriteViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position, @NonNull Favourite model) {

                Picasso.get().load(model.getImage()).into(holder.image);
                holder.product.setText(model.getProduct());
                holder.price.setText("$" + model.getPrice());

                holder.btnDelete.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {

                        final String docId = favouriteAdapter.getSnapshots().getSnapshot(position).toString().split("Favourite/")[1].split(",")[0];
                        Log.d("TAG", docId);
                        database.collection("Carts")
                                .document(UserManager.getUserCartId())
                                .collection("Favourite")
                                .document(docId)
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            notifyDataSetChanged();
                                            Toast.makeText(getApplicationContext(),"Item deleted", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(getApplicationContext(),"ERROR: " + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
                ;
            }
        };

        recyclerView.setAdapter(favouriteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    private class FavouriteViewHolder extends RecyclerView.ViewHolder {

        private ImageView image, btnDelete;
        private TextView product, price;

        public FavouriteViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.productImage4);
            product = itemView.findViewById(R.id.productName4);
            price = itemView.findViewById(R.id.productPrice4);
            btnDelete = itemView.findViewById(R.id.btnDelete2);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        favouriteAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        favouriteAdapter.stopListening();
    }

    public void clickBack(View v){
        onBackPressed();
    }
}