package lk.zeamac.app.garmentmachinery.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import lk.zeamac.app.garmentmachinery.R;
import lk.zeamac.app.garmentmachinery.adapter.CheckOutAdapter;
import lk.zeamac.app.garmentmachinery.entity.CartEntity;
import lk.zeamac.app.garmentmachinery.fragment.CartFragment;

public class CheckoutActivity extends AppCompatActivity {

    private ArrayList<CartEntity> productItems;
    private RecyclerView recyclerViewProductView;
    private CheckOutAdapter cartViewAdapter;
    private FirebaseFirestore fireStore;
    TextView totalProductFee, itemTotal;
    public static String totalPrice;
    double totalAmount, changeAmount, totalAmountMonth, changeAmountFinal, totalAmountDelivery, changeAmountFinalDel;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        fireStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        totalProductFee = findViewById(R.id.total_fee);
        itemTotal = findViewById(R.id.itemTotal);

        Spinner spinnerMonth = findViewById(R.id.monthCount);
        String[] itemsMonth = {"Select Months","1 Month", "2 Months", "3 Months", "4 Months", "5 Months", "6 Months", "7 Months", "8 Months", "9 Months", "10 Months", "11 Months", "12 Months"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsMonth);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        Spinner spinnerDelivery = findViewById(R.id.deliveryType);
        String[] itemsDelivery = {"Select Delivery Area","In Kurunegala area", "In other areas"};
        ArrayAdapter<String> adapterDelivery = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsDelivery);
        adapterDelivery.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDelivery.setAdapter(adapterDelivery);

        findViewById(R.id.checkoutBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBack = new Intent(CheckoutActivity.this, MainActivity.class);
                startActivity(intentBack);
            }
        });


        findViewById(R.id.confirmRentBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentConfirmRent = new Intent(CheckoutActivity.this,PaymentMethodActivity.class);
                startActivity(intentConfirmRent);
            }
        });


        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            CollectionReference cartRef = fireStore.collection("Cart");
            Query query = cartRef.whereEqualTo("userId", currentUser.getUid());
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    String cartSize = String.valueOf(queryDocumentSnapshots.size());
                    if (!queryDocumentSnapshots.isEmpty()) {
                        productItems = new ArrayList<>();
                        recyclerViewProductView = findViewById(R.id.checkoutView);
                        recyclerViewProductView.setLayoutManager(new GridLayoutManager(CheckoutActivity.this, 1, GridLayoutManager.VERTICAL, false));
                        cartViewAdapter = new CheckOutAdapter(productItems, CheckoutActivity.this);
                        recyclerViewProductView.setAdapter(cartViewAdapter);
                        for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
                            CartEntity cart = change.getDocument().toObject(CartEntity.class);
                            itemTotal.setText(cartSize);
                            switch (change.getType()) {
                                case ADDED:
                                    productItems.add(cart);
                                case MODIFIED:
                                    CartEntity old = productItems.stream().filter(i -> i.getId().equals(cart.getId())).findFirst().orElse(null);


                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    CollectionReference cartRef = db.collection("Cart");
                                    cartRef.whereEqualTo("id", cart.getId()).whereEqualTo("selected", false).get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    for (DocumentSnapshot document : task1.getResult()) {
                                                        CartEntity item = document.toObject(CartEntity.class);
                                                        totalAmount = 0;
                                                        totalAmount += item.getCartProductFixedPrice() * old.getQty();

                                                    }
                                                    changeAmount += totalAmount;
                                                    totalPrice = String.valueOf(changeAmount);
                                                    totalProductFee.setText(totalPrice);



                                                    cartViewAdapter.notifyDataSetChanged();
                                                } else {
                                                }
                                            });

                                    if (old != null) {
                                        old.setQty(cart.getQty());
                                        old.setCartProductFixedPrice(cart.getCartProductFixedPrice());


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


        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedMonth = parentView.getItemAtPosition(position).toString();

                totalAmountMonth = 1;
                spinnerDelivery.setSelection(0);

                if (selectedMonth.equals("Select Months")) {
                    totalAmountMonth = 1;
                } else if (selectedMonth.equals("1 Months")) {
                    totalAmountMonth = 1;
                } else if (selectedMonth.equals("2 Months")) {
                    totalAmountMonth = 2;
                } else if (selectedMonth.equals("3 Months")) {
                    totalAmountMonth = 3;
                } else if (selectedMonth.equals("4 Months")) {
                    totalAmountMonth = 4;
                } else if (selectedMonth.equals("5 Months")) {
                    totalAmountMonth = 5;
                } else if (selectedMonth.equals("6 Months")) {
                    totalAmountMonth = 6;
                } else if (selectedMonth.equals("7 Months")) {
                    totalAmountMonth = 7;
                } else if (selectedMonth.equals("8 Months")) {
                    totalAmountMonth = 8;
                } else if (selectedMonth.equals("9 Months")) {
                    totalAmountMonth = 9;
                } else if (selectedMonth.equals("10 Months")) {
                    totalAmountMonth = 10;
                } else if (selectedMonth.equals("11 Months")) {
                    totalAmountMonth = 11;
                } else if (selectedMonth.equals("12 Months")) {
                    totalAmountMonth = 12;
                }

                changeAmountFinal = changeAmount * totalAmountMonth;

                totalPrice = String.valueOf(changeAmountFinal);
                totalProductFee.setText(totalPrice);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });


       spinnerDelivery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedDelivery = parentView.getItemAtPosition(position).toString();

                totalAmountDelivery = 0;

                if (selectedDelivery.equals("Select Delivery Area")) {
                    totalAmountDelivery = 0;
                } else if (selectedDelivery.equals("In Kurunegala area")) {
                    totalAmountDelivery = 1000;
                } else if (selectedDelivery.equals("In other areas")) {
                    totalAmountDelivery = 3500;
                }

                changeAmountFinalDel = changeAmountFinal + totalAmountDelivery;

                totalPrice = String.valueOf(changeAmountFinalDel);
                totalProductFee.setText(totalPrice);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

//        itemsCheckOut = new ArrayList<>();
//        itemsCheckOut.add(new CheckOutEntity("singleneedlelockstitchmachine","Single needle lock stitch machine","15000.00","2"));
//        itemsCheckOut.add(new CheckOutEntity("singleneedlelockstitchmachine","Double needle lock stitch machine","15000.00","1"));
//        itemsCheckOut.add(new CheckOutEntity("singleneedlelockstitchmachine","Overlock/Serger Machine","8000.00","1"));
//        itemsCheckOut.add(new CheckOutEntity("singleneedlelockstitchmachine","Flatlock/Cover stitch Machine","20000.00","1"));
//        itemsCheckOut.add(new CheckOutEntity("singleneedlelockstitchmachine","Kansai Special Machine/Multineedle chain stitch machine","25000.00","1"));
//
//        recyclerViewCheckOut = findViewById(R.id.checkoutView);
//        recyclerViewCheckOut.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
//        checkOutViewAdapter = new CheckOutAdapter(itemsCheckOut);
//        recyclerViewCheckOut.setAdapter(checkOutViewAdapter);

    }
}