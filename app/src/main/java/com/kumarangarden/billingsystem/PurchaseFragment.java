package com.kumarangarden.billingsystem;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kumarangarden.billingsystem.m_FireBase.FirebaseHelper;
import com.kumarangarden.billingsystem.m_Model.Item;
import com.kumarangarden.billingsystem.m_Print.BillingService;
import com.kumarangarden.billingsystem.m_UI.ItemDialog;
import com.kumarangarden.billingsystem.m_UI.ItemViewHolder;
import com.kumarangarden.billingsystem.m_Utility.DateTimeUtil;
import com.roughike.bottombar.BottomBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kanna_000 on 09-08-2017.
 */

public class PurchaseFragment extends Fragment {

    ItemDialog newItem;
    DatabaseReference db;
    FirebaseHelper helper;
    RecyclerView itemsView;
    FirebaseRecyclerAdapter<Item, ItemViewHolder> firebaseRecyclerAdapter;
    TextView dateView, timeView, totalView;
    View view;
    BottomBar bottomBar;
    AutoCompleteTextView editCustomer;

    boolean editingCustomer = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_purchase, container, false);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        db = firebaseDatabase.getReference();

        /*if(!BillingService.initialized) {
            BillingService.initialized = true;
            firebaseDatabase.setPersistenceEnabled(true);
        }*/

        db.keepSynced(true);

        helper = new FirebaseHelper(db);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Item, ItemViewHolder>(Item.class,
                R.layout.itemcard, ItemViewHolder.class, db.child("Items").getRef()) {
            @Override
            protected void populateViewHolder(ItemViewHolder holder, final Item item, final int position) {
                DatabaseReference databaseReference = firebaseRecyclerAdapter.getRef(position);
                item.SetID(databaseReference.getKey());
                holder.Initialize(item, position);

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        newItem.setItem(item);
                        newItem.show();
                        //Intent intent = new Intent(view.getContext(), ItemActivity.class);
                        //view.getContext().startActivity(intent);
                        return false;
                    }
                });
              /*  holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Intent intent = new Intent(view.getContext(), ItemActivity.class);
                        //view.getContext().startActivity(intent);
                        Toast.makeText(view.getContext(), "Touched  " + item.Name, Toast.LENGTH_LONG).show();
                    }
                });*/
            }
        };

        itemsView = (RecyclerView) view.findViewById(R.id.itemsView);
        itemsView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        itemsView.setAdapter(firebaseRecyclerAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(view.getContext(), "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                ItemViewHolder holder = (ItemViewHolder) viewHolder;
                Toast.makeText(view.getContext(), holder.getName() + " Removed", Toast.LENGTH_SHORT).show();

                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                DatabaseReference dbRef = firebaseRecyclerAdapter.getRef(position);
                dbRef.removeValue();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(itemsView);

        newItem = new ItemDialog(getContext());
        newItem.setTitle("Item");
        newItem.setContentView(R.layout.itemform);
        newItem.InitControls(helper, false);

        //SimpleDateFormat sdf = new SimpleDateFormat("E, dd/MM/yyyy HH:mm:ss");
        //String currentDateandTime = sdf.format(new Date());

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String currentDateandTime = sdf.format(c.getTime());

        dateView = (TextView) view.findViewById(R.id.textDate);
        dateView.setText(currentDateandTime);

        sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        currentDateandTime = sdf.format(c.getTime());

        timeView = (TextView) view.findViewById(R.id.textTime);
        timeView.setText(currentDateandTime);


        final ArrayAdapter<String> autoComplete = new ArrayAdapter<String>(view.getContext(), R.layout.autocompletetext);
        db.child("Customers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                autoComplete.clear();
                //Basically, this says "For each DataSnapshot *Data* in dataSnapshot, do what's inside the method.
                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()){
                    //Get the suggestion by childing the key of the string you want to get.
                    String suggestion = suggestionSnapshot.getKey(); //suggestionSnapshot.child("Name").getValue(String.class);
                    //Add the retrieved string to the list
                    autoComplete.add(suggestion);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        editCustomer = (AutoCompleteTextView)view.findViewById(R.id.editCustomer);
        editCustomer.setThreshold(1);
        editCustomer.setAdapter(autoComplete);
        editCustomer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                editingCustomer = hasFocus;
            }
        });
        editCustomer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                db.child("Commands/Name").setValue(editCustomer.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        totalView = (TextView) view.findViewById(R.id.textTotal);
        totalView.setText("மொத்தம் ₹: 0.00");

        db.child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float sum = 0;
                int i = 0;
                //Basically, this says "For each DataSnapshot *Data* in dataSnapshot, do what's inside the method.
                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()){
                    Item item = suggestionSnapshot.getValue(Item.class);
                    sum += item.GetNetPrice();
                    i++;
                }
                totalView.setText( i + " பொருள் ₹: " + sum);
                firebaseRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        bottomBar = (BottomBar) container.findViewById(R.id.bottomBar);

        /*
        totalView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    holding = true;
                    ScheduleStateChange();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    holding = false;
                    handler.removeCallbacksAndMessages(null);
                }
                return false;
            }
        });*/

        Toolbar toolbar =(Toolbar) view.findViewById(R.id.purchaseToolbar);
        toolbar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ToggleMode();
                return false;
            }
        });

        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(view.getContext().getApplicationContext());
        int bottomBarState  =  saved_values.getInt("ONLY_PURCHASE", 0);
        SetAppMode(bottomBarState);

        FloatingActionButton addProduct = (FloatingActionButton) view.findViewById(R.id.addItem);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newItem.show();
            }
        });



        db.child("Commands").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateHUD(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //MainActivity.SetDateTime(db);
        return view;
    }

    private void updateHUD(DataSnapshot dataSnapshot) {
        String data;//dataSnapshot.getValue(String.class);
        switch (dataSnapshot.getKey())
        {
            case "Date":
                data = dataSnapshot.getValue(String.class);
                dateView.setText(DateTimeUtil.GetFormatChanged("yyyyMMdd", "dd/MM/yyyy", data));
                if(!MainActivity.localdateSet)
                    MainActivity.dateSet = true;
                break;
            case "Time":
                data = dataSnapshot.getValue(String.class);
                timeView.setText(DateTimeUtil.GetFormatChanged("hh:mm:ss a", "hh:mm a", data));
                if(!MainActivity.localtimeSet)
                    MainActivity.timeSet = true;
                break;
            case "Name":
                data = dataSnapshot.getValue(String.class);
                if(!editingCustomer)
                    editCustomer.setText(data);
                break;
        }
    }

    public void ToggleMode() {
        bottomBar.setVisibility(bottomBar.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);

        int bottomBarState = bottomBar.getVisibility();

        SetAppMode(bottomBarState);

        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(view.getContext().getApplicationContext());
        SharedPreferences.Editor editor = saved_values.edit();
        editor.putInt("ONLY_PURCHASE", bottomBarState);
        editor.commit();
    }

    public void SetAppMode(int bottomBarState) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)itemsView.getLayoutParams();
        FloatingActionButton addItem = (FloatingActionButton) view.findViewById(R.id.addItem);

        RelativeLayout.LayoutParams addItemParams = (RelativeLayout.LayoutParams) addItem.getLayoutParams();

        if(bottomBarState == View.VISIBLE ) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.addItem);
            addItemParams.setMargins(0, 0, 0, (int)getResources().getDimension(R.dimen.float_bar_bottom_ex));
        }
        else
        {
            params.addRule(RelativeLayout.ALIGN_BOTTOM, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
            addItemParams.setMargins(0, 0, 0, (int)getResources().getDimension(R.dimen.float_bar_bottom));
        }
        itemsView.setLayoutParams(params);
        addItem.setLayoutParams(addItemParams);
    }

    public void SetDate(View view) {
        final Calendar c = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this.view.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String currentDate = sdf.format(c.getTime());
                dateView.setText(currentDate);

                String db_date = new SimpleDateFormat("yyyyMMdd").format(c.getTime());
                db.child("Commands/Date").setValue(db_date);

                MainActivity.dateSet = true;
            }
        },c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
        datePickerDialog.show();
    }

    public void SetTime(View view) {
        final Calendar c = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this.view.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), hourOfDay, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                String currentDateandTime = sdf.format(c.getTime());
                timeView.setText(currentDateandTime);

                String db_time = new SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH).format(c.getTime());
                db.child("Commands/Time").setValue(db_time);
                MainActivity.timeSet = true;

            }
        }, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }
}
