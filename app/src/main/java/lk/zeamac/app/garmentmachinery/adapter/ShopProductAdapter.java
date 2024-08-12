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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.zeamac.app.garmentmachinery.R;
import lk.zeamac.app.garmentmachinery.entity.ProductEntity;
import lk.zeamac.app.garmentmachinery.entity.ShopProductEntity;

public class ShopProductAdapter extends RecyclerView.Adapter<ShopProductAdapter.Viewholder> {

    private static ArrayList<ShopProductEntity> items;
    private FirebaseStorage storage;
    private Context context;
    OnProductClickListener onProductClickListener;


    public ShopProductAdapter(ArrayList<ShopProductEntity> items, Context context, OnProductClickListener productClickListener) {
        this.items = items;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
        this.onProductClickListener = productClickListener;
    }

    public void setSearchList(ArrayList<ShopProductEntity> searchList) {
        this.items = searchList;
        notifyDataSetChanged();
    }

    public interface OnProductClickListener {
        void OnProductClick(String product);
    }


    @NonNull
    @Override
    public ShopProductAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_shop_product, parent, false);

        return new Viewholder(inflate, onProductClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopProductAdapter.Viewholder holder, int position) {
        ShopProductEntity item = items.get(position);
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice());


        storage.getReference("product-images/"+item.getImage())
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

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView name,price,rentNow;
        ImageView image;

        public Viewholder(@NonNull View itemView, final OnProductClickListener clickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.titleTextShop);
            price = itemView.findViewById(R.id.shopPrice);
            image = itemView.findViewById(R.id.imgShop);

            rentNow = itemView.findViewById(R.id.addToCart);
            rentNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.OnProductClick(items.get(position).getId());

                    }
                }
            });
        }
    }
}
