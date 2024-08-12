package lk.zeamac.app.garmentmachinery.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

import lk.zeamac.app.garmentmachinery.R;
import lk.zeamac.app.garmentmachinery.activity.CheckoutActivity;
import lk.zeamac.app.garmentmachinery.adapter.CartViewAdapter;
import lk.zeamac.app.garmentmachinery.entity.CartEntity;

public class CartFragment extends Fragment {

    private CartViewAdapter cartViewAdapter;
    private RecyclerView recyclerViewCart;
    private ArrayList<CartEntity> productItems;
    private RecyclerView recyclerViewProductView;
    private FirebaseFirestore fireStore;
    private FirebaseAuth firebaseAuth;
    AlertDialog.Builder builder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);

        fireStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        builder = new AlertDialog.Builder(getContext());

        cartViewAdapter = new CartViewAdapter(productItems, getActivity());
        cartViewAdapter.setOnProductClickRemoveListener(new CartViewAdapter.OnProductClickListener() {
            @Override
            public void OnProductClick(int position) {
                builder.setTitle("Alert")
                        .setMessage("Do you want to remove Product")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeProduct(position);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

        fragment.findViewById(R.id.checkoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCheckOut = new Intent(getActivity(), CheckoutActivity.class);
                startActivity(intentCheckOut);
            }
        });

        //     View Product

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            CollectionReference cartRef = fireStore.collection("Cart");
            Query query = cartRef.whereEqualTo("userId", currentUser.getUid());
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        productItems = new ArrayList<>();
                        recyclerViewProductView = fragment.findViewById(R.id.cartView);
                        recyclerViewProductView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
                        cartViewAdapter = new CartViewAdapter(productItems, getActivity());
                        recyclerViewProductView.setAdapter(cartViewAdapter);
                        for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
                            CartEntity cart = change.getDocument().toObject(CartEntity.class);
                            switch (change.getType()) {
                                case ADDED:
                                    productItems.add(cart);
                                case MODIFIED:
                                    CartEntity old = productItems.stream().filter(i -> i.getId().equals(cart.getId())).findFirst().orElse(null);


                                    ProgressDialog dialog = new ProgressDialog(getActivity());
                                    dialog.setMessage("Adding new Price");
                                    dialog.setCancelable(true);



                                    if (old != null) {
                                        old.setQty(cart.getQty());
                                        old.setCartProductFixedPrice(cart.getCartProductFixedPrice());




//                                        dialog.dismiss();
                                    }
                                    break;
                                case REMOVED:
                                    productItems.remove(cart);
                            }
                        }
                        cartViewAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

//        Spinner spinner = fragment.findViewById(R.id.monthCount);
//        String[] items = {"Select Months","1 Month", "2 Months", "3 Months", "4 Months", "5 Months", "6 Months", "7 Months", "8 Months", "9 Months", "10 Months", "11 Months", "12 Months"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//
//        Spinner spinnerDelivery = fragment.findViewById(R.id.deliveryType);
//        String[] itemsDelivery = {"Select Delivery Area","In Kurunegala area", "In other areas"};
//        ArrayAdapter<String> adapterDelivery = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, itemsDelivery);
//        adapterDelivery.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerDelivery.setAdapter(adapterDelivery);
//
//        //Cart loading
//        ArrayList<CartEntity> itemsCart = new ArrayList<>();
////        itemsCart.add(new CartEntity("singleneedlelockstitchmachine","Single needle lock stitch machine","7500.00","2","15000.00"));
////        itemsCart.add(new CartEntity("singleneedlelockstitchmachine","Double needle lock stitch machine","15000.00","1","15000.00"));
////        itemsCart.add(new CartEntity("singleneedlelockstitchmachine","Overlock/Serger Machine","8000.00","1","8000.00"));
////        itemsCart.add(new CartEntity("singleneedlelockstitchmachine","Flatlock/Cover stitch Machine","20000.00","1","20000.00"));
////        itemsCart.add(new CartEntity("singleneedlelockstitchmachine","Kansai Special Machine/Multineedle chain stitch machine","25000.00","1","25000.00"));
//
//        recyclerViewCart = fragment.findViewById(R.id.cartView);
//        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));
//        cartViewAdapter = new CartViewAdapter(itemsCart, getActivity());
//        recyclerViewCart.setAdapter(cartViewAdapter);

    }

    private void removeProduct(int position) {
        CartEntity productToDelete = productItems.get(position);

        fireStore.collection("Cart").whereEqualTo("id", productToDelete.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch writeBatch = fireStore.batch();
                List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot doc : snapshots) {
                    writeBatch.delete(doc.getReference());
                }
                writeBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        productItems.remove(position);
                        cartViewAdapter.notifyItemRemoved(position);
                        Toast.makeText(getActivity().getApplicationContext(), "Product Removed", Toast.LENGTH_LONG).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), "Failed to Removed Product Images: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Failed to Removed Product: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}