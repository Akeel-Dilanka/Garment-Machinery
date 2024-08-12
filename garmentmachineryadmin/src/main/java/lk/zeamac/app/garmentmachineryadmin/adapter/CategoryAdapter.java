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

import lk.zeamac.app.garmentmachineryadmin.entity.CategoryEntity;
import lk.zeamac.app.garmentmachineryadmin.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private ArrayList<CategoryEntity> items;
    private FirebaseStorage storage;
    private Context context;
    private static CategoryAdapter.OnCategoryDeleteListener deleteListener;

    public CategoryAdapter(ArrayList<CategoryEntity> items, Context context) {
        this.items = items;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }

    public interface OnCategoryDeleteListener {
        void onCategoryDelete(int position);
    }

    public void setOnCategoryDeleteListener(CategoryAdapter.OnCategoryDeleteListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_holder_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
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
        LinearLayout cateCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.categoryTitle);
            image = itemView.findViewById(R.id.categoryImage);

            cateCancel = itemView.findViewById(R.id.cateCancel);

            // delete action
            cateCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteListener != null) {
                        deleteListener.onCategoryDelete(getAdapterPosition());
                    }
                }
            });
        }
    }
}
