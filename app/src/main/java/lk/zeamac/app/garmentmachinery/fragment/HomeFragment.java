package lk.zeamac.app.garmentmachinery.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import lk.zeamac.app.garmentmachinery.R;
import lk.zeamac.app.garmentmachinery.activity.OrderInfoActivity;
import lk.zeamac.app.garmentmachinery.adapter.BestDealViewAdapter;
import lk.zeamac.app.garmentmachinery.adapter.CategoryAdapter;
import lk.zeamac.app.garmentmachinery.entity.BestDealEntity;
import lk.zeamac.app.garmentmachinery.entity.CategoryEntity;
import lk.zeamac.app.garmentmachinery.entity.UserEntity;

public class HomeFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    private RecyclerView.Adapter categoryViewAdapter, bestDealViewAdapter;
    private RecyclerView recyclerViewCategory, recyclerViewBestDeal;
    private ArrayList<CategoryEntity> categoryItems;
    private FirebaseFirestore fireStore;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);

        fireStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        fragment.findViewById(R.id.whatsapp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String phoneNumber = "+94716131046";
                    String message = "Hello Garment Machinery Store!";
                    String encodedMessage = URLEncoder.encode(message, "UTF-8");
                    Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + encodedMessage);
                    Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intentWhatsapp);

                } catch (UnsupportedEncodingException e) {
                    Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                } catch (android.content.ActivityNotFoundException e) {
                    gotoUrl("https://www.whatsapp.com/");
                }
            }

            private void gotoUrl(String s) {
                Uri uri = Uri.parse(s);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });



        fragment.findViewById(R.id.locationSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLocation = new Intent(getActivity(), OrderInfoActivity.class);
                startActivity(intentLocation);
            }
        });

        fragment.findViewById(R.id.textInputSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new ShopFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        drawerLayout = getActivity().findViewById(R.id.drawerLayout);
        navigationView = getActivity().findViewById(R.id.navigationView);
        toolbar = fragment.findViewById(R.id.toolBar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.userName);
        TextView userEmail = headerView.findViewById(R.id.userEmail);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            fireStore.collection("Users").document(currentUser.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        UserEntity userEntity = document.toObject(UserEntity.class);
                        if (userEntity != null) {
                            userName.setText(userEntity.getName());
                            userEmail.setText(userEntity.getEmail());
                        }
                    } else {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            userName.setText(user.getDisplayName());
                            userEmail.setText(user.getEmail());
                        }
                    }
                }
            });
        }

        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.open();

            }
        });

        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) getActivity());


        //View Category
        categoryItems = new ArrayList<>();
        RecyclerView itemView = fragment.findViewById(R.id.categoryView);
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
                        case MODIFIED:
                            CategoryEntity old = categoryItems.stream().filter(i -> i.getId().equals(category.getId())).findFirst().orElse(null);


                            if (old != null) {
                                old.setName(category.getName());
                                old.setImagePath(category.getImagePath());
                            }
                            break;
                        case REMOVED:
                            categoryItems.remove(category);
                    }
                }

                categoryAdapter.notifyDataSetChanged();
            }
        });



        //bestDeal loading
        ArrayList<BestDealEntity> itemsDeal = new ArrayList<>();
        itemsDeal.add(new BestDealEntity("kansaispecialmachinemultineedlechainstitchmachine", "Kansai Special Machine/Multineedle chain stitch machine", "25000.00"));
        itemsDeal.add(new BestDealEntity("computercontrolledcyclemachine", "Computer controlled cycle machine", "35000.00"));
        itemsDeal.add(new BestDealEntity("doubleneedlechainstitchmachine", "Double needle lock stitch machine", "15000.00"));
        itemsDeal.add(new BestDealEntity("singleneedlelockstitchmachine", "Single needle lock stitch machine", "7500.00"));

        recyclerViewBestDeal = fragment.findViewById(R.id.bestDealView);
        recyclerViewBestDeal.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        bestDealViewAdapter = new BestDealViewAdapter(itemsDeal, getActivity().getSupportFragmentManager());
        recyclerViewBestDeal.setAdapter(bestDealViewAdapter);

    }

    @Override
    public void onCategoryClick(String categoryType, String categoryName) {

        ShopFragment shopFragment = new ShopFragment();

        Bundle ct = new Bundle();
        ct.putString("categoryType", categoryType);
        shopFragment.setArguments(ct);

        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, shopFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}