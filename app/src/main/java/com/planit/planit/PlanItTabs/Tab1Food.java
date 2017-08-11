package com.planit.planit.PlanItTabs;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;
import com.planit.planit.LoginActivity;
import com.planit.planit.PlanItActivity;
import com.planit.planit.R;
import com.planit.planit.utils.AmountUnit;
import com.planit.planit.utils.AmountUnitPhone;
import com.planit.planit.utils.Item;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Tab1Food extends Fragment{

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewAmount;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    DatabaseReference fDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    RecycleAdapter adapter;
    ArrayList<Item> ItemList;
    FirebaseListAdapter<String> myAdapter;
    ArrayList<String> phoneNumberOfEvent;
    ArrayList<String> namesOfPhoneNumberOfEvent;
    Spinner areaSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.planit_tab1_food, container, false);
        phoneNumberOfEvent = new ArrayList<>();
        namesOfPhoneNumberOfEvent = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        ItemList = new ArrayList<>();

        //region firebase
        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() == null) {
            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginIntent);
            getActivity().finish();
        }
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent2 = new Intent(getActivity(), LoginActivity.class);
                    startActivity(loginIntent2);
                    getActivity().finish();
                }
            }
        };
        fUser = fAuth.getCurrentUser();
        if (fUser == null) {
            Intent loginIntent3 = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginIntent3);
            getActivity().finish();
        }
        fDatabase = FirebaseDatabase.getInstance().getReference();
        //endregion
        fDatabase.child("events").child("1234567").child("Friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                //final List<String> areas = new ArrayList<String>();
                phoneNumberOfEvent.clear();
                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String phoneName = areaSnapshot.getKey().toString();//.getValue(String.class);
                    phoneNumberOfEvent.add(phoneName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        //region for fab
         //TODO change
        //DatabaseReference eventsRef = fDatabase.child("events").child(eventId).child("FoodAndDrinks");

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.home_add_event);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //dropdown.setOnItemSelectedListener(getContext());
                final View inflaterView =  LayoutInflater.from(getContext()).inflate(R.layout.add_amount_unit_layout, null);

                fDatabase.child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Is better to use a List, because you don't know the size
                        // of the iterator returned by dataSnapshot.getChildren() to
                        // initialize the array
                        namesOfPhoneNumberOfEvent.clear();
                       // final List<String> areas = new ArrayList<String>();

                        for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                            if(phoneNumberOfEvent.contains(areaSnapshot.getKey())){
                                String areaName = areaSnapshot.child("firstName").getValue(String.class) + " " + areaSnapshot.child("lastName").getValue(String.class);
                                namesOfPhoneNumberOfEvent.add(areaName);
                            }

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                final AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Add New Food/Drink to event")
                        .setMessage("Amount And Units")
                        .setView(inflaterView)
                        .create();
                areaSpinner = (Spinner) inflaterView.findViewById(R.id.spinner1);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, namesOfPhoneNumberOfEvent);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                areaSpinner.setAdapter(areasAdapter);//areaSpinner.setSelection(position);
                //areaSpinner.setOnItemSelectedListener(null);
                OnItemSelectedListener l = new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(final AdapterView<?> aParent, final View aView,
                                               final int aPosition, final long aRowId) {
                        Toast.makeText(getContext(), "yayyy", Toast.LENGTH_SHORT).show();
                        //areaSpinner.setSelection(aPosition);
                    }
                    @Override
                    public void onNothingSelected(final AdapterView<?> aParent) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getContext(), "fuckkkkkkkkkk", Toast.LENGTH_SHORT).show();

                    }
                };

                //areaSpinner.setOnItemSelectedListener(l);
                areaSpinner.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(getContext(), "shoyyyyyyyy", Toast.LENGTH_SHORT).show();
                                areaSpinner.setSelection(position);
                            }
                        }
                );


                dialog.show();

//                Button addButton = (Button) inflaterView.findViewById(R.id.AddButton);
//                addButton.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
////                        final String timeStr;
////                        if (timePicker.getMinute() < 10) {
////                            timeStr = String.valueOf(timePicker.getHour()) + ":0" + String.valueOf(timePicker.getMinute());
////                        } else {
////                            timeStr = String.valueOf(timePicker.getHour()) + ":" + String.valueOf(timePicker.getMinute());
////
////                        }
////                        int day = datePicker.getDayOfMonth();
////                        int month = datePicker.getMonth() + 1;
////                        int year = datePicker.getYear();
////                        final String dateStr = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
////                        Log.i("debugging", timeStr);
////
////                        final TodoMessage msgObj = new TodoMessage(String.valueOf(taskEditText.getText()), timeStr, dateStr, "");
////
////
////                        //second section
////                        //save it to the firebase db
////                        FirebaseDatabase database = FirebaseDatabase.getInstance();
////                        String key = database.getReference("users").child(mAuth.getCurrentUser().getUid()).push().getKey();
////                        msgObj.setIdMsg(key);
////                        Map<String, Object> childUpdates = new HashMap<>();
////                        HashMap<String, String> todo = new HashMap<String, String>();
////                        todo.put("data", msgObj.getData());
////                        todo.put("hourCreated", msgObj.getHourCreated());
////                        todo.put("todoHour", msgObj.getTodoDate());
////                        todo.put("todoDate", msgObj.getTodoHour());
////                        todo.put("idMsg", msgObj.getIdMsg());
////
////                        childUpdates.put(key, todo);
////                        database.getReference("users").child(mAuth.getCurrentUser().getUid()).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
////                            @Override
////                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
////                                if (databaseError == null) {
////                                    finish();
////                                }
////                                todoList.add(msgObj);
////                                adapter.notifyItemInserted(todoList.size() - 1);
////                            }
////                        });
//                        dialog.dismiss();
//                    }
//                });
//                Button cancelButton = (Button) inflaterView.findViewById(R.id.CancelButton);
//                cancelButton.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
            }});

        return rootView;
    }


    public class OnSpinnerItemClicked implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            Toast.makeText(parent.getContext(), "Clicked : " +
                    parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();


        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        fAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    public void onResume() {
        super.onResume();
        try {

            //fDatabase = FirebaseDatabase.getInstance().getReference();
            fDatabase.child("events").child("1234567").child("FoodAndDrinks").addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ItemList.clear();

                            //Log.w("TodoApp", "getUser:onCancelled " + dataSnapshot.toString());
                            //Log.w("TodoApp", "count = " + String.valueOf(dataSnapshot.getChildrenCount()) + " values " + dataSnapshot.getKey());
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                String title = data.child("title").getValue().toString();
                                ArrayList<AmountUnitPhone> p = new ArrayList<AmountUnitPhone>();
                                for (DataSnapshot data2 : data.child("phoneNumbers").getChildren()) {
                                    Log.w("TodoApp", "getUser:onCancelled " + data2.getValue().toString());
                                    p.add(new AmountUnitPhone(data2.child("Amount").getValue().toString(), data2.child("Unit").getValue().toString(), data2.getKey().toString()));
                                }
                                Item item = new Item(title, p);//data.getValue(Item.class);
                                ItemList.add(item);
                            }
                            adapter = new RecycleAdapter();
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("PlantIt- Tab1Food", "Problem:onCancelled", databaseError.toException());
                        }
                    });
        } catch (Exception ex) {
            String TAG = "sdsd";
            Log.d(TAG, ex.getMessage());
        }

    }

    /////
    private class RecycleAdapter extends RecyclerView.Adapter {

        @Override
        public int getItemCount() {
            return ItemList.size();
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_message_item, parent, false);
            CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.foodlist_item,
                    parent, false);
            SimpleItemViewHolder pvh = new SimpleItemViewHolder(v);
            return pvh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SimpleItemViewHolder viewHolder = (SimpleItemViewHolder) holder;
            viewHolder.position = position;

            if (position % 2 == 0) {
                ((SimpleItemViewHolder) holder).setBackGroundColor(Color.parseColor("#58D3F7"));
            } else {
                ((SimpleItemViewHolder) holder).setBackGroundColor(Color.parseColor("#F5DA81"));
            }

            Item todo = ItemList.get(position);

            ((SimpleItemViewHolder) holder).todoTextView.setText(todo.getTitle());
        }

        public final class SimpleItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView todoTextView;
            LinearLayout expandedView;
            final CardView cv;
            public int position;


            public SimpleItemViewHolder(final CardView cv) {
                super(cv);
                cv.setOnClickListener(this);
                this.cv = cv;
                this.todoTextView = (TextView) cv.findViewById(R.id.item_text);
                this.expandedView = (LinearLayout) cv.findViewById(R.id.expand_view_layout);

            }

            public void setBackGroundColor(int colorBG) {
                this.cv.setBackgroundColor(colorBG);
            }

            @Override
            public void onClick(final View view) {

                LayoutInflater inflater = LayoutInflater.from(getContext());
                LinearLayout containerDestacado = (LinearLayout) cv.findViewById(R.id.expand_view_layout);



                Item item = (Item)ItemList.toArray()[position];
                ArrayList<AmountUnitPhone> aup = item.getQuantities();
                for(AmountUnitPhone a:  aup){
                    View inflatedLayout = inflater.inflate(R.layout.amount_unit_layout, null, false);

                    //phone
                    TextView phoneNum = (TextView) inflatedLayout.findViewById(R.id.item_phone);
                    String phone = a.getPhone().toString();

                    //DatabaseReference ref = fDatabase.child("users").child(phone);
                    //String fullname = ref.child("firstName").getKey().toString() +" "+ ref.child("lastName").toString();
                    phoneNum.setText(phone);

                    //Amount
                    TextInputEditText amount = (TextInputEditText) inflatedLayout.findViewById(R.id.amount_input);
                    amount.setText(a.getAmount());

                    //Unit
                    TextInputEditText unit = (TextInputEditText) inflatedLayout.findViewById(R.id.unit_input);
                    unit.setText(a.getUnit());
                    containerDestacado.addView(inflatedLayout);
                }


                boolean shouldExpand = this.expandedView.getVisibility() == View.GONE;

                //ChangeBounds transition = new ChangeBounds();
                //transition.setDuration(125);

                if (shouldExpand) {
                    this.expandedView.setVisibility(View.VISIBLE);
                    //viewHolder.imageView_toggle.setImageResource(R.drawable.collapse_symbol);
                } else {
                    this.expandedView.setVisibility(View.GONE);
                    containerDestacado.removeAllViews();
                    //viewHolder.imageView_toggle.setImageResource(R.drawable.expand_symbol);
                }

                //TransitionManager.beginDelayedTransition(recyclerView);
                this.itemView.setActivated(shouldExpand);
            }
        }
    }

}

