package lk.zeamac.app.garmentmachinery.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import lk.zeamac.app.garmentmachinery.R;
import lk.zeamac.app.garmentmachinery.adapter.FavoriteProductAdapter;
import lk.zeamac.app.garmentmachinery.adapter.ShopProductAdapter;
import lk.zeamac.app.garmentmachinery.entity.BestDealEntity;
import lk.zeamac.app.garmentmachinery.entity.FavoriteProductEntity;
import lk.zeamac.app.garmentmachinery.entity.ShopProductEntity;

public class FavoriteFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    private RecyclerView.Adapter favoriteViewAdapter;
    private RecyclerView recyclerViewFavorite;

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);

        //favorite product loading
        ArrayList<FavoriteProductEntity> itemsFavoriteProduct = new ArrayList<>();
        itemsFavoriteProduct.add(new FavoriteProductEntity("singleneedlelockstitchmachine","Single needle lock stitch machine","7500.00"));
        itemsFavoriteProduct.add(new FavoriteProductEntity("doubleneedlechainstitchmachine","Double needle lock stitch machine","15000.00"));
        itemsFavoriteProduct.add(new FavoriteProductEntity("computercontrolledcyclemachine","Computer controlled cycle machine","35000.00"));
        itemsFavoriteProduct.add(new FavoriteProductEntity("kansaispecialmachinemultineedlechainstitchmachine","Kansai Special Machine/Multineedle chain stitch machine","25000.00"));

        recyclerViewFavorite = fragment.findViewById(R.id.favoriteView);
        recyclerViewFavorite.setLayoutManager(new GridLayoutManager(getActivity(),2));
        favoriteViewAdapter = new FavoriteProductAdapter(itemsFavoriteProduct);
        recyclerViewFavorite.setAdapter(favoriteViewAdapter);

    }
}