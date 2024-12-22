package com.example.manager_food.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manager_food.R;
import com.example.manager_food.model.OrderItem;

import java.util.List;

public class OrdersPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_NEW = 0;
    private static final int VIEW_TYPE_IN_PREPARATION = 1;
    private static final int VIEW_TYPE_IN_DELIVERY = 2;
    private static final int VIEW_TYPE_ON_DELIVERY = 3;
    private static final int VIEW_TYPE_CANCELLED = 4;
    private static final int VIEW_TYPE_COMPLETED = 5;

    private final Context context;
    private final List<OrderItem> newOrders;
    private final List<OrderItem> ongoingOrders;
    private final List<OrderItem> inDeliveryOrders;
    private final List<OrderItem> onDeliveryOrders;
    private final List<OrderItem> cancelledOrders;
    private final List<OrderItem> completedOrders;

    public OrdersPagerAdapter(Context context,
                              List<OrderItem> newOrders,
                              List<OrderItem> ongoingOrders,
                              List<OrderItem> inDeliveryOrders,
                              List<OrderItem> onDeliveryOrders,
                              List<OrderItem> cancelledOrders,
                              List<OrderItem> completedOrders) {
        this.context = context;
        this.newOrders = newOrders;
        this.ongoingOrders = ongoingOrders;
        this.inDeliveryOrders = inDeliveryOrders;
        this.onDeliveryOrders = onDeliveryOrders;
        this.cancelledOrders = cancelledOrders;
        this.completedOrders = completedOrders;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < newOrders.size()) {
            return VIEW_TYPE_NEW;
        } else if (position < newOrders.size() + ongoingOrders.size()) {
            return VIEW_TYPE_IN_PREPARATION;
        } else if (position < newOrders.size() + ongoingOrders.size() + inDeliveryOrders.size()) {
            return VIEW_TYPE_IN_DELIVERY;
        } else if (position < newOrders.size() + ongoingOrders.size() + inDeliveryOrders.size() + onDeliveryOrders.size()) {
            return VIEW_TYPE_ON_DELIVERY;
        } else if (position < newOrders.size() + ongoingOrders.size() + inDeliveryOrders.size() + onDeliveryOrders.size() + cancelledOrders.size()) {
            return VIEW_TYPE_CANCELLED;
        } else {
            return VIEW_TYPE_COMPLETED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case VIEW_TYPE_NEW:
                return new NewOrderViewHolder(inflater.inflate(R.layout.frag_cancelled_order, parent, false));
            case VIEW_TYPE_IN_PREPARATION:
                return new OngoingOrderViewHolder(inflater.inflate(R.layout.frag_cancelled_order, parent, false));
            case VIEW_TYPE_IN_DELIVERY:
                return new InDeliveryOrderViewHolder(inflater.inflate(R.layout.frag_cancelled_order, parent, false));
            case VIEW_TYPE_ON_DELIVERY:
                return new OnDeliveryOrderViewHolder(inflater.inflate(R.layout.frag_cancelled_order, parent, false));
            case VIEW_TYPE_CANCELLED:
                return new CancelledOrderViewHolder(inflater.inflate(R.layout.frag_cancelled_order, parent, false));
            case VIEW_TYPE_COMPLETED:
                return new CompletedOrderViewHolder(inflater.inflate(R.layout.frag_cancelled_order, parent, false));
            default:
                throw new IllegalStateException("Unexpected view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NewOrderViewHolder) {
            // Bind new order
        } else if (holder instanceof OngoingOrderViewHolder) {
            // Bind ongoing order
        } else if (holder instanceof InDeliveryOrderViewHolder) {
            // Bind in delivery order
        } else if (holder instanceof OnDeliveryOrderViewHolder) {
            // Bind on delivery order
        } else if (holder instanceof CancelledOrderViewHolder) {
            // Bind cancelled order
        } else if (holder instanceof CompletedOrderViewHolder) {
            // Bind completed order
        }
    }

    @Override
    public int getItemCount() {
        return newOrders.size() + ongoingOrders.size() + inDeliveryOrders.size() + onDeliveryOrders.size() + cancelledOrders.size() + completedOrders.size();
    }

    // ViewHolder classes for different order types
    static class NewOrderViewHolder extends RecyclerView.ViewHolder {
        TextView id_order, customerName, orderDate, orderTotal,status_order;

        public NewOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            id_order = itemView.findViewById(R.id.order_id_cancelled_order);
            customerName = itemView.findViewById(R.id.customer_name_cancelled_order);
            orderDate = itemView.findViewById(R.id.order_date_cancelled_order);
            orderTotal = itemView.findViewById(R.id.order_total_cancelled_order);
            status_order = itemView.findViewById(R.id.order_date_cancelled_order);
        }
    }

    static class OngoingOrderViewHolder extends RecyclerView.ViewHolder {
        TextView id_order, customerName, orderDate, orderTotal,status_order;

        public OngoingOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            id_order = itemView.findViewById(R.id.order_id_cancelled_order);
            customerName = itemView.findViewById(R.id.customer_name_cancelled_order);
            orderDate = itemView.findViewById(R.id.order_date_cancelled_order);
            orderTotal = itemView.findViewById(R.id.order_total_cancelled_order);
            status_order = itemView.findViewById(R.id.order_date_cancelled_order);
        }
    }

    static class InDeliveryOrderViewHolder extends RecyclerView.ViewHolder {
        TextView id_order, customerName, orderDate, orderTotal,status_order;

        public InDeliveryOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            id_order = itemView.findViewById(R.id.order_id_cancelled_order);
            customerName = itemView.findViewById(R.id.customer_name_cancelled_order);
            orderDate = itemView.findViewById(R.id.order_date_cancelled_order);
            orderTotal = itemView.findViewById(R.id.order_total_cancelled_order);
            status_order = itemView.findViewById(R.id.order_date_cancelled_order);
        }
    }

    static class OnDeliveryOrderViewHolder extends RecyclerView.ViewHolder {
        TextView id_order, customerName, orderDate, orderTotal,status_order;

        public OnDeliveryOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            id_order = itemView.findViewById(R.id.order_id_cancelled_order);
            customerName = itemView.findViewById(R.id.customer_name_cancelled_order);
            orderDate = itemView.findViewById(R.id.order_date_cancelled_order);
            orderTotal = itemView.findViewById(R.id.order_total_cancelled_order);
            status_order = itemView.findViewById(R.id.order_date_cancelled_order);
        }
    }

    static class CancelledOrderViewHolder extends RecyclerView.ViewHolder {
        TextView id_order, customerName, orderDate, orderTotal,status_order;

        public CancelledOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            id_order = itemView.findViewById(R.id.order_id_cancelled_order);
            customerName = itemView.findViewById(R.id.customer_name_cancelled_order);
            orderDate = itemView.findViewById(R.id.order_date_cancelled_order);
            orderTotal = itemView.findViewById(R.id.order_total_cancelled_order);
            status_order = itemView.findViewById(R.id.order_date_cancelled_order);
        }
    }

    static class CompletedOrderViewHolder extends RecyclerView.ViewHolder {
        TextView id_order, customerName, orderDate, orderTotal,status_order;

        public CompletedOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            id_order = itemView.findViewById(R.id.order_id_cancelled_order);
            customerName = itemView.findViewById(R.id.customer_name_cancelled_order);
            orderDate = itemView.findViewById(R.id.order_date_cancelled_order);
            orderTotal = itemView.findViewById(R.id.order_total_cancelled_order);
            status_order = itemView.findViewById(R.id.order_date_cancelled_order);
        }
    }
}
