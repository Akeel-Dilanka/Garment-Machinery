package lk.zeamac.app.garmentmachinery.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import lk.zeamac.app.garmentmachinery.R;
import lk.zeamac.app.garmentmachinery.adapter.MyOrderDetailsViewAdapter;
import lk.zeamac.app.garmentmachinery.entity.BestDealEntity;
import lk.zeamac.app.garmentmachinery.entity.MyOrderDetailsEntity;

public class OrderDetailsActivity extends AppCompatActivity {

    private RecyclerView.Adapter orderDetailsViewAdapter;
    private RecyclerView recyclerViewOrderDetails;
    private ArrayList<MyOrderDetailsEntity> itemsOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        findViewById(R.id.orderDetailsBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderDetailsActivity.this,MainActivity.class));
            }
        });

        itemsOrder = new ArrayList<>();
        itemsOrder.add(new MyOrderDetailsEntity("01","singleneedlelockstitchmachine","Single needle lock stitch machine","7500.00","Pending","02 jun 2023"));
        itemsOrder.add(new MyOrderDetailsEntity("02","doubleneedlechainstitchmachine","Double needle lock stitch machine","15000.00","Pending","02 jun 2023"));
        itemsOrder.add(new MyOrderDetailsEntity("03","computercontrolledcyclemachine","Computer controlled cycle machine","35000.00","Pending","02 jun 2023"));
        itemsOrder.add(new MyOrderDetailsEntity("04","kansaispecialmachinemultineedlechainstitchmachine","Kansai Special Machine/Multineedle chain stitch machine","25000.00","Pending","02 jun 2023"));

        recyclerViewOrderDetails = findViewById(R.id.OrderView);
        recyclerViewOrderDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        orderDetailsViewAdapter = new MyOrderDetailsViewAdapter(itemsOrder);
        recyclerViewOrderDetails.setAdapter(orderDetailsViewAdapter);

    }
}