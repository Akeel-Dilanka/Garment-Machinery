package lk.zeamac.app.garmentmachinery.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lk.zeamac.app.garmentmachinery.R;
import lk.zeamac.app.garmentmachinery.entity.CartEntity;

public class CartViewAdapter extends RecyclerView.Adapter<CartViewAdapter.Viewholder> {
    Context context;
    private ArrayList<CartEntity> items;
    private FirebaseStorage storage;
    private FirebaseFirestore fireStore;
    private static OnProductClickListener onProductClickRemoveListener;

    public CartViewAdapter(ArrayList<CartEntity> item, Context context) {
        this.items = item;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }

    public interface OnProductClickListener {
        void OnProductClick(int position);
    }

    public void setOnProductClickRemoveListener(OnProductClickListener clickListener){
        this.onProductClickRemoveListener = clickListener;
    }

    @NonNull
    @Override
    public CartViewAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_cart, parent, false);

        return new Viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewAdapter.Viewholder holder, int position) {

        fireStore = FirebaseFirestore.getInstance();

        CartEntity item = items.get(position);


        CollectionReference cartRef = fireStore.collection("Products");
        Query query = cartRef.whereEqualTo("id", item.getId());
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            Map<String, Object> data = documentSnapshot.getData();

                            holder.name.setText(data.get("name").toString());
                            holder.availableQty.setText(data.get("qty").toString());


                            holder.allQuantity = data.get("qty").toString();

                            holder.holderQty = Long.valueOf(holder.allQuantity);


                            holder.count = item.getQty();
                            holder.fixedPrice = item.getCartProductFixedPrice();
                            holder.totalPrice = holder.count * holder.fixedPrice;
                            holder.tp = String.valueOf(holder.totalPrice);
                            holder.changePrice.setText(holder.tp);


                            holder.increaseQty.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (holder.holderQty > holder.count) {
                                        holder.count++;
                                        holder.updateQty.setText("" + holder.count);

                                        holder.totalPrice = holder.count * holder.fixedPrice;
                                        holder.tp = String.valueOf(holder.totalPrice);
                                        holder.changePrice.setText(holder.tp);

                                        holder.UpdateQty(holder.count, item.getId());




                                    } else {
                                    }
                                }
                            });

                            holder.decreaseQty.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (holder.count <= 1) {
                                        holder.count = 1L;

                                    } else {
                                        holder.count--;
                                        holder.updateQty.setText("" + holder.count);
                                        holder.totalPrice = holder.count * holder.fixedPrice;
                                        holder.tp = String.valueOf(holder.totalPrice);
                                        holder.changePrice.setText(holder.tp);

                                        holder.UpdateQty(holder.count, item.getId());



                                    }
                                }
                            });


                        } else {
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        holder.price.setText(item.getCartProductFixedPrice().toString());
        holder.qtyText.setText(item.getQty().toString());
        storage.getReference("product-images/" + item.getId())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .into(holder.image);
                    }
                });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView titleText, priceText, qtyText, totalText;
        ImageView img;
        EditText updateQty;
        TextView name, price, qty, decreaseQty, increaseQty, availableQty, changePrice;
        ImageView image, removeProduct;
        Long count, holderQty, fixedPrice, totalPrice;
        String allQuantity, tp;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            qtyText = itemView.findViewById(R.id.qtyTextCart);

            name = itemView.findViewById(R.id.titleTextCart);
            price = itemView.findViewById(R.id.priceTextCart3);
            image = itemView.findViewById(R.id.imgTextCart);
            changePrice = itemView.findViewById(R.id.totalTextCart);

            updateQty = itemView.findViewById(R.id.qtyTextCart);
            decreaseQty = itemView.findViewById(R.id.textView6);
            increaseQty = itemView.findViewById(R.id.textView4);
            availableQty = itemView.findViewById(R.id.priceTextCart2);

            removeProduct = itemView.findViewById(R.id.textView1CartDeleteItem);
            removeProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onProductClickRemoveListener != null) {
                        onProductClickRemoveListener.OnProductClick(getAdapterPosition());
                    }
                }
            });
        }

        private void UpdateQty(Long count, String id) {

            DocumentReference reference = fireStore.collection("Cart").document(id);
            reference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot productDoc = task.getResult();
                    if (productDoc.exists()) {
                        Toast.makeText(context, "Quantity Updated !", Toast.LENGTH_SHORT).show();
                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("qty", count);
                        reference.update(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }
            });
        }

    }

}
