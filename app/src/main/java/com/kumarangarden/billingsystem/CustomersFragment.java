package com.kumarangarden.billingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kumarangarden.billingsystem.m_FireBase.FirebaseHelper;
import com.kumarangarden.billingsystem.m_Model.Customer;
import com.kumarangarden.billingsystem.m_Model.Product;
import com.kumarangarden.billingsystem.m_UI.CustomerDialog;
import com.kumarangarden.billingsystem.m_UI.CustomerViewHolder;
import com.kumarangarden.billingsystem.m_UI.ProductDialog;
import com.kumarangarden.billingsystem.m_UI.ProductViewHolder;

/**
 * Created by kanna_000 on 09-08-2017.
 */

public class CustomersFragment extends Fragment {

    CustomerDialog newCustomer;
    DatabaseReference db;
    FirebaseHelper helper;
    RecyclerView customersView;
    FirebaseRecyclerAdapter<Customer, CustomerViewHolder> firebaseRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_customers, container, false);

        db = FirebaseDatabase.getInstance().getReference();
        db.keepSynced(true);

        helper = new FirebaseHelper(db);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Customer, CustomerViewHolder>(Customer.class,
                R.layout.customercard, CustomerViewHolder.class, db.child("Customers").orderByPriority()) {
            @Override
            protected void populateViewHolder(CustomerViewHolder holder, final Customer customer, final int position) {
                DatabaseReference databaseReference = firebaseRecyclerAdapter.getRef(position);
                customer.SetName(databaseReference.getKey());
                holder.Initialize(customer);
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        newCustomer.setCustomer(customer);
                        newCustomer.show();
                        return false;
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), CustomerActivity.class);
                        intent.putExtra("Name", customer.GetName());
                        startActivity(intent);
                    }
                });
            }
        };

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        customersView = (RecyclerView) view.findViewById(R.id.customersView);
        customersView.setLayoutManager(mLayoutManager);
        customersView.setAdapter(firebaseRecyclerAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(view.getContext(), "on Move", Toast.LENGTH_SHORT).show();

                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                CustomerViewHolder holder = (CustomerViewHolder) viewHolder;
                if(holder.getName().matches("பொது"))
                    return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                CustomerViewHolder holder = (CustomerViewHolder) viewHolder;
                if(holder.getName().matches("பொது"))
                    return;
                Toast.makeText(view.getContext(), holder.getName() + " Removed", Toast.LENGTH_SHORT).show();

                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                DatabaseReference dbRef = firebaseRecyclerAdapter.getRef(position);
                dbRef.removeValue();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(customersView);

        newCustomer = new CustomerDialog(getContext());
        newCustomer.setTitle("Customer");
        newCustomer.setContentView(R.layout.customerform);
        newCustomer.InitControls();

        return view;
    }

    void AddCustomer(View view)
    {
        newCustomer.show();
    }



    void SaveCustomer(View view)
    {
        String toastMessage = newCustomer.getIsValid();

        if(toastMessage.matches(""))    //no error
        {
            Customer customer = newCustomer.getCustomer();
            if(!helper.save(customer))
                toastMessage ="Failed Saving";
            newCustomer.clear();
            toastMessage = customer.GetName() + " Added";
        }

        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
    }

}
