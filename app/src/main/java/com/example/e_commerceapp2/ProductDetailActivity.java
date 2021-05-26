package com.example.e_commerceapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {

    private FirebaseFirestore database;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FloatingActionButton addToCart;
    private String name, image, brand, description;
    private Integer price, stock, quantity=1;
    private Button addCartItemSave;
    private ImageView productImage, dialogImage, addQuantity, minusQuantity, addCartItemCancel;
    private TextView productName, productPrice, productStock, productBrand, productDescription, dialogPrice, dialogQuantity;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productImage = (ImageView) findViewById(R.id.productImage2);
        productName = (TextView) findViewById(R.id.productName2);
        productPrice = (TextView) findViewById(R.id.productPrice2);
        productStock = (TextView) findViewById(R.id.productStock);
        productBrand = (TextView) findViewById(R.id.productBrand);
        productDescription = (TextView) findViewById(R.id.productDescription);

        database = FirebaseFirestore.getInstance();

        //Retrieve selected category from intent
        Intent in = getIntent();
        Bundle b = in.getExtras();
        name = b.getString("name");
        image = b.getString("image");
        price = b.getInt("price");
        stock = b.getInt("stock");
        brand = b.getString("brand");
        description = b.getString("description");


        //Set info to TextView
        Picasso.get().load(image).into(productImage);
        productName.setText(name);
        productPrice.setText("$"+price);
        productStock.setText(String.valueOf(stock));
        productBrand.setText(brand);
        productDescription.setText(description);

    }

    public void addToCart(int quantity) {

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("image", image);
        cartItem.put("product", name);
        Log.d("TAG", "Product Price: "+ price);
        cartItem.put("price", price);
        cartItem.put("quantity", quantity);

         database.collection("Carts").document(UserManager.getUserCartId())
                .collection("Basket").add(cartItem).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
             @Override
             public void onComplete(@NonNull Task<DocumentReference> task) {
                 Log.d("TAG", "Item added to cart");
                 Toast.makeText(getApplicationContext(), "Added to cart",
                         Toast.LENGTH_SHORT).show();
                 finish();
             }
         });

    }

    public void addToFavourite(View v) {

        if(mAuth.getCurrentUser()!=null) {
            Map<String, Object> favouriteItem = new HashMap<>();
            favouriteItem.put("image", image);
            favouriteItem.put("product", name);
            favouriteItem.put("price", price);

            database.collection("Carts").document(UserManager.getUserCartId())
                    .collection("Favourite").add(favouriteItem).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    Log.d("TAG", "Item added to favourite");
                    Toast.makeText(getApplicationContext(), "Added to favourite",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        else{
            dialogBuilder = new AlertDialog.Builder(this);
            final View loginPrompt = getLayoutInflater().inflate(R.layout.login_prompt, null);
            dialogBuilder.setView(loginPrompt);
            dialog = dialogBuilder.create();
            dialog.show();
        }

    }

    //create pop-up
    public void addToCartDialog(View v){
        if(mAuth.getCurrentUser()!=null) {

            dialogBuilder = new AlertDialog.Builder(this);
            View cartPopupView = getLayoutInflater().inflate(R.layout.add_cart_item_popup, null);

            dialogImage = (ImageView) cartPopupView.findViewById(R.id.productImage5);
            dialogPrice = (TextView) cartPopupView.findViewById(R.id.productPrice5);
            dialogQuantity = (TextView) cartPopupView.findViewById(R.id.productQuantity2);
            addQuantity = (ImageView) cartPopupView.findViewById(R.id.btnPlus2);
            minusQuantity = (ImageView) cartPopupView.findViewById(R.id.btnMinus2);
            addCartItemCancel = (ImageView) cartPopupView.findViewById(R.id.btnDelete3);
            addCartItemSave = (Button) cartPopupView.findViewById(R.id.addToCart2);

            Picasso.get().load(image).into(dialogImage);
            dialogPrice.setText("$" + price);
            dialogQuantity.setText(quantity.toString());

            addQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (quantity >= 1) {
                        quantity++;
                        dialogQuantity.setText(quantity.toString());
                    }
                }
            });
            minusQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (quantity > 1) {
                        quantity--;
                        dialogQuantity.setText(quantity.toString());
                    }
                }
            });
            addCartItemCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            addCartItemSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToCart(Integer.parseInt(dialogQuantity.getText().toString()));
                }
            });

            dialogBuilder.setView(cartPopupView);
            dialog = dialogBuilder.create();
            dialog.show();
        }
        else{
            dialogBuilder = new AlertDialog.Builder(this);
            final View loginPrompt = getLayoutInflater().inflate(R.layout.login_prompt, null);
            dialogBuilder.setView(loginPrompt);
            dialog = dialogBuilder.create();
            dialog.show();
        }

    }

    public void clickBack(View v){
        Log.d("TAG", "clickedddddd");
        onBackPressed();
    }
}