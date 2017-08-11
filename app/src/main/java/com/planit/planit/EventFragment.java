package com.planit.planit;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.planit.planit.utils.Event;
import com.planit.planit.utils.FirebaseTables;
import com.planit.planit.utils.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HP on 06-Aug-17.
 */

public class EventFragment extends Fragment {

    private eventsAdapter eAdapterHosted; // adapter for hosted events
    private eventsAdapter eAdapterInvited; // adapter for invited events

    private RecyclerView recyclerViewHosted; // recycler for hosted events
    private RecyclerView recyclerViewInvited; // recycler for invited events
    private User currentUser;

    private final ArrayList<String> eventIds = new ArrayList<>(); // will hold the event IDS

    private DatabaseReference mDatabase;

    public EventFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eAdapterHosted = new eventsAdapter();
        eAdapterInvited = new eventsAdapter();

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View pView = inflater.inflate(R.layout.event_list_layout, container, false);
        // set recycler views
        recyclerViewHosted = (RecyclerView) pView.findViewById(R.id.events_list_view_hosted);
        recyclerViewInvited = (RecyclerView) pView.findViewById(R.id.events_list_view_invited);

        LinearLayoutManager linearLayoutManagerHosted = new LinearLayoutManager(getContext());
        recyclerViewHosted.setLayoutManager(linearLayoutManagerHosted);
        LinearLayoutManager linearLayoutManagerInvited = new LinearLayoutManager(getContext());
        recyclerViewInvited.setLayoutManager(linearLayoutManagerInvited);

        recyclerViewHosted.setAdapter(eAdapterHosted);
        recyclerViewInvited.setAdapter(eAdapterInvited);
        return pView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setData(User user)
    {
        this.currentUser = user;
        mDatabase.child(FirebaseTables.eventsInfoTable).addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        final String eventKey = dataSnapshot.getKey();
                        // if we reached here, there's only one host and he's this user.
                        // so we'll go over the invited lists and delete the event's from their
                        // list.
                        mDatabase.child(FirebaseTables.eventsToUsers + "/" + eventKey).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Event e = dataSnapshot.getValue(Event.class);
                                // delete event id from all hosts hosted events
                                ArrayList<String> hosted = new ArrayList<String>(e.getHosted().keySet());
                                for (String host : hosted)
                                {
                                    mDatabase.child(FirebaseTables.usersToEvents + "/" + host
                                            + "/hosted/" + eventKey).setValue(null);
                                }

                                // delete event id from all invited's invited events
                                if (e.getInvited() != null)
                                {
                                    ArrayList<String> inviteds = new ArrayList<String>(e.getInvited().keySet());
                                    for (String invited : inviteds)
                                    {
                                        mDatabase.child(FirebaseTables.usersToEvents + "/" + invited
                                                + "/invited/" + eventKey).setValue(null);
                                    }
                                }
                                mDatabase.child(FirebaseTables.eventsToUsers + "/" + eventKey).
                                        setValue(null);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
        mDatabase.child(FirebaseTables.usersToEvents + "/" + currentUser.getPhoneNumber() + "/hosted").
                addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d("Child listener", "new child was added to hosted!");
                        getEvent(dataSnapshot.getKey(), eAdapterHosted);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Log.d("Child listener", "new child was removed from hosted!");
                        eAdapterHosted.removeEvent(dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        mDatabase.child(FirebaseTables.usersToEvents + "/" + currentUser.getPhoneNumber() + "/invited").
                addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d("Child listener", "new child was added to invited!");
                        getEvent(dataSnapshot.getKey(), eAdapterInvited);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Log.d("Child listener", "new child was removed from invited!");
                        eAdapterInvited.removeEvent(dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void getEvent(String eventID, final eventsAdapter adapter)
    {
        mDatabase.child("events/" + eventID).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("getEvent debug", dataSnapshot.getKey());
                        Event event = dataSnapshot.getValue(Event.class);
                        event.setKey(dataSnapshot.getKey());
                        Log.d("events retrieval", "event name is " + event.getName());
                        adapter.addEvent(event);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    public void handleDeleteEvent(final Event toDelete)
    {
        /*
        if (host)
            if (hostsList is empty)
                -- delete event from invited/hosted event of all invited/hosted people
                -- delete event
            else
                --delete host from invited
        if (not host)
            -- delete event from own invited list
         */
        // retrieve event's hosts and invited lists
        mDatabase.child(FirebaseTables.eventsToUsers + "/" + toDelete.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Event e = dataSnapshot.getValue(Event.class);
                        String eventKey = dataSnapshot.getKey();
                        String currentPhone = currentUser.getPhoneNumber();
                        toDelete.setHosted(e.getHosted());
                        toDelete.setInvited(e.getInvited());
                        // user is host
                        if (toDelete.getHosted().containsKey(currentPhone))
                        {
                            // user was only host, delete whole event
                            if (toDelete.getHosted().size() == 1)
                            {
                                // delete event from all databases
                                mDatabase.child(FirebaseTables.eventsInfoTable + "/" + eventKey).setValue(null);
                            }
                            else
                            {
                                // user was not only host, so delete from hosts list on both necessary tables,
                                // usersToEvents and eventsToUsers
                                mDatabase.child(FirebaseTables.usersToEvents + "/" + currentPhone + "/hosted/"
                                        + eventKey).setValue(null);
                                mDatabase.child(FirebaseTables.eventsToUsers + "/" + eventKey + "/hosted/"
                                        + currentPhone).setValue(null);
                            }
                        }
                        else if (toDelete.getInvited().containsKey(currentPhone))
                        {
                            // user is invited, delete just from both invited lists
                            mDatabase.child(FirebaseTables.usersToEvents + "/" + currentPhone + "/invited/"
                                    + eventKey).setValue(null);
                            mDatabase.child(FirebaseTables.eventsToUsers + "/" + eventKey + "/invited/"
                                    + currentPhone).setValue(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private class eventsAdapter extends RecyclerView.Adapter<eventsAdapter.ViewHolder>
    {
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
                                                                        View.OnLongClickListener{
            CardView card;
            TextView eName;
            TextView eDate;
            TextView eTime;
            TextView eLocation;

            public ViewHolder(CardView view) {
                super(view);
                this.card = view;
                this.eName = (TextView) view.findViewById(R.id.event_name);
                this.eDate = (TextView) view.findViewById(R.id.event_date);
                this.eTime = (TextView) view.findViewById(R.id.event_time);
                this.eLocation = (TextView) view.findViewById(R.id.event_location);
                this.card.setOnClickListener(this);
                this.card.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Event event = events.get(position);

                Log.d("clicked event", event.getName());
                Log.d("clicked event", event.getKey());

                Intent eventIntent = new Intent(getContext(), EventActivity.class);
                eventIntent.putExtra("user", new Gson().toJson(currentUser));
                eventIntent.putExtra("event", new Gson().toJson(event));
                startActivity(eventIntent);
            }

            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete " + this.eName.getText().toString() + "?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handleDeleteEvent(events.get(getAdapterPosition()));
                        notifyItemRemoved(getAdapterPosition());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
                return true;
            }
        }

        ArrayList<Event> events;

        public  eventsAdapter()
        {
            this.events = new ArrayList<>();
        }

        public eventsAdapter(ArrayList<Event> events)
        {
            this.events = events;
        }

        public void addEvent(Event e)
        {
            this.events.add(e);
            notifyItemInserted(getItemCount());
        }

        public void removeEvent(String eventKey)
        {
            int index = findIndexByKey(eventKey);
            if (index == -1)
            {
                // event not found
                return;
            }
            this.events.remove(index);
            notifyItemRemoved(index);
        }

        public int findIndexByKey(String key)
        {
            for (int i = 0; i < getItemCount(); i++)
            {
                if (this.events.get(i).getKey().equals(key))
                {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView cView = (CardView) LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.event_item, parent, false);
            return new ViewHolder(cView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Event currentEvent = this.events.get(position);

            holder.eName.setText(currentEvent.getName());
            holder.eDate.setText(currentEvent.getDate());
            holder.eTime.setText(currentEvent.getTime());
            holder.eLocation.setText(currentEvent.getLocation());
        }

        @Override
        public int getItemCount() {
            return this.events.size();
        }
    }
}
