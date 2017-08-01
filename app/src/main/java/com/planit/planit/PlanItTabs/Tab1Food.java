package com.planit.planit.PlanItTabs;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.planit.planit.LoginActivity;
import com.planit.planit.R;
import com.planit.planit.utils.Item;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import org.w3c.dom.Comment;

import java.util.ArrayList;

import static android.R.attr.data;
import static android.content.ContentValues.TAG;


public class Tab1Food extends Fragment {

    private RecyclerView recyclerView;
    private Adapter adapter;
    private Toast toast;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    DatabaseReference fDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.planit_tab1_food, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new Adapter();
        recyclerView.setAdapter(adapter);
        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser() == null)
        {
            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginIntent);
            getActivity().finish();
        }
        fAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null)
                {
                    Intent loginIntent2 = new Intent(getActivity(), LoginActivity.class);
                    startActivity(loginIntent2);
                    getActivity().finish();
                }
            }
        });
        fUser = fAuth.getCurrentUser();
        if(fUser == null)
        {
            Intent loginIntent3 = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginIntent3);
            getActivity().finish();
        }
        fDatabase = FirebaseDatabase.getInstance().getReference();
        // /TODO change
        DatabaseReference eventsRef = fDatabase.child("events").child("1234567").child("FoodAndDrinks");
        //DatabaseReference eventsRef = fDatabase.child("events").child(eventId).child("FoodAndDrinks");
        //onAddItem();
        //FireBaseFood(eventsRef);

        return rootView;
    }

    public void FireBaseFood(DatabaseReference eventsRef){
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChild In Food Added:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                Item item = dataSnapshot.getValue(Item.class);
                adapter.addItem(item);


                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                Item newComment = dataSnapshot.getValue(Item.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                Item movedComment = dataSnapshot.getValue(Item.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(getContext(), "Failed to load Food And Drinks, Please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        eventsRef.addChildEventListener(childEventListener);
    }
    public void onAddItem(String title, String phoneNumber, String amount, String units) {
        Log.i("todo adder", "todo is " + data);
        adapter.addItem(new Item(title, phoneNumber, amount,units));

    }
/////
private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView todoTextView;
//        TextView addedOnTextView;
//        TextView dueToDateTextView;
//        TextView dueToHourTextView;
        //CheckBox checkBox;
        CardView card;

        protected ViewHolder(CardView cv) {
            super(cv);
            this.card = cv;
            this.todoTextView = (TextView) cv.findViewById(R.id.item_text);
//            this.addedOnTextView = (TextView) cv.findViewById(R.id.added_on);
//            this.dueToDateTextView = (TextView) cv.findViewById(R.id.due_to_date);
//            this.dueToHourTextView = (TextView) cv.findViewById(R.id.due_to_hour);
//            this.checkBox = (CheckBox) cv.findViewById(R.id.checkbox);
//            this.checkBox.setOnCheckedChangeListener(this);
            //this.card.setOnLongClickListener(this);
            this.card.setOnClickListener(this);
        }

//        @Override
//        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            Item currentTodo = mItem.get(getAdapterPosition());
//            if (!onBind) {
//
//                mItem.add(0, mItem.remove(getAdapterPosition()));
//                notifyItemMoved(getAdapterPosition(), 0);
//                notifyItemChanged(0);
//            }
//        }

//        @Override
//        public boolean onLongClick(View view) {
//            Toast.makeText(getContext(), "long clicked!", Toast.LENGTH_SHORT).show();
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setTitle(this.todoTextView.getText()).setItems(R.array.menu_array,
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            switch (which) {
//                                case 0:
//                                    mTodos.remove(getAdapterPosition());
//                                    notifyItemRemoved(getAdapterPosition());
//                            }
//                        }
//                    });
//            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    mTodos.remove(getAdapterPosition());
//                    notifyItemRemoved(getAdapterPosition());
//                }
//            });
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            builder.create().show();
//            return true;
//        }

        @Override
        public void onClick(View v) {
            //TODO take from Tomer's code in github
//            Toast.makeText(getContext(), "clicked food!", Toast.LENGTH_SHORT).show();
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setTitle(this.todoTextView.getText()).setItems(R.array.menu_array,
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            switch (which) {
//                                case 0:
//                                    mItem.remove(getAdapterPosition());
//                                    notifyItemRemoved(getAdapterPosition());
//                            }
//                        }
//                    });
//            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    mItem.remove(getAdapterPosition());
//                    notifyItemRemoved(getAdapterPosition());
//                }
//            });
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            builder.create().show();
        }
    }

    private boolean onBind;
    private final String[] COLORS = {"#A9CCE3", "#D4E6F1"};
    private ArrayList<Item> mItem;
    private int numberOfChecked;

    public Adapter() {
        this.mItem = new ArrayList<>();
        this.numberOfChecked = 0;
    }

    public void addItem(Item message) {
        this.mItem.add(getItemCount() - numberOfChecked, message);
        notifyItemInserted(getItemCount() - numberOfChecked);
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.foodlist_item,
                parent, false);
        cView.setCardBackgroundColor(Color.parseColor(COLORS[viewType]));
        ViewHolder hView = new ViewHolder(cView);
        return hView;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Item currentTodo = mItem.get(position);
        holder.todoTextView.setText(currentTodo.getTitle());

//        holder.dueToDateTextView.setText(currentTodo.getDueToDate());
//        holder.dueToHourTextView.setText(currentTodo.getDueToHour());
//        holder.addedOnTextView.setText(currentTodo.getAddedStr());
//        onBind = true;
//        holder.checkBox.setChecked(currentTodo.isChecked());
//        onBind = false;
//        if(currentTodo.isChecked())
//        {
//            Log.i("checked debug", "setting enabled to false at position " + position);
//            holder.checkBox.setEnabled(false);
//        }
//        else
//        {
//            Log.i("checked debug", "setting enabled to true at position " + position);
//            holder.checkBox.setEnabled(true);
//        }
    }
}
}
