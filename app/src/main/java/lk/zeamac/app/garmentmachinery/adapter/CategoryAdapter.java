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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.zeamac.app.garmentmachinery.entity.CategoryEntity;
import lk.zeamac.app.garmentmachinery.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private static ArrayList<CategoryEntity> items;
    private FirebaseStorage storage;
    private Context context;
    OnCategoryClickListener onCategoryClickListener;

    public CategoryAdapter(ArrayList<CategoryEntity> items, Context context, OnCategoryClickListener listener) {
        this.items = items;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
        this.onCategoryClickListener = listener;
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(String categoryType, String categoryName);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_holder_category, parent, false);
        return new ViewHolder(view, onCategoryClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryEntity item = items.get(position);
        holder.textName.setText(item.getName());

        storage.getReference("category-images/" + item.getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
        TextView textName;
        ImageView image;

        public ViewHolder(@NonNull View itemView, final OnCategoryClickListener listener) {
            super(itemView);
            textName = itemView.findViewById(R.id.categoryTitle);
            image = itemView.findViewById(R.id.categoryImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCategoryClick(items.get(position).getName(), items.get(position).getImagePath());
                    }
                }
            });

        }
    }
}
