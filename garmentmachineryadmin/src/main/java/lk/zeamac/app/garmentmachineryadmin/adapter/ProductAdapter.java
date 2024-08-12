package lk.zeamac.app.garmentmachineryadmin.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.zeamac.app.garmentmachineryadmin.entity.ProductEntity;
import lk.zeamac.app.garmentmachineryadmin.R;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private ArrayList<ProductEntity> items;
    private FirebaseStorage storage;
    private Context context;
    private static OnProductDeleteListener deleteListener;

    public ProductAdapter(ArrayList<ProductEntity> items, Context context) {
        this.items = items;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }

    public interface OnProductDeleteListener {
        void onProductDelete(int position);
    }

    public void setOnProductDeleteListener(OnProductDeleteListener listener) {
        this.deleteListener = listener;
    }

    public void setFilterSearchList(ArrayList<ProductEntity> filterList) {
        this.items = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_holder_shop_product, parent, false);
        return new ProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        ProductEntity item = items.get(position);
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice());
        holder.qty.setText(item.getQty());


        storage.getReference("product-images/" + item.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, qty;
        ImageView image;
        LinearLayout cardCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.titleTextShop);
            price = itemView.findViewById(R.id.shopPrice);
            qty = itemView.findViewById(R.id.shopQty);
            image = itemView.findViewById(R.id.imgShop);

            cardCancel = itemView.findViewById(R.id.cardCancel);

            // delete action
            cardCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteListener != null) {
                        deleteListener.onProductDelete(getAdapterPosition());
                    }
                }
            });
        }
    }
}
