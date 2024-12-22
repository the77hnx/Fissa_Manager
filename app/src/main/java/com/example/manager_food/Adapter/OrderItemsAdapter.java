package com.example.manager_food.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manager_food.R;
import com.example.manager_food.model.OrderItems;

import java.util.List;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.OrderItemsViewHolder> {

    private final List<OrderItems> orderItemsList;

    public OrderItemsAdapter(List<OrderItems> orderItemsList) {
        this.orderItemsList = orderItemsList;
    }

    @NonNull
    @Override
    public OrderItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.order_item_view, parent, false);
        return new OrderItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemsViewHolder holder, int position) {
        OrderItems orderItems = orderItemsList.get(position);
        holder.bind(orderItems);
    }

    @Override
    public int getItemCount() {
        return orderItemsList.size();
    }

    static class OrderItemsViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemName;
        private final TextView itemQuantity;
        private final TextView itemPrice;

        public OrderItemsViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name_order);
            itemQuantity = itemView.findViewById(R.id.item_quantity_order);
            itemPrice = itemView.findViewById(R.id.item_price_order);
        }

        public void bind(OrderItems orderItems) {
            itemName.setText(orderItems.getItemName());
            String quantity = String.valueOf(orderItems.getItemQuantity());
            itemQuantity.setText(quantity);
            itemPrice.setText(String.format("%.2f", orderItems.getItemPrice())); // Format double to string
        }
    }
}
