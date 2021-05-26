package com.example.e_commerceapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e_commerceapp2.ui.category.DashboardFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class ProductListActivity extends AppCompatActivity {

    private FirebaseFirestore database;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView2);
        database = FirebaseFirestore.getInstance();

        //Retrieve selected category from intent
        Intent in = getIntent();
        Bundle b = in.getExtras();
        String category = b.getString("category");

        //Query returns all product with selected category
        Query query = database.collection("Products").whereEqualTo("category",category);

        //Using Firebase UI to populate RecylerView
        //Configure recycler adapter options
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();

        productAdapter = new FirestoreRecyclerAdapter<Product, ProductViewHolder>(options) {

            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
                return new ProductViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
                Picasso.get().load(model.getImage()).into(holder.image);
                holder.name.setText(model.getName());
                holder.price.setText("$"+model.getPrice());

                //Store selected item and display product details accordingly
                holder.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent productDetailActivity = new Intent(getApplicationContext(), ProductDetailActivity.class);
                        productDetailActivity.putExtra("image",model.getImage());
                        productDetailActivity.putExtra("name",model.getName());
                        productDetailActivity.putExtra("price", model.getPrice());
                        productDetailActivity.putExtra("stock",model.getStock());
                        productDetailActivity.putExtra("brand",model.getBrand());
                        productDetailActivity.putExtra("description",model.getDescription());
                        startActivity(productDetailActivity);
                    }
                });
            }
        };

        recyclerView.setAdapter(productAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }


    private class ProductViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView name;
        private TextView price;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.productImage);
            name = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.productPrice);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        productAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        productAdapter.stopListening();
    }

    public void clickBack(View v){
        onBackPressed();
    }

}