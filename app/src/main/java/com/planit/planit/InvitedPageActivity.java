package com.planit.planit;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
 * Created by HP on 14-Aug-17.
 */

public class InvitedPageActivity extends AppCompatActivity implements View.OnClickListener{

    private final int PERMISSION_READ_CONTACTS = 0;

    final int HOST = 0;
    final int INVITE = 1;

    TextView eventTitle;
    FloatingActionButton addPeople;
    FloatingActionButton deletePeople;
    FloatingActionButton discardSelection;

    RecyclerView recyclerView;
    ContactsAdapter cAdapter;

    DatabaseReference mDatabase;
    User currentUser;
    Event currentEvent;

    ChildEventListener eventsToUsersHostedListener;
    ChildEventListener eventsToUsersInvitedListener;

    boolean isSelecting;
    boolean isHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.invite_page_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.event_activity_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //need to change to the relevent event
        getSupportActionBar().setTitle(null);

        eventTitle = (TextView) findViewById(R.id.event_title);
        addPeople = (FloatingActionButton) findViewById(R.id.invite_people);
        deletePeople = (FloatingActionButton) findViewById(R.id.delete_people);
        discardSelection = (FloatingActionButton) findViewById(R.id.discard_selection);
        addPeople.setOnClickListener(this);
        deletePeople.setOnClickListener(this);
        discardSelection.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.invited_recycler_view);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        cAdapter = new ContactsAdapter();
        recyclerView.setAdapter(cAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Bundle extras = getIntent().getExtras();
        currentUser = new Gson().fromJson(extras.getString("user"), User.class);
        currentEvent = new Gson().fromJson(extras.getString("event"), Event.class);
        isHost = extras.getBoolean("isHost");

        isSelecting = false;

        eventTitle.setText(currentEvent.getName());

        if (isHost)
        {
            addPeople.setVisibility(View.VISIBLE);
        };

        eventsToUsersHostedListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("Child listener", "new child was added to hosted!");
                getContact(dataSnapshot.getKey(), HOST);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("Child listener", "new child was removed from hosted!");
                cAdapter.removeContact(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        eventsToUsersInvitedListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("Child listener", "new child was added to invited!");
                getContact(dataSnapshot.getKey(), INVITE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("Child listener", "new child was removed from invited!");
                cAdapter.removeContact(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.child(FirebaseTables.eventsToUsers + "/" + currentEvent.getKey() + "/hosted").
                addChildEventListener(eventsToUsersHostedListener);
        mDatabase.child(FirebaseTables.eventsToUsers + "/" + currentEvent.getKey() + "/invited").
                addChildEventListener(eventsToUsersInvitedListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mDatabase.child(FirebaseTables.eventsToUsers + "/" + currentEvent.getKey() + "/hosted").
                removeEventListener(eventsToUsersHostedListener);
        mDatabase.child(FirebaseTables.eventsToUsers + "/" + currentEvent.getKey() + "/invited").
                removeEventListener(eventsToUsersInvitedListener);
        cAdapter.clear();
    }

    public void getContact(String userPhone, final int type) {
        mDatabase.child("users/" + userPhone).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("getEvent debug", dataSnapshot.getKey());
                        User user = dataSnapshot.getValue(User.class);
                        user.setPhoneNumber(dataSnapshot.getKey());
                        Log.d("contacts retrieval", "event name is " + user.getFullName());
                        cAdapter.addContact(user, type);
                        if (type == HOST)
                        {
                            currentEvent.addHost(user);
                        }
                        else
                        {
                            currentEvent.addInvited(user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    public void handleDeletePeople()
    {
        for (String phone : cAdapter.hostsToDelete)
        {
            handleDeletePerson(phone);
        }
        for (String phone : cAdapter.invitedToDelete)
        {
            handleDeletePerson(phone);
        }
    }

    public void handleDeletePerson(final String phoneToDelete)
    {
        mDatabase.child(FirebaseTables.eventsToUsers + "/" + currentEvent.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Event e = dataSnapshot.getValue(Event.class);
                        final String eventKey = dataSnapshot.getKey();
                        // user is host
                        if (currentEvent.getHosted().containsKey(phoneToDelete)) {
                            // user was only host, delete whole event
                            if (currentEvent.getHosted().size() == 1) {
                                // delete event from all databases
                                mDatabase.child(FirebaseTables.eventsInfoTable + "/" + eventKey).setValue(null);
                            } else {
                                // user was not only host, so delete from hosts list on both necessary tables,
                                // usersToEvents and eventsToUsers
                                mDatabase.child(FirebaseTables.usersToEvents + "/" + phoneToDelete + "/hosted/"
                                        + eventKey).setValue(null);
                                mDatabase.child(FirebaseTables.eventsToUsers + "/" + eventKey + "/hosted/"
                                        + phoneToDelete).setValue(null);
                            }
                        } else if (currentEvent.getInvited().containsKey(phoneToDelete)) {
                            // user is invited, delete just from both invited lists
                            mDatabase.child(FirebaseTables.usersToEvents + "/" + phoneToDelete + "/invited/"
                                    + eventKey).setValue(null);
                            mDatabase.child(FirebaseTables.eventsToUsers + "/" + eventKey + "/invited/"
                                    + phoneToDelete).setValue(null);
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
                                                if (item.isUserInQuantites(phoneToDelete))
                                                {
                                                    mDatabase.child("eventsData/" + eventKey + "/foodAndDrinks/"
                                                            + data.getKey() + "/quantities/"
                                                            + phoneToDelete).setValue(null);
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
                                                if (item.isUserInQuantites(phoneToDelete))
                                                {
                                                    mDatabase.child("eventsData/" + eventKey + "/equipment/"
                                                            + data.getKey() + "/quantities/"
                                                            + phoneToDelete).setValue(null);
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
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.invite_people:
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                        // ask for permission
                        ActivityCompat.requestPermissions(this,
                                new String[]{android.Manifest.permission.READ_CONTACTS},
                                PERMISSION_READ_CONTACTS);
                    }
                else
                {
                    // we have permission
                    Intent inviteActivity = new Intent(this, InviteActivity.class);
                    inviteActivity.putExtra("user", new Gson().toJson(currentUser));
                    inviteActivity.putExtra("event", new Gson().toJson(currentEvent));
                    startActivity(inviteActivity);
                }
            case R.id.delete_people:
                handleDeletePeople();
                addPeople.setVisibility(View.VISIBLE);
                deletePeople.setVisibility(View.GONE);
                discardSelection.setVisibility(View.GONE);
                break;
            case R.id.discard_selection:
                cAdapter.clearSelections();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case PERMISSION_READ_CONTACTS:
                Intent inviteActivity = new Intent(this, InviteActivity.class);
                inviteActivity.putExtra("user", new Gson().toJson(currentUser));
                inviteActivity.putExtra("event", new Gson().toJson(currentEvent));
                startActivity(inviteActivity);
        }
    }

    private class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
                View.OnLongClickListener{

            CardView card;
            TextView name;
            TextView phone;
            ImageView hostIcon;

            ContactViewHolder(View view) {
                super(view);
                this.card = (CardView) view;
                this.name = (TextView) view.findViewById(R.id.invite_name_text);
                this.phone = (TextView) view.findViewById(R.id.invite_phone_number);
                this.hostIcon = (ImageView) view.findViewById(R.id.host_icon_inviting_list);
                this.card.setOnClickListener(this);
                this.card.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (!isSelecting || currentUser.getPhoneNumber().equals(this.phone.getText().toString()))
                {
                    return;
                }
                String currentPhone = this.phone.getText().toString();
                int index = findIndexByKey(currentPhone);
                if (hostsToDelete.contains(currentPhone))
                {
                    hostsToDelete.remove(currentPhone);
                    notifyItemChanged(index + 1);
                    if (hostsToDelete.isEmpty() && invitedToDelete.isEmpty())
                    {
                        addPeople.setVisibility(View.VISIBLE);
                        deletePeople.setVisibility(View.GONE);
                        discardSelection.setVisibility(View.GONE);
                        isSelecting = false;
                    }
                    return;
                }
                if (invitedToDelete.contains(currentPhone))
                {
                    invitedToDelete.remove(currentPhone);
                    notifyItemChanged(index + 2);
                    if (hostsToDelete.isEmpty() && invitedToDelete.isEmpty())
                    {
                        addPeople.setVisibility(View.VISIBLE);
                        deletePeople.setVisibility(View.GONE);
                        discardSelection.setVisibility(View.GONE);
                        isSelecting = false;
                    }
                    return;
                }
                if (index < numOfHosts)
                {
                    // this is a host
                    hostsToDelete.add(currentPhone);
                    notifyItemChanged(index + 1);
                }
                else
                {
                    invitedToDelete.add(currentPhone);
                    notifyItemChanged(index + 2);
                }
            }

            @Override
            public boolean onLongClick(View v) {
                if (isSelecting || !isHost || currentUser.getPhoneNumber().equals
                        (this.phone.getText().toString()))
                {
                    return true;
                }
                isSelecting = true;
                addPeople.setVisibility(View.GONE);
                deletePeople.setVisibility(View.VISIBLE);
                discardSelection.setVisibility(View.VISIBLE);
                String currentPhone = this.phone.getText().toString();
                int index = findIndexByKey(currentPhone);
                if (index < numOfHosts)
                {
                    // this is a host
                    hostsToDelete.add(currentPhone);
                    notifyItemChanged(index + 1);
                }
                else
                {
                    invitedToDelete.add(currentPhone);
                    notifyItemChanged(index + 2);
                }
                return true;
            }
        }

        class TitleViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView subtitle;

            TitleViewHolder(View textView) {
                super(textView);
                this.title = (TextView) textView.findViewById(R.id.recycler_view_title);
                this.subtitle = (TextView) textView.findViewById(R.id.recycler_view_subtitle);
            }
        }

        final int HOST_TITLE = 0;
        final int INVITE_TITLE = 1;
        final int CONTACT = 2;

        ArrayList<User> users;
        HashMap<String, Integer> usersType;
        int numOfHosts;
        boolean noHosted;
        boolean noInvited;

        ArrayList<String> hostsToDelete;
        ArrayList<String> invitedToDelete;

        public ContactsAdapter() {
            this.users = new ArrayList<>();
            this.usersType = new HashMap<>();
            this.numOfHosts = 0;
            this.noHosted = true;
            this.noInvited = true;
            this.hostsToDelete = new ArrayList<>();
            this.invitedToDelete = new ArrayList<>();
        }

        public void addContact(User e, int type) {
            if (type == HOST) {
                this.users.add(this.numOfHosts, e);
                this.usersType.put(e.getPhoneNumber(), HOST);
                this.noHosted = false;
                this.numOfHosts++;
                notifyItemInserted(this.numOfHosts);
            } else {
                this.users.add(e);
                this.usersType.put(e.getPhoneNumber(), INVITE);
                if (noInvited)
                {
                    notifyItemChanged(getItemCount() - 2);
                }
                this.noInvited = false;
                notifyItemInserted(getItemCount());
            }
        }

        public void removeContact(String phoneToDelete)
        {
            int index = findIndexByKey(phoneToDelete);
            if (index == -1)
            {
                // event not found
                return;
            }
            this.users.remove(index);
            if (this.usersType.get(phoneToDelete) == HOST)
            {
                // means this event was a hosted one
                this.numOfHosts--;
                this.usersType.remove(phoneToDelete);
                notifyItemRemoved(index + 1);
            }
            else if (this.usersType.get(phoneToDelete) == INVITE)
            {
                this.usersType.remove(phoneToDelete);
                notifyItemRemoved(index + 2);
            }
            if (!this.usersType.containsValue(INVITE))
            {
                this.noInvited = true;
                notifyItemChanged(this.numOfHosts + 1);
            }
            if (!this.usersType.containsValue(HOST))
            {
                this.noHosted = true;
                notifyItemChanged(0);
            }
        }

        public int findIndexByKey(String key) {
            for (int i = 0; i < getItemCount() - 2; i++)
            {
                if (this.users.get(i).getPhoneNumber().equals(key))
                {
                    return i;
                }
            }
            return -1;
        }

        public void clearSelections()
        {
            this.hostsToDelete.clear();
            this.invitedToDelete.clear();
            addPeople.setVisibility(View.VISIBLE);
            deletePeople.setVisibility(View.GONE);
            discardSelection.setVisibility(View.GONE);
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == CONTACT) {
                return new ContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.contact_item, parent, false));
            } else {
                return new TitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.recycler_view_header, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int type = getItemViewType(position);
            if (type == CONTACT) {
                int index = 0;
                boolean isHost = false;
                if (position <= this.numOfHosts) {
                    // this is a host
                    isHost = true;
                    index = position - 1;
                } else {
                    index = position - 2;
                }
                final User currentUserBinding = this.users.get(index);
                ContactViewHolder userView = (ContactViewHolder) holder;
                if (currentUser.getPhoneNumber().equals(currentUserBinding.getPhoneNumber()))
                {
                    // set name to "You" for nice interace
                    userView.name.setText(R.string.you);
                }
                else
                {
                    userView.name.setText(currentUserBinding.getFullName());
                }
                userView.phone.setText(currentUserBinding.getPhoneNumber());
                if (isHost)
                {
                    userView.hostIcon.setVisibility(View.VISIBLE);
                }
                else
                {
                    userView.hostIcon.setVisibility(View.GONE);
                }
                if (hostsToDelete.contains(currentUserBinding.getPhoneNumber())
                        || invitedToDelete.contains(currentUserBinding.getPhoneNumber()))
                {
                    userView.card.setBackgroundColor(getColor(android.R.color.darker_gray));
                }
                else
                {
                    userView.card.setBackgroundColor(getColor(R.color.cardview_light_background));
                }
            } else if (type == HOST_TITLE) {
                TitleViewHolder eventView = (TitleViewHolder) holder;
                eventView.title.setText(R.string.hosted_events);
                eventView.subtitle.setText(R.string.hosted_events_empty);
            } else if (type == INVITE_TITLE) {
                TitleViewHolder eventView = (TitleViewHolder) holder;
                eventView.title.setText(R.string.invited_events);
                eventView.subtitle.setText(R.string.invited_list_empty);
                if (this.noInvited) {
                    eventView.subtitle.setVisibility(View.VISIBLE);
                } else {
                    eventView.subtitle.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return HOST_TITLE;
            }
            if (position == numOfHosts + 1) {
                return INVITE_TITLE;
            } else {
                return CONTACT;
            }
        }

        @Override
        public int getItemCount() {
            return this.users.size() + 2;
        }

        public void clear()
        {
            this.users.clear();
            this.usersType.clear();
            this.numOfHosts = 0;
            this.noHosted = true;
            this.noInvited = true;
        }
    }
}
