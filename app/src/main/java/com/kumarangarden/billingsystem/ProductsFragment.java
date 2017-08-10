package com.kumarangarden.billingsystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.kumarangarden.billingsystem.m_Model.Product;
import com.kumarangarden.billingsystem.m_UI.ProductDialog;
import com.kumarangarden.billingsystem.m_UI.ProductViewHolder;

/**
 * Created by kanna_000 on 09-08-2017.
 */

public class ProductsFragment extends Fragment {

    ProductDialog newProduct;
    DatabaseReference db;
    FirebaseHelper helper;
    RecyclerView productsView;
    FirebaseRecyclerAdapter<Product, ProductViewHolder> firebaseRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_products, container, false);

        db = FirebaseDatabase.getInstance().getReference();
        db.keepSynced(true);

        helper = new FirebaseHelper(db);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(Product.class,
                R.layout.productcard, ProductViewHolder.class, db.child("Products").getRef()) {
            @Override
            protected void populateViewHolder(ProductViewHolder holder, final Product product, final int position) {
                holder.name.setText(product.Name);
                holder.id.setText(product.ID);
                holder.price.setText("₹: " + product.Price);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(view.getContext(), "Touched  " + product.Name, Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        productsView = (RecyclerView) view.findViewById(R.id.productsView);
        productsView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        productsView.setAdapter(firebaseRecyclerAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(view.getContext(), "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                ProductViewHolder holder = (ProductViewHolder) viewHolder;
                Toast.makeText(view.getContext(), holder.name.getText().toString() + " Removed", Toast.LENGTH_SHORT).show();

                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                DatabaseReference dbRef = firebaseRecyclerAdapter.getRef(position);
                dbRef.removeValue();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(productsView);

        newProduct = new ProductDialog(getContext());
        newProduct.setTitle("Product");
        newProduct.setContentView(R.layout.productform);
        newProduct.InitControls();

        return view;
    }

    void AddProduct(View view)
    {
        newProduct.show();
    }

    void CancelProduct(View view)
    {
        newProduct.cancel();
    }

    void SaveProduct(View view)
    {
        String toastMessage = newProduct.getIsValid();

        if(toastMessage.matches(""))    //no error
        {
            Product product = newProduct.getProduct();
            if(!helper.save(product))
                toastMessage ="Failed Saving";
            newProduct.clear();
            toastMessage = product.Name + " Added";
        }

        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
    }
}