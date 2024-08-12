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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
import lk.zeamac.app.garmentmachineryadmin.adapter.CategoryAdapter;
import lk.zeamac.app.garmentmachineryadmin.adapter.ProductAdapter;
import lk.zeamac.app.garmentmachineryadmin.entity.CategoryEntity;
import lk.zeamac.app.garmentmachineryadmin.entity.ProductEntity;

public class CategoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    private ImageView imageButton;
    private FirebaseFirestore fireStore;
    private FirebaseStorage storage;
    private Uri imagePath;
    private ArrayList<CategoryEntity> categoryItems;
    EditText editTextName;
    private CategoryAdapter categoryAdapter;
    private boolean isImageSelected = false;
    AlertDialog.Builder builder;

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);


        fireStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        builder = new AlertDialog.Builder(getContext());

        categoryAdapter = new CategoryAdapter(categoryItems, getActivity());
        categoryAdapter.setOnCategoryDeleteListener(new CategoryAdapter.OnCategoryDeleteListener() {
            @Override
            public void onCategoryDelete(int position) {

                builder.setTitle("Alert")
                        .setMessage("Do you want to delete the category?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                deleteCategory(position);

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

        fragment.findViewById(R.id.categoryBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(getActivity(), MainActivity.class);
                startActivity(intentMain);
            }
        });


        //add Category
        imageButton = fragment.findViewById(R.id.categoryImageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResultLauncher.launch(Intent.createChooser(intent, "Select Image"));
            }
        });


        //Add New Record
        fragment.findViewById(R.id.addNewCategoryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextName = fragment.findViewById(R.id.categoryName);

                String name = editTextName.getText().toString();
                String imageId = UUID.randomUUID().toString();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getActivity(), "Please Enter Category Name", Toast.LENGTH_LONG).show();
                } else if (!isImageSelected) {
                    Toast.makeText(getActivity(), "Please Select Category Image", Toast.LENGTH_LONG).show();
                } else {

                    CategoryEntity category = new CategoryEntity(imageId, name, imageId);


                    ProgressDialog dialog = new ProgressDialog(getActivity());
                    dialog.setMessage("Adding new Category");
                    dialog.setCancelable(false);
                    dialog.show();

                    fireStore.collection("Categories").add(category).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            if (imagePath != null) {
                                dialog.setMessage("Uploading image...");
                                StorageReference reference = storage.getReference("category-images").child(imageId);
                                reference.putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        dialog.dismiss();
                                        categoryAdapter.notifyDataSetChanged();
                                        editTextName.setText("");
                                        isImageSelected = false;
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


        //View Category
        categoryItems = new ArrayList<>();
        RecyclerView itemView = fragment.findViewById(R.id.categoryView);
        categoryAdapter = new CategoryAdapter(categoryItems, getActivity());

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);

        itemView.setLayoutManager(gridLayoutManager);
        itemView.setAdapter(categoryAdapter);

        fireStore.collection("Categories").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
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


    //Delete Category
    private void deleteCategory(int position) {
        CategoryEntity categoryToDelete = categoryItems.get(position);

        // Delete the category from Firestore
        fireStore.collection("Categories").whereEqualTo("imagePath", categoryToDelete.getImagePath()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                        StorageReference reference = storage.getReference("category-images").child(categoryToDelete.getImagePath());
                        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Remove the item from the list and update the adapter
                                categoryItems.remove(position);
                                categoryAdapter.notifyItemRemoved(position);
                                Toast.makeText(getActivity().getApplicationContext(), "Category deleted successfully", Toast.LENGTH_LONG).show();
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