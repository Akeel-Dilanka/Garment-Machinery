package lk.zeamac.app.garmentmachinery.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.common.subtyping.qual.Bottom;

import lk.zeamac.app.garmentmachinery.R;
import lk.zeamac.app.garmentmachinery.entity.UserEntity;
import lk.zeamac.app.garmentmachinery.fragment.AccountFragment;
import lk.zeamac.app.garmentmachinery.fragment.CartFragment;
import lk.zeamac.app.garmentmachinery.fragment.FavoriteFragment;
import lk.zeamac.app.garmentmachinery.fragment.HomeFragment;
import lk.zeamac.app.garmentmachinery.fragment.ShopFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    GoogleSignInClient googleClient;
    GoogleSignInOptions googleOptions;

    @SuppressLint({"MissingInflatedId", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(this);

        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        googleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleClient = GoogleSignIn.getClient(this, googleOptions);

    }


    private boolean isBottomNavigationItemSelected = false;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (!isBottomNavigationItemSelected) {
            isBottomNavigationItemSelected = true;

            if (itemId == R.id.bottomNavHome || itemId == R.id.sideNavHome) {
                loadFragment(new HomeFragment());
                bottomNavigationView.setSelectedItemId(R.id.bottomNavHome);
            } else if (itemId == R.id.bottomNavAccount || itemId == R.id.sideNavAccount) {
                loadFragment(new AccountFragment());
                bottomNavigationView.setSelectedItemId(R.id.bottomNavAccount);
            } else if (itemId == R.id.bottomNavCart || itemId == R.id.sideNavCart) {
                loadFragment(new CartFragment());
                bottomNavigationView.setSelectedItemId(R.id.bottomNavCart);
            } else if (itemId == R.id.bottomNavCategory || itemId == R.id.sideNavCategory) {
                loadFragment(new ShopFragment());
                bottomNavigationView.setSelectedItemId(R.id.bottomNavCategory);
            } else if (itemId == R.id.bottomNavFavorite || itemId == R.id.sideNavFavorite) {
                loadFragment(new FavoriteFragment());
                bottomNavigationView.setSelectedItemId(R.id.bottomNavFavorite);
            } else if (itemId == R.id.sideNavOrders) {
                Intent intentNaveOrders = new Intent(MainActivity.this, OrderDetailsActivity.class);
                startActivity(intentNaveOrders);
            } else if (itemId == R.id.sideNavAboutUs) {
                Intent intentNavAboutUs = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(intentNavAboutUs);
            } else if (itemId == R.id.sideNavLogout) {

                googleClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();

                        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("hasLoggedIn", false);
                        editor.apply();

                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });

//            googleClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    finish();
//                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                }
//            });

            } else {
            }

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }

            isBottomNavigationItemSelected = false;
        }

        return true;
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}