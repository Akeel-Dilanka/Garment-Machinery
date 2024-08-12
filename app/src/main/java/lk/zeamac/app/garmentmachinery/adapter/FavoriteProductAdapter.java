package lk.zeamac.app.garmentmachinery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import lk.zeamac.app.garmentmachinery.R;
import lk.zeamac.app.garmentmachinery.entity.FavoriteProductEntity;
import lk.zeamac.app.garmentmachinery.entity.ShopProductEntity;

public class FavoriteProductAdapter extends RecyclerView.Adapter<FavoriteProductAdapter.Viewholder> {
    ArrayList<FavoriteProductEntity> items;
    Context context;

    public FavoriteProductAdapter(ArrayList<FavoriteProductEntity> item) {
        this.items = item;
    }

    @NonNull
    @Override
    public FavoriteProductAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_favorite_product, parent, false);

        return new Viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteProductAdapter.Viewholder holder, int position) {
        holder.titleText.setText(items.get(position).getTitle());
        holder.priceText.setText(items.get(position).getPrice());
        int drawableResourcesId = holder.itemView.getResources()
                .getIdentifier(items.get(position).getImgPath(), "drawable", holder.itemView.getContext().getPackageName());

        Glide.with(context)
                .load(drawableResourcesId)
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView titleText, priceText;
        ImageView img;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleTextFavorite);
            priceText = itemView.findViewById(R.id.favoritePrice);
            img = itemView.findViewById(R.id.imgFavoriteCard);
        }
    }
}
