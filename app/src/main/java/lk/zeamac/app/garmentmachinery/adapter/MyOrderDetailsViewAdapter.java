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

import lk.zeamac.app.garmentmachinery.entity.MyOrderDetailsEntity;
import lk.zeamac.app.garmentmachinery.R;

public class MyOrderDetailsViewAdapter extends RecyclerView.Adapter<MyOrderDetailsViewAdapter.Viewholder> {
    ArrayList<MyOrderDetailsEntity> items;
    Context context;

    public MyOrderDetailsViewAdapter(ArrayList<MyOrderDetailsEntity> item) {
        this.items = item;
    }

    @NonNull
    @Override
    public MyOrderDetailsViewAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_order_details, parent, false);

        return new MyOrderDetailsViewAdapter.Viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderDetailsViewAdapter.Viewholder holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        holder.price.setText(items.get(position).getPrice());
        holder.status.setText(items.get(position).getStatus());
        holder.id.setText(items.get(position).getId());
        holder.date.setText(items.get(position).getDate());
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
        TextView title, price, status,id,date;
        ImageView img;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgOrderList);
            title = itemView.findViewById(R.id.titleTextCart);
            price= itemView.findViewById(R.id.order_list_price);
            status = itemView.findViewById(R.id.status);
            id = itemView.findViewById(R.id.order_list_id);
            date = itemView.findViewById(R.id.order_list_date);
        }
    }
}
