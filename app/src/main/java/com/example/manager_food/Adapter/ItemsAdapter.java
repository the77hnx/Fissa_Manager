package com.example.manager_food.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.manager_food.R;
import com.example.manager_food.model.Category;
import com.example.manager_food.model.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    private List<Item> itemList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ItemsAdapter(List<Item> itemList, Context context) {
        this.itemList = new ArrayList<>(itemList);
        this.context = context;
    }

    public void setItems(List<Item> itemList) {
        this.itemList.clear();
        this.itemList.addAll(itemList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_popup_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);

        Log.d("Adapter", "Binding category: " + item.getName());
        Log.d("Adapter", "Binding category: " + item.getDescription());
        Log.d("Adapter", "Binding category: " + item.getCategory());
        Log.d("Adapter", "Binding category: " + item.getPrice());
        Log.d("Adapter", "Binding category: " + item.getId());

        holder.nameTextView.setText(item.getName());
        holder.priceTextView.setText(String.format("السعر : %s دج", item.getPrice()));
        holder.descriptionTextView.setText(item.getDescription());

        // Load image using Glide (or Picasso)
        String baseUrl = "https://www.fissadelivery.com/fissa/";
        String fullImagePath = baseUrl + item.getImagePath().replace("../", "");

        Glide.with(context)
                .load(fullImagePath)
                .into(holder.productImageView);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item);
            }
        });

        holder.editButton.setOnClickListener(v -> {
            Toast.makeText(context, "هاته الاضافة غير متوفرة حاليا يرجي حذف المنتج واعادة ادخاله", Toast.LENGTH_SHORT).show();
            if (onItemClickListener != null) {
                onItemClickListener.onEditClick(item);
            }
        });

        holder.removeButton.setOnClickListener(v -> {
            // Remove from database (you need to implement this part with an API call)
//            deleteProduct(item.getId(), position);
            // Remove item from the list and update RecyclerView
            itemList.remove(position);
            notifyItemRemoved(position);
            if (onItemClickListener != null) {
                onItemClickListener.onRemoveClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    private void deleteProduct(int productId, int position) {
        OkHttpClient client = new OkHttpClient();

        // Create the request body with product ID
        String postData = "id_prod=" + productId;
        RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), postData);

        // Build the POST request
        Request request = new Request.Builder()
                .url("https://www.fissadelivery.com/fissa/Manager/Delete_Product.php")
                .post(body)
                .build();

        // Make the request asynchronously
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                // Handle failure, e.g., no internet connection or server error
                e.printStackTrace();
                Toast.makeText(context, "Error deleting product", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Show success message when the product is deleted
                    String responseBody = response.body().string();
                    if (responseBody.contains("Product deleted successfully")) {
                        // Remove item from list and notify adapter
                        itemList.remove(position);
                        ((Activity) context).runOnUiThread(() -> {
                            notifyItemRemoved(position);
                            Toast.makeText(context, "تم حذف المنتج بنجاح", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        // If the response does not indicate success
                        Toast.makeText(context, "فشل حذف المنتج", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // If the response is not successful
                    Toast.makeText(context, "فشل حذف المنتج", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public interface OnItemClickListener {
        void onItemClick(Item item);
        void onEditClick(Item item);
        void onRemoveClick(Item item);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView priceTextView;
        private TextView descriptionTextView;
        private Button editButton;
        private Button removeButton;
        private  ImageView productImageView;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productNameTextView_frag);
            priceTextView = itemView.findViewById(R.id.productPriceTextView_frag);
            descriptionTextView = itemView.findViewById(R.id.item_description_popup);
            productImageView = itemView.findViewById(R.id.product_image);
            editButton = itemView.findViewById(R.id.editproduct);
            removeButton = itemView.findViewById(R.id.removeproduct);
        }
    }
}
