package com.example.manager_food.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manager_food.R;
import com.example.manager_food.model.OrderItem;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<OrderItem> orders;
    private OnOrderClickListener onOrderClickListener;

    public interface OnOrderClickListener {
        void onOrderClick(OrderItem order);
    }

    public OrderAdapter(List<OrderItem> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.onOrderClickListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_cancelled_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderItem order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView customerName;
        TextView orderDate;
        TextView orderTotal;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.customer_name_cancelled_order);
            orderDate = itemView.findViewById(R.id.order_date_cancelled_order);
            orderTotal = itemView.findViewById(R.id.order_total_cancelled_order);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onOrderClickListener.onOrderClick(orders.get(position));
                }
            });
        }

        public void bind(OrderItem order) {
            customerName.setText(order.getCustomerName());
            orderDate.setText(order.getOrderDate());
            orderTotal.setText(String.valueOf(order.getOrderTotal()));
        }
    }
}
