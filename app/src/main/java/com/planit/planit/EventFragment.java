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
import android.widget.ProgressBar;
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
import com.planit.planit.utils.Item;
import com.planit.planit.utils.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HP on 06-Aug-17.
 */

public class EventFragment extends Fragment {

    private eventsAdapter eAdapter; // adapter for hosted events

    private RecyclerView recyclerView; // recycler for hosted events
    private User currentUser;

    private final ArrayList<String> eventIds = new ArrayList<>(); // will hold the event IDS

    private DatabaseReference mDatabase;

    private final int HOST = 0;
    private final int INVITE = 1;

    public EventFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eAdapter = new eventsAdapter();

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View pView = inflater.inflate(R.layout.event_list_layout, container, false);
        // set recycler views
        recyclerView = (RecyclerView) pView.findViewById(R.id.events_list_view);

        LinearLayoutManager linearLayoutManagerHosted = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManagerHosted);

        recyclerView.setAdapter(eAdapter);

        return pView;
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
                        Event e = dataSnapshot.getValue(Event.class);
                        e.setKey(dataSnapshot.getKey());
                        eAdapter.replaceEvent(e);
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
                                        // if this event has lists of data, delete them
                                        if (mDatabase.child("eventsData/" + eventKey) != null)
                                        {
                                            mDatabase.child("eventsData/" + eventKey).setValue(null);
                                        }
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
                        getEvent(dataSnapshot.getKey(), HOST);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Log.d("Child listener", "new child was removed from hosted!");
                        eAdapter.removeEvent(dataSnapshot.getKey());
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
                        getEvent(dataSnapshot.getKey(), INVITE);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Log.d("Child listener", "new child was removed from invited!");
                        eAdapter.removeEvent(dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void getEvent(String eventID, final int type)
    {
        mDatabase.child("events/" + eventID).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("getEvent debug", dataSnapshot.getKey());
                        Event event = dataSnapshot.getValue(Event.class);
                        event.setKey(dataSnapshot.getKey());
                        Log.d("events retrieval", "event name is " + event.getName());
                        eAdapter.addEvent(event, type);
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
                        final String eventKey = dataSnapshot.getKey();
                        final String currentPhone = currentUser.getPhoneNumber();
                        toDelete.setHosted(e.getHosted());
                        toDelete.setInvited(e.getInvited());
                        // user is host
                        if (toDelete.getHosted().containsKey(currentPhone)) {
                            // user was only host, delete whole event
                            if (toDelete.getHosted().size() == 1) {
                                // delete event from all databases
                                mDatabase.child(FirebaseTables.eventsInfoTable + "/" + eventKey).setValue(null);
                            } else {
                                // user was not only host, so delete from hosts list on both necessary tables,
                                // usersToEvents and eventsToUsers
                                mDatabase.child(FirebaseTables.usersToEvents + "/" + currentPhone + "/hosted/"
                                        + eventKey).setValue(null);
                                mDatabase.child(FirebaseTables.eventsToUsers + "/" + eventKey + "/hosted/"
                                        + currentPhone).setValue(null);
                            }
                        } else if (toDelete.getInvited().containsKey(currentPhone)) {
                            // user is invited, delete just from both invited lists
                            mDatabase.child(FirebaseTables.usersToEvents + "/" + currentPhone + "/invited/"
                                    + eventKey).setValue(null);
                            mDatabase.child(FirebaseTables.eventsToUsers + "/" + eventKey + "/invited/"
                                    + currentPhone).setValue(null);
                        }
                        // remove this user from food, drinks and equipment lists
                        // for each list, iterate each requested item's quantities.
                        // if the user participated in this item, remove his quantity.
                        mDatabase.child("eventsData/" + eventKey + "/foodAndDrinks").
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists())
                                        {
                                            return;
                                        }
                                        Event event = dataSnapshot.getValue(Event.class);
                                        if (event.getFoodAndDrinks() != null)
                                        {
                                            for (Map.Entry<String, Item> data : event.getFoodAndDrinks().entrySet())
                                            {
                                                Item item = data.getValue();
                                                if (item.isUserInQuantites(currentPhone))
                                                {
                                                    mDatabase.child("eventsData/" + eventKey + "/foodAndDrinks/"
                                                            + data.getKey() + "/quantities/"
                                                            + currentPhone).setValue(null);
                                                }
                                            }
                                        }
                                    }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        mDatabase.child("eventsData/" + eventKey + "/equipment").
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists())
                                        {
                                            return;
                                        }
                                        Event event = dataSnapshot.getValue(Event.class);
                                        if (event.getFoodAndDrinks() != null)
                                        {
                                            for (Map.Entry<String, Item> data : event.getFoodAndDrinks().entrySet())
                                            {
                                                Item item = data.getValue();
                                                if (item.isUserInQuantites(currentPhone))
                                                {
                                                    mDatabase.child("eventsData/" + eventKey + "/equipment/"
                                                            + data.getKey() + "/quantities/"
                                                            + currentPhone).setValue(null);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                        }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private class eventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
                                                                        View.OnLongClickListener{
            CardView card;
            TextView eName;
            TextView eDate;
            TextView eTime;
            TextView eLocation;

            public EventViewHolder(View view) {
                super(view);
                this.card = (CardView) view.findViewById(R.id.event_card);
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
                int index = 0;
                if (position <= lastHostedEvent)
                {
                    index = position - 1;
                }
                else
                {
                    index = position - 2;
                }
                Event event = events.get(index);

                Log.d("clicked event", event.getName());
                Log.d("clicked event", event.getKey());

                Intent eventIntent = new Intent(getContext(), EventActivity.class);
                eventIntent.putExtra("user", new Gson().toJson(currentUser));
                eventIntent.putExtra("event", new Gson().toJson(event));
                eventIntent.putExtra("isHost", position <= lastHostedEvent);
                startActivity(eventIntent);
            }

            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete " + this.eName.getText().toString() + "?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int index = 0;
                        int position = getAdapterPosition();
                        if (position <= lastHostedEvent)
                        {
                            index = position - 1;
                        }
                        else
                        {
                            index = position - 2;
                        }
                        handleDeleteEvent(events.get(index));
                        notifyItemRemoved(index);
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

        class TitleViewHolder extends RecyclerView.ViewHolder
        {
            TextView title;
            TextView subtitle;

            public TitleViewHolder(View textView)
            {
                super(textView);
                this.title = (TextView) textView.findViewById(R.id.recycler_view_title);
                this.subtitle = (TextView) textView.findViewById(R.id.recycler_view_subtitle);
            }
        }

        final int HOST_TITLE = 0;
        final int INVITE_TITLE = 1;
        final int EVENT = 2;

        ArrayList<Event> events;
        HashMap<String, Integer> eventType;
        int lastHostedEvent;
        boolean noInvited;
        boolean noHosted;

        public  eventsAdapter()
        {
            this.events = new ArrayList<>();
            this.eventType = new HashMap<>();
            this.lastHostedEvent = 0;
            this.noInvited = true;
            this.noHosted = true;
        }

        public void addEvent(Event e, int type)
        {
            if (type == HOST)
            {
                this.events.add(this.lastHostedEvent, e);
                this.eventType.put(e.getKey(), HOST);
                if (noHosted)
                {
                    notifyItemChanged(0);
                }
                this.noHosted = false;
                this.lastHostedEvent++;
                notifyItemInserted(this.lastHostedEvent);
            }
            else
            {
                this.events.add(e);
                this.eventType.put(e.getKey(), INVITE);
                if (noInvited)
                {
                    notifyItemChanged(getItemCount() - 2);
                }
                this.noInvited = false;
                notifyItemInserted(getItemCount());
            }
        }

        public void replaceEvent(Event event)
        {
            String eventKey = event.getKey();
            int index = findIndexByKey(eventKey);
            if (index == -1)
            {
                // event not found
                Log.d("edit debug", "event not found");
                return;
            }
            Log.d("edit debug", "changed event " + event.getName());
            this.events.remove(index);
            this.events.add(index, event);
            if (index < lastHostedEvent)
            {
                notifyItemChanged(index + 1);
            }
            else
            {
                notifyItemChanged(index + 2);
            }
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
            if (this.eventType.get(eventKey) == HOST)
            {
                // means this event was a hosted one
                this.lastHostedEvent--;
                this.eventType.remove(eventKey);
            }
            if (!this.eventType.containsValue(INVITE))
            {
                this.noInvited = true;
                notifyItemChanged(getItemCount());
            }
            if (!this.eventType.containsValue(HOST))
            {
                this.noHosted = true;
                notifyItemChanged(0);
            }
            notifyDataSetChanged();
        }

        public int findIndexByKey(String key)
        {
            Log.d("Looking for", "key is " + key);
            for (int i = 0; i < this.events.size(); i++)
            {
                Log.d("iteration", "key now is " + this.events.get(i).getKey());
                if (this.events.get(i).getKey().equals(key))
                {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == EVENT)
            {
                return new EventViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.event_item, parent, false));
            }
            else
            {
                return new TitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.recycler_view_header, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int type = getItemViewType(position);
            if (type == EVENT)
            {
                int index = 0;
                if (position <= this.lastHostedEvent)
                {
                    index = position - 1;
                }
                else
                {
                    index = position - 2;
                }
                final Event currentEvent = this.events.get(index);
                EventViewHolder eventView = (EventViewHolder) holder;
                eventView.eName.setText(currentEvent.getName());
                eventView.eDate.setText(currentEvent.getDate());
                eventView.eTime.setText(currentEvent.getTime());
                eventView.eLocation.setText(currentEvent.getLocation());
            }
            else if (type == HOST_TITLE)
            {
                TitleViewHolder eventView = (TitleViewHolder) holder;
                eventView.title.setText(R.string.hosted_events);
                eventView.subtitle.setText(R.string.hosted_events_empty);
                if (noHosted)
                {
                    eventView.subtitle.setVisibility(View.VISIBLE);
                }
                else
                {
                    eventView.subtitle.setVisibility(View.GONE);
                }
            }
            else if (type == INVITE_TITLE)
            {
                TitleViewHolder eventView = (TitleViewHolder) holder;
                eventView.title.setText(R.string.invited_events);
                eventView.subtitle.setText(R.string.invited_events_empty);
                if (noInvited)
                {
                    eventView.subtitle.setVisibility(View.VISIBLE);
                }
                else
                {
                    eventView.subtitle.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)
            {
                return HOST_TITLE;
            }
            if (position == this.lastHostedEvent + 1)
            {
                return INVITE_TITLE;
            }
            else
            {
                return EVENT;
            }
        }

        @Override
        public int getItemCount() {
            return this.events.size() + 2;
        }
    }
}
