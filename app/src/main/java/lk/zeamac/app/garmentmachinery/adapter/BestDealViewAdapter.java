package lk.zeamac.app.garmentmachinery.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import lk.zeamac.app.garmentmachinery.R;
import lk.zeamac.app.garmentmachinery.activity.SingleProductViewActivity;
import lk.zeamac.app.garmentmachinery.entity.BestDealEntity;
import lk.zeamac.app.garmentmachinery.fragment.ShopFragment;

public class BestDealViewAdapter extends RecyclerView.Adapter<BestDealViewAdapter.Viewholder> {
    ArrayList<BestDealEntity> items;
    Context context;
    private FragmentManager fragmentManager;

    public BestDealViewAdapter(ArrayList<BestDealEntity> item, FragmentManager fragmentManager) {
        this.items = item;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public BestDealViewAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_best_deal, parent, false);

        return new Viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull BestDealViewAdapter.Viewholder holder, int position) {
        holder.titleText.setText(items.get(position).getTitle());
        holder.priceText.setText(items.get(position).getPrice());
        int drawableResourcesId = holder.itemView.getResources()
                .getIdentifier(items.get(position).getImgPath(), "drawable", holder.itemView.getContext().getPackageName());

        Glide.with(context)
                .load(drawableResourcesId)
                .into(holder.img);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new ShopFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

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
            titleText = itemView.findViewById(R.id.titleTextDeal);
            priceText = itemView.findViewById(R.id.dealPrice);
            img = itemView.findViewById(R.id.imgBestDeal);
        }
    }
}
