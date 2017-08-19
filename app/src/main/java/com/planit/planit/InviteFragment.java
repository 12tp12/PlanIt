package com.planit.planit;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.planit.planit.utils.Event;
import com.planit.planit.utils.FirebaseTables;
import com.planit.planit.utils.User;
import com.twitter.sdk.android.core.services.CollectionService;
import com.twitter.sdk.android.tweetcomposer.internal.util.ObservableScrollView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Created by HP on 03-Aug-17.
 */

public class InviteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private ContactsAdapter mAdapter;
    private RecyclerView recyclerView;

    private DatabaseReference mDatabase;

    private String searchTerm;

    User currentUser;
    Event currentEvent;

    public InviteFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ContactsAdapter();

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.invite_list_layout, container, false);
        recyclerView = (RecyclerView) mView.findViewById(R.id.contacts_recycler_view);

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void setData(User currentUser, Event currentEvent)
    {
        this.currentUser = currentUser;
        this.currentEvent = currentEvent;
        getLoaderManager().initLoader(ContactsQuery.QUERY_ID, null, this);
    }

    public void invite()
    {
        HashMap<String, Object> invited = mAdapter.mSelectedInvited;
        HashMap<String, Object> hosted = mAdapter.mSelectedHosts;

        mDatabase.child(FirebaseTables.eventsToUsers + "/" + currentEvent.getKey() +
                "/invited").updateChildren(invited);
        mDatabase.child(FirebaseTables.eventsToUsers + "/" + currentEvent.getKey() +
                "/hosted").updateChildren(hosted);

        for (Map.Entry<String, Object> entry : invited.entrySet())
        {
            mDatabase.child(FirebaseTables.usersToEvents + "/" + entry.getKey() + "/invited/"
                            + currentEvent.getKey()).setValue(true);
        }
        for (Map.Entry<String, Object> entry : hosted.entrySet())
        {
            mDatabase.child(FirebaseTables.usersToEvents + "/" + entry.getKey() + "/hosted/"
                    + currentEvent.getKey()).setValue(true);
        }
        getActivity().finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri contentUri = ContactsQuery.CONTENT_URI;

        if (searchTerm != null) {
            contentUri = Uri.withAppendedPath(ContactsQuery.CONTENT_URI_FILTER, Uri.encode(searchTerm));
        }
        return new CursorLoader(getActivity(),
                contentUri,
                ContactsQuery.PROJECTION,
                ContactsQuery.SELECTION,
                ContactsQuery.SELECTION_ARGS,
                ContactsQuery.SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        while (data.moveToNext())
        {
            final String name = data.getString(ContactsQuery.DISPLAY_NAME);
            final String phone = validatePhone(data.getString(ContactsQuery.NUMBER));
            ValueEventListener l = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        Log.d("DEBUG", "phone is " + dataSnapshot.getKey());
                        if (dataSnapshot.getKey().equals(currentUser.getPhoneNumber()))
                        {
                            Log.d("DEBUG", "skipping user");
                            // skip this user
                            return;
                        }
                        mAdapter.addUser(new ContactUser(name, phone));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                        Log.d("check", "in ONCANCELLED");
                }
            };
            mDatabase.child(FirebaseTables.users + "/" + phone).addListenerForSingleValueEvent(l);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void onQueryNotify(String newText)
    {
        mAdapter.reset();
        if (newText == null || newText.isEmpty())
        {
            searchTerm = null;
            getLoaderManager().restartLoader(
                    ContactsQuery.QUERY_ID, null, InviteFragment.this);
            return;
        }
        searchTerm = newText;
        // Restarts the loader. This triggers onCreateLoader(), which builds the
        // necessary content Uri from mSearchTerm.
        getLoaderManager().restartLoader(
                ContactsQuery.QUERY_ID, null, InviteFragment.this);
    }

    public String validatePhone(String phone)
    {
        return phone.replace("+972", "05").replace(" ", "").replace("-","").trim();
    }

    protected class ContactUser
    {
        protected String name;
        protected String phone;

        protected ContactUser(String name, String phone)
        {
            this.name = name;
            this.phone = phone;
        }
    }

    private class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>
    {
        private class ContactsComparator implements Comparator<ContactUser>{
            @Override
            public int compare(ContactUser o1, ContactUser o2) {
                return (o1.name.compareTo(o2.name));
            }
        }

        private ArrayList<ContactUser> users;
        private ArrayList<String> numbers;
        private HashMap<String, Object> mSelectedInvited;
        private HashMap<String, Object> mSelectedHosts;

        public ContactsAdapter()
        {
            this.users = new ArrayList<>();
            this.numbers = new ArrayList<>();
            this.mSelectedInvited = new HashMap<>();
            this.mSelectedHosts = new HashMap<>();
        }

        public void setSelected(String number)
        {
            if (mSelectedInvited.containsKey(number))
            {
                // swap from invited to hosted
                mSelectedInvited.remove(number);
                mSelectedHosts.put(number, true);
                return;
            }
            else if (mSelectedHosts.containsKey(number))
            {
                // remove from selected list
                mSelectedHosts.remove(number);
                return;
            }
            // else, this is the first click so add to invited
            mSelectedInvited.put(number, true);
        }

        public void addUser(ContactUser user)
        {
            if (this.numbers.contains(user.phone))
            {
                Log.d("DEBUG", "user exists " + user.phone);
                return;
            }
            Log.d("DEBUG", "Added user " + user.phone);
            this.users.add(user);
            this.numbers.add(user.phone);
            Collections.sort(this.users, new ContactsComparator());
            if (getItemCount() == 1)
            {
                notifyItemInserted(0);
            }
            else
            {
                notifyItemRangeChanged(0, getItemCount() - 1);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Inflates the list item layout.
            final CardView itemLayout =
                    (CardView)LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.contact_item, parent, false);

            final ViewHolder holder = new ViewHolder(itemLayout);

            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder view, int position) {

            ContactUser currentUser = this.users.get(position);

            view.name.setText(currentUser.name);
            view.phone.setText(currentUser.phone);
            if(mSelectedHosts.containsKey(currentUser.phone))
            {
                view.invite.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.invite.
                        getLayoutParams();
                params.removeRule(RelativeLayout.ALIGN_PARENT_END);
                view.hostInvite.setVisibility(View.VISIBLE);
                return;
            }
            else if(mSelectedInvited.containsKey(currentUser.phone))
            {
                view.invite.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.invite.
                        getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                view.hostInvite.setVisibility(View.GONE);
                return;
            }
            view.invite.setVisibility(View.GONE);
            view.hostInvite.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return this.users.size();
        }

        public void reset()
        {
            Log.d("DEBUG", "cleared users");
            int size = getItemCount();
            this.users.clear();
            this.numbers.clear();
            notifyItemRangeRemoved(0, size);
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
            CardView card;
            TextView name;
            TextView phone;
            ImageView hostInvite;
            ImageView invite;

            public ViewHolder(CardView cView)
            {
                super(cView);
                this.card = cView;
                this.name = (TextView) cView.findViewById(R.id.invite_name_text);
                this.phone = (TextView) cView.findViewById(R.id.invite_phone_number);
                this.hostInvite = (ImageView) cView.findViewById(R.id.host_icon_inviting_list);
                this.invite = (ImageView) cView.findViewById(R.id.icon_inviting_list);
                this.card.setOnClickListener(this);
                this.card.setClickable(true);
            }

            @Override
            public void onClick(View v) {
                Log.d("listener check", "clicked " + this.name.getText().toString());
                setSelected(this.phone.getText().toString());
                notifyItemChanged(getAdapterPosition());
            }
        }
    }
    /**
     * This interface defines constants for the Cursor and CursorLoader, based on constants defined
     * in the {@link android.provider.ContactsContract.CommonDataKinds.Phone} class.
     */
    public interface ContactsQuery {

        // An identifier for the loader
        final static int QUERY_ID = 1;

        // A content URI for the Contacts table
        final static Uri CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        final static Uri CONTENT_URI_FILTER = ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI;

        // The selection clause for the CursorLoader query. The search criteria defined here
        // restrict results to contacts that have a display name and are linked to visible groups.
        // Notice that the search on the string provided by the user is implemented by appending
        // the search string to CONTENT_FILTER_URI.
        @SuppressLint("InlinedApi")
        final static String SELECTION =
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + "<>''"
                        + " AND " + ContactsContract.CommonDataKinds.Phone.IN_VISIBLE_GROUP + "=1"
                        + " AND " + ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + " > 0"
                        + " AND " + ContactsContract.CommonDataKinds.Phone.TYPE + "=" +
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                        + " AND (" + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE ? OR "
                        + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE ? ) AND " +
                        ContactsContract.CommonDataKinds.Phone.ACCOUNT_TYPE_AND_DATA_SET + "= ?";

        final static String[] SELECTION_ARGS = {"+972_________", "05________", "com.whatsapp"};

        // The desired sort order for the returned Cursor. In Android 3.0 and later, the primary
        // sort key allows for localization. In earlier versions. use the display name as the sort
        // key.
        @SuppressLint("InlinedApi")
        final static String SORT_ORDER =
                ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY;

        // The projection for the CursorLoader query. This is a list of columns that the Contacts
        // Provider should return in the Cursor.
        @SuppressLint("InlinedApi")
        final static String[] PROJECTION = {

                // The contact's row id
                ContactsContract.CommonDataKinds.Phone._ID,

                // A pointer to the contact that is guaranteed to be more permanent than _ID. Given
                // a contact's current _ID value and LOOKUP_KEY, the Contacts Provider can generate
                // a "permanent" contact URI.
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,

                // In platform version 3.0 and later, the Contacts table contains
                // DISPLAY_NAME_PRIMARY, which either contains the contact's displayable name or
                // some other useful identifier such as an email address. This column isn't
                // available in earlier versions of Android, so you must use Contacts.DISPLAY_NAME
                // instead.
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,

                ContactsContract.CommonDataKinds.Phone.NUMBER,

                // The sort order column for the returned Cursor, used by the AlphabetIndexer
                SORT_ORDER,
        };

        // The query column numbers which map to each value in the projection
        final static int ID = 0;
        final static int LOOKUP_KEY = 1;
        final static int DISPLAY_NAME = 2;
        final static int NUMBER = 3;
        final static int SORT_KEY = 4;
    }
}
