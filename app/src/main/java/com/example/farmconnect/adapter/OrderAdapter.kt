package com.example.farmconnect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.farmconnect.R
import com.example.farmconnect.model.Order

class OrderAdapter(
    private val orders: MutableList<Order>,
    private val onAcceptClick: (Order) -> Unit,
    private val onRejectClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount() = orders.size

    fun updateOrders(newOrders: List<Order>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvOrderNumber: TextView = itemView.findViewById(R.id.tvOrderNumber)
        private val tvBuyerName: TextView = itemView.findViewById(R.id.tvBuyerName)
        private val tvOrderType: TextView = itemView.findViewById(R.id.tvOrderType)
        private val tvTotalAmount: TextView = itemView.findViewById(R.id.tvTotalAmount)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvItemCount: TextView = itemView.findViewById(R.id.tvItemCount)
        private val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        private val btnReject: Button = itemView.findViewById(R.id.btnReject)

        fun bind(order: Order) {
            tvOrderNumber.text = "Order #${order.id.take(8)}"
            tvBuyerName.text = order.buyerName
            tvOrderType.text = order.orderType.name
            tvTotalAmount.text = "$${String.format("%.2f", order.total)}"
            tvStatus.text = order.status.name
            tvItemCount.text = "${order.items.size} items"

            // Show/hide buttons based on status
            if (order.status == Order.OrderStatus.PENDING) {
                btnAccept.visibility = View.VISIBLE
                btnReject.visibility = View.VISIBLE
                btnAccept.setOnClickListener { onAcceptClick(order) }
                btnReject.setOnClickListener { onRejectClick(order) }
            } else {
                btnAccept.visibility = View.GONE
                btnReject.visibility = View.GONE
            }
        }
    }
}




