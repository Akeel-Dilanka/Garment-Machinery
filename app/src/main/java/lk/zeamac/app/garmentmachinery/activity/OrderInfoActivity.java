package lk.zeamac.app.garmentmachinery.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import lk.zeamac.app.garmentmachinery.R;

public class OrderInfoActivity extends AppCompatActivity {

    Button buttonDelivery,buttonConfirm;
    LinearLayout linearLayout;
    private FirebaseFirestore fireStore;
    private FirebaseAuth firebaseAuth;
    TextView address,addNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);


        findViewById(R.id.orderInfoBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentOrderInfoBackBtn = new Intent(OrderInfoActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentOrderInfoBackBtn);
                finish();
            }

        });


        findViewById(R.id.orderInfoConfirmBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentOrderInfoConfirmBtn = new Intent(OrderInfoActivity.this, DeliveryLocationActivity.class);
                startActivity(intentOrderInfoConfirmBtn);
            }
        });


        findViewById(R.id.pickupBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPickupBtn = new Intent(OrderInfoActivity.this,StoreMapLocationActivity.class);
                startActivity(intentPickupBtn);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        buttonDelivery= findViewById(R.id.deliveryBtn);
        linearLayout = findViewById(R.id.addAddress);
        buttonConfirm = findViewById(R.id.orderInfoConfirmBtn);
        address = findViewById(R.id.deliveryText);
        addNew = findViewById(R.id.editAddress);

        buttonDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonDelivery.setBackgroundColor(Color.rgb(171,63,115));
                linearLayout.setVisibility(View.VISIBLE);
                buttonConfirm.setVisibility(View.VISIBLE);
                address.setVisibility(View.VISIBLE);
                addNew.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.editAddress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderInfoActivity.this, AddDeliveryAddressActivity.class);
                startActivity(intent);
            }
        });

        //check user available
        findViewById(R.id.orderInfoConfirmBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null){
                    fireStore.collection("UsersAddress").document(currentUser.getUid()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            DocumentSnapshot snapshot = task.getResult();

                            if(snapshot.exists()){
                                Intent intent = new Intent(OrderInfoActivity.this, DeliveryLocationActivity.class );
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(OrderInfoActivity.this, "Please Add Your Address", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });

    }



}