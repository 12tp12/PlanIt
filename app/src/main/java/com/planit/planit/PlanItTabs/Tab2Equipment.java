package com.planit.planit.PlanItTabs;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.planit.planit.LoginActivity;
import com.planit.planit.R;
import com.planit.planit.utils.AmountUnitPhone;
import com.planit.planit.utils.Event;
import com.planit.planit.utils.Item;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.planit.planit.utils.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Tab2Equipment extends Fragment{

    private RecyclerView recyclerView;
    ItemsAdapter adapter;

    TextView emptyList;

    FirebaseAuth fAuth;
    FirebaseUser fUser;
    DatabaseReference mDatabase;
    ChildEventListener equipmentListener;

    User currentUser;
    Event currentEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //region firebase
        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() == null) {
            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginIntent);
            getActivity().finish();
        }
        fUser = fAuth.getCurrentUser();
        if (fUser == null) {
            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginIntent);
            getActivity().finish();
        }
        equipmentListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.planit_tab1_food, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.items_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        adapter = new ItemsAdapter();
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        emptyList = (TextView) rootView.findViewById(R.id.list_empty);
        //endregion
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.add_item);

        Bundle extras = getArguments();
        currentUser = new Gson().fromJson(extras.getString("user"), User.class);
        currentEvent = new Gson().fromJson(extras.getString("event"), Event.class);


        mDatabase.child("eventsData/" + currentEvent.getKey() + "/equipment").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists())
                        {
                            // TODO stop spinner here
                            emptyList.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        equipmentListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("equipment listener", "item was added!");
                Item item = dataSnapshot.getValue(Item.class);
                item.setTitle(dataSnapshot.getKey());
                adapter.addItem(item);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("equipment listener", "item was changed!");
                Item item = dataSnapshot.getValue(Item.class);
                item.setTitle(dataSnapshot.getKey());
                adapter.changeItem(item);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("equipment listener", "item was removed!");
                adapter.removeItem(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.child("eventsData/" + currentEvent.getKey() + "/equipment").
                addChildEventListener(equipmentListener);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //dropdown.setOnItemSelectedListener(getContext());
                final View addEquipmentView = LayoutInflater.from(getContext()).inflate(R.layout.add_equipment_item, null);
                final TextView equipmentTitle = (TextView) addEquipmentView.findViewById(R.id.add_equipment_name);
                final TextView equipmentAmount = (TextView) addEquipmentView.findViewById(R.id.equipment_amount_input);
                final AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Add Equipment To Event")
                        .setView(addEquipmentView).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // leave empty, will set afterwards so dialog will not dismiss
                            }
                        })
                        .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create();

                equipmentTitle.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        final String title = s.toString();
                        if (title.isEmpty())
                        {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(false);
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setFocusable(false);
                            return;
                        }
                        mDatabase.child("eventsData/" + currentEvent.getKey() + "/equipment/" +
                                title).
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Item i = dataSnapshot.getValue(Item.class);
                                        if (i != null)
                                        {
                                            equipmentTitle.setError("Item already exists.");
                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(false);
                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setFocusable(false);
                                        }
                                        else
                                        {
                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(true);
                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setFocusable(true);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(false);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setFocusable(false);

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(validateEquipment(equipmentTitle, equipmentAmount)) {
                                    String foodTitleString = equipmentTitle.getText().toString();
                                    String foodAmountString = equipmentAmount.getText().toString();
                                    addNewEquipment(foodTitleString, foodAmountString);
                                    dialog.dismiss();
                                }
                            }
                        }
                );
            }
        });

        //mDatabase.child("eventsData/" + currentEvent.getKey() + "/equipment").
        //        addChildEventListener(equipmentListener);

        return rootView;
    }

    public Boolean validateEquipment(final TextView title, final TextView equipmentAmount)
    {
        if (title.getText().toString().isEmpty())
        {
            title.setError("Title is empty.");
            return false;
        }
        if (Integer.parseInt(equipmentAmount.getText().toString()) <= 0)
        {
            equipmentAmount.setError("Amount cannot be zero.");
            return false;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDatabase.child("eventsData/" + currentEvent.getKey() + "/equipment").
                removeEventListener(equipmentListener);
    }

    public void addNewEquipment(String foodTitle, String foodAmount)
    {
        Item newItem = new Item(foodTitle,Integer.parseInt(foodAmount), null, currentUser.getPhoneNumber(),
                currentUser.getFullName());
        Map<String, Object> childUpdates = new HashMap<>();
        //puts the full event in events root in firebase
        Log.d("path to DB is", "eventsData/" + currentEvent.getKey() + "/equipment/"
                + newItem.getTitle());
        childUpdates.put("eventsData/" + currentEvent.getKey() + "/equipment/"
                + newItem.getTitle(), newItem.toFirebaseMap());
        mDatabase.updateChildren(childUpdates);
    }

    private class ItemsAdapter extends RecyclerView.Adapter {

        private ArrayList<Item> itemsList;
        //int changedIndex = -1;

        public ItemsAdapter()
        {
            this.itemsList = new ArrayList<>();
        }

        public void addItem(Item item)
        {
            this.itemsList.add(item);
            emptyList.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            notifyItemInserted(getItemCount());
        }

        public void changeItem(Item changedItem)
        {
            int indexToChange = findItem(changedItem.getTitle());
            if (indexToChange == -1)
            {
                return;
            }
            this.itemsList.remove(indexToChange);
            this.itemsList.add(indexToChange, changedItem);
            //this.changedIndex = indexToChange;
            notifyItemChanged(indexToChange);
        }

        public void removeItem(String removedItem)
        {
            int indexToRemove = findItem(removedItem);
            if (indexToRemove == -1)
            {
                return;
            }
            this.itemsList.remove(indexToRemove);
            notifyItemRemoved(indexToRemove);
        }

        private int findItem(String title)
        {
            for (int i = 0; i < getItemCount(); i++)
            {
                if (this.itemsList.get(i).getTitle().equals(title))
                {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public int getItemCount() {
            return this.itemsList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_message_item, parent, false);
            CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.foodlist_item,
                    parent, false);
            return new SimpleItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SimpleItemViewHolder viewHolder = (SimpleItemViewHolder) holder;
            Item item = this.itemsList.get(position);

            viewHolder.equipmentTitle.setText(item.getTitle());
            viewHolder.requestedBy.setText(String.format(getResources().getString(R.string.addedby),
                    item.getRequestedByName()));
            viewHolder.amount.setText(String.valueOf(item.getNeededAmount()));
            if (item.getRequestedByPhone().equals(currentUser.getPhoneNumber()))
            {
                // this item was added by this user, allow him to delete it
                viewHolder.deleteButton.setVisibility(View.VISIBLE);
            }
            if (viewHolder.wasExpanded)
            {
                viewHolder.expandView();
            }
        }

        public final class SimpleItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView equipmentTitle;
            TextView requestedBy;
            TextView amount;
            ImageButton deleteButton;
            LinearLayout expandedView;
            boolean wasExpanded = false;
            final CardView cv;


            public SimpleItemViewHolder(final CardView cv) {
                super(cv);
                this.cv = cv;
                this.equipmentTitle = (TextView) cv.findViewById(R.id.item_title);
                this.requestedBy = (TextView) cv.findViewById(R.id.item_requested_by);
                this.amount = (TextView) cv.findViewById(R.id.item_amount_unit);
                this.deleteButton = (ImageButton) cv.findViewById(R.id.delete_item);
                this.expandedView = (LinearLayout) cv.findViewById(R.id.expand_view_layout);
                this.cv.setOnClickListener(this);
                this.deleteButton.setOnClickListener(this);
            }

            @Override
            public void onClick(final View view) {
                switch (view.getId())
                {
                    case R.id.delete_item:
                        deleteItem();
                        break;
                    default:
                        expandView();
                }
            }

            public void deleteItem()
            {
                Item item = itemsList.get(getAdapterPosition());
                mDatabase.child("eventsData/" + currentEvent.getKey() + "/equipment/"
                        + item.getTitle()).setValue(null);
            }

            public void expandView()
            {
                boolean shouldExpand = this.expandedView.getChildCount() == 0;

                //&& !(wasExpanded && changedIndex == getAdapterPosition())
                if (!shouldExpand ){;
                    wasExpanded = false;
                    this.expandedView.removeAllViews();
                    return;
                }

                wasExpanded = true;

                LayoutInflater inflater = LayoutInflater.from(getContext());

                final Item item = itemsList.get(getAdapterPosition());
                HashMap<String, AmountUnitPhone> aup = item.getQuantities();
                if (aup == null)
                {
                    // no one commited for this item
                    View inflatedLayout = inflater.inflate(R.layout.amount_unit_layout, this.cv, false);
                    final EditText amountEditText = (EditText) inflatedLayout.findViewById(R.id.amount_layout);
                    final TextView foodUnitLayout = (TextView) inflatedLayout.findViewById(R.id.item_unit_by_layout);
                    final AppCompatButton updateButton = (AppCompatButton) inflatedLayout.findViewById(R.id.item_update_button);

                    amountEditText.setText("0");
                    amountEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

                    foodUnitLayout.setVisibility(View.GONE);

                    updateButton.setVisibility(View.VISIBLE);
                    updateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String amount = amountEditText.getText().toString();
                            if (amount.isEmpty() || amount.equals("0"))
                            {
                                Toast.makeText(getContext(), "Amount can't be zero", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // we know amount is numeric since the edittext input type is numeric
                            int amountNumeric = Integer.parseInt(amount);
                            if (item.getLeftNeeded() < amountNumeric)
                            {
                                Toast.makeText(getContext(), "Wow! you want to bring too much!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            item.addQuantity(currentUser.getPhoneNumber(), amountNumeric, currentUser.getFullName());
                            mDatabase.child("eventsData/" + currentEvent.getKey() + "/equipment/"
                                    + item.getTitle() + "/quantities/" + currentUser.getPhoneNumber()).
                                    setValue(new AmountUnitPhone(amountNumeric, currentUser.getFullName()));
                        }
                    });
                    this.expandedView.addView(inflatedLayout);
                    return;
                } // aup == null if
                if (aup.containsKey(currentUser.getPhoneNumber()))
                {
                    final AmountUnitPhone current = aup.get(currentUser.getPhoneNumber());
                    View inflatedLayout = inflater.inflate(R.layout.amount_unit_layout, this.cv, false);
                    final EditText amountEditText = (EditText) inflatedLayout.findViewById(R.id.amount_layout);
                    TextView foodUnitLayout = (TextView) inflatedLayout.findViewById(R.id.item_unit_by_layout);
                    AppCompatButton updateButton = (AppCompatButton) inflatedLayout.findViewById(R.id.item_update_button);

                    amountEditText.setText(Integer.toString(current.getAmount()));
                    amountEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

                    foodUnitLayout.setVisibility(View.GONE);

                    updateButton.setVisibility(View.VISIBLE);
                    updateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String amount = amountEditText.getText().toString();
                            if (amount.isEmpty() || amount.equals("0"))
                            {
                                Toast.makeText(getContext(), "Amount can't be zero", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // we know amount is numeric since the edittext input type is numeric
                            int amountNumeric = Integer.parseInt(amount);
                            Log.d("needed now", String.valueOf(item.getLeftNeeded()
                                    + current.getAmount()));
                            if (amountNumeric > item.getLeftNeeded() + current.getAmount())
                            {
                                Toast.makeText(getContext(), "Wow! you want to bring too much!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            item.addQuantity(currentUser.getPhoneNumber(), amountNumeric, currentUser.getFullName());
                            current.setAmount(amountNumeric);
                            mDatabase.child("eventsData/" + currentEvent.getKey() + "/equipment/"
                                    + item.getTitle() + "/quantities/" + currentUser.getPhoneNumber()).
                                    setValue(new AmountUnitPhone(amountNumeric, currentUser.getFullName()));
                        }
                    });
                    this.expandedView.addView(inflatedLayout);
                } // containsKey if
                // now handle the rest of the views, other people that are bringing this item
                if (item.getQuantities().size() > 1)
                {
                    TextView alreadyGot = new TextView(getContext());
                    alreadyGot.setText(R.string.already_got);
                    this.expandedView.addView(alreadyGot);
                }
                for (Map.Entry<String, AmountUnitPhone> entry : item.getQuantities().entrySet())
                {
                    if (entry.getKey().equals(currentUser.getPhoneNumber()))
                    {
                        continue;
                    }
                    String currentPhone = entry.getKey();
                    AmountUnitPhone currentQuantity = entry.getValue();

                    View inflatedLayout = inflater.inflate(R.layout.amount_unit_layout, this.cv, false);
                    final EditText amountEditText = (EditText) inflatedLayout.findViewById(R.id.amount_layout);
                    TextView foodUnitLayout = (TextView) inflatedLayout.findViewById(R.id.item_unit_by_layout);
                    amountEditText.setText(Integer.toString(currentQuantity.getAmount()));
                    amountEditText.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.tw__transparent
                            , null));
                    foodUnitLayout.setText(getResources().getString(R.string.unit_from,
                            "", currentQuantity.getFullname()));

                    this.expandedView.addView(inflatedLayout);
                }
            }
        }
    }

}

