package lk.zeamac.app.garmentmachineryadmin.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lk.zeamac.app.garmentmachineryadmin.R;
import lk.zeamac.app.garmentmachineryadmin.activity.MainActivity;
import lk.zeamac.app.garmentmachineryadmin.adapter.ProductAdapter;
import lk.zeamac.app.garmentmachineryadmin.entity.ProductEntity;

public class ProductFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    private SearchView searchView;
    private ImageView imageButton;
    private FirebaseFirestore fireStore;
    private FirebaseStorage storage;
    private Uri imagePath;
    private ArrayList<ProductEntity> productItems;
    private ArrayList<String> categoryArray;
    private ArrayAdapter<String> adapter;
    private ProductAdapter productAdapter;
    EditText editTextName, editTextDes, editTextPrice, editTextQty;
    private QuerySnapshot categorySnap;
    private boolean isImageSelected = false;
    private String selectedCategory;
    AlertDialog.Builder builder;

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);

        fireStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        builder = new AlertDialog.Builder(getContext());

        productAdapter = new ProductAdapter(productItems, getActivity());
        productAdapter.setOnProductDeleteListener(new ProductAdapter.OnProductDeleteListener() {
            @Override
            public void onProductDelete(int position) {

                builder.setTitle("Alert")
                        .setMessage("Do you want to delete the product?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                deleteProduct(position);

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

        fragment.findViewById(R.id.productBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(getActivity(), MainActivity.class);
                startActivity(intentMain);
            }
        });


        searchView = fragment.findViewById(R.id.productSearchView);
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


        // CategorySpinner
        categoryArray = new ArrayList<>();
        Spinner categorySpinner = fragment.findViewById(R.id.productCategorySpinner);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categoryArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getCategoryData();

        //Add Product
        imageButton = fragment.findViewById(R.id.productImageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResultLauncher.launch(Intent.createChooser(intent, "Select Image"));
            }
        });

        fragment.findViewById(R.id.addNewProductBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextName = fragment.findViewById(R.id.productName);
                editTextDes = fragment.findViewById(R.id.productDescription);
                editTextPrice = fragment.findViewById(R.id.productPrice);
                editTextQty = fragment.findViewById(R.id.productQty);

                String name = editTextName.getText().toString();
                String description = editTextDes.getText().toString();
                String price = editTextPrice.getText().toString();
                String qty = editTextQty.getText().toString();


                if (!isImageSelected) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please Select Product Image", Toast.LENGTH_LONG).show();
                } else if (name.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please Enter Product Name", Toast.LENGTH_LONG).show();
                } else if (selectedCategory.equals("Select")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please Select Category", Toast.LENGTH_LONG).show();
                } else if (description.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please Enter Description", Toast.LENGTH_LONG).show();
                } else if (price.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please Enter Product Price", Toast.LENGTH_LONG).show();
                } else if (qty.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please Enter Product Quantity", Toast.LENGTH_LONG).show();
                } else {

                    String imageId = UUID.randomUUID().toString();

                    ProductEntity product = new ProductEntity(imageId, name, selectedCategory, description, price, qty, imageId);

                    ProgressDialog dialog = new ProgressDialog(getActivity());
                    dialog.setMessage("Adding new Product");
                    dialog.setCancelable(false);
                    dialog.show();

                    fireStore.collection("Products").add(product).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            if (imagePath != null) {
                                dialog.setMessage("Uploading image...");
                                StorageReference reference = storage.getReference("product-images").child(imageId);
                                reference.putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        dialog.dismiss();
                                        productAdapter.notifyDataSetChanged();
                                        categorySpinner.setSelection(0);
                                        editTextName.setText("");
                                        editTextDes.setText("");
                                        editTextPrice.setText("");
                                        editTextQty.setText("");
                                        int drawableResourceId = R.drawable.splash_app_icon_transparent;
                                        imageButton.setImageResource(drawableResourceId);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                        dialog.setMessage("Uploading " + (int) progress + "%");

                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            dialog.dismiss();
                            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }
        });


        //View Product
        productItems = new ArrayList<>();
        RecyclerView itemView = fragment.findViewById(R.id.productView);
        productAdapter = new ProductAdapter(productItems, getActivity());

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);

        itemView.setLayoutManager(gridLayoutManager);
        itemView.setAdapter(productAdapter);

        fireStore.collection("Products").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (DocumentChange change : value.getDocumentChanges()) {
                    ProductEntity product = change.getDocument().toObject(ProductEntity.class);
                    switch (change.getType()) {
                        case ADDED:
                            productItems.add(product);
                            break;
                        case MODIFIED:
                            ProductEntity old = productItems.stream().filter(i -> i.getId().equals(product.getId())).findFirst().orElse(null);

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
                            break;
                    }
                }

                productAdapter.notifyDataSetChanged();
            }
        });

    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                imagePath = result.getData().getData();
                Picasso.get().load(imagePath).into(imageButton);
                isImageSelected = true;
            }
        }
    });

    private void getCategoryData() {

        fireStore.collection("Categories").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                categorySnap = queryDocumentSnapshots;
                if (queryDocumentSnapshots.size() > 0) {
                    categoryArray.clear();
                    categoryArray.add("Select");
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        categoryArray.add(doc.getString("name"));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "No Data", Toast.LENGTH_LONG).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void searchList(String text) {
        ArrayList<ProductEntity> itemsList = new ArrayList<>();
        for (ProductEntity product : productItems) {
            if (product.getName().toLowerCase().contains(text.toLowerCase())) {
                itemsList.add(product);
            }
        }
        if (itemsList.isEmpty()) {
            Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
        } else {
            productAdapter.setFilterSearchList(itemsList);
        }
    }

    //Delete Product
    private void deleteProduct(int position) {
        ProductEntity productToDelete = productItems.get(position);

        // Delete the product from Firestore
        fireStore.collection("Products").whereEqualTo("image", productToDelete.getImage()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = fireStore.batch();
                List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot snapshot : snapshots) {
                    batch.delete(snapshot.getReference());
                }
                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Delete the corresponding image from Firebase Storage
                        StorageReference reference = storage.getReference("product-images").child(productToDelete.getImage());
                        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Remove the item from the list and update the adapter
                                productItems.remove(position);
                                productAdapter.notifyItemRemoved(position);
                                Toast.makeText(getActivity().getApplicationContext(), "Product deleted successfully", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity().getApplicationContext(), "Failed to delete image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), "Failed to delete product: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


}