package lk.zeamac.app.garmentmachinery.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lk.zeamac.app.garmentmachinery.R;
import lk.zeamac.app.garmentmachinery.adapter.BestDealViewAdapter;
import lk.zeamac.app.garmentmachinery.adapter.ProductAdapter;
import lk.zeamac.app.garmentmachinery.adapter.ShopProductAdapter;
import lk.zeamac.app.garmentmachinery.entity.BestDealEntity;
import lk.zeamac.app.garmentmachinery.entity.CartEntity;
import lk.zeamac.app.garmentmachinery.entity.ProductEntity;
import lk.zeamac.app.garmentmachinery.entity.ShopProductEntity;
import lk.zeamac.app.garmentmachinery.fragment.HomeFragment;

public class SingleProductViewActivity extends AppCompatActivity implements ShopProductAdapter.OnProductClickListener {

    private FirebaseStorage storage;
    int count = 1;
    int updatedValue;
    double productPrice;
    Long fixedPrice;
    private ArrayList<ShopProductEntity> productItems;
    private FirebaseFirestore fireStore;
    private FirebaseAuth firebaseAuth;
    private ShopProductAdapter shopProductAdapter;
    private RecyclerView recyclerViewSimilarProduct;
    TextView title, description, price, qty, totalPrice, increase, decrease, updateQty;
    ImageView image;
    String categoryName, productId, queryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product_view);

        storage = FirebaseStorage.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            productId = bundle.getString("productId");
            OnProductClick(productId);
        }

        findViewById(R.id.checkoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SingleProductViewActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.imageViewBackBtn).setOnClickListener(v -> finish());

        Spinner spinner = findViewById(R.id.monthCountSingleProduct);
        String[] itemsMonth = {"Select Months", "1 Month", "2 Months", "3 Months", "4 Months", "5 Months", "6 Months", "7 Months", "8 Months", "9 Months", "10 Months", "11 Months", "12 Months"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsMonth);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Spinner spinnerDelivery = findViewById(R.id.deliveryTypeSingleProduct);
        String[] itemsDelivery = {"Select Delivery Area", "In Kurunegala area", "In other areas"};
        ArrayAdapter<String> adapterDelivery = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsDelivery);
        adapterDelivery.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDelivery.setAdapter(adapterDelivery);


        //similar product loading
//        ArrayList<ProductEntity> itemsProduct = new ArrayList<>();
//        itemsProduct.add(new ProductEntity("singleneedlelockstitchmachine","Single needle lock stitch machine","7500.00"));
//        itemsProduct.add(new ProductEntity("singleneedlelockstitchmachine","Double needle lock stitch machine","15000.00"));
//        itemsProduct.add(new ProductEntity("singleneedlelockstitchmachine","Overlock/Serger Machine","8000.00"));
//        itemsProduct.add(new ProductEntity("singleneedlelockstitchmachine","Flatlock/Cover stitch Machine","20000.00"));
//        itemsProduct.add(new ProductEntity("singleneedlelockstitchmachine","Kansai Special Machine/Multineedle chain stitch machine","25000.00"));
//
//        recyclerViewSimilarProduct = findViewById(R.id.similarView);
//        recyclerViewSimilarProduct.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
//        similarProductAdapter = new ProductAdapter(itemsProduct);
//        recyclerViewSimilarProduct.setAdapter(similarProductAdapter);

        productItems = new ArrayList<>();
        recyclerViewSimilarProduct = findViewById(R.id.similarView);
        recyclerViewSimilarProduct.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        shopProductAdapter = new ShopProductAdapter(productItems, SingleProductViewActivity.this, this::OnProductClick);
        recyclerViewSimilarProduct.setAdapter(shopProductAdapter);


        fireStore.collection("Products")
                //                .limit(5)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        for (DocumentChange change : value.getDocumentChanges()) {
                            ShopProductEntity product = change.getDocument().toObject(ShopProductEntity.class);
                            switch (change.getType()) {
                                case ADDED:
                                    productItems.add(product);
                                case MODIFIED:
                                    ShopProductEntity old = productItems.stream().filter(i -> i.getId().equals(product.getId())).findFirst().orElse(null);


                                    if (old != null) {
                                        old.setName(product.getName());
                                        old.setDescription(product.getDescription());
                                        old.setCategory(product.getCategory());
                                        old.setQty(product.getQty());
                                        old.setPrice(product.getPrice());
                                        old.setImage(product.getImage());
                                    }
                                    break;
                                case REMOVED:
                                    productItems.remove(product);
                            }
                        }

                        shopProductAdapter.notifyDataSetChanged();

                    }
                });


        findViewById(R.id.addToCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Long totalQty = Long.parseLong(String.valueOf(count));


                DocumentReference reference = fireStore.collection("Cart").document(queryId);
                reference.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot productDoc = task.getResult();
                        if (productDoc.exists()) {
                            Toast.makeText(SingleProductViewActivity.this, "Already added! Quantity Updated", Toast.LENGTH_SHORT).show();
                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("qty", totalQty);

                            reference.update(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                        } else {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = user.getUid();

                            DocumentReference reference1 = fireStore.collection("Cart").document(queryId);

                            CartEntity cart = new CartEntity(queryId, queryId, userId, totalQty, fixedPrice, false);

                            reference1.set(cart).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(SingleProductViewActivity.this, "Add To Cart Successful", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SingleProductViewActivity.this, "Add To Cart Failed", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }


                });

            }
        });


    }

    @Override
    public void OnProductClick(String product) {
        queryId = product;
        loadProduct(product);
        count = 1;
    }

    public void loadProduct(String productId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("Products").whereEqualTo("id", productId);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override


            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ShopProductEntity product = document.toObject(ShopProductEntity.class);

                        title = findViewById(R.id.singleTitle);
                        description = findViewById(R.id.singleDescription);
                        price = findViewById(R.id.singlePrice);
                        qty = findViewById(R.id.availableQty);
                        image = findViewById(R.id.singleImage);
                        totalPrice = findViewById(R.id.total_fee);
                        increase = findViewById(R.id.textIncrease);
                        decrease = findViewById(R.id.textDecrease);
                        updateQty = findViewById(R.id.singleQty);


                        updatedValue = Integer.parseInt(product.getQty());
                        productPrice = Double.parseDouble(product.getPrice());
                        Double d = Double.parseDouble(product.getPrice());
                        fixedPrice = d.longValue();


                        totalPrice.setText("" + productPrice);
                        updateQty.setText("1");

                        increase.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (updatedValue > count) {
                                    count++;
                                    totalPrice.setText("" + count * productPrice);
                                    updateQty.setText("" + count);


                                } else {
                                    count = updatedValue;
                                }
                            }
                        });
                        decrease.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (count <= 1) {
                                    count = 1;
                                } else {
                                    count--;
                                    updateQty.setText("" + count);
                                    totalPrice.setText("" + count * productPrice);


                                }

                            }
                        });


                        updateQty.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                try {
                                    int inputValue = Integer.parseInt(editable.toString());
                                    if (inputValue > updatedValue) {
                                        Toast.makeText(SingleProductViewActivity.this, "Quantity cannot exceed available stock", Toast.LENGTH_SHORT).show();
                                        updateQty.setText(String.valueOf(updatedValue));
                                    } else if (inputValue < 1) {
                                        Toast.makeText(SingleProductViewActivity.this, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
                                        updateQty.setText(String.valueOf(1));
                                    } else {
                                        count = inputValue;
                                        totalPrice.setText("" + count * productPrice);
                                    }
                                } catch (NumberFormatException e) {
                                    Toast.makeText(SingleProductViewActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                        storage.getReference("product-images/" + product.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(image);
                            }
                        });

                        title.setText(product.getName());
                        description.setText(product.getDescription());
                        price.setText(product.getPrice());
                        qty.setText(product.getQty());
                        categoryName = product.getCategory();

                        OnCategorySearch(categoryName);

                    }
                } else {
                    Log.w("FireStore", "Error getting documents", task.getException());
                }
            }
        });

    }

    private void OnCategorySearch(String categoryType) {

        ArrayList<ShopProductEntity> filteredList = new ArrayList<>();
        for (ShopProductEntity product : productItems) {

            if (product.getCategory().equals(categoryType)) {
                filteredList.add(product);
            }
            shopProductAdapter.setSearchList(filteredList);
        }

    }

}