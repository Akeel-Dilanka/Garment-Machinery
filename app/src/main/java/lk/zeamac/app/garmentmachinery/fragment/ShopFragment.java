package lk.zeamac.app.garmentmachinery.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import lk.zeamac.app.garmentmachinery.R;
import lk.zeamac.app.garmentmachinery.activity.MainActivity;
import lk.zeamac.app.garmentmachinery.activity.SingleProductViewActivity;
import lk.zeamac.app.garmentmachinery.adapter.CategoryAdapter;
import lk.zeamac.app.garmentmachinery.adapter.ShopProductAdapter;
import lk.zeamac.app.garmentmachinery.entity.CategoryEntity;
import lk.zeamac.app.garmentmachinery.entity.ShopProductEntity;

public class ShopFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener, ShopProductAdapter.OnProductClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    private ShopProductAdapter shopAllProductViewAdapter;
    private SearchView searchView;
    private ArrayList<ShopProductEntity> productItems;
    private ArrayList<CategoryEntity> categoryItems;
    private FirebaseFirestore fireStore;


    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);

        fireStore = FirebaseFirestore.getInstance();

        fragment.findViewById(R.id.imageViewBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBackBtn = new Intent(getActivity(), MainActivity.class);
                startActivity(intentBackBtn);
            }
        });

        searchView = fragment.findViewById(R.id.textInputSearch);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchList(newText);

                return false;
            }
        });


        //View Category
        categoryItems = new ArrayList<>();
        RecyclerView itemView = fragment.findViewById(R.id.category_product_view);
        CategoryAdapter categoryAdapter = new CategoryAdapter(categoryItems, getActivity(), this::onCategoryClick);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL,false);

        itemView.setLayoutManager(linearLayoutManager);
        itemView.setAdapter(categoryAdapter);

        fireStore.collection("Categories").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (DocumentChange change : value.getDocumentChanges()) {
                    CategoryEntity category = change.getDocument().toObject(CategoryEntity.class);
                    switch (change.getType()) {
                        case ADDED:
                            categoryItems.add(category);
                            break;
                        case MODIFIED:
                            CategoryEntity old = categoryItems.stream().filter(i -> i.getId().equals(category.getId())).findFirst().orElse(null);

                            if (old != null) {
                                old.setName(category.getName());
                                old.setImagePath(category.getImagePath());
                            }
                            break;
                        case REMOVED:
                            categoryItems.remove(category);
                            break;
                    }
                }

                categoryAdapter.notifyDataSetChanged();
            }
        });


        //View Product
        productItems = new ArrayList<>();
        RecyclerView recyclerView = fragment.findViewById(R.id.view_all_product_view);
        shopAllProductViewAdapter = new ShopProductAdapter(productItems, getActivity(),this::OnProductClick);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(shopAllProductViewAdapter);

        fireStore.collection("Products").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (DocumentChange change : value.getDocumentChanges()) {
                    ShopProductEntity product = change.getDocument().toObject(ShopProductEntity.class);
                    switch (change.getType()) {
                        case ADDED:
                            productItems.add(product);
                            break;
                        case MODIFIED:
                            ShopProductEntity old = productItems.stream().filter(i -> i.getId().equals(product.getId())).findFirst().orElse(null);

                            if (old != null) {
                                old.setName(product.getName());
                                old.setCategory(product.getCategory());
                                old.setPrice(product.getPrice());
                                old.setImage(product.getImage());
                            }
                            break;
                        case REMOVED:
                            productItems.remove(product);
                            break;
                    }
                }

                shopAllProductViewAdapter.notifyDataSetChanged();

                Bundle ct = getArguments();
                if (ct != null) {
                    String categoryType = ct.getString("categoryType");

                    filterProductsByCategory(categoryType);

                }
            }
        });


    }

    private void searchList(String text){
        ArrayList<ShopProductEntity> itemsList = new ArrayList<>();
        for (ShopProductEntity product : productItems) {
            if (product.getName().toLowerCase().contains(text.toLowerCase())) {
                itemsList.add(product);
            }
        }
        if (itemsList.isEmpty()) {
            Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
        } else {
            shopAllProductViewAdapter.setSearchList(itemsList);
        }
    }

    @Override
    public void onCategoryClick(String categoryType, String categoryName) {

        ArrayList<ShopProductEntity> filteredList = new ArrayList<>();
        for (ShopProductEntity product : productItems) {
            if (product.getCategory().equals(categoryType)) {
                filteredList.add(product);
            }
        }

        shopAllProductViewAdapter.setSearchList(filteredList);
    }

    private void filterProductsByCategory(String categoryType) {
        ArrayList<ShopProductEntity> filteredList = new ArrayList<>();
        for (ShopProductEntity product : productItems) {
            if (product.getCategory().equals(categoryType)) {
                filteredList.add(product);
            }
        }
        shopAllProductViewAdapter.setSearchList(filteredList);
    }

    @Override
    public void OnProductClick(String product) {
        Intent intent = new Intent(getActivity(), SingleProductViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("productId", product);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
