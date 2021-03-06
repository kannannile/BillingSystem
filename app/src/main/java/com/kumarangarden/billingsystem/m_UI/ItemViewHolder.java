package com.kumarangarden.billingsystem.m_UI;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kumarangarden.billingsystem.R;
import com.kumarangarden.billingsystem.m_Model.Item;

/**
 * Created by kanna_000 on 10-08-2017.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder {

    private TextView name, price, quantity, id;

    public ItemViewHolder(View itemView) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.textName);
        price = (TextView) itemView.findViewById(R.id.textPrice);
        quantity = (TextView) itemView.findViewById(R.id.textQuantity);
        id = (TextView) itemView.findViewById(R.id.textID);
    }

    public String getName() {
        return name.getText().toString();
    }

    public void Initialize(Item item, int position)
    {
        name.setText((position +1) + ". " + item.Name);
        quantity.setText("₹: " + item.UnitPrice + " x " + item.Quantity);
        id.setText(item.GetID());
        price.setText("₹: " + item.GetNetPrice());
    }

}
