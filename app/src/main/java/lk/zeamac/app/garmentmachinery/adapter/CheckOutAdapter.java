package lk.zeamac.app.garmentmachinery.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import lk.zeamac.app.garmentmachinery.R;
import lk.zeamac.app.garmentmachinery.entity.CartEntity;

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.Viewholder> {
    private static ArrayList<CartEntity> items;
    private FirebaseStorage storage;
    private static Context context;
    private static FirebaseFirestore fireStore;

    public CheckOutAdapter(ArrayList<CartEntity> item , Context context ){
        this.items = item;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CheckOutAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_checkout, parent, false);


        return new CheckOutAdapter.Viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckOutAdapter.Viewholder holder, int position) {
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

                            holder.title.setText(data.get("name").toString());


                        } else {
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        holder.price.setText(item.getCartProductFixedPrice().toString());

        holder.qty.setText(item.getQty().toString());


        storage.getReference("product-images/" + item.getId())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .into(holder.img);
                    }
                });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView title, price,qty;
        ImageView img;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgTextCheckOut);
            title = itemView.findViewById(R.id.titleTextCheckout);
            price= itemView.findViewById(R.id.totalCheckoutPrice);
            qty= itemView.findViewById(R.id.qtyCheckout);
        }
    }
}
