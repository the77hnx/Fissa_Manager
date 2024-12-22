package com.example.manager_food.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manager_food.R;
import com.example.manager_food.model.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class CompletedOrdersAdapter extends RecyclerView.Adapter<CompletedOrdersAdapter.CompletedOrderViewHolder> {

    private final Context context;
    private List<OrderItem> orderList;
    private final OnOrderClickListener onOrderClickListener;

    // Define a constant for the "Completed" status
    private static final String COMPLETED_STATUS = "Completed";
    private static final int COMPLETED_STATUS_ID = 6; // Assuming 6 corresponds to "Completed"

    public CompletedOrdersAdapter(Context context, List<OrderItem> orderList, OnOrderClickListener onOrderClickListener) {
        this.context = context;
        this.onOrderClickListener = onOrderClickListener;
        this.orderList = new ArrayList<>();
        updateOrderList(orderList); // Initialize the order list with filtered orders
    }

    @NonNull
    @Override
    public CompletedOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.frag_cancelled_order, parent, false);
        return new CompletedOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedOrderViewHolder holder, int position) {
        OrderItem order = orderList.get(position);
        holder.bind(order);
        holder.itemView.setOnClickListener(v -> onOrderClickListener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    // Method to update the list of orders and refresh the RecyclerView
    public void updateOrderList(List<OrderItem> newOrderList) {
        List<OrderItem> filteredOrders = new ArrayList<>();
        for (OrderItem order : newOrderList) {
            // Filter based on status or status ID
            if (COMPLETED_STATUS.equals(order.getOrderStatus()) || order.getIdStatutCommande() == COMPLETED_STATUS_ID) {
                filteredOrders.add(order);
            }
        }
        // Replace the current orderList with the filtered list
        orderList.clear();
        orderList.addAll(filteredOrders);
        notifyDataSetChanged();
    }

    public interface OnOrderClickListener {
        void onOrderClick(OrderItem order);
    }

    static class CompletedOrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView customerName;
        private final TextView orderDate;
        private final TextView orderId;
        private final TextView orderTotal;
        private final TextView orderMessage;
        private final TextView orderStatus;
        private final RecyclerView itemsRecyclerView; // RecyclerView for order items

        public CompletedOrderViewHolder(View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.customer_name_cancelled_order);
            orderDate = itemView.findViewById(R.id.order_date_cancelled_order);
            orderId = itemView.findViewById(R.id.order_id_cancelled_order);
            orderStatus = itemView.findViewById(R.id.order_Status_tv_cancelled_oder);
            orderTotal = itemView.findViewById(R.id.order_total_cancelled_order);
            orderMessage = itemView.findViewById(R.id.order_message_cancelled_order);
            itemsRecyclerView = itemView.findViewById(R.id.recycler_view_cancelled_items); // Initialize RecyclerView for items
        }

        public void bind(OrderItem order) {
            customerName.setText("اسم الزبون : " + order.getCustomerName());
            orderDate.setText("تاريخ الطلب : " + order.getOrderDate());
            orderId.setText("ايدي الطلب : " + order.getOrderId());
            orderTotal.setText("السعر الاجمالي : " + order.getOrderTotal() + " دج");
            orderMessage.setText(order.getOrderMessage());
            orderStatus.setText("حالة الطلب : " + order.getOrderStatus());

            OrderItemsAdapter itemsAdapter = new OrderItemsAdapter(order.getItems());
            itemsRecyclerView.setAdapter(itemsAdapter);
            itemsRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}
