package lk.zeamac.app.garmentmachinery.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import lk.zeamac.app.garmentmachinery.R;

public class StoreMapLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_map_location);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.mapId);
        mapFragment.getMapAsync(StoreMapLocationActivity.this);

        findViewById(R.id.shopNowBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentShopNowBtn = new Intent(StoreMapLocationActivity.this,MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentShopNowBtn);
                finish();
            }
        });

        findViewById(R.id.storeLocationBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStoreLocationBackBtn = new Intent(StoreMapLocationActivity.this,OrderInfoActivity.class);
                startActivity(intentStoreLocationBackBtn);
            }
        });


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        LatLng latLng = new LatLng(7.378202873142259, 80.1868529831793);

        map.addMarker(new MarkerOptions().position(latLng).title("Power Mart Shop"));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

    }
}